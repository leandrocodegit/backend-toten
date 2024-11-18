package br.com.totem.controller.request;

import br.com.totem.model.Endereco;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DispositivoRequest {

    private String mac;
    private String nome;
    private boolean ignorarAgenda;
    private String latitude;
    private String longitude;
    private Endereco endereco;
    private ConfiguracaoRequest configuracao;
}
