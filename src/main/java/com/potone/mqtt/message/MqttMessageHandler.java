package com.potone.mqtt.message;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttMessageHandler {

    String value() default "";
}
