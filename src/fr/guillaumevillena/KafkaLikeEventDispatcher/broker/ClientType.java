package fr.guillaumevillena.KafkaLikeEventDispatcher.broker;

/**
 * Each type of client, remote is for a remote client over TCP. A local client is located inside the same application
 */
enum ClientType {
  LOCAL,
  REMOTE
}
