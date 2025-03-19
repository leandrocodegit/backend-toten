package br.com.totem.model;

import br.com.totem.model.constantes.StatusConexao;
import br.com.totem.model.constantes.TipoConexao;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(collection = "conexoes")
public class Conexao {

    @Id
    private String id;
    private LocalDateTime ultimaAtualizacao;
    private StatusConexao status;
    private StatusConexao statusMCU;
    private TipoConexao tipoConexao;
    private Boolean habilitarWifi;
    private String ssid;
    private String senha;
    private Boolean habilitarLoraWan;
    private Integer modoLora;
    private String classe;
    private String devEui;
    private String appEui;
    private String appKey;
    private String nwkSKey;
    private String appSKey;
    private String devAddr;
    private Integer txPower;
    private Integer dataRate;
    private Boolean adr = Boolean.FALSE;
    private Double snr;
    private Integer rssi;
    private Boolean autoJoin = Boolean.FALSE;
    private Integer tempoAtividade;
    private Boolean fracionarMensagem = Boolean.FALSE;
    private String latitude;
    private String longitude;

}
