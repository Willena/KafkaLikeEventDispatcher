package fr.guillaumevillena.KafkaLikeEventDispatcher.clients;


import fr.guillaumevillena.KafkaLikeEventDispatcher.broker.KafkaLikeEventStack;

/**
 * A local client implementation.
 * This is meant to be used in the same application where the KafkaLikeEventStack is initialized
 * This is basically a simple relay to the KafkaLikeEventStack class
 */
public class LocalKafkaLikeClient extends AbstractKafkaLikeClient {

  public void askForEvent() {
    KafkaLikeEventStack.askForEvent(this);
  }

  public void commit(String topic) {
    KafkaLikeEventStack.commit(this, topic);
  }

  public void subscribe(String topic3) {
    KafkaLikeEventStack.subscribe(this, topic3);
  }

  public void register(String[] strings) {
    KafkaLikeEventStack.register(this, strings);
  }

  public void produceEvent(Object o, String topic1) {
    KafkaLikeEventStack.produce(o, topic1);
  }
}
