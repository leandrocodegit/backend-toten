package br.com.totem.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MensagemEmailRequest {

    private String email;
    private String nome;
    private String celular;
    private String mensagem;
    private String assunto;
}
