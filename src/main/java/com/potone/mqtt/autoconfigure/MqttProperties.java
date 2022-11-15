package com.potone.mqtt.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author fan'zi'ang
 * @create 2022/11/11
 */
@Validated
@ConfigurationProperties(prefix = "spring.integration.mqtt", ignoreInvalidFields = true)
public class MqttProperties {

    /**
     * mqtt servers
     */
    @Valid
    @NestedConfigurationProperty
    private Map<String, MqttServerProperty> servers = new LinkedHashMap<>();

    public Map<String, MqttServerProperty> getServers() {
        return servers;
    }

    public void setServers(Map<String, MqttServerProperty> servers) {
        this.servers = servers;
    }

}
