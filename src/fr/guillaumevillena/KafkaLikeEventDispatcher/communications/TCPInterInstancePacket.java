package fr.guillaumevillena.KafkaLikeEventDispatcher.communications;

import java.io.Serializable;
import java.util.UUID;

public class TCPInterInstancePacket implements Serializable {

  private final MethodNames method;
  private final Object[] args;
  private final String uuid;

  public TCPInterInstancePacket(MethodNames method, Object[] args) {
    uuid = UUID.randomUUID().toString();
    this.method = method;
    this.args = args;
  }

  public TCPInterInstancePacket(MethodNames method) {
    uuid = UUID.randomUUID().toString();
    this.method = method;
    this.args = null;
  }

  public MethodNames getMethodName() {
    return method;
  }

  public String getMethodNameAsString() {
    return method.toString();
  }

  public Object[] getArguments() {
    return args;
  }

  public String getUuid() {
    return uuid;
  }
}
