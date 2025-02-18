package projetolivros.livros.Controller;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Dto.PedidoDto;
import projetolivros.livros.Model.Pedido;
import projetolivros.livros.Service.PedidoService;
import projetolivros.livros.Service.PixService;
import projetolivros.livros.Service.RelatorioService;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired
    private PixService pixService;
    @Autowired
    private RelatorioService relatorioService;
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
    @GetMapping("/{id}/relatorio")
    @ResponseStatus(HttpStatus.OK)
    public void gerarRelatorio(@PathVariable Long id, HttpServletResponse response) throws IOException, JRException {

            relatorioService.gerarRelatorio(id, response);

    }
    @GetMapping("")
    public ResponseEntity<?> listarPedidosDoUsuario() {
        try {
            // Obtenha os pedidos do usuário
            List<Pedido> pedidos = pedidoService.listarPedidosPorUsuario();

            // Verifica se a lista de pedidos está vazia
            if (pedidos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Nenhum pedido encontrado para este usuário");
            }

            // Mapeia a lista de pedidos para o DTO
            List<PedidoDto> pedidoDTOs = pedidos.stream()
                    .map(pedido -> new PedidoDto(pedido.getId(), pedido.getStatus(), pedido.getValorTotal()))
                    .collect(Collectors.toList());

            // Retorna a lista de DTOs
            return ResponseEntity.ok(pedidoDTOs);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao listar pedidos");
        }
    }
}

