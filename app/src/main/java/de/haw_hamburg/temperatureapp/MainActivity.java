package de.haw_hamburg.temperatureapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String MQTT_SERVER_URI = "tcp://mqtt.example.com:1883";
    private static final String MQTT_CLIENT_ID = "android-client";
    private static final String MQTT_TOPIC = "my/topic";

    private org.eclipse.paho.client.mqttv3.MqttClient mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectToMqttServer();
    }

    private void connectToMqttServer() {
        String clientId = MQTT_CLIENT_ID + "-" + MqttClient.generateClientId();
        try {
            mqttClient = new org.eclipse.paho.client.mqttv3.MqttClient(MQTT_SERVER_URI, clientId, new MemoryPersistence());

            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            mqttClient.connect(connectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Connected to MQTT server");
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "Failed to connect to MQTT server", exception);
                }

            });

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d(TAG, "MQTT connection lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    Log.d(TAG, "Received message: " + payload);
                    // Process the received message as needed
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Called when message delivery is complete
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, "MQTT connection error", e);
        }
    }

    private void subscribeToTopic() {
        try {
            mqttClient.subscribe(MQTT_TOPIC, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    Log.d(TAG, "Received message on subscribed topic: " + payload);
                    // Process the received message as needed
                }
            });
            Log.d(TAG, "Subscribed to topic: " + MQTT_TOPIC);
        } catch (MqttException e) {
            Log.e(TAG, "Failed to subscribe to topic: " + MQTT_TOPIC, e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectFromMqttServer();
    }

    private void disconnectFromMqttServer() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                Log.d(TAG, "Disconnected from MQTT server");
            }
        } catch (MqttException e) {
            Log.e(TAG, "MQTT disconnection error", e);
        }
    }
}