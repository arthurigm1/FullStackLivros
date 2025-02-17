package projetolivros.livros.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projetolivros.livros.Dto.PixChargeRequest;
import projetolivros.livros.Model.Pedido;
import projetolivros.livros.Service.PedidoService;
import projetolivros.livros.Service.PixService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired
    private PixService pixService;

    @Autowired
    private PedidoService pedidoService;

    @PostMapping("")
    public ResponseEntity<String> criarPedido() {
        try {
            String qrCodeUrl = pedidoService.criarPedido();
            return ResponseEntity.ok(qrCodeUrl);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gerar QR Code");
        }
    }


    private BigDecimal calcularValorTotal(Pedido pedido) {
        return pedido.getLivros().stream()
                .map(item -> item.getPreco().multiply(new BigDecimal(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
