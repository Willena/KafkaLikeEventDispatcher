package fr.guillaumevillena.kafkalikeeventstack.broker;


import fr.guillaumevillena.kafkalikeeventstack.Clients.AbstractKafkaLikeClient;

import java.util.HashMap;
import java.util.Map;

public class ClientHandle {

    private final String uuid;
    private final ClientType type;
    private Map<String, Integer> clientStatus;

    private AbstractKafkaLikeClient clientInstance;

    public ClientHandle(String uuid, ClientType type, AbstractKafkaLikeClient client){
        this.uuid = uuid;
        this.type = type;
        this.clientStatus = new HashMap<>();
        this.clientInstance = client;
    }

    public AbstractKafkaLikeClient getClientInstance(){
        return clientInstance;
    }

    public String getUuid() {
        return uuid;
    }

    public ClientType getType() {
        return type;
    }

    public Map<String, Integer> getClientStatus() {
        return clientStatus;
    }
}
