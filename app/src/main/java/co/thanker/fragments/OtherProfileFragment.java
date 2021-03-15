package co.thanker.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.annotation.Nullable;

import co.thanker.InviteActivity;
import co.thanker.PersonalStatsActivity;
import co.thanker.R;
import co.thanker.data.FriendRank;
import co.thanker.data.FriendRequest;
import co.thanker.data.StatsThanks;
import co.thanker.data.Thanks;
import co.thanker.data.ThanksData;
import co.thanker.data.ThanksItem;
import co.thanker.data.ThanksValue;
import co.thanker.data.TopItem;
import co.thanker.data.User;
import co.thanker.data.UserSnippet;
import co.thanker.data.UserValue;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;
import co.thanker.utils.MultiColorCircle;
import co.thanker.utils.TextUtils;
import co.thanker.utils.Utils;

//import com.github.mikephil.charting.components.Description;
//import com.github.mikephil.charting.data.PieEntry;
//import com.github.mikephil.charting.formatter.IAxisValueFormatter;
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
//import com.github.mikephil.charting.co.thanker.utils.MPPointF;

public class OtherProfileFragment extends Fragment {

    private final String TAG = "OtherProfileFragment";
    private final String THANKS_DB = "thanks-db";
    private final String THANKS_DATA = "thanks-data";
    private static final String DB_REFERENCE = "users";
    private final String TOP_REF = "tops";
    private final String OUR_USER_ID = "our-user-id";
    private final String USER_SNIPPET = "user-snippet";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String USER_OBJECT = "user-object";
    private final String OTHER_USER_OBJECT = "other-user-object";
    private static final String THANKS_REFERENCE = "thanks-test";
    private final String COUNTRIES_REFERENCE = "countries-values";
    private final String STATS_THANKS = "stats-thanks";
    private final String THANKS_GIVEN = "thanks-given";
    private final String THANKS_RECEIVED = "thanks-received";
    private final String FRIENDS_DB = "friends-db";
    private final String FRIEND_REQUESTS = "friend-requests";
    private final String TOP_USERS_THANKS_RECEIVED = "top-users-thanks-received";
    private final String TOP_USERS_THANKS_GIVEN = "top-users-thanks-given";
    private final String USERS_THANKS_VALUES = "users-thanks-values";
    private final String LIST_THANKS_VALUES = "list-thanks-values";
    private final String USER_ID_STRING = "user-id-string";
    private final String LISTING_TYPE = "listing-type";
    private final String TO_USER = "toUserId";
    private final String FROM_USER = "fromUserId";
    private final String TO_USER_FROM_USER = "fromUserToUser";
    private static final String TYPE_OWN_THANKS = "type-own-thanks";
    private static final String TYPE_COMBINED_THANKS = "type-combined-thanks";
    public static final String FRAGMENT_TYPE = "fragment-type";
    private final String DYNAMIC_GIVER = "dynamic-giver";
    private final String DYNAMIC_RECEIVER = "dynamic-receiver";
    private final String THANKS_DYNAMIC = "thanks-dynamic";
    private final String EXCHANGE_THANKS = "exchange-thanks";
    private final String OTHER_USER_THANKS = "other-user-thanks";
    private final String CONTINUE_SENDING_ID = "continue-sending-user-id";
    private final String PLATFORM_MESSAGE = "platform-message";
    private final String FOUND_USER = "found-user";
    private final String ACTIVATED_THANKS = "activated-thanks";
    private final String TYPE_THANKS = "NORMAL";
    private final String TYPE_SUPER = "SUPER";
    private final String TYPE_MEGA = "MEGA";
    private final String TYPE_POWER = "POWER";
    private final String TYPE_ULTRA = "ULTRA";
    private final String TYPE_SAVED = "type-thanks-saved";

    private final int FRAGMENT_OTHER_PROFILE = 2;
    private final int USER_READ_THRESHOLD = 5;
    private final int FIVE_DAYS = 5;
    private final int MAX_RECENT_INDEX = 4;
    private final int MAX_TOP_RANK = 10;
    private final int THANKS_VALUE = 1;
    private final int SUPER_THANKS_VALUE = 10;
    private final int MEGA_THANKS_VALUE = 100;
    private final int POWER_THANKS_VALUE = 1000;
    private final long FIVE_DAYS_IN_MILLIS = 432000000;

    private long mTopReceivedIndex;
    private long mTopGivenIndex;

    private ImageView mButtonThanks;
    private ImageView mButtonSuperThanks;
    private ImageView mButtonMegaThanks;
    private ImageView mButtonPowerThanks;
    private ImageView mButtonUltraThanks;
    private CardView mCardThanks;
    private CardView mCardSuperThanks;
    private CardView mCardMegaThanks;
    private CardView mCardPowerThanks;
    private CardView mCardUltraThanks;
    private CardView mCardStats;
    private ImageView mAddFriendButton;
    private ImageView mFriendRequestImage;
    private Button mActivateThanksButton;
    private Button mButtonEditReason;
    private EditText mEditReason;
    private TextView mNameView;
    private TextView mTextThanksGiven;
    private TextView mTextThanksReceived;
    private TextView mTextValueLabel;
    private TextView mCombinedTextLabel;
    private TextView mTextHasMostlyThanked;
    private TextView mTextFoundUser;
    private TextView mTextUserCategories;
    private TextView mTextTodayThanks;
    private TextView mTextLevel;
    private TextView mTextCombinedLabel;
    private TextView mSuperThanksGiven;
    private TextView mMegaThanksGiven;
    private TextView mPowerThanksGiven;
    private TextView mSuperThanksReceived;
    private TextView mMegaThanksReceived;
    private TextView mPowerThanksReceived;
    private TextView mUltraThanksGiven;
    private TextView mUltraThanksReceived;
    private TextView mTextCurrentThankers;
    private TextView mTextFutureThankers;
    private TextView mTextBonusThankers;
    private TextView mTopGivenLabel;
    private TextView mTopReceivedLabel;
    private TextView mTextRequestSent;
    private TextView mTextMeToYou;
    private TextView mTextYouToMe;
    private TextView mTextFriendType;
    private TextView mTextThankTomorrow;
    private TextView mTextPrivacy;
    private TextView mTextPrivacyToday;
    private Switch mSwitchPrivacy;
    private ImageView mImagePrivacy;
    private Switch mSwitchPrivacyToday;
    private ImageView mImagePrivacyToday;
    private ImageView mImagePrivacyVisible;
    private ImageView mProfileImage;
    private ImageView mImageWelcome;
    private ImageView mMyPictureStats;
    private ImageView mOtherPictureStats;
    private ImageView mImageFriendType;
    private ImageView mImageInfoEdit;

    private MultiColorCircle mStatusCircle;
    private CardView mAddFriendCircle;
    private ProgressBar mProgressBar;
    private EditText mInputThanksReason;
    private LinearLayout mLinearPendingRequest;
    private LinearLayout mLinearFriendType;
    private LinearLayout mLinearLayoutThanksGiven;
    private LinearLayout mLinearCharts;
    private LinearLayout mLinearTopThankersGiven;
    private LinearLayout mLinearTopThankersReceived;
    private LinearLayout mLinearToday;
    private LinearLayout mLinearThanksButtons;
    private LinearLayout mLinearOurProfileStats;
    private LinearLayout mLinearOtherProfileStats;
    private LinearLayout mLinearEdit;
    private LinearLayout mLinearThanks;
    private LinearLayout mLinearThanksContainer;
    private LinearLayout mLinearActiveThanks;
    private LinearLayout mLinearThankersInfo;
    private LinearLayout mLinearCompatibilities;
    private LinearLayout mLinearUltra;
    private LinearLayout mLinearVisibilityToday;
    private List<ImageView> mThanksTypesGivenList;
    private List<ImageView> mThanksTypesReceivedList;
    private List<ImageView> mGivenWelcomeImageList;
    private List<ImageView> mReceivedWelcomeImageList;
    private List<TextView> mListTextThanksGiven;
    private List<TextView> mListTextThanksReceived;
    private List<TextView> mListTextTopTenUsernamesReceived;
    private List<TextView> mListTextTopTenUsernamesGiven;
    private List<TextView> mCategoriesLabelsList;
    private List<TextView> mCategoriesValuesList;
    private List<TextView> mListChartLabels;
    private List<ImageView> mListIconsThanks;
    private List<LinearLayout> mListLinearLayoutGiven;
    private List<LinearLayout> mListLinearLayoutReceived;
    private List<LinearLayout> mListLinearTopTenRankReceived;
    private List<LinearLayout> mListLinearTopTenRankGiven;
    private List<LinearLayout> mListLinearChartsThanks;
    private List<LinearLayout> mListLinearRectangles;
    private List<LinearLayout> mListLinearRecentThanks;

    private Bundle mDataBundle;
    private boolean mIsConceptual;
    private boolean mControlRecentGiven;
    private boolean mShowDescription;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private ListenerRegistration mTodayThanksListener;
    private Query mTodayThanksQuery;

    private View mView;

    private User mUser;
    private User mOtherUser;
    private String mOtherUserId;
    private String mOtherUserName;
    private String mUserCountry;
    private String mOurLevelString;

    private List<TopItem> mTopReceivedList;
    private List<ThanksItem> mOtherUserThanksGiven;
    private List<ThanksItem> mOtherUserThanksReceived;
    private Thanks mThanks;

    private PieChart mPieChartOtherUser;
    private PieChart mCombinedPieChart;

    private boolean mHasThankedToday = false;
    private boolean mWasIInTopTenThankers = false;
    private boolean mHasBonus;

    private boolean mHasWrittenThanksReason = false;
    private boolean mHasClickedSaveButtonReason = false;
    private String mReasonString;
    private String mRecentThanksKey;

    private List<ThanksValue> mThanksMapOtherUser;
    private List<ThanksValue> mThanksMapOurUser;
    private List<ThanksValue> mCombinedThanksMap;
    private List<ThanksValue> mCombinedRelativeThanksMap;
    private List<ThanksValue> mOtherUserThanksValues;

    private List<UserValue> mGetAllUsersThatThankedOtherUserList;

    private ColorStateList mDisabledColor;
    private ColorStateList mThanksColor;
    private ColorStateList mSuperThanksColor;
    private ColorStateList mMegaThanksColor;
    private ColorStateList mPowerThanksColor;
    private ColorStateList mUltraThanksColor;

    private long mOurThanksCurrency;
    private long mOurRecentThanksCount;
    private long mOtherUserReceivedCount;
    private long mGivenIndex;
    private long mReceivedIndex;

    private int mCountShowTodayThanks;

    private String mMonth;
    private String mYear;
    private String mDay;
    private String mTodayThanksString;
    private String mDescription;
    private String mThanksType;
    private String mThanksKey;
    private Thanks mRecentThanks;
    private boolean mEditingReason;
    private boolean mHasEditedToday;
    private int mThankersSpend;
    private boolean mIsTodayThanksComplete;
    private LocationManager mLocationManager;

    private ThanksData mOurThanksData;
    private ThanksData mOtherThanksData;

    private boolean mContinueSendingId = false;
    private boolean mStoppedSendingId;
    private Message mSendIdMessage;

