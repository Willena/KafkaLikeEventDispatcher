package fr.guillaumevillena.KafkaLikeEventDispatcher.example;

import fr.guillaumevillena.KafkaLikeEventDispatcher.broker.KafkaLikeEventStack;
import fr.guillaumevillena.KafkaLikeEventDispatcher.clients.LocalKafkaLikeClient;

/**
 * Simple class that shows how the system works with a local client.
 */
public class SimpleAppLocal {

  public static void main(String[] args) {

    KafkaLikeEventStack.startTCPServer(1532);

    LocalKafkaLikeClient client = new LocalKafkaLikeClient();
    LocalKafkaLikeClient client2 = new LocalKafkaLikeClient();

    client.register(new String[]{"MAIN", "SEC"});
    client.addEventCallback((data, topic) -> {
      System.out.println("CLIENT : topic = " + topic + " data " + data);
      client.commit(topic);
    });

    client2.register(new String[]{"MAIN", "SEC"});
    client2.addEventCallback((data, topic) -> {
      System.out.println("CLIENT 2 : topic = " + topic + " data " + data);
      client2.commit(topic);
    });


    client.produceEvent("LEvent1", "MAIN");
    client.produceEvent("LEvent2", "SEC");
    client.produceEvent("LEvent5", "MAIN");
    client.produceEvent("LEvent6", "MAIN");
    client.produceEvent("LEvent7", "MAIN");
    client.produceEvent("LEvent8", "MAIN");


    while (true) {
      client.askForEvent();
      client2.askForEvent();
    }


  }

}