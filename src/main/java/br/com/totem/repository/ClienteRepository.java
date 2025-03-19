package br.com.totem.repository;

import br.com.totem.model.Cliente;
import br.com.totem.model.Cor;
import br.com.totem.model.Dispositivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends MongoRepository<Cliente, UUID> {

    @Query("{ 'cliente': { $ne: null }, 'cliente.id': ?0, 'id': ?1 }")
    Optional<Cliente> findByClienteAndId(UUID clienteId, UUID id);
    @Query("{ 'id': ?0, 'principal': true }")
    Optional<Cliente> findByClientePrincipal(UUID id);
    public Page<Cliente> findAllByAtivoAndPrincipal(boolean ativo, boolean principal, Pageable pageable);
    public Page<Cliente> findAllByAtivo(boolean ativo, Pageable pageable);
    @Query("{" +
            "   $or: [" +
            "       { 'id': ?0 }," +
            "       { 'nome': { $regex: ?0, $options: 'i' } }," +
            "       { 'enderecoCompleto': { $regex: ?0, $options: 'i' } }" +
            "   ]," +
            "   'ativo': ?1" +
            "}")
    Page<Cliente> findByIdAndNomeContaining(String texto, boolean ativo, Pageable pageable);

    @Query("{" +
            "'cliente': { $ne: null }, 'cliente.id': ?0," +
            "   $or: [" +
            "       { 'id': ?1 }," +
            "       { 'nome': { $regex: ?0, $options: 'i' } }," +
            "       { 'enderecoCompleto': { $regex: ?0, $options: 'i' } }" +
            "   ]," +
            "   'ativo': ?2" +
            "}")
    Page<Cliente> findByIdAndNomeContaining(UUID clienteId, String texto, boolean ativo, Pageable pageable);
}
