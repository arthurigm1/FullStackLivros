package projetolivros.livros.Controller;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Dto.PedidoDto;
import projetolivros.livros.Model.Endereco;
import projetolivros.livros.Model.Pedido;
import projetolivros.livros.Service.PedidoService;
import projetolivros.livros.Service.PixService;
import projetolivros.livros.Service.RelatorioService;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
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

    @PostMapping("/{enderecoId}")
    public ResponseEntity<String> criarPedido(@PathVariable UUID enderecoId) {
        try {
            String qrCodeUrl = pedidoService.criarPedido(enderecoId);
            return ResponseEntity.ok(qrCodeUrl);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gerar QR Code");
        }
    }



    @GetMapping("/{id}/relatorio")
    @ResponseStatus(HttpStatus.OK)
    public void gerarRelatorio(@PathVariable Long id, HttpServletResponse response) throws IOException, JRException {

            relatorioService.gerarRelatorio(id, response);

    }
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



