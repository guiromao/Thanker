package co.thanker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

public class RotateTransformation extends BitmapTransformation {

    private float rotateRotationAngle = 0f;
    private int mOrientation;

    public RotateTransformation(Context context, int orientation) {
        //super(context);
        mOrientation = orientation;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        int exifOrientationDegrees = getExifOrientationDegrees(mOrientation);
        return TransformationUtils.rotateImageExif(pool, toTransform, exifOrientationDegrees);
    }

    private int getExifOrientationDegrees(int orientation) {
        int exifInt;
        switch (orientation) {
            case 90:
                exifInt = ExifInterface.ORIENTATION_ROTATE_90;
                break;
            // other cases
            default:
                exifInt = ExifInterface.ORIENTATION_NORMAL;
                break;
        }
        return exifInt;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}