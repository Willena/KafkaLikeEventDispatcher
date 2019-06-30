package fr.guillaumevillena.kafkalikeeventstack.Clients;


import fr.guillaumevillena.kafkalikeeventstack.broker.KafkaLikeEventStack;
import fr.guillaumevillena.kafkalikeeventstack.communications.ClientSocketThread;
import fr.guillaumevillena.kafkalikeeventstack.communications.MethodNames;
import fr.guillaumevillena.kafkalikeeventstack.communications.TCPInterInstancePacket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LocalMirrorKafkaLikeClient extends AbstractKafkaLikeClient {

  private ClientSocketThread clientSocketThread;

  public LocalMirrorKafkaLikeClient(ClientSocketThread thread) {
    this.clientSocketThread = thread;
  }

  public void processTCPInterInstancePacket(TCPInterInstancePacket packet) {


    try {

      // get the method name and call it with arguments given as parametter...
      for (Method m : this.getClass().getMethods()) {
        if (m.getName().equals(packet.getMethodNameAsString())) {
          System.out.println("MEthod : " + packet.getMethodNameAsString());
          if (packet.getArguments() != null)
            m.invoke(this, packet.getArguments());
          else
            m.invoke(this);
          break;
        }
      }

    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }


  }

  @Override
  public void setOffset(Integer integer, String key) {
    super.setOffset(integer, key);

    clientSocketThread.send(new TCPInterInstancePacket(MethodNames.SET_OFFSET_METHOD, new Object[]{integer, key}));
  }

  @Override
  public void fireCallback(Object object, String topic) {
    super.fireCallback(object, topic);
    clientSocketThread.send(new TCPInterInstancePacket(MethodNames.FIRE_CALLBACK, new Object[]{object, topic}));
  }

  @Override
  public void askForEvent() {
    KafkaLikeEventStack.askForEvent(this);
  }

  @Override
  public void commit(String topic) {
    KafkaLikeEventStack.commit(this, topic);
  }

  @Override
  public void subscribe(String topic3) {
    KafkaLikeEventStack.subscribe(this, topic3);
  }

  @Override
  public void register(String[] strings) {
    KafkaLikeEventStack.register(this, strings);
  }

  @Override
  public void produceEvent(Object o, String topic1) {
    KafkaLikeEventStack.produce(o, topic1);
  }


}
