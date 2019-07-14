package fr.guillaumevillena.KafkaLikeEventDispatcher.clients;

import fr.guillaumevillena.KafkaLikeEventDispatcher.listeners.KafkaLikeMultipleTopicEventListenner;
import fr.guillaumevillena.KafkaLikeEventDispatcher.listeners.KafkaLikeSingleTopicEventListenner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This is general implementation of a client for the event log server.
 */
public abstract class AbstractKafkaLikeClient implements Serializable {

  protected String uniqId;
  private Map<String, ArrayList<KafkaLikeMultipleTopicEventListenner>> perTopicListenners = new HashMap<>();
  private ArrayList<KafkaLikeMultipleTopicEventListenner> globalListerner = new ArrayList<>();
  private Map<String, Integer> perTopicOffsets = new HashMap<>();

  /**
   * The main constructor. It initialize the uniq Id of the client
   */
  public AbstractKafkaLikeClient() {
    this.uniqId = UUID.randomUUID().toString();
  }

  /**
   * This method is a relay to the askForEvent inside the KafkaLikeEventStack class
   */
  public abstract void askForEvent();

  public abstract void askForEvent(long sleepTime);


  /**
   * This method is a relay to the askForEvent inside the KafkaLikeEventStack class
   */
  public abstract void commit(String topic);


  /**
   * This method is a relay to the askForEvent inside the KafkaLikeEventStack class
   */
  public abstract void subscribe(String topic3);


  /**
   * This method is a relay to the askForEvent inside the KafkaLikeEventStack class
   */
  public abstract void register(String[] strings);


  /**
   * This method is a relay to the askForEvent inside the KafkaLikeEventStack class
   */
  public abstract void produceEvent(Object o, String topic1);


  /**
   * @return the uniq id of the client
   */
  public String getUniqId() {
    return uniqId;
  }

  /**
   * This function is called to update the local client offset.
   * Generally it is beeing called by the KafkaLikeEventStack
   *
   * @param integer the current offset
   * @param key     the topic the offset needs to be updated
   */
  public void setOffset(Integer integer, String key) {
    perTopicOffsets.put(key, integer);
    System.out.println("Commit ! on " + key + " set to " + integer);
  }

  /**
   * @param key topic name
   * @return the offset for a given topic
   */
  public Integer getCurrentOfsset(String key) {
    if (!perTopicOffsets.containsKey(key))
      perTopicOffsets.put(key, 0);

    return perTopicOffsets.get(key);
  }

  /**
   * Add a callback for the specified topic. You will get notification for a single topic
   *
   * @param topic     the topic name
   * @param listenner the listenner instance
   */
  public void addEventCallback(String topic, KafkaLikeSingleTopicEventListenner listenner) {
    listenner.setTopicName(topic);

    if (!perTopicListenners.containsKey(topic))
      perTopicListenners.put(topic, new ArrayList<>());

    if (!perTopicListenners.get(topic).contains(listenner))
      perTopicListenners.get(topic).add(listenner);
  }

  /**
   * General event callback. The callback is called regardless of the Topic.
   * The topic name is provided with the object. It is great if you dont want
   * to have mulitple listener for each topic.
   *
   * @param listenner the listener instance
   */
  public void addEventCallback(KafkaLikeMultipleTopicEventListenner listenner) {

    globalListerner.add(listenner);

  }

  /**
   * Method called by the KafkaLikeEventStack to notify the client and trigger all event listeners
   *
   * @param object The object that needs to be send to the final user
   * @param topic  the topic of the event
   */
  public void fireCallback(Object object, String topic) {

    for (KafkaLikeMultipleTopicEventListenner l : globalListerner)
      l.onEventReceived(object, topic);

    if (perTopicListenners.containsKey(topic)) {
      for (KafkaLikeMultipleTopicEventListenner l : perTopicListenners.get(topic)) {
        l.onEventReceived(object, topic);
      }
    }

  }


}
