package br.com.totem.model;

import br.com.totem.model.constantes.Comando;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogConexao {

    private String hora;
    private Comando comando;
    private int quantidade;

}
