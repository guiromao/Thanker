package co.thanker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Random;

import co.thanker.utils.TypefaceSpan;

public class SplashActivity extends Activity {

    private final String TAG = SplashActivity.class.getSimpleName();

    private TextView mTextWelcome;
    private ImageView mImageSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mTextWelcome = findViewById(R.id.text_welcome);
        mImageSplash = findViewById(R.id.image_splash);
        String [] arrayWelcome = getResources().getStringArray(R.array.welcome_messages);
        int randomIndex = new Random().nextInt(arrayWelcome.length);
        Log.v(TAG, "Welcome messages. Array length: " + arrayWelcome.length);

        /*SpannableString message = new SpannableString(arrayWelcome[randomIndex]);
        message.setSpan(new TypefaceSpan(this, "greatwishes.otf"), 0, message.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/

        mTextWelcome.setText(arrayWelcome[randomIndex]);
        Glide.with(this).load(R.drawable.animatesplash).into(mImageSplash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showMainActivity();
            }
        }, 3000);
    }

    private void showMainActivity() {
        Intent intent = new Intent(
                SplashActivity.this, MainActivity.class
        );
        startActivity(intent);
        finish();
    }
}