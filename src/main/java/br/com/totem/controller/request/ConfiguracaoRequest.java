package br.com.totem.controller.request;

import br.com.totem.controller.request.validacoes.ConfiguracaoCreate;
import br.com.totem.controller.request.validacoes.ConfiguracaoUpdate;
import br.com.totem.model.constantes.Efeito;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfiguracaoRequest {

    @NotNull(groups = {ConfiguracaoUpdate.class})
    private UUID id;
    @NotNull(groups = {ConfiguracaoCreate.class})
    @NotBlank(groups = {ConfiguracaoCreate.class})
    private String nome;
    @NotNull(groups = {ConfiguracaoCreate.class})
    private Efeito efeito;
    private int[] cor;
    private String primaria;
    private String secundaria;
    private int leds;
    private int faixa;
    private int intensidade;
    private int[] correcao;
    private int velocidade;
    private boolean responder;
    private String mac;
    private long time;
    private boolean rapida;

}
