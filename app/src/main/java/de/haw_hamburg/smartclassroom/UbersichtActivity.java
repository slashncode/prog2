package de.haw_hamburg.smartclassroom;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.TextView;

import info.mqtt.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UbersichtActivity extends BaseActivity implements View.OnClickListener {

    private Button button1;
    private Button button2;
    private Button button3;
    private HashMap<View, String> cardMap = new HashMap<>();
    private ArrayList<Bundle> cardBundles = new ArrayList<>();
    private Bundle cardBundle1 = new Bundle();
    private Bundle cardBundle2 = new Bundle();

    MqttAndroidClient mqttAndroidClient;

    final String serverUri = "tcp://public.mqtthq.com:1883";

    String clientId = "ExampleAndroidClient";
    final String subscriptionTopic = "prog2/smartclassroom";
    final String publishTopic = "prog2/smartclassroom";
    final String publishMessage = "Hello World!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubersicht);

        setupBottomNavigationView();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        button1 = findViewById(R.id.buttonRollos);
        button2 = findViewById(R.id.buttonLicht);
        button3 = findViewById(R.id.buttonHeizung);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

        LinearLayout cardContainer = findViewById(R.id.cardContainer);


        int padding100_in_dp = 100;  // 100 dps
        int padding20_in_dp = 20;  // 20 dps
        int padding_in_dp = 16;  // 16 dps
        final float scale = getResources().getDisplayMetrics().density;
        int padding100_in_px = (int) (padding100_in_dp * scale + 0.5f);
        int padding20_in_px = (int) (padding20_in_dp * scale + 0.5f);
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        // MQTT

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    System.out.println("Reconnected to: " + serverURI);
                    // Because Clean Session is true, we need to re-subscribe
                    subscribeToTopic();
                } else {
                    System.out.println("Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("Incoming message: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                disconnectedBufferOptions.setBufferEnabled(true);
                disconnectedBufferOptions.setBufferSize(100);
                disconnectedBufferOptions.setPersistBuffer(false);
                disconnectedBufferOptions.setDeleteOldestMessages(false);
                mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                subscribeToTopic();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                System.out.println("Failed to connect to: " + serverUri);
            }
        });

        int lightCardCount = 3;
        int heaterCardCount = 2;

        for (int i = 1; i <= lightCardCount; i++) {
            cardContainer.addView(createCard(i, "Licht", false));
        }

        for (int i = 1; i <= heaterCardCount; i++) {
            if(i == heaterCardCount){
                cardContainer.addView(createCard(i, "Heizung", true));
            } else {
                cardContainer.addView(createCard(i, "Heizung", false));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof Button) {
            Button button = (Button) v;
            boolean isSelected = button.isSelected();
            button.setSelected(!isSelected);

            if (isSelected) {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6750A4")));
                button.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E3DFE3")));
                button.setTextColor(Color.parseColor("#000000"));
            }

            if (button.getId() == R.id.buttonLicht) {
                hideCardsWithSameHeadline("Licht", isSelected);
            } else if (button.getId() == R.id.buttonHeizung) {
                hideCardsWithSameHeadline("Heizung", isSelected);
            }
        }
    }

    // Method to hide cards with the same headline
    private void hideCardsWithSameHeadline(String headline, boolean isSelected) {
        LinearLayout cardContainer = findViewById(R.id.cardContainer);

        for (Map.Entry<View, String> entry : cardMap.entrySet()) {
            View cardView = entry.getKey();
            String cardHeadline = entry.getValue();

            System.out.println(headline);

            if (cardHeadline.contains(headline) && isSelected) {
                cardView.setVisibility(View.VISIBLE);
            } else if (cardHeadline.contains(headline)) {
                cardView.setVisibility(View.GONE);
            }
        }
    }

    private CardView createCard(int cardNumber, String typ, boolean lastCard) {
        int padding100_in_dp = 100;  // 100 dps
        int padding20_in_dp = 20;  // 20 dps
        int padding_in_dp = 16;  // 16 dps
        final float scale = getResources().getDisplayMetrics().density;
        int padding100_in_px = (int) (padding100_in_dp * scale + 0.5f);
        int padding20_in_px = (int) (padding20_in_dp * scale + 0.5f);
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLayoutParams.setMargins(padding_in_px, padding_in_px, padding_in_px, padding_in_px);
        if (lastCard) {
            cardLayoutParams.setMargins(padding_in_px, padding_in_px, padding_in_px, padding100_in_px);
        }
        cardView.setLayoutParams(cardLayoutParams);
        cardView.setCardBackgroundColor(Color.WHITE);
        cardView.setCardElevation(8);
        cardView.setRadius(8);

        LinearLayout cardLayout = new LinearLayout(this);
        cardLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.setPadding(padding_in_px, padding_in_px, padding_in_px, padding_in_px);

        TextView headerText = new TextView(this);
        LinearLayout.LayoutParams headerLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        headerText.setLayoutParams(headerLayoutParams);
        headerText.setText(typ + " " + cardNumber);
        headerText.setTextSize(18);
        headerText.setTypeface(headerText.getTypeface(), Typeface.BOLD);

        TextView subheadText = new TextView(this);
        LinearLayout.LayoutParams subheadLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        subheadText.setLayoutParams(subheadLayoutParams);
        if (typ.equals("Licht")) {
            if (sharedPreferences.getInt("seekBarValueLicht" + cardNumber, 0) > 0) {
                subheadText.setText("An: " + sharedPreferences.getInt("seekBarValueLicht" + cardNumber, 0) + "%");
            } else {
                subheadText.setText("Aus");
            }
        } else {
            if (sharedPreferences.getInt("seekBarValueHeizung" + cardNumber, 13) > 13) {
                subheadText.setText("An: " + sharedPreferences.getInt("seekBarValueHeizung" + cardNumber, 13) + "°C");
            } else {
                subheadText.setText("Aus");
            }
        }

        subheadText.setTextSize(16);
        subheadText.setTypeface(subheadText.getTypeface(), Typeface.ITALIC);

        Space space = new Space(this);
        LinearLayout.LayoutParams spaceLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        spaceLayoutParams.setMargins(0, padding20_in_px, 0, padding20_in_px);
        space.setLayoutParams(spaceLayoutParams);

        SeekBar seekBar = new SeekBar(this);
        seekBar.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        if (typ.contains("Licht")) {
            seekBar.setMax(100);
            int seekBarValue = sharedPreferences.getInt("seekBarValueLicht" + cardNumber, 0);
            seekBar.setProgress(seekBarValue);
        } else {
            seekBar.setMax(25);
            seekBar.setMin(13);
            int seekBarValue = sharedPreferences.getInt("seekBarValueHeizung" + cardNumber, 13);
            seekBar.setProgress(seekBarValue);
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (typ.equals("Licht")) {
                    if (progress > 0) {
                        subheadText.setText("An: " + progress + "%");
                    }
                    else {
                        subheadText.setText("Aus");
                    }
                }
                else {
                    if (progress > 13) {
                        subheadText.setText("An: " + progress + " °C");
                    } else {
                        subheadText.setText("Aus");
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (typ.equals("Licht")) {
                    editor.putInt("seekBarValueLicht" + cardNumber, seekBar.getProgress());
                    editor.apply();
                    publishMessage("APP Licht " + cardNumber + ": " + seekBar.getProgress() + "%");
                } else {
                    editor.putInt("seekBarValueHeizung" + cardNumber, seekBar.getProgress());
                    editor.apply();
                    publishMessage("APP Heizung " + cardNumber + ": " + seekBar.getProgress() + "°C");
                }
            }
        });

        cardLayout.addView(headerText);
        cardLayout.addView(subheadText);
        cardLayout.addView(space);
        cardLayout.addView(seekBar);

        cardView.addView(cardLayout);

        cardMap.put(cardView, typ + cardNumber);

        return cardView;
    }

    public void subscribeToTopic(){
        mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                System.out.println("Subscribed!");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                System.out.println("Failed to subscribe");
            }
        });

        mqttAndroidClient.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // message Arrived!
                String payload = new String(message.getPayload());
                // Create a list to hold card bundles
                System.out.println("Message: " + topic + " : " + payload);
                if (new String(message.getPayload()).contains("Temperature")) {
                    TextView temperatureText = findViewById(R.id.temperatureText);
                    temperatureText.setText(payload.substring(payload.lastIndexOf(": ") + 2) + "°C");
                    String value = payload.substring(payload.lastIndexOf(": ") + 2).replace(',', '.');
                    if (Float.parseFloat(value) <= 23.00) {
                        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.menu_mitteilungen);
                        badge.isVisible();
                        cardBundle1.putString("headline", "Heizung 1 anschalten?");
                        cardBundle1.putString("subhead", "Es fröstelt, mach doch mal die Heizung an!");
                        if (!cardBundles.contains(cardBundle1)) {
                            cardBundles.add(cardBundle1);
                        }
                        if (cardBundles.size() == 1) {
                            badge.setNumber(1);
                        }
                        if (cardBundles.size() == 2) {
                            badge.setNumber(2);
                        }
                    } else {
                        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.menu_mitteilungen);
                        if (badge.getNumber() == 0) {
                            badge.setVisible(false);
                            cardBundles.remove(cardBundle1);
                        }
                    }
                }
                if (new String(message.getPayload()).contains("Light")) {
                    ImageView lightIcon = findViewById(R.id.iconImage);
                    String value = payload.substring(payload.lastIndexOf(": ") + 2).replace(',', '.');
                    if (Float.parseFloat(value) <= 30.00) {
                        lightIcon.setImageResource(R.drawable.icon_dark);
                        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.menu_mitteilungen);
                        badge.isVisible();
                        badge.setNumber(1);
                        cardBundle2.putString("headline", "Licht 1 anschalten?");
                        cardBundle2.putString("subhead", "Es ist zu dunkel, mach doch mal Licht an!");
                        if (!cardBundles.contains(cardBundle2)) {
                            cardBundles.add(cardBundle2);
                        }
                        if (cardBundles.size() == 1) {
                            badge.setNumber(1);
                        }
                        if (cardBundles.size() == 2) {
                            badge.setNumber(2);
                        }
                    } else {
                        lightIcon.setImageResource(R.drawable.icon_light);
                        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.menu_mitteilungen);
                        if (badge.getNumber() == 0) {
                            badge.setVisible(false);
                            cardBundles.remove(cardBundle2);
                        }
                    }
                }
            }
        });
    }

    public void publishMessage(String messageString){
        MqttMessage message = new MqttMessage();
        message.setPayload(messageString.getBytes());
        mqttAndroidClient.publish(publishTopic, message);
        System.out.println("Message Published");
        if(!mqttAndroidClient.isConnected()){
            System.out.println(mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_ubersicht) {
            startActivity(new Intent(this, UbersichtActivity.class));
            return true;
        } else if (item.getItemId() == R.id.menu_mitteilungen) {
            Intent intent = new Intent(this, MitteilungenActivity.class);
            intent.putParcelableArrayListExtra("cardBundles", cardBundles);
            startActivity(intent);
            return true;
        }
        return false;
    }

}
