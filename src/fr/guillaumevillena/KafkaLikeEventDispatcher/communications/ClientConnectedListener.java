package fr.guillaumevillena.KafkaLikeEventDispatcher.communications;

/**
 * Event listener when a user just connected to the TCP Server
 */
public interface ClientConnectedListener {
  /**
   * Called when a user join
   *
   * @param clientSocketThread the current socket thread
   */
  void onClientConnected(ClientSocketThread clientSocketThread);
}
