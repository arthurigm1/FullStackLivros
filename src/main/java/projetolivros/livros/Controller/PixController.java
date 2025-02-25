package projetolivros.livros.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Dto.PixChargeRequest;
import projetolivros.livros.Service.PixService;

@RestController
@RequestMapping("/pix")
@RequiredArgsConstructor
@Tag(name = "PIX", description = "Gerenciamento de pagamentos via PIX")
public class PixController {

    private final PixService pixService;

    @Operation(summary = "Gerar EVP", description = "Cria uma chave EVP (chave aleatória) para transações via PIX.")
    @GetMapping
    public ResponseEntity<String> pixCreateEVP() {
        JSONObject response = this.pixService.pixCreateEVP();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.toString());
    }

    @Operation(summary = "Criar cobrança PIX", description = "Cria uma cobrança PIX com base nos detalhes fornecidos.")
    @PostMapping
    public ResponseEntity<String> pixCreateCharge(@RequestBody PixChargeRequest pixChargeRequest) {
        JSONObject response = this.pixService.pixCreateCharge(pixChargeRequest);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.toString());
    }

    @GetMapping("/verificar/{txid}")
    public ResponseEntity<String> verificarPagamento(@PathVariable String txid) {
        JSONObject resposta = pixService.verificarPagamento(txid);
        return ResponseEntity.ok(resposta.toString());
    }

    @GetMapping("/verificarid/{id}")
    public ResponseEntity<String> verificarId(@PathVariable String id) {
        // Chama o método verificarid no serviço e retorna a resposta
        JSONObject resposta =  pixService.verificarid(id);
        return ResponseEntity.ok(resposta.toString());
    }
}
