package projetolivros.livros.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Dto.PedidoDto;
import projetolivros.livros.Service.PedidoService;
import projetolivros.livros.Service.RelatorioService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/pedido")
@RequiredArgsConstructor
@Tag(name = "Pedido", description = "Gerenciamento de pedidos")
public class PedidoController {

    private final RelatorioService relatorioService;
    private final PedidoService pedidoService;

    @Operation(summary = "Gerar relatório do pedido", description = "Gera um relatório em PDF para um pedido específico.")
    @GetMapping("/{id}/relatorio")
    public ResponseEntity<byte[]> gerarRelatorio(@PathVariable Long id) {
        byte[] pdfBytes = relatorioService.gerarRelatorio(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=relatorio_pedido.pdf")
                .body(pdfBytes);
    }

    @Operation(summary = "Criar um novo pedido", description = "Cria um pedido com base no endereço informado e retorna um QR Code para pagamento.")
    @PostMapping("/{enderecoId}")
    public ResponseEntity<Map<String, String>> criarPedido(@PathVariable UUID enderecoId) {
        try {
            Map<String, String> response = pedidoService.criarPedido(enderecoId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao gerar QR Code"));
        }
    }

    @Operation(summary = "Listar pedidos do usuário", description = "Retorna a lista de pedidos associados ao usuário logado.")
    @GetMapping("")
    public ResponseEntity<?> listarPedidosDoUsuario() {
        try {
            List<PedidoDto> pedidoDTOs = pedidoService.listarPedidosPorUsuario();
            return ResponseEntity.ok(pedidoDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao listar pedidos");
        }
    }
}
