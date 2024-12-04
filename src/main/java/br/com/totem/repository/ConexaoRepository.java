package br.com.totem.repository;

import br.com.totem.model.Conexao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface ConexaoRepository extends MongoRepository<Conexao, String> {

    @Query("{ 'ultimaAtualizacao' : { $lt: ?0 }, 'conexao.status' : 'Online', 'ativo' : true }")
    List<Conexao> findAllAtivosComUltimaAtualizacaoAntesQueEstavaoOnline(Date dataLimite);

}
