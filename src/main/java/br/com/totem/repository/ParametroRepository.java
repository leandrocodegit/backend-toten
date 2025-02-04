package br.com.totem.repository;

import br.com.totem.model.Parametro;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ParametroRepository extends MongoRepository<Parametro, UUID> {}
