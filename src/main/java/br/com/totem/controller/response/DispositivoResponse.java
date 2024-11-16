package br.com.totem.controller.response;

import br.com.totem.model.Configuracao;
import br.com.totem.model.Cor;
import br.com.totem.model.Endereco;
import br.com.totem.model.Temporizador;
import br.com.totem.model.constantes.Comando;
import br.com.totem.model.constantes.Efeito;
import br.com.totem.utils.TimeUtil;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DispositivoResponse {


    private String mac;
    private String nome;
    private String ip;
    private int memoria;
    private String versao;
    private boolean ignorarAgenda;
    private LocalDateTime ultimaAtualizacao;
    private boolean ativo;
    private String conexao;
    private String latitude;
    private String longitude;
    private Comando comando;
    private Configuracao configuracao;
    private CorResponse cor;
    private Endereco endereco;
    private String enderecoComplento;
    private String enderecoCompleto;
    private boolean isTimer;
    private Temporizador temporizador;


    public String getConexao() {
        long differenceInMinutes = Duration.between(ultimaAtualizacao, LocalDateTime.now()).toMinutes();
        if (differenceInMinutes >= 5)
           return "Offline";
        return "Online";
    }

    public boolean isTimer() {
        return TimeUtil.isTime(this);
    }
}
