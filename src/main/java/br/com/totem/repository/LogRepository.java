package br.com.totem.repository;

import br.com.totem.controller.response.LogConexaoResponse;
import br.com.totem.model.Log;
import br.com.totem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface LogRepository extends MongoRepository<Log, Long> {


    Page<Log> findAllByComandoInOrderByDataDesc(List<String> comandos, Pageable pageable);
    @Aggregation(pipeline = {
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
    List<LogConexaoResponse> findLogsGroupedByCommandAndHour();
}
