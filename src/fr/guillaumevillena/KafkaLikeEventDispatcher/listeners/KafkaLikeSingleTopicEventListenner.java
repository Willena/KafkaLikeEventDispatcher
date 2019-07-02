package fr.guillaumevillena.KafkaLikeEventDispatcher.listeners;

/**
 * A class representing a callback for a specific topic
 * It receives all event on all topics and filter them given a single endpoint
 */
public abstract class KafkaLikeSingleTopicEventListenner implements KafkaLikeMultipleTopicEventListenner {

  private String topicName;

  /**
   * Method that set the topic name
   *
   * @param topicName Set the topic name used for filtering
   */
  public void setTopicName(String topicName) {
    this.topicName = topicName;
  }

  /**
   * The original callback function. It filters on the topic name previously set.
   *
   * @param data      event data
   * @param topicName the topic name
   */
  public void onEventReceived(Object data, String topicName) {
    if (topicName.equals(this.topicName))
      onEventReceived(data);
  }


  /**
   * callback that should be implemented by the client
   *
   * @param data the event
   */
  public abstract void onEventReceived(Object data);

}
