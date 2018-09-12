package com.afifzafri.backpacktrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // read from SharedPreferences
        SharedPreferences sharedpreferences = getSharedPreferences("logindata", Context.MODE_PRIVATE);
        String access_token = sharedpreferences.getString("access_token", "");

        // if token not exist, means not login so redirect to login page
        if(access_token == null)
        {
            Intent intentPage = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intentPage);
        }
        else
        {
            Intent intentPage = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intentPage);
        }
    }
}
