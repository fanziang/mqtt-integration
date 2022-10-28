package com.potone.mqtt.message;

@FunctionalInterface
public interface ByteMessageHandler {

    void handle(String topic, byte[] bytes);
}
