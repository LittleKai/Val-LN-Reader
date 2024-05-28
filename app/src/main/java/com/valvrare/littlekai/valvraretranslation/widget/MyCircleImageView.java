package com.valvrare.littlekai.valvraretranslation.widget;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class MyCircleImageView {

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        final int bitmapSize;
//        final int color = 0xff424242;
        final int color = 0xff000000;

//            bitmap = decodeBitmap(bitmap,100,100);
        if (bitmap.getWidth() > bitmap.getHeight()) {
            bitmapSize = bitmap.getWidth();
        } else {
            bitmapSize = bitmap.getHeight();
        }
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmapSize, bitmapSize, true);
        Bitmap output = Bitmap.createBitmap(bitmapSize, bitmapSize, Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final Rect rect = new Rect(0, 0, bitmapSize, bitmapSize);
        final RectF rectF = new RectF(rect);
        final Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawRoundRect(rectF, bitmapSize / 2, bitmapSize / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(scaledBitmap, rect, rect, paint);


        return output;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        Log.d("Kai", "decodeBitmap: " + options.outHeight + "," + width);

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        int ratio = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inSampleSize = ratio;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeBitmap(Bitmap bitmap, int reqWidth, int reqHeight) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, reqWidth * 3 / 2, reqHeight * 3 / 2, true);
        return getRoundedCornerBitmap(scaledBitmap);
    }
}