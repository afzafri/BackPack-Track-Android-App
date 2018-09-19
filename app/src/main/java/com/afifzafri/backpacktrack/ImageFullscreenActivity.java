package com.afifzafri.backpacktrack;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

public class ImageFullscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);

        // init transparent toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); // remove title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back navigation

        ImageView imageFullscreen = (ImageView) findViewById(R.id.imageFullscreen);

        // get url from intent, and set to imageview if not null
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            String image_url = extras.getString("image_url");
            Picasso.get().load(image_url).into(imageFullscreen);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_image_menu, menu);
        return true;
    }

    // override default back navigation action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // close
            case android.R.id.home:
                // todo: goto back activity from here

                finish();
                return true;

            // save image
            case R.id.action_save_image:
                String image_url = getIntent().getExtras().getString("image_url");
                ImageView imageFullscreen = (ImageView) findViewById(R.id.imageFullscreen);
                Picasso.get().load(image_url).into(new PhotoDownloader(getApplicationContext(), image_url , imageFullscreen));

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
