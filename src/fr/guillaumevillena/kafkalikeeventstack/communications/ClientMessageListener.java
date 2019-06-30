package fr.guillaumevillena.kafkalikeeventstack.communications;

public interface ClientMessageListener {
    void onMessageReceived(ClientSocketThread clientSocketThread, Object msg);
}
