package fr.guillaumevillena.kafkalikeeventstack.Clients;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.guillaumevillena.kafkalikeeventstack.listeners.KafkaLikeMultipleTopicEventListenner;
import fr.guillaumevillena.kafkalikeeventstack.listeners.KafkaLikeSingleTopicEventListenner;

public abstract class AbstractKafkaLikeClient implements Serializable {

  private final String uniqId;
  private Map<String, ArrayList<KafkaLikeMultipleTopicEventListenner>> perTopicListenners = new HashMap<>();
  private ArrayList<KafkaLikeMultipleTopicEventListenner> globalListerner = new ArrayList<>();
  private Map<String, Integer> perTopicOffsets = new HashMap<>();

  public AbstractKafkaLikeClient() {
    this.uniqId = UUID.randomUUID().toString();
  }

  public abstract void askForEvent();

  public abstract void commit(String topic);

  public abstract void subscribe(String topic3);

  public abstract void register(String[] strings);

  public abstract void produceEvent(Object o, String topic1);

  public String getUniqId() {
    return uniqId;
  }

  public void setOffset(Integer integer, String key) {
    perTopicOffsets.put(key, integer);
    System.out.println("Commit ! on " + key + " set to " + integer);
  }

  public Integer getCurrentOfsset(String key) {
    if (!perTopicOffsets.containsKey(key))
      perTopicOffsets.put(key, 0);

    return perTopicOffsets.get(key);
  }

  public void addEventCallback(String topic, KafkaLikeSingleTopicEventListenner listenner) {
    listenner.setTopicName(topic);
    listenner.setClient(this);

    if (!perTopicListenners.containsKey(topic))
      perTopicListenners.put(topic, new ArrayList<KafkaLikeMultipleTopicEventListenner>());

    if (!perTopicListenners.get(topic).contains(listenner))
      perTopicListenners.get(topic).add(listenner);
  }

  public void addEventCallback(KafkaLikeMultipleTopicEventListenner listenner) {

    globalListerner.add(listenner);

  }

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
