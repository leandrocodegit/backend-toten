package br.com.totem.repository;

import br.com.totem.model.Operacao;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OperacaoRepository extends MongoRepository<Operacao, String> {}
