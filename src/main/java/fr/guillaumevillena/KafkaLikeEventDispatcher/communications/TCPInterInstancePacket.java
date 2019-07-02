package fr.guillaumevillena.KafkaLikeEventDispatcher.communications;

import java.io.Serializable;
import java.util.UUID;

/**
 * The object used to call remote methods
 * It contains the name and arguement that will be executed on the Client object
 * This is a readonly class
 */
public class TCPInterInstancePacket implements Serializable {

  private final MethodNames method;
  private final Object[] args;
  private final String uuid;

  /**
   * Create the Packet with the methid to call and the arguments
   *
   * @param method a registered method name
   * @param args   a list of arguments to pass to the remote method
   */
  public TCPInterInstancePacket(MethodNames method, Object[] args) {
    uuid = UUID.randomUUID().toString();
    this.method = method;
    this.args = args;
  }

  /**
   * Alternative constructor is not argument are necessary
   *
   * @param method a registered method name
   */
  public TCPInterInstancePacket(MethodNames method) {
    uuid = UUID.randomUUID().toString();
    this.method = method;
    this.args = null;
  }

  /**
   * @return the method name Object
   */
  public MethodNames getMethodName() {
    return method;
  }

  /**
   * @return the method name as a usable string
   */
  public String getMethodNameAsString() {
    return method.toString();
  }

  /**
   * @return the collection of arguments
   */
  public Object[] getArguments() {
    return args;
  }

  /**
   * @return the uniq id of the packet. Can be used to differentiate similare packets
   */
  public String getUuid() {
    return uuid;
  }
}
