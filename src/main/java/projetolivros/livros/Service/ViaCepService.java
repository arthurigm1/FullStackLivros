package projetolivros.livros.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import projetolivros.livros.Model.Endereco;
@Service
public class ViaCepService {

    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/%s/json/";

    public Endereco buscarEnderecoPorCep(String cep) {
        String url = String.format(VIA_CEP_URL, cep);
        RestTemplate restTemplate = new RestTemplate();
        Endereco endereco = restTemplate.getForObject(url, Endereco.class);

        // Verifica se a API retornou dados válidos (ex: se o "erro" não existe)
        if (endereco != null && endereco.getLogradouro() != null) {
            return endereco; // Endereço válido
        } else {
            return null; // Endereço inválido ou não encontrado
        }
    }
}
