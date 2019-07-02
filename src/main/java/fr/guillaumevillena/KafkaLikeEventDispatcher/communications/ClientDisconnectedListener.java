package fr.guillaumevillena.KafkaLikeEventDispatcher.communications;

/**
 * Event listener when a user just disconnected to the TCP Server
 */
public interface ClientDisconnectedListener {
  /**
   * Called when a user disconnected
   *
   * @param thread the current socket thread
   */
  void onClientDisconnected(ClientSocketThread thread);

}
