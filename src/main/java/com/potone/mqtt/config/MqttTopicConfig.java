package com.potone.mqtt.config;

import com.potone.mqtt.message.MqttMessageHandler;

/**
 * @Author fan'zi'ang
 * @create 2022/10/29
 */
public interface MqttTopicConfig {

    /**
     * Returns the name of topic.
     * support wildcards: + #
     *
     * @return topic name
     */
    String getTopicName();

    /**
     * Returns qos value.
     *
     * @return qos value
     */
    Integer getQos();

    /**
     * Returns message handler name.
     *
     * @return message handler name
     * @see MqttMessageHandler#value()
     */
    String getMessageHandler();

}
