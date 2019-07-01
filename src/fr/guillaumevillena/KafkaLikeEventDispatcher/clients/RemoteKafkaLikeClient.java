package fr.guillaumevillena.KafkaLikeEventDispatcher.clients;


import fr.guillaumevillena.KafkaLikeEventDispatcher.communications.ClientMessageListener;
import fr.guillaumevillena.KafkaLikeEventDispatcher.communications.ClientSocketThread;
import fr.guillaumevillena.KafkaLikeEventDispatcher.communications.TCPClient;
import fr.guillaumevillena.KafkaLikeEventDispatcher.communications.TCPInterInstancePacket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static fr.guillaumevillena.KafkaLikeEventDispatcher.communications.MethodNames.*;

public class RemoteKafkaLikeClient extends AbstractKafkaLikeClient implements ClientMessageListener {

  private TCPClient client;

  public RemoteKafkaLikeClient(String host, int portNumber) {
    super();
    client = new TCPClient(host, portNumber);
    // try to connect to the server and return if not connected
    client.addMessageListener(this);
    client.start();
  }

  public void close() {
    client.sendMessage(new TCPInterInstancePacket(UNREGISTER_METHOD, new Object[]{getUniqId()}));
    client.disconnect();
  }

  @Override
  public void onMessageReceived(ClientSocketThread clientSocketThread, Object msg) {
    if (msg instanceof TCPInterInstancePacket) {

      try {

        // get the method name and call it with arguments given as parametter...
        for (Method m : RemoteKafkaLikeClient.class.getMethods()) {
          if (m.getName().equals(((TCPInterInstancePacket) msg).getMethodNameAsString())) {
            System.out.println(((TCPInterInstancePacket) msg).getMethodNameAsString());
            m.invoke(RemoteKafkaLikeClient.this, ((TCPInterInstancePacket) msg).getArguments());
            break;
          }
        }

      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void askForEvent() {
    client.sendMessage(new TCPInterInstancePacket(ASK_EVENT_METHOD));
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void commit(String topic) {
    client.sendMessage(new TCPInterInstancePacket(COMMIT_METHOD, new Object[]{topic}));
  }

  @Override
  public void subscribe(String topic3) {
    client.sendMessage(new TCPInterInstancePacket(SUBSCRIBE_METHOD, new Object[]{topic3}));
  }

  @Override
  public void produceEvent(Object o, String topic1) {
    client.sendMessage(new TCPInterInstancePacket(PUSH_EVENT_METHOD, new Object[]{o, topic1}));
  }

  @Override
  public void register(String[] strings) {
    client.sendMessage(new TCPInterInstancePacket(REGISTER_METHOD, new Object[]{strings}));
    for (String s : strings)
      subscribe(s);
  }


}
