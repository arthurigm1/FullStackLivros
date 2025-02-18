package projetolivros.livros.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projetolivros.livros.Model.Endereco;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.EnderecoRepository;
import projetolivros.livros.Security.SecurityService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EnderecoService {


    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private SecurityService securityService;

    public Endereco atualizarEndereco(UUID id, Endereco enderecoAtualizado) {
        Optional<Endereco> enderecoExistenteOptional = enderecoRepository.findById(id);

        if (!enderecoExistenteOptional.isPresent()) {
            return null; // Retorna null se o endereço não for encontrado
        }

        Endereco enderecoExistente = enderecoExistenteOptional.get();

        // Atualizando as informações do endereço
        enderecoExistente.setCep(enderecoAtualizado.getCep());
        enderecoExistente.setLogradouro(enderecoAtualizado.getLogradouro());
        enderecoExistente.setComplemento(enderecoAtualizado.getComplemento());
        enderecoExistente.setBairro(enderecoAtualizado.getBairro());
        enderecoExistente.setLocalidade(enderecoAtualizado.getLocalidade());
        enderecoExistente.setUf(enderecoAtualizado.getUf());

        // Salvando o endereço atualizado
        return enderecoRepository.save(enderecoExistente);
    }

    public Endereco salvarEndereco(Endereco endereco) {
        Usuario usuario = securityService.obterUsuarioLogado();
        endereco.setUsuario(usuario);
        return enderecoRepository.save(endereco);
    }
    public void removerEndereco(UUID enderecoId) {
        Optional<Endereco> endereco = enderecoRepository.findById(enderecoId);
        if (endereco.isPresent()) {
            enderecoRepository.delete(endereco.get());
        } else {
            // Tratar caso o endereço não seja encontrado, caso necessário
            throw new RuntimeException("Endereço não encontrado");
        }
    }

    public List<Endereco> buscarEnderecosPorUsuario() {
        Usuario usuario = securityService.obterUsuarioLogado();

        return enderecoRepository.findByUsuarioId(usuario.getId()); // Supondo que você tenha esse método no repositório
    }
    public Endereco buscarEnderecoPorId(UUID id) {
        return enderecoRepository.findById(id).orElse(null);
    }
}