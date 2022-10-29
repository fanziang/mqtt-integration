package com.potone.mqtt.config;

/**
 * @Author fan'zi'ang
 * @create 2022/10/29
 */
public interface MqttServerConfig {

    String getId();

    String getUrl();

    String getUserName();

    String getPassword();

    boolean isCleanSession();

    int getDefaultQos();

    String getDefaultTopic();

}
