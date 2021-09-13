Feature: Subscribe and check overall

  Scenario: happy path
    Given Created a queue 'dummy' and subscribed as 'follower'
    When send a message
    Then receive a message
  Scenario: send 10 messages and check ordering
    Given Created a queue 'multi-dummyV1' and subscribed as 'follower-dummy'
    When send messages in 'unique_messages_10_payload.txt'
    Then received messages in 'unique_messages_10_payload.txt'