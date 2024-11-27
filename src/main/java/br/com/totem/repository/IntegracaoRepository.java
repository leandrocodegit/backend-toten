package br.com.totem.repository;

import br.com.totem.model.Integracao;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface IntegracaoRepository extends MongoRepository<Integracao, String> {

     boolean existsByNome(String nome);
     Optional<Integracao> findByClientIdAndSecret(String clientId, String secret);
     Optional<Integracao> findByClientId(String clientId);
}
