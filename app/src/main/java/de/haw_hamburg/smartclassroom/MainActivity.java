package de.haw_hamburg.smartclassroom;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBottomNavigationView();
        bottomNavigationView.setSelectedItemId(R.id.menu_ubersicht);
    }
}