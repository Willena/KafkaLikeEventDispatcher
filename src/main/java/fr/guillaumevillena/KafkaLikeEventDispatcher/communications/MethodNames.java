package fr.guillaumevillena.KafkaLikeEventDispatcher.communications;

/**
 * Enum of available method names for the TCP exchange
 */
public enum MethodNames {
  COMMIT_METHOD("commit"),
  ASK_EVENT_METHOD("askForEvent"),
  SUBSCRIBE_METHOD("subscribe"),
  PUSH_EVENT_METHOD("produceEvent"),
  REGISTER_METHOD("register"),
  SET_OFFSET_METHOD("setOffset"),
  FIRE_CALLBACK("fireCallback"),
  UNREGISTER_METHOD("unregister"),
  SET_UUID("setUniqID");

  private final String name;

  /**
   * Initialize with the method name as string
   *
   * @param methodName method name string
   */
  MethodNames(String methodName) {
    this.name = methodName;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
