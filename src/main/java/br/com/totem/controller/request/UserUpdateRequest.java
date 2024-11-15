package br.com.totem.controller.request;

import br.com.totem.model.constantes.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class UserUpdateRequest {

    private UUID id;
    private String nome;
    private String email;
    private String password;
    private String ConfirmPassword;
    private List<Role> roles;
}
