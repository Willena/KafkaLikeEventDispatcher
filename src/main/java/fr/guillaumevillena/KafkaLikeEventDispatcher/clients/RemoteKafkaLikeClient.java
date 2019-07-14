package fr.guillaumevillena.KafkaLikeEventDispatcher.clients;


import fr.guillaumevillena.KafkaLikeEventDispatcher.communications.ClientMessageListener;
import fr.guillaumevillena.KafkaLikeEventDispatcher.communications.ClientSocketThread;
import fr.guillaumevillena.KafkaLikeEventDispatcher.communications.TCPClient;
import fr.guillaumevillena.KafkaLikeEventDispatcher.communications.TCPInterInstancePacket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static fr.guillaumevillena.KafkaLikeEventDispatcher.communications.MethodNames.*;

/**
 * The remote version of the Client.
 * It connects over TCP to the server.
 * Message sent by the remote client are received by the LocalMirrorKafkaLikeClient that translate the
 * TCP command to function call.
 */
public class RemoteKafkaLikeClient extends AbstractKafkaLikeClient implements ClientMessageListener {

  private TCPClient client;

  /**
   * Initialize and connect to a TCP server with the provided informations
   *
   * @param host       hostname to connect to
   * @param portNumber the port number of the TCPServer
   */
  public RemoteKafkaLikeClient(String host, int portNumber) {
    super();
    client = new TCPClient(host, portNumber);
    // try to connect to the server and return if not connected
    client.addMessageListener(this);
    client.start();
  }

  /**
   * Closes the connection with the server and clear all threads
   */
  public void close() {
    client.sendMessage(new TCPInterInstancePacket(UNREGISTER_METHOD, new Object[]{getUniqId()}));
    client.disconnect();
  }

  public void setUniqID(String uuid) {
    uniqId = uuid;
  }

  /**
   * Callback triggered when a message from the server is received.
   * The message consists in the method name to call with th required parameter.
   * The method is found using Java Reflexion API
   *
   * @param clientSocketThread the current socket thread
   * @param msg                the message to be analysed
   */
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

      } catch (IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Call the askforEvent method in the KafkaLikeEventStack remotely
   */
  @Override
  public void askForEvent() {
    askForEvent(80);
  }

  @Override
  public void askForEvent(long sleepTime) {
    client.sendMessage(new TCPInterInstancePacket(ASK_EVENT_METHOD));
    try {
      Thread.sleep(sleepTime);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Call the commit method in the KafkaLikeEventStack remotely
   */
  @Override
  public void commit(String topic) {
    client.sendMessage(new TCPInterInstancePacket(COMMIT_METHOD, new Object[]{topic}));
  }

  /**
   * Call the subscribe method in the KafkaLikeEventStack remotely
   */
  @Override
  public void subscribe(String topic3) {
    client.sendMessage(new TCPInterInstancePacket(SUBSCRIBE_METHOD, new Object[]{topic3}));
  }

  /**
   * Call the produce method in the KafkaLikeEventStack remotely
   */
  @Override
  public void produceEvent(Object o, String topic1) {
    client.sendMessage(new TCPInterInstancePacket(PUSH_EVENT_METHOD, new Object[]{o, topic1}));
  }

  /**
   * Call the register method in the KafkaLikeEventStack remotely
   */
  @Override
  public void register(String[] strings) {
    client.sendMessage(new TCPInterInstancePacket(REGISTER_METHOD, new Object[]{strings}));
    for (String s : strings)
      subscribe(s);
  }


}
