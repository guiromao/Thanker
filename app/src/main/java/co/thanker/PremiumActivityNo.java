package co.thanker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jama.carouselview.CarouselView;
import com.jama.carouselview.CarouselViewListener;
import com.jama.carouselview.enums.IndicatorAnimationType;
import com.jama.carouselview.enums.OffsetType;


import java.util.HashMap;
import java.util.Map;

import co.thanker.data.PremiumData;
import co.thanker.data.ThanksData;
import co.thanker.utils.DataUtils;

/*import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;*/
//import com.stripe.android.model.Token;

public class PremiumActivityNo extends AppCompatActivity /*implements BillingProcessor.IBillingHandler*/{

    private final String TAG = PremiumActivityNo.class.getSimpleName();
    private final String INFO_HAS_PREMIUM = "info-has-premium";
    private final String DB_REFERENCE = "users";
    private final String PREMIUM_REFERENCE = "premium-info";
    private final String THANKS_DATA = "thanks-data";
    private final String LOCATION_ID = "location-id";
    private final static String PAYPAL_ID = "AUbsCpJu9zIZ-qM0g8wjgdejDpKddr6FlAfxEARt4ZvfNI8y9kdJ3MAa0B57YiVC2npH1efNIxkanuZJ";
    private final String PLATFORM_MESSAGE = "platform-message";
    public final static String STRIPE_KEY = "pk_live_Vr6jM5nhEHguxB6r3L3d1m9b00HSQZkDgS";
    public final static String STRIPE_KEY_TEST = "pk_test_knLtrfKeKTdZOOgL4HUDf3R100zsc5PaH5";
    private static final String SUBSCRIPTION_ID = "thankerpremium";
    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA25pInp1cwHmis++7Ig0gDQlg8VQjbVe9sCVtCCPmJcNeTdvOclXnHJU2aUkpmcYEsiw9NbNN3+hAPusEOYHd4S9U7HlAhXnf0sSgaaMex3YJmvK0NF9XKpqivaMu+CXgc0iRubpjcHble2xOaKhsEekljkXAYuIFjiJ+bprhdentVevZRG2DH6tc6QP5kmP4S+rq0iIkeIVfrlfBY/ovbfoCRbSRbbh4+cRw1OjVxd3cJP9bUtAe2hcR2pQkpiCrPBTFlVW5Kvm1czbZErmF4ffFqXDwDQjdxSYVj7MSY3dGdgoNbvAnMz7KhKAuPyCDsHIF0Rpgsf1kX+30B5cpHQIDAQAB";
    private static final String MERCHANT_ID = "BCR2DN6TVPF2VHYL";
    private final String PREFS_EARNED_THANKERS = "prefs-earned-thankers";
    private final String NUMBER_THANKERS_STRING = "number-thankers";
    private final static String SUBSCRIPTION_PRICE_EURO = "5";
    private final long ONE_MONTH_MILLIS = 2592000000L;
    private final long SIX_MONTHS_MILLIS = 15552000000L;
    private final long TWELVE_MONTHS_MILLIS = 31104000000L;
    private final int NUMBER_THANKERS = 150;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DocumentReference mPremiumRef;
    private PaymentsClient mPaymentsClient;

    private boolean mHasPremium;
    private int mCountClicks;
    private Activity mActivity;

