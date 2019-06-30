package fr.guillaumevillena.kafkalikeeventstack.communications;

public interface ClientDisconnectedListener {
    void onClientDisconnected(ClientSocketThread thread);

}
