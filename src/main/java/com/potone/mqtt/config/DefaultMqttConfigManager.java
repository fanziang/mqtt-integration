package com.potone.mqtt.config;

import com.potone.mqtt.autoconfigure.MqttProperties;
import com.potone.mqtt.autoconfigure.MqttServerProperty;
import com.potone.mqtt.autoconfigure.MqttTopicProperty;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author fan'zi'ang
 * @create 2022/11/12
 */
public class DefaultMqttConfigManager implements MqttConfigAdapter {

    private List<MqttServerConfig> serverConfigs = new ArrayList<>();

    private Map<String, MqttServerConfig> serversMap = new HashMap<>();

    private Map<String, List<MqttTopicConfig>> topicsMap = new HashMap<>();

    public DefaultMqttConfigManager(MqttProperties mqttProperties) {
        if (null != mqttProperties && MapUtils.isNotEmpty(mqttProperties.getServers())) {
            for (Map.Entry<String, MqttServerProperty> entry : mqttProperties.getServers().entrySet()) {
                MqttServerProperty serverProperty = entry.getValue();
                String key = entry.getKey();
                MqttServerConfigImpl serverConfig = new MqttServerConfigImpl();
                serverConfig.setServerId(key);
                serverConfig.setConsumerClientId(serverProperty.getConsumerClientId());
                serverConfig.setProducerClientId(serverProperty.getProducerClientId());
                serverConfig.setUrl(serverProperty.getUrl());
                serverConfig.setUserName(serverProperty.getUserName());
                serverConfig.setPassword(serverProperty.getPassword());
                serverConfig.setCleanSession(serverProperty.isCleanSession());
                serverConfig.setDefaultQos(serverProperty.getDefaultQos());
                serverConfig.setDefaultTopic(serverProperty.getDefaultTopic());
                serverConfig.setConnectionTimeout(serverProperty.getConnectionTimeout());
                serverConfig.setAutomaticReconnect(serverProperty.getAutomaticReconnect());
                serverConfig.setKeepAliveInterval(serverProperty.getKeepAliveInterval());
                serverConfig.setMaxInflight(serverProperty.getMaxInflight());
                serverConfig.setMqttVersion(serverProperty.getMqttVersion());
                List<MqttTopicConfig> list = new ArrayList<>();
                if (null != serverProperty.getTopics()) {
                    for (MqttTopicProperty topicProperty : serverProperty.getTopics()) {
                        MqttTopicConfigImpl topicConfig = new MqttTopicConfigImpl();
                        topicConfig.setTopicName(topicProperty.getTopic());
                        topicConfig.setQos(topicProperty.getQos());
                        topicConfig.setMessageHandler(topicProperty.getMessageHandler());
                        list.add(topicConfig);
                    }
                }
                topicsMap.put(key, list);
                serverConfigs.add(serverConfig);
                serversMap.put(key, serverConfig);
            }
        }
    }

    @Override
    public List<MqttServerConfig> getServerConfigs() {
        return serverConfigs;
    }

    @Override
    public MqttServerConfig getServerConfig(String serverId) {
        return serversMap.get(serverId);
    }

    @Override
    public List<MqttTopicConfig> getServerTopicConfigs(String serverId) {
        return topicsMap.get(serverId);
    }
}
