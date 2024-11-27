package br.com.totem.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Endereco {

    private String cep;
    private String state;
    private String city;
    private String neighborhood;
    private String street;
    private String numero;

    @Override
    public String toString() {
        if(street != null && neighborhood != null && cep != null && state != null)
            return street + " " + (numero != null ? numero : "") + " " + neighborhood + ", " + cep + ", " + city + " - " + state;
        return  "";
    }
}
