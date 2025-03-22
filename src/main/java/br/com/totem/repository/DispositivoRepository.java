package br.com.totem.repository;

import br.com.totem.model.Cliente;
import br.com.totem.model.Dispositivo;
import br.com.totem.model.DispositivoPorCor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DispositivoRepository extends MongoRepository<Dispositivo, Long> {

    @Query("{ 'cliente': { $ne: null }, 'cliente.id': ?0, 'id': ?1 }")
    Optional<Dispositivo> findByClienteAndId(UUID clienteId, long id);
    long countByAtivo(boolean ativo);
    @Aggregation(pipeline = {
            "{ $lookup: { from: 'cor', localField: 'cor.primaria', foreignField: '_id', as: 'configuracaoDetalhada' } }",
            "{ $unwind: '$configuracaoDetalhada' }",
            "{ $group: { _id: '$configuracaoDetalhada.primaria', quantidade: { $sum: 1 } } }",
            "{ $project: { item: '$_id', quantidade: 1, _id: 0 } }"
    })
    List<DispositivoPorCor> agruparPorConfiguracaoPrimaria();

    List<Dispositivo> findAllByIdInAndAtivo(List<Long> ids, boolean ativo);
    @Query("{'cliente': { $ne: null }, 'cliente.id': ?0, 'ativo': ?1, 'cor': { $ne: null } }")
    List<Dispositivo> findAllByAtivo(UUID clienteId, boolean ativo);
    List<Dispositivo> findAllByAtivo(boolean ativo);
    @Query("{'cliente': { $ne: null }, 'cliente.id': ?0, 'ativo': ?1 }")
    Page<Dispositivo> findAllByAtivo(UUID clienteId, boolean ativo, Pageable pageable);
    @Query("{'cliente': { $ne: null }, 'cliente.id': ?0}")
    Page<Dispositivo> findAllByCliente(UUID clienteId, Pageable pageable);
    @Query("{ 'ativo': ?0 }")
    Page<Dispositivo> findAllByInativo(boolean ativo, Pageable pageable);
    @Query("{'cliente': { $ne: null },  'cliente.id': ?0, 'ativo' : true, 'ultimaAtualizacao' : { $lt: ?1 }, 'cor': { $ne: null } }")
    List<Dispositivo> findAllAtivosComUltimaAtualizacaoAntes(UUID clienteId, Date dataLimite);
    @Query("{'cliente': { $ne: null }, 'cliente.id': ?0, 'ativo' : true, 'ultimaAtualizacao' : { $lt: ?1 }, 'cor': { $ne: null } }")
    Page<Dispositivo> findAllAtivosComUltimaAtualizacaoAntes(UUID clienteId, Date dataLimite, Pageable pageable);

    @Query("{ 'cor': null }")
    List<Dispositivo> findDispositivosSemConfiguracao();
    @Query("{'cliente': { $ne: null }, 'cliente.id': ?0, 'configuracao': null }")
    Page<Dispositivo> findDispositivosSemConfiguracao(UUID clienteId, Pageable pageable);

    @Query("{" +
            "   $or: [" +
            "       { 'id': ?0 }," +
            "       { 'nome': { $regex: ?0, $options: 'i' } }," +
            "       { 'enderecoCompleto': { $regex: ?0, $options: 'i' } }" +
            "   ]," +
//            "   'ativo': true" +
            "}")
    Page<Dispositivo> findByIdAndNomeContaining(String texto, Pageable pageable);
    @Query("{" +
            "'cliente': { $ne: null }, 'cliente.id': ?0," +
            "   $or: [" +
            "       { 'id': ?1 }," +
            "       { 'nome': { $regex: ?1, $options: 'i' } }," +
            "       { 'enderecoCompleto': { $regex: ?1, $options: 'i' } }" +
            "   ]," +
//            "   'ativo': true" +
            "}")
    Page<Dispositivo> findByIdAndNomeContainingAndClienteId(UUID clienteId, String texto, Pageable pageable);
    @Query("{ 'cor.id': ?0 }")
    List<Dispositivo> findDispositivosPorCor(UUID id);


}
