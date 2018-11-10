package com.afifzafri.backpacktrack;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class WebviewWidgetActivity extends AppCompatActivity {

    private WebView webView;


    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_widget);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back navigation

        // get data pass through intent
        Bundle extras = getIntent().getExtras();
        final String title = extras.getString("title");
        final String widget = extras.getString("widget");

        setTitle(title); // set actionbar title

        // show loading spinner
        final FrameLayout loadingFrame = (FrameLayout) findViewById(R.id.loadingFrame);
        loadingFrame.setVisibility(View.VISIBLE);

        webView = (WebView) findViewById(R.id.webview_content);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loadingFrame.setVisibility(View.VISIBLE);
                view.loadUrl(url);

                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                loadingFrame.setVisibility(View.GONE);
            }
        });

        // widget url
        webView.loadUrl(AppHelper.baseurl + "/" + widget);

    }

    // override default back navigation action
    // need finish(), to destroy the current activity so that it go back to last activity with last fragment
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
