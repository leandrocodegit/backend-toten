package br.com.totem.controller.response;

import br.com.totem.model.constantes.TipoCor;
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
    private TipoCor tipoCor;

}
