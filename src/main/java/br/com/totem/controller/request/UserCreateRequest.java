package br.com.totem.controller.request;

import br.com.totem.model.constantes.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class UserCreateRequest extends AuthUserRequest {

    private UUID clienteId;
    private String nome;
    private List<Role> roles;
    private Boolean business;

}
