package fr.guillaumevillena.KafkaLikeEventDispatcher.example;

import fr.guillaumevillena.KafkaLikeEventDispatcher.clients.RemoteKafkaLikeClient;

/**
 * Simple class to test and demonstrate how the systel works with a remote client
 */
public class SimpleRemoteApp {

  public static void main(String[] args) {

    RemoteKafkaLikeClient client = new RemoteKafkaLikeClient("localhost", 1532);
    client.register(new String[]{"MAIN", "SEC"});
    client.addEventCallback((data, topic) -> {
      System.out.println("topic = " + topic + " data " + data);
      client.commit(topic);
    });


    client.produceEvent("REvent1--", "MAIN");
    client.produceEvent("REvent2--", "SEC");
    client.produceEvent("REvent5--", "MAIN");
    client.produceEvent("REvent6--", "MAIN");
    client.produceEvent("REvent7--", "MAIN");
    client.produceEvent("REvent8--", "MAIN");


    while (true) {
      client.askForEvent();
    }

  }

}
