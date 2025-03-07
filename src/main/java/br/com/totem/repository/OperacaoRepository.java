package br.com.totem.repository;

import br.com.totem.model.Cor;
import br.com.totem.model.Dispositivo;
import br.com.totem.model.Operacao;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OperacaoRepository extends MongoRepository<Operacao, Long> {

    @Query("{ 'corTemporizador.id': ?0 }")
    List<Operacao> findDispositivosPorCorTemporizada(UUID id);
    @Query("{ 'corVibracao.id': ?0 }")
    List<Operacao> findDispositivosPorCorVibracao(UUID id);
}
