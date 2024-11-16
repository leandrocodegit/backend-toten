package br.com.totem.repository;

import br.com.totem.model.Configuracao;
import br.com.totem.model.Cor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface CorRepository extends MongoRepository<Cor, UUID> {

    List<Cor> findByRapida(boolean rapida);
}
