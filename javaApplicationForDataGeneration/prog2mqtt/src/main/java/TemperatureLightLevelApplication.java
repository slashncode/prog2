import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Random;
import java.text.DecimalFormat;


public class TemperatureLightLevelApplication {
    private boolean running;
    private float temperature;
    private float lightLevel;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final String BROKER = "tcp://public.mqtthq.com:1883";
    private static final String TOPIC = "prog2/smartclassroom";
    private static final MqttClient client;

    static {
        try {
            client = new MqttClient(BROKER, MqttClient.generateClientId());
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }


    public TemperatureLightLevelApplication() {
        this.running = true;
        this.temperature = generateRandomFloat(13.00f, 20.00f);
        this.lightLevel = generateRandomFloat(0.00f, 100.00f);
    }

    public void start() {
        float temperatureRange = 1.00f;
        float lightLevelRange = 30.00f;

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);

        try {
            client.connect(options);
            while (running) {
                if (running) {
                    MQTTPublisher.publish("JAVA Temperature: " + df.format(temperature), client, TOPIC);
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (running) {
                    MQTTPublisher.publish("JAVA Light Level: " + df.format(lightLevel), client, TOPIC);
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (running) {
                    try {
                        int sleepTimer = generateRandomInt(1000, 3000);
                        MQTTPublisher.publish("JAVA Sleep for: " + sleepTimer + "ms", client, TOPIC);

                        Thread.sleep(sleepTimer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                float temperatureChange = generateRandomFloat(-1.00f, 1.00f);
                if (temperature + temperatureChange > 25.00f) {
                    temperature -= temperatureRange;
                } else if (temperature + temperatureChange < 13.00f) {
                    temperature += temperatureRange;
                } else {
                    temperature += temperatureChange;
                }

                float lightLevelChange = generateRandomFloat(-4.00f, 4.00f);
                if (lightLevel + lightLevelChange > 100.00f) {
                    lightLevel -= lightLevelRange;
                } else if (lightLevel + lightLevelChange < 0.00f) {
                    lightLevel += lightLevelRange;
                } else {
                    lightLevel += lightLevelChange;
                }
            }
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        running = false;
        try {
            client.disconnect();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    private static float generateRandomFloat(float min, float max) {
        Random random = new Random();
        return random.nextFloat() * (max - min) + min;
    }

    private static int generateRandomInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}