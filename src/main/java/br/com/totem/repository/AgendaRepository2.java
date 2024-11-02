package br.com.totem.repository;

import br.com.totem.model.Agenda;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendaRepository2 extends MongoRepository<Agenda, UUID> {

    @Query("{ 'configuracao._id': ?0 }")
    List<Agenda> findAgendasByConfiguracaoId(UUID configuracaoId);

    @Query("{ 'dispositivos.mac': ?0 }")
    List<Agenda> findAgendasByDispositivoId(String mac);

    @Query("{" +
            " $expr: {" +
            "   $and: [" +
            "     { $lte: [ { $dateFromParts: { year: { $year: '$inicio' }, month: { $month: '$inicio' }, day: { $dayOfMonth: '$inicio' } } }, ?0 ] }," +
            "     { $gte: [ { $dateFromParts: { year: { $year: '$termino' }, month: { $month: '$termino' }, day: { $dayOfMonth: '$termino' } } }, ?0 ] }," +
            "     { $ne: [ { $dateToString: { format: '%Y-%m-%d', date: '$execucao' } }, { $dateToString: { format: '%Y-%m-%d', date: ?0 } } ] }" +
            "   ]" +
            " }," +
            " 'ativo': true" +
            "}")
    List<Agenda> findAgendasPorDiaMes(LocalDateTime data);

    @Query("{" +
            " $expr: {" +
            "   $and: [" +
            "     { $lte: [ { $dateFromParts: { month: { $month: '$inicio' }, day: { $dayOfMonth: '$inicio' } } }, ?0 ] }," +
            "     { $gte: [ { $dateFromParts: { month: { $month: '$termino' }, day: { $dayOfMonth: '$termino' } } }, ?0 ] }," +
            "     { $ne: [ { $dateToString: { format: '%m-%d', date: '$execucao' } }, { $dateToString: { format: '%m-%d', date: ?0 } } ] }" +
            "   ]" +
            " }," +
            " 'ativo': true" +
            "}")
    List<Agenda> findAgendasPorPeriodo(LocalDateTime data);

    @Query("{" +
            "   $and: [" +
            "     { $expr: {" +
            "         $or: [" +
            "           { $and: [" +
            "               { $lte: [ { $dateToString: { format: '%m-%d', date: '$inicio' } }, ?0 ] }," +
            "               { $gte: [ { $dateToString: { format: '%m-%d', date: '$termino' } }, ?0 ] }," +
            "               { $ne: [ { $dateToString: { format: '%m-%d', date: '$execucao' } }, ?0 ] }" +
            "           ] }," +
            "           { $and: [" +
            "               { $lte: [ { $dateToString: { format: '%m-%d', date: '$inicio' } }, ?0 ] }," +
            "               { $gte: [ { $dateToString: { format: '%m-%d', date: '$termino' } }, ?0 ] }," +
            "               { $ne: [ { $dateToString: { format: '%m-%d', date: '$execucao' } }, ?0 ] }" +
            "           ] }" +
            "         ]" +
            "     }}," +
            "     { 'ativo': true }" +
            "   ]" +
            "}")
    List<Agenda> listaTodasAgendasHoje(LocalDateTime data);

    @Query("{" +
            " $expr: {" +
            "   $and: [" +
            "     { $lte: [ { $dateFromParts: { year: { $year: '$inicio' }, month: { $month: '$inicio' }, day: { $dayOfMonth: '$inicio' } } }, ?1 ] }," +
            "     { $gte: [ { $dateFromParts: { year: { $year: '$termino' }, month: { $month: '$termino' }, day: { $dayOfMonth: '$termino' } } }, ?0 ] }" +
            "   ]" +
            " }," +
            " 'dispositivos.mac': ?2," +
            " 'ativo': true" +
            "}")
    Optional<Agenda> findFirstByPeirodoDispositivo(LocalDateTime inicio, LocalDateTime termino, String dispositivoMac);

    @Query("{" +
            " $expr: {" +
            "   $and: [" +
            "     { $lte: [ { $dateFromParts: { month: { $month: '$inicio' }, day: { $dayOfMonth: '$inicio' } } }, ?1 ] }," +
            "     { $gte: [ { $dateFromParts: { month: { $month: '$termino' }, day: { $dayOfMonth: '$termino' } } }, ?0 ] }" +
            "   ]" +
            " }," +
            " 'dispositivos.mac': ?2," +
            " 'ativo': true" +
            "}")
    Optional<Agenda> findFirstByDiaMesAndDispositivo(LocalDateTime inicio, LocalDateTime termino, String dispositivoMac);
}
