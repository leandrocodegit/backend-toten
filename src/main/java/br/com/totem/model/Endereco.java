package br.com.totem.model;

import br.com.totem.model.constantes.Comando;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

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
        if(street != null && numero != null && neighborhood != null && cep != null && state != null)
            return street + " " + numero + " " + neighborhood + ", " + cep + ", " + city + " - " + state;
        return  "";
    }
}
