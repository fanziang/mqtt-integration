package com.potone.mqtt.config;

import java.util.List;

/**
 * @Author fan'zi'ang
 * @create 2022/11/12
 */
public interface MqttConfigAdapter {

    List<MqttServerConfig> getServerConfigs();

    MqttServerConfig getServerConfig(String serverId);

    List<MqttTopicConfig> getServerTopicConfigs(String serverId);
}
