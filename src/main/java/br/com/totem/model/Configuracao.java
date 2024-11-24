package br.com.totem.model;

import br.com.totem.model.constantes.TipoCor;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Configuracao {

    private int leds;
    private int intensidade;
    private int faixa;
    private TipoCor tipoCor;

}
