package br.com.totem.controller.request;

import br.com.totem.controller.request.validacoes.ConfiguracaoCreate;
import br.com.totem.controller.request.validacoes.ConfiguracaoUpdate;
import br.com.totem.model.constantes.Efeito;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfiguracaoRequest {

    private int leds;
    private int intensidade;
    private int faixa;

}