    private View mView;
    private Button mPaymentButton;
    private Button mButtonCheck;
    private Button mGooglePayButton;
    private TextView mPaymentStatus;
    private TextView mTextDescription;
    private RadioGroup mRadioPlans;
    private LinearLayout mLinearSubscribe;
    private LinearLayout mLinearFirstCollect;
    private ImageView mImageCoins;
    private ImageView mImagePremium;
    private ImageView mAnimationView;
    private TextView mTextEarned;
    private TextView mTextEarnedToday;
    private TextView mTextEarnedLabel;
    private TextView mTextComeTomorrow;
    private TextView mTextCongrats;
    private TextView mTextPickPlan;
    private Button mButtonEarned;
    private CardView mCardOneMonth;
    private CardView mCardSixMonths;
    private CardView mCardTwelveMonths;
    private FrameLayout mFrameOne;
    private FrameLayout mFrameSix;
    private FrameLayout mFrameTwelve;
    private LinearLayout mLinearPremiumPlans;
    private LinearLayout mLinearComeTomorrow;
    private int mMonths;
    private String mTypePremium;
    private boolean mHasOpenedPlans;
    private boolean mHasPaid;
    private boolean mHasCollectedThankers;
    private boolean mReadyToPurchase;
    private boolean mHasSubscribed;

    //private View mGooglePayButton;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 11;
    private TextView mGooglePayStatusText;

