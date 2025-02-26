package projetolivros.livros.Model.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UsuarioRole {

    ADMIN("admin"),
    USER("user");
    private String role;

    UsuarioRole(String role){
        this.role = role;
    }


    public String getRole() {
        return role;
    }


}
