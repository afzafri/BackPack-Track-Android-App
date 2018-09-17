package com.afifzafri.backpacktrack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.io.ByteArrayOutputStream;

/**
 * Sketch Project Studio
 * Created by Angga on 12/04/2016 14.27.
 */
public class AppHelper {

    /**
     * Turn drawable resource into byte array.
     *
     * @param context parent context
     * @param id      drawable resource id
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 60, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Turn drawable into byte array.
     *
     * @param drawable data
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] getFileDataFromDrawableAvatar(Context context, Drawable drawable, int newWidth, int newHeight) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        AppHelper resize = new AppHelper();
        bitmap = resize.getResizedBitmap(bitmap, newWidth, newHeight);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Resize bitmap with new height and width
     *
     * @param bm
     * @param newWidth
     * @param newHeight
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        // get new width and height for maintaining aspect ratio
        int widthAspect = aspectRatio(bm, newWidth, newHeight)[0];
        int heightAspect = aspectRatio(bm, newWidth, newHeight)[1];

        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) widthAspect) / width;
        float scaleHeight = ((float) heightAspect) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    /**
     * Return width and height of an image that maintained the aspect ratio
     *
     * @param bm
     * @param width
     * @param height
     * @return
     */
    public int[] aspectRatio(Bitmap bm, int width, int height)
    {
        int[] newSize = new int[2];

        float aspectRatio = bm.getWidth() /
                (float) bm.getHeight();

        if(width != 0 && height == 0)
        {
            // by width
            newSize[0] = width;
            newSize[1] = Math.round(width / aspectRatio);;
        }
        else if(height != 0 && width == 0)
        {
            // by heigth
            newSize[0] = Math.round(height * aspectRatio);
            newSize[1] = height;
        }
        else if(width != 0 && height != 0)
        {
            newSize[0] = width;
            newSize[1] = height;
        }

        return newSize;
    }
}