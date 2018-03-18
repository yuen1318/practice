package io.toro.pairprogramming.repositories;


import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.toro.pairprogramming.models.Message;

@Repository
public interface MessageRepository extends CrudRepository<Message,Long>{

    List<Message> findAllMessageByProjectIdOrderByTimestampAsc( Long id );
}
