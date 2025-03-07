package br.com.totem.controller.response;

import br.com.totem.model.Cliente;
import br.com.totem.model.Endereco;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ClienteResponse {

    private UUID id;
    private String nome;
    private Boolean ativo = Boolean.FALSE;
    private Endereco endereco;
    private List<ClienteResponse> clientes;
}
