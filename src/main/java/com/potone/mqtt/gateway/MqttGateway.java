package com.potone.mqtt.gateway;

public interface MqttGateway {

    void sendToMqtt(String serverId, byte[] payload);

    void sendToMqtt(String serverId, String topic, byte[] payload);

    void sendToMqtt(String serverId, String topic, int qos, byte[] payload);

}