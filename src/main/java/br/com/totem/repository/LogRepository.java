package br.com.totem.repository;

import br.com.totem.model.Log;
import br.com.totem.model.LogConexao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LogRepository extends MongoRepository<Log, Long> {


    @Query("{" +
            "   $or: [" +
            "       { 'mac': { $regex: ?0, $options: 'i' } }" +
            "   ]," +
            "}")
    Page<Log> findAllByMac(String mac, Pageable pageable);
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
    List<LogConexao> findLogsGroupedByCommandAndHour();
}
