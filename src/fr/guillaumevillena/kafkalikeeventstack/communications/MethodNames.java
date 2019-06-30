package fr.guillaumevillena.kafkalikeeventstack.communications;

public enum MethodNames {
    COMMIT_METHOD("commit"),
    ASK_EVENT_METHOD("askForEvent"),
    SUBSCRIBE_METHOD("subscribe"),
    PUSH_EVENT_METHOD("produceEvent"),
    REGISTER_METHOD("register"),
    SET_OFFSET_METHOD("setOffset"),
    FIRE_CALLBACK("fireCallback"),
    UNREGISTER_METHOD("unregister");


    private final String name;

    MethodNames(String methodName) {
        this.name = methodName;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
