package projetolivros.livros.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Dto.LivroCarrinhoDto;
import projetolivros.livros.Dto.LivroCarrinhoRequestdto;
import projetolivros.livros.Service.CarrinhoService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Carrinho", description = "Endpoints para gerenciamento do carrinho de compras")
@RestController
@RequestMapping("carrinhos")
@RequiredArgsConstructor
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    @Operation(summary = "Adiciona um livro ao carrinho")
    @ApiResponse(responseCode = "200", description = "Livro adicionado ao carrinho com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro na requisição")
    @PostMapping
    public ResponseEntity<LivroCarrinhoRequestdto> adicionarAoCarrinho(@RequestBody @Valid LivroCarrinhoRequestdto livroCarrinhoRequest) throws Exception {
        LivroCarrinhoRequestdto livroAdicionado = carrinhoService.criarOuAdicionarLivroAoCarrinho(
                livroCarrinhoRequest.getLivroId(),
                1
        );
        return ResponseEntity.ok(livroAdicionado);
    }

    @Operation(summary = "Lista todos os itens do carrinho")
    @ApiResponse(responseCode = "200", description = "Lista de itens retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<LivroCarrinhoDto>> listarItens() {
        return ResponseEntity.ok(carrinhoService.listarItensDoCarrinho());
    }

    @Operation(summary = "Remove um item do carrinho")
    @ApiResponse(responseCode = "200", description = "Item removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Item não encontrado no carrinho")
    @DeleteMapping("/{livroId}")
    public ResponseEntity<Void> removerUmaQuantidade(@PathVariable UUID livroId) {
        carrinhoService.removerUmaQuantidade(livroId);
        return ResponseEntity.ok().build();
    }
}
