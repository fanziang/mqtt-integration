package com.potone.mqtt.config;

import com.potone.mqtt.message.MqttMessageHandler;

/**
 * @Author fan'zi'ang
 * @create 2022/10/29
 */
public interface MqttTopicConfig {

    /**
     * support wildcards: + #
     */
    String getTopicName();

    Integer getQos();

    /**
     * @return
     * @see MqttMessageHandler#value()
     */
    String getMessageHandler();

}
