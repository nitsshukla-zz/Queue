package com.navi.app.repo;

import com.navi.app.QueueApplication;
import com.navi.app.model.QueueDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public
interface QueueRepo extends CrudRepository<QueueDAO, Long> {
  Optional<QueueDAO> findByName(String name);
}