    private boolean mActivatedThanks;
    private AppCompatActivity mActivity;
    private ActionBar mActionBar;
    private boolean mHoldToday;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_third_party, container, false);

        mView = view;
        mCountShowTodayThanks = 0;
        mEditingReason = false;
        mHasEditedToday = false;
        mThankersSpend = 0;
        mIsTodayThanksComplete = false;
        mControlRecentGiven = false;
        mShowDescription = true;
        boolean gotWholeUser = false;
        mHoldToday = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initLocation();
        }

        mDataBundle = getArguments();
        mUserCountry = mDataBundle.getString(OUR_USER_COUNTRY);
        mContinueSendingId = mDataBundle.getBoolean(CONTINUE_SENDING_ID, false);
        mActivatedThanks = mDataBundle.getBoolean(ACTIVATED_THANKS, false);

        if (mDataBundle.containsKey(OTHER_USER_OBJECT)) {
            mOtherUser = (User) mDataBundle.getSerializable(OTHER_USER_OBJECT);
            gotWholeUser = true;
        } else if (mDataBundle.containsKey(USER_ID_STRING)) {
            mOtherUserId = mDataBundle.getString(USER_ID_STRING);
            gotWholeUser = false;
        }

        Log.v(TAG, "Receiving thanks from Thanks Fragment: " + mUserCountry);

        initializeDate();
        initializeRecentThanksViews();
        initializeColors();

        setupFirebase();

        initializeViews(view);
        initializeWelcomeImages(view);
        implementOnClicks();

        if (mActivatedThanks && mContinueSendingId) {
            if (getActivity() != null) {
                mLinearThanksButtons.setVisibility(View.VISIBLE);
                mTextValueLabel.setVisibility(View.VISIBLE);
                mTextValueLabel.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
                mCardThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.circlethanks));
                mActivateThanksButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_accent));
                mActivateThanksButton.setText(getActivity().getString(R.string.thanks));
                mTextValueLabel.setText("Thanks x1");
                mThanksType = TYPE_THANKS;
                mTextFoundUser.setVisibility(View.VISIBLE);
            }
        }

        mProgressBar.setVisibility(View.VISIBLE);

        if (savedInstanceState != null) {
            //Restore the fragment's state here
            boolean hasReasonText = savedInstanceState.getBoolean("hasWrittenReason", false);
            boolean hasClickedSave = savedInstanceState.getBoolean("hasSavedReason", false);
            boolean hasThankedToday = savedInstanceState.getBoolean("hasThankedToday", false);
            mRecentThanksKey = savedInstanceState.getString("recentThanksKey", "");
            String reason = savedInstanceState.getString("reasonText", "");
            mTodayThanksString = savedInstanceState.getString("todayThanksText", null);
            mCountShowTodayThanks = savedInstanceState.getInt("countThanksShown", 0);
            mIsTodayThanksComplete = savedInstanceState.getBoolean("is-thanks-complete", false);
            mActivatedThanks = savedInstanceState.getBoolean(ACTIVATED_THANKS, false);
            mThanksType = savedInstanceState.getString(TYPE_SAVED, null);
            mControlRecentGiven = savedInstanceState.getBoolean("control-given");
            mShowDescription = savedInstanceState.getBoolean("show-description", true);
            mHoldToday = savedInstanceState.getBoolean("hold-today", false);

            if (getActivity() != null) {
                if (mThanksType != null && !mHasThankedToday) {
                    decolorCardViews();
                    switch (mThanksType) {
                        case TYPE_THANKS:
                            mActivateThanksButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_green));
                            mActivateThanksButton.setText(getActivity().getString(R.string.thanks));
                            mCardThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.circlethanks));
                            mTextValueLabel.setText("x1");
                            mTextValueLabel.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
                            mThankersSpend = THANKS_VALUE;
                            checkReturnThankers();
                            break;

                        case TYPE_SUPER:
                            mActivateThanksButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_super_thanks));
                            mActivateThanksButton.setText(getActivity().getString(R.string.super_thanks));
                            mCardSuperThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.circlesuper));
                            mTextValueLabel.setText("x10");
                            mTextValueLabel.setTextColor(getActivity().getResources().getColor(R.color.superThanksCoin));
                            mThankersSpend = SUPER_THANKS_VALUE;
                            checkReturnThankers();
                            break;

                        case TYPE_MEGA:
                            mActivateThanksButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_mega_thanks));
                            mActivateThanksButton.setText(getActivity().getString(R.string.mega_thanks));
                            mCardMegaThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.circlemega));
                            mTextValueLabel.setText("x100");
                            mTextValueLabel.setTextColor(getActivity().getResources().getColor(R.color.megaThanksCoin));
                            mThankersSpend = MEGA_THANKS_VALUE;
                            checkReturnThankers();
                            break;

                        case TYPE_POWER:
                            mActivateThanksButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_power_thanks));
                            mActivateThanksButton.setText(getActivity().getString(R.string.power_thanks));
                            mCardPowerThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.circlepower));
                            mTextValueLabel.setText("x1000");
                            mTextValueLabel.setTextColor(getActivity().getResources().getColor(R.color.powerThanksCoin));
                            mThankersSpend = POWER_THANKS_VALUE;
                            checkReturnThankers();
                            break;

                        case TYPE_ULTRA:
                            mActivateThanksButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_indigo));
                            mActivateThanksButton.setText(getActivity().getString(R.string.ultra_thanks));
                            mCardUltraThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.circlepower));
                            mTextValueLabel.setText("x10.000");
                            mTextValueLabel.setTextColor(getActivity().getResources().getColor(R.color.indigo));
                            mThankersSpend = 10000;
                            checkReturnThankers();
                            break;
                    }
                }
            }

            if (!mActivatedThanks) {
                mLinearThanksContainer.setVisibility(View.VISIBLE);
                mLinearActiveThanks.setVisibility(View.GONE);
                mLinearThanksButtons.setVisibility(View.GONE);
                mTextValueLabel.setVisibility(View.GONE);
                mLinearThankersInfo.setVisibility(View.GONE);
            } else {
                mLinearThanksContainer.setVisibility(View.GONE);
                mLinearActiveThanks.setVisibility(View.VISIBLE);
                mLinearThanksButtons.setVisibility(View.VISIBLE);
                mTextValueLabel.setVisibility(View.VISIBLE);
                mLinearThankersInfo.setVisibility(View.VISIBLE);
            }

            if (hasThankedToday) {

                mInputThanksReason.setVisibility(View.INVISIBLE);

                //just making sure these variables are unchanged
                mHasThankedToday = true;
                mHasClickedSaveButtonReason = true;
            } else if (!hasClickedSave && hasThankedToday) {
                mInputThanksReason.setVisibility(View.VISIBLE);
                mInputThanksReason.setText(reason);
            }
        }

        initializeUsersTopRankViewsAndValues(view);
        initRectangleCharts(view);
        initializeChartLabels(view);
        //populateTopRanks();

        if (getActivity() != null) {
            Utils.hideKeyboardFrom(getActivity());
        }

        initAddFriendButton();

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mEditingReason && !mHoldToday) {
                    mLinearEdit.setVisibility(View.GONE);
                    mTextTodayThanks.setVisibility(View.VISIBLE);
                    mLinearToday.setClickable(true);

                    if (mRecentThanks.getWasWelcomed()) {
                        mImageWelcome.setVisibility(View.VISIBLE);
                    }

                    if (mRecentThanks.getDescription().length() > 0) {
                        mLinearVisibilityToday.setVisibility(View.VISIBLE);
                        //mSwitchPrivacyToday.setVisibility(View.VISIBLE);
                        mImagePrivacyVisible.setVisibility(View.VISIBLE);
                        mTextPrivacyToday.setVisibility(View.VISIBLE);
                        Log.v(TAG, "Today visibility. It's visible");
                    } else {
                        mLinearVisibilityToday.setVisibility(View.GONE);
                        Log.v(TAG, "Today visibility. It's NOT visible");
                    }

                    mEditingReason = false;
                }

                return false;
            }
        });

        final boolean hadOtherUser = gotWholeUser;

        mFirestore.collection(DB_REFERENCE).document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists() && getActivity() != null) {
                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading our User\'s info");
                            mUser = documentSnapshot.toObject(User.class);

                            ImageUtils.loadImageInto(getActivity(), mUser.getImageUrl(), mMyPictureStats);

                            if (hadOtherUser) {
                                populateUi();
                                Utils.changeBarTitle(getActivity(), mActionBar, DataUtils.capitalize(mOtherUser.getName()));
                                //mProgressBar.setVisibility(View.GONE);
                            } else {
                                mFirestore.collection(DB_REFERENCE).document(mOtherUserId)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading Other User\'s info");
                                                    mOtherUser = documentSnapshot.toObject(User.class);
                                                    mOtherUserName = DataUtils.capitalize(mOtherUser.getName());
                                                    Utils.changeBarTitle(getActivity(), mActionBar, mOtherUserName);
                                                    populateUi();
                                                    //mProgressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });


        return view;
    }

    public void populateUi() {
        openCompatibilitiesAndCategories();
        openOtherUserInfo();
        populateRecentThanksOtherUser();
        ourRecentThanks();
        openThanksData();
        mapsOfThanks();
        usersThankedOtherUser();
        usersOtherUserThanked();
        checkFriendship();
        powerTodaysThanks();
    }

    public void openCompatibilitiesAndCategories() {
        String otherName = DataUtils.capitalize(mOtherUser.getName());
        mOtherUserId = mOtherUser.getUserId();

        String categories = DataUtils.translateAndFormat(getActivity(), mOtherUser.getPrimaryCategory());

        if (mOtherUser.getSecondaryCategory() != null) {
            if (!mOtherUser.getSecondaryCategory().equals("")) {
                categories += " | " + DataUtils.translateAndFormat(getActivity(), mOtherUser.getSecondaryCategory());
            }
        }

        mTextUserCategories.setText(categories);

        if (getActivity() != null) {
            String ourFirstName = DataUtils.capitalize(DataUtils.firstName(mUser.getName()));
            String otherFirstName = DataUtils.capitalize(DataUtils.firstName(mOtherUser.getName()));
            ImageUtils.loadImageInto(getActivity(), mOtherUser.getImageUrl(), mOtherPictureStats);
            mTextMeToYou.setText(Html.fromHtml(getActivity().getString(R.string.from_you_to_user, ourFirstName, otherFirstName)));
            mTextYouToMe.setText(Html.fromHtml(getActivity().getString(R.string.from_user_to_you, otherFirstName, ourFirstName)));
            mTextCombinedLabel.setText(getActivity().getString(R.string.you_combined, otherName));
        }
    }

    public void openOtherUserInfo() {
        if (getActivity() != null) {
            mNameView.setText(DataUtils.capitalize(mOtherUser.getName()));
            ImageUtils.loadImageInto(getActivity(), mOtherUser.getImageUrl(), mProfileImage);

        }
    }

    public void populateRecentThanksOtherUser() {
        populateGiven();
        populateReceived();
        otherUserRecentThanks();
    }

    public void populateGiven() {
        mFirestore.collection(THANKS_DB).whereEqualTo("fromUserId", mOtherUser.getUserId())
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mOtherUserThanksGiven = new ArrayList<>();
                        mGivenIndex = 0;

                        for (QueryDocumentSnapshot thanksSnapshot : queryDocumentSnapshots) {
                            final Thanks thanks = thanksSnapshot.toObject(Thanks.class);

                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading recent Thanks Given by OtherUser");

                            mFirestore.collection(USER_SNIPPET).document(thanks.getToUserId())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading UserSnippet from who OtherUser gave recent Thanks to");
                                                UserSnippet user = documentSnapshot.toObject(UserSnippet.class);
                                                String name = DataUtils.capitalize(user.getName());

                                                mOtherUserThanksGiven.add(new ThanksItem(name, thanks.getThanksType(), thanks.getDate(), thanks.getWasWelcomed()));
                                                mGivenIndex++;

                                                if (mGivenIndex == queryDocumentSnapshots.size()) {
                                                    updateRecentGiven();
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public void updateRecentGiven() {

        DataUtils.sortRecentThanks(mOtherUserThanksGiven);

        for (int i = 0; i != 5 && i != mOtherUserThanksGiven.size(); i++) {
            mListTextThanksGiven.get(i).setVisibility(View.VISIBLE);
            mListTextThanksGiven.get(i).setTypeface(null, Typeface.BOLD);
            mListTextThanksGiven.get(i).setText(mOtherUserThanksGiven.get(i).getName());
            if (getActivity() != null) {
                mThanksTypesGivenList.get(i).setVisibility(View.VISIBLE);
                mThanksTypesGivenList.get(i).setImageDrawable(ImageUtils.getThanksDraw(getActivity(), mOtherUserThanksGiven.get(i).getThanksType()));
            }

            mListLinearLayoutGiven.get(i).setVisibility(View.VISIBLE);

            if (mOtherUserThanksGiven.get(i).getWasWelcomed()) {
                ImageView newWelcome = mGivenWelcomeImageList.get(i);
                int color = Color.parseColor("#B6B2B2"); //The color u want
                newWelcome.setColorFilter(color);
                mGivenWelcomeImageList.get(i).setVisibility(View.VISIBLE);
            } else {
                mGivenWelcomeImageList.get(i).setVisibility(View.INVISIBLE);
            }

            mListLinearRecentThanks.get(i).setVisibility(View.VISIBLE);
        }
    }

    public void updateRecentReceived() {

        DataUtils.sortRecentThanks(mOtherUserThanksReceived);

        for (int i = 0; i != 5 && i != mOtherUserThanksReceived.size(); i++) {
            mListTextThanksReceived.get(i).setVisibility(View.VISIBLE);
            mListTextThanksReceived.get(i).setTypeface(null, Typeface.BOLD);
            mListTextThanksReceived.get(i).setText(mOtherUserThanksReceived.get(i).getName());
            if (getActivity() != null) {
                mThanksTypesReceivedList.get(i).setVisibility(View.VISIBLE);
                mThanksTypesReceivedList.get(i).setImageDrawable(ImageUtils.getThanksDraw(getActivity(), mOtherUserThanksReceived.get(i).getThanksType()));
            }

            mListLinearLayoutReceived.get(i).setVisibility(View.VISIBLE);

            if (mOtherUserThanksReceived.get(i).getWasWelcomed()) {
                ImageView newWelcome = mReceivedWelcomeImageList.get(i);
                int color = Color.parseColor("#B6B2B2"); //The color u want
                newWelcome.setColorFilter(color);
                mReceivedWelcomeImageList.get(i).setVisibility(View.VISIBLE);
            } else {
                mReceivedWelcomeImageList.get(i).setVisibility(View.INVISIBLE);
            }

            mListLinearRecentThanks.get(i).setVisibility(View.VISIBLE);
        }
    }

    public void populateReceived() {
        mFirestore.collection(THANKS_DB).whereEqualTo("toUserId", mOtherUser.getUserId())
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mOtherUserThanksReceived = new ArrayList<>();
                        mReceivedIndex = 0;

                        for (QueryDocumentSnapshot thanksSnapshot : queryDocumentSnapshots) {
                            final Thanks thanks = thanksSnapshot.toObject(Thanks.class);

                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading recent Thanks received");

                            mFirestore.collection(USER_SNIPPET).document(thanks.getFromUserId())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading UserSnippet info from User that OtherUser received recent Thanks from");
                                                UserSnippet user = documentSnapshot.toObject(UserSnippet.class);
                                                String name = DataUtils.capitalize(user.getName());

                                                mOtherUserThanksReceived.add(new ThanksItem(name, thanks.getThanksType(), thanks.getDate(), thanks.getWasWelcomed()));
                                                mReceivedIndex++;

                                                if (mReceivedIndex == queryDocumentSnapshots.size()) {
                                                    updateRecentReceived();
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public void otherUserRecentThanks() {
        String status = "";
        MultiColorCircle.CustomStrokeObject s1 = null;
        MultiColorCircle.CustomStrokeObject s2 = null;

        if (getActivity() != null) {
            status = getActivity().getString(R.string.starter_thanker);
            mStatusCircle.setWidthOfCircleStroke(65);
            mStatusCircle.setWidthOfBoarderStroke(0);
            mStatusCircle.setColorOfBoarderStroke(ContextCompat.getColor(getActivity(), R.color.white));

            s1 = new MultiColorCircle.CustomStrokeObject(
                    80, 20, ContextCompat.getColor(getActivity(), R.color.lightGreyLevel)
            );
            s2 = new MultiColorCircle.CustomStrokeObject(
                    20, 0, ContextCompat.getColor(getActivity(), R.color.colorPrimary)
            );
        }

        long number = filterRecentThanks(mOtherUser.getRecentThanks());
        double numFactor = number / FIVE_DAYS;
        String thankerLevel = "starter";

        if (getActivity() != null) {
            //mLinearStatsOne.setBackgroundColor(getActivity().getResources().getColor(R.color.starterGreen));
            status = getActivity().getString(R.string.starter_thanker);
                                /*FirebaseDatabase.getInstance().getReference().child(DB_REFERENCE).child(mAuth.getCurrentUser().getUid())
                                        .child("thankerLevel").setValue("starter");*/

            //mTextRecentThanks.setText(getActivity().getString(R.string.recent_thanks) + ": " + number);

            if (numFactor >= 2.00) {
                //mLinearStatsTwo.setBackgroundColor(getActivity().getResources().getColor(R.color.walkerGreen));
                status = getActivity().getString(R.string.walker_thanker);
                thankerLevel = "walker";

                s1 = new MultiColorCircle.CustomStrokeObject(
                        60, 40, ContextCompat.getColor(getActivity(), R.color.lightGreyLevel)
                );
                s2 = new MultiColorCircle.CustomStrokeObject(
                        40, 0, ContextCompat.getColor(getActivity(), R.color.colorPrimary)
                );

            }

            if (numFactor >= 5.00) {
                //mLinearStatsThree.setBackgroundColor(getActivity().getResources().getColor(R.color.explorerGreen));
                status = getActivity().getString(R.string.explorer_thanker);
                thankerLevel = "explorer";

                s1 = new MultiColorCircle.CustomStrokeObject(
                        40, 60, ContextCompat.getColor(getActivity(), R.color.lightGreyLevel)
                );
                s2 = new MultiColorCircle.CustomStrokeObject(
                        60, 0, ContextCompat.getColor(getActivity(), R.color.colorPrimary)
                );
            }

            if (numFactor >= 7.00) {
                //mLinearStatsFour.setBackgroundColor(getActivity().getResources().getColor(R.color.thankerGreen));
                status = getActivity().getString(R.string.true_thanker);
                thankerLevel = "thanker";

                s1 = new MultiColorCircle.CustomStrokeObject(
                        20, 80, ContextCompat.getColor(getActivity(), R.color.lightGreyLevel)
                );
                s2 = new MultiColorCircle.CustomStrokeObject(
                        80, 0, ContextCompat.getColor(getActivity(), R.color.colorPrimary)
                );
            }

            if (numFactor >= 10.00) {
                //mLinearStatsFive.setBackgroundColor(getActivity().getResources().getColor(R.color.masterGreen));
                status = getActivity().getString(R.string.master_thanker);
                //mTextStatusLabel.setVisibility(View.GONE);
                //mImageLabel.setVisibility(View.VISIBLE);
                thankerLevel = "master";

                s1 = new MultiColorCircle.CustomStrokeObject(
                        0, 0, ContextCompat.getColor(getActivity(), R.color.lightGreyLevel)
                );
                s2 = new MultiColorCircle.CustomStrokeObject(
                        100, 0, ContextCompat.getColor(getActivity(), R.color.colorPrimary)
                );
            }
        }

        List<MultiColorCircle.CustomStrokeObject> myList = new ArrayList<>();
        myList.add(s1);
        myList.add(s2);

        mStatusCircle.setCircleStrokes(myList);
        mTextLevel.setText(DataUtils.capitalize(status));
    }

    public void ourRecentThanks() {
        mOurRecentThanksCount = filterRecentThanks(mUser.getRecentThanks());
        double averageThanks = (mOurRecentThanksCount / FIVE_DAYS);
        mOurLevelString = "starter";

        if (averageThanks >= 2.0 && averageThanks < 5.0) {
            mOurLevelString = "walker";
        }

        else if (averageThanks >= 5.0 && averageThanks < 7.0) {
            mOurLevelString = "explorer";
        }

        else if (averageThanks >= 7.0 && averageThanks < 10.0) {
            mOurLevelString = "true";
        }

        else if (averageThanks >= 10.0) {
            mOurLevelString = "master";
        }

        Log.v(TAG, "Average Thanks in last 5 days. Recent count: " + mOurRecentThanksCount);
        Log.v(TAG, "Average Thanks in last 5 days: " + averageThanks + ". Our Level String: " + mOurLevelString);
    }

    public long filterRecentThanks(List<Long> recentThanks) {
        long result = 0;

        for (Long item : recentThanks) {
            if (item >= (System.currentTimeMillis() - FIVE_DAYS_IN_MILLIS)) {
                result++;
                Log.v(TAG, "Average Thanks in last 5 days. Got this Thanks on: " + item);
            }
        }

        return result;
    }

    public void openThanksData() {
        mFirestore.collection(THANKS_DATA).document(mOtherUser.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading ThanksData from OtherUser");
                            mOtherThanksData = documentSnapshot.toObject(ThanksData.class);

                            long superThanksGiven = mOtherThanksData.getSuperThanksGiven();
                            long megaThanksGiven = mOtherThanksData.getMegaThanksGiven();
                            long powerThanksGiven = mOtherThanksData.getPowerThanksGiven();
                            long ultraThanksGiven = mOtherThanksData.getUltraThanksGiven();
                            long superThanksReceived = mOtherThanksData.getSuperThanksReceived();
                            long megaThanksReceived = mOtherThanksData.getMegaThanksReceived();
                            long powerThanksReceived = mOtherThanksData.getPowerThanksReceived();
                            long ultraThanksReceived = mOtherThanksData.getUltraThanksReceived();
                            mOtherUserReceivedCount = mOtherThanksData.getReceivedCount();
                            mOtherUserName = DataUtils.capitalize(mOtherUser.getName());

                            mSuperThanksGiven.setText(String.format("%,d", superThanksGiven));
                            mMegaThanksGiven.setText(String.format("%,d", megaThanksGiven));
                            mPowerThanksGiven.setText(String.format("%,d", powerThanksGiven));
                            mUltraThanksGiven.setText(String.format("%,d", ultraThanksGiven));
                            mSuperThanksReceived.setText(String.format("%,d", superThanksReceived));
                            mMegaThanksReceived.setText(String.format("%,d", megaThanksReceived));
                            mPowerThanksReceived.setText(String.format("%,d", powerThanksReceived));
                            mUltraThanksReceived.setText(String.format("%,d", ultraThanksReceived));

                            mTextThanksGiven.setText(String.format("%,d", mOtherThanksData.getGivenThanksValue()));
                            mTextThanksReceived.setText(String.format("%,d", mOtherThanksData.getReceivedThanksValue()));

                            mNameView.setText(DataUtils.capitalize(mOtherUser.getName()));
                        }
                    }
                });

        mFirestore.collection(THANKS_DATA).document(mUser.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading ThanksData from our User");
                            mOurThanksData = documentSnapshot.toObject(ThanksData.class);
                            mOurThanksCurrency = mOurThanksData.getThanksCurrency();

                            if (getActivity() != null) {
                                mTextCurrentThankers.setText(Html.fromHtml(getActivity().getString(R.string.current_thankers, mOurThanksCurrency)));
                                checkReturnThankers();
                            }

                            powerUpButtons();
                        }
                    }
                });
    }

    public void printTop(List<UserValue> list) {
        for (UserValue item : list) {
            Log.v(TAG, "Printing top. User ID: " + item.getUserId() + ". Value: " + item.getValueThanks());
        }
    }

    public void usersThankedOtherUser() {
        mGetAllUsersThatThankedOtherUserList = mOtherUser.getTopUsersReceived();
        sortUsersThanks(mGetAllUsersThatThankedOtherUserList);
        printTop(mGetAllUsersThatThankedOtherUserList);
        List<UserValue> topTenReceived = topTen(mGetAllUsersThatThankedOtherUserList);
        printTop(topTenReceived);
        mTopReceivedList = new ArrayList<>();

        mWasIInTopTenThankers = wasIInTopTen(mGetAllUsersThatThankedOtherUserList);

        mTopReceivedIndex = 0;

        for (UserValue userValue : topTenReceived) {
            mFirestore.collection(USER_SNIPPET).document(userValue.getUserId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading UserSnippet for making top 10 Received");
                                UserSnippet user = documentSnapshot.toObject(UserSnippet.class);
                                String userName = DataUtils.capitalize(user.getName());
                                mListTextTopTenUsernamesReceived.get((int) mTopReceivedIndex).setText(userName);
                                mListLinearTopTenRankReceived.get((int) mTopReceivedIndex).setVisibility(View.VISIBLE);
                                mTopReceivedIndex++;
                                mTopReceivedList.add(new TopItem(userValue.getUserId(), userName, userValue.getValueThanks()));
                                Log.v(TAG, "Printing top in Snippet Read. User ID: " + userValue.getUserId() + ". Value: " + userValue.getValueThanks());
                            }
                        }
                    });
        }
    }

    public void updateTopReceived(Thanks thanks) {
        boolean existsInTop = false;
        long thanksValue = DataUtils.thanksTypeToLong(thanks);
        for (int i = 0; i != mTopReceivedList.size() && !existsInTop; i++) {
            Log.v(TAG, "Updating Top Received. Index: " + i + ". User ID: " + mTopReceivedList.get(i).getUserId());
            if (mTopReceivedList.get(i).getUserId().equalsIgnoreCase(mUser.getUserId())) {
                existsInTop = true;
                mTopReceivedList.get(i).setValue(mTopReceivedList.get(i).getValue() + thanksValue);
            }
        }

        if (!existsInTop) {
            mTopReceivedList.add(new TopItem(mUser.getUserId(), mUser.getName(), thanksValue));
        }

        sortTopList();

        if (amIInTop()) {
            for (int i = 0; i != 10 && i != mTopReceivedList.size(); i++) {
                mListTextTopTenUsernamesReceived.get(i).setText(mTopReceivedList.get(i).getName());
                mListLinearTopTenRankReceived.get(i).setVisibility(View.VISIBLE);
            }
        }

    }

    public boolean amIInTop() {
        for (int i = 0; i != 10; i++) {
            if (mUser.getUserId().equalsIgnoreCase(mTopReceivedList.get(i).getUserId())) {
                return true;
            }
        }
        return false;
    }

    public void sortTopList() {
        Collections.sort(mTopReceivedList, new Comparator<TopItem>() {
            @Override
            public int compare(TopItem o1, TopItem o2) {
                return Long.compare(o2.getValue(), o1.getValue());
            }
        });
    }

    public void usersOtherUserThanked() {
        List<UserValue> usersThanked = topTen(mOtherUser.getTopUsersGiven());

        mTopGivenIndex = 0;

        for (UserValue userValue : usersThanked) {
            mFirestore.collection(USER_SNIPPET).document(userValue.getUserId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading UserSnippet for making top 10 Given");
                                UserSnippet user = documentSnapshot.toObject(UserSnippet.class);
                                String userName = DataUtils.capitalize(user.getName());
                                mListTextTopTenUsernamesGiven.get((int) mTopGivenIndex).setText(userName);
                                mListLinearTopTenRankGiven.get((int) mTopGivenIndex).setVisibility(View.VISIBLE);
                                mTopGivenIndex++;
                            }
                        }
                    });
        }
    }

    public boolean wasIInTopTen(List<UserValue> list) {
        int i = 0;
        for (UserValue user : list) {
            if (user.getUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid())) {
                return (i <= 9);
            }
        }

        return false;
    }

    public List<UserValue> topTen(List<UserValue> list) {
        List<UserValue> result = new ArrayList<>();
        sortUsersThanks(list);

        for (int i = 0; i != 10 && i != list.size(); i++) {
            result.add(list.get(i));
        }

        return result;
    }

    public void mapsOfThanks() {
        mThanksMapOurUser = new ArrayList<>();
        mThanksMapOurUser.add(new ThanksValue("personThanks", mUser.getPersonThanks()));
        mThanksMapOurUser.add(new ThanksValue("brandThanks", mUser.getBrandThanks()));
        mThanksMapOurUser.add(new ThanksValue("businessThanks", mUser.getBusinessThanks()));
        mThanksMapOurUser.add(new ThanksValue("natureThanks", mUser.getNatureThanks()));
        mThanksMapOurUser.add(new ThanksValue("healthThanks", mUser.getHealthThanks()));
        mThanksMapOurUser.add(new ThanksValue("foodThanks", mUser.getFoodThanks()));
        mThanksMapOurUser.add(new ThanksValue("associationThanks", mUser.getAssociationThanks()));
        mThanksMapOurUser.add(new ThanksValue("homeThanks", mUser.getHomeThanks()));
        mThanksMapOurUser.add(new ThanksValue("scienceThanks", mUser.getScienceThanks()));
        mThanksMapOurUser.add(new ThanksValue("religionThanks", mUser.getReligionThanks()));
        mThanksMapOurUser.add(new ThanksValue("sportsThanks", mUser.getSportsThanks()));
        mThanksMapOurUser.add(new ThanksValue("lifestyleThanks", mUser.getLifestyleThanks()));
        mThanksMapOurUser.add(new ThanksValue("techThanks", mUser.getTechThanks()));
        mThanksMapOurUser.add(new ThanksValue("fashionThanks", mUser.getFashionThanks()));
        mThanksMapOurUser.add(new ThanksValue("educationThanks", mUser.getEducationThanks()));
        mThanksMapOurUser.add(new ThanksValue("gamesThanks", mUser.getGamesThanks()));
        mThanksMapOurUser.add(new ThanksValue("travelThanks", mUser.getTravelThanks()));
        mThanksMapOurUser.add(new ThanksValue("govThanks", mUser.getGovThanks()));
        mThanksMapOurUser.add(new ThanksValue("beautyThanks", mUser.getBeautyThanks()));
        mThanksMapOurUser.add(new ThanksValue("financeThanks", mUser.getFinanceThanks()));
        mThanksMapOurUser.add(new ThanksValue("cultureThanks", mUser.getCultureThanks()));

        mThanksMapOtherUser = new ArrayList<>();
        mThanksMapOtherUser.add(new ThanksValue("personThanks", mOtherUser.getPersonThanks()));
        mThanksMapOtherUser.add(new ThanksValue("brandThanks", mOtherUser.getBrandThanks()));
        mThanksMapOtherUser.add(new ThanksValue("businessThanks", mOtherUser.getBusinessThanks()));
        mThanksMapOtherUser.add(new ThanksValue("natureThanks", mOtherUser.getNatureThanks()));
        mThanksMapOtherUser.add(new ThanksValue("healthThanks", mOtherUser.getHealthThanks()));
        mThanksMapOtherUser.add(new ThanksValue("foodThanks", mOtherUser.getFoodThanks()));
        mThanksMapOtherUser.add(new ThanksValue("associationThanks", mOtherUser.getAssociationThanks()));
        mThanksMapOtherUser.add(new ThanksValue("homeThanks", mOtherUser.getHomeThanks()));
        mThanksMapOtherUser.add(new ThanksValue("scienceThanks", mOtherUser.getScienceThanks()));
        mThanksMapOtherUser.add(new ThanksValue("religionThanks", mOtherUser.getReligionThanks()));
        mThanksMapOtherUser.add(new ThanksValue("sportsThanks", mOtherUser.getSportsThanks()));
        mThanksMapOtherUser.add(new ThanksValue("lifestyleThanks", mOtherUser.getLifestyleThanks()));
        mThanksMapOtherUser.add(new ThanksValue("techThanks", mOtherUser.getTechThanks()));
        mThanksMapOtherUser.add(new ThanksValue("fashionThanks", mOtherUser.getFashionThanks()));
        mThanksMapOtherUser.add(new ThanksValue("educationThanks", mOtherUser.getEducationThanks()));
        mThanksMapOtherUser.add(new ThanksValue("gamesThanks", mOtherUser.getGamesThanks()));
        mThanksMapOtherUser.add(new ThanksValue("travelThanks", mOtherUser.getTravelThanks()));
        mThanksMapOtherUser.add(new ThanksValue("govThanks", mOtherUser.getGovThanks()));
        mThanksMapOtherUser.add(new ThanksValue("beautyThanks", mOtherUser.getBeautyThanks()));
        mThanksMapOtherUser.add(new ThanksValue("financeThanks", mOtherUser.getFinanceThanks()));
        mThanksMapOtherUser.add(new ThanksValue("cultureThanks", mOtherUser.getCultureThanks()));

        mOtherUserThanksValues = mThanksMapOtherUser;

        long totalValuesOtherUser = checkThanksValues(mOtherUserThanksValues);

        if (totalValuesOtherUser > 0) {
            Log.v(TAG, "New OtherProfile. Creating Thanks stats");
            mTextHasMostlyThanked.setVisibility(View.VISIBLE);
            if (getActivity() != null) {
                mTextHasMostlyThanked.setText(DataUtils.capitalize(mOtherUser.getName()) + " " + getActivity().getString(R.string.main_thanks_categories));
            }
            mCardStats.setVisibility(View.VISIBLE);
            mLinearCharts.setVisibility(View.VISIBLE);
            sortThanksValues(mOtherUserThanksValues);
            boolean valid = true;
            final int MAX = 5;

            for (int i = 0; i != MAX && valid; i++) {
                ThanksValue tValue = mOtherUserThanksValues.get(i);
                if (tValue.getValue() > 0) {
                    float percentage = Math.round(100 * tValue.getValue() / totalValuesOtherUser);
                    int percentageInt = Math.round(percentage);
                    String category = thanksCategoryToStringCategory(tValue.getKey());
                    String categoryToWrite = category;
                    if (getActivity() != null) {
                        categoryToWrite = DataUtils.translateAndFormat(getActivity(), category);
                    }
                    mListLinearChartsThanks.get(i).setVisibility(View.VISIBLE);
                    mCategoriesLabelsList.get(i).setText(categoryToWrite);
                    mCategoriesValuesList.get(i).setText(percentageInt + "%");

                    if (getActivity() != null) {
                        Drawable iconDraw = ImageUtils.getIconImage(getActivity(), category);
                        iconDraw.setColorFilter(getResources().getColor(R.color.defaultTextColor2), PorterDuff.Mode.MULTIPLY);
                        mListIconsThanks.get(i).setBackground(iconDraw);
                    }

                    int viewWidth = 1000;
                    int viewHeight = 30;

                    Paint paint = new Paint();

                    if (getActivity() != null) {
                        paint.setColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    }

                    float rectangleLength = tValue.getValue() * viewWidth / totalValuesOtherUser;

                    int bitmapWidth = Math.round(rectangleLength);
                    int bitmapHeight = (int) Math.round(viewHeight * 0.8);
                    Bitmap bg = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);

                    Canvas canvas = new Canvas(bg);
                    float top = (float) (viewHeight - (viewHeight * 0.8));

                    if (tValue.getValue() > 0) {
                        canvas.drawRect(0, top, rectangleLength, bitmapHeight, paint);
                    }

                    mListLinearRectangles.get(i).setBackground(new BitmapDrawable(bg));
                } else {
                    valid = false;
                }
            }

            //create Chart of shared Appreciations
            if (mThanksMapOurUser != null) {
                combineSortMapsOfThanksAndCreateJointChart();
            } else {
                Log.v(TAG, "Didn't find thanks map of other user");
                mCombinedTextLabel.setVisibility(View.GONE);
                mCombinedPieChart.setVisibility(View.GONE);
            }
        } else {
            mLinearCompatibilities.setVisibility(View.GONE);
            mCombinedTextLabel.setVisibility(View.GONE);
            mCombinedPieChart.setVisibility(View.GONE);
        }
    }

    public void checkFriendship() {
        if (mUser.isFriendOf(mOtherUser)) {

            mAddFriendButton.setVisibility(View.GONE);
            mAddFriendCircle.setVisibility(View.GONE);
            Log.v(TAG, "Checking Add Friend Button removal. It was on Am I Friend Listener.");
            mLinearPendingRequest.setVisibility(View.GONE);

            int color = 0;
            if (getActivity() != null) {
                color = getActivity().getResources().getColor(R.color.colorPrimary);
            }

            String friendType = getActivity().getString(R.string.friend);

            mTextFriendType.setTextColor(color);
            mTextFriendType.setText(friendType);
            //mFriendTypeButton.setText(friendType);
            mLinearFriendType.setVisibility(View.VISIBLE);
            ;

            Drawable typeDraw = mImageFriendType.getDrawable();
            typeDraw.setColorFilter(color, PorterDuff.Mode.MULTIPLY);


            long thanksValues = mOtherUser.getThanksGivenTo(mUser);
            Log.v(TAG, "Finding Friend Type");

            Log.v(TAG, "Finding friend type. ID: " + mOtherUserId + ", Received Thanks: " + thanksValues);

            if (getActivity() != null) {

                color = getActivity().getResources().getColor(R.color.colorPrimary);
                friendType = getActivity().getString(R.string.friend);

                if (thanksValues >= 0 && thanksValues < 10) {
                    color = getActivity().getResources().getColor(R.color.colorPrimary);
                    friendType = getActivity().getString(R.string.friend);
                } else if (thanksValues >= 10 && thanksValues < 100) {
                    color = getActivity().getResources().getColor(R.color.superThanksCoin);
                    friendType = getActivity().getString(R.string.super_friend);
                } else if (thanksValues >= 100 && thanksValues < 1000) {
                    color = getActivity().getResources().getColor(R.color.megaThanksCoin);
                    friendType = getActivity().getString(R.string.mega_friend);
                } else if (thanksValues >= 1000) {
                    color = getActivity().getResources().getColor(R.color.powerThanksCoin);
                    friendType = getActivity().getString(R.string.power_friend);
                } else if (thanksValues >= 10000) {
                    color = getActivity().getResources().getColor(R.color.ultraThanksCoin);
                    friendType = getActivity().getString(R.string.ultra_friend);
                } else {
                    color = getActivity().getResources().getColor(R.color.colorPrimary);
                }

                mTextFriendType.setTextColor(color);
                mTextFriendType.setText(friendType);
                mLinearFriendType.setVisibility(View.VISIBLE);

                typeDraw = mImageFriendType.getDrawable();
                typeDraw.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }
        } else if (mUser.hasReceivedRequestFrom(mOtherUser) || mOtherUser.hasReceivedRequestFrom(mUser)) {
            mAddFriendButton.setVisibility(View.GONE);
            mAddFriendCircle.setVisibility(View.GONE);
            mLinearPendingRequest.setVisibility(View.VISIBLE);

            if(mUser.hasReceivedRequestFrom(mOtherUser)){
                mTextRequestSent.setText(getActivity().getString(R.string.pending_your_approval));
            }
        }
    }

    public void powerTodaysThanks() {
        mTodayThanksQuery = mFirestore.collection(THANKS_DB)
                .whereEqualTo("fromUserId", mUser.getUserId())
                .whereEqualTo("toUserId", mOtherUser.getUserId())
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1);

        mTodayThanksListener = mTodayThanksQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (QueryDocumentSnapshot thanksSnapshot : queryDocumentSnapshots) {
                    if (thanksSnapshot.exists()) {

                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading most Recent Thanks we gave to OtherUser");

                        mThanksKey = thanksSnapshot.getId();
                        Thanks thanks = thanksSnapshot.toObject(Thanks.class);
                        mRecentThanks = thanks;
                        Log.v("MainSnapThanks", "The Thanks exists!");
                        Log.v("MainSnapThanks", thanks.printThanks());
                        Date lastThanks = new Date(thanks.getDate());
                        Date now = new Date(System.currentTimeMillis());

                        Calendar calendarLastThanks = Calendar.getInstance();
                        Calendar calendarNow = Calendar.getInstance();
                        calendarLastThanks.setTime(lastThanks);
                        calendarNow.setTime(now);

                        if (calendarLastThanks.get(Calendar.DAY_OF_MONTH) == calendarNow.get(Calendar.DAY_OF_MONTH)
                                && calendarLastThanks.get(Calendar.MONTH) == calendarNow.get(Calendar.MONTH)
                                && calendarLastThanks.get(Calendar.YEAR) == calendarNow.get(Calendar.YEAR)) {
                            mHasThankedToday = true;
                            mLinearThanksButtons.setVisibility(View.GONE);
                            mLinearThanks.setVisibility(View.GONE);
                            mTextValueLabel.setVisibility(View.GONE);
                            mActivateThanksButton.setVisibility(View.GONE);
                            mTextThankTomorrow.setVisibility(View.VISIBLE);
                            mImageInfoEdit.setVisibility(View.VISIBLE);
                            mSwitchPrivacyToday.setThumbTintList(getSwitchColor(mRecentThanks.getThanksType()));
                            mButtonEditReason.setBackground(ImageUtils.getThanksButtonBackground(getActivity(), mRecentThanks.getThanksType()));
                            updateLinearTodayBackground(mRecentThanks);
                            if (getActivity() != null) {
                                if (thanks.getShowThanksDescription()) {
                                    mSwitchPrivacyToday.setChecked(true);
                                    mImagePrivacyToday.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.toggleon));
                                    mImagePrivacyVisible.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.toggleon));
                                    mTextPrivacyToday.setText(getActivity().getString(R.string.visible_all));
                                } else {
                                    mSwitchPrivacyToday.setChecked(false);
                                    mImagePrivacyToday.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.toggleoff));
                                    mImagePrivacyVisible.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.toggleoff));
                                    mTextPrivacyToday.setText(getActivity().getString(R.string.visible_only_receiver));

                                }
                            }

                            //mCountShowTodayThanks--;

                            if (!mHasEditedToday) {
                                createTodayThanks(thanks, true);
                            }

                            if (thanks.getDescription().equals("")) {
                                //mInputThanksReason.setVisibility(View.VISIBLE);
                                //mButtonSaveThanksReason.setVisibility(View.VISIBLE);
                                mHasClickedSaveButtonReason = false;
                                mLinearVisibilityToday.setVisibility(View.GONE);
                                //implementSaveReasonClickListener(data.getKey());
                            } else {
                                if (!mHasEditedToday) {
                                    Log.v(TAG, "Updating today's thanks. Current text: " + mTodayThanksString);
                                    updateTodayThanks(thanks.getDescription());
                                    mDescription = thanks.getDescription();
                                }
                                mLinearVisibilityToday.setVisibility(View.VISIBLE);

                            }

                            if (mRecentThanks.getShowThanksDescription()) {
                                mSwitchPrivacyToday.setChecked(true);
                                mShowDescription = true;
                                Drawable toggleThisDraw = mImagePrivacy.getDrawable();
                                toggleThisDraw.setColorFilter(getActivity().getResources().getColor(R.color.defaultTextColor2), PorterDuff.Mode.SRC_ATOP);
                                Drawable toggleDraw = mImagePrivacyToday.getDrawable();
                                toggleDraw.setColorFilter(getActivity().getResources().getColor(R.color.defaultTextColor2), PorterDuff.Mode.SRC_ATOP);
                                Drawable toggleVisibleDraw = mImagePrivacyVisible.getDrawable();
                                toggleVisibleDraw.setColorFilter(getActivity().getResources().getColor(R.color.defaultTextColor2), PorterDuff.Mode.SRC_ATOP);
                            } else {
                                mSwitchPrivacyToday.setChecked(false);
                                mShowDescription = false;
                                Drawable toggleThisDraw = mImagePrivacy.getDrawable();
                                toggleThisDraw.setColorFilter(getActivity().getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
                                Drawable toggleDraw = mImagePrivacyToday.getDrawable();
                                toggleDraw.setColorFilter(getActivity().getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
                                Drawable toggleVisibleDraw = mImagePrivacyVisible.getDrawable();
                                toggleVisibleDraw.setColorFilter(getActivity().getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
                            }
                        }

                            /*if (thanks.getDescription().equals("")) {
                                mInputThanksReason.setVisibility(View.VISIBLE);
                                mHasClickedSaveButtonReason = false;
                            }*/

                        Log.v("MainSnapThanks", "Value of mHasThankedToday: " + mHasThankedToday);
                        //powerUpButtons(mSuperThanks, mMegaThanks, mPowerThanks);


                    } else {
                        mHasThankedToday = false;
                        mImageInfoEdit.setVisibility(View.GONE);
                        //powerUpButtons(mSuperThanks, mMegaThanks, mPowerThanks);
                    }

                    //powerup buttons was here
                }
            }
        });

        //powerUpButtons(); Estava aqui, 2
    }

    public void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        String ourUserId = mAuth.getCurrentUser().getUid();
    }

    public void initializeViews(View v) {
        mNameView = (TextView) v.findViewById(R.id.text_profile_name);
        mTextCombinedLabel = (TextView) v.findViewById(R.id.text_combined_info);
        mSuperThanksGiven = (TextView) v.findViewById(R.id.text_super_thanks_given);
        mMegaThanksGiven = (TextView) v.findViewById(R.id.text_mega_thanks_given);
        mPowerThanksGiven = (TextView) v.findViewById(R.id.text_power_thanks_given);
        mUltraThanksGiven = (TextView) v.findViewById(R.id.text_ultra_thanks_given);
        mSuperThanksReceived = (TextView) v.findViewById(R.id.text_super_thanks_received);
        mMegaThanksReceived = (TextView) v.findViewById(R.id.text_mega_thanks_received);
        mPowerThanksReceived = (TextView) v.findViewById(R.id.text_power_thanks_received);
        mUltraThanksReceived = (TextView) v.findViewById(R.id.text_ultra_thanks_received);
        mTopGivenLabel = (TextView) v.findViewById(R.id.top_given_label);
        mTopReceivedLabel = (TextView) v.findViewById(R.id.top_received_label);
        mTextMeToYou = (TextView) v.findViewById(R.id.text_from_me_to_you);
        mTextYouToMe = (TextView) v.findViewById(R.id.text_from_you_to_me);
        mTextFriendType = (TextView) v.findViewById(R.id.text_friend_type);
        mTextThankTomorrow = (TextView) v.findViewById(R.id.text_thank_tomorrow);
        mTextCurrentThankers = (TextView) v.findViewById(R.id.text_current_thankers);
        mTextFutureThankers = (TextView) v.findViewById(R.id.text_future_thankers);
        mTextBonusThankers = (TextView) v.findViewById(R.id.text_bonus);
        mTextFoundUser = (TextView) v.findViewById(R.id.text_found_user);
        mSwitchPrivacy = v.findViewById(R.id.switch_show_description);
        mTextRequestSent = v.findViewById(R.id.text_request_sent);
        mImagePrivacy = v.findViewById(R.id.image_toggle);
        mImagePrivacyToday = v.findViewById(R.id.image_toggle_today);
        mImagePrivacyVisible = v.findViewById(R.id.image_toggle_today_visible);
        mSwitchPrivacyToday = v.findViewById(R.id.switch_show_description_today);
        mTextPrivacy = v.findViewById(R.id.text_show_description);
        mTextPrivacyToday = v.findViewById(R.id.text_privacy);
        mButtonEditReason = (Button) v.findViewById(R.id.button_edit_reason);
        mEditReason = (EditText) v.findViewById(R.id.edit_reason);
        mProfileImage = (ImageView) v.findViewById(R.id.profile_picture);
        mImageWelcome = (ImageView) v.findViewById(R.id.welcome_check);
        mMyPictureStats = (ImageView) v.findViewById(R.id.profile_picture_stats);
        mOtherPictureStats = (ImageView) v.findViewById(R.id.profile_other_user_picture_stats);
        mImageFriendType = (ImageView) v.findViewById(R.id.image_friend_type);
        mImageInfoEdit = (ImageView) v.findViewById(R.id.image_info_edit);
        mStatusCircle = (MultiColorCircle) v.findViewById(R.id.status_circle);
        //mCardViewTodayWelcome = (CardView) v.findViewById(R.id.cardview_welcome);
        //mCardCompatibilities = (CardView) v.findViewById(R.id.cardview_compatibilities);
        mAddFriendCircle = (CardView) v.findViewById(R.id.cardview_friends);
        mActivateThanksButton = (Button) v.findViewById(R.id.button_activate_thanks);
        mButtonThanks = (ImageView) v.findViewById(R.id.button_thanks);
        mButtonSuperThanks = (ImageView) v.findViewById(R.id.button_super_thanks);
        mButtonMegaThanks = (ImageView) v.findViewById(R.id.button_mega_thanks);
        mButtonPowerThanks = (ImageView) v.findViewById(R.id.button_power_thanks);
        mButtonUltraThanks = (ImageView) v.findViewById(R.id.button_ultra_thanks);
        mCardThanks = (CardView) v.findViewById(R.id.cardview_button_thanks);
        mCardSuperThanks = (CardView) v.findViewById(R.id.cardview_button_super_thanks);
        mCardMegaThanks = (CardView) v.findViewById(R.id.cardview_button_mega_thanks);
        mCardPowerThanks = (CardView) v.findViewById(R.id.cardview_button_power_thanks);
        mCardUltraThanks = (CardView) v.findViewById(R.id.cardview_button_ultra_thanks);
        mCardStats = (CardView) v.findViewById(R.id.card_stats);
        mAddFriendButton = (ImageView) v.findViewById(R.id.image_add_friend);
        mFriendRequestImage = (ImageView) v.findViewById(R.id.image_friend_request);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        mTextThanksGiven = (TextView) v.findViewById(R.id.text_thanks_given_user);
        mTextThanksReceived = (TextView) v.findViewById(R.id.text_thanks_received_user);
        mTextLevel = (TextView) v.findViewById(R.id.text_thanker_level);
        mInputThanksReason = (EditText) v.findViewById(R.id.input_thanks_description);
        //mButtonSaveThanksReason = (Button) v.findViewById(R.id.button_save_description);
        //mTopGivenLabelText = (TextView) v.findViewById(R.id.text_top_10_given_label);
        //mTextFriendRequestSent = (TextView) v.findViewById(R.id.text_request_sent);
        mTextHasMostlyThanked = (TextView) v.findViewById(R.id.label_chart);
        mTextValueLabel = (TextView) v.findViewById(R.id.text_thanks_value);
        mTextUserCategories = (TextView) v.findViewById(R.id.text_user_categories);
        mTextTodayThanks = (TextView) v.findViewById(R.id.text_today_thanks);
        mLinearLayoutThanksGiven = (LinearLayout) v.findViewById(R.id.linear_layout_recent_thanks);
        mLinearTopThankersReceived = (LinearLayout) v.findViewById(R.id.linear_layout_top_thankers);
        mLinearTopThankersGiven = (LinearLayout) v.findViewById(R.id.linear_layout_top_given);
        mLinearToday = (LinearLayout) v.findViewById(R.id.linear_today);
        //mTopReceivedLabelText = (TextView) v.findViewById(R.id.text_top_10label);
        mLinearPendingRequest = (LinearLayout) v.findViewById(R.id.linear_friend_request);
        mLinearFriendType = (LinearLayout) v.findViewById(R.id.linear_friend);
        mLinearThanksButtons = (LinearLayout) v.findViewById(R.id.linear_thanks_buttons);
        mLinearVisibilityToday = v.findViewById(R.id.linear_visible_description);
        //mLinearLayoutThanksReceived = (LinearLayout) v.findViewById(R.id.linear_layout_thanks_received);
        mLinearOurProfileStats = (LinearLayout) v.findViewById(R.id.linear_our_profile_stats);
        mLinearOtherProfileStats = (LinearLayout) v.findViewById(R.id.linear_other_profile_stats);
        mLinearCharts = (LinearLayout) v.findViewById(R.id.linear_categories_thanked);
        mLinearEdit = (LinearLayout) v.findViewById(R.id.linear_edit_reason);
        mLinearThanks = (LinearLayout) v.findViewById(R.id.linear_thanks);
        mLinearThanksContainer = (LinearLayout) v.findViewById(R.id.linear_thanks_container);
        mLinearActiveThanks = (LinearLayout) v.findViewById(R.id.linear_active_thanks_container);
        mLinearThankersInfo = (LinearLayout) v.findViewById(R.id.linear_thankers_info);
        mLinearCompatibilities = (LinearLayout) v.findViewById(R.id.linear_compatibilities);
        mLinearUltra = (LinearLayout) v.findViewById(R.id.linear_ultra_thanks);
        mCombinedTextLabel = (TextView) v.findViewById(R.id.label_combined_chart);
        //mLine = (View) v.findViewById(R.id.view_line);

        //mPieChartOtherUser = (PieChart) v.findViewById(R.id.chart_thanks);
        mCombinedPieChart = (PieChart) v.findViewById(R.id.chart_combined_thanks);

        mProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        decolorCardViews();

        Drawable toggleDraw = mImagePrivacy.getDrawable();
        toggleDraw.setColorFilter(getActivity().getResources().getColor(R.color.defaultTextColor2), PorterDuff.Mode.SRC_ATOP);

        /*
        if(getActivity() != null) {
            mGiverLinearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
            mReceiverLinearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
            mThanksGivenRecyclerView.setLayoutManager(mGiverLinearLayoutManager);
            mThanksReceivedRecyclerView.setLayoutManager(mReceiverLinearLayoutManager);
        } */

        //mSwipeRefreshLayoutGiven = v.findViewById(R.id.swipe_thanks_given);
        //mTextThanksReceived.setText(getContext().getString(R.string.thanks_received) + " 0");

        if (getActivity() != null) {
            mActivateThanksButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_grey));

            /*Drawable addFriendDrawable = AppCompatResources.getDrawable(getActivity(), R.drawable.add_friend);
            Drawable wrappedDrawable = DrawableCompat.wrap(addFriendDrawable);
            DrawableCompat.setTint(wrappedDrawable, getActivity().getResources().getColor(R.color.colorPrimary));*/
        }

        mLinearThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinearThanksContainer.setVisibility(View.GONE);
                mLinearThankersInfo.setVisibility(View.VISIBLE);
                mLinearThanksButtons.setVisibility(View.VISIBLE);
                mLinearActiveThanks.setVisibility(View.VISIBLE);
                mTextValueLabel.setVisibility(View.VISIBLE);
                mActivatedThanks = true;
                mInputThanksReason.setVisibility(View.VISIBLE);
                mThanksType = TYPE_THANKS;
                mTextValueLabel.setText("Thanks x1");
                mTextValueLabel.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                if (getActivity() != null) {
                    mCardThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.circlethanks));
                    mActivateThanksButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_green));
                }
            }
        });

        mActivateThanksButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                mLinearThankersInfo.setVisibility(View.GONE);

                final String fromDateCode = mUser.getUserId() + "_" + mDay + "_" + mMonth + "_" + mYear;
                final String toDateCode = mOtherUserId + "_" + mDay + "_" + mMonth + "_" + mYear;

                String currentCountry = mUserCountry;

                if (currentCountry != null) {
                    if (currentCountry.equals("")) {
                        currentCountry = mUser.getLivingCountry();
                    }
                } else {
                    currentCountry = mUser.getLivingCountry();
                }

                final String countryString = TextUtils.replaceSignals(currentCountry);
                String description = mInputThanksReason.getText().toString().trim();
                mThanks = new Thanks(mUser.getUserId(), mOtherUserId, description, System.currentTimeMillis(), mOtherUser.getPrimaryCategory(),
                        mOtherUser.getSecondaryCategory(), DataUtils.generateYear(), DataUtils.generateMonth(), DataUtils.generateDay(), countryString, mThanksType, mShowDescription);

                saveThanks(mUser, mOtherUser, mThanks);

                mLinearThankersInfo.setVisibility(View.GONE);
                mInputThanksReason.setVisibility(View.GONE);
                mActivateThanksButton.setEnabled(false);
                if (getActivity() != null) {
                    mActivateThanksButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_grey));
                    mActivateThanksButton.setText(getActivity().getString(R.string.thanks_for_thanking));
                    mActivateThanksButton.setVisibility(View.GONE);
                }
            }
        });

        mImagePrivacy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (getActivity() != null) {
                    if (!mShowDescription) {
                        mShowDescription = true;
                        mImagePrivacy.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.toggleon));
                        Drawable toggleDraw = mImagePrivacy.getDrawable();
                        toggleDraw.setColorFilter(getActivity().getResources().getColor(R.color.defaultTextColor2), PorterDuff.Mode.SRC_ATOP);

                        mTextPrivacy.setText(getActivity().getString(R.string.description_public));
                        Log.v(TAG, "Set description as true.");
                    } else {
                        mShowDescription = false;
                        mImagePrivacy.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.toggleoff));
                        Drawable toggleDraw = mImagePrivacy.getDrawable();
                        toggleDraw.setColorFilter(getActivity().getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
                        mTextPrivacy.setText(getActivity().getString(R.string.description_private, DataUtils.capitalize(mOtherUserName)));
                        Log.v(TAG, "Set description as false.");
                    }
                }

                return false;
            }
        });

        mImagePrivacyToday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (getActivity() != null) {
                    if (!mShowDescription) {
                        mShowDescription = true;
                        mImagePrivacyToday.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.toggleon));
                        mImagePrivacyVisible.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.toggleon));
                        mImagePrivacyVisible.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.toggleon));
                        Drawable toggleDraw = mImagePrivacyToday.getDrawable();
                        toggleDraw.setColorFilter(getActivity().getResources().getColor(R.color.defaultTextColor2), PorterDuff.Mode.SRC_ATOP);
                        Drawable toggleVisibleDraw = mImagePrivacyVisible.getDrawable();
                        toggleVisibleDraw.setColorFilter(getActivity().getResources().getColor(R.color.defaultTextColor2), PorterDuff.Mode.SRC_ATOP);
                        mTextPrivacyToday.setText(getActivity().getString(R.string.visible_all));
                    } else {
                        mShowDescription = false;
                        mImagePrivacyToday.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.toggleoff));
                        mImagePrivacyVisible.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.toggleoff));
                        Drawable toggleDraw = mImagePrivacyToday.getDrawable();
                        toggleDraw.setColorFilter(getActivity().getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
                        Drawable toggleVisibleDraw = mImagePrivacyVisible.getDrawable();
                        toggleVisibleDraw.setColorFilter(getActivity().getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
                        mTextPrivacyToday.setText(getActivity().getString(R.string.visible_only_receiver));
                    }
                }
                return false;
            }
        });

        mLinearPendingRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!doesListContainRequest(mUser.getFriendRequests(), mOtherUser.getUserId())) {
                    List<FriendRequest> otherRequests = mOtherUser.getFriendRequests();
                    otherRequests = removeRequest(otherRequests, mUser.getUserId());
                    long unseenRequests = mOtherUser.getUnseenRequests();
                    unseenRequests--;
                    mOtherUser.setUnseenRequests(unseenRequests);
                    Log.v(TAG, "Adding new friends. Unmaking request. Current requests on receiver: " + unseenRequests);
                    mOtherUser.setFriendRequests(otherRequests);
                    mFirestore.collection(DB_REFERENCE).document(mOtherUser.getUserId()).set(mOtherUser);
                    mLinearPendingRequest.setVisibility(View.GONE);
                    mAddFriendButton.setVisibility(View.VISIBLE);
                    mAddFriendCircle.setVisibility(View.VISIBLE);
                }
                else {
                    mLinearPendingRequest.setEnabled(false);
                }
            }
        });

    }

    public List<FriendRequest> removeRequest(List<FriendRequest> requests, String id) {
        List<FriendRequest> result = new ArrayList<>();

        for (FriendRequest item : requests) {
            if (!item.getUserId().equalsIgnoreCase(id)) {
                result.add(item);
            }
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void initLocation() {
        if (getActivity() != null) {
            mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            //checkLocationPermission();

            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED/* && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/) {
                //LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    mUserCountry = DataUtils.getLocationCountry(getActivity(), latitude, longitude);
                    Log.v(TAG, "Checking user's location: " + mUserCountry);
                }
            }
        }

    }

    public void implementOnClicks() {

        for (LinearLayout linearGiven : mListLinearLayoutGiven) {
            linearGiven.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Fragment thanksFragment = new ThanksFragment();
                    Bundle infoBundle = new Bundle();
                    infoBundle.putSerializable(USER_OBJECT, mOtherUser);
                    infoBundle.putString(THANKS_DYNAMIC, DYNAMIC_GIVER);
                    infoBundle.putString(OUR_USER_COUNTRY, mUserCountry);
                    thanksFragment.setArguments(infoBundle);

                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, thanksFragment).addToBackStack(null).commit();
                    }
                }
            });
        }

        for (LinearLayout linearReceived : mListLinearLayoutReceived) {
            linearReceived.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Fragment thanksFragment = new ThanksFragment();
                    Bundle infoBundle = new Bundle();
                    infoBundle.putSerializable(USER_OBJECT, mOtherUser);
                    infoBundle.putString(THANKS_DYNAMIC, DYNAMIC_RECEIVER);
                    infoBundle.putString(OUR_USER_COUNTRY, mUserCountry);
                    thanksFragment.setArguments(infoBundle);

                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, thanksFragment).addToBackStack(null).commit();
                    }
                }
            });
        }

        mLinearCharts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent statsIntent = new Intent(getActivity(), PersonalStatsActivity.class);
                statsIntent.putExtra(USER_OBJECT, mOtherUser);
                statsIntent.putExtra(LIST_THANKS_VALUES, (Serializable) mThanksMapOtherUser);
                startActivity(statsIntent);
            }
        });

        mLinearTopThankersGiven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment thankersFragment = new ThankersListFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(USER_OBJECT, mOtherUser);
                bundle.putString(USER_ID_STRING, mOtherUser.getUserId());
                bundle.putString(LISTING_TYPE, TOP_USERS_THANKS_GIVEN);
                bundle.putString(OUR_USER_COUNTRY, mUserCountry);
                thankersFragment.setArguments(bundle);

                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, thankersFragment).addToBackStack(null).commit();
                }
            }
        });

        mLinearTopThankersReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment thankersFragment = new ThankersListFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(USER_OBJECT, mOtherUser);
                bundle.putString(USER_ID_STRING, mOtherUser.getUserId());
                bundle.putString(LISTING_TYPE, TOP_USERS_THANKS_RECEIVED);
                bundle.putString(OUR_USER_COUNTRY, mUserCountry);
                thankersFragment.setArguments(bundle);

                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, thankersFragment).addToBackStack(null).commit();
                }
            }
        });

        mLinearOurProfileStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment thanksFragment = new ThanksFragment();
                Bundle infoBundle = new Bundle();
                infoBundle.putSerializable(USER_OBJECT, mUser);
                //infoBundle.putBoolean(IS_PREMIUM, mIsPremium);
                infoBundle.putString(THANKS_DYNAMIC, DYNAMIC_GIVER);
                infoBundle.putString(EXCHANGE_THANKS, "yes");
                infoBundle.putString(OTHER_USER_THANKS, mOtherUserId);
                thanksFragment.setArguments(infoBundle);

                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, thanksFragment).addToBackStack(null).commit();
                }
            }
        });

        mLinearOtherProfileStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment thanksFragment = new ThanksFragment();
                Bundle infoBundle = new Bundle();
                infoBundle.putSerializable(USER_OBJECT, mUser);
                infoBundle.putString(THANKS_DYNAMIC, DYNAMIC_RECEIVER);
                infoBundle.putString(EXCHANGE_THANKS, "yes");
                infoBundle.putString(OTHER_USER_THANKS, mOtherUserId);
                thanksFragment.setArguments(infoBundle);

                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, thanksFragment).addToBackStack(null).commit();
                }
            }
        });

        mLinearToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHoldToday = true;
                mEditReason.setText(mDescription);
                mTextTodayThanks.setVisibility(View.GONE);
                mImageWelcome.setVisibility(View.GONE);
                mImagePrivacyVisible.setVisibility(View.GONE);
                mTextPrivacyToday.setVisibility(View.GONE);
                mLinearEdit.setVisibility(View.VISIBLE);
                mLinearToday.setClickable(false);
                mEditingReason = true;

                mButtonEditReason.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mDescription = mEditReason.getText().toString();
                        mRecentThanks.setShowThanksDescription(mShowDescription);
                        mRecentThanks.setDescription(mDescription);
                        mFirestore.collection(THANKS_DB).document(mThanksKey).set(mRecentThanks);
                        mTextTodayThanks.setText(Html.fromHtml(updateThanksString(mDescription)));
                        mLinearEdit.setVisibility(View.GONE);
                        mTextTodayThanks.setVisibility(View.VISIBLE);
                        mImagePrivacyVisible.setVisibility(View.VISIBLE);
                        mTextPrivacyToday.setVisibility(View.VISIBLE);
                        mLinearToday.setClickable(true);

                        if (mRecentThanks.getWasWelcomed()) {
                            mImageWelcome.setVisibility(View.VISIBLE);
                        }

                        mEditingReason = false;
                        mHasEditedToday = true;

                        mHoldToday = false;

                        Log.v(TAG, "Editing reason: " + updateThanksString(mDescription));

                    }
                });
            }
        });

        mImageInfoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle(getActivity().getString(R.string.edit_thanks_reason));
                    builder.setMessage(getActivity().getString(R.string.how_to_edit_reason));
                    builder.setPositiveButton(getActivity().getString(R.string.got_it),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }

    public String updateThanksString(String description) {
        String result = "";
        if (getActivity() != null) {
            result += getActivity().getString(R.string.today) + " " + getActivity().getString(R.string.you_lower) + " ";

            String thankType = DataUtils.thanksTypeToString(getActivity(), mRecentThanks.getThanksType());
            result += getActivity().getString(R.string.thanked_today, thankType);

            if (mOtherUser != null) {
                result += " " + mOtherUserName;
            }

            if (description != null) {
                if (!description.equals("")) {
                    result += ": \"" + description + "\"";
                }
            }

            if (!DataUtils.isPunctuation(result.charAt(result.length() - 2))) {
                result += "!";
            }
        }

        return result;
    }

    public void initAddFriendButton() {

        mAddFriendCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long unseenRequests = mOtherUser.getUnseenRequests() + 1;
                List<FriendRequest> listRequests = mOtherUser.getFriendRequests();
                if (!doesListContainRequest(listRequests, mUser.getUserId())) {
                    listRequests.add(new FriendRequest(mUser.getUserId(), false));
                    mOtherUser.setUnseenRequests(unseenRequests);
                    mOtherUser.setFriendRequests(listRequests);
                    mFirestore.collection(DB_REFERENCE).document(mOtherUser.getUserId()).set(mOtherUser);
                    Log.v(TAG, "Adding new friends. Making the request. Current requests on receiver: " + unseenRequests);
                    mAddFriendCircle.setVisibility(View.GONE);
                    mLinearPendingRequest.setVisibility(View.VISIBLE);
                    //mTextFriendRequestSent.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public boolean doesListContainRequest(List<FriendRequest> list, String userId) {
        for (FriendRequest item : list) {
            if (item.getUserId().equalsIgnoreCase(userId)) {
                return true;
            }
        }
        return false;
    }

    public void initializeWelcomeImages(View view) {
        mGivenWelcomeImageList = new ArrayList<>();
        mReceivedWelcomeImageList = new ArrayList<>();

        mGivenWelcomeImageList.add((ImageView) view.findViewById(R.id.image_welcome_1));
        mGivenWelcomeImageList.add((ImageView) view.findViewById(R.id.image_welcome_2));
        mGivenWelcomeImageList.add((ImageView) view.findViewById(R.id.image_welcome_3));
        mGivenWelcomeImageList.add((ImageView) view.findViewById(R.id.image_welcome_4));
        mGivenWelcomeImageList.add((ImageView) view.findViewById(R.id.image_welcome_5));

        mReceivedWelcomeImageList.add((ImageView) view.findViewById(R.id.image_welcome_received_1));
        mReceivedWelcomeImageList.add((ImageView) view.findViewById(R.id.image_welcome_received_2));
        mReceivedWelcomeImageList.add((ImageView) view.findViewById(R.id.image_welcome_received_3));
        mReceivedWelcomeImageList.add((ImageView) view.findViewById(R.id.image_welcome_received_4));
        mReceivedWelcomeImageList.add((ImageView) view.findViewById(R.id.image_welcome_received_5));
    }

    public void initializeColors() {
        if (getActivity() != null) {
            mDisabledColor = getActivity().getResources().getColorStateList(R.color.disabled_button);
            mThanksColor = getActivity().getResources().getColorStateList(R.color.colorPrimary);
            mSuperThanksColor = getActivity().getResources().getColorStateList(R.color.superThanksCoin);
            mMegaThanksColor = getActivity().getResources().getColorStateList(R.color.megaThanksCoin);
            mPowerThanksColor = getActivity().getResources().getColorStateList(R.color.powerThanksCoin);
            mUltraThanksColor = getActivity().getResources().getColorStateList(R.color.ultraThanksCoin);
        }
    }

    public ColorStateList getSwitchColor(String thanksType) {
        ColorStateList color = mThanksColor;

        switch (thanksType.toLowerCase()) {
            case "normal":
                color = mThanksColor;
                break;
            case "super":
                color = mSuperThanksColor;
                break;
            case "mega":
                color = mMegaThanksColor;
                break;
            case "power":
                color = mPowerThanksColor;
                break;
            case "ultra":
                color = mUltraThanksColor;
                break;
        }

        return color;
    }

    public void initRectangleCharts(View view) {
        mListLinearChartsThanks = new ArrayList<>();
        mListLinearRectangles = new ArrayList<>();
        mCategoriesLabelsList = new ArrayList<>();
        mCategoriesValuesList = new ArrayList<>();
        mListIconsThanks = new ArrayList<>();

        mListLinearChartsThanks.add((LinearLayout) view.findViewById(R.id.linear_category_1));
        mListLinearChartsThanks.add((LinearLayout) view.findViewById(R.id.linear_category_2));
        mListLinearChartsThanks.add((LinearLayout) view.findViewById(R.id.linear_category_3));
        mListLinearChartsThanks.add((LinearLayout) view.findViewById(R.id.linear_category_4));
        mListLinearChartsThanks.add((LinearLayout) view.findViewById(R.id.linear_category_5));

        mListLinearRectangles.add((LinearLayout) view.findViewById(R.id.rectangle_category_1));
        mListLinearRectangles.add((LinearLayout) view.findViewById(R.id.rectangle_category_2));
        mListLinearRectangles.add((LinearLayout) view.findViewById(R.id.rectangle_category_3));
        mListLinearRectangles.add((LinearLayout) view.findViewById(R.id.rectangle_category_4));
        mListLinearRectangles.add((LinearLayout) view.findViewById(R.id.rectangle_category_5));

        mListIconsThanks.add((ImageView) view.findViewById(R.id.icon_category_1));
        mListIconsThanks.add((ImageView) view.findViewById(R.id.icon_category_2));
        mListIconsThanks.add((ImageView) view.findViewById(R.id.icon_category_3));
        mListIconsThanks.add((ImageView) view.findViewById(R.id.icon_category_4));
        mListIconsThanks.add((ImageView) view.findViewById(R.id.icon_category_5));

        mCategoriesLabelsList.add((TextView) view.findViewById(R.id.text_category_1));
        mCategoriesLabelsList.add((TextView) view.findViewById(R.id.text_category_2));
        mCategoriesLabelsList.add((TextView) view.findViewById(R.id.text_category_3));
        mCategoriesLabelsList.add((TextView) view.findViewById(R.id.text_category_4));
        mCategoriesLabelsList.add((TextView) view.findViewById(R.id.text_category_5));

        mCategoriesValuesList.add((TextView) view.findViewById(R.id.text_category_value_1));
        mCategoriesValuesList.add((TextView) view.findViewById(R.id.text_category_value_2));
        mCategoriesValuesList.add((TextView) view.findViewById(R.id.text_category_value_3));
        mCategoriesValuesList.add((TextView) view.findViewById(R.id.text_category_value_4));
        mCategoriesValuesList.add((TextView) view.findViewById(R.id.text_category_value_5));

    }

    public void initializeUsersTopRankViewsAndValues(View view) {
        mListLinearTopTenRankReceived = new ArrayList<LinearLayout>();
        mListTextTopTenUsernamesReceived = new ArrayList<TextView>();
        mListLinearTopTenRankGiven = new ArrayList<LinearLayout>();
        mListTextTopTenUsernamesGiven = new ArrayList<TextView>();

        mListLinearTopTenRankReceived.add((LinearLayout) view.findViewById(R.id.linear_top_spot_1));
        mListLinearTopTenRankReceived.add((LinearLayout) view.findViewById(R.id.linear_top_spot_2));
        mListLinearTopTenRankReceived.add((LinearLayout) view.findViewById(R.id.linear_top_spot_3));
        mListLinearTopTenRankReceived.add((LinearLayout) view.findViewById(R.id.linear_top_spot_4));
        mListLinearTopTenRankReceived.add((LinearLayout) view.findViewById(R.id.linear_top_spot_5));
        mListLinearTopTenRankReceived.add((LinearLayout) view.findViewById(R.id.linear_top_spot_6));
        mListLinearTopTenRankReceived.add((LinearLayout) view.findViewById(R.id.linear_top_spot_7));
        mListLinearTopTenRankReceived.add((LinearLayout) view.findViewById(R.id.linear_top_spot_8));
        mListLinearTopTenRankReceived.add((LinearLayout) view.findViewById(R.id.linear_top_spot_9));
        mListLinearTopTenRankReceived.add((LinearLayout) view.findViewById(R.id.linear_top_spot_10));

        mListLinearTopTenRankGiven.add((LinearLayout) view.findViewById(R.id.linear_top_spot_given_1));
        mListLinearTopTenRankGiven.add((LinearLayout) view.findViewById(R.id.linear_top_spot_given_2));
        mListLinearTopTenRankGiven.add((LinearLayout) view.findViewById(R.id.linear_top_spot_given_3));
        mListLinearTopTenRankGiven.add((LinearLayout) view.findViewById(R.id.linear_top_spot_given_4));
        mListLinearTopTenRankGiven.add((LinearLayout) view.findViewById(R.id.linear_top_spot_given_5));
        mListLinearTopTenRankGiven.add((LinearLayout) view.findViewById(R.id.linear_top_spot_given_6));
        mListLinearTopTenRankGiven.add((LinearLayout) view.findViewById(R.id.linear_top_spot_given_7));
        mListLinearTopTenRankGiven.add((LinearLayout) view.findViewById(R.id.linear_top_spot_given_8));
        mListLinearTopTenRankGiven.add((LinearLayout) view.findViewById(R.id.linear_top_spot_given_9));
        mListLinearTopTenRankGiven.add((LinearLayout) view.findViewById(R.id.linear_top_spot_given_10));

        mListTextTopTenUsernamesReceived.add((TextView) view.findViewById(R.id.username_1));
        mListTextTopTenUsernamesReceived.add((TextView) view.findViewById(R.id.username_2));
        mListTextTopTenUsernamesReceived.add((TextView) view.findViewById(R.id.username_3));
        mListTextTopTenUsernamesReceived.add((TextView) view.findViewById(R.id.username_4));
        mListTextTopTenUsernamesReceived.add((TextView) view.findViewById(R.id.username_5));
        mListTextTopTenUsernamesReceived.add((TextView) view.findViewById(R.id.username_6));
        mListTextTopTenUsernamesReceived.add((TextView) view.findViewById(R.id.username_7));
        mListTextTopTenUsernamesReceived.add((TextView) view.findViewById(R.id.username_8));
        mListTextTopTenUsernamesReceived.add((TextView) view.findViewById(R.id.username_9));
        mListTextTopTenUsernamesReceived.add((TextView) view.findViewById(R.id.username_10));

        mListTextTopTenUsernamesGiven.add((TextView) view.findViewById(R.id.username_given_1));
        mListTextTopTenUsernamesGiven.add((TextView) view.findViewById(R.id.username_given_2));
        mListTextTopTenUsernamesGiven.add((TextView) view.findViewById(R.id.username_given_3));
        mListTextTopTenUsernamesGiven.add((TextView) view.findViewById(R.id.username_given_4));
        mListTextTopTenUsernamesGiven.add((TextView) view.findViewById(R.id.username_given_5));
        mListTextTopTenUsernamesGiven.add((TextView) view.findViewById(R.id.username_given_6));
        mListTextTopTenUsernamesGiven.add((TextView) view.findViewById(R.id.username_given_7));
        mListTextTopTenUsernamesGiven.add((TextView) view.findViewById(R.id.username_given_8));
        mListTextTopTenUsernamesGiven.add((TextView) view.findViewById(R.id.username_given_9));
        mListTextTopTenUsernamesGiven.add((TextView) view.findViewById(R.id.username_given_10));

    }

    public void initializeChartLabels(View view) {
        mListChartLabels = new ArrayList<>();

        mListChartLabels.add((TextView) view.findViewById(R.id.text_compatibilities_1));
        mListChartLabels.add((TextView) view.findViewById(R.id.text_compatibilities_2));
        mListChartLabels.add((TextView) view.findViewById(R.id.text_compatibilities_3));
        mListChartLabels.add((TextView) view.findViewById(R.id.text_compatibilities_4));
        mListChartLabels.add((TextView) view.findViewById(R.id.text_compatibilities_5));
    }

    public void initializeDate() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat day = new SimpleDateFormat("dd");
        SimpleDateFormat month = new SimpleDateFormat("MMMM", Locale.US);
        SimpleDateFormat year = new SimpleDateFormat("YYYY");

        mDay = day.format(cal.getTime());
        mMonth = month.format(cal.getTime()).toLowerCase();
        mYear = year.format(cal.getTime());
    }

    public long checkThanksValues(List<ThanksValue> listValues) {
        long result = 0;

        for (ThanksValue thanksValue : listValues) {
            result += thanksValue.getValue();
        }

        return result;
    }

    public void initializeRecentThanksViews() {
        mListTextThanksGiven = new ArrayList<TextView>();
        mListTextThanksReceived = new ArrayList<TextView>();
        mListLinearLayoutGiven = new ArrayList<LinearLayout>();
        mListLinearLayoutReceived = new ArrayList<LinearLayout>();
        mListLinearRecentThanks = new ArrayList<>();
        mThanksTypesGivenList = new ArrayList<>();
        mThanksTypesReceivedList = new ArrayList<>();

        if (getActivity() != null) {
            mListTextThanksGiven.add((TextView) mView.findViewById(R.id.text_given_1));
            mListTextThanksGiven.add((TextView) mView.findViewById(R.id.text_given_2));
            mListTextThanksGiven.add((TextView) mView.findViewById(R.id.text_given_3));
            mListTextThanksGiven.add((TextView) mView.findViewById(R.id.text_given_4));
            mListTextThanksGiven.add((TextView) mView.findViewById(R.id.text_given_5));

            mListTextThanksReceived.add((TextView) mView.findViewById(R.id.text_received_1));
            mListTextThanksReceived.add((TextView) mView.findViewById(R.id.text_received_2));
            mListTextThanksReceived.add((TextView) mView.findViewById(R.id.text_received_3));
            mListTextThanksReceived.add((TextView) mView.findViewById(R.id.text_received_4));
            mListTextThanksReceived.add((TextView) mView.findViewById(R.id.text_received_5));

            mListLinearLayoutGiven.add((LinearLayout) mView.findViewById(R.id.linear_layout_given_1));
            mListLinearLayoutGiven.add((LinearLayout) mView.findViewById(R.id.linear_layout_given_2));
            mListLinearLayoutGiven.add((LinearLayout) mView.findViewById(R.id.linear_layout_given_3));
            mListLinearLayoutGiven.add((LinearLayout) mView.findViewById(R.id.linear_layout_given_4));
            mListLinearLayoutGiven.add((LinearLayout) mView.findViewById(R.id.linear_layout_given_5));

            mListLinearLayoutReceived.add((LinearLayout) mView.findViewById(R.id.linear_layout_received_1));
            mListLinearLayoutReceived.add((LinearLayout) mView.findViewById(R.id.linear_layout_received_2));
            mListLinearLayoutReceived.add((LinearLayout) mView.findViewById(R.id.linear_layout_received_3));
            mListLinearLayoutReceived.add((LinearLayout) mView.findViewById(R.id.linear_layout_received_4));
            mListLinearLayoutReceived.add((LinearLayout) mView.findViewById(R.id.linear_layout_received_5));

            mThanksTypesGivenList.add((ImageView) mView.findViewById(R.id.image_thanks_type_1));
            mThanksTypesGivenList.add((ImageView) mView.findViewById(R.id.image_thanks_type_2));
            mThanksTypesGivenList.add((ImageView) mView.findViewById(R.id.image_thanks_type_3));
            mThanksTypesGivenList.add((ImageView) mView.findViewById(R.id.image_thanks_type_4));
            mThanksTypesGivenList.add((ImageView) mView.findViewById(R.id.image_thanks_type_5));

            mThanksTypesReceivedList.add((ImageView) mView.findViewById(R.id.image_thanks_type_received_1));
            mThanksTypesReceivedList.add((ImageView) mView.findViewById(R.id.image_thanks_type_received_2));
            mThanksTypesReceivedList.add((ImageView) mView.findViewById(R.id.image_thanks_type_received_3));
            mThanksTypesReceivedList.add((ImageView) mView.findViewById(R.id.image_thanks_type_received_4));
            mThanksTypesReceivedList.add((ImageView) mView.findViewById(R.id.image_thanks_type_received_5));

            mListLinearRecentThanks.add((LinearLayout) mView.findViewById(R.id.linear_recent_1));
            mListLinearRecentThanks.add((LinearLayout) mView.findViewById(R.id.linear_recent_2));
            mListLinearRecentThanks.add((LinearLayout) mView.findViewById(R.id.linear_recent_3));
            mListLinearRecentThanks.add((LinearLayout) mView.findViewById(R.id.linear_recent_4));
            mListLinearRecentThanks.add((LinearLayout) mView.findViewById(R.id.linear_recent_5));
        }
    }


    public String thanksTypeToString(String thanksType) {
        String type = "";
        if (thanksType != null) {
            if (!thanksType.equals("")) {
                if (getActivity() != null) {
                    switch (thanksType) {
                        case "NORMAL":
                            type = getActivity().getString(R.string.thanked_string);
                            break;
                        case "SUPER":
                            type = getActivity().getString(R.string.super_thanked_string);
                            break;
                        case "MEGA":
                            type = getActivity().getString(R.string.mega_thanked_string);
                            break;
                        case "POWER":
                            type = getActivity().getString(R.string.power_thanked_string);
                            break;
                        case "ULTRA":
                            type = getActivity().getString(R.string.ultra_thanked_string);
                            break;
                    }
                }
            }
        }
        return type;
    }

    private void createTodayThanks(final Thanks thanks, boolean openingOnCreate) {

        if (mTodayThanksString == null) {
            if (getActivity() != null) {
                mTodayThanksString = "";
                if (openingOnCreate) {
                    mTodayThanksString += getActivity().getString(R.string.today) + " " + getActivity().getString(R.string.you_lower) + " ";
                } else {
                    mTodayThanksString += getActivity().getString(R.string.you_upper) + " ";
                }
                String thankType = DataUtils.thanksTypeToString(getActivity(), thanks.getThanksType());
                mTodayThanksString += getActivity().getString(R.string.thanked_today, thankType);

                if (mOtherUser != null) {
                    mTodayThanksString += " " + DataUtils.capitalize(mOtherUser.getName()) + "!";
                }

                //mTodayThanksString = getActivity().getString(R.string.you_thanked_today, thankType, name);

            }
        } else {
            //mRetrievedTodayThanksFromSavedInstance = true;
        }

        if (getActivity() != null) {
            mTextTodayThanks.setText(Html.fromHtml(mTodayThanksString));
            updateLinearTodayBackground(thanks);
        }

        if (thanks.getWasWelcomed()) {
            mImageWelcome.setVisibility(View.VISIBLE);
            int color = Color.parseColor(ImageUtils.pickWelcomeColor(thanks)); //The color u want
            mImageWelcome.setColorFilter(color);
        }

    }

    public void updateLinearTodayBackground(Thanks thanks) {
        GradientDrawable border = (GradientDrawable) mLinearToday.getBackground();

        //Drawable border = getActivity().getResources().getDrawable(R.drawable.custom_border);
        int color = ImageUtils.pickColor(getActivity(), thanks.getThanksType());
        border.setStroke(3, color);
        //border.setColorFilter(color, PorterDuff.Mode.DST_IN );
        //mTextTodayThanks.setBackgroundDrawable(border);
        mLinearToday.setVisibility(View.VISIBLE);
    }

    public void incrementReceivedCountInOtherUser() {
        mOtherUserReceivedCount = mOtherThanksData.getReceivedCount() + 1;
        mOtherThanksData.setReceivedCount(mOtherUserReceivedCount);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void saveThanks(User giver, User receiver, final Thanks thanks) {

        Log.v(TAG, "Checking new Thanks. Country: " + thanks.getCountry());

        showThanksAnimation(thanks);

        final String type = thanks.getThanksType();

        long currentThanksValue = 0;

        initializeDate();

        createTodayThanks(thanks, false);

        switch (type) {
            case "NORMAL":
                currentThanksValue = THANKS_VALUE;
                break;
            case "SUPER":
                currentThanksValue = SUPER_THANKS_VALUE;
                break;
            case "MEGA":
                currentThanksValue = MEGA_THANKS_VALUE;
                break;
            case "POWER":
                currentThanksValue = POWER_THANKS_VALUE;
                break;
            case "ULTRA":
                currentThanksValue = 10000;
                break;
        }

        final long currentThanksValueConstant = currentThanksValue;

        mFirestore.collection(THANKS_DB).add(thanks)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        mThanksKey = documentReference.getId();
                        //documentReference.update("thanksId", mThanksKey);

                        incrementReceivedCountInOtherUser();
                        checkIfMessageUpdateThanksCount();

                        //Commented to run!
                        Log.v(TAG, "Thanks Count: " + mOurThanksData.getThanksCount());
                        if (((mOurThanksData.getThanksCount() + 1) % 15 == 0) && ((mOurThanksData.getThanksCount() + 1) < 100)) {
                            if (getActivity() != null) {
                                String[] arrayTitles = getActivity().getResources().getStringArray(R.array.followup_titles);
                                String[] arraySubtitles = getActivity().getResources().getStringArray(R.array.followup_subtitles);
                                int randIndex = new Random().nextInt(arrayTitles.length);
                                DataUtils.createMessage(mUser.getUserId(), arrayTitles[randIndex], arraySubtitles[randIndex], PLATFORM_MESSAGE, 0);
                            }
                        }

                        //update the Thanks Categories values of Thanks Given in User profile (Giver)
                        mUser.addValueOnCategory(currentThanksValueConstant * 2, thanks.getPrimaryCategory());
                        if (thanks.getSecondaryCategory() != null) {
                            if (!thanks.getSecondaryCategory().equals("")) {
                                mUser.addValueOnCategory(currentThanksValueConstant, thanks.getSecondaryCategory());
                            }
                        }

                        if (getActivity() != null) {
                            DataUtils.sendMessageFromThanks(getActivity(), mOtherUserId, mUser.getName(), mUser.getUserId(), thanks, DataUtils.MSG_SEE_PREMIUM);
                        }

                        updateThanksValuesInCountryAndDate(thanks);

                        //updating thanks received on OtherUser, than updating thanks given on mUser --> For getting Top Ranks in the future
                        updateWhatReceived(thanks);
                        updateWhatGave(thanks);

                        updateOurRecentThanksList();

                        mFirestore.runTransaction(new Transaction.Function<Void>() {
                            @Override
                            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                mFirestore.collection(DB_REFERENCE).document(mUser.getUserId()).set(mUser);
                                mFirestore.collection(DB_REFERENCE).document(mOtherUser.getUserId()).set(mOtherUser);
                                mFirestore.collection(THANKS_DATA).document(mUser.getUserId()).set(mOurThanksData);
                                mFirestore.collection(THANKS_DATA).document(mOtherUser.getUserId()).set(mOtherThanksData);
                                // Success
                                return null;
                            }
                        });

                        mOtherUserThanksReceived.add(new ThanksItem(mUser.getName(), thanks.getThanksType(), thanks.getDate(), thanks.getWasWelcomed()));
                        DataUtils.sortRecentThanks(mOtherUserThanksReceived);
                        updateRecentReceived();
                        updateReceivedValue();
                        updateReceivedSpecial(thanks);
                        updateTopReceived(thanks);
                    }
                });


        mOurThanksData.setThanksCount(mOurThanksData.getThanksCount() + 1);
        mOurThanksData.setGivenThanksValue(mOurThanksData.getGivenThanksValue() + currentThanksValueConstant);

        long thanksCurrency = mOurThanksData.getThanksCurrency();
        long numberOfThanks = mOurThanksData.getThanksCount();

        if (mOurLevelString.equalsIgnoreCase("master")) {
            thanksCurrency += 3;
        } else if (mOurLevelString.equalsIgnoreCase("true")) {
            thanksCurrency += 2;
        } else {
            thanksCurrency++;
        }

        mUser.setThankerLevel(mOurLevelString);

        String titleMessage = "";
        String textMessage = "";

        if (currentThanksValueConstant != 1) {
            thanksCurrency -= currentThanksValueConstant;
        }

        if ((numberOfThanks % SUPER_THANKS_VALUE) == 0) {
            thanksCurrency += SUPER_THANKS_VALUE;

            if (getActivity() != null) {
                titleMessage = getActivity().getString(R.string.you_earn_10_thankers);
                textMessage = getActivity().getString(R.string.prize_10_thankers_text);
                DataUtils.createMessage(mUser.getUserId(), titleMessage, textMessage, PLATFORM_MESSAGE, DataUtils.MSG_SEE_PREMIUM);
            }
        }

        if ((numberOfThanks % MEGA_THANKS_VALUE) == 0) {
            thanksCurrency += MEGA_THANKS_VALUE;

            if (getActivity() != null) {
                titleMessage = getActivity().getString(R.string.you_earn_100_thankers);
                textMessage = getActivity().getString(R.string.prize_100_thankers_text);
                DataUtils.createMessage(mUser.getUserId(), titleMessage, textMessage, PLATFORM_MESSAGE, DataUtils.MSG_SEE_PREMIUM);
            }
        }

        if (numberOfThanks % POWER_THANKS_VALUE == 0) {
            thanksCurrency += POWER_THANKS_VALUE;

            if (getActivity() != null) {
                titleMessage = getActivity().getString(R.string.you_earn_1000_thankers);
                textMessage = getActivity().getString(R.string.prize_1000_thankers_text);
                DataUtils.createMessage(mUser.getUserId(), titleMessage, textMessage, PLATFORM_MESSAGE, DataUtils.MSG_SEE_PREMIUM);
            }
        }

        if (numberOfThanks % 10000 == 0) {
            thanksCurrency += 10000;

            if (getActivity() != null) {
                titleMessage = getActivity().getString(R.string.you_earn_10000_thankers);
                textMessage = getActivity().getString(R.string.prize_10000_thankers_text);
                DataUtils.createMessage(mUser.getUserId(), titleMessage, textMessage, PLATFORM_MESSAGE, DataUtils.MSG_SEE_PREMIUM);
            }
        }

        mOurThanksData.setThanksCurrency(thanksCurrency);

        if (!textMessage.equals("") && !textMessage.equals("")) {
            //DataUtils.createMessage(mUser.getUserId(), titleMessage, textMessage, PLATFORM_MESSAGE);
        }

        if (thanksCurrency <= 0) {
            thanksCurrency = 1;
        }

        mOurThanksData.updateSpecialGiven(thanks);

        //Now updating on the receiver Side

        //mOtherThanksData.setReceivedCount(mOtherThanksData.getReceivedCount() + 1);
        long receivedThanksValue = mOtherThanksData.getReceivedThanksValue();
        receivedThanksValue += currentThanksValueConstant;
        mOtherThanksData.setReceivedThanksValue(receivedThanksValue);
        mOtherThanksData.updateSpecialReceived(thanks);

    }

    public void updateReceivedValue() {
        mTextThanksReceived.setText(String.format("%,d", mOtherThanksData.getReceivedThanksValue()));
    }

    public void updateReceivedSpecial(Thanks thanks) {
        switch (thanks.getThanksType().toLowerCase()) {
            case "super":
                mSuperThanksReceived.setText(String.format("%,d", mOtherThanksData.getSuperThanksReceived()));
                break;
            case "mega":
                mMegaThanksReceived.setText(String.format("%,d", mOtherThanksData.getMegaThanksReceived()));
                break;
            case "power":
                mPowerThanksReceived.setText(String.format("%,d", mOtherThanksData.getPowerThanksReceived()));
                break;
            case "ultra":
                mUltraThanksReceived.setText(String.format("%,d", mOtherThanksData.getUltraThanksReceived()));
                mLinearUltra.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void updateOurRecentThanksList() {
        List<Long> recentThanks = mUser.getRecentThanks();
        List<Long> newList = new ArrayList<>();
        newList.add(System.currentTimeMillis());

        for (Long item : recentThanks) {
            if ((System.currentTimeMillis() - item) < FIVE_DAYS_IN_MILLIS) {
                newList.add(item);
            }
        }

        mUser.setRecentThanks(newList);
    }

    public void updateWhatReceived(final Thanks thanks) {
        List<FriendRank> fromFriends = mOtherUser.getFriends();
        if (mOtherUser.isFriendOf(mUser)) {
            boolean found = false;
            for (int i = 0; i != fromFriends.size() && !found; i++) {
                if (fromFriends.get(i).getUserId().equalsIgnoreCase(mUser.getUserId())) {
                    long received = fromFriends.get(i).getThanksReceivedFrom() + DataUtils.thanksTypeToLong(thanks);
                    fromFriends.get(i).setThanksReceivedFrom(received);
                    fromFriends.get(i).setRankFactor(fromFriends.get(i).getRankFactor() + (DataUtils.thanksTypeToLong(thanks) / 2));
                    mOtherUser.setFriends(fromFriends);
                    found = true;
                }
            }
        }

        List<UserValue> topReceived = mOtherUser.getTopUsersReceived();
        if (mOtherUser.hasBeenThankedBy(mUser)) {
            boolean found = false;
            for (int n = 0; n != topReceived.size() && !found; n++) {
                if (topReceived.get(n).getUserId().equalsIgnoreCase(mUser.getUserId())) {
                    long received = topReceived.get(n).getValueThanks() + DataUtils.thanksTypeToLong(thanks);
                    topReceived.get(n).setValueThanks(received);
                    mOtherUser.setTopUsersReceived(topReceived);
                }
            }
        } else {
            topReceived.add(new UserValue(mUser.getUserId(), DataUtils.thanksTypeToLong(thanks)));
            sortUsersThanks(topReceived);
            mOtherUser.setTopUsersReceived(topReceived);
            if (!mWasIInTopTenThankers && wasIInTopTen(topReceived)) {
                if (getActivity() != null) {

                    String ourName = DataUtils.capitalize(mUser.getName());
                    String otherUserName = DataUtils.capitalize(mOtherUser.getName());

                    String textUserEnteredTopTen = TextUtils.getStringFromArray(getActivity()
                            .getResources().getStringArray(R.array.others_entered_our_top_10));

                    String textWeEnteredTopTen = TextUtils.getStringFromArray(getActivity()
                            .getResources().getStringArray(R.array.we_entered_top_10_text));

                    textUserEnteredTopTen = String.format(textUserEnteredTopTen, ourName, ourName);
                    textWeEnteredTopTen = String.format(textWeEnteredTopTen, otherUserName, otherUserName);

                    DataUtils.createMessage(mOtherUserId,
                            getActivity().getString(R.string.user_entered_your_top_ten, ourName),
                            textUserEnteredTopTen,
                            mUser.getUserId(), DataUtils.MSG_SEE_MY_TOP);

                    DataUtils.createMessage(mUser.getUserId(),
                            getActivity().getString(R.string.you_entered_top_10, otherUserName),
                            textWeEnteredTopTen,
                            mOtherUserId, DataUtils.MSG_SEE_OTHER_TOP);
                }
            }
        }
    }

    public void updateWhatGave(final Thanks thanks) {
        List<FriendRank> toFriends = mUser.getFriends();
        if (mUser.isFriendOf(mOtherUser)) {
            boolean found = false;
            for (int i = 0; i != toFriends.size() && !found; i++) {
                if (toFriends.get(i).getUserId().equalsIgnoreCase(mOtherUser.getUserId())) {
                    long given = toFriends.get(i).getThanksGivenTo() + DataUtils.thanksTypeToLong(thanks);
                    toFriends.get(i).setThanksReceivedFrom(given);
                    toFriends.get(i).setRankFactor(toFriends.get(i).getRankFactor() + DataUtils.thanksTypeToLong(thanks));
                    mUser.setFriends(toFriends);
                    found = true;
                }
            }
        }

        List<UserValue> topGiven = mUser.getTopUsersGiven();
        if (mUser.hasThankedTo(mOtherUser)) {
            boolean found = false;
            for (int n = 0; n != topGiven.size() && !found; n++) {
                if (topGiven.get(n).getUserId().equalsIgnoreCase(mOtherUser.getUserId())) {
                    long given = topGiven.get(n).getValueThanks() + DataUtils.thanksTypeToLong(thanks);
                    topGiven.get(n).setValueThanks(given);
                    mUser.setTopUsersGiven(topGiven);
                }
            }
        } else {
            topGiven.add(new UserValue(mOtherUser.getUserId(), DataUtils.thanksTypeToLong(thanks)));
            mUser.setTopUsersGiven(topGiven);
        }
    }

    public void updateThanksValuesInCountryAndDate(final Thanks thanks) {
        Log.v(TAG, "This is the country:" + mUserCountry);
        mFirestore.runTransaction(new Transaction.Function<Void>() {
            @androidx.annotation.Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                final String country = DataUtils.getEnglishCountry(getActivity(), mUserCountry);
                Query countryQuery = mFirestore.collection(COUNTRIES_REFERENCE)
                        .whereEqualTo("country", country)
                        .whereEqualTo("year", DataUtils.generateYear())
                        .whereEqualTo("month", DataUtils.generateMonth())
                        .whereEqualTo("day", DataUtils.generateDay())
                        .limit(1);

                countryQuery
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                StatsThanks stats = new StatsThanks(country, thanks.getPrimaryCategory(), thanks.getSecondaryCategory(), thanks.getThanksType());
                                StatsThanks existingStats;
                                StatsThanks writeStats = stats;

                                if (queryDocumentSnapshots.size() > 0) {
                                    for (QueryDocumentSnapshot statsSnapshot : queryDocumentSnapshots) {
                                        String docKey = statsSnapshot.getId();
                                        existingStats = statsSnapshot.toObject(StatsThanks.class);
                                        existingStats.addStatsThanksOf(stats);
                                        writeStats = existingStats;
                                        mFirestore.collection(COUNTRIES_REFERENCE).document(docKey).set(writeStats);

                                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading Stats Thanks of today");
                                    }
                                } else {
                                    mFirestore.collection(COUNTRIES_REFERENCE).add(writeStats);
                                }
                            }

                        });

                return null;
            }
        });
    }


    public ArrayList<String> retrieveCategoriesList(Thanks thanks) {
        ArrayList<String> categories = new ArrayList<>();

        switch (thanks.getPrimaryCategory()) {
            case "Person":
                categories.add("personThanks");
                break;
            case "Brand":
                categories.add("brandThanks");
                break;
            case "Business":
                categories.add("businessThanks");
                break;
            case "Nature":
                categories.add("natureThanks");
                break;
            case "Health":
                categories.add("healthThanks");
                break;
            case "Food":
                categories.add("foodThanks");
                break;
            case "Association":
                categories.add("associationThanks");
                break;
            case "Home":
                categories.add("homeThanks");
                break;
            case "Science":
                categories.add("scienceThanks");
                break;
            case "Religion":
                categories.add("religionThanks");
                break;
            case "Sports":
                categories.add("sportsThanks");
                break;
            case "Lifestyle":
                categories.add("lifestyleThanks");
                break;
            case "Technology":
                categories.add("techThanks");
                break;
            case "Fashion":
                categories.add("fashionThanks");
                break;
            case "Education":
                categories.add("educationThanks");
                break;
            case "Games":
                categories.add("gamesThanks");
                break;
            case "Travel":
                categories.add("travelThanks");
                break;
            /*case "Land":
                categories.add("landThanks");
                break;*/
            case "Institutional":
                categories.add("govThanks");
                break;
            case "Beauty":
                categories.add("beautyThanks");
                break;
            case "Finance":
                categories.add("financeThanks");
                break;
            case "Culture":
                categories.add("cultureThanks");
                break;
        }

        if (thanks.getSecondaryCategory() != null) {
            if (!thanks.getSecondaryCategory().equals("")) {
                switch (thanks.getSecondaryCategory()) {
                    case "Person":
                        categories.add("personThanks");
                        break;
                    case "Brand":
                        categories.add("brandThanks");
                        break;
                    case "Business":
                        categories.add("businessThanks");
                        break;
                    case "Nature":
                        categories.add("natureThanks");
                        break;
                    case "Health":
                        categories.add("healthThanks");
                        break;
                    case "Food":
                        categories.add("foodThanks");
                        break;
                    case "Association":
                        categories.add("associationThanks");
                        break;
                    case "Home":
                        categories.add("homeThanks");
                        break;
                    case "Science":
                        categories.add("scienceThanks");
                        break;
                    case "Religion":
                        categories.add("religionThanks");
                        break;
                    case "Sports":
                        categories.add("sportsThanks");
                        break;
                    case "Lifestyle":
                        categories.add("lifestyleThanks");
                        break;
                    case "Technology":
                        categories.add("techThanks");
                        break;
                    case "Fashion":
                        categories.add("fashionThanks");
                        break;
                    case "Education":
                        categories.add("educationThanks");
                        break;
                    case "Games":
                        categories.add("gamesThanks");
                        break;
                    case "Travel":
                        categories.add("travelThanks");
                        break;
                    /*case "Land":
                        categories.add("landThanks");
                        break;*/
                    case "Institutional":
                        categories.add("govThanks");
                        break;
                    case "Beauty":
                        categories.add("beautyThanks");
                        break;
                    case "Finance":
                        categories.add("financeThanks");
                        break;
                    case "Culture":
                        categories.add("cultureThanks");
                        break;
                }
            }
        }

        return categories;
    }

    public void sortUsersThanks(List<UserValue> usersValues) {
        Collections.sort(usersValues, new Comparator<UserValue>() {
            @Override
            public int compare(UserValue o1, UserValue o2) {
                return Long.compare(o2.getValueThanks(), o1.getValueThanks());
            }
        });
    }

    public void combineSortMapsOfThanksAndCreateJointChart() {

        createCombinedThanksMap();

        if (mCombinedRelativeThanksMap.size() > 0) {

            if (getActivity() != null) {
                createChart(mCombinedRelativeThanksMap,
                        getActivity().getBaseContext().getString(R.string.combined_users_thanks), mCombinedPieChart, mCombinedTextLabel, TYPE_COMBINED_THANKS);
            }
        } else {
            mLinearCompatibilities.setVisibility(View.GONE);
            mCombinedTextLabel.setVisibility(View.GONE);
            mCombinedPieChart.setVisibility(View.GONE);
        }
    }

    public void createCombinedThanksMap() {
        makeCombinedThanksList();
        mCombinedRelativeThanksMap = new ArrayList<>();

        long sharedThanksValues = getSharedThanksValues(mCombinedThanksMap);
        int keyCheck = 0; //for controlling the instances that are actually added to the joint Relative Thanks Map

        for (int i = 0; i != mCombinedThanksMap.size(); i++) {
            String key = mCombinedThanksMap.get(i).getKey();
            Log.v(TAG, "Creating combined thanks. " + mCombinedThanksMap.get(i).getValue());
            Log.v(TAG, "Creating combined thanks. Shared thanks: " + sharedThanksValues);
            long relativeValue = Math.round((100 * mCombinedThanksMap.get(i).getValue()) / sharedThanksValues);
            Log.v(TAG, "Creating combined thanks. " + (100 * mCombinedThanksMap.get(i).getValue()) / sharedThanksValues + "%");
            if (!doesThanksMapContainKey(mCombinedRelativeThanksMap, key)) {
                mCombinedRelativeThanksMap.add(new ThanksValue(key, relativeValue));
                Log.v(TAG, "Combined Thanks List Relative One. Key: " + mCombinedRelativeThanksMap.get(keyCheck).getKey() + ", Value: " + mCombinedRelativeThanksMap.get(keyCheck).getValue());
                keyCheck++;
            }
        }
        mCombinedRelativeThanksMap.add(new ThanksValue("", 0));
        Log.v(TAG, "Checking combined thanks map. Number of shared categories: " + mCombinedRelativeThanksMap.size());
    }

    private boolean doesThanksMapContainKey(List<ThanksValue> map, String key) {
        for (ThanksValue value : map) {
            if (value.getKey().equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    private void makeCombinedThanksList() {
        mCombinedThanksMap = new ArrayList<>();
        List<ThanksValue> tempList = new ArrayList<>();

        for (ThanksValue thanksValue : mThanksMapOtherUser) {
            if (thanksValue.getValue() > 0) {
                String key = thanksValue.getKey();
                Log.v(TAG, "Creating combined thanks. Key in other User: " + key + ". Value: " + thanksValue.getValue());
                for (ThanksValue ourThanksValue : mThanksMapOurUser) {
                    if (ourThanksValue.getKey().equalsIgnoreCase(key) && ourThanksValue.getValue() > 0) {
                        if (!doesThanksMapContainKey(mCombinedThanksMap, ourThanksValue.getKey())) {
                            Log.v(TAG, "Creating combined thanks. Key in our User: " + ourThanksValue.getKey() + ". Value: " + ourThanksValue.getValue());
                            mCombinedThanksMap.add(new ThanksValue(key, thanksValue.getValue() + ourThanksValue.getValue()));
                        }
                    }
                }
            }
        }

        sortThanksValues(mCombinedThanksMap);
        tempList = mCombinedThanksMap;

        mCombinedThanksMap = new ArrayList<>();

        for (int i = 0; i != 5 && i != tempList.size(); i++) {
            mCombinedThanksMap.add(tempList.get(i));
            Log.v(TAG, "Combined Thanks List. Key: " + mCombinedThanksMap.get(i).getKey() + ", Value: " + mCombinedThanksMap.get(i).getValue());
        }

    }

    private long getSharedThanksValues() {
        long result = 0;
        List<String> sharedKeys = new ArrayList<>();

        for (ThanksValue thanksValue : mThanksMapOtherUser) {
            if (thanksValue.getValue() > 0) {
                String key = thanksValue.getKey();
                for (ThanksValue ourThanksValue : mThanksMapOurUser) {
                    if (ourThanksValue.getKey().equalsIgnoreCase(key) && ourThanksValue.getValue() > 0) {
                        if (!DataUtils.doesListContainItem(sharedKeys, ourThanksValue.getKey())) {
                            result += thanksValue.getValue() + ourThanksValue.getValue();
                        }
                    }
                }
            }
        }

        return result;
    }

    private long getSharedThanksValues(List<ThanksValue> list) {
        long result = 0;

        for (ThanksValue item : list) {
            result += item.getValue();
        }

        return result;
    }

    public void decolorCardViews() {
        if (getActivity() != null) {
            mCardThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.circlenone));
            mCardSuperThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.circlenone));
            mCardMegaThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.circlenone));
            mCardPowerThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.circlenone));
            mCardUltraThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.circlenone));
        }
    }

    public void logThanksGivenCategories(List<ThanksValue> thanksValues) {
        for (ThanksValue value : thanksValues) {
            Log.v(TAG, "Thanks Values Given. " + value.getKey() + ": " + value.getValue());
        }
    }

    public void createChart(List<ThanksValue> thanksValuesMap, String string, PieChart
            pieChart, TextView label, String type) {

        int mapSize = getRealMapSize(thanksValuesMap);

        if (thanksValuesMap.size() == 0) {
            pieChart.setVisibility(View.GONE);
            mCombinedTextLabel.setVisibility(View.GONE);
        } else {

            mCombinedTextLabel.setVisibility(View.VISIBLE);
            sortThanksValues(thanksValuesMap);

            if (thanksValuesMap.size() > 0) {

                ArrayList entries = getDataEntries(thanksValuesMap);

                writeCompatibilities(thanksValuesMap);

                String title = "";

                //this will work because we currently generate only one chart with this library
                if (getActivity() != null) {
                    //layout_title = getActivity().getBaseContext().getString(R.string.main_thanks_of) + " " + string;
                    //mCombinedTextLabel.setText(getActivity().getString(R.string.combined_thanks_of_you_and, mOtherUserName) + " (%)");
                }
                PieDataSet dataSet = new PieDataSet(entries, null); //layout_title = null

                ArrayList categories = generateThankedCategoryNames(thanksValuesMap);

                PieData data = new PieData(categories, dataSet);
                pieChart.setData(data);

                if (type.equals(TYPE_OWN_THANKS)) {
                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                } else {
                    dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                }

                pieChart.animateXY(3000, 3000);

                pieChart.setDescription(null);

                pieChart.setUsePercentValues(false);

                logTopCategories(entries, categories);

                Legend legend = pieChart.getLegend();
                legend.setEnabled(false);

                pieChart.setDrawSliceText(false);
                pieChart.setDrawMarkerViews(false);
                pieChart.getData().setDrawValues(false);

                pieChart.invalidate();

                if (isChartable(thanksValuesMap)) {
                    Log.v(TAG, "Charts: " + pieChart.toString() + " is chartable");
                    if (getActivity() != null) {
                        label.setText(getActivity().getBaseContext().getString(R.string.combined_users_thanks));
                    }
                    pieChart.setVisibility(View.VISIBLE);
                } else {
                    Log.v(TAG, "Charts: " + pieChart.toString() + " isn't chartable");
                    pieChart.setVisibility(View.GONE);
                    if (getActivity() != null) {
                        label.setText(getActivity().getBaseContext().getString(R.string.no_data_yet));
                        label.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    public void writeCompatibilities(List<ThanksValue> thanksValueMap) {

        for (int i = 0; i != thanksValueMap.size() - 1 && i != 5; i++) {
            if (getActivity() != null) {
                String text = getActivity().getString(R.string.add_thanks_compatibility,
                        thanksValueMap.get(i).getValue(),
                        DataUtils.translateAndFormat(getActivity(), DataUtils.thanksCategoryToStringCategory(thanksValueMap.get(i).getKey()))) + "\n";
                mListChartLabels.get(i).setText(text);
                mListChartLabels.get(i).setVisibility(View.VISIBLE);
            }
        }
    }

    private int getRealMapSize(List<ThanksValue> list) {
        int result = 0;

        for (ThanksValue value : list) {
            if (value.getValue() > 0) {
                result++;
            }
        }
        return result;
    }

    public void logTopCategories(ArrayList names, ArrayList sections) {
        for (int i = 0; i != names.size(); i++) {
            Log.v(TAG, "Categories listing Here: " + names.get(i).toString() + ": " + sections.get(i).toString());
        }
    }

    public ArrayList generateThankedCategoryNames(List<ThanksValue> values) {
        ArrayList results = new ArrayList<>();

        if (getActivity() != null) {
            for (int i = 0; i != values.size() && i != 5; i++) {
                if (values.get(i).getValue() > 0) {
                    results.add(DataUtils.decapitalize(translate(thanksCategoryToStringCategory(values.get(i).getKey()))));
                }
            }
        }

        return results;
    }

    public String translate(String word) {

        if (getActivity() != null) {
            List<String> englishStrings = DataUtils.getTranslatableStrings(getActivity());
            String[] ourLanguageStrings = getActivity().getResources().getStringArray(R.array.translatable_pages);

            for (int i = 0; i != englishStrings.size(); i++) {
                if (word.equalsIgnoreCase(englishStrings.get(i))) {
                    return ourLanguageStrings[i];
                }
            }
        }

        return "";
    }

    public String thanksCategoryToStringCategory(String s) {
        String result = "";

        if (s != null) {
            switch (s) {
                case "personThanks":
                    result = "People";
                    break;
                case "brandThanks":
                    result = "Brands";
                    break;
                case "businessThanks":
                    result = "Business";
                    break;
                case "natureThanks":
                    result = "Nature";
                    break;
                case "healthThanks":
                    result = "Health";
                    break;
                case "foodThanks":
                    result = "Food";
                    break;
                case "associationThanks":
                    result = "Associations";
                    break;
                case "homeThanks":
                    result = "Home";
                    break;
                case "scienceThanks":
                    result = "Science";
                    break;
                case "religionThanks":
                    result = "Religion";
                    break;
                case "sportsThanks":
                    result = "Sports";
                    break;
                case "lifestyleThanks":
                    result = "Lifestyle";
                    break;
                case "techThanks":
                    result = "Technology";
                    break;
                case "fashionThanks":
                    result = "Fashion";
                    break;
                case "educationThanks":
                    result = "Education";
                    break;
                case "gamesThanks":
                    result = "Games";
                    break;
                case "travelThanks":
                    result = "Travel";
                    break;
                case "govThanks":
                    result = "Institutional";
                    break;
                case "beautyThanks":
                    result = "Beauty";
                    break;
                case "cultureThanks":
                    result = "Culture";
                    break;
                case "financeThanks":
                    result = "Finance";
                    break;
            }
        }

        return result;
    }

    public boolean isChartable(List<ThanksValue> thanksValues) {

        for (ThanksValue value : thanksValues) {
            if (value.getValue() > 0) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Entry> getDataEntries(List<ThanksValue> values) {
        ArrayList<Entry> data = new ArrayList<>();

        if (values.size() > 0) {
            Log.v(TAG, "Charting: will create chart for " + values.toString());
            for (int i = 0; i != 5 && i != (values.size() - 1); i++) {
                ThanksValue thanksValue = values.get(i);
                if (thanksValue.getValue() > 0) {
                    Entry entry = new Entry(thanksValue.getValue(), i);
                    data.add(entry);
                }
            }
        } else {
            Log.v(TAG, "Charting: won't create for " + values.toString());
        }

        return data;
    }

    public void sortThanksValues(List<ThanksValue> thanksValues) {
        Collections.sort(thanksValues, new Comparator<ThanksValue>() {
            @Override
            public int compare(ThanksValue o1, ThanksValue o2) {
                return Long.compare(o2.getValue(), o1.getValue());
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void powerUpButtons() {
        Log.v("MainSnapThanks", "Value of hasThankedToday: " + mHasThankedToday);
        Log.v(TAG, "New OtherProfile. Entering Power Up Buttons. Our Thanks Currency: " + mOurThanksCurrency);

        if (mUser != null) {

            if (getActivity() != null) {

                String currentCountry = mUserCountry;

                if (currentCountry != null) {
                    if (currentCountry.equals("")) {
                        currentCountry = mUser.getLivingCountry();
                    }
                } else {
                    currentCountry = mUser.getLivingCountry();
                }

                final String countryString = currentCountry;

                if (!mHasThankedToday) {
                    if (getActivity() != null) {
                        mActivateThanksButton.setClickable(true);
                        if (!mActivatedThanks) {
                            mActivateThanksButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_green));
                        }
                    }
                    mButtonThanks.setBackgroundTintList(mThanksColor);
                    mButtonThanks.setClickable(true);
                    mButtonThanks.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View v) {
                            long date = System.currentTimeMillis();
                            mThanks = new Thanks(mUser.getUserId(), mOtherUserId, "", date, mOtherUser.getPrimaryCategory(),
                                    mOtherUser.getSecondaryCategory(), mYear, mMonth, mDay, countryString, "NORMAL");
                            //mSwitchPrivacy.setThumbTintList(getSwitchColor("normal"));
                            //Toast.makeText(getContext(), getString(R.string.you_thanked) + " " + DataUtils.capitalize(mOtherUser.getName()), Toast.LENGTH_LONG).show();
                            //activateThanksDescription();
                            decolorCardViews();
                            if (getActivity() != null) {
                                mCardThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.circlethanks));
                                mActivateThanksButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_green));
                                mActivateThanksButton.setText(getActivity().getString(R.string.thanks));
                                mTextValueLabel.setText("Thanks x1");
                                mTextValueLabel.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                                mThankersSpend = 0; //In reality, we always earn 1 more Thanker, for every thanks given
                                checkReturnThankers();
                            }
                            mThanksType = TYPE_THANKS;
                        }
                    });

                    if (mOurThanksCurrency >= SUPER_THANKS_VALUE) {
                        mButtonSuperThanks.setBackgroundTintList(mSuperThanksColor);
                        mButtonSuperThanks.setClickable(true);
                        //mButtonSuperThanks.append(" (" + superThanks + ")");
                        mButtonSuperThanks.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(View v) {
                                long date = System.currentTimeMillis();
                                mThanks = new Thanks(mUser.getUserId(), mOtherUserId, "", date, mOtherUser.getPrimaryCategory(),
                                        mOtherUser.getSecondaryCategory(), mYear, mMonth, mDay, countryString, "SUPER");
                                //mSwitchPrivacy.setThumbTintList(getSwitchColor("super"));
                                //Toast.makeText(getContext(), getString(R.string.you_thanked) + " " + DataUtils.capitalize(mOtherUser.getName()), Toast.LENGTH_LONG).show();
                                //activateThanksDescription();
                                decolorCardViews();
                                if (getActivity() != null) {
                                    mCardSuperThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.circlesuper));
                                    mActivateThanksButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_super_thanks));
                                    mActivateThanksButton.setText(getActivity().getString(R.string.super_thanks));
                                    mTextValueLabel.setText("Thanks x10");
                                    mTextValueLabel.setTextColor(getActivity().getResources().getColor(R.color.superThanksCoin));
                                    mThankersSpend = SUPER_THANKS_VALUE;
                                    checkReturnThankers();
                                }
                                mThanksType = TYPE_SUPER;
                            }
                        });
                    } else {
                        //mButtonSuperThanks.setEnabled(false);
                        //mButtonSuperThanks.setBackgroundTintList(mDisabledColor);
                        mButtonSuperThanks.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.superthanksoff));
                        mButtonSuperThanks.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (getActivity() != null) {
                                    Toast.makeText(getActivity(), getActivity().getString(R.string.thankers_for_super), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    if (mOurThanksCurrency >= MEGA_THANKS_VALUE) {
                        mButtonMegaThanks.setBackgroundTintList(mMegaThanksColor);
                        mButtonMegaThanks.setClickable(true);
                        //mButtonMegaThanks.append(" (" + megaThanks + ")");
                        mButtonMegaThanks.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(View v) {
                                long date = System.currentTimeMillis();
                                mThanks = new Thanks(mUser.getUserId(), mOtherUserId, "", date, mOtherUser.getPrimaryCategory(),
                                        mOtherUser.getSecondaryCategory(), mYear, mMonth, mDay, countryString, "MEGA");
                                //mSwitchPrivacy.setThumbTintList(getSwitchColor("mega"));
                                //Toast.makeText(getContext(), getString(R.string.you_thanked) + " " + DataUtils.capitalize(mOtherUser.getName()), Toast.LENGTH_LONG).show();
                                //activateThanksDescription();
                                decolorCardViews();
                                if (getActivity() != null) {
                                    mCardMegaThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.circlemega));
                                    ;
                                    mActivateThanksButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_mega_thanks));
                                    mActivateThanksButton.setText(getActivity().getString(R.string.mega_thanks));
                                    mTextValueLabel.setText("Thanks x100");
                                    mTextValueLabel.setTextColor(getActivity().getResources().getColor(R.color.megaThanksCoin));
                                    mThankersSpend = MEGA_THANKS_VALUE;
                                    checkReturnThankers();
                                }
                                mThanksType = TYPE_MEGA;
                            }
                        });
                    } else {
                        //mButtonMegaThanks.setEnabled(false);
                        //mButtonMegaThanks.setBackgroundTintList(mDisabledColor);
                        mButtonMegaThanks.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.megathanksoff));
                        mButtonMegaThanks.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (getActivity() != null) {
                                    Toast.makeText(getActivity(), getActivity().getString(R.string.thankers_for_mega), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    if (mOurThanksCurrency >= POWER_THANKS_VALUE) {
                        mButtonPowerThanks.setBackgroundTintList(mPowerThanksColor);
                        mButtonPowerThanks.setClickable(true);
                        //mButtonPowerThanks.append(" (" + powerThanks + ")");
                        mButtonPowerThanks.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(View v) {
                                long date = System.currentTimeMillis();
                                mThanks = new Thanks(mUser.getUserId(), mOtherUserId, "", date, mOtherUser.getPrimaryCategory(),
                                        mOtherUser.getSecondaryCategory(), mYear, mMonth, mDay, countryString, "POWER");
                                //mSwitchPrivacy.setThumbTintList(getSwitchColor("power"));
                                //Toast.makeText(getContext(), getString(R.string.you_thanked) + " " + DataUtils.capitalize(mOtherUser.getName()), Toast.LENGTH_LONG).show();
                                //activateThanksDescription();
                                decolorCardViews();
                                if (getActivity() != null) {
                                    mCardPowerThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.circlepower));
                                    mActivateThanksButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_power_thanks));
                                    mActivateThanksButton.setText(getActivity().getString(R.string.power_thanks));
                                    mTextValueLabel.setText("Thanks x1000");
                                    mTextValueLabel.setTextColor(getActivity().getResources().getColor(R.color.powerThanksCoin));
                                    mThankersSpend = POWER_THANKS_VALUE;
                                    checkReturnThankers();
                                }
                                mThanksType = TYPE_POWER;
                            }
                        });
                    } else {
                        //mButtonPowerThanks.setEnabled(false);
                        //mButtonPowerThanks.setBackgroundTintList(mDisabledColor);
                        mButtonPowerThanks.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.powerthanksoff));
                        mButtonPowerThanks.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (getActivity() != null) {
                                    Toast.makeText(getActivity(), getActivity().getString(R.string.thankers_for_power), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    if (mOurThanksCurrency >= 10000) {
                        mCardUltraThanks.setVisibility(View.VISIBLE);
                        mButtonUltraThanks.setClickable(true);
                        //mButtonPowerThanks.append(" (" + powerThanks + ")");
                        mButtonUltraThanks.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(View v) {
                                long date = System.currentTimeMillis();
                                mThanks = new Thanks(mUser.getUserId(), mOtherUserId, "", date, mOtherUser.getPrimaryCategory(),
                                        mOtherUser.getSecondaryCategory(), mYear, mMonth, mDay, countryString, "POWER");
                                //mSwitchPrivacy.setThumbTintList(getSwitchColor("ultra"));
                                //Toast.makeText(getContext(), getString(R.string.you_thanked) + " " + DataUtils.capitalize(mOtherUser.getName()), Toast.LENGTH_LONG).show();
                                //activateThanksDescription();
                                decolorCardViews();
                                if (getActivity() != null) {
                                    mCardUltraThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.circleultra));
                                    mActivateThanksButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_indigo));
                                    mActivateThanksButton.setText(getActivity().getString(R.string.ultra_thanks));
                                    mTextValueLabel.setText("Thanks x10.000");
                                    mTextValueLabel.setTextColor(getActivity().getResources().getColor(R.color.indigo));
                                    mThankersSpend = 10000;
                                    checkReturnThankers();
                                }
                                mThanksType = TYPE_ULTRA;
                            }
                        });
                    } else {

                        //mCardUltraThanks.setVisibility(View.GONE);
                    }

                } else {
                    mActivateThanksButton.setText(getActivity().getString(R.string.thanks_again_tomorrow));
                    mActivateThanksButton.setEnabled(false);
                    mActivateThanksButton.setClickable(false);
                    greyAllButtons();
                }
            }
        }

        mProgressBar.setVisibility(View.GONE);
        Log.v("MainSnapshot", "Run all powerButtons method");
    }

    private void checkReturnThankers() {
        if (getActivity() != null) {
            long updatedThankers = calculateUpdatedThankers();
            String thankersText = getActivity().getString(R.string.future_thankers, (updatedThankers - mThankersSpend));

            if (mHasBonus) {
                mTextBonusThankers.setText(Html.fromHtml(getActivity().getString(R.string.bonus_thankers_string)));
                mTextBonusThankers.setVisibility(View.VISIBLE);
            }

            mTextFutureThankers.setText(Html.fromHtml(thankersText));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showThanksAnimation(Thanks thanks) {

        if (getActivity() != null) {

            View thanksView = Utils.getThanksView(getActivity(), thanks);
            ImageView imageThanks = thanksView.findViewById(R.id.fullimage);

            PopupWindow pw = new PopupWindow(thanksView, 480, 480, true);
            pw.showAtLocation(mView, Gravity.CENTER, 0, 0);
            Glide.with(getActivity()).load(ImageUtils.getThanksAnimation(getActivity(), thanks)).into(imageThanks);
            imageThanks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.dismiss();
                }
            });
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pw.dismiss();
                }
            }, 5000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void greyAllButtons() {
        mButtonThanks.setEnabled(false);
        mButtonThanks.setBackgroundTintList(mDisabledColor);
        mButtonSuperThanks.setEnabled(false);
        mButtonSuperThanks.setBackgroundTintList(mDisabledColor);
        mButtonMegaThanks.setEnabled(false);
        mButtonMegaThanks.setBackgroundTintList(mDisabledColor);
        mButtonPowerThanks.setEnabled(false);
        mButtonPowerThanks.setBackgroundTintList(mDisabledColor);
        mButtonUltraThanks.setEnabled(false);
        mButtonUltraThanks.setBackgroundTintList(mDisabledColor);

    }

    private void updateTodayThanks(String reason) {
        if (mCountShowTodayThanks <= 1 && !mTodayThanksString.contains(": \"")) {
            mTodayThanksString = mTodayThanksString.substring(0, mTodayThanksString.length() - 1);
            mTodayThanksString += ": \"" + reason + "\"";

            if (!DataUtils.isPunctuation(mTodayThanksString.charAt(mTodayThanksString.length() - 2))) {
                mTodayThanksString += "!";
            }

            mTextTodayThanks.setText(Html.fromHtml(mTodayThanksString));

            mIsTodayThanksComplete = true;
        }

        mCountShowTodayThanks += 2;
    }

    public void checkIfMessageUpdateThanksCount() {
        if (getActivity() != null) {
            String title = getActivity().getString(R.string.upped_level);
            String body = "";
            boolean sendMessage = false;
            double averageThanks = (mUser.getNumberRecentThanks() + 1) / 5; //the average of the last 5 days

            Log.v(TAG, "Checking up level situations. Recent Thanks Count (5 days): " + (mUser.getNumberRecentThanks() + 1) + ". Average/day: " + averageThanks);

            if (mOurLevelString != null) {
                if (!mOurLevelString.equals("")) {

                    if (mOurLevelString.equalsIgnoreCase("starter") && averageThanks >= 2) {
                        sendMessage = true;
                        body = getActivity().getString(R.string.upped_to_walker);
                    } else if (mOurLevelString.equalsIgnoreCase("walker") && averageThanks >= 5) {
                        sendMessage = true;
                        body = getActivity().getString(R.string.upped_to_explorer);
                    } else if (mOurLevelString.equalsIgnoreCase("explorer") && averageThanks >= 7) {
                        sendMessage = true;
                        body = getActivity().getString(R.string.upped_to_true);
                    } else if (mOurLevelString.equalsIgnoreCase("true") && averageThanks >= 10) {
                        sendMessage = true;
                        body = getActivity().getString(R.string.upped_to_master);
                    }

                    if (sendMessage) {
                        DataUtils.createMessage(mUser.getUserId(), title, body, PLATFORM_MESSAGE, DataUtils.MSG_SEE_PREMIUM);
                    }
                }
            }
        }
    }

    private boolean hasBonus(long ownThanksCount) {
        return ((ownThanksCount + 1) % 10 == 0 || (ownThanksCount + 1) % 100 == 0 || (ownThanksCount + 1) % 1000 == 0);
    }

    private long calculateUpdatedThankers() {
        long calculateThankers = mOurThanksCurrency + 1;
        long ownThanksCount = 0;

        if (mOurThanksData != null) {
            ownThanksCount = mOurThanksData.getThanksCount();
        }

        if ((ownThanksCount + 1) % 10 == 0) {
            calculateThankers += 10;
        }

        if ((ownThanksCount + 1) % 100 == 0) {
            calculateThankers += 100;
        }

        if ((ownThanksCount + 1) % 1000 == 0) {
            calculateThankers += 1000;
        }

        Log.v(TAG, "Calculating Thankers to return. Current ThanksCounty: " + ownThanksCount);

        return calculateThankers;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        if (mInputThanksReason != null) {
            if (mInputThanksReason.getText() != null) {
                mReasonString = mInputThanksReason.getText().toString();
            }
        }

        state.putBoolean("hasWrittenReason", mHasWrittenThanksReason);
        state.putBoolean("hasThankedToday", mHasThankedToday);
        state.putBoolean("hasSavedReason", mHasClickedSaveButtonReason);
        state.putString("reasonText", mReasonString);
        state.putString("todayThanksText", mTodayThanksString);
        state.putInt("countThanksShown", mCountShowTodayThanks);
        state.putBoolean("is-thanks-complete", mIsTodayThanksComplete);
        state.putString(TYPE_SAVED, mThanksType);
        state.putBoolean("control-given", mControlRecentGiven);
        state.putBoolean("show-description", mShowDescription);
        state.putBoolean("hold-today", mHoldToday);

        state.putInt("fragment", FRAGMENT_OTHER_PROFILE);
        state.putString("otherUserid", mOtherUserId);
        state.putBoolean(ACTIVATED_THANKS, mActivatedThanks);
        Log.v(TAG, "Saving activated thanks. Value of mActivatedThanks: " + mActivatedThanks);

        if (mRecentThanksKey != null && !mRecentThanksKey.equals("")) {
            state.putString("recentThanksKey", mRecentThanksKey);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity = (AppCompatActivity) getActivity();
        mActionBar = mActivity.getSupportActionBar();

        mActionBar.setSubtitle(null);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        mActionBar.setHomeAsUpIndicator(upArrow);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mContinueSendingId) {
            if (getActivity() != null) {
                byte[] userIdInBytes = mAuth.getCurrentUser().getUid().getBytes();
                mSendIdMessage = new Message(userIdInBytes);
                Nearby.getMessagesClient(getActivity()).publish(mSendIdMessage);

                mStoppedSendingId = false;

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            Nearby.getMessagesClient(getActivity()).unpublish(mSendIdMessage);
                            mStoppedSendingId = true;
                        }
                    }
                }, 5000);
            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();

        if (mUserCountry != null) {

        }

        if (mTodayThanksListener != null) {
            mTodayThanksListener.remove();
        }

        if (mContinueSendingId) {
            if (!mStoppedSendingId) {
                if (getActivity() != null) {
                    Nearby.getMessagesClient(getActivity()).unpublish(mSendIdMessage);
                }
            }
        }

        //put custom border color back to Primary
        if (getActivity() != null) {
            GradientDrawable border = (GradientDrawable) mLinearToday.getBackground();
            border.setStroke(2, getActivity().getResources().getColor(R.color.colorPrimary));
        }
    }

}
