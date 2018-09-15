package com.afifzafri.backpacktrack;

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
                    fragment = new HomeFragment();
                    setTitle(getString(R.string.title_itineraries));
                    break;
                case R.id.navigation_create:
                    fragment = new HomeFragment();
                    setTitle(getString(R.string.title_create));
                    break;
                case R.id.navigation_profile:
                    fragment = new HomeFragment();
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

}
