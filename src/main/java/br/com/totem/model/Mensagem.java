package br.com.totem.model;

import br.com.totem.model.constantes.Efeito;
import br.com.totem.model.constantes.Comando;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mensagem {

    private String id;
    private String ip;
    private int memoria;
    private Comando comando;
    private Efeito efeito;
    private int[] parametros;

}