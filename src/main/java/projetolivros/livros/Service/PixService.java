package projetolivros.livros.Service;
import br.com.efi.efisdk.EfiPay;
import br.com.efi.efisdk.exceptions.EfiPayException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import projetolivros.livros.Dto.PixChargeRequest;
import projetolivros.livros.Pix.Credentials;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class PixService {

    @Value("${CLIENT_ID}")
    private String clientId;

    @Value("${CLIENT_SECRET}")
    private String clientSecret;

    public JSONObject pixCreateEVP(){

        JSONObject options = configuringJsonObject();

        try {
            EfiPay efi = new EfiPay(options);
            JSONObject response = efi.call("pixCreateEvp", new HashMap<String,String>(), new JSONObject());
            System.out.println(response.toString());
            return response;
        }catch (EfiPayException e){
            System.out.println(e.getError());
            System.out.println(e.getErrorDescription());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public JSONObject pixCreateCharge(PixChargeRequest pixChargeRequest){

        JSONObject options = configuringJsonObject();

        JSONObject body = new JSONObject();
        body.put("calendario", new JSONObject().put("expiracao", 3600));
        body.put("devedor", new JSONObject().put("cpf", "12345678909").put("nome", "Francisco da Silva"));
        body.put("valor", new JSONObject().put("original", pixChargeRequest.valor()));
        body.put("chave", pixChargeRequest.chave());

        JSONArray infoAdicionais = new JSONArray();
        infoAdicionais.put(new JSONObject().put("nome", "Campo 1").put("valor", "Informação Adicional1 do PSP-Recebedor"));
        infoAdicionais.put(new JSONObject().put("nome", "Campo 2").put("valor", "Informação Adicional2 do PSP-Recebedor"));
        body.put("infoAdicionais", infoAdicionais);

        try {
            EfiPay efi = new EfiPay(options);
            JSONObject response = efi.call("pixCreateImmediateCharge", new HashMap<String,String>(), body);

            int idFromJson= response.getJSONObject("loc").getInt("id");
            pixGenerateQRCode(String.valueOf(idFromJson));



            return response;
        }catch (EfiPayException e){
            System.out.println(e.getError());
            System.out.println(e.getErrorDescription());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    public String pixGenerateQRCode(String id) {
        JSONObject options = configuringJsonObject();
        HashMap<String, String> params = new HashMap<>();
        params.put("id", id);

        try {
            EfiPay efi = new EfiPay(options);
            Map<String, Object> response = efi.call("pixGenerateQRCode", params, new HashMap<>());

            // Captura a URL do QR Code
            String qrCodeUrl = (String) response.get("imagemQrcode");

            return qrCodeUrl; // Retorna a URL da imagem base64

        } catch (EfiPayException e) {
            System.out.println(e.getError());
            System.out.println(e.getErrorDescription());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }




    private JSONObject configuringJsonObject(){
        Credentials credentials = new Credentials();

        JSONObject options = new JSONObject();
        options.put("client_id", clientId);
        options.put("client_secret", clientSecret);
        options.put("certificate", credentials.getCertificate());
        options.put("sandbox", credentials.isSandbox());

        return options;
    }

    public JSONObject verificarPagamento(String txid) {
        JSONObject options = configuringJsonObject();
        HashMap<String, String> params = new HashMap<>();
        params.put("txid", txid);
        try {
            EfiPay efi = new EfiPay(options);
            return efi.call("pixDetailCharge", params, new JSONObject());
        } catch (EfiPayException e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", e.getError());
            errorResponse.put("description", e.getErrorDescription());
            return errorResponse;
        } catch (Exception e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Erro inesperado");
            errorResponse.put("description", e.getMessage());
            return errorResponse;
        }
    }

    public JSONObject verificarid(String id) {
        JSONObject options = configuringJsonObject();
        HashMap<String, String> params = new HashMap<>();
        params.put("id", id);
        try {
            EfiPay efi = new EfiPay(options);
            JSONObject response =  efi.call("pixDetailLocation", params, new JSONObject());
            return  response;
        } catch (EfiPayException e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", e.getError());
            errorResponse.put("description", e.getErrorDescription());
            return errorResponse;
        } catch (Exception e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Erro inesperado");
            errorResponse.put("description", e.getMessage());
            return errorResponse;
        }
    }
}
