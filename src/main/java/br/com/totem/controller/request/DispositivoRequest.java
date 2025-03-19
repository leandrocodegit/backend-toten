package br.com.totem.controller.request;

import br.com.totem.model.Endereco;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DispositivoRequest {

    private long id;
    private ClienteRequest cliente;
    private String nome;
    private boolean ignorarAgenda;
    private boolean permiteComando;
    private String latitude;
    private String longitude;
    private Endereco endereco;
    private Float sensibilidadeVibracao;
    private ConfiguracaoRequest configuracao;
    private ConexaoRequest conexao;
}
