package com.potone.mqtt.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author fan'zi'ang
 * @create 2022/10/29
 */
public class MessageHandlers {

    private static final Logger LOG = LoggerFactory.getLogger(MessageHandlers.class);

    private Map<String, ByteMessageHandler> handlerMap = new HashMap<>();

    private ApplicationContext applicationContext;

    private boolean initialized = false;

    public boolean isInitialized() {
        return initialized;
    }

    public MessageHandlers(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void init() {
        LOG.info("MessageHandlers is initializing");
        handlerMap.clear();
        Map<String, ByteMessageHandler> map = this.applicationContext.getBeansOfType(ByteMessageHandler.class);
        for (ByteMessageHandler messageHandler : map.values()) {
            MqttMessageHandler handlerAnnotation = messageHandler.getClass().getDeclaredAnnotation(MqttMessageHandler.class);
            if (null != handlerAnnotation) {
                LOG.info("MessageHandlers detect one ByteMessageHandler: {}", handlerAnnotation.value());
                handlerMap.put(handlerAnnotation.value(), messageHandler);
            }
        }
        initialized = true;
        LOG.info("MessageHandlers is initialized with {} ByteMessageHandler", handlerMap.size());
    }

    public void handleMessage(String handlerName, String topic, byte[] bytes) {
        ByteMessageHandler handler = handlerMap.get(handlerName);
        if (null != handler) {
            handler.handle(topic, bytes);
        } else {
            LOG.warn("handlerName not exists: {}", handlerName);
        }
    }

}
