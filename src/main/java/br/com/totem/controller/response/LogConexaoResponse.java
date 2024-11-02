package br.com.totem.controller.response;

import br.com.totem.model.constantes.Comando;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogConexaoResponse {

    private String hora;
    private Comando comando;
    private int quantidade;

}
