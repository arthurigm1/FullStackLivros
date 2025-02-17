package projetolivros.livros.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projetolivros.livros.Dto.PixChargeRequest;
import projetolivros.livros.Model.*;
import projetolivros.livros.Repository.CarrinhoRepository;
import projetolivros.livros.Repository.PedidoRepository;
import projetolivros.livros.Security.SecurityService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final PixService pixService;
    private final SecurityService securityService;

    public String criarPedido() {
        // Obter o usuário logado
        Usuario usuario = securityService.obterUsuarioLogado();

        // Buscar o carrinho do usuário
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new IllegalStateException("Carrinho não encontrado"));

        // Verificar se o carrinho está vazio
        if (carrinho.getItems().isEmpty()) {
            throw new IllegalArgumentException("O carrinho está vazio.");
        }

        // Validar se os itens do carrinho são válidos (quantidade > 0 e preço != null)
        if (carrinho.getItems().stream().anyMatch(item -> item.getQuantidade() <= 0 || item.getPreco() == null)) {
            throw new IllegalArgumentException("Carrinho contém itens inválidos.");
        }

        // Criar um novo pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);

        // Calcular o valor total do pedido
        BigDecimal valorTotal = carrinho.getItems().stream()
                .map(item -> item.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setValorTotal(valorTotal);  // Atribuindo o valor total ao pedido

        // Salvar o pedido primeiro para garantir que o ID seja gerado
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // Criar a lista de itens do pedido a partir de LivroCarrinho
        List<LivroPedido> itensDoPedido = carrinho.getItems().stream()
                .map(item -> {
                    LivroPedido livroPedido = new LivroPedido();

                    // Criar a chave composta para LivroPedido
                    PedidoPk pedidoPk = new PedidoPk();
                    pedidoPk.setPedidoId(pedidoSalvo.getId());  // Usar o ID do pedido salvo
                    pedidoPk.setLivroId(item.getLivro().getId());

                    livroPedido.setId(pedidoPk);  // Atribuindo a chave composta
                    livroPedido.setLivro(item.getLivro());  // Associando o livro ao pedido
                    livroPedido.setQuantidade(item.getQuantidade());
                    livroPedido.setPreco(item.getPreco());
                    livroPedido.setPedido(pedidoSalvo);  // Associando o pedido ao item
                    System.out.println("LivroPedido antes de salvar: " + livroPedido);

                    return livroPedido;
                })
                .collect(Collectors.toList());

        // Atribuir a lista de LivroPedido ao Pedido
        pedidoSalvo.setLivros(itensDoPedido);

        // Salvar o pedido com os itens e valor total
        pedidoRepository.save(pedidoSalvo);

        // Criar a requisição para o pagamento via Pix
        PixChargeRequest pixChargeRequest = new PixChargeRequest("a3438546-659f-47f0-b07e-4e168ede19a2",valorTotal.toString());
        var response = pixService.pixCreateCharge(pixChargeRequest);

        if (response != null && response.getJSONObject("loc").has("id")) {
            // Gerar o QR Code do pagamento Pix
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
}
