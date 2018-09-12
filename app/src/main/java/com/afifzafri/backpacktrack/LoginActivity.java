package com.afifzafri.backpacktrack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginBut = (Button) findViewById(R.id.loginBut);

        loginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) findViewById(R.id.username);
                EditText password = (EditText) findViewById(R.id.password);
                CheckBox remember_me = (CheckBox) findViewById(R.id.remember_me);

                Toast.makeText(getApplicationContext(), "Username: "+ username.getText().toString() + " Password: "+ password.getText().toString() + " Remember me: "+ (remember_me.isChecked() ? 1 : 0), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
