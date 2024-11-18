package br.com.totem.controller.response;

import br.com.totem.model.constantes.Role;
import lombok.Getter;
import lombok.Setter;

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
