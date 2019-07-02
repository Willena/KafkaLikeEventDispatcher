package fr.guillaumevillena.KafkaLikeEventDispatcher.broker;


import fr.guillaumevillena.KafkaLikeEventDispatcher.clients.AbstractKafkaLikeClient;

import java.util.HashMap;
import java.util.Map;

/**
 * This class will store a client in the server/eventstack. It is use to keep track of the status and keep
 * the connection with the client
 */
public class ClientHandle {

  private final String uuid;
  private final ClientType type;
  private Map<String, Integer> clientStatus;

  private AbstractKafkaLikeClient clientInstance;

  /**
   * @param uuid   the unic Id of the Client
   * @param type   the client Type (remote or local)
   * @param client the actual client instance
   */
  public ClientHandle(String uuid, ClientType type, AbstractKafkaLikeClient client) {
    this.uuid = uuid;
    this.type = type;
    this.clientStatus = new HashMap<>();
    this.clientInstance = client;
  }

  /**
   * @return the current client instance in the ClientHandle
   */
  public AbstractKafkaLikeClient getClientInstance() {
    return clientInstance;
  }

  /**
   * @return the current uuid of the client
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * @return the current client type (local or remote)
   */
  public ClientType getType() {
    return type;
  }

  /**
   * @return the map with the client status on each topic
   */
  public Map<String, Integer> getClientStatus() {
    return clientStatus;
  }
}
