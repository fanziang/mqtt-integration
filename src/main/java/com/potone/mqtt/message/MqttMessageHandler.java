package com.potone.mqtt.message;

import java.lang.annotation.*;

/**
 * @Author fan'zi'ang
 * @create 2022/10/29
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttMessageHandler {

    String value() default "";
}
