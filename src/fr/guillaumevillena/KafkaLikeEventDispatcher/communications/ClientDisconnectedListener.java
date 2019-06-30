package fr.guillaumevillena.KafkaLikeEventDispatcher.communications;

public interface ClientDisconnectedListener {
    void onClientDisconnected(ClientSocketThread thread);

}
