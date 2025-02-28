package projetolivros.livros.Service;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
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

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Adicionando imagem do logo
            InputStream imageStream = getClass().getResourceAsStream("/templates/pagiinova.png");
            if (imageStream != null) {
                Image img = new Image(ImageDataFactory.create(imageStream.readAllBytes()));
                img.setWidth(200);
                img.setHeight(80);
                document.add(img);
            }

            // Título
            Paragraph title = new Paragraph("Relatório do Pedido")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.DARK_GRAY);
            document.add(title);

            // Dados do usuário e do pedido
            document.add(new Paragraph("Usuário: " + usuario.getNome()).setFontSize(12));
            document.add(new Paragraph("Endereço: " + endereco.getLogradouro() + ", " + endereco.getBairro() + " - " + endereco.getLocalidade()).setFontSize(12));
            document.add(new Paragraph("ID do Pedido: " + pedido.getId()).setFontSize(12));
            document.add(new Paragraph("Valor Total: R$ " + pedido.getValorTotal()).setFontSize(12));
            document.add(new Paragraph("Status: " + pedido.getStatus().toString()).setFontSize(12));

            // Data de Cadastro
            String dataFormatada = (pedido.getDataCadastro() != null)
                    ? pedido.getDataCadastro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                    : "Data não disponível";
            document.add(new Paragraph("Data de Cadastro: " + dataFormatada).setFontSize(12));

            document.add(new Paragraph("\n------------------------------------------------------------\n"));

            // Tabela de livros
            Table table = new Table(3);
            table.addHeaderCell(new Cell().add(new Paragraph("Livro")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Autor")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Quantidade")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());

            for (Livro livro : livros) {
                Optional<LivroPedido> livroPedidoOpt = livroPedidoRepository.findByPedidoAndLivro(pedido, livro);
                int quantidade = livroPedidoOpt.map(LivroPedido::getQuantidade).orElse(0);

                table.addCell(new Cell().add(new Paragraph(livro.getTitulo())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(livro.getPreco()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(quantidade))));
            }

            document.add(table);
            document.add(new Paragraph("\n------------------------------------------------------------"));

            // Fechar o documento
            document.close();

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar o relatório: " + e.getMessage());
        }
    }
    public byte[] gerarRelatorioTodosPedidos() {
        try {
            // Recupera todos os pedidos
            List<Pedido> pedidos = pedidoRepository.findAll();

            // Cria o documento PDF
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.A4.rotate()); // Usar página no modo paisagem

            // Adicionando imagem do logo
            InputStream imageStream = getClass().getResourceAsStream("/templates/pagiinova.png");
            if (imageStream != null) {
                Image img = new Image(ImageDataFactory.create(imageStream.readAllBytes()));
                img.setWidth(200);
                img.setHeight(80);
                img.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(img);
            }

            // Título
            Paragraph title = new Paragraph("Relatório de Todos os Pedidos")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginBottom(20);
            document.add(title);

            // Tabela de pedidos
            Table table = new Table(UnitValue.createPercentArray(new float[]{10, 15, 10, 10, 15, 30})); // Larguras das colunas em porcentagem
            table.setWidth(UnitValue.createPercentValue(100)); // Tabela ocupa 100% da largura da página

            // Cabeçalho da tabela
            table.addHeaderCell(new Cell().add(new Paragraph("ID Pedido")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Usuário")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Status")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Valor Total")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Data Cadastro")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Itens")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());

            // Iterando sobre todos os pedidos
            for (Pedido pedido : pedidos) {
                Usuario usuario = pedido.getUsuario();
                String usuarioemail = usuario != null ? usuario.getEmail() : "Não informado";
                String status = pedido.getStatus().toString();
                String valorTotal = "R$ " + pedido.getValorTotal().toString();
                String dataCadastro = pedido.getDataCadastro() != null
                        ? pedido.getDataCadastro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                        : "Data não disponível";

                // Adicionando linha na tabela
                table.addCell(new Cell().add(new Paragraph(String.valueOf(pedido.getId()))));
                table.addCell(new Cell().add(new Paragraph(usuarioemail)));
                table.addCell(new Cell().add(new Paragraph(status)));
                table.addCell(new Cell().add(new Paragraph(valorTotal)));
                table.addCell(new Cell().add(new Paragraph(dataCadastro)));

                // Adicionando os itens do pedido
                StringBuilder itens = new StringBuilder();
                List<Livro> livros = livroPedidoRepository.findLivrosByPedidoId(pedido.getId());
                for (Livro livro : livros) {
                    Optional<LivroPedido> livroPedidoOpt = livroPedidoRepository.findByPedidoAndLivro(pedido, livro);
                    int quantidade = livroPedidoOpt.map(LivroPedido::getQuantidade).orElse(0);
                    itens.append(livro.getTitulo()).append(" (").append(quantidade).append("), ");
                }

                // Remover a vírgula final
                if (itens.length() > 0) {
                    itens.setLength(itens.length() - 2);
                }

                // Célula de itens com quebra de linha automática
                Cell cellItens = new Cell().add(new Paragraph(itens.toString()));
                cellItens.setPadding(5); // Espaçamento interno
                table.addCell(cellItens);
            }

            // Adicionando a tabela no documento
            document.add(table);

            // Fechar o documento
            document.close();

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar o relatório de todos os pedidos: " + e.getMessage());
        }
    }
    public Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}