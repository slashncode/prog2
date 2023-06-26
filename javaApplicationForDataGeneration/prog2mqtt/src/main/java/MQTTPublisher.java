import org.eclipse.paho.client.mqttv3.*;

public class MQTTPublisher {
    public static void publish(String publishMessage, MqttClient client, String topic) {
        try {
            MqttMessage message = new MqttMessage(publishMessage.getBytes());
            client.publish(topic, message);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}