package br.com.totem.controller.response;

import br.com.totem.model.Configuracao;
import br.com.totem.model.Endereco;
import br.com.totem.model.constantes.Comando;
import br.com.totem.util.TimeUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DispositivoResponse {

    private String mac;
    private String nome;
    private String ip;
    private int memoria;
    private String versao;
    private boolean ignorarAgenda;
    private boolean ativo;
    private String latitude;
    private String longitude;
    private Comando comando;
    private Configuracao configuracao;
    private CorResponse cor;
    private Endereco endereco;
    private String enderecoCompleto;
    private boolean isTimer;
    private OperacaoResponse operacao;
    private ConexaoResponse conexao;

    public boolean isTimer() {
        return TimeUtil.isTime(this);
    }
}
