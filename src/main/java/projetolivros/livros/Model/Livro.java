package projetolivros.livros.Model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class) // Habilita auditoria na entidade
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 150,name = "titulo")
    private String titulo;

    @Column(name = "isbn", length = 20, nullable = false,unique = true)
    private String isbn;
    @Column(name = "data_publicacao")
    private LocalDate dataPublicacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20,name = "genero")
    private GeneroLivro genero;

    @Column(name = "preco",precision = 18,scale = 2)
    private BigDecimal preco;

    @CreatedDate // CRIA DATA AUTOMATICO NO CADASTRO
    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @LastModifiedDate //ATUALIZA A DATA AUTOMATICO QUANDO HOUVER ALTERACAO!
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_autor")
    private Autor autor;

    @ManyToOne()
    @JoinColumn(name = "id_usuario")
    private Usuario idusuario;
}
