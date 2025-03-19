package br.com.totem.controller.response;

import br.com.totem.model.Configuracao;
import br.com.totem.model.Endereco;
import br.com.totem.model.constantes.Comando;
import br.com.totem.util.TimeUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DispositivoResponse {

    private long id;
    private ClienteResponse cliente;
    private UUID clienteId;
    private String nome;
    private String ip;
    private int memoria;
    private String versao;
    private boolean ignorarAgenda;
    private boolean ativo;
    private boolean permiteComando;
    private String latitude;
    private String longitude;
    private Comando comando;
    private CorResponse cor;
    private Endereco endereco;
    private String enderecoCompleto;
    private Float sensibilidadeVibracao;
    private boolean isTimer;
    private OperacaoResponse operacao;
    private ConexaoResponse conexao;

    public boolean isTimer() {
        return TimeUtil.isTime(this);
    }
}
