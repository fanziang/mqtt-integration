package com.potone.mqtt.exception;

public class MqttServerException extends RuntimeException {

    public MqttServerException(String message) {
        super(message);
    }

    public MqttServerException(Throwable cause) {
        super(cause);
    }

    public MqttServerException(String message, Throwable cause) {
        super(message, cause);
    }

}