    private CarouselView mCarousel;
    private BillingProcessor mBillingProcessor;
    private SharedPreferences mPrefsThankers;
    private SharedPreferences.Editor mEditorPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View view;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_no);

        mActivity = this;
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mPremiumRef = mFirestore.collection(PREMIUM_REFERENCE).document(mAuth.getCurrentUser().getUid());

        mHasOpenedPlans = false;
        mHasPaid = false;
        mHasCollectedThankers = false;
        mCountClicks = 0;
        mReadyToPurchase = false;
        mHasSubscribed = false;

        if (getIntent() != null) {
            mHasPremium = getIntent().getBooleanExtra(INFO_HAS_PREMIUM, false);
        } else {
            mHasPremium = false;
        }

        if (savedInstanceState != null) {
            mCountClicks = savedInstanceState.getInt("clicks");
            mHasSubscribed = savedInstanceState.getBoolean("has-subscribed");
        }

        Log.v(TAG, "Checking PremiumData. This user is PremiumData: " + mHasPremium);

        mPrefsThankers = getSharedPreferences(PREFS_EARNED_THANKERS + mAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
        mEditorPrefs = mPrefsThankers.edit();

        //mPaymentButton = findViewById(R.id.button_subscribe);
        mPaymentStatus = findViewById(R.id.text_payment_status);
        mButtonCheck = (Button) findViewById(R.id.button_check);
        //mRadioPlans = (RadioGroup) findViewById(R.id.radio_subscription_type);
        mGooglePayButton = (Button) findViewById(R.id.button_google);
        mLinearSubscribe = (LinearLayout) findViewById(R.id.linear_to_subscribe);
        mLinearFirstCollect = (LinearLayout) findViewById(R.id.linear_first_collect);
        mImageCoins = (ImageView) findViewById(R.id.gif_premium_coins);
        mImagePremium = (ImageView) findViewById(R.id.image_premium);
        mAnimationView = findViewById(R.id.animation_premium);
        mTextEarned = (TextView) findViewById(R.id.text_earned_thankers);
        mTextEarnedToday = findViewById(R.id.text_you_earned_today);
        mTextEarnedLabel = (TextView) findViewById(R.id.text_earned_thankers_label);
        mTextComeTomorrow = (TextView) findViewById(R.id.text_come_tomorrow);
        mTextPickPlan = (TextView) findViewById(R.id.text_pick_plan);
        mTextCongrats = (TextView) findViewById(R.id.text_congrats);
        mButtonEarned = (Button) findViewById(R.id.button_earn);
        mCardOneMonth = (CardView) findViewById(R.id.cardview_plan_1);
        mCardSixMonths = (CardView) findViewById(R.id.cardview_plan_6);
        mCardTwelveMonths = (CardView) findViewById(R.id.cardview_plan_12);
        mLinearPremiumPlans = (LinearLayout) findViewById(R.id.linear_premium_plans);
        mLinearComeTomorrow = (LinearLayout) findViewById(R.id.linear_come_tomorrow);
        mFrameOne = (FrameLayout) findViewById(R.id.frame_1);
        mFrameSix = (FrameLayout) findViewById(R.id.frame_6);
        mFrameTwelve = (FrameLayout) findViewById(R.id.frame_12);
        //mTextDescription = (TextView) findViewById(R.id.text_description);
        //mTextDescription.setText(Html.fromHtml(getString(R.string.promotional_premium_description)));


        if(!BillingProcessor.isIabServiceAvailable(this)) {
            Log.v(TAG, "In-App. Premium, BillingProcessor Not Available!");
            //Toast.makeText(this,"In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16", Toast.LENGTH_SHORT).show();
        }

        mBillingProcessor = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                //showToast("onProductPurchased: " + productId);
                Log.v(TAG, "In-App. Premium purchased and handled in onProductPurchased!");
                handleSuccessPayment();
            }
            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                //showToast("onBillingError: " + Integer.toString(errorCode));
            }
            @Override
            public void onBillingInitialized() {
                //showToast("onBillingInitialized");
                mReadyToPurchase = true;
                Log.v(TAG, "In-App. Premium. Billing initialized");

            }
            @Override
            public void onPurchaseHistoryRestored() {
                //showToast("onPurchaseHistoryRestored");
                Log.v(TAG, "In-App. Premium purchased and handled in onPurchaseHistoryRestored!");
                for(String sku : mBillingProcessor.listOwnedProducts())
                    Log.v(TAG, "Owned Managed Product: " + sku);
                for(String sku : mBillingProcessor.listOwnedSubscriptions()){
                    Log.v(TAG, "Owned Subscription: " + sku);
                }
            }
        });

        if(mHasSubscribed){
            mButtonCheck.setVisibility(View.GONE);
            mCarousel.setVisibility(View.GONE);
            showPremiumScreen();
        }

        else{
            mButtonCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mBillingProcessor.isSubscriptionUpdateSupported()){
                        if(mReadyToPurchase){
                            mBillingProcessor.subscribe(mActivity, SUBSCRIPTION_ID);
                        }
                    }
                }
            });

            mCarousel = findViewById(R.id.carousel);
            final int[] arrayImages = {R.drawable.megathanks, R.drawable.welcomewink, R.drawable.barchart, R.drawable.logot, };
            final String[] arrayTitles = getResources().getStringArray(R.array.premium_titles);
            final String[] arraySubtitles = getResources().getStringArray(R.array.premium_subtitles);

            mCarousel.setSize(3);
            mCarousel.setResource(R.layout.item_carousel_premium);
            //mCarousel.setAutoPlay(true);
            mCarousel.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
            mCarousel.setCarouselOffset(OffsetType.CENTER);
            mCarousel.setCarouselViewListener(new CarouselViewListener() {
                @Override
                public void onBindView(View view, int position) {
                    // Example here is setting up a full image carousel
                    TextView textView = view.findViewById(R.id.text_carousel_two);
                    textView.setText(arrayTitles[position]);

                    ImageView imageView = view.findViewById(R.id.image_carousel);
                    imageView.setImageDrawable(getResources().getDrawable(arrayImages[position]));

                    if(arrayImages[position] == R.drawable.welcomewink || arrayImages[position] == R.drawable.barchart){
                        int color = Color.parseColor("#4CAF50"); //The color u want
                        imageView.setColorFilter(color);
                    }

                    TextView subtitleView = view.findViewById(R.id.carousel_subtitle);
                    subtitleView.setText(arraySubtitles[position]);
                }
            });
            // After you finish setting up, show the CarouselView
            mCarousel.show();
        }

        mButtonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBillingProcessor.isSubscriptionUpdateSupported()){
                    if(mReadyToPurchase){
                        mBillingProcessor.subscribe(mActivity, SUBSCRIPTION_ID);
                    }
                }
            }
        });

    }

    public void handleSuccessPayment() {
        //mRadioPlans.setVisibility(View.GONE);
        PremiumData premiumData = new PremiumData(Long.valueOf(System.currentTimeMillis())/*, mSubscriptionTime*/);
        //mFirestore.getReference().child(DB_REFERENCE).child(mAuth.getCurrentUser().getUid()).child("isPremium").setValue(true);
        mPremiumRef.set(premiumData);
        mPaymentStatus.setText(R.string.payments_approved);

        Map<String, Object> timeMap = new HashMap<>();
        timeMap.put("time", FieldValue.serverTimestamp());
        mFirestore.collection("time").document("now").set(timeMap);

        String title = getString(R.string.title_activate_premium);
        String body = getString(R.string.body_activate_premium);
        DataUtils.createMessage(mAuth.getCurrentUser().getUid(), title, body, PLATFORM_MESSAGE, 0);

        //mGooglePayButton.setEnabled(false);

        mLinearSubscribe.setVisibility(View.GONE);
        mLinearFirstCollect.setVisibility(View.VISIBLE);
        Glide.with(this).load(R.drawable.precoinsgif).into(mImageCoins);
        mTextEarned.setText(String.valueOf(NUMBER_THANKERS));

        mButtonEarned.setOnClickListener(new View.OnClickListener() {
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
                                    currency += NUMBER_THANKERS;
                                    mFirestore.collection(THANKS_DATA).document(mAuth.getCurrentUser().getUid()).update("thanksCurrency", currency);
                                    mEditorPrefs.putLong(NUMBER_THANKERS_STRING, NUMBER_THANKERS);
                                    mEditorPrefs.commit();

                                    mTextEarnedLabel.setVisibility(View.GONE);
                                    mButtonEarned.setVisibility(View.GONE);
                                    mTextEarned.setText(getString(R.string.collected));

                                    String title = getString(R.string.you_collected_premium, NUMBER_THANKERS);
                                    String body = getString(R.string.you_collected_premium_text, NUMBER_THANKERS);

                                    DataUtils.createMessage(mAuth.getCurrentUser().getUid(), title, body, PLATFORM_MESSAGE, 0);

                                    Glide.with(getApplicationContext()).load(R.drawable.receivecoinsgif).into(mImageCoins);

                                    mHasSubscribed = true;

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //mTextEarnedLabel.setVisibility(View.VISIBLE);
                                            showPremiumScreen();
                                        }
                                    }, 4000);
                                }

                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading our Thanks Data");
                            }
                        });
            }
        });
    }

    public void showPremiumScreen(){
        mTextEarnedLabel.setText(getString(R.string.come_back_tomorrow_for_more));
        mButtonEarned.setVisibility(View.GONE);
        //mPremiumLabel.setVisibility(View.VISIBLE);
        mTextEarned.setVisibility(View.GONE);
        mTextEarnedToday.setText(Html.fromHtml(getString(R.string.you_collected_premium_today, 150)));
        //mTextYouHave.setVisibility(View.GONE);

        mPremiumRef.update("lastTimeThankersClaimed", FieldValue.serverTimestamp());
        mImageCoins.setVisibility(View.GONE);

        int color = Color.parseColor("#4CAF50"); //The color u want
        mImagePremium.setColorFilter(color);

        Glide.with(getApplicationContext()).load(R.drawable.animatepremium).into(mAnimationView);

        mLinearComeTomorrow.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if (!mBillingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            Log.v(TAG, "In-App. Premium purchased and handled in onActivityResult!. With requestCode: " + requestCode + ". And resultCode: " + resultCode);
            if(resultCode == -1){
                handleSuccessPayment();
            }
       // }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("clicks", mCountClicks);
        outState.putString("type-premium-selected", mTypePremium);
        outState.putBoolean("has-opened-plans", mHasOpenedPlans);
        outState.putBoolean("has-paid", mHasPaid);
        outState.putBoolean("has-collected-thankers", mHasCollectedThankers);
        outState.putBoolean("has-subscribed", mHasSubscribed);
    }

    @Override
    public void onResume() {
        super.onResume();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        SpannableString s = new SpannableString("Thanker Premium");
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.defaultTextColor)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
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


