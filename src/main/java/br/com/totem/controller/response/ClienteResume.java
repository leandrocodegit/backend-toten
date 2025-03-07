package br.com.totem.controller.response;

import br.com.totem.model.Endereco;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ClienteResume {

    private String nome;
    private Endereco endereco;
}
