package com.potone.mqtt.integration;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.potone.mqtt.config.MqttConfigAdapter;
import com.potone.mqtt.config.MqttServerConfig;
import com.potone.mqtt.config.MqttTopicConfig;
import com.potone.mqtt.exception.MqttServerException;
import com.potone.mqtt.gateway.MqttManager;
import com.potone.mqtt.message.ByteMessageHandler;
import com.potone.mqtt.message.MqttMessageHandler;
import com.potone.mqtt.util.UUIDGenerator;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author fan'zi'ang
 * @create 2022/10/29
 */
public class MqttAutoFlowRegistrar implements MqttManager {

    private static final Logger LOG = LoggerFactory.getLogger(MqttAutoFlowRegistrar.class);

    private ApplicationContext applicationContext;

    private IntegrationFlowContext flowContext;

    private MqttConfigAdapter mqttConfigAdapter;

    private MessageHandlers messageHandlers = new MessageHandlers();

    private Map<String, Map<String, MqttTopicConfig>> serverTopicHandlerMap = new HashMap<>();

    private Map<String, MqttPahoClientFactory> clientFactoryMap = new HashMap<>();

    private Map<String, MqttPahoMessageDrivenChannelAdapter> inboundAdapterMap = new HashMap<>();

    private Map<String, IntegrationFlowContext.IntegrationFlowRegistration> inboundFlowMap = new HashMap<>();

    private Map<String, IntegrationFlowContext.IntegrationFlowRegistration> outboundFlowMap = new HashMap<>();

    private String taskId = UUIDGenerator.generate();

    public MqttAutoFlowRegistrar(ApplicationContext applicationContext, MqttConfigAdapter mqttConfigAdapter) {
        this.applicationContext = applicationContext;
        this.flowContext = applicationContext.getBean(IntegrationFlowContext.class);
        this.mqttConfigAdapter = mqttConfigAdapter;
        LOG.info("Mqtt clients building task id is {}", taskId);
    }

    protected MqttTopicConfig getTopicHandler(String serverId, String topicName) {
        Map<String, MqttTopicConfig> map = serverTopicHandlerMap.get(serverId);
        if (null != map) {
            return map.get(topicName);
        }
        return null;
    }

    @Override
    public void refreshAll() {
        clearAll();
        register();
    }

    @Override
    public void refreshServer(String serverId) {
        refreshServer(mqttConfigAdapter.getServerConfig(serverId));
    }

    public void refreshServer(MqttServerConfig server) {
        if (null == server) {
            return;
        }
        String serverId = server.getServerId();
        List<MqttTopicConfig> listeners = mqttConfigAdapter.getServerTopicConfigs(serverId);
        Map<String, MqttTopicConfig> topicHandlerMap = new HashMap<>();
        if (null != listeners) {
            for (MqttTopicConfig listener : listeners) {
                topicHandlerMap.put(listener.getTopicName(), listener);
            }
        }
        serverTopicHandlerMap.put(serverId, topicHandlerMap);
        int[] qos = new int[listeners.size()];
        for (int i = 0; i < qos.length; i++) {
            MqttTopicConfig listener = listeners.get(i);
            qos[i] = null == listener.getQos() ? server.getDefaultQos() : listener.getQos();
        }
        if (!clientFactoryMap.containsKey(serverId)) {
            MqttPahoClientFactory clientFactory = createClientFactory(server);
            clientFactoryMap.put(serverId, clientFactory);
            if (!topicHandlerMap.isEmpty()) {
                configInbound(serverId, getConsumerClientId(server), clientFactory, qos);
            }
            configOutbound(server, getProducerClientId(server), clientFactory);
        } else {
            MqttPahoMessageDrivenChannelAdapter adapter = inboundAdapterMap.get(serverId);
            if (null != adapter) {
                if (topicHandlerMap.isEmpty()) {
                    inboundAdapterMap.remove(serverId);
                    removeRegistration(inboundFlowMap.get(serverId));
                    inboundFlowMap.remove(serverId);
                } else {
                    adapter.stop();
                    String[] topics = adapter.getTopic();
                    for (String topic : topics) {
                        adapter.removeTopic(topic);
                    }
                    adapter.addTopics(listeners.stream().map(MqttTopicConfig::getTopicName).collect(Collectors.toList()).toArray(new String[0]), qos);
                    adapter.start();
                }
            } else if (!topicHandlerMap.isEmpty()) {
                configInbound(serverId, getConsumerClientId(server), clientFactoryMap.get(serverId), qos);
            }
            if (!outboundFlowMap.containsKey(serverId)) {
                configOutbound(server, getProducerClientId(server), clientFactoryMap.get(serverId));
            }
        }
    }

