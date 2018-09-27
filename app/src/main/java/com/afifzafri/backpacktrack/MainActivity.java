package com.afifzafri.backpacktrack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        Fragment fragment = null;

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    setTitle(getString(R.string.title_home));
                    break;
                case R.id.navigation_itineraries:
                    fragment = new ItinerariesFragment();
                    setTitle(getString(R.string.title_itineraries));
                    break;
                case R.id.navigation_create:
                    fragment = new HomeFragment();
                    setTitle(getString(R.string.title_create));
                    break;
                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    setTitle(getString(R.string.title_profile));
                    break;
            }
            return loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //loading the default fragment
        loadFragment(new HomeFragment());
        setTitle(getString(R.string.title_home));

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    /**
     * Check if press hardware back button on fragments other than home, redirect to home fragment
     * else if pressed at home, prompt confirm message to exit app
     */
    @Override
    public void onBackPressed() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        if (bottomNavigationView.getSelectedItemId() == R.id.navigation_home) {
            // Create dialog box, ask confirmation before proceed
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Exit");
            alert.setMessage("Are you sure you want to exit the application?");
            // set positive button, yes etc
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish(); // close app
                    dialog.dismiss();
                }
            });
            // set negative button, no etc
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alert.show(); // show alert message
        } else {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

}
