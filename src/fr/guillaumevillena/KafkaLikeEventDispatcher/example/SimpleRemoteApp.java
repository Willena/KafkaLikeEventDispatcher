package fr.guillaumevillena.KafkaLikeEventDispatcher.example;

import fr.guillaumevillena.KafkaLikeEventDispatcher.clients.RemoteKafkaLikeClient;
import fr.guillaumevillena.KafkaLikeEventDispatcher.listeners.KafkaLikeMultipleTopicEventListenner;

public class SimpleRemoteApp {

  public static void main(String[] args) {

    RemoteKafkaLikeClient client = new RemoteKafkaLikeClient("localhost", 1532);
    client.register(new String[]{"MAIN", "SEC"});
    client.addEventCallback(new KafkaLikeMultipleTopicEventListenner() {
      @Override
      public void onEventReceived(Object data, String topic) {
        System.out.println("topic = " + topic + " data " + data);
        client.commit(topic);
      }
    });



    client.produceEvent("REvent1--", "MAIN");
    client.produceEvent("REvent2--", "SEC");
    client.produceEvent("REvent5--", "MAIN");
    client.produceEvent("REvent6--", "MAIN");
    client.produceEvent("REvent7--", "MAIN");
    client.produceEvent("REvent8--", "MAIN");


    while (true){
      client.askForEvent();
    }

  }

}
