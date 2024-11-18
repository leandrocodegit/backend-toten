package br.com.totem.repository;

import br.com.totem.model.Dispositivo;
import br.com.totem.model.DispositivoPorCor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface DispositivoRepository extends MongoRepository<Dispositivo, String> {

    @Aggregation(pipeline = {
            "{ $lookup: { from: 'cor', localField: 'cor.primaria', foreignField: '_id', as: 'configuracaoDetalhada' } }",
            "{ $unwind: '$configuracaoDetalhada' }",
            "{ $group: { _id: '$configuracaoDetalhada.primaria', quantidade: { $sum: 1 } } }",
            "{ $project: { item: '$_id', quantidade: 1, _id: 0 } }"
    })
    List<DispositivoPorCor> agruparPorConfiguracaoPrimaria();

    List<Dispositivo> findAllByMacInAndAtivo(List<String> macs, boolean ativo);
    @Query("{ 'ativo': ?0, 'configuracao': { $ne: null } }")
    List<Dispositivo> findAllByAtivo(boolean ativo);
    @Query("{ 'ativo': ?0, 'configuracao': { $ne: null } }")
    Page<Dispositivo> findAllByAtivo(boolean ativo, Pageable pageable);
    @Query("{ 'ativo': ?0 }")
    Page<Dispositivo> findAllByInativo(boolean ativo, Pageable pageable);
    @Query("{ 'ativo' : true, 'ultimaAtualizacao' : { $lt: ?0 }, 'configuracao': { $ne: null } }")
    List<Dispositivo> findAllAtivosComUltimaAtualizacaoAntes(Date dataLimite);
    @Query("{ 'ativo' : true, 'ultimaAtualizacao' : { $lt: ?0 }, 'configuracao': { $ne: null } }")
    Page<Dispositivo> findAllAtivosComUltimaAtualizacaoAntes(Date dataLimite, Pageable pageable);
    @Query("{ 'ativo' : true, 'ultimaAtualizacao' : { $lt: ?0 }, 'comando' : 'ONLINE', 'configuracao': { $ne: null } }")
    List<Dispositivo> findAllAtivosComUltimaAtualizacaoAntesQueEstavaoOnline(Date dataLimite);

    @Query("{ 'configuracao': null }")
    List<Dispositivo> findDispositivosSemConfiguracao();
    @Query("{ 'configuracao': null }")
    Page<Dispositivo> findDispositivosSemConfiguracao(Pageable pageable);

    @Query("{" +
            "   $or: [" +
            "       { 'mac': ?0 }," +
            "       { 'nome': { $regex: ?0, $options: 'i' } }," +
            "       { 'enderecoCompleto': { $regex: ?0, $options: 'i' } }" +
            "   ]," +
//            "   'ativo': true" +
            "}")
    Page<Dispositivo> findByMacAndNomeContaining(String texto, Pageable pageable);

}
