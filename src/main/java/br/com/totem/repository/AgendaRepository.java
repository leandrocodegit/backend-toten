package br.com.totem.repository;

import br.com.totem.model.Agenda;
import br.com.totem.model.DispositivoPorCor;
import br.com.totem.model.User;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendaRepository extends MongoRepository<Agenda, UUID> {

    @Query("{ 'configuracao._id': ?0 }")
    List<Agenda> findAgendasByConfiguracaoId(UUID configuracaoId);
    @Query("{ 'dispositivos.mac': ?0 }")
    List<Agenda> findAgendasByDispositivoId(String mac);

    @Query("{" +
            "   $expr: {" +
            "     $and: [" +
            "       { $lte: [ { $dateToString: { format: '%m-%d', date: '$inicio' } }, { $dateToString: { format: '%m-%d', date: ?0 } } ] }," +
            "       { $gte: [ { $dateToString: { format: '%m-%d', date: '$termino' } }, { $dateToString: { format: '%m-%d', date: ?0 } } ] }," +
            "       { $or: [" +
            "           { $not: { $ifNull: ['$execucao', false] } }, " +
            "           { $ne: [ { $dateToString: { format: '%m-%d', date: '$execucao' } }, { $dateToString: { format: '%m-%d', date: ?0 } } ] }" +
            "       ] }" +
            "     ]" +
            "   }," +
            "   'ativo': true" +
            "}")
    List<Agenda> findAgendasByDataDentroDoIntervalo(LocalDate data);

    @Query("{" +
            "   $expr: {" +
            "     $and: [" +
            "       { $lte: [ { $dateToString: { format: '%m-%d', date: '$inicio' } }, { $dateToString: { format: '%m-%d', date: ?0 } } ] }," +
            "       { $gte: [ { $dateToString: { format: '%m-%d', date: '$termino' } }, { $dateToString: { format: '%m-%d', date: ?0 } } ] }," +
            "     ]" +
            "   }," +
            "   'ativo': true" +
            "}")
    List<Agenda> findAllAgendasByDataDentroDoIntervalo(LocalDate data);

    @Query("{" +
            " $expr: {" +
            "   $and: [" +
            "       { $lte: [ { $dateToString: { format: '%m-%d', date: '$inicio' } }, { $dateToString: { format: '%m-%d', date: ?1 } } ] }," +
            "       { $gte: [ { $dateToString: { format: '%m-%d', date: '$termino' } }, { $dateToString: { format: '%m-%d', date: ?0 } } ] }" +
            "   ]" +
            " }," +
            " 'dispositivos.mac': ?2," +
            " 'ativo': true" +
            " '_id': { $ne: ?3 }" +
            "}")
    List<Agenda> findFirstByDataAndDispositivo(LocalDate inicio, LocalDate termino, String dispositivoMac, UUID agendaId);


}


