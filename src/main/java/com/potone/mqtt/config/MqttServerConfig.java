package com.potone.mqtt.config;

/**
 * @Author fan'zi'ang
 * @create 2022/10/29
 */
public interface MqttServerConfig {

    /**
     * Returns id of MQTT server.
     *
     * @return server id
     */
    String getServerId();

    /**
     * Returns id of consumer client.
     *
     * @return id of consumer client
     */
    String getConsumerClientId();

    /**
     * Returns id of producer client.
     *
     * @return id of producer client
     */
    String getProducerClientId();

    /**
     * Returns the connection url of MQTT server.
     *
     * @return connection url of MQTT server
     */
    String getUrl();

    /**
     * Returns the user name to use for the connection.
     *
     * @return the user name to use for the connection.
     */
    String getUserName();

    /**
     * Returns the password to use for the connection.
     *
     * @return the password to use for the connection.
     */
    String getPassword();

    /**
     * Returns whether the client and server should remember state for the client across reconnects.
     *
     * @return the clean session flag
     */
    boolean isCleanSession();

    /**
     * Returns default qos of MQTT server.
     *
     * @return default qos of MQTT server
     */
    int getDefaultQos();

    /**
     * Returns the topic to which the message will be published.
     *
     * @return the default topic.
     */
    String getDefaultTopic();

    /**
     * Returns the connection timeout value.
     *
     * @return the connection timeout value.
     */
    Integer getConnectionTimeout();

    /**
     * Returns whether the client will automatically attempt to reconnect to the
     * server if the connection is lost.
     *
     * @return the automatic reconnection flag.
     */
    Boolean getAutomaticReconnect();

    /**
     * Returns the "keep alive" interval.
     *
     * @return the keep alive interval.
     */
    Integer getKeepAliveInterval();

    /**
     * Returns the "max inflight".
     * The max inflight limits to how many messages we can send without receiving acknowledgments.
     *
     * @return the max inflight
     */
    Integer getMaxInflight();

    /**
     * Returns the MQTT version.
     *
     * @return the MQTT version.
     */
    Integer getMqttVersion();
}
