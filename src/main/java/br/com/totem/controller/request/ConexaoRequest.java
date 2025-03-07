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
    private int adr;
    private int snr;
    private int rssi;
    private boolean autoJoin;

}
