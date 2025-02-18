package projetolivros.livros.Service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import projetolivros.livros.Model.Pedido;
import projetolivros.livros.Repository.PedidoRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    @Value("${jasper.report.path}")
    private String jasperReportPath;

    private final PedidoRepository pedidoRepository;


    public void gerarRelatorio(Long pedidoId, HttpServletResponse response) throws JRException, IOException {
        // Buscando o pedido no banco de dados
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // Carregar e compilar o arquivo .jrxml
        ClassPathResource reportFile = new ClassPathResource(jasperReportPath);
        InputStream reportStream = reportFile.getInputStream();

        // Compilar o arquivo .jrxml para .jasper
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Preenchendo os parâmetros
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("pedido", pedido);

        // Criando a fonte de dados para o relatório
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(pedido.getLivros());

        // Preenchendo o relatório com dados
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Definindo o tipo de resposta HTTP para um arquivo PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=relatorio_pedido.pdf");

        // Gerando o PDF e escrevendo na resposta HTTP
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }
}
