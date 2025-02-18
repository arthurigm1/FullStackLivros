package projetolivros.livros.Controller;

import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Controller.Mapper.CarrinhoMapper;
import projetolivros.livros.Controller.Mapper.LivroMapper;
import projetolivros.livros.Dto.LivroCarrinhoDto;
import projetolivros.livros.Dto.LivroCarrinhoRequestdto;
import projetolivros.livros.Model.Carrinho;
import projetolivros.livros.Model.LivroCarrinho;
import projetolivros.livros.Service.CarrinhoService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("carrinhos")
@RequiredArgsConstructor
public class CarrinhoController {
    private final CarrinhoMapper mapper;
    @Autowired
    private CarrinhoService carrinhoService;

    @PostMapping
    public ResponseEntity<LivroCarrinhoRequestdto> adicionarAoCarrinho(@RequestBody @Valid LivroCarrinhoRequestdto livroCarrinhoRequest) throws Exception {
        // Chama o serviço para adicionar ou criar o carrinho com o livro
        LivroCarrinhoRequestdto livroAdicionado = carrinhoService.criarOuAdicionarLivroAoCarrinho(
                livroCarrinhoRequest.getLivroId(),
                1
        );

        // Retorna o DTO com as informações do livro adicionado
        return ResponseEntity.ok(livroAdicionado);
    }


    @GetMapping
    public ResponseEntity<List<LivroCarrinhoDto>> listarItens() {
        return ResponseEntity.ok(carrinhoService.listarItensDoCarrinho());
    }


    @DeleteMapping("/{livroId}")
    public ResponseEntity<Void> removerUmaQuantidade(@PathVariable UUID livroId) {
        carrinhoService.removerUmaQuantidade(livroId);
        return ResponseEntity.ok().build();
    }
}
