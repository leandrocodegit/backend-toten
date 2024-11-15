package br.com.totem.controller.request;

import br.com.totem.model.constantes.Comando;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class TemporizadorRequest {

    private UUID idConfiguracao;
    private String mac;
    private boolean cancelar;

}
