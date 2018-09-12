package com.afifzafri.backpacktrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // read from SharedPreferences
        SharedPreferences sharedpreferences = getSharedPreferences("logindata", Context.MODE_PRIVATE);
        String name = sharedpreferences.getString("name", "");

        TextView user_name = (TextView) findViewById(R.id.user_name);
        user_name.setText(name);
    }
}
