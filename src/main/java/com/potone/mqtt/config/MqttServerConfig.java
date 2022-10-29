package com.potone.mqtt.config;

public interface MqttServerConfig {

    String getId();

    String getUrl();

    String getUserName();

    String getPassword();

    boolean isCleanSession();

    int getDefaultQos();

    String getDefaultTopic();

}
