package com.afifzafri.backpacktrack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ImageFullscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);

        // init transparent toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); // remove title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back navigation

        FrameLayout loadingFrame = (FrameLayout) findViewById(R.id.loadingFrame);
        ImageView imageFullscreen = (ImageView) findViewById(R.id.imageFullscreen);
        TextView textCaption = (TextView) findViewById(R.id.textCaption);

        // get url from intent, and set to imageview if not null
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            String image_url = extras.getString("image_url");
            String caption = extras.getString("caption");

            Picasso.get().load(image_url).into(imageFullscreen);
            textCaption.setText(caption);

            loadingFrame.setVisibility(View.GONE);
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
