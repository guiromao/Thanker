package co.thanker.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import co.thanker.R;
import co.thanker.data.Thanks;
import co.thanker.data.User;

public class ImageUtils {

    public static final String DEFAULT_IMAGE = "https://firebasestorage.googleapis.com/v0/b/thanker-b301f.appspot.com/o/avatarVerde.png?alt=media&token=18dfc445-2329-4850-836e-9b125a49bb17";

    public static void loadImageInto(Context context, String url, ImageView imageView) {
        if (context != null) {

            int orientation = 0;

            if (!url.equalsIgnoreCase(DEFAULT_IMAGE)) {
                orientation = 90;
            }

            RequestOptions myOptions = new RequestOptions()
                    .fitCenter(); // or centerCrop
            //.override(300, 300);

            //DiskCacheStrategy.RESOURCE will enable that we maintain the final loaded image in Cache

            Glide.with(context)
                    .asBitmap()
                    .apply(myOptions)
                    //.transform(new RotateTransformation(context, orientation))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .load(url)//replace null with String url of image
                    .into(imageView);
        }
    }

    public static void loadImageIntoRound(Context context, String url, ImageView imageView) {
        if (context != null) {

            int orientation = 0;

            if (!url.equalsIgnoreCase(DEFAULT_IMAGE)) {
                orientation = 90;
            }

            RequestOptions myOptions = new RequestOptions()
                    .fitCenter(); // or centerCrop
            //.override(300, 300);

            //DiskCacheStrategy.RESOURCE will enable that we maintain the final loaded image in Cache

            Glide.with(context)
                    .asBitmap()
                    .apply(myOptions)
                    //.transform(new RotateTransformation(context, orientation))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .load(url)//replace null with String url of image
                    .circleCrop()
                    .into(imageView);
        }
    }

    /*public static void loadRoundedImageInto(Context context, String url, ImageView imageView){
        int radius = context.getResources().getDimensionPixelSize(R.dimen.corner_radius);
        Glide.with(context)
                .load(url)
                .transform(RoundedCorners(radius))
                // Alternative: .transforms(CenterCrop(), RoundedCorners(radius))
                .placeholder(R.drawable.placeholder_thumbnail_square_primary)
                .error(R.drawable.placeholder_thumbnail_square_primary)
                .transition(DrawableTransitionOptions.withCrossFade()).into(imageView)
    }*/

