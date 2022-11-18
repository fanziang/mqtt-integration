package com.potone.mqtt.autoconfigure;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;

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
     * The timeout value, measured in seconds.
     * If it is not null, it must be &gt;0.
     * The default timeout is 30 seconds when it is null.
     */
    private Integer connectionTimeout;

    /**
     * Sets whether the client will automatically attempt to reconnect to the
     * server if the connection is lost.
     */
    private Boolean automaticReconnect;

    /**
     * The "keep alive" interval, measured in seconds.
     * If it is not null, it must be &gt;0.
     */
    private Integer keepAliveInterval;

    /**
     * The max inflight limits to how many messages we can send without receiving acknowledgments.
     * If it is not null, it must be &gt;0.
     * The default value is 10 when it is null.
     */
    private Integer maxInflight;

    /**
     * the MQTT version
     */
    private Integer mqttVersion;

    /**
     * Set to true if you don't want to block when sending messages. Default false.
     * When true, message sent/delivered events will be published for reception
     * by a suitably configured 'ApplicationListener' or an event
     * inbound-channel-adapter.
     */
    private boolean async = true;

    /**
     * The maximum time to wait for an action to complete.
     * Default {@value MqttPahoMessageDrivenChannelAdapter#DEFAULT_COMPLETION_TIMEOUT} milliseconds.
     * When it is null, default value is used.
     */
    private Long completionTimeout;

    /**
     * The time (ms) to wait between reconnection attempts.
     * Default 10000.
     * When it is null, default value is used.
     */
    private Integer recoveryInterval;

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

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Boolean getAutomaticReconnect() {
        return automaticReconnect;
    }

    public void setAutomaticReconnect(Boolean automaticReconnect) {
        this.automaticReconnect = automaticReconnect;
    }

    public Integer getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public void setKeepAliveInterval(Integer keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    public Integer getMaxInflight() {
        return maxInflight;
    }

    public void setMaxInflight(Integer maxInflight) {
        this.maxInflight = maxInflight;
    }

    public Integer getMqttVersion() {
        return mqttVersion;
    }

    public void setMqttVersion(Integer mqttVersion) {
        this.mqttVersion = mqttVersion;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public Long getCompletionTimeout() {
        return completionTimeout;
    }

    public void setCompletionTimeout(Long completionTimeout) {
        this.completionTimeout = completionTimeout;
    }

    public Integer getRecoveryInterval() {
        return recoveryInterval;
    }

    public void setRecoveryInterval(Integer recoveryInterval) {
        this.recoveryInterval = recoveryInterval;
    }
}
