package projetolivros.livros.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projetolivros.livros.Model.Endereco;

import java.util.UUID;

public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {
}
