package br.com.totem.controller.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfiguracaoResponse {

    private int leds;
    private int faixa;
    private int intensidade;

}
