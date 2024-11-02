package br.com.totem.controller.response;

import br.com.totem.model.constantes.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class UserResponse {

    private UUID id;
    private String nome;
    private String email;
    private Boolean status;
    private List<Role> roles;
}
