package fr.guillaumevillena.KafkaLikeEventDispatcher.listeners;

public interface KafkaLikeMultipleTopicEventListenner {

    void onEventReceived(Object data, String topic);

}

