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


    public List<Editora> listarTodas() {
        return editoraRepository.findAll();
    }

    public Optional<Editora> buscarPorId(UUID id) {
        return editoraRepository.findById(id);
    }

    public Editora salvar(EditoraDto editora) {
        Editora editora1 = new Editora();
        editora1.setNome(editora.getNome());
        editora1.setImg(editora.getImg());
        return editoraRepository.save(editora1);
    }

    public Editora atualizar(UUID id, Editora editora) {
        editora.setId(id);
        return editoraRepository.save(editora);
    }

    public void excluir(UUID id) {
        editoraRepository.deleteById(id);
    }
}
