package br.com.totem.repository;

import br.com.totem.model.Dispositivo;
import br.com.totem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository  extends MongoRepository<User, UUID> {

    Optional<User> findByEmailAndStatus(String email, Boolean status);
    Optional<User> findByEmail(String email);
    long countByStatus(Boolean status);

    @Query("{" +
            "   $or: [" +
            "       { 'email': { $regex: ?0, $options: 'i' } }," +
            "       { 'nome': { $regex: ?0, $options: 'i' } }" +
            "   ]," +
//            "   'ativo': true" +
            "}")
    Page<User> findByNomeAndEmailContaining(String texto, Pageable pageable);
}
