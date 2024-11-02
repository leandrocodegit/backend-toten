package br.com.totem.model;

import br.com.totem.model.constantes.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
@Builder
@Document(collection = "users")
public class User {

    @Id
    private UUID id;
    private String nome;
    private String password;
    private String email;
    private Boolean status;
    private List<Role> roles;

}
