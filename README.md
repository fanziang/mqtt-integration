# mqtt-integration
<strong>use spring integration mqtt and support multi mqtt servers.</strong>
## Demo
```
import com.potone.modbus.util.ModbusUtils;
import com.potone.mqtt.config.MqttServerConfig;
import com.potone.mqtt.config.MqttTopicConfig;
import com.potone.mqtt.integration.MqttAutoFlowRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
...

@Component
public class MqttRegistrar extends MqttAutoFlowRegistrar {

    @Autowired
    private MqttServerService mqttServerService;

    @Autowired
    private MqttTopicListenerService mqttTopicListenerService;

    @Autowired
    private MqttTopicLogService mqttTopicLogService;

    public MqttRegistrar(ApplicationContext applicationContext) {
        super(applicationContext);
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
        MqttTopicListener topicListener = (MqttTopicListener) getTopicHandler(serverId, topic);
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
import com.potone.mqtt.message.ByteMessageHandler;
import com.potone.mqtt.message.MqttMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
...

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
import com.potone.mqtt.integration.MqttAutoFlowRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
...

@SpringBootApplication
public class RiskApplication {

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(RiskApplication.class, args);
        MqttAutoFlowRegistrar mqttRegistrar = application.getBean(MqttAutoFlowRegistrar.class);
        if (null != mqttRegistrar) {
            mqttRegistrar.register();
        }
    }
}
```
```
public interface MqttService {

    void sendToMqtt(String serverId, byte[] payload);

    void sendToMqtt(String serverId, String topic, byte[] payload);

    void sendToMqtt(String serverId, String topic, int qos, byte[] payload);

    void refreshServer(String serverId);

    void refreshAll();
}
```
```
@Service
public class MqttServiceImpl implements MqttService {

    @Autowired
    private MqttRegistrar mqttRegistrar;

    @Override
    public void sendToMqtt(String serverId, byte[] payload) {
        mqttRegistrar.sendToMqtt(serverId, payload);
    }

    @Override
    public void sendToMqtt(String serverId, String topic, byte[] payload) {
        mqttRegistrar.sendToMqtt(serverId, topic, payload);
    }

    @Override
    public void sendToMqtt(String serverId, String topic, int qos, byte[] payload) {
        mqttRegistrar.sendToMqtt(serverId, topic, qos, payload);
    }

    @Override
    public void refreshServer(String serverId) {
        mqttRegistrar.refreshServer(serverId);
    }

    @Override
    public void refreshAll() {
        mqttRegistrar.register();
    }
}
```