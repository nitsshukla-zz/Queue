package com.navi.app.integration;

import com.google.gson.Gson;
import com.navi.app.QueueApplication;
import com.navi.app.dtos.Message;
import com.navi.app.dtos.QueueInfo;
import com.navi.app.dtos.SubscribeRequest;
import com.navi.app.dtos.SubscriberInfo;
import com.navi.app.dtos.SubscriberPayload;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.awaitility.core.ConditionFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CucumberContextConfiguration
@SpringBootTest(classes = QueueApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StepDefinitionsTest extends IntegrationTest {

  private static final String DEFAULT_MESSAGE = "{\n" +
      "    \"message\": \"{\\\"nam\\\":\\\"aparajaita\\\"}\",\n" +
      "    \"requestId\": \"ee312312\"\n" +
      "}";
  public static final ConditionFactory AWAIT_15_SECS = await().atMost(15L, TimeUnit.SECONDS);
  public static final ConditionFactory AWAIT_30_SECS = await().atMost(30L, TimeUnit.SECONDS);
  public static final Gson GSON = new Gson();
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
    send_message(DEFAULT_MESSAGE);
  }

  private void send_message(String data) {
    Message message = Message.builder().message(data).requestId(UUID.randomUUID().toString()).build();
    ResponseEntity<String> messageResponseEntity = restTemplate.postForEntity(getUrl("/v1/queue/"+queueName), message, String.class);
    assertEquals(HttpStatus.OK, messageResponseEntity.getStatusCode());
    Message payload = GSON.fromJson(messageResponseEntity.getBody(), Message.class);
    assertEquals(payload.getMessage(), data);

  }

  @Then("receive a message")
  public void receive_a_message() {
    AWAIT_15_SECS
        .until(() -> dataReceievedCheck(DEFAULT_MESSAGE, this.subscriberName));
    assertTrue(dataReceievedCheck(DEFAULT_MESSAGE, this.subscriberName));
    Set<Map.Entry> entries = new HashSet<>(HEADERS.entrySet());
    Set<Map.Entry> headers = new HashSet<>(dummyController.headersReceived.entrySet());
    entries.removeAll(headers);
    assertTrue(entries.isEmpty());
  }
  private Boolean dataReceievedCheck(String data, String subscriberName) {
    return dataReceievedCheck(Collections.singletonList(data), subscriberName);
  }
  private Boolean dataReceievedCheck(List<String> data, String subscriberName) {
    Set<String> dataSet = new HashSet<>((data));
    List<String> messages = dummyController.bodyMessagesReceived
        .stream()
        .filter(message -> dataSet.contains(message.getPayload()))
        .filter(message -> message.getSubscriberName().equals(subscriberName))
        .map(SubscriberPayload::getPayload)
        .collect(Collectors.toList());
    if (data.size() != messages.size())return false;
    Iterator<String> expectedMessageIterator = messages.iterator();
    Iterator<String> messageIterator = messages.iterator();
    while (expectedMessageIterator.hasNext() && messageIterator.hasNext()) {
      if (!expectedMessageIterator.next().equals(messageIterator.next())) {
        System.out.println("Expected " + expectedMessageIterator.next() + " but got: " + messageIterator.next());
        return false;
      }
    }
    return true;
  }

  @Then("received messages in {string}")
  public void receivedMessagesInMessages__payloadTxt(String fileName) {
    Scanner scanner = new Scanner(Objects.requireNonNull(StepDefinitionsTest.class.getClassLoader().getResourceAsStream(fileName)));
    List<String> expectedMessages = new LinkedList<>();
    while (scanner.hasNextLine())
      expectedMessages.add(scanner.nextLine());
    AWAIT_30_SECS
        .pollInterval(1L, TimeUnit.SECONDS)
        .until(() -> dataReceievedCheck(expectedMessages, subscriberName));
    assertTrue(dataReceievedCheck(expectedMessages, subscriberName));
  }

  @When("send messages in {string}")
  public void sendMessagesInMessages__payloadTxt(String fileName) {
    Scanner scanner = new Scanner(Objects.requireNonNull(StepDefinitionsTest.class.getClassLoader().getResourceAsStream(fileName)));
    while (scanner.hasNextLine())
      send_message(scanner.nextLine());
  }
}