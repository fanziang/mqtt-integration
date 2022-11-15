package com.potone.mqtt.autoconfigure;

import com.potone.mqtt.config.DefaultMqttConfigManager;
import com.potone.mqtt.config.MqttConfigAdapter;
import com.potone.mqtt.integration.MqttAutoFlowRegistrar;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author fan'zi'ang
 * @create 2022/11/13
 */
@Configuration
public class MqttAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MqttAutoFlowRegistrar mqttAutoFlowRegistrar(ApplicationContext applicationContext, MqttConfigAdapter mqttConfigAdapter) {
        return new MqttAutoFlowRegistrar(applicationContext, mqttConfigAdapter);
    }

    @Configuration
    @ConditionalOnMissingBean(MqttConfigAdapter.class)
    @EnableConfigurationProperties(MqttProperties.class)
    public static class MqttConfiguration {

        @Bean
        public MqttConfigAdapter mqttConfigAdapter(MqttProperties mqttProperties) {
            return new DefaultMqttConfigManager(mqttProperties);
        }

    }
}
