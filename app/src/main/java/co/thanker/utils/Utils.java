package co.thanker.utils;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import co.thanker.R;
import co.thanker.data.Thanks;

public class Utils {

    private static final String TAG = "Utils";
    private static final String COUNTRIES_REFERENCE = "countries-values";

    public static void hideKeyboardFrom(Activity activity){
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void changeBarTitle(Context context, ActionBar actionBar, String string){
        SpannableString s = new SpannableString(string);
        s.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.defaultTextColor)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(s);
    }

    public static void changeBarSubTitle(Context context, ActionBar actionBar, String string){
        SpannableString s = new SpannableString(string);
        s.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.defaultTextColor)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setSubtitle(s);
    }

    public static View getThanksView(Context context, Thanks thanks) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View thanksView = null;

        switch(thanks.getThanksType()){
            case "NORMAL": thanksView = inflater.inflate(R.layout.custom_fullimage_dialog, null, false); break;
            case "SUPER": thanksView = inflater.inflate(R.layout.custom_fullimage_dialog, null, false); break;
            case "MEGA": thanksView = inflater.inflate(R.layout.custom_fullimage_dialog, null, false); break;
            case "POWER": thanksView = inflater.inflate(R.layout.custom_fullimage_dialog, null, false); break;
            case "ULTRA": thanksView = inflater.inflate(R.layout.custom_fullimage_dialog, null, false); break;

        }

        return thanksView;
    }

}
