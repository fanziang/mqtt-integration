package com.potone.mqtt.config;

/**
 * @Author fan'zi'ang
 * @create 2022/11/12
 */
public class MqttServerConfigImpl implements MqttServerConfig {

    private String serverId;

    private String consumerClientId;

    private String producerClientId;

    private String url;

    private String userName;

    private String password;

    private boolean cleanSession;

    private int defaultQos;

    private String defaultTopic;

    private Integer connectionTimeout;

    private Boolean automaticReconnect;

    private Integer keepAliveInterval;

    private Integer maxInflight;

    private Integer mqttVersion;

    private boolean async = true;

    private Long completionTimeout;

    private Integer recoveryInterval;

    @Override
    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    @Override
    public String getConsumerClientId() {
        return consumerClientId;
    }

    public void setConsumerClientId(String consumerClientId) {
        this.consumerClientId = consumerClientId;
    }

    @Override
    public String getProducerClientId() {
        return producerClientId;
    }

    public void setProducerClientId(String producerClientId) {
        this.producerClientId = producerClientId;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    @Override
    public int getDefaultQos() {
        return defaultQos;
    }

    public void setDefaultQos(int defaultQos) {
        this.defaultQos = defaultQos;
    }

    @Override
    public String getDefaultTopic() {
        return defaultTopic;
    }

    public void setDefaultTopic(String defaultTopic) {
        this.defaultTopic = defaultTopic;
    }

    @Override
    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @Override
    public Boolean getAutomaticReconnect() {
        return automaticReconnect;
    }

    public void setAutomaticReconnect(Boolean automaticReconnect) {
        this.automaticReconnect = automaticReconnect;
    }

    @Override
    public Integer getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public void setKeepAliveInterval(Integer keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    @Override
    public Integer getMaxInflight() {
        return maxInflight;
    }

    public void setMaxInflight(Integer maxInflight) {
        this.maxInflight = maxInflight;
    }

    @Override
    public Integer getMqttVersion() {
        return mqttVersion;
    }

    public void setMqttVersion(Integer mqttVersion) {
        this.mqttVersion = mqttVersion;
    }

    @Override
    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    @Override
    public Long getCompletionTimeout() {
        return completionTimeout;
    }

    public void setCompletionTimeout(Long completionTimeout) {
        this.completionTimeout = completionTimeout;
    }

    @Override
    public Integer getRecoveryInterval() {
        return recoveryInterval;
    }

    public void setRecoveryInterval(Integer recoveryInterval) {
        this.recoveryInterval = recoveryInterval;
    }
}
