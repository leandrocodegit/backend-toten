package br.com.totem.controller.request;

import br.com.totem.model.Endereco;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ClienteRequest {

    private UUID id;
    private String nome;
    private Boolean ativo = Boolean.FALSE;
    private Endereco endereco;
}
