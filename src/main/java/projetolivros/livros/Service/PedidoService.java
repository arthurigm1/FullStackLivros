package projetolivros.livros.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projetolivros.livros.Dto.EnderecoDto;
import projetolivros.livros.Dto.LivroPedidodto;
import projetolivros.livros.Dto.PedidoDto;
import projetolivros.livros.Dto.PixChargeRequest;
import projetolivros.livros.Model.*;
import projetolivros.livros.Model.Enum.StatusPedido;
import projetolivros.livros.Repository.CarrinhoRepository;
import projetolivros.livros.Repository.EnderecoRepository;
import projetolivros.livros.Repository.PedidoRepository;
import projetolivros.livros.Security.SecurityService;

import java.math.BigDecimal;
import java.util.List;
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
        }).collect(Collectors.toList()); // Corrigido para retornar a lista de PedidoDto
    }

    public String criarPedido(UUID enderecoid) {
        // Obter o usuário logado
        Usuario usuario = securityService.obterUsuarioLogado();

        // Buscar o carrinho do usuário
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new IllegalStateException("Carrinho não encontrado"));

        // Verificar se o carrinho está vazio
        if (carrinho.getItems().isEmpty()) {
            throw new IllegalArgumentException("O carrinho está vazio.");
        }

        // Validar itens do carrinho
        if (carrinho.getItems().stream().anyMatch(item -> item.getQuantidade() <= 0 || item.getPreco() == null)) {
            throw new IllegalArgumentException("Carrinho contém itens inválidos.");
        }

        // Criar o pedido e associar o endereço recebido
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        pedido.setUsuario(usuario);
        Endereco endereco = enderecoRepository.findById(enderecoid)
                .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado"));

        pedido.setEndereco(endereco);


        // Calcular o valor total do pedido
        BigDecimal valorTotal = carrinho.getItems().stream()
                .map(item -> item.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setValorTotal(valorTotal);

        // Salvar o pedido primeiro para garantir que o ID seja gerado
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // Criar os itens do pedido
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

        // Atribuir os itens ao pedido e salvar
        pedidoSalvo.setLivros(itensDoPedido);
        pedidoRepository.save(pedidoSalvo);

        // Criar a requisição de pagamento via Pix
        PixChargeRequest pixChargeRequest = new PixChargeRequest("a3438546-659f-47f0-b07e-4e168ede19a2", valorTotal.toString());
        var response = pixService.pixCreateCharge(pixChargeRequest);

        if (response != null && response.getJSONObject("loc").has("id")) {
            int idFromJson = response.getJSONObject("loc").getInt("id");
            String qrCodeUrl = pixService.pixGenerateQRCode(String.valueOf(idFromJson));

            // Limpar o carrinho após a finalização do pedido
            carrinho.getItems().clear();
            carrinhoRepository.save(carrinho);

            return qrCodeUrl;
        } else {
            throw new IllegalStateException("Erro ao gerar QR Code: resposta do Pix inválida");
        }
    }

    public void atualizarStatusParaPago(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        pedido.setStatus(StatusPedido.PAGO);
        pedidoRepository.save(pedido);
    }
}
