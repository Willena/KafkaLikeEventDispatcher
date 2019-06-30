package fr.guillaumevillena.kafkalikeeventstack.communications;

public interface ClientConnectedListener {
    void onClientConnected(ClientSocketThread clientSocketThread);
}
