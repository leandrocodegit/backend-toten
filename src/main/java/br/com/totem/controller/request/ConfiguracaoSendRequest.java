package br.com.totem.controller.request;

import br.com.totem.model.Configuracao;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConfiguracaoSendRequest {

    private CorRequest cor;
    private String device;

}
