package com.potone.mqtt.gateway;

/**
 * @Author fan'zi'ang
 * @create 2022/11/16
 */
public interface MqttManager extends MqttGateway {

    void refreshServer(String serverId);

    void refreshAll();
}
