package de.haw_hamburg.smartclassroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView.getContext().getClass().getName().contains("UbersichtActivity")) {
            bottomNavigationView.setSelectedItemId(R.id.menu_ubersicht);
        } else {
            bottomNavigationView.setSelectedItemId(R.id.menu_mitteilungen);
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_ubersicht) {
            startActivity(new Intent(this, UbersichtActivity.class));
            return true;
        } else if (item.getItemId() == R.id.menu_mitteilungen) {
            startActivity(new Intent(this, MitteilungenActivity.class));
            return true;
        }
        return false;
    }
}
