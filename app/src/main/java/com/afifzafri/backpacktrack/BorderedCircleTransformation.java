package com.afifzafri.backpacktrack;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.graphics.ColorUtils;

import com.squareup.picasso.Transformation;

public class BorderedCircleTransformation implements Transformation {
    private int borderColor;
    private int borderRadius;

    public BorderedCircleTransformation(int borderColor, int borderRadius) {
        this.borderColor = ColorUtils.setAlphaComponent(borderColor, 0xFF);
        this.borderRadius = borderRadius;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;

        // Prepare the background
        Paint paintBg = new Paint();
        paintBg.setColor(borderColor);
        paintBg.setAntiAlias(true);

        // Draw the background circle
        canvas.drawCircle(r, r, r, paintBg);

        // Draw the image smaller than the background so a little border will be seen
        canvas.drawCircle(r, r, r - borderRadius, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public void setBorderRadius(int borderRadius) {
        this.borderRadius = borderRadius;
    }
}