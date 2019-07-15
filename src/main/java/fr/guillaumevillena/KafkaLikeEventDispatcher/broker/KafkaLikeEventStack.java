package fr.guillaumevillena.KafkaLikeEventDispatcher.broker;


import fr.guillaumevillena.KafkaLikeEventDispatcher.clients.AbstractKafkaLikeClient;
import fr.guillaumevillena.KafkaLikeEventDispatcher.clients.LocalMirrorKafkaLikeClient;
import fr.guillaumevillena.KafkaLikeEventDispatcher.communications.ClientSocketThread;
import fr.guillaumevillena.KafkaLikeEventDispatcher.communications.TCPInterInstancePacket;
import fr.guillaumevillena.KafkaLikeEventDispatcher.communications.TCPServer;
import fr.guillaumevillena.KafkaLikeEventDispatcher.topic.KafkaLikeTopic;

import java.util.HashMap;
import java.util.Map;

/**
 * The brain of the Event dispatcher
 * This class store every message and client "instance" connected to it
 * It also handle the TCP server if activated
 * All methods are static
 */
public class KafkaLikeEventStack {

  public static final String BROKER_PREFIX = "__BROKER_";
  public static final String NEW_CLIENTS_TOPIC = BROKER_PREFIX + "NEW_CLIENTS";
  public static final String DISCONECT_CLIENTS_TOPIC = BROKER_PREFIX + "DISCONECT_CLIENTS";
  private static Map<String, ClientHandle> clientOffsetStatus = new HashMap<>(); // One clientID can have multiple topics and each fr.guillaumevillena.KafkaLikeEventDispatcher.topic has an offset !
  private static Map<String, KafkaLikeTopic> topics = new HashMap<>();
  private static TCPServer tcpServer;
  private static Map<ClientSocketThread, ClientHandle> clientSocketThreads = new HashMap<>();

  /**
   * This function initalize the TCP server and connects a few callbacks to get message received
   *
   * @param portNumber port the server will listen on
   * @return a instance to the TCPServer thread started
   */
  public static Thread startTCPServer(int portNumber) {
    tcpServer = new TCPServer(portNumber);
    tcpServer.addClientConnectedListener(clientSocketThread -> {
      if (!clientSocketThreads.containsKey(clientSocketThread)) {

        LocalMirrorKafkaLikeClient fakeClient = new LocalMirrorKafkaLikeClient(clientSocketThread);
        ClientHandle handle = new ClientHandle(fakeClient.getUniqId(), ClientType.REMOTE, fakeClient);
        clientSocketThreads.put(clientSocketThread, handle);

        //Inform listeners about the new client on the dedicated topic :)
        produce(handle.getUuid(), NEW_CLIENTS_TOPIC);

      }
    });

    tcpServer.addClientDisconnectedListener(thread -> {
      String uuid = clientSocketThreads.get(thread).getUuid();
      clientSocketThreads.remove(thread);
      produce(uuid, DISCONECT_CLIENTS_TOPIC);
    });

    tcpServer.addClientMessageListener((clientSocketThread, msg) -> {
      if (msg instanceof TCPInterInstancePacket) {

        if (!clientSocketThreads.containsKey(clientSocketThread)) {

          LocalMirrorKafkaLikeClient fakeClient = new LocalMirrorKafkaLikeClient(clientSocketThread);
          ClientHandle handle = new ClientHandle(fakeClient.getUniqId(), ClientType.REMOTE, fakeClient);

          clientSocketThreads.put(clientSocketThread, handle);

        }

        ((LocalMirrorKafkaLikeClient) (clientSocketThreads.get(clientSocketThread).getClientInstance())).processTCPInterInstancePacket((TCPInterInstancePacket) msg);
      }
    });
    return tcpServer.start();
  }

  /**
   * Function called to stop the TCP Server
   */
  public static void stopTCPServer() {

  }

