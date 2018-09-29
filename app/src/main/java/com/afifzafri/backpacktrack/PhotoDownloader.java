package com.afifzafri.backpacktrack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

public class PhotoDownloader implements Target {
    private final String image_url;
    private ImageView imageView;
    private Context ctx;
    public PhotoDownloader(Context ctx, String image_url, ImageView imageView) {
        this.image_url = image_url;
        this.imageView = imageView;
        this.ctx = ctx;
    }
    @Override
    public void onPrepareLoad(Drawable arg0) {
    }
    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
        // get file name from url
        String filename = URLUtil.guessFileName(image_url, null, null);

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Pictures/BackPack Track/" + filename);
        try {
            file.createNewFile();
            FileOutputStream ostream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.close();
            imageView.setImageBitmap(bitmap);

            // Refresh Gallery
            ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            Toast.makeText(ctx, "Image saved in /Pictures/BackPack Track/", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ctx, "Failed to save image!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        Toast.makeText(ctx, "Failed to save image!", Toast.LENGTH_SHORT).show();
    }
}