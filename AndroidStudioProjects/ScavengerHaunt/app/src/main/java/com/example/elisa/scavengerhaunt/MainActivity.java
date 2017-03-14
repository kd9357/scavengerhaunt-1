package com.example.elisa.scavengerhaunt;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;

// MAIN MENU

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // put button information here
    }


    @Override
    public void onClick(View v) {
        // click to start HowToPlay activity
        startActivity(new Intent(this, HowToPlay.class));
    }
}
