package br.com.totem.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MensagemEmailRequest {

    private String email;
    private String mensagem;
    private String assunto;
}
