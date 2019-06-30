package fr.guillaumevillena.kafkalikeeventstack.listeners;

public interface KafkaLikeMultipleTopicEventListenner {

    void onEventReceived(Object data, String topic);

}

