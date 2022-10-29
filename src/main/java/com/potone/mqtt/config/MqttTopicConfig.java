package com.potone.mqtt.config;

public interface MqttTopicConfig {

    String getServerId();

    String getTopicName();

    Integer getQos();

    String getMessageHandler();

}
