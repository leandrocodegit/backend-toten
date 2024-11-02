package br.com.totem.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequest {

    private String password;
    private String email;
}