    private void configInbound(String serverId, String clientId, MqttPahoClientFactory clientFactory, int[] qos) {
        try {
            MqttPahoMessageDrivenChannelAdapter inboundAdapter = createAdapter(serverId, clientId, clientFactory, qos);
            IntegrationFlowContext.IntegrationFlowRegistration inboundFlowRegistration = registerInbound(serverId, clientId, inboundAdapter, new DirectChannel());
            inboundAdapterMap.put(serverId, inboundAdapter);
            inboundFlowMap.put(serverId, inboundFlowRegistration);
        } catch (Exception e) {
            LOG.error("inboundFlowRegistration error", e);
        }
    }

    private void configOutbound(MqttServerConfig server, String clientId, MqttPahoClientFactory clientFactory) {
        try {
            IntegrationFlowContext.IntegrationFlowRegistration outboundFlowRegistration = registerOutbound(server, clientId, clientFactory);
            outboundFlowMap.put(server.getServerId(), outboundFlowRegistration);
        } catch (Exception e) {
            LOG.error("outboundFlowRegistration error", e);
        }
    }

    private String getConsumerClientId(MqttServerConfig server) {
        String clientId = server.getConsumerClientId();
        if (StringUtils.isBlank(clientId)) {
            clientId = server.getServerId() + "-" + taskId + "-consumer";
        }
        return clientId;
    }

    private String getProducerClientId(MqttServerConfig server) {
        String clientId = server.getProducerClientId();
        if (StringUtils.isBlank(clientId)) {
            clientId = server.getServerId() + "-" + taskId + "-producer";
        }
        return clientId;
    }

    public void clearAll() {
        LOG.info("MqttAutoFlowRegistrar clear is called");
        for (IntegrationFlowContext.IntegrationFlowRegistration registration : inboundFlowMap.values()) {
            removeRegistration(registration);
        }
        for (IntegrationFlowContext.IntegrationFlowRegistration registration : outboundFlowMap.values()) {
            removeRegistration(registration);
        }
        serverTopicHandlerMap.clear();
        clientFactoryMap.clear();
        inboundAdapterMap.clear();
        inboundFlowMap.clear();
        outboundFlowMap.clear();
    }

    public void register() {
        LOG.info("MqttAutoFlowRegistrar register start");
        messageHandlers.init();
        List<MqttServerConfig> servers = mqttConfigAdapter.getServerConfigs();
        for (MqttServerConfig server : servers) {
            refreshServer(server);
        }
        LOG.info("MqttAutoFlowRegistrar register end");
    }

