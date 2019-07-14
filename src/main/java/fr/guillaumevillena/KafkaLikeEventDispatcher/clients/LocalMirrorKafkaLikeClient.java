package fr.guillaumevillena.KafkaLikeEventDispatcher.clients;


import fr.guillaumevillena.KafkaLikeEventDispatcher.broker.KafkaLikeEventStack;
import fr.guillaumevillena.KafkaLikeEventDispatcher.communications.ClientSocketThread;
import fr.guillaumevillena.KafkaLikeEventDispatcher.communications.MethodNames;
import fr.guillaumevillena.KafkaLikeEventDispatcher.communications.TCPInterInstancePacket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Spacial class that is used as connector between the remote client and the KafkaLikeEventStack class
 * It contains a link to the socket thread and send event and command over network. It is a Gateway for
 * the remote client that is local to the server application.
 */
public class LocalMirrorKafkaLikeClient extends AbstractKafkaLikeClient {

  private ClientSocketThread clientSocketThread;

  public LocalMirrorKafkaLikeClient(ClientSocketThread thread) {
    super();
    this.clientSocketThread = thread;
    clientSocketThread.send(new TCPInterInstancePacket(MethodNames.SET_UUID, new String[]{getUniqId()}));
  }

  /**
   * Method beeing called by the socket thread when it decode a packet from the remote client.
   * The packet contains the name of the method and arguments to pass. It uses Java relfexion to
   * find the desired method. (Might not be very safe)
   *
   * @param packet the packet with the request
   */
  public void processTCPInterInstancePacket(TCPInterInstancePacket packet) {


    try {

      // get the method name and call it with arguments given as parametter...
      for (Method m : this.getClass().getMethods()) {
        if (packet.getArguments() != null && m.getName().equals(packet.getMethodNameAsString()) && m.getParameterCount() == packet.getArguments().length) {
          System.out.println("MEthod : " + packet.getMethodNameAsString());
          m.invoke(this, packet.getArguments());
          break;
        } else if (packet.getArguments() == null && m.getName().equals(packet.getMethodNameAsString()) && m.getParameterCount() == 0) {
          m.invoke(this);
          break;
        }
      }

    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }


  }


  /**
   * This override the main method to send the command to the remote client over TCP
   *
   * @param integer the current offset
   * @param key     the topic the offset needs to be updated
   */
  @Override
  public void setOffset(Integer integer, String key) {
    super.setOffset(integer, key);

    clientSocketThread.send(new TCPInterInstancePacket(MethodNames.SET_OFFSET_METHOD, new Object[]{integer, key}));
  }

  /**
   * This override the main method to send the associated command to the remote client over network
   *
   * @param object The object that needs to be send to the final user
   * @param topic  the topic of the event
   */
  @Override
  public void fireCallback(Object object, String topic) {
    super.fireCallback(object, topic);
    clientSocketThread.send(new TCPInterInstancePacket(MethodNames.FIRE_CALLBACK, new Object[]{object, topic}));
  }

  /**
   * As this is a conenctor between the remote client and the server, this method is triggered by the remote client
   * so it calls the server via a method call
   */
  public void askForEvent() {
    askForEvent(0);
  }

  @Override
  public void askForEvent(long sleepTime) {
    KafkaLikeEventStack.askForEvent(this);
    try {
      Thread.sleep(sleepTime);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * As this is a conenctor between the remote client and the server, this method is triggered by the remote client
   * so it calls the server via a method call
   *
   * @param topic the topic name
   */
  @Override
  public void commit(String topic) {
    KafkaLikeEventStack.commit(this, topic);
  }

  /**
   * This is a relay for the remote sended call to the local internal version of the method
   *
   * @param topic3 the topic to listen on
   */
  @Override
  public void subscribe(String topic3) {
    KafkaLikeEventStack.subscribe(this, topic3);
  }

  /**
   * This is a relay for the remote client call. It call directly the KafkaLikeEventStack
   *
   * @param strings the topic to register on
   */
  @Override
  public void register(String[] strings) {
    KafkaLikeEventStack.register(this, strings);
  }

  /**
   * This is a simple relay to directly send the data from the remote client to the KafkaLikeEventStack
   *
   * @param o      the object to be added inside the event log
   * @param topic1 the topic name
   */
  @Override
  public void produceEvent(Object o, String topic1) {
    KafkaLikeEventStack.produce(o, topic1);
  }

}
