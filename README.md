# mqtt-integration
<strong>use spring integration mqtt and support multi mqtt servers.</strong>
## Demo
1. If MQTT configuration is predictable, configure properties, otherwise you can implement MqttConfigAdapter.
    ```
    spring:
      integration:
        mqtt:
          servers:
            1:
              url: tcp://127.0.0.1:1883
              userName: admin
              password: 123456
              topics:
                - topic: topic1
                  qos: 1
                  messageHandler: messageHandler1
                - topic: topic2
                  qos: 1
                  messageHandler: messageHandler2
    ```
    ```
    import com.potone.mqtt.config.MqttConfigAdapter;
    import com.potone.mqtt.config.MqttServerConfig;
    import com.potone.mqtt.config.MqttTopicConfig;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Component;
    import java.util.ArrayList;
    import java.util.List;
    ...
    
    @Component
    public class MqttServerManager implements MqttConfigAdapter {
    
        @Autowired
        private MqttServerService mqttServerService;
    
        @Autowired
        private MqttTopicListenerService mqttTopicListenerService;
    
        @Override
        public List<MqttServerConfig> getServerConfigs() {
            return new ArrayList<>(mqttServerService.list());
        }
    
        @Override
        public MqttServerConfig getServerConfig(String serverId) {
            return mqttServerService.getById(serverId);
        }
    
        @Override
        public List<MqttTopicConfig> getServerTopicConfigs(String serverId) {
            return new ArrayList<>(mqttTopicListenerService.list(new LambdaQueryWrapper<MqttTopicListener>().eq(MqttTopicListener::getServerId, serverId)));
        }
    }
    ```
2. Implement ByteMessageHandler.
    ```
    import com.potone.mqtt.message.ByteMessageHandler;
    import com.potone.mqtt.message.MqttMessageHandler;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Component;
    ...
    
    @MqttMessageHandler("messageHandler1")
    @Component
    public class TestMessageHandler implements ByteMessageHandler {
    
        @Override
        public void handle(String topic, byte[] bytes) {
            //do something
        }
    }
    ```
3. Call MqttAutoFlowRegistrar.register().
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
4. If necessary, package it as a Service.
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
   import com.potone.mqtt.integration.MqttAutoFlowRegistrar;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.stereotype.Service;
   ...
   
    @Service
    public class MqttServiceImpl implements MqttService {
    
        @Autowired
        private MqttAutoFlowRegistrar mqttRegistrar;
    
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