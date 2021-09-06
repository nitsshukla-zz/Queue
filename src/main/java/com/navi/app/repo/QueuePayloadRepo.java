package com.navi.app.repo;

import com.navi.app.model.QueuePayload;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public
interface QueuePayloadRepo extends CrudRepository<QueuePayload, Long> {
  Optional<QueuePayload> findByOffset(long offset);
}
