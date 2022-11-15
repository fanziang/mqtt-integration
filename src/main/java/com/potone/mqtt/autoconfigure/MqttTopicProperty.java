package com.potone.mqtt.autoconfigure;

import javax.validation.constraints.NotBlank;

/**
 * @Author fan'zi'ang
 * @create 2022/11/11
 */
public class MqttTopicProperty {

    /**
     * topic name or filter, support wildcards: + #
     */
    @NotBlank
    private String topic;

    /**
     * qos
     */
    private Integer qos;

    /**
     * handler name
     */
    private String messageHandler;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getQos() {
        return qos;
    }

    public void setQos(Integer qos) {
        this.qos = qos;
    }

    public String getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(String messageHandler) {
        this.messageHandler = messageHandler;
    }
}
