package fr.guillaumevillena.KafkaLikeEventDispatcher.communications;

/**
 * Event listener when an error occur
 */
public interface ErrorEncounteredListener {
  /**
   * The method called when an error is caught
   *
   * @param clientSocketThread the thread with the error
   * @param str                the error string
   * @param e                  the exception object
   */
  void onError(ClientSocketThread clientSocketThread, String str, Exception e);
}
