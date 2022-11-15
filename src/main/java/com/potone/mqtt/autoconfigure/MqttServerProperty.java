package com.potone.mqtt.autoconfigure;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author fan'zi'ang
 * @create 2022/11/11
 */
public class MqttServerProperty {

    /**
     * url
     */
    @NotBlank
    private String url;

    /**
     * consumer client id
     */
    private String consumerClientId;

    /**
     * producer client id
     */
    private String producerClientId;

    /**
     * user name
     */
    private String userName;

    /**
     * password
     */
    private String password;

    /**
     * default topic, default value is "topic"
     */
    private String defaultTopic = "topic";

    /**
     * default qos, default value is 0
     */
    private int defaultQos;

    /**
     * clean session, default value is false
     */
    private boolean cleanSession;

    /**
     * topic list in this server
     */
    @Valid
    @NestedConfigurationProperty
    private List<MqttTopicProperty> topics = new ArrayList<>();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getConsumerClientId() {
        return consumerClientId;
    }

    public void setConsumerClientId(String consumerClientId) {
        this.consumerClientId = consumerClientId;
    }

    public String getProducerClientId() {
        return producerClientId;
    }

    public void setProducerClientId(String producerClientId) {
        this.producerClientId = producerClientId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDefaultTopic() {
        return defaultTopic;
    }

    public void setDefaultTopic(String defaultTopic) {
        this.defaultTopic = defaultTopic;
    }

    public int getDefaultQos() {
        return defaultQos;
    }

    public void setDefaultQos(int defaultQos) {
        this.defaultQos = defaultQos;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public List<MqttTopicProperty> getTopics() {
        return topics;
    }

    public void setTopics(List<MqttTopicProperty> topics) {
        this.topics = topics;
    }
}
