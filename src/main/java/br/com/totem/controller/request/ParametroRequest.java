package br.com.totem.controller.request;

import br.com.totem.model.constantes.Comando;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ParametroRequest {

    private Comando comando;
    private List<String> devices;

}
