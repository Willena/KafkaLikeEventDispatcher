package fr.guillaumevillena.KafkaLikeEventDispatcher.communications;

public interface ErrorEncounteredListener {
    void onError(ClientSocketThread clientSocketThread, String str, Exception e);
}
