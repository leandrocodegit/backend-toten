package br.com.totem.repository;

import br.com.totem.model.Configuracao;
import br.com.totem.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConfiguracaoRepository extends MongoRepository<Configuracao, UUID> {

}
