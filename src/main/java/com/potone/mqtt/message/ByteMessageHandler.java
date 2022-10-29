package com.potone.mqtt.message;

/**
 * @Author fan'zi'ang
 * @create 2022/10/29
 */
@FunctionalInterface
public interface ByteMessageHandler {

    void handle(String topic, byte[] bytes);
}
