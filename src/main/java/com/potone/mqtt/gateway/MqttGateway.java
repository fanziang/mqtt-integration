package com.potone.mqtt.gateway;

/**
 * @Author fan'zi'ang
 * @create 2022/10/29
 */
public interface MqttGateway {

    void sendToMqtt(String serverId, byte[] payload);

    void sendToMqtt(String serverId, String topic, byte[] payload);

    void sendToMqtt(String serverId, String topic, int qos, byte[] payload);

}