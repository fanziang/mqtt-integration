# mqtt-integration
<strong>use spring integration mqtt and support multi mqtt servers.</strong>
## Demo
```
@Configuration
public class MqttConfig {

    @Bean
    public MessageHandlers messageHandlers(ApplicationContext applicationContext) {
        return new MessageHandlers(applicationContext);
    }
}
```
```
@Component
public class MqttRegistrar extends MqttAutoFlowRegistrar {

    @Autowired
    private MqttServerService mqttServerService;

    @Autowired
    private MqttTopicListenerService mqttTopicListenerService;

    @Autowired
    private MqttTopicLogService mqttTopicLogService;

    public MqttRegistrar(IntegrationFlowContext flowContext, MessageHandlers messageHandlers) {
        super(flowContext, messageHandlers);
    }

    @Override
    protected List<MqttServerConfig> getServerConfigs() {
        return new ArrayList<>(mqttServerService.list());
    }

    @Override
    protected MqttServerConfig getServerConfig(String serverId) {
        return mqttServerService.getById(serverId);
    }

    @Override
    protected List<MqttTopicConfig> getServerTopicConfigs(String serverId) {
        return new ArrayList<>(mqttTopicListenerService.list(new LambdaQueryWrapper<MqttTopicListener>().eq(MqttTopicListener::getServerId, serverId)));
    }

    @Override
    public void afterHandleMessage(String serverId, String clientId, String topic, byte[] bytes) {
        MqttTopicListener topicListener = (MqttTopicListener) getServerTopicHandlerMap().get(serverId).get(topic);
        if ("database".equalsIgnoreCase(topicListener.getLogType())) {
            MqttTopicLog topicLog = new MqttTopicLog();
            topicLog.setServerId(serverId);
            topicLog.setClientId(clientId);
            topicLog.setTopicName(topic);
            topicLog.setMessage(ModbusUtils.bytes2HexString(bytes));
            topicLog.setCreateBy("admin");
            topicLog.setCreateTime(new Date());
            mqttTopicLogService.save(topicLog);
        }
    }

}
```
```
@Slf4j
@MqttMessageHandler("testMessageHandler")
@Component
public class TestMessageHandler implements ByteMessageHandler {

    @Override
    public void handle(String topic, byte[] bytes) {
        //do something
    }
}
```
```
@SpringBootApplication
public class RiskApplication {

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(RiskApplication.class, args);
        MqttRegistrar mqttRegistrar = application.getBean(MqttRegistrar.class);
        mqttRegistrar.register();
    }
}
```