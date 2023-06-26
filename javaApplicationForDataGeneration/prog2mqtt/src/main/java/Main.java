import org.eclipse.paho.client.mqttv3.*;

public class Main {
    public static void main(String[] args) {
        TemperatureLightLevelApplication application = new TemperatureLightLevelApplication();

        // Create and configure MQTT client
        String broker = "tcp://public.mqtthq.com:1883";
        String topic = "prog2/smartclassroom";
        String clientId = "TemperatureLightLevelClient";
        int qos = 1;

        try {
            MqttClient client = new MqttClient(broker, clientId);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            client.connect(connectOptions);

            // Subscribe to the MQTT topic
            client.subscribe(topic, qos);

            // Creat    e an MQTT message callback
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    System.out.println(payload);

                    // Check if the received message is "STOP" to stop the application
                    if ("STOP".equals(payload)) {
                        System.out.println("Application stopped");
                        application.stop();
                        client.disconnect();
                    }

                    if (payload.contains("APP Licht")) {
                        System.out.println("\n#################################");
                        System.out.println("SIMULATE LIGHT SWITCH");
                        System.out.println("SET LIGHT LEVEL TO: " + payload.substring(payload.lastIndexOf(": ") + 2));
                        System.out.println("#################################\n");
                    }
                    if (payload.contains("APP Heizung")) {
                        System.out.println("\n#################################");
                        System.out.println("SIMULATE HEATER");
                        System.out.println("SET HEATER TO: " + payload.substring(payload.lastIndexOf(": ") + 2));
                        System.out.println("#################################\n");
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            // Start the TemperatureLightLevelApplication
            Thread applicationThread = new Thread(application::start);
            applicationThread.start();

            // Wait for the application to stop
            applicationThread.join();
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}