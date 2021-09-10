Feature: Subscribe

  Scenario: happy path
    Given Created a queue 'dummy' and subscribed as 'follower'
    When send a message
    Then receive a message