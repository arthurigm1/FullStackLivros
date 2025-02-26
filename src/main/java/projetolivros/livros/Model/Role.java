package projetolivros.livros.Model;

import jakarta.persistence.*;
import lombok.*;
import projetolivros.livros.Model.Enum.RoleName;

import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RoleName nome;
}
