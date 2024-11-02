package br.com.totem.controller.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AuthUserRequest {

    private String email;
    private String password;

}
