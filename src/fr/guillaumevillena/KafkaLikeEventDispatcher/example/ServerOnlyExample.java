package fr.guillaumevillena.KafkaLikeEventDispatcher.example;

import fr.guillaumevillena.KafkaLikeEventDispatcher.broker.KafkaLikeEventStack;

/**
 * App that only starts the EventStack server
 */
public class ServerOnlyExample {

  public static void main(String[] args) {
    KafkaLikeEventStack.startTCPServer(1532);

    while (true) {
    }
  }

}
