package co.thanker.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import co.thanker.PremiumActivityNo;
import co.thanker.R;
import co.thanker.data.PremiumData;
import co.thanker.data.ThanksData;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;
import co.thanker.utils.Utils;

public class PremiumFragmentYes extends Fragment {

    private final String TAG = "PremiumFragmentYes";
    private final String TIME = "time";
    private final String INFO_HAS_PREMIUM = "info-has-premium";
    private final String DB_REFERENCE = "users";
    private final String THANKS_DATA = "thanks-data";
    private final String PREMIUM_REFERENCE = "premium-info";
    private final static String SUBSCRIPTION_PRICE_EURO = "5";
    private final static String PAYPAL_MERCHANT_ID = "FQU8T77VZHRTW";
    private final String PLATFORM_MESSAGE = "platform-message";
    private final static String PAYPAL_ID = "AUbsCpJu9zIZ-qM0g8wjgdejDpKddr6FlAfxEARt4ZvfNI8y9kdJ3MAa0B57YiVC2npH1efNIxkanuZJ";
    private final String PREFS_EARNED_THANKERS = "prefs-earned-thankers";
    private final String NUMBER_THANKERS_STRING = "number-thankers";
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 11;
    private static final String STRIPE_KEY = PremiumActivityNo.STRIPE_KEY;
    private final static String STRIPE_KEY_TEST = PremiumActivityNo.STRIPE_KEY_TEST;
    private final int NUMBER_THANKERS = 150;
    private final int EXTRA_THANKERS = 5;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DocumentReference mPremiumRef;
    private DatabaseReference mTimeRef;

