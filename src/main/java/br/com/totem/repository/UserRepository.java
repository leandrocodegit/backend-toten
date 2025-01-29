package br.com.totem.repository;

import br.com.totem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository  extends MongoRepository<User, UUID> {

    Optional<User> findByEmailAndStatus(String email, Boolean status);
    @Query("{" +
            "   $and: [" +
            "       { 'email': ?0 }," +
            "       { 'email': { $not: { $regex: 'master', $options: 'i' } } }" +
            "   ]" +
            "}")
    Optional<User> buscarPorEmail(String email);
    Optional<User> findByEmail(String email);
    long countByStatus(Boolean status);

    @Query("{" +
            "   $and: [" +
            "       { $or: [" +
            "           { 'email': { $regex: ?0, $options: 'i' } }," +
            "           { 'nome': { $regex: ?0, $options: 'i' } }" +
            "       ]}," +
            "       { 'email': { $not: { $regex: 'master', $options: 'i' } } }" +
            "   ]" +
            "}")
    Page<User> findByNomeAndEmailContaining(String texto, Pageable pageable);
    @Query("{ 'email': { $not: { $regex: 'master', $options: 'i' } } }")
    Page<User> listaUsuarios(Pageable pageable);
}
