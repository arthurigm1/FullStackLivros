package projetolivros.livros.Model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString(exclude = {"livros"}) //LOMBOK
@EntityListeners(AuditingEntityListener.class) // Habilita auditoria na entidade
public class Autor {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome",length = 100, nullable = false)
    private String nome;
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;
    @Column(name = "nacionalidade",length = 50, nullable = false)
    private String nacionalidade;

    @OneToMany(mappedBy = "autor",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Livro> livros;

    @CreatedDate // CRIA DATA AUTOMATICO NO CADASTRO
    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @LastModifiedDate //ATUALIZA A DATA AUTOMATICO QUANDO HOUVER ALTERACAO!
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;


}

