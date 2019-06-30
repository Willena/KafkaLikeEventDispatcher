package fr.guillaumevillena.KafkaLikeEventDispatcher.communications;

public interface ClientConnectedListener {
    void onClientConnected(ClientSocketThread clientSocketThread);
}
