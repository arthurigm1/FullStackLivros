package projetolivros.livros.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projetolivros.livros.Dto.*;
import projetolivros.livros.Model.*;
import projetolivros.livros.Model.Enum.StatusPedido;
import projetolivros.livros.Repository.CarrinhoRepository;
import projetolivros.livros.Repository.EnderecoRepository;
import projetolivros.livros.Repository.PedidoRepository;
import projetolivros.livros.Security.SecurityService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final PixService pixService;
    private final SecurityService securityService;
    private final EnderecoRepository enderecoRepository;


    public List<PedidoAdminDto> listarTodosPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        if (pedidos.isEmpty()) {
            throw new RuntimeException("Nenhum pedido encontrado");
        }

        return pedidos.stream().map(pedido -> {
            Endereco endereco = pedido.getEndereco();
            EnderecoDto enderecoDto = new EnderecoDto(
                    endereco.getCep(),
                    endereco.getLogradouro(),
                    endereco.getComplemento(),
                    endereco.getBairro(),
                    endereco.getLocalidade(),
                    endereco.getUf()
            );

            List<LivroPedidodto> itensDto = pedido.getLivros().stream()
                    .map(item -> {
                        Livro livro = item.getLivro();
                        BigDecimal subtotal = livro.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()));

                        return new LivroPedidodto(
                                livro.getTitulo(),
                                item.getQuantidade(),
                                livro.getPreco(),
                                subtotal
                        );
                    })
                    .collect(Collectors.toList());

            // Calcula o valor total
            BigDecimal valorTotal = itensDto.stream()
                    .map(LivroPedidodto::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            String email = pedido.getUsuario().getEmail();
            LocalDateTime dataCadastro = pedido.getDataCadastro();
            return new PedidoAdminDto(
                    pedido.getId(),
                    pedido.getStatus(),
                    valorTotal,
                    enderecoDto,
                    itensDto,
                    email,
                    dataCadastro
            );
        }).collect(Collectors.toList());
    }

    public List<PedidoDto> listarPedidosPorUsuario() {

        Usuario usuario = securityService.obterUsuarioLogado();

        List<Pedido> pedidos = pedidoRepository.findByUsuarioId(usuario.getId());
        if (pedidos.isEmpty()) {
            throw new RuntimeException("Nenhum pedido encontrado para este usuário");
        }

        return pedidos.stream().map(pedido -> {
            Endereco endereco = pedido.getEndereco();
            EnderecoDto enderecoDto = new EnderecoDto(
                    endereco.getCep(),
                    endereco.getLogradouro(),
                    endereco.getComplemento(),
                    endereco.getBairro(),
                    endereco.getLocalidade(),
                    endereco.getUf()
            );

            List<LivroPedidodto> itensDto = pedido.getLivros().stream()
                    .map(item -> {
                        Livro livro = item.getLivro();
                        BigDecimal subtotal = livro.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()));

                        return new LivroPedidodto(
                                livro.getTitulo(),
                                item.getQuantidade(),
                                livro.getPreco(),
                                subtotal
                        );
                    })
                    .collect(Collectors.toList());

            BigDecimal valorTotal = itensDto.stream()
                    .map(LivroPedidodto::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return new PedidoDto(
                    pedido.getId(),
                    pedido.getStatus(),
                    valorTotal,
                    enderecoDto,
                    itensDto
            );
        }).collect(Collectors.toList());
    }

    public Map<String, String> criarPedido(UUID enderecoid) {
        Usuario usuario = securityService.obterUsuarioLogado();
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new IllegalStateException("Carrinho não encontrado"));

        if (carrinho.getItems().isEmpty()) {
            throw new IllegalArgumentException("O carrinho está vazio.");
        }
        if (carrinho.getItems().stream().anyMatch(item -> item.getQuantidade() <= 0 || item.getPreco() == null)) {
            throw new IllegalArgumentException("Carrinho contém itens inválidos.");
        }

        Endereco endereco = enderecoRepository.findById(enderecoid)
                .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado"));

        BigDecimal valorTotal = carrinho.getItems().stream()
                .map(item -> item.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PixChargeRequest pixChargeRequest = new PixChargeRequest("a3438546-659f-47f0-b07e-4e168ede19a2", valorTotal.toString());
        var response = pixService.pixCreateCharge(pixChargeRequest);

        if (response != null && response.getJSONObject("loc").has("id") && response.has("pixCopiaECola")) {
            int idFromJson = response.getJSONObject("loc").getInt("id");
            String pixCopiaECola = response.getString("pixCopiaECola");
            String qrCodeUrl = pixService.pixGenerateQRCode(String.valueOf(idFromJson));

            Pedido pedido = new Pedido();
            pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
            pedido.setUsuario(usuario);
            pedido.setEndereco(endereco);
            pedido.setValorTotal(valorTotal);


            Pedido pedidoSalvo = pedidoRepository.save(pedido);


            List<LivroPedido> itensDoPedido = carrinho.getItems().stream()
                    .map(item -> {
                        LivroPedido livroPedido = new LivroPedido();
                        PedidoPk pedidoPk = new PedidoPk();
                        pedidoPk.setPedidoId(pedidoSalvo.getId());
                        pedidoPk.setLivroId(item.getLivro().getId());

                        livroPedido.setId(pedidoPk);
                        livroPedido.setLivro(item.getLivro());
                        livroPedido.setQuantidade(item.getQuantidade());
                        livroPedido.setPreco(item.getPreco());
                        livroPedido.setPedido(pedidoSalvo);

                        return livroPedido;
                    })
                    .collect(Collectors.toList());


            pedidoSalvo.setLivros(itensDoPedido);
            pedidoRepository.save(pedidoSalvo);
            carrinho.getItems().clear();
            carrinhoRepository.save(carrinho);

            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("qrCodeUrl", qrCodeUrl);
            responseMap.put("pixCopiaECola", pixCopiaECola);
            return responseMap;

        } else {
            throw new IllegalStateException("Erro ao gerar QR Code: resposta do Pix inválida");
        }
    }

    public void atualizarStatusParaPago(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        pedido.setStatus(StatusPedido.PAGO);
        pedidoRepository.save(pedido);
    }

    public ResumoPedidosDto listarTodosPedidosComResumo() {
        List<Pedido> pedidos = pedidoRepository.findAll();

        BigDecimal valorTotal = pedidos.stream()
                .map(Pedido::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorPago = pedidos.stream()
                .filter(pedido -> StatusPedido.PAGO.equals(pedido.getStatus()))
                .map(Pedido::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorAguardandoPagamento = pedidos.stream()
                .filter(pedido -> StatusPedido.AGUARDANDO_PAGAMENTO.equals(pedido.getStatus()))
                .map(Pedido::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long countPago = pedidos.stream()
                .filter(pedido -> StatusPedido.PAGO.equals(pedido.getStatus()))
                .count();

        long countAguardandoPagamento = pedidos.stream()
                .filter(pedido -> StatusPedido.AGUARDANDO_PAGAMENTO.equals(pedido.getStatus()))
                .count();


        ResumoPedidosDto resumo = new ResumoPedidosDto();
        resumo.setValorTotal(valorTotal);
        resumo.setValorPago(valorPago);
        resumo.setValorAguardandoPagamento(valorAguardandoPagamento);
        resumo.setCountPago(countPago);
        resumo.setCountAguardandoPagamento(countAguardandoPagamento);

        return resumo;
    }


}
