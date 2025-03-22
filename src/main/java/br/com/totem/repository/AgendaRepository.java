package br.com.totem.repository;

import br.com.totem.model.Agenda;
import br.com.totem.model.Cliente;
import br.com.totem.model.Operacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendaRepository extends MongoRepository<Agenda, UUID> {

    @Query("{ 'cliente': { $ne: null }, 'cliente.id': ?0, }")
    Page<Agenda> findAllByCliente(UUID clienteId, Pageable pageable);
    @Query("{ 'cliente': { $ne: null }, 'cliente.id': ?0, 'id': ?1 }")
    Optional<Agenda> findByClienteAndId(UUID clienteId, UUID id);
    @Query("{ 'cor._id': ?0 }")
    List<Agenda> findAgendasByCorId(UUID configuracaoId);
    @Query("{'cliente': { $ne: null }, 'cliente.id': ?0, 'cor._id': ?1 }")
    List<Agenda> findAgendasByCorId(UUID clienteId, UUID configuracaoId);


    @Query("{ 'dispositivos.id': ?0 }")
    List<Agenda> findAgendasByDispositivoId(long id);
    @Query("{'cliente': { $ne: null },  'cliente.id': ?0, 'dispositivos.id': ?1 }")
    List<Agenda> findAgendasByDispositivoId(UUID clienteId, long id);

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
            "       { $ne: {id: 0}" +
            "       { $lte: [ { $dateToString: { format: '%m-%d', date: '$inicio' } }, { $dateToString: { format: '%m-%d', date: ?1 } } ] }," +
            "       { $gte: [ { $dateToString: { format: '%m-%d', date: '$termino' } }, { $dateToString: { format: '%m-%d', date: ?2 } } ] }," +
            "     ]" +
            "   }," +
            "   'ativo': true" +
            "   'todos': true" +
            "}")
    List<Agenda> findAllAgendasByDataDentroDoIntervaloTodosDispositivos(UUID id, LocalDate inicio, LocalDate termino);


    @Query("{ $or: [ { 'todos': true }, { 'dispositivos': { $in: ?0 } } ], 'ativo': true }")
    List<Agenda> findByDispositivosOuTodosAtivos(List<Long> dispositivos);

    @Query("{ $and: [ { 'id': { $ne: ?0 } }, { $or: [ { 'todos': true }, { 'dispositivos': { $in: ?1 } } ] }, { 'ativo': true } ] }")
    List<Agenda> findByDispositivosOuTodosAtivos(UUID id, List<Long> dispositivos);

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
            "'cliente': { $ne: null },  'cliente.id': ?0, 'dispositivos.id': ?1 }" +
            "   $expr: {" +
            "     $and: [" +
            "       { $lte: [ { $dateToString: { format: '%m-%d', date: '$inicio' } }, { $dateToString: { format: '%m-%d', date: ?1 } } ] }," +
            "       { $gte: [ { $dateToString: { format: '%m-%d', date: '$termino' } }, { $dateToString: { format: '%m-%d', date: ?1 } } ] }," +
            "     ]" +
            "   }," +
            "   'ativo': true" +
            "}")
    List<Agenda> findAllAgendasByDataDentroDoIntervalo(UUID clienteId, LocalDate data);



    @Query("{" +
            " $expr: {" +
            "   $and: [" +
            "       { $eq: [ { $month: '$inicio' }, ?0 ] }," +
            "   ]" +
            " }," +
            " 'ativo': ?1" +
            "}")
    List<Agenda> findAllDoMesAtualInOrderByInicioDesc(int mes, boolean ativo, Sort sort);
    @Query("{" +
            "'cliente': { $ne: null },  'cliente.id': ?0," +
            " $expr: {" +
            "   $and: [" +
            "       { $eq: [ { $month: '$inicio' }, ?0 ] }," +
            "   ]" +
            " }," +
            " 'ativo': ?1" +
            "}")
    List<Agenda> findAllDoMesAtualInOrderByInicioDesc(UUID clienteId, int mes, boolean ativo, Sort sort);
    @Aggregation(pipeline = {
                    "     {" +
                    "       $project:" +
                    "         {" +
                    "           month: { $month: '$inicio' }" +
                    "         }" +
                    "     }," +
            "{ $match: { month: 11, ativo: ?0 }} "
    })
    List<Integer> findAllDoMes(boolean ativo);

    @Query("{'cliente': { $ne: null },  'cliente.id': ?0, 'ativo': ?1 }")
    List<Agenda> findAllByAtivo(UUID clienteId, boolean ativo);


    List<Agenda> findAllByAtivo(boolean ativo);

    @Query("{" +
            "'cliente': { $ne: null },  'cliente.id': ?0," +
            " $expr: {" +
            "   $and: [" +
            "       { $lte: [ { $dateToString: { format: '%m-%d', date: '$inicio' } }, { $dateToString: { format: '%m-%d', date: ?1 } } ] }," +
            "       { $gte: [ { $dateToString: { format: '%m-%d', date: '$termino' } }, { $dateToString: { format: '%m-%d', date: ?2 } } ] }" +
            "   ]" +
            " }," +
            " 'dispositivos.id': ?3," +
            " 'ativo': true" +
            " '_id': { $ne: ?4 }" +
            "}")
    List<Agenda> findFirstByDataAndDispositivo(UUID clienteId, LocalDate inicio, LocalDate termino, long dispositivoId, UUID agendaId);

    @Query("{ 'cor.id': ?0 }")
    List<Agenda> findAgendasPorCorVibracao(UUID id);


}


