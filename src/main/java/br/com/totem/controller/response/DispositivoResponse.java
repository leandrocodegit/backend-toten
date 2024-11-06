package br.com.totem.controller.response;

import br.com.totem.model.Configuracao;
import br.com.totem.model.constantes.Comando;
import br.com.totem.model.constantes.Efeito;
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


    public String getConexao() {
        long differenceInMinutes = Duration.between(ultimaAtualizacao, LocalDateTime.now()).toMinutes();
        if (differenceInMinutes >= 5)
           return "Offline";
        return "Online";
    }
}