    public static int pickColor(Context context, String thanksType) {
        int color = 0;

        switch (thanksType) {
            case "NORMAL":
                color = context.getResources().getColor(R.color.colorPrimary);
                break;
            case "SUPER":
                color = context.getResources().getColor(R.color.superThanksCoin);
                break;
            case "MEGA":
                color = context.getResources().getColor(R.color.megaThanksCoin);
                break;
            case "POWER":
                color = context.getResources().getColor(R.color.powerThanksCoin);
                break;
            case "ULTRA":
                color = context.getResources().getColor(R.color.ultraThanksCoin);
                break;
            default:
                break;
        }

        return color;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Drawable getThanksDraw(Context context, String thanksType) {
        Drawable result = null;

        if (context != null) {
            switch (thanksType) {
                case "NORMAL":
                    result = context.getResources().getDrawable(R.drawable.thanks);
                    break;
                case "SUPER":
                    result = context.getResources().getDrawable(R.drawable.superthanks);
                    break;
                case "MEGA":
                    result = context.getResources().getDrawable(R.drawable.megathanks);
                    break;
                case "POWER":
                    result = context.getResources().getDrawable(R.drawable.powerthanks);
                    break;
                case "ULTRA":
                    result = context.getResources().getDrawable(R.drawable.ultrathanks);
                    break;
            }
        }

        return result;
    }

    public static ColorStateList getThanksColor(Context context, Thanks thanks) {

        ColorStateList color = null;

        switch(thanks.getThanksType().toLowerCase()){
            case "normal": color = context.getResources().getColorStateList(R.color.colorPrimary); break;
            case "super": color = context.getResources().getColorStateList(R.color.superThanksCoin); break;
            case "mega": color = context.getResources().getColorStateList(R.color.megaThanksCoin); break;
            case "power": color = context.getResources().getColorStateList(R.color.powerThanksCoin); break;
            case "ultra": color = context.getResources().getColorStateList(R.color.ultraThanksCoin); break;
        }

        return color;
    }

    /*public static Drawable getRecentThanksDraw(Context context, String thanksType){
        Drawable result = null;

        if(context != null){
            switch(thanksType){
                case "NORMAL": result = context.getResources().getDrawable(R.drawable.thanksrecent); break;
                case "SUPER": result = context.getResources().getDrawable(R.drawable.superthanksrecent); break;
                case "MEGA": result = context.getResources().getDrawable(R.drawable.megathanksrecent); break;
                case "POWER": result = context.getResources().getDrawable(R.drawable.powerthanksrecent); break;
                case "ULTRA": result = context.getResources().getDrawable(R.drawable.ultrathanksrecent); break;
            }
        }

        return result;
    }*/

    public static Drawable getCategoryDrawable(Context context, String category) {
        Drawable image = null;

        switch (category.toLowerCase()) {
            case "":
                image = context.getResources().getDrawable(R.drawable.overallcat);
                break;
            case "person":
            case "people":
                image = context.getResources().getDrawable(R.drawable.peoplecat);
                break;
            case "brand":
            case "brands":
                image = context.getResources().getDrawable(R.drawable.brandscat);
                break;
            case "business":
                image = context.getResources().getDrawable(R.drawable.businesscat);
                break;
            case "nature":
                image = context.getResources().getDrawable(R.drawable.naturecat);
                break;
            case "health":
                image = context.getResources().getDrawable(R.drawable.healthcat);
                break;
            case "food":
                image = context.getResources().getDrawable(R.drawable.foodcat);
                break;
            case "association":
            case "associations":
                image = context.getResources().getDrawable(R.drawable.associationscat);
                break;
            case "home":
                image = context.getResources().getDrawable(R.drawable.homecat);
                break;
            case "science":
                image = context.getResources().getDrawable(R.drawable.sciencecat);
                break;
            case "religion":
                image = context.getResources().getDrawable(R.drawable.religioncat);
                break;
            case "sports":
                image = context.getResources().getDrawable(R.drawable.sportscat);
                break;
            case "lifestyle":
                image = context.getResources().getDrawable(R.drawable.lifestylecat);
                break;
            case "technology":
                image = context.getResources().getDrawable(R.drawable.technologycat);
                break;
            case "fashion":
                image = context.getResources().getDrawable(R.drawable.fashioncat);
                break;
            case "education":
                image = context.getResources().getDrawable(R.drawable.educationcat);
                break;
            case "games":
                image = context.getResources().getDrawable(R.drawable.gamescat);
                break;
            case "travel":
                image = context.getResources().getDrawable(R.drawable.travelcat);
                break;
            //case "Land": image = context.getResources().getDrawable(R.drawable.land); break;
            case "institutional":
                image = context.getResources().getDrawable(R.drawable.governmentcat);
                break;
            case "beauty":
                image = context.getResources().getDrawable(R.drawable.beautycat);
                break;
            case "culture":
                image = context.getResources().getDrawable(R.drawable.culturecat);
                break;
            case "finance":
                image = context.getResources().getDrawable(R.drawable.financecat);
                break;
        }

        return image;
    }

    public static Drawable getIconImage(Context context, String type) {
        Drawable image = null;

        switch (type) {
            case "People":
                image = context.getResources().getDrawable(R.drawable.people);
                break;
            case "Brands":
                image = context.getResources().getDrawable(R.drawable.brands);
                break;
            case "Business":
                image = context.getResources().getDrawable(R.drawable.business);
                break;
            case "Nature":
                image = context.getResources().getDrawable(R.drawable.nature);
                break;
            case "Health":
                image = context.getResources().getDrawable(R.drawable.health);
                break;
            case "Food":
                image = context.getResources().getDrawable(R.drawable.food);
                break;
            case "Associations":
                image = context.getResources().getDrawable(R.drawable.associations);
                break;
            case "Home":
                image = context.getResources().getDrawable(R.drawable.home);
                break;
            case "Science":
                image = context.getResources().getDrawable(R.drawable.science);
                break;
            case "Religion":
                image = context.getResources().getDrawable(R.drawable.religion);
                break;
            case "Sports":
                image = context.getResources().getDrawable(R.drawable.sports);
                break;
            case "Lifestyle":
                image = context.getResources().getDrawable(R.drawable.lifestyle);
                break;
            case "Technology":
                image = context.getResources().getDrawable(R.drawable.tech);
                break;
            case "Fashion":
                image = context.getResources().getDrawable(R.drawable.fashion);
                break;
            case "Education":
                image = context.getResources().getDrawable(R.drawable.education);
                break;
            case "Games":
                image = context.getResources().getDrawable(R.drawable.games);
                break;
            case "Travel":
                image = context.getResources().getDrawable(R.drawable.travel);
                break;
            //case "Land": image = context.getResources().getDrawable(R.drawable.land); break;
            case "Institutional":
                image = context.getResources().getDrawable(R.drawable.gov);
                break;
            case "Beauty":
                image = context.getResources().getDrawable(R.drawable.beauty);
                break;
            case "Culture":
                image = context.getResources().getDrawable(R.drawable.culture);
                break;
            case "Finance":
                image = context.getResources().getDrawable(R.drawable.finance);
                break;
        }

        return image;
    }

    public static Drawable getIconImageGrey(Context context, String type) {
        Drawable image = null;

        switch (type) {
            case "People":
                image = context.getResources().getDrawable(R.drawable.cat_people);
                break;
            case "Brands":
                image = context.getResources().getDrawable(R.drawable.cat_brands);
                break;
            case "Business":
                image = context.getResources().getDrawable(R.drawable.cat_business);
                break;
            case "Nature":
                image = context.getResources().getDrawable(R.drawable.cat_nature);
                break;
            case "Health":
                image = context.getResources().getDrawable(R.drawable.cat_health);
                break;
            case "Food":
                image = context.getResources().getDrawable(R.drawable.cat_food);
                break;
            case "Associations":
                image = context.getResources().getDrawable(R.drawable.cat_associations);
                break;
            case "Home":
                image = context.getResources().getDrawable(R.drawable.cat_home);
                break;
            case "Science":
                image = context.getResources().getDrawable(R.drawable.cat_science);
                break;
            case "Religion":
                image = context.getResources().getDrawable(R.drawable.cat_religion);
                break;
            case "Sports":
                image = context.getResources().getDrawable(R.drawable.cat_sports);
                break;
            case "Lifestyle":
                image = context.getResources().getDrawable(R.drawable.cat_lifestyle);
                break;
            case "Technology":
                image = context.getResources().getDrawable(R.drawable.cat_tech);
                break;
            case "Fashion":
                image = context.getResources().getDrawable(R.drawable.cat_fashion);
                break;
            case "Education":
                image = context.getResources().getDrawable(R.drawable.cat_education);
                break;
            case "Games":
                image = context.getResources().getDrawable(R.drawable.cat_games);
                break;
            case "Travel":
                image = context.getResources().getDrawable(R.drawable.cat_travel);
                break;
            //case "Land": image = context.getResources().getDrawable(R.drawable.land); break;
            case "Institutional":
                image = context.getResources().getDrawable(R.drawable.cat_governament);
                break;
            case "Beauty":
                image = context.getResources().getDrawable(R.drawable.cat_beauty);
                break;
            case "Culture":
                image = context.getResources().getDrawable(R.drawable.cat_culture);
                break;
            case "Finance":
                image = context.getResources().getDrawable(R.drawable.cat_finance);
                break;
        }

        return image;
    }

    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    public static int getBackgroundThanksColor(Context context, String thanksType) {
        int color = 0;

        switch (thanksType) {
            case "NORMAL":
                color = context.getResources().getColor(R.color.colorPrimary);
                break;
            case "SUPER":
                color = context.getResources().getColor(R.color.amber);
                break;
            case "MEGA":
                color = context.getResources().getColor(R.color.blue);
                break;
            case "POWER":
                color = context.getResources().getColor(R.color.indigo);
                break;
            default:
                break;
        }

        return color;
    }

    public static Drawable getThanksButtonBackground(Context context, String thanksType) {
        Drawable result = null;

        switch (thanksType.toUpperCase()) {
            case "NORMAL":
                result = context.getResources().getDrawable(R.drawable.button_rounded_green);
                break;
            case "SUPER":
                result = context.getResources().getDrawable(R.drawable.button_super_thanks);
                break;
            case "MEGA":
                result = context.getResources().getDrawable(R.drawable.button_mega_thanks);
                break;
            case "POWER":
                result = context.getResources().getDrawable(R.drawable.button_power_thanks);
                break;
            case "ULTRA":
                result = context.getResources().getDrawable(R.drawable.button_rounded_indigo);
                break;
            default: result = context.getResources().getDrawable(R.drawable.button_rounded_grey);
                break;
        }

        return result;
    }

    public static String pickWelcomeColor(Thanks thanks) {
        String result = "";

        switch (thanks.getThanksType().toUpperCase()) {
            case "NORMAL":
                result = "#4CAF50";
                break;
            case "SUPER":
                result = "#28a9e0";
                break;
            case "MEGA":
                result = "#1d7ba3";
                break;
            case "POWER":
                result = "#165e7d";
                break;
            case "ULTRA":
                result = "#104157";
                break;
        }

        return result;
    }

    public static String getCelebrationAnimation(Thanks thanks) {
        String gifUrl = "";

        switch (thanks.getThanksType()) {
            case "NORMAL":
                gifUrl = "https://firebasestorage.googleapis.com/v0/b/thanker-b301f.appspot.com/o/thankyou3.png?alt=media&token=63d26151-da2e-462b-9e07-3a08770de6da";
                break;
            case "SUPER":
                gifUrl = "https://firebasestorage.googleapis.com/v0/b/thanker-b301f.appspot.com/o/thankyou3.png?alt=media&token=63d26151-da2e-462b-9e07-3a08770de6da";
                break;
            case "MEGA":
                gifUrl = "https://firebasestorage.googleapis.com/v0/b/thanker-b301f.appspot.com/o/thankyou3.png?alt=media&token=63d26151-da2e-462b-9e07-3a08770de6da";
                break;
            case "POWER":
                gifUrl = "https://firebasestorage.googleapis.com/v0/b/thanker-b301f.appspot.com/o/thankyou3.png?alt=media&token=63d26151-da2e-462b-9e07-3a08770de6da";
                break;
            case "ULTRA":
                gifUrl = "https://firebasestorage.googleapis.com/v0/b/thanker-b301f.appspot.com/o/thankyou3.png?alt=media&token=63d26151-da2e-462b-9e07-3a08770de6da";
                break;
        }

        return gifUrl;
    }

    public static Drawable getThanksAnimation(Context context, Thanks thanks) {
        Drawable result = null;

        switch(thanks.getThanksType().toLowerCase()){
            case "normal": result = context.getResources().getDrawable(R.drawable.animatethanks); break;
            case "super": result = context.getResources().getDrawable(R.drawable.animatesuper); break;
            case "mega": result = context.getResources().getDrawable(R.drawable.animatemega); break;
            case "power": result = context.getResources().getDrawable(R.drawable.animatepower); break;
            case "ultra": result = context.getResources().getDrawable(R.drawable.animateultra); break;
        }

        return result;
    }
}
