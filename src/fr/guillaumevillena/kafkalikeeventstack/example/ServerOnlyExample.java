package fr.guillaumevillena.kafkalikeeventstack.example;

import fr.guillaumevillena.kafkalikeeventstack.broker.KafkaLikeEventStack;

public class ServerOnlyExample {

  public static void main(String[] args) {
    KafkaLikeEventStack.startTCPServer(1532);

    while (true) {
    }
  }

}
