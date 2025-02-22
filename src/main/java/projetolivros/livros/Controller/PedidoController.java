package projetolivros.livros.Controller;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Dto.PedidoDto;
import projetolivros.livros.Model.Endereco;
import projetolivros.livros.Model.Pedido;
import projetolivros.livros.Repository.PedidoRepository;
import projetolivros.livros.Service.PedidoService;
import projetolivros.livros.Service.PixService;
import projetolivros.livros.Service.RelatorioService;


import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.*;
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

    @Autowired
    private PedidoRepository pedidoRepository;
    private final String jasperReportPath = "relatorios/pedido_report.jrxml";
    @GetMapping("/{id}/relatorio")
    public ResponseEntity<byte[]> gerarRelatorio(@PathVariable Long id) {
        byte[] pdfBytes = relatorioService.gerarRelatorio(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=relatorio_pedido.pdf")
                .body(pdfBytes);
    }

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

   /* @GetMapping("/{id}/relatorio")
    @ResponseStatus(HttpStatus.OK)
    public void gerarRelatorio(@PathVariable Long id, HttpServletResponse response) throws IOException, JRException {

            relatorioService.gerarRelatorio(id, response);

    }*/


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



