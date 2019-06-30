package fr.guillaumevillena.KafkaLikeEventDispatcher.communications;

public interface ClientMessageListener {
    void onMessageReceived(ClientSocketThread clientSocketThread, Object msg);
}
