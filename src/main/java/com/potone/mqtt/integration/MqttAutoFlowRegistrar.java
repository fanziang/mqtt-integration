package com.potone.mqtt.integration;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.potone.mqtt.config.MqttServerConfig;
import com.potone.mqtt.config.MqttTopicConfig;
import com.potone.mqtt.exception.MqttServerException;
import com.potone.mqtt.gateway.MqttGateway;
import com.potone.mqtt.message.MessageHandlers;
import com.potone.mqtt.util.UUIDGenerator;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public abstract class MqttAutoFlowRegistrar implements MqttGateway {

    private static final Logger LOG = LoggerFactory.getLogger(MqttAutoFlowRegistrar.class);

    private IntegrationFlowContext flowContext;

    private MessageHandlers messageHandlers;

    private Map<String, Map<String, MqttTopicConfig>> serverTopicHandlerMap = new HashMap<>();

    private Map<String, MqttPahoClientFactory> clientFactoryMap = new HashMap<>();

    private Map<String, MqttPahoMessageDrivenChannelAdapter> inboundAdapterMap = new HashMap<>();

    private Map<String, IntegrationFlowContext.IntegrationFlowRegistration> inboundFlowMap = new HashMap<>();

    private Map<String, IntegrationFlowContext.IntegrationFlowRegistration> outboundFlowMap = new HashMap<>();

    private String middleId = UUIDGenerator.generate();

    protected MqttAutoFlowRegistrar(IntegrationFlowContext flowContext, MessageHandlers messageHandlers) {
        this.flowContext = flowContext;
        this.messageHandlers = messageHandlers;
        LOG.info("Mqtt client id middle value is {}", middleId);
    }

    public Map<String, Map<String, MqttTopicConfig>> getServerTopicHandlerMap() {
        return serverTopicHandlerMap;
    }

    protected abstract List<MqttServerConfig> getServerConfigs();

    protected abstract MqttServerConfig getServerConfig(String serverId);

    protected abstract List<MqttTopicConfig> getServerTopicConfigs(String serverId);

    public void refreshServer(String serverId) {
        refreshServer(getServerConfig(serverId));
    }

    public void refreshServer(MqttServerConfig server) {
        if (null == server) {
            return;
        }
        List<MqttTopicConfig> listeners = getServerTopicConfigs(server.getId());
        Map<String, MqttTopicConfig> topicHandlerMap = new HashMap<>();
        for (MqttTopicConfig listener : listeners) {
            topicHandlerMap.put(listener.getTopicName(), listener);
        }
        serverTopicHandlerMap.put(server.getId(), topicHandlerMap);
        int[] qos = new int[listeners.size()];
        for (int i = 0; i < qos.length; i++) {
            MqttTopicConfig listener = listeners.get(i);
            qos[i] = null == listener.getQos() ? server.getDefaultQos() : listener.getQos();
        }
        if (!clientFactoryMap.containsKey(server.getId())) {
            MqttPahoClientFactory clientFactory = createClientFactory(server);
            clientFactoryMap.put(server.getId(), clientFactory);
            String clientId = "";
            if (!topicHandlerMap.isEmpty()) {
                clientId = server.getId() + "-" + middleId + "-consumer";
                MqttPahoMessageDrivenChannelAdapter inboundAdapter = createAdapter(server.getId(), clientId, clientFactory, qos);
                inboundAdapterMap.put(server.getId(), inboundAdapter);
                inboundFlowMap.put(server.getId(), registerInbound(server.getId(), clientId, inboundAdapter, new DirectChannel()));
            }
            clientId = server.getId() + "-" + middleId + "-producer";
            IntegrationFlowContext.IntegrationFlowRegistration outboundFlowRegistration = registerOutbound(server, clientId, clientFactory);
            outboundFlowMap.put(server.getId(), outboundFlowRegistration);
        } else {
            MqttPahoMessageDrivenChannelAdapter adapter = inboundAdapterMap.get(server.getId());
            if (null != adapter) {
                if (topicHandlerMap.isEmpty()) {
                    inboundAdapterMap.remove(server.getId());
                    removeRegistration(inboundFlowMap.get(server.getId()));
                    inboundFlowMap.remove(server.getId());
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
                String clientId = server.getId() + "-" + middleId + "-consumer";
                MqttPahoMessageDrivenChannelAdapter inboundAdapter = createAdapter(server.getId(), clientId, clientFactoryMap.get(server.getId()), qos);
                inboundAdapterMap.put(server.getId(), inboundAdapter);
                inboundFlowMap.put(server.getId(), registerInbound(server.getId(), clientId, inboundAdapter, new DirectChannel()));
            }
        }
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
        if (!messageHandlers.isInitialized()) {
            LOG.info("MessageHandlers is not initialized and try to init now");
            messageHandlers.init();
        }
        List<MqttServerConfig> servers = getServerConfigs();
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
                    MqttTopicConfig topicListener = serverTopicHandlerMap.get(serverId).get(topic);
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

}
