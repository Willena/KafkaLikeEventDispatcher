package fr.guillaumevillena.kafkalikeeventstack.Clients;


import fr.guillaumevillena.kafkalikeeventstack.communications.ClientMessageListener;
import fr.guillaumevillena.kafkalikeeventstack.communications.ClientSocketThread;
import fr.guillaumevillena.kafkalikeeventstack.communications.TCPClient;
import fr.guillaumevillena.kafkalikeeventstack.communications.TCPInterInstancePacket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import static fr.guillaumevillena.kafkalikeeventstack.communications.MethodNames.*;

public class KafkaLikeRemoteClient extends AbstractKafkaLikeClient implements ClientMessageListener {

  private TCPClient client;

  public KafkaLikeRemoteClient(String host, int portNumber) {
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
        for (Method m : KafkaLikeRemoteClient.class.getMethods()) {
          if (m.getName().equals(((TCPInterInstancePacket) msg).getMethodNameAsString())) {
            System.out.println(((TCPInterInstancePacket) msg).getMethodNameAsString());
            m.invoke(KafkaLikeRemoteClient.this, ((TCPInterInstancePacket) msg).getArguments());
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
