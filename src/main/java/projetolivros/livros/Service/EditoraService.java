package projetolivros.livros.Service;

import projetolivros.livros.Dto.EditoraDto;
import projetolivros.livros.Model.Editora;
import projetolivros.livros.Repository.EditoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service

public class EditoraService {

    @Autowired
    private   EditoraRepository editoraRepository;


    // Método para buscar todas as editoras
    public List<Editora> listarTodas() {
        return editoraRepository.findAll();
    }

    // Método para buscar uma editora pelo ID
    public Optional<Editora> buscarPorId(UUID id) {
        return editoraRepository.findById(id);
    }

    // Método para salvar uma nova editora
    public Editora salvar(EditoraDto editora) {
        Editora editora1 = new Editora();
        // Mapeie os campos do DTO para a entidade
        editora1.setNome(editora.getNome());
        editora1.setImg(editora.getImg());
        return editoraRepository.save(editora1);
    }

    // Método para atualizar uma editora
    public Editora atualizar(UUID id, Editora editora) {
        editora.setId(id);  // Garantir que o ID é setado para a atualização
        return editoraRepository.save(editora);
    }

    // Método para excluir uma editora
    public void excluir(UUID id) {
        editoraRepository.deleteById(id);
    }
}
