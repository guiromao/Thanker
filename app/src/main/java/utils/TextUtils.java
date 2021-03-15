package utils;

import android.content.Context;

import co.thanker.R;

public class TextUtils {

    public TextUtils(){

    }

    public static boolean isEmail(Context context, String text){
        return(
                (text.contains(".com")
                || text.contains(".net")
                || text.contains(".org")
                || text.contains(".xyz")
                || text.contains(".io")
                || text.contains(".pt")
                || text.contains(".es")
                || text.contains(".co")
                || text.contains(".us")
                || text.contains(".ru")
                || text.contains(".br")
                || text.contains(".cn")
                || text.contains(".uk"))
                && text.contains(context.getString(R.string.at))
                );
    }

}
