# mqtt-integration
<strong>use spring integration mqtt and support multi mqtt servers.</strong>
## Demo
1. If MQTT configuration is predictable, configure properties. 
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
2.  If MQTT configuration is not predictable, you can implement MqttConfigAdapter.
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
3. Implement ByteMessageHandler.
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
4. send message.
    ```
   import com.potone.mqtt.gateway.MqttManager;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.web.bind.annotation.*;
   
    @RestController
    @RequestMapping("/mqtt")
    public class MqttController {
    
        @Autowired
        private MqttManager mqttManager;
    
        @PostMapping("/sendString")
        public String sendString(@RequestParam String serverId, @RequestParam String topic, @RequestBody String message) {
            mqttManager.sendToMqtt(serverId, topic, message.getBytes());
            return "send message : " + message;
        }
   }
    ```