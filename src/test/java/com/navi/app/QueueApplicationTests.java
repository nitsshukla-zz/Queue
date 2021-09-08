package com.navi.app;

import com.navi.app.dtos.Message;
import com.navi.app.model.QueuePayload;
import org.dozer.DozerBeanMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QueueApplicationTests {
	@Autowired
	private DozerBeanMapper dozerBeanMapper;
	@Test
	void contextLoads() {
		QueuePayload queuePayload = new QueuePayload();
		queuePayload.setPayload("dasda");
		queuePayload.setId(12L);
    System.out.println(dozerBeanMapper.map(queuePayload, Message.class));
	}

}
