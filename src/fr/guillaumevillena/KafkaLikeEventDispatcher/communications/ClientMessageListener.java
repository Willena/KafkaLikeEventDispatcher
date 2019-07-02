package fr.guillaumevillena.KafkaLikeEventDispatcher.communications;

/**
 * Event listener when a user received a message.
 */
public interface ClientMessageListener {
    /**
     * Called when a user message is received in the socket
     *
     * @param clientSocketThread the current socket thread
     */
    void onMessageReceived(ClientSocketThread clientSocketThread, Object msg);
}
