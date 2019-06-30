package fr.guillaumevillena.KafkaLikeEventDispatcher.topic;

import java.util.ArrayList;

public class KafkaLikeTopic {
    private String name;
    private ArrayList<Object> eventLog;


    public KafkaLikeTopic(String topicName) {
        name = topicName;
        eventLog = new ArrayList<>(100);
    }

    public Integer pushToLog(Object e) {
        eventLog.add(e);
        return eventLog.size() - 1;
    }

    public Integer pushTolog(Object e, Integer pos) {
        eventLog.add(pos, e);
        return pos;
    }

    public ArrayList<Object> getEventLog() {
        return eventLog;
    }
}
