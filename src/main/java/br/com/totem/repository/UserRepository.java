package br.com.totem.repository;

import br.com.totem.model.Cliente;
import br.com.totem.model.Cor;
import br.com.totem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository  extends MongoRepository<User, UUID> {


    @Query("{ 'cliente': { $ne: null }, 'cliente.id': ?0, 'id': ?1 }")
    Optional<User> findByClienteAndId(UUID clienteId, UUID id);
    Optional<User> findByEmailAndStatus(String email, Boolean status);
    @Query("{" +
            "   $and: [" +
            "       { 'email': ?0 }," +
            "       { 'email': { $not: { $regex: 'master', $options: 'i' } } }" +
            "   ]" +
            "}")
    Optional<User> buscarPorEmail(String email);
    @Query("{" +
            "'cliente': { $ne: null }, 'cliente.id': ?0, " +
            "   $and: [" +
            "       { 'email': ?1 }," +
            "       { 'email': { $not: { $regex: 'master', $options: 'i' } } }" +
            "   ]" +
            "}")
    Optional<User> buscarPorEmail(UUID clienteId, String email);
    @Query("{'cliente': { $ne: null }, 'cliente.id': ?0, 'email': { $not: { $regex: 'master', $options: 'i' } } }")
    Optional<User> findByEmail(UUID clienteId, String email);
    Optional<User> findByEmail(String email);

    long countByClienteAndStatus(Cliente clienteId, Boolean status);

    @Query("{" +
            "'cliente': { $ne: null }, 'cliente.id': ?0, " +
            "   $and: [" +
            "       { $or: [" +
            "           { 'email': { $regex: ?1, $options: 'i' } }," +
            "           { 'nome': { $regex: ?1, $options: 'i' } }" +
            "       ]}," +
            "       { 'email': { $not: { $regex: 'master', $options: 'i' } } }" +
            "   ]" +
            "}")
    Page<User> findByNomeAndEmailContaining(UUID clienteId, String texto, Pageable pageable);
    @Query("{'cliente': { $ne: null }, 'cliente.id': ?0, 'business': ?1, 'email': { $not: { $regex: 'master', $options: 'i' } } }")
    Page<User> listaUsuarios(UUID clienteId, boolean business, Pageable pageable);
    Page<User> findAllByBusiness(boolean business, Pageable pageable);
}
