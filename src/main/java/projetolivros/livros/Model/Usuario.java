package projetolivros.livros.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import projetolivros.livros.Model.Enum.UsuarioRole;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
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
    private String nome;

    @Column
    private String senha;

    @Column(unique = true)
    private String cpf;
    @Column
    private LocalDate dataNascimento;
    @Column
    private String email;

    private String verificationCode;

    private boolean enabled;


    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Livro> livros;



    @OneToMany(mappedBy = "usuario")
    private List<Favorito> favoritos;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "carrinho_id",foreignKey = @ForeignKey(name = "carrinho_fk",value = ConstraintMode.CONSTRAINT))
    private Carrinho carrinho;

    @JsonBackReference
    @OneToMany(mappedBy = "usuario")
    private List<Endereco> enderecos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Pedido> pedidos;

    @Enumerated(EnumType.STRING)
    private UsuarioRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UsuarioRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));

    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }
}