package br.com.totem.repository;

import br.com.totem.model.Cor;
import br.com.totem.model.Integracao;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface IntegracaoRepository extends MongoRepository<Integracao, String> {

     @Query("{ 'cliente': { $ne: null }, 'cliente.id': ?0 }")
     Page<Integracao> findAllByCliente(UUID clienteId, Pageable pageable);
     boolean existsByNome(String nome);
     Optional<Integracao> findByClientIdAndSecret(String clientId, String secret);
     Optional<Integracao> findByClientId(String clientId);
}
