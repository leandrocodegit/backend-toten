package br.com.totem.controller.response;

import br.com.totem.model.constantes.StatusConexao;
import br.com.totem.model.constantes.TipoConexao;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ConexaoResponse {

    private LocalDateTime ultimaAtualizacao;
    private StatusConexao status;
    private TipoConexao tipoConexao;
    private StatusConexao statusMCU;
    private boolean habilitarWifi;
    private String ssid;
    private String senha;
    private boolean habilitarLoraWan;
    private int modoLora;
    private String classe;
    private String devEui;
    private String appEui;
    private String appKey;
    private String nwkSKey;
    private String appSKey;
    private String devAddr;
    private int txPower;
    private int dataRate;
    private boolean adr;
    private double snr;
    private int rssi;
    private boolean autoJoin;
    private int tempoAtividade;
    private boolean fracionarMensagem;
    private String latitude;
    private String longitude;
}
