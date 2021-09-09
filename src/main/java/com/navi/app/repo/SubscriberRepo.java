package com.navi.app.repo;

import com.navi.app.dtos.SubscriberInfo;
import com.navi.app.model.Subscriber;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public
interface SubscriberRepo extends CrudRepository<Subscriber, Long> {
  Optional<Subscriber> findByName(String name);
}
