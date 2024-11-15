package br.com.totem.repository;

import br.com.totem.model.Configuracao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface ConfiguracaoRepository extends MongoRepository<Configuracao, UUID> {

    List<Configuracao> findByRapida(boolean rapida);
}
