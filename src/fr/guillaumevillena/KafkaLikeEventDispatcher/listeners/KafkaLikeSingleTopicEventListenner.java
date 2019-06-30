package fr.guillaumevillena.KafkaLikeEventDispatcher.listeners;

import fr.guillaumevillena.KafkaLikeEventDispatcher.clients.AbstractKafkaLikeClient;
import fr.guillaumevillena.KafkaLikeEventDispatcher.broker.KafkaLikeEventStack;

public abstract class KafkaLikeSingleTopicEventListenner implements KafkaLikeMultipleTopicEventListenner {

    private String topicName;
    private AbstractKafkaLikeClient client;

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setClient(AbstractKafkaLikeClient client) {
        this.client = client;
    }

    public void commit(){
        KafkaLikeEventStack.commit(client, topicName);
    }

    public void onEventReceived(Object data, String topicName){
        onEventReceived(data);
    }


    public abstract void onEventReceived(Object data);

}
