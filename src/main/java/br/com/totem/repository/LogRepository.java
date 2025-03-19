package br.com.totem.repository;

import br.com.totem.model.Cliente;
import br.com.totem.model.Log;
import br.com.totem.model.LogConexao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.UUID;

public interface LogRepository extends MongoRepository<Log, Long> {



    Page<Log> findAllByClienteAndComandoInOrderByDataDesc(Cliente cliente, List<String> comandos, Pageable pageable);
    Page<Log> findAllByClienteOrderByDataDesc(Cliente cliente, Pageable pageable);
    @Aggregation(pipeline = {
            "{ $match: { 'cliente': { $ne: null }, 'cliente.id': ?0 } }",
            "{ $match: { comando: { $in: [ 'ONLINE', 'OFFLINE' ] } } }",
            "{ $group: { " +
                    "_id: { comando: '$comando', hour: { $hour: '$data' } }, " +
                    "quantidade: { $sum: 1 } " +
                    "} }",
            "{ $project: { " +
                    "hora: '$_id.hour', " +
                    "comando: '$_id.comando', " +
                    "quantidade: 1, " +
                    "_id: 0 " +
                    "} }",
            "{ $sort: { hour: 1 } }" +
            "{ $limit: 100 }"
    })
    List<LogConexao> findLogsGroupedByCommandAndHour(UUID clienteId);
}
