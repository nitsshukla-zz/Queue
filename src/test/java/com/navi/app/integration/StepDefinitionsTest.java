package com.navi.app.integration;

import com.navi.app.QueueApplication;
import com.navi.app.dtos.Message;
import com.navi.app.dtos.QueueInfo;
import com.navi.app.dtos.SubscribeRequest;
import com.navi.app.dtos.SubscriberInfo;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.awaitility.core.ConditionFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CucumberContextConfiguration
@SpringBootTest(classes = QueueApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ContextConfiguration(QueueApplication.class)
public class StepDefinitionsTest extends IntegrationTest {

  private static final String JSON = "{\n" +
      "    \"message\": \"{\\\"nam\\\":\\\"aparajaita\\\"}\",\n" +
      "    \"requestId\": \"ee312312\"\n" +
      "}";
  public static final ConditionFactory AWAIT_15_SECS = await().atMost(15L, TimeUnit.SECONDS);
  private String queueName, subscriberName;

  @Given("Created a queue {string} and subscribed as {string}")
  public void created_a_queue_dummy_and_subscribed_as_follower(String queueName, String subscriberName) {
    ResponseEntity<QueueInfo> queueInfoResponseEntity =
      restTemplate.postForEntity(getUrl("/v1/admin/queue/" + queueName), null, QueueInfo.class);
    System.out.println("Queue creation response: " + queueInfoResponseEntity.getBody());
    SubscribeRequest subscribeRequest = SubscribeRequest.builder().queueName(queueName).name(subscriberName)
        .callbackDetails(getCallbackDetails())
        .build();
    ResponseEntity<SubscriberInfo> subscriberInfoResponseEntity =
        restTemplate.postForEntity(getUrl("/v1/subscribe"), subscribeRequest, SubscriberInfo.class);
    System.out.println("Subscriber creation response: " + subscriberInfoResponseEntity.getBody());
    this.queueName = queueName;
    this.subscriberName = subscriberName;
  }

  @When("send a message")
  public void send_a_message() {
    Message message = Message.builder().message(JSON).requestId(UUID.randomUUID().toString()).build();
    ResponseEntity<String> messageResponseEntity = restTemplate.postForEntity(getUrl("/v1/queue/"+queueName), message, String.class);
    System.out.println(messageResponseEntity.getBody());
  }
  @Then("receive a message")
  public void receive_a_message() {
    AWAIT_15_SECS
        .until(() -> payloadReceived(JSON, this.subscriberName));
    assertTrue(payloadReceived(JSON, this.subscriberName));
    Set<Map.Entry> entries = new HashSet<>(HEADERS.entrySet());
    Set<Map.Entry> headers = new HashSet<>(dummyController.headersReceived.entrySet());
    entries.removeAll(headers);
    assertTrue(entries.isEmpty());
  }

  private Boolean payloadReceived(String data, String subscriberName) {
    return dummyController.bodyMessagesReceived
        .stream()
        .filter(message -> message.getPayload().equals(data))
        .anyMatch(message -> message.getSubscriberName().equals(subscriberName));
  }
}