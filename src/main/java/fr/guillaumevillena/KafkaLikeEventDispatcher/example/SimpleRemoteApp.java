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


    client.produceEvent("MAIN", "REvent1--");
    client.produceEvent("SEC", "REvent2--");
    client.produceEvent("MAIN", "REvent5--");
    client.produceEvent("MAIN", "REvent6--");
    client.produceEvent("MAIN", "REvent7--");
    client.produceEvent("MAIN", "REvent8--");
    client.produceEvent(client.getUniqId(), "Event for self");


    while (true) {
      client.askForEvent(1000);
    }

  }

}
