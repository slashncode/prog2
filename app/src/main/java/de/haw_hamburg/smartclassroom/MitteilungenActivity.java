package de.haw_hamburg.smartclassroom;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


public class MitteilungenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mitteilungen);

        setupBottomNavigationView();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ArrayList<Bundle> cardBundles = extras.getParcelableArrayList("cardBundles");

            if (cardBundles != null) {
                LinearLayout cardContainer = findViewById(R.id.mitteilungenContainer);

                int j = 0;
                for (Bundle cardBundle : cardBundles) {
                    j++;
                    if (cardBundles.size() == j) {
                        CardView cardView = createCard(cardBundle, true);
                        cardContainer.addView(cardView);
                    } else {
                        CardView cardView = createCard(cardBundle, false);
                        cardContainer.addView(cardView);
                    }
                }

                BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.menu_mitteilungen);
                badge.isVisible();
                if (cardBundles.size() == 1) {
                    badge.setNumber(1);
                }
                if (cardBundles.size() == 2) {
                    badge.setNumber(2);
                }
            }
        }
    }

    private CardView createCard(Bundle cardBundle, boolean lastCard) {
        int padding100_in_dp = 100;  // 100 dps
        int padding48_in_dp = 48;  // 48 dps
        int padding16_in_dp = 16;  // 16 dps
        int padding8_in_dp = 8;  // 8 dps
        final float scale = getResources().getDisplayMetrics().density;
        int padding100_in_px = (int) (padding100_in_dp * scale + 0.5f);
        int padding48_in_px = (int) (padding48_in_dp * scale + 0.5f);
        int padding16_in_px = (int) (padding16_in_dp * scale + 0.5f);
        int padding8_in_px = (int) (padding8_in_dp * scale + 0.5f);

        CardView cardView = new CardView(this);
        RelativeLayout.LayoutParams cardLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        cardLayoutParams.setMargins(padding48_in_px, padding16_in_px, padding48_in_px, padding16_in_px);
        cardView.setLayoutParams(cardLayoutParams);
        if (lastCard) {
            cardLayoutParams.setMargins(padding48_in_px, padding16_in_px, padding48_in_px, padding100_in_px);
        }
        cardView.setCardBackgroundColor(Color.parseColor("#ECE6F0"));
        cardView.setRadius(padding16_in_dp);

        LinearLayout cardLayout = new LinearLayout(this);
        cardLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.setPadding(padding16_in_px, padding16_in_px, padding16_in_px, padding16_in_px);

        TextView headlineText = new TextView(this);
        LinearLayout.LayoutParams headlineLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        headlineText.setLayoutParams(headlineLayoutParams);
        headlineText.setText(cardBundle.getString("headline"));
        headlineText.setTextSize(20);
        headlineText.setTypeface(headlineText.getTypeface(), Typeface.BOLD);

        TextView messageText = new TextView(this);
        LinearLayout.LayoutParams messageLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        messageLayoutParams.setMargins(0, padding16_in_px,0,padding16_in_px);
        messageText.setLayoutParams(messageLayoutParams);
        messageText.setText(cardBundle.getString("subhead"));
        messageText.setTextSize(16);

        LinearLayout actionsLayout = new LinearLayout(this);
        LinearLayout.LayoutParams actionsLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        actionsLayoutParams.gravity = Gravity.RIGHT;
        actionsLayout.setLayoutParams(actionsLayoutParams);
        actionsLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button action1Button = new Button(this);
        LinearLayout.LayoutParams action1LayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        action1LayoutParams.setMargins(padding8_in_px, padding8_in_px, padding8_in_px, padding8_in_px);
        action1Button.setLayoutParams(action1LayoutParams);
        action1Button.setText("Wiederholen");
        action1Button.setTextColor(Color.parseColor("#000000"));
        action1Button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECE6F0")));

        Button action2Button = new Button(this);
        LinearLayout.LayoutParams action2LayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        action2LayoutParams.setMargins(padding8_in_px, padding8_in_px, padding8_in_px, padding8_in_px);
        action2Button.setLayoutParams(action2LayoutParams);
        action2Button.setText("Löschen");
        action2Button.setTextColor(Color.parseColor("#000000"));
        action2Button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECE6F0")));

        actionsLayout.addView(action1Button);
        actionsLayout.addView(action2Button);

        cardLayout.addView(headlineText);
        cardLayout.addView(messageText);
        cardLayout.addView(actionsLayout);

        cardView.addView(cardLayout);

        return cardView;
    }
}