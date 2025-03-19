package br.com.totem.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConexaoRequest {

    private Boolean habilitarWifi;
    private String ssid;
    private String senha;
    private boolean habilitarLoraWan;
    private Integer modoLora;
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
    private boolean autoJoin;
    private int tempoAtividade;
    private boolean fracionarMensagem;
    private String latitude;
    private String longitude;

}