    private MqttPahoClientFactory createClientFactory(MqttServerConfig server) {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{server.getUrl()});
        if (StringUtils.isNotBlank(server.getUserName())) {
            options.setUserName(server.getUserName());
        }
        if (StringUtils.isNotBlank(server.getPassword())) {
            options.setPassword(server.getPassword().toCharArray());
        }
        options.setCleanSession(server.isCleanSession());
        if (null != server.getAutomaticReconnect()) {
            options.setAutomaticReconnect(server.getAutomaticReconnect());
        }
        if (null != server.getConnectionTimeout()) {
            options.setConnectionTimeout(server.getConnectionTimeout());
        }
        if (null != server.getKeepAliveInterval()) {
            options.setKeepAliveInterval(server.getKeepAliveInterval());
        }
        if (null != server.getMaxInflight()) {
            options.setMaxInflight(server.getMaxInflight());
        }
        if (null != server.getMqttVersion()) {
            options.setMqttVersion(server.getMqttVersion());
        }
        factory.setConnectionOptions(options);
        return factory;
    }

    private MqttPahoMessageDrivenChannelAdapter createAdapter(String serverId, String clientId, MqttPahoClientFactory clientFactory, int... qos) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId, clientFactory,
                serverTopicHandlerMap.get(serverId).keySet().toArray(new String[0]));
        adapter.setCompletionTimeout(5000);
        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
        converter.setPayloadAsBytes(true);
        adapter.setConverter(converter);
        adapter.setQos(qos);
        return adapter;
    }

    private IntegrationFlowContext.IntegrationFlowRegistration registerInbound(String serverId, String clientId, MessageProducerSupport adapter,
                                                                               MessageChannel channel) {
        IntegrationFlow flow = IntegrationFlows.from(adapter)
                .channel(channel)
                .handle(message -> {
                    String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
                    Object payLoad = message.getPayload();
                    byte[] bytes = (byte[]) payLoad;
                    MqttTopicConfig topicListener = getTopicListener(serverTopicHandlerMap.get(serverId), topic);
                    if (null != topicListener) {
                        beforeHandleMessage(serverId, clientId, topic, bytes);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("mqtt topic {} receive data 0x{}", topic, ByteArrayUtil.toHexString(bytes));
                        }
                        messageHandlers.handleMessage(topicListener.getMessageHandler(), topic, bytes);
                        afterHandleMessage(serverId, clientId, topic, bytes);
                    }
                })
                .get();
        return this.flowContext.registration(flow).id(clientId).useFlowIdAsPrefix().register();
    }

    private MqttTopicConfig getTopicListener(Map<String, MqttTopicConfig> topicConfigMap, String topic) {
        if (MapUtils.isEmpty(topicConfigMap) || StringUtils.isBlank(topic)) {
            return null;
        }
        MqttTopicConfig topicListener = topicConfigMap.get(topic);
        if (null != topicListener) {
            return topicListener;
        }
        for (Map.Entry<String, MqttTopicConfig> entry : topicConfigMap.entrySet()) {
            String key = entry.getKey();
            boolean match = matchTopic(key, topic);
            if (match) {
                topicListener = entry.getValue();
                break;
            }
        }
        //cache this topic listener
        topicConfigMap.put(topic, topicListener);
        return topicListener;
    }

    private boolean matchTopic(String topicFilter, String topicName) {
        if (StringUtils.isEmpty(topicFilter) || StringUtils.isEmpty(topicName)) {
            return false;
        }
        if (topicFilter.equals(topicName)) {
            return true;
        }
        String[] s1 = topicFilter.split("/");
        String[] s2 = topicName.split("/");
        int len1 = s1.length;
        int len2 = s2.length;
        int len = len1 > len2 ? len1 : len2;
        boolean match = true;
        for (int i = 0; i < len; i++) {
            if (i >= len1 || i >= len2 || (!"+".equals(s1[i]) && !s1[i].equals(s2[i]))) {
                match = false;
                break;
            } else if ("#".equals(s1[i])) {
                break;
            }
        }
        return match;
    }

    public void beforeHandleMessage(String serverId, String clientId, String topic, byte[] bytes) {
        //do nothing
    }

    public void afterHandleMessage(String serverId, String clientId, String topic, byte[] bytes) {
        //do nothing
    }

    private void removeRegistration(IntegrationFlowContext.IntegrationFlowRegistration flowReg) {
        this.flowContext.remove(flowReg.getId());
    }

    private IntegrationFlowContext.IntegrationFlowRegistration registerOutbound(MqttServerConfig server, String clientId, MqttPahoClientFactory clientFactory) {
        IntegrationFlow flow = f -> {
            MqttPahoMessageHandler messageHandler =
                    new MqttPahoMessageHandler(clientId, clientFactory);
            messageHandler.setAsync(true);
            messageHandler.setDefaultTopic(server.getDefaultTopic());
            messageHandler.setDefaultQos(server.getDefaultQos());
            f.handle(messageHandler);
        };
        return this.flowContext.registration(flow).id(clientId).useFlowIdAsPrefix().register();
    }

    public IntegrationFlowContext.IntegrationFlowRegistration getOutboundFlow(String serverId) {
        return outboundFlowMap.get(serverId);
    }

    private void send(String serverId, String topic, Integer qos, byte[] payload) {
        IntegrationFlowContext.IntegrationFlowRegistration flowRegistration = getOutboundFlow(serverId);
        if (null == flowRegistration) {
            throw new MqttServerException("mqtt server config not found");
        }
        if (StringUtils.isBlank(topic) && null == qos) {
            flowRegistration.getMessagingTemplate().convertAndSend(payload);
        } else {
            Map<String, Object> headers = new HashMap<>();
            if (StringUtils.isNotBlank(topic)) {
                headers.put(MqttHeaders.TOPIC, topic);
            }
            if (null != qos) {
                headers.put(MqttHeaders.QOS, qos);
            }
            flowRegistration.getMessagingTemplate().convertAndSend(flowRegistration.getInputChannel(), payload, headers);
        }
    }

    @Override
    public void sendToMqtt(String serverId, byte[] payload) {
        send(serverId, null, null, payload);
    }

    @Override
    public void sendToMqtt(String serverId, String topic, byte[] payload) {
        send(serverId, topic, null, payload);
    }

    @Override
    public void sendToMqtt(String serverId, String topic, int qos, byte[] payload) {
        send(serverId, topic, qos, payload);
    }

    private class MessageHandlers {

        private Map<String, ByteMessageHandler> handlerMap = new HashMap<>();

        private boolean initialized = false;

        public boolean isInitialized() {
            return initialized;
        }

        public void init() {
            if (initialized) return;
            LOG.info("MessageHandlers is initializing");
            handlerMap.clear();
            Map<String, ByteMessageHandler> map = applicationContext.getBeansOfType(ByteMessageHandler.class);
            for (ByteMessageHandler messageHandler : map.values()) {
                MqttMessageHandler handlerAnnotation = messageHandler.getClass().getDeclaredAnnotation(MqttMessageHandler.class);
                if (null != handlerAnnotation) {
                    LOG.info("MessageHandlers detect one ByteMessageHandler: {}", handlerAnnotation.value());
                    handlerMap.put(handlerAnnotation.value(), messageHandler);
                }
            }
            initialized = true;
            LOG.info("MessageHandlers initialization is done");
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

}
