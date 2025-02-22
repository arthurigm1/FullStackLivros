package projetolivros.livros.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projetolivros.livros.Dto.AvaliacaoLivroDto;
import projetolivros.livros.Dto.AvaliacaoLivroNota;
import projetolivros.livros.Dto.MediaAvaliacaoDto;
import projetolivros.livros.Model.AvaliacaoLivro;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.AvaliacaoRepository;
import projetolivros.livros.Repository.LivroRepository;
import projetolivros.livros.Security.SecurityService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final LivroRepository livroRepository;
    private  final SecurityService securityService;

    public MediaAvaliacaoDto calcularMediaNotasPorLivro(UUID livroId) {
        Optional<Livro> livroOptional = livroRepository.findById(livroId);
        Livro livro = livroOptional.get();
        List<AvaliacaoLivro> avaliacoes = avaliacaoRepository.findByLivroId(livroId);
        if (avaliacoes.isEmpty()) {
            return new MediaAvaliacaoDto();
        }
        double somaNotas = 0;
        for (AvaliacaoLivro avaliacao : avaliacoes) {
            somaNotas += avaliacao.getNota();
        }

        double mediaNota = somaNotas / avaliacoes.size();


        MediaAvaliacaoDto mediaDTO = new MediaAvaliacaoDto();
        mediaDTO.setMediaNota(mediaNota);
        return mediaDTO;
    }
    public List<AvaliacaoLivroNota> getAvaliacoesPorLivro(UUID livroId) {
        List<AvaliacaoLivro> avaliacoes = avaliacaoRepository.findByLivroId(livroId);

        List<AvaliacaoLivroNota> avaliacoesDTO = new ArrayList<>();
        for (AvaliacaoLivro avaliacao : avaliacoes) {
            AvaliacaoLivroNota dto = new AvaliacaoLivroNota();
            dto.setId(avaliacao.getId());
            dto.setNota(avaliacao.getNota());
            dto.setComentario(avaliacao.getComentario());
            dto.setUsuarioNome(avaliacao.getUsuario().getNome());
            avaliacoesDTO.add(dto);
        }

        return avaliacoesDTO;
    }

    public Optional<AvaliacaoLivro> criarAvaliacao(UUID livroId, AvaliacaoLivroDto avaliacaoDTO) {

        Usuario usuario = securityService.obterUsuarioLogado();
        Optional<Livro> livroOptional = livroRepository.findById(livroId);
        Livro livro = livroOptional.get();
        Optional<AvaliacaoLivro> avaliacaoExistente = avaliacaoRepository.findByUsuarioAndLivro(usuario, livro);
        if (avaliacaoExistente.isPresent()) {

            return Optional.empty();
        }

        AvaliacaoLivro avaliacao = new AvaliacaoLivro();
        avaliacao.setLivro(livro);
        avaliacao.setNota(avaliacaoDTO.getNota());
        avaliacao.setComentario(avaliacaoDTO.getComentario());
        avaliacao.setUsuario(usuario);

        AvaliacaoLivro salvaAvaliacao = avaliacaoRepository.save(avaliacao);

        return Optional.of(salvaAvaliacao);
    }





}
