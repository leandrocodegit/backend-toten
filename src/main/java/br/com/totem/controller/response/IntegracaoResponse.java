package br.com.totem.controller.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntegracaoResponse {

    private String nome;
    private String clientId;
    private String secret;
    private boolean status;
}
