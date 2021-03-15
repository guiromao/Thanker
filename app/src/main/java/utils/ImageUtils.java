package utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageUtils {

    public static void loadImageInto(Context context, String url, ImageView imageView){
        if(context != null) {
            Glide.with(context)
                    .load(url)
                    .into(imageView);
        }
    }

}
