package projetolivros.livros.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projetolivros.livros.Model.Endereco;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.EnderecoRepository;
import projetolivros.livros.Security.SecurityService;

import java.util.UUID;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ViaCepService viaCepService;

    @Autowired
    private SecurityService securityService;
    public Endereco salvarEndereco(Endereco endereco) {
        Usuario usuario = securityService.obterUsuarioLogado();
        endereco.setUsuario(usuario);
        return enderecoRepository.save(endereco);
    }

    public Endereco buscarEnderecoPorId(UUID id) {
        return enderecoRepository.findById(id).orElse(null);
    }
}