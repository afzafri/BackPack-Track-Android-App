package com.afifzafri.backpacktrack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppHelper {

    /**
     * RESTFul API Endpoint
     */
    public static final String baseurl = "http://178.128.97.69";

    public AppHelper() {}

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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
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

    /**
     * method for checking if and ImageView have a drawable attached to it
     * credit: https://stackoverflow.com/a/32066539/5784900
     *
     * @param view
     * @return
     */
    public boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }

    /**
     * Method to convert String date format
     *
     * @param currentDate
     * @return
     */
    public String convertDate(String currentDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        Date newDate = null;
        try {
            newDate = format.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat("dd/mm/yyyy");
        String date = format.format(newDate);

        return date;
    }

    /**
     * Method to convert 24h time format to 12h
     *
     * @param currentTime
     * @return
     */
    public String convertTime(String currentTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
        Date dateObj = null;
        try {
            dateObj = sdf.parse(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("hh:mm aa").format(dateObj);
    }

    /**
     * Method for set spinner selection equals to input text
     * Credit: https://stackoverflow.com/a/24470136/5784900
     *
     * @param spin
     * @param text
     */
    public void setSpinText(Spinner spin, String text)
    {
        if(spin != null && spin.getSelectedItem() != null ) {
            for (int i = 0; i < spin.getAdapter().getCount(); i++) {
                if (spin.getAdapter().getItem(i).toString().contains(text)) {
                    spin.setSelection(i);
                }
            }
        }
    }
}