package com.potone.mqtt.config;

/**
 * @Author fan'zi'ang
 * @create 2022/11/12
 */
public class MqttTopicConfigImpl implements MqttTopicConfig {

    private String topicName;

    private Integer qos;

    private String messageHandler;

    @Override
    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    @Override
    public Integer getQos() {
        return qos;
    }

    public void setQos(Integer qos) {
        this.qos = qos;
    }

    @Override
    public String getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(String messageHandler) {
        this.messageHandler = messageHandler;
    }
}
