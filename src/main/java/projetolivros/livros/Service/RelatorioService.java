package projetolivros.livros.Service;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.TextAlignment;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import projetolivros.livros.Model.*;
import projetolivros.livros.Repository.LivroPedidoRepository;
import projetolivros.livros.Repository.PedidoRepository;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFont;
import java.io.ByteArrayOutputStream;
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

    public byte[] gerarRelatorio2(Long id) {
        try {
            Pedido pedido = pedidoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

            Usuario usuario = pedido.getUsuario();
            Endereco endereco = pedido.getEndereco();


            List<Livro> livros = livroPedidoRepository.findLivrosByPedidoId(pedido.getId());

            // Criando o byte array output stream para o PDF
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            Image img = new Image(ImageDataFactory.create("C:\\Users\\arthu\\OneDrive\\Documentos\\Projetos_java\\livros\\src\\main\\resources\\templates\\pagiinova.png")); // Caminho da imagem
            img.setWidth(200);
            img.setHeight(80);
            document.add(img);

            Paragraph title = new Paragraph("Relatório do Pedido")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.DARK_GRAY);
            document.add(title);

            // Adicionando dados do usuário e do pedido
            document.add(new Paragraph("Usuário: " + usuario.getNome())
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Endereço: " + endereco.getLogradouro() + ", " + endereco.getBairro() + " - " + endereco.getLocalidade())
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("ID do Pedido: " + pedido.getId())
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Valor Total: R$ " + pedido.getValorTotal())
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("Status: " + pedido.getStatus().toString())
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontColor(ColorConstants.BLACK));

            // Adicionando data de cadastro
            String dataFormatada = (pedido.getDataCadastro() != null)
                    ? pedido.getDataCadastro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                    : "Data não disponível";
            document.add(new Paragraph("Data de Cadastro: " + dataFormatada)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontColor(ColorConstants.BLACK));

            // Adicionando uma linha horizontal
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("------------------------------------------------------------"));

            // Adicionando os livros em uma tabela
            Table table = new Table(3);
            table.addHeaderCell(new Cell().add(new Paragraph("Livro")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Autor")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Quantidade")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());

            // Preenchendo os dados da tabela com os livros
            for (Livro livro : livros) {
                Optional<LivroPedido> livroPedido = livroPedidoRepository.findByPedidoAndLivro(pedido, livro);
                int quantidade = livroPedido.get().getQuantidade();


                table.addCell(livro.getTitulo()).setTextAlignment(TextAlignment.LEFT);
                table.addCell(String.valueOf(livro.getPreco())).setTextAlignment(TextAlignment.CENTER);
                table.addCell(String.valueOf(quantidade)).setTextAlignment(TextAlignment.CENTER);
            }

            document.add(table);

            // Adicionando uma linha horizontal
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("------------------------------------------------------------"));

            // Fechar o documento
            document.close();

            // Retornando o PDF como byte array
            return byteArrayOutputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar o relatório: " + e.getMessage());
        }
    }
    public Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}