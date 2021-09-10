package com.navi.app.repo;

import com.navi.app.model.OffsetSequenceNumber;
import com.navi.app.model.QueuePayload;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public
interface QueuePayloadOffsetRepo extends CrudRepository<OffsetSequenceNumber, Long> {
}
