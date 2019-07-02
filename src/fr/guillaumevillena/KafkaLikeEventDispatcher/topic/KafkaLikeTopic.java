package fr.guillaumevillena.KafkaLikeEventDispatcher.topic;

import java.util.ArrayList;

/**
 * Class tha represent a Topic
 */
public class KafkaLikeTopic {
    private String name;
    private ArrayList<Object> eventLog;


    /**
     * Create a new topic
     *
     * @param topicName the topic name
     */
    public KafkaLikeTopic(String topicName) {
        name = topicName;
        eventLog = new ArrayList<>(100);
    }

    /**
     * Add a new object to the event log
     *
     * @param e object to add to the log
     * @return the current offset of the object
     */
    public Integer pushToLog(Object e) {
        eventLog.add(e);
        return eventLog.size() - 1;
    }

    /**
     * Add an object at a specific position in the log
     *
     * @param e   the object to add
     * @param pos the position in the log
     * @return the current event position
     */
    public Integer pushTolog(Object e, Integer pos) {
        eventLog.add(pos, e);
        return pos;
    }

    /**
     * Get the event log of the topic
     * @return the event log instance
     */
    public ArrayList<Object> getEventLog() {
        return eventLog;
    }
}
