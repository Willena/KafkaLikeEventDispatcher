package fr.guillaumevillena.KafkaLikeEventDispatcher.listeners;

/**
 * Interface to implement the callback when an event is received
 * It gives any event in any topics
 */
public interface KafkaLikeMultipleTopicEventListenner {

  /**
   * Callback called when en event is available
   *
   * @param data  event data
   * @param topic topic name
   */
  void onEventReceived(Object data, String topic);

}

