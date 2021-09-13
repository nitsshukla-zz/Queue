package com.navi.app.repo;

import com.navi.app.model.Subscriber;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public
interface SubscriberRepo extends CrudRepository<Subscriber, Long> {
  Optional<Subscriber> findByName(String name);

  List<Subscriber> findByQueue_Name(String queueName);

  @Modifying
  @Query("update Subscriber set offset = :offset where id = :id")
  void setOffset(@Param(value = "id") Long id, @Param(value = "offset") Long offset);
}
