package fr.guillaumevillena.KafkaLikeEventDispatcher.broker;


import fr.guillaumevillena.KafkaLikeEventDispatcher.clients.AbstractKafkaLikeClient;
import fr.guillaumevillena.KafkaLikeEventDispatcher.clients.LocalMirrorKafkaLikeClient;
import fr.guillaumevillena.KafkaLikeEventDispatcher.communications.*;
import fr.guillaumevillena.KafkaLikeEventDispatcher.topic.KafkaLikeTopic;

import java.util.HashMap;
import java.util.Map;

public class KafkaLikeEventStack {

  private static Map<String, ClientHandle> clientOffsetStatus = new HashMap<>(); // One clientID can have multiple topics and each fr.guillaumevillena.KafkaLikeEventDispatcher.topic has an offset !
  private static Map<String, KafkaLikeTopic> topics = new HashMap<>();
  private static TCPServer tcpServer;
  private static Map<ClientSocketThread, ClientHandle> clientSocketThreads = new HashMap<>();

  public static Thread startTCPServer(int portNumber) {
    tcpServer = new TCPServer(portNumber);
    tcpServer.addClientConnectedListener(new ClientConnectedListener() {
      @Override
      public void onClientConnected(ClientSocketThread clientSocketThread) {
        if (!clientSocketThreads.containsKey(clientSocketThread)) {

          LocalMirrorKafkaLikeClient fakeClient = new LocalMirrorKafkaLikeClient(clientSocketThread);
          ClientHandle handle = new ClientHandle(fakeClient.getUniqId(), ClientType.REMOTE, fakeClient);

          clientSocketThreads.put(clientSocketThread, handle);

        }
      }
    });

    tcpServer.addClientDisconnectedListener(new ClientDisconnectedListener() {
      @Override
      public void onClientDisconnected(ClientSocketThread thread) {
        clientSocketThreads.remove(thread);
      }
    });

    tcpServer.addClientMessageListener(new ClientMessageListener() {
      @Override
      public void onMessageReceived(ClientSocketThread clientSocketThread, Object msg) {
        if (msg instanceof TCPInterInstancePacket) {

          if (!clientSocketThreads.containsKey(clientSocketThread)) {

            LocalMirrorKafkaLikeClient fakeClient = new LocalMirrorKafkaLikeClient(clientSocketThread);
            ClientHandle handle = new ClientHandle(fakeClient.getUniqId(), ClientType.REMOTE, fakeClient);

            clientSocketThreads.put(clientSocketThread, handle);

          }

          ((LocalMirrorKafkaLikeClient) (clientSocketThreads.get(clientSocketThread).getClientInstance())).processTCPInterInstancePacket((TCPInterInstancePacket) msg);
        }
      }
    });
    return tcpServer.start();
  }

  public static void stopTCPServer() {

  }

  public static void produce(Object e, String topicName) {

    if (!topics.containsKey(topicName)) {
      topics.put(topicName, new KafkaLikeTopic(topicName));
      System.out.println("Auto Created fr.guillaumevillena.KafkaLikeEventDispatcher.topic !");
    }

    topics.get(topicName).pushToLog(e);
    System.out.println("Added new event : " + e.toString());
  }

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


  public static void register(AbstractKafkaLikeClient kafkaLikeClient, String[] topicNames) {
    if (!clientOffsetStatus.containsKey(kafkaLikeClient.getUniqId())) {
      clientOffsetStatus.put(kafkaLikeClient.getUniqId(), new ClientHandle(kafkaLikeClient.getUniqId(), ClientType.LOCAL, kafkaLikeClient));
      for (String topic : topicNames) {
        clientOffsetStatus.get(kafkaLikeClient.getUniqId()).getClientStatus().put(topic, 0);
        if (!topics.containsKey(topic)) {
          topics.put(topic, new KafkaLikeTopic(topic));
          System.out.println("Auto Created fr.guillaumevillena.KafkaLikeEventDispatcher.topic !");
        }
      }
    }

    for (String t : topicNames) {
      kafkaLikeClient.setOffset(0, t);
    }
  }

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


  public static void askForEvent(AbstractKafkaLikeClient client) {
    if (!clientOffsetStatus.containsKey(client.getUniqId()))
      register(client, new String[]{});

    for (String key : clientOffsetStatus.get(client.getUniqId()).getClientStatus().keySet()) {
      KafkaLikeTopic topic = topics.get(key);

      if (client.getCurrentOfsset(key) > topic.getEventLog().size())
        client.setOffset(topic.getEventLog().size(), key);

      if (client.getCurrentOfsset(key) == clientOffsetStatus.get(client.getUniqId()).getClientStatus().get(key)) {
        //client.eventCallback(fifo.get(client.getCurrentOfsset()));
        if ((!topic.getEventLog().isEmpty()) && (client.getCurrentOfsset(key) < topic.getEventLog().size())) {
          clientOffsetStatus.get(client.getUniqId()).getClientStatus().put(key, client.getCurrentOfsset(key) + 1);
          client.fireCallback(topic.getEventLog().get(client.getCurrentOfsset(key)), key);
        }
      }
    }
  }

}
