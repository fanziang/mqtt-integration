package com.potone.mqtt.config;

import com.potone.mqtt.message.MqttMessageHandler;

public interface MqttTopicConfig {

    String getTopicName();

    Integer getQos();

    /**
     * @return
     * @see MqttMessageHandler#value()
     */
    String getMessageHandler();

}