  /**
   * A Simple function to produce and add an event to the log of a KafkaLikeTopic
   *
   * @param e         the Serializable object to be added in the Event log
   * @param topicName the name of the topic the event should be added. It will be created automatically if not already existing
   */
  public static void produce(Object e, String topicName) {

    if (!topics.containsKey(topicName)) {
      topics.put(topicName, new KafkaLikeTopic(topicName));
      System.out.println("Auto Created fr.guillaumevillena.KafkaLikeEventDispatcher.topic !");
    }

    topics.get(topicName).pushToLog(e);
    System.out.println("Added new event : " + e.toString());
  }

  /**
   * If the client wants to receive the next message in the queue, it needs to call the commit function
   * after reading the initial message. This function will update the current client offset that will then provide
   * the ability to send the next event to the client
   *
   * @param client    the client commiting
   * @param topicName the topic to commit into
   */
  public static void commit(AbstractKafkaLikeClient client, String topicName) {

    if (!topics.containsKey(topicName))
      return;

    if (!clientOffsetStatus.containsKey(client.getUniqId()))
      return;

    if (!clientOffsetStatus.get(client.getUniqId()).getClientStatus().containsKey(topicName))
      return;


    KafkaLikeTopic topic = topics.get(topicName);

    if (client.getCurrentOfsset(topicName) <= clientOffsetStatus.get(client.getUniqId()).getClientStatus().get(topicName)) {
      client.setOffset(client.getCurrentOfsset(topicName) + 1, topicName);
    }
  }


  /**
   * This method should be called to register the client in the system so that everything get initialized internally.
   * At this point you can already subscribe to topics
   *
   * @param kafkaLikeClient the client that is registering
   * @param topicNames      a list of topic names
   */
  public static void register(AbstractKafkaLikeClient kafkaLikeClient, String[] topicNames) {
    if (!clientOffsetStatus.containsKey(kafkaLikeClient.getUniqId())) {
      clientOffsetStatus.put(kafkaLikeClient.getUniqId(), new ClientHandle(kafkaLikeClient.getUniqId(), ClientType.LOCAL, kafkaLikeClient));

      //Register on topics
      for (String topic : topicNames) {
        subscribe(kafkaLikeClient, topic);
      }
      //Register the client only topic, automatically listened by the client.
      subscribe(kafkaLikeClient, kafkaLikeClient.getUniqId());
    }
  }

  /**
   * Ask the Event log manager to also send event for a specified topic
   *
   * @param client the client subscribing to a topic
   * @param name   the topic name to be subscribed
   */
  public static void subscribe(AbstractKafkaLikeClient client, String name) {
    if (!topics.containsKey(name)) {
      topics.put(name, new KafkaLikeTopic(name));
      System.out.println("Auto Created fr.guillaumevillena.KafkaLikeEventDispatcher.topic !");
    }

    if (!clientOffsetStatus.containsKey(client.getUniqId()))
      register(client, new String[]{name});
    else {
      clientOffsetStatus.get(client.getUniqId()).getClientStatus().put(name, 0);
      client.setOffset(0, name);
    }
  }


  /**
   * The method needs to be pooled at a regular basis if you want to receive events and message from the log
   *
   * @param client the client requesting events
   */
  public static void askForEvent(AbstractKafkaLikeClient client) {
    if (!clientOffsetStatus.containsKey(client.getUniqId()))
      register(client, new String[]{});

    for (String key : clientOffsetStatus.get(client.getUniqId()).getClientStatus().keySet()) {
      KafkaLikeTopic topic = topics.get(key);

      if (client.getCurrentOfsset(key) > topic.getEventLog().size())
        client.setOffset(topic.getEventLog().size(), key);

      if (client.getCurrentOfsset(key).equals(clientOffsetStatus.get(client.getUniqId()).getClientStatus().get(key))) {
        //client.eventCallback(fifo.get(client.getCurrentOfsset()));
        if ((!topic.getEventLog().isEmpty()) && (client.getCurrentOfsset(key) < topic.getEventLog().size())) {
          clientOffsetStatus.get(client.getUniqId()).getClientStatus().put(key, client.getCurrentOfsset(key) + 1);
          client.fireCallback(topic.getEventLog().get(client.getCurrentOfsset(key)), key);
        }
      }
    }
  }

}
