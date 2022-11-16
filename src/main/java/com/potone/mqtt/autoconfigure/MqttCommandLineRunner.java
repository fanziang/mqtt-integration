package com.potone.mqtt.autoconfigure;

import com.potone.mqtt.gateway.MqttManager;
import org.springframework.boot.CommandLineRunner;

/**
 * @Author fan'zi'ang
 * @create 2022/11/16
 */
public class MqttCommandLineRunner implements CommandLineRunner {

    private MqttManager mqttManager;

    public MqttCommandLineRunner(MqttManager mqttManager) {
        this.mqttManager = mqttManager;
    }

    @Override
    public void run(String... args) throws Exception {
        if (null != mqttManager) {
            mqttManager.refreshAll();
        }
    }
}
