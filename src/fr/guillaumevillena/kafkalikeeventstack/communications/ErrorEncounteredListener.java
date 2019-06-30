package fr.guillaumevillena.kafkalikeeventstack.communications;

public interface ErrorEncounteredListener {
    void onError(ClientSocketThread clientSocketThread, String str, Exception e);
}
