package co.thanker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import co.thanker.data.ThanksData;

public class RewardActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private final String TAG = RewardActivity.class.getSimpleName();
    private static final String DB_REFERENCE = "users";
    private final String THANKS_CURRENCY = "thanks-currency";
    private final String USER_ID_STRING = "user-id-string";
    private final String THANKS_DATA = "thanks-data";
    private final String ADS_TEST = "ca-app-pub-3940256099942544/5224354917";
    private final String ADS_PRODUCTION = "ca-app-pub-2260307616934752/7390262712";
    private final String PUB_ID = "ca-app-pub-2260307616934752~5418813293";

    private RewardedVideoAd mAd;
    private FirebaseFirestore mFirestore;
    private DocumentReference mThanksStatsRef;
    private ThanksData mThanksData;
    private LinearLayout mLinearLoading;
    private TextView mTextReward;
    private long mCurrentCurrency;
    private long mTotalCurrency;
    private int mReward;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        getSupportActionBar().hide();
        getSupportActionBar().setTitle("Thanker Rewards");

        mLinearLoading = (LinearLayout) findViewById(R.id.linear_loading);

        MobileAds.initialize(this, PUB_ID);
        // Use an activity context to get the rewarded video instance.
        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);

        mAd.loadAd(ADS_PRODUCTION,
                new AdRequest.Builder().build());

        if (getIntent() != null) {
            mCurrentCurrency = getIntent().getLongExtra(THANKS_CURRENCY, 0);
            mUserId = getIntent().getStringExtra(USER_ID_STRING);
        }

        mFirestore = FirebaseFirestore.getInstance();
        mThanksStatsRef = mFirestore.collection(THANKS_DATA).document(mUserId);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().show();
                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getString(R.string.no_ad_available), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }, 2000);

            }
        }, 7000);
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Log.v(TAG, "Rewards. Vídeo carregado");
        if (mAd.isLoaded()) {
            mAd.show();
            mLinearLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.v(TAG, "Rewards. Vídeo a abrir");
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.v(TAG, "Rewards. Vídeo começou");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.v(TAG, "Rewards. Vídeo fechado");
        Toast rewardToast = Toast.makeText(this, getString(R.string.reward_string, mReward, mTotalCurrency), Toast.LENGTH_LONG);
        mTextReward = (TextView) rewardToast.getView().findViewById(android.R.id.message);
        if (mTextReward != null) {
            mTextReward.setGravity(Gravity.CENTER);
        }
        rewardToast.show();
        finish();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        mReward = rewardItem.getAmount();
        mTotalCurrency = mReward + mCurrentCurrency;
        mThanksStatsRef.update("thanksCurrency", mTotalCurrency);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.v(TAG, "Rewards. Vídeo não completo");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.v(TAG, "Rewards. Vídeo não carregou. Erro: " + i);
    }

    @Override
    public void onRewardedVideoCompleted() {
        Log.v(TAG, "Rewards. Vídeo completo");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onResume() {
        mAd.resume(this);
        super.onResume();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        SpannableString s = new SpannableString("Thanker Rewards");
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.defaultTextColor)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    @Override
    public void onPause() {
        mAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mAd.destroy(this);
        super.onDestroy();
    }

}
