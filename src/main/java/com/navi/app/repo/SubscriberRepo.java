package com.navi.app.repo;

import com.navi.app.model.Subscriber;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public
interface SubscriberRepo extends CrudRepository<Subscriber, Long> {

}
