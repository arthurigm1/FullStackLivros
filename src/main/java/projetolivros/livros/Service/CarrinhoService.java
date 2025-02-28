package projetolivros.livros.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projetolivros.livros.Controller.Mapper.CarrinhoMapper;
import projetolivros.livros.Dto.LivroCarrinhoDto;
import projetolivros.livros.Dto.LivroCarrinhoRequestdto;
import projetolivros.livros.Exceptions.CarrinhoNaoEncontradoException;
import projetolivros.livros.Model.*;
import projetolivros.livros.Repository.CarrinhoRepository;
import projetolivros.livros.Repository.LivroCarrinhoRepository;
import projetolivros.livros.Repository.LivroRepository;
import projetolivros.livros.Security.SecurityService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarrinhoService {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private LivroCarrinhoRepository livroCarrinhoRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private SecurityService securityService;
    @Autowired
    private CarrinhoMapper livrocarrinhoMapper;






    public LivroCarrinhoRequestdto criarOuAdicionarLivroAoCarrinho(UUID livroId, int quantidade) throws Exception {
        Usuario usuario = securityService.obterUsuarioLogado();
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    Carrinho novoCarrinho = new Carrinho();
                    novoCarrinho.setUsuarioId(usuario.getId());
                    return carrinhoRepository.save(novoCarrinho);
                });

        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        CarrinhoPk carrinhoPk = new CarrinhoPk(livro.getId(), carrinho.getId());
        LivroCarrinho livroCarrinhoExistente = livroCarrinhoRepository.findById(carrinhoPk).orElse(null);

        if (livroCarrinhoExistente != null) {
            livroCarrinhoExistente.setQuantidade(livroCarrinhoExistente.getQuantidade() + quantidade);
            BigDecimal novoPrecoTotal = livroCarrinhoExistente.getPrecoTotal().add(livro.getPreco().multiply(BigDecimal.valueOf(quantidade)));
            livroCarrinhoExistente.setPrecoTotal(novoPrecoTotal);
            livroCarrinhoRepository.save(livroCarrinhoExistente);
        }else {
            LivroCarrinho novoLivroCarrinho = new LivroCarrinho();
            novoLivroCarrinho.setId(carrinhoPk);
            novoLivroCarrinho.setCarrinho(carrinho);
            novoLivroCarrinho.setLivro(livro);
            novoLivroCarrinho.setQuantidade(quantidade);
            novoLivroCarrinho.setPreco(livro.getPreco());
            novoLivroCarrinho.setPrecoTotal(livro.getPreco().multiply(BigDecimal.valueOf(quantidade)));
            livroCarrinhoRepository.save(novoLivroCarrinho);
        }


        return new LivroCarrinhoRequestdto(livro.getId(), livro.getTitulo(), livro.getPreco().multiply(BigDecimal.valueOf(quantidade)));
    }






    public List<LivroCarrinhoDto> listarItensDoCarrinho() {
        Usuario usuario = securityService.obterUsuarioLogado();

        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));

        return livroCarrinhoRepository.findByCarrinhoId(carrinho.getId())
                .stream()
                .map(livroCarrinho -> new LivroCarrinhoDto(
                        livroCarrinho.getLivro().getId(),
                        livroCarrinho.getLivro().getTitulo(),
                        livroCarrinho.getPreco(),
                        livroCarrinho.getQuantidade()
                ))
                .collect(Collectors.toList());
    }


    public void removerUmaQuantidade(UUID livroId) {
        Usuario usuario = securityService.obterUsuarioLogado();
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));

        Optional<LivroCarrinho> itemOptional = livroCarrinhoRepository.findByCarrinhoIdAndLivroId(carrinho.getId(), livroId);

        if (itemOptional.isPresent()) {
            LivroCarrinho item = itemOptional.get();
            if (item.getQuantidade() > 1) {
                item.setQuantidade(item.getQuantidade() - 1);
                BigDecimal precoTotal = item.getPrecoTotal().subtract(item.getPreco());
                item.setPrecoTotal(precoTotal);
                livroCarrinhoRepository.save(item);
            }else{
                livroCarrinhoRepository.delete(item);}
        }
    }

}
