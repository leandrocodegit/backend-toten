package br.com.totem.repository;

import br.com.totem.model.Configuracao;
import br.com.totem.model.Cor;
import br.com.totem.model.Dispositivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CorRepository extends MongoRepository<Cor, UUID> {


    @Query("{ 'cliente': { $ne: null }, 'cliente.id': ?0, 'id': ?1 }")
    Optional<Cor> findByClienteAndId(UUID clienteId, UUID id);
    Optional<Cor> findByIdAndExclusiva(UUID ID, boolean exclusiva);
    @Query("{ 'cliente': { $ne: null }, 'cliente.id': ?0, 'id': ?1, 'exclusiva': ?2 }")
    Optional<Cor> findByIdAndExclusiva(UUID clienteId, UUID ID, boolean exclusiva);
    Page<Cor> findAllByRapidaAndVibracaoAndExclusiva(boolean rapida, boolean vibracao, boolean exclusiva, Pageable pageable);
    @Query("{ 'cliente': { $ne: null }, 'cliente.id': ?0, 'rapida': ?1, 'vibracao': ?2, 'exclusiva': ?3 }")
    Page<Cor> findAllByRapidaAndVibracaoAndExclusiva(UUID clienteId, boolean rapida, boolean vibracao, boolean exclusiva, Pageable pageable);
    Page<Cor> findAllByVibracaoAndExclusiva(boolean vibracao, boolean exclusiva, Pageable pageable);
    @Query("{ 'cliente': { $ne: null }, 'cliente.id': ?0, 'vibracao': ?1, 'exclusiva': ?2 }")
    Page<Cor> findAllByVibracaoAndExclusiva(UUID clienteId, boolean vibracao, boolean exclusiva, Pageable pageable);


}
