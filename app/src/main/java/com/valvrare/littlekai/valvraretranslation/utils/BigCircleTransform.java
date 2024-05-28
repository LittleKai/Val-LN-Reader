package com.valvrare.littlekai.valvraretranslation.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
//import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
//import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by Kai on 9/26/2016.
 */
public class BigCircleTransform extends BitmapTransformation {
    public BigCircleTransform(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        int size = Math.max(source.getWidth(), source.getHeight());
//        int x = -(source.getWidth() - size) / 2;
//        int y = -(source.getHeight() - size) / 2;

        // TODO this could be acquired from the pool too
        Bitmap squared = Bitmap.createScaledBitmap(source, size, size, true);
//        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

        Bitmap output = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (output == null) {
            output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }
//        Canvas canvas = new Canvas(output);
//
//        final Rect rect = new Rect(0, 0, size, size);
//        final RectF rectF = new RectF(rect);
//        final Paint paint = new Paint();
//        paint.setFilterBitmap(true);
//        paint.setDither(true);
//        paint.setAntiAlias(true);
//        canvas.drawARGB(0, 0, 0, 0);
//
//        canvas.drawRoundRect(rectF, size / 2, size / 2, paint);
//
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(squared, rect, rect, paint);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        return output;
    }

    @Override
    public String getId() {
        return getClass().getName();
    }

}
