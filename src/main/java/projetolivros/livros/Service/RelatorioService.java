package projetolivros.livros.Service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import projetolivros.livros.Model.Endereco;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Model.Pedido;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.LivroPedidoRepository;
import projetolivros.livros.Repository.PedidoRepository;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final String jasperReportPath = "relatorios/pedido_report.jrxml";
    private final String jasperReportPedido = "relatorios/livros_subreport.jasper";

    private final PedidoRepository pedidoRepository;

private final LivroPedidoRepository livroPedidoRepository;


    public byte[] gerarRelatorio(Long id) {
        try {
            // Buscando o pedido no banco de dados
            Pedido pedido = pedidoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

            // Acessando diretamente o usuário e o endereço relacionados ao pedido
            Usuario usuario = pedido.getUsuario();  // Obter o usuário diretamente do pedido
            Endereco endereco = pedido.getEndereco();  // Obter o endereço diretamente do pedido

            // Buscando os livros relacionados ao pedido através da tabela LivroPedido
            List<Livro> livros = livroPedidoRepository.findLivrosByPedidoId(pedido.getId());

            // Carregar e compilar o arquivo .jrxml
            ClassPathResource reportFile = new ClassPathResource(jasperReportPath);
            InputStream reportStream = reportFile.getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Formatar a data no código Java
            String dataFormatada = (pedido.getDataCadastro() != null)
                    ? pedido.getDataCadastro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                    : "Data não disponível";

            // Parâmetros do relatório
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("nome", usuario.getNome()); // Passando o nome do usuário
            parameters.put("endereco", endereco.getLogradouro() + ", " + endereco.getBairro() + " - " + endereco.getLocalidade()); // Passando o endereço do pedido
            parameters.put("id", pedido.getId());
            parameters.put("valorTotal", pedido.getValorTotal());
            parameters.put("status", pedido.getStatus().toString());

            parameters.put("dataCadastro", dataFormatada); // Passando a data já formatada
            //   parameters.put("livros", livros); // Passando os livros como parâmetro

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList(pedido));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,dataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar o relatório: " + e.getMessage());
        }
    }
    public Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}