package projetolivros.livros.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "favoritos")
@AllArgsConstructor
@NoArgsConstructor
public class Favorito {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JsonManagedReference
    private Usuario usuario;

    @ManyToOne
    @JsonManagedReference
    private Livro livro;
}
