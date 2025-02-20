package projetolivros.livros.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projetolivros.livros.Model.AvaliacaoLivro;
import projetolivros.livros.Repository.AvaliacaoRepository;
import projetolivros.livros.Repository.LivroRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final LivroRepository livroRepository;

    public List<AvaliacaoLivro> getAvaliacoesPorLivro(UUID livroId) {
        return avaliacaoRepository.findByLivroId(livroId);
    }

    public Optional<Optional<AvaliacaoLivro>> criarAvaliacao(UUID livroId, AvaliacaoLivro avaliacao) {
        return livroRepository.findById(livroId).map(livro -> {
            avaliacao.setLivro(livro);
            return Optional.of(avaliacaoRepository.save(avaliacao));
        });
    }

    public double getMediaAvaliacoes(UUID livroId) {
        List<AvaliacaoLivro> avaliacoes = avaliacaoRepository.findByLivroId(livroId);

        if (avaliacoes.isEmpty()) {
            return 0.0; // Retorna 0 caso não haja avaliações
        }

        double soma = avaliacoes.stream().mapToDouble(AvaliacaoLivro::getNota).sum();
        return soma / avaliacoes.size();
    }
}