    private boolean mHasPremium;
    private boolean mHasCollectedToday;
    private long mEntryDate;
    private Timestamp mLastThankersClaim;
    private long mConsecutiveRenewals;
    private long mExpiryDate;
    private String mUserId;
    private long mCurrentTime;
    private long mSubscriptionTime;
    private long mSubscriptionTemporary;
    private String mPriceString;
    private int mCountClicks = 0;
    private long mThankersToCollect;
    private Button mButtonCollect;
    private Button mRenewButton;
    private TextView mPaymentStatus;
    private TextView mThankersView;
    private TextView mTextLabel;
    private TextView mTextClickToSeeExtensions;
    private TextView mTextNewDate;
    private TextView mTextTodayThankers;
    private ImageView mGifCoins;
    private ImageView mImagePremium;
    private ImageView mAnimationView;
    private LinearLayout mLinearPremiumLabel;
    private int mMonths;
    private String mTypePremium;
    private SharedPreferences mPrefsThankers;
    private SharedPreferences.Editor mEditorPrefs;
    private long mTodayThankers;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_premium_yes, container, false);

        SpannableString s = new SpannableString("Thanker Premium");

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(s);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mPremiumRef = mFirestore.collection(PREMIUM_REFERENCE).document(mAuth.getCurrentUser().getUid());
        mUserId = mAuth.getCurrentUser().getUid();
        mCountClicks = 0;
        mHasCollectedToday = false;

        mPrefsThankers = getActivity().getSharedPreferences(PREFS_EARNED_THANKERS + mUserId, Context.MODE_PRIVATE);
        mEditorPrefs = mPrefsThankers.edit();
        mTodayThankers = mPrefsThankers.getLong(NUMBER_THANKERS_STRING, 0);

        if (getArguments() != null) {
            mHasPremium = getArguments().getBoolean(INFO_HAS_PREMIUM, false);
        } else {
            mHasPremium = false;
        }

        Log.v(TAG, "Checking PremiumData. This user is PremiumData: " + mHasPremium);

        if (savedInstanceState != null) {
            mCountClicks = savedInstanceState.getInt("clicks");
            mThankersToCollect = savedInstanceState.getLong("thankers-to-collect");
        }

        if (mHasPremium) {
            mButtonCollect = (Button) view.findViewById(R.id.button_earn);
            mThankersView = (TextView) view.findViewById(R.id.text_earned_thankers);
            mTextLabel = (TextView) view.findViewById(R.id.text_earned_thankers_label);
            mTextTodayThankers = (TextView) view.findViewById(R.id.text_today_thankers);
            mTextClickToSeeExtensions = (TextView) view.findViewById(R.id.text_click_plans);
            mTextNewDate = (TextView) view.findViewById(R.id.text_subscription_new_date);
            mGifCoins = (ImageView) view.findViewById(R.id.gif_premium_coins);
            mImagePremium = (ImageView) view.findViewById(R.id.image_premium);
            mAnimationView = view.findViewById(R.id.animation_premium);
            mRenewButton = (Button) view.findViewById(R.id.button_google);
            mLinearPremiumLabel = view.findViewById(R.id.linear_premium);
            mTextClickToSeeExtensions.setVisibility(View.GONE);
            Glide.with(this).load(R.drawable.precoinsgif).into(mGifCoins);

            mTextClickToSeeExtensions.setPaintFlags(mTextClickToSeeExtensions.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


                //  }
        } else {

            mPaymentStatus = view.findViewById(R.id.text_payment_status);
        }

        initPremium();

        return view;
    }

    public void initPremium(){
        if(mHasPremium){
            mFirestore.collection(PREMIUM_REFERENCE).document(mUserId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            PremiumData premium;
                            if(documentSnapshot.exists()){
                                premium = documentSnapshot.toObject(PremiumData.class);
                                mFirestore.collection("time").document("now")
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if(documentSnapshot.exists()){
                                                    Timestamp timestamp = (Timestamp) documentSnapshot.getData().get("time");
                                                    if(timestamp != null){
                                                        Date nowTime = timestamp.toDate();
                                                        mEntryDate = premium.getInitialOrRenewalDate();
                                                        mLastThankersClaim = premium.getLastTimeThankersClaimed();
                                                        long lastClaimed;
                                                        if(mLastThankersClaim != null){
                                                            lastClaimed = mLastThankersClaim.toDate().getTime();
                                                        }
                                                        else {
                                                            lastClaimed = 0;
                                                        }

                                                        Log.v(TAG, "Timestamp last claimed in millis: " + lastClaimed);

                                                        mConsecutiveRenewals = premium.getConsecutiveRenewals();
                                                        mTextNewDate.setText(DataUtils.getDateString(getActivity(), mSubscriptionTemporary));

                                                        Date thenDate;

                                                        if (lastClaimed == 0) {
                                                            thenDate = new Date(mEntryDate);
                                                        } else {
                                                            thenDate = new Date(lastClaimed);
                                                        }

                                                        Date nowDate = new Date(nowTime.getTime());

                                                        Calendar todayCalendar = Calendar.getInstance();
                                                        Calendar thenCalendar = Calendar.getInstance();
                                                        todayCalendar.setTime(nowDate);
                                                        thenCalendar.setTime(thenDate);

                                                        todayCalendar.set(Calendar.HOUR_OF_DAY, 12);
                                                        thenCalendar.set(Calendar.HOUR_OF_DAY, 11);

                                                        long msDiff = todayCalendar.getTimeInMillis() - thenCalendar.getTimeInMillis();
                                                        long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);

                                                        long numberThankers = NUMBER_THANKERS * daysDiff + (mConsecutiveRenewals * EXTRA_THANKERS /** daysDiff*/);

                                                        if (lastClaimed == 0) {
                                                            numberThankers = NUMBER_THANKERS * (daysDiff + 1) + (mConsecutiveRenewals * EXTRA_THANKERS);
                                                        }

                                                        mThankersToCollect = numberThankers;
                                                        String message;

                                                        Log.v(TAG, "Premium Stuff, yeah. DaysDiff: " + daysDiff);

                                                        if ((daysDiff == 0) && lastClaimed != 0) {
                                                            mButtonCollect.setText(getString(R.string.click_tomorrow));
                                                            mButtonCollect.setEnabled(false);
                                                            mButtonCollect.setClickable(false);
                                                            mButtonCollect.setVisibility(View.GONE);
                                                            mLinearPremiumLabel.setVisibility(View.VISIBLE);
                                                            mImagePremium.setVisibility(View.GONE);
                                                            int color = Color.parseColor("#4CAF50"); //The color u want
                                                            mImagePremium.setColorFilter(color);
                                                            Glide.with(getActivity()).load(R.drawable.animatepremium).into(mAnimationView);
                                                            message = getString(R.string.come_back_tomorrow_for_more);
                                                            mTextLabel.setText(message);

                                                            mTextTodayThankers.setText(Html.fromHtml(getString(R.string.you_collected_premium_today, mTodayThankers)));
                                                            mThankersView.setVisibility(View.GONE);
                                                            //mTextYouHave.setVisibility(View.GONE);
                                                            mGifCoins.setVisibility(View.GONE);
                                                            mHasCollectedToday = true;
                                                        } else if ((daysDiff > 0 || lastClaimed == 0)) {

                                                            mTextTodayThankers.setVisibility(View.VISIBLE);
                                                            mGifCoins.setVisibility(View.VISIBLE);
                                                            mThankersView.setVisibility(View.VISIBLE);
                                                            mTextLabel.setVisibility(View.VISIBLE);
                                                            mButtonCollect.setVisibility(View.VISIBLE);

                                                            message = getString(R.string.collect_thankers, mThankersToCollect);
                                                            mThankersView.setText(String.valueOf(mThankersToCollect));

                                                            mButtonCollect.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {

                                                                    mFirestore.collection(THANKS_DATA).document(mAuth.getCurrentUser().getUid())
                                                                            .get()
                                                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                    if(documentSnapshot.exists()){
                                                                                        ThanksData data = documentSnapshot.toObject(ThanksData.class);
                                                                                        long currency = data.getThanksCurrency();
                                                                                        currency += mThankersToCollect;
                                                                                        mFirestore.collection(THANKS_DATA).document(mAuth.getCurrentUser().getUid()).update("thanksCurrency", currency);
                                                                                        mEditorPrefs.putLong(NUMBER_THANKERS_STRING, mThankersToCollect);
                                                                                        mEditorPrefs.commit();
                                                                                        mTodayThankers = mThankersToCollect;
                                                                                        mHasCollectedToday = true;

                                                                                        mTextLabel.setVisibility(View.GONE);
                                                                                        mButtonCollect.setVisibility(View.GONE);
                                                                                        mThankersView.setText(getString(R.string.collected));

                                                                                        String title = getString(R.string.you_collected_premium, mThankersToCollect);
                                                                                        String body = getString(R.string.you_collected_premium_text, mThankersToCollect);

                                                                                        DataUtils.createMessage(mUserId, title, body, PLATFORM_MESSAGE, 0);

                                                                                        Glide.with(getActivity()).load(R.drawable.receivecoinsgif).into(mGifCoins);

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                mTextLabel.setText(getString(R.string.come_back_tomorrow_for_more));
                                                                                                mButtonCollect.setVisibility(View.GONE);
                                                                                                //mPremiumLabel.setVisibility(View.VISIBLE);
                                                                                                mThankersView.setVisibility(View.GONE);
                                                                                                //mTextYouHave.setVisibility(View.GONE);

                                                                                                mPremiumRef.update("lastTimeThankersClaimed", FieldValue.serverTimestamp());

                                                                                                mButtonCollect.setText(getString(R.string.click_tomorrow));
                                                                                                mButtonCollect.setEnabled(false);
                                                                                                mButtonCollect.setClickable(false);
                                                                                                mButtonCollect.setVisibility(View.GONE);
                                                                                                mLinearPremiumLabel.setVisibility(View.VISIBLE);
                                                                                                int color = Color.parseColor("#4CAF50"); //The color u want
                                                                                                mImagePremium.setColorFilter(color);
                                                                                                Glide.with(getActivity()).load(R.drawable.animatepremium).into(mAnimationView);
                                                                                                String message = getString(R.string.come_back_tomorrow_for_more);
                                                                                                mTextLabel.setText(message);
                                                                                                mTextLabel.setVisibility(View.VISIBLE);
                                                                                                mTextTodayThankers.setText(Html.fromHtml(getString(R.string.you_collected_premium_today, mTodayThankers)));
                                                                                                mThankersView.setVisibility(View.GONE);
                                                                                                //mTextYouHave.setVisibility(View.GONE);
                                                                                                mGifCoins.setVisibility(View.GONE);
                                                                                                mHasCollectedToday = true;
                                                                                            }
                                                                                        }, 4000);

                                                                                    }

                                                                                    Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading our Thanks Data");
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                        }
                                                    }
                                                }

                                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading last registered Timestamp");
                                            }
                                        });
                            }

                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading our Premium info");
                        }
                    });
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("clicks", mCountClicks);
        outState.putString("type-premium-selected", mTypePremium);
        outState.putLong("thankers-to-collect", mThankersToCollect);
        outState.putLong("expiry-date", mExpiryDate);
    }

    @Override
    public void onResume(){
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setSubtitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if(getActivity() != null){
            Utils.changeBarTitle(getActivity(), actionBar, "Thanker Premium");
        }

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}



