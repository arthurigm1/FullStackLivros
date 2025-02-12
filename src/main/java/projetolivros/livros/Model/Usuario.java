package projetolivros.livros.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    @NotNull
    @NotBlank
    private String nome;

    @Column
    private String senha;

    @Column
    private String email;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }


    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY
//            , cascade = CascadeType.ALL
    )

    private List<Livro> livros;


    @JsonIgnoreProperties("usuario")  // Ignora a propriedade 'usuario' dentro de Favorito
    @OneToMany(mappedBy = "usuario")
    private List<Favorito> favoritos;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "carrinho_id",foreignKey = @ForeignKey(name = "carrinho_fk",value = ConstraintMode.CONSTRAINT))
    private Carrinho carrinho;

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
