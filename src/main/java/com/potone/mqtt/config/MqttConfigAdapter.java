package com.potone.mqtt.config;

import java.util.List;

/**
 * @Author fan'zi'ang
 * @create 2022/11/12
 */
public interface MqttConfigAdapter {

    /**
     * get configs of all servers
     *
     * @return server config list
     */
    List<MqttServerConfig> getServerConfigs();

    /**
     * get config by server id
     *
     * @param serverId
     * @return server config
     */
    MqttServerConfig getServerConfig(String serverId);

    /**
     * get all topic configs of server by server id
     *
     * @param serverId
     * @return topic config list
     */
    List<MqttTopicConfig> getServerTopicConfigs(String serverId);
}
