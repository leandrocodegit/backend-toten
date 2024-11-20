package br.com.totem.repository;

import br.com.totem.model.Dashboard;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface DashBoardrepository extends MongoRepository<Dashboard, UUID> {

}
