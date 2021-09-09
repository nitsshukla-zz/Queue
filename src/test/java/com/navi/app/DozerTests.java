package com.navi.app;

import com.navi.app.dtos.CallbackDetails;
import com.navi.app.dtos.Message;
import com.navi.app.dtos.SubscribeRequest;
import com.navi.app.dtos.SubscriberInfo;
import com.navi.app.model.CallbackInfo;
import com.navi.app.model.QueuePayload;
import com.navi.app.model.Subscriber;
import org.dozer.DozerBeanMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DozerTests {
	@Autowired
	private DozerBeanMapper dozerBeanMapper;
	@Test
	void messageMapping() {
		QueuePayload queuePayload = new QueuePayload();
		queuePayload.setPayload("dasda");
		queuePayload.setId(12L);
    System.out.println(dozerBeanMapper.map(queuePayload, Message.class));
	}

	@Test
	void subscriberMapping() {
		CallbackDetails callbackDetails = CallbackDetails.builder().endpoint("endpoint").headers(Collections.singletonMap("dsa","dsa")).build();
		SubscriberInfo subscriberInfo = SubscriberInfo.builder().name("dsada").queueName("topic").callbackDetails(callbackDetails).build();
		Subscriber subscriber = dozerBeanMapper.map(subscriberInfo, Subscriber.class);

		assertEquals(callbackDetails.getEndpoint(), subscriber.getCallbackInfo().getEndpoint());
		assertEquals(callbackDetails.getHeaders(), subscriber.getCallbackInfo().getHeaders());
		assertEquals(subscriberInfo.getName(), subscriber.getName());
		assertEquals(subscriberInfo.getQueueName(), subscriber.getQueue().getName());
	}
	@Test
	void subscriberRequestMapping() {
		CallbackDetails callbackDetails = CallbackDetails.builder().endpoint("endpoint").headers(Collections.singletonMap("dsa","dsa")).build();
		SubscribeRequest subscribeRequest = SubscribeRequest.builder().name("dsada").queueName("topic").callbackDetails(callbackDetails).build();
		Subscriber subscriber = dozerBeanMapper.map(subscribeRequest, Subscriber.class);

		assertEquals(callbackDetails.getEndpoint(), subscriber.getCallbackInfo().getEndpoint());
		assertEquals(callbackDetails.getHeaders(), subscriber.getCallbackInfo().getHeaders());
		assertEquals(subscribeRequest.getName(), subscriber.getName());
		assertEquals(subscribeRequest.getQueueName(), subscriber.getQueue().getName());
	}


}
