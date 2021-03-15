package co.thanker.fragments;

import android.Manifest;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

import br.com.goncalves.pugnotification.notification.PugNotification;
import co.thanker.InviteActivity;
import co.thanker.MainActivity;
import co.thanker.PersonalStatsActivity;
import co.thanker.R;
import co.thanker.data.BirthdayTimes;
import co.thanker.data.ListInvites;
import co.thanker.data.StatsThanks;
import co.thanker.data.Thanks;
import co.thanker.data.ThanksData;
import co.thanker.data.ThanksInvite;
import co.thanker.data.ThanksItem;
import co.thanker.data.ThanksValue;
import co.thanker.data.User;
import co.thanker.data.UserValue;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;
import co.thanker.utils.MultiColorCircle;
import co.thanker.utils.PathUtils;
import co.thanker.utils.TextUtils;
import co.thanker.utils.Utils;

import static android.app.Activity.RESULT_OK;

public class MyProfileFragment extends Fragment {

    private final String TAG = "MyProfileFragment";
    private final String THANKS_DB = "thanks-db";
    private final String STATS_THANKS = "stats-thanks";
    private final String THANKS_DATA = "thanks-data";
    private static final String DB_REFERENCE = "users";
    private final String TOP_REF = "tops";
    private final String FRIENDS_DB = "friends-db";
    private final String IMAGES_DB = "images-db";
    private final String PREMIUM_REFERENCE = "premium-info";
    private final String SAVE_IMAGES_USERS = "users-profile-pictures";
    private final String INVITE_EMAIL = "invite-email";
    private final String INVITE_REF = "invites";
    private final String COUNTRIES_REFERENCE = "countries-values";
    private static final String SMS_REFERENCE = "sms-invites";
    private final String USER_OBJECT = "user-object";
    private final String USER_SNIPPET = "user-snippet";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String LISTING_TYPE = "listing-type";
    private final String DYNAMIC_GIVER = "dynamic-giver";
    private final String DYNAMIC_RECEIVER = "dynamic-receiver";
    private final String THANKS_DYNAMIC = "thanks-dynamic";
    private final String MESSAGES_REFERENCE = "messages-list";
    private final String THANKS_GIVEN = "thanks-given";
    private final String IS_PREMIUM = "has-welcomes";
    private final String THANKS_RECEIVED = "thanks-received";
    private final String TOP_USERS_THANKS_RECEIVED = "top-users-thanks-received";
    private final String TOP_USERS_THANKS_GIVEN = "top-users-thanks-given";
    private final String LIST_THANKS_VALUES = "list-thanks-values";
    private static final String THANKS_REFERENCE = "thanks-test";
    private final String IMAGE_URL = "imageUrl";
    private final String LAST_LOGIN_TIME = "lastLogin";
    private final String USER_ID_STRING = "user-id-string";
    private static final String OPEN_ANOTHER_PROFILE = "open-another-profile";
    private final String LAST_REGISTERED_RECEIVED_THANKS = "last-registered-received-thanks";
    private final String PREFS_BIRTHDAY = "prefs-birthday";
    private final String NUMBER_INVITES = "number-invites";
    private final String INVITES_EMAIL = "invites-email";
    private final String INVITES_SMS = "invites-sms";
    private final String COUNTRY_PREFS = "country-prefs";

    private final int RC_PHOTO_PICKER = 11;
    private final int RC_STORAGE = 12;
    private final long THREE_HOURS = 10800000;
    private final int THANKS_VALUE = 1;
    private final int FIVE_DAYS = 5;
    private final int SUPER_THANKS_VALUE = 10;
    private final int MEGA_THANKS_VALUE = 100;
    private final int POWER_THANKS_VALUE = 1000;
    private final long FIVE_DAYS_IN_MILLIS = 432000000;
    private final int PRIZE_INVITE = 50;
    private final int PERC_QUALITY_IMAGE = 60;

    private ActionBar mActionBar;
    private TextView mNameView;
    private TextView mTextCurrency;
    private TextView mGivenThanks;
    private TextView mReceivedThanks;
    private TextView mTextStatusThanking;
    private TextView mTextStatusLabel;
    private TextView mTextThanksCount;
    private TextView mTextNewThanksReceivedNumber;
    private TextView mTextNewThanksReceivedLabel;
    private TextView mTextCategories;
    private TextView mTextNumberFriends;
    private TextView mTextThanksCurrency;
    private TextView mCuteMessage;
    private TextView mSuperGiven;
    private TextView mSuperReceived;
    private TextView mMegaGiven;
    private TextView mMegaReceived;
    private TextView mPowerGiven;
    private TextView mPowerReceived;
    private TextView mUltraGiven;
    private TextView mUltraReceived;
    private TextView mMyThanksLabel;
    private TextView mTextDiaryThanks;
    private ImageView mProfileImage;
    private ImageView mImageLabel;
    private ImageView mOuterCircle;
    private ImageView mImageInfoLevel;
    private ImageView mImageInfoThankers;
    private ImageView mImageInfoDiary;
    private ImageView mImagePictureInfo;
    private ImageView mImageInfoThanksCount;
    private LinearLayout mLinearCategoriesThanked;
    private ProgressBar mProgressBar;
    private LinearLayout mLinearStatus;
    private LinearLayout mLinearStatsOne;
    private LinearLayout mLinearStatsTwo;
    private LinearLayout mLinearStatsThree;
    private LinearLayout mLinearStatsFour;
    private LinearLayout mLinearStatsFive;
    private LinearLayout mLinearThankers;
    private LinearLayout mLinearCount;
    private LinearLayout mLinearRecentMessage;
    private LinearLayout mLinearUltra;
    private LinearLayout mLinearDiary;
    private LinearLayout mLinearImage;
    private Button mGivenButton;
    private Button mReceivedButton;
    private MultiColorCircle mStatusCircle;


    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DocumentReference mFireUser;

    private FirebaseStorage mUsersImagesStorage;
    private StorageReference mStorageReference;
    private DocumentReference mInvitesRef;

    private User mUser;
    private ThanksData mUserThanksData;
    private String mCountry;
    private String mUserId;
    private boolean mHasWelcomes;
    private boolean mIsPremium;

    private long mCountInvites;
    private boolean mControlRecentGiven;
    private boolean mControlRecentReceived;

    private FrameLayout mFrameRecent;
    private FrameLayout mFrameTop;
    private ImageView mInfoRecent;
    private ImageView mInfoTop;
    private ImageView mInfoStats;
    private List<LinearLayout> mListLinearGiven;
    private List<LinearLayout> mListLinearReceived;
    private List<TextView> mListTextGiven;
    private List<TextView> mListTextReceived;
    private List<ImageView> mListImageGiven;
    private List<ImageView> mListImageReceived;
    private List<ImageView> mListWelcomesGiven;
    private List<ImageView> mListWelcomesReceived;
    private long mCountGiven;
    private long mCountReceived;
    private List<LinearLayout> mListLinearRecent;
    private List<LinearLayout> mListLinearCategories;
    private List<LinearLayout> mListRectangles;
    private List<TextView> mListLabelCategories;
    private List<TextView> mListValueCategories;
    private List<ImageView> mCategoryImages;
    private List<ThanksValue> mListThanksValues;

    private List<TextView> mListTextTopTenUsernamesReceived;
    private List<TextView> mListTextTopTenUsernamesGiven;
    private List<LinearLayout> mListLinearTopTenRankReceived;
    private List<LinearLayout> mListLinearTopTenRankGiven;
    private CardView mCardTop;
    private LinearLayout mLinearTopGiven;
    private LinearLayout mLinearTopReceived;

    private SharedPreferences mBirthdayPref;
    private SharedPreferences.Editor mEditor;
    private BirthdayTimes mBirthdayTimes;
    private SharedPreferences mSharedCountryPrefs;
    private SharedPreferences mSharedPrefPremium;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        view.clearFocus();

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mControlRecentGiven = false;
        mControlRecentReceived = false;

        if (savedInstanceState != null) {
            mControlRecentGiven = savedInstanceState.getBoolean("control-given");
            mControlRecentReceived = savedInstanceState.getBoolean("control-received");
        }

        if (getArguments() != null) {
            mCountry = getArguments().getString(OUR_USER_COUNTRY);
        }

        setupFirebaseInProfile();
        initializeViews(view);
        initializeRecentThanks(view);

        if (mAuth.getCurrentUser() != null) {
            updateUi();
        }

        if (getActivity() != null) {
            Utils.hideKeyboardFrom(getActivity());
            //Ring ring = new Ring(getActivity());
        }

        if (getActivity() != null && mAuth.getCurrentUser() != null) {
            mSharedCountryPrefs = getActivity().getSharedPreferences(COUNTRY_PREFS + mAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
            mCountry = mSharedCountryPrefs.getString("country", "");
        }

        //updateProfileUi(mAuth.getCurrentUser().getUid());
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                requestStoragePermissions();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
                Log.v(TAG, "Photos: Started activitiyForResult");
            }
        });

        if (getActivity() != null) {
            mCuteMessage.setText(TextUtils.generateRandomCuteness(getActivity()));
        }

        return view;
    }

    public void updateUi() {
        openOurInfo();
    }

    public void openOurInfo() {
        mFireUser
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading our User\'s info in MyProfile");
                            mUser = documentSnapshot.toObject(User.class);
                            String name = "";
                            long birthday = mUser.getBirthday();
                            mHasWelcomes = mUser.getHasWelcomes();

                            if (mUser.getName() != null) {
                                name = DataUtils.capitalize(mUser.getName());
                            }

                            if (getActivity() != null) {
                                mBirthdayPref = getActivity().getSharedPreferences(PREFS_BIRTHDAY + mUserId, Context.MODE_PRIVATE);
                                mEditor = mBirthdayPref.edit();
                            }

                            Log.v("MainSnapshot", "URL of profile image: " + mUser.getImageUrl());
                            Log.v("MainSnapshot", "Email: " + mUser.getEmail());
                            Log.v("MainSnapshot", "Birthday: " + mUser.getBirthday());
                            Log.v("MainSnapshot", "Country: " + mUser.getLivingCountry());
                            if (mUser.getImageUrl() != null) {

                                Log.v("MainSnapshot", "Will show saved picture");
                                ImageUtils.loadImageInto(getContext(), mUser.getImageUrl(), mProfileImage);

                            }

                            //Open recent thanks Level

                            checkRecentThanks();

                            mTextNumberFriends.setText(String.format("%,d", mUser.getFriends().size()));

                            //mLinearStatus.setVisibility(View.VISIBLE);

                            List<ThanksItem> recentThanksGiven = new ArrayList<>();

                            mFirestore.collection(THANKS_DB).whereEqualTo("fromUserId", mUser.getUserId())
                                    .orderBy("date", Query.Direction.DESCENDING)
                                    .limit(5)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            long numberThanks = queryDocumentSnapshots.size();
                                            mCountGiven = 0;
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                Thanks thanks = documentSnapshot.toObject(Thanks.class);
                                                String type = thanks.getThanksType();
                                                boolean isWelcomed = thanks.getWasWelcomed();
                                                Log.v(TAG, "Getting recent Thanks given in Profile. Thanks nr " + (mCountGiven + 1) + ". Thanks ID: " + documentSnapshot.getId());
                                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading Recent Thanks that we Gave, on MyProfile");

                                                mFirestore.collection(USER_SNIPPET).document(thanks.getToUserId())
                                                        .get()
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                if (documentSnapshot.exists()) {
                                                                    String toName = DataUtils.capitalize(documentSnapshot.getString("name"));
                                                                    recentThanksGiven.add(new ThanksItem(toName, type, thanks.getDate(), isWelcomed));
                                                                    mCountGiven++;
                                                                    Log.v(TAG, "Getting recent Thanks given in Profile. Added new ThanksItem: " + mCountGiven);
                                                                    Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading UserSnippet\'s info from User we gave Thanks to, MyProfile");

                                                                    if (mCountGiven == 5 || mCountGiven == queryDocumentSnapshots.size()) {
                                                                        DataUtils.sortRecentThanks(recentThanksGiven);
                                                                        for (int i = 0; i != mCountGiven; i++) {
                                                                            mListTextGiven.get(i).setText(recentThanksGiven.get(i).getName());
                                                                            mListImageGiven.get(i).setImageDrawable(ImageUtils.getThanksDraw(getActivity(), recentThanksGiven.get(i).getThanksType()));

                                                                            if (recentThanksGiven.get(i).getWasWelcomed()) {
                                                                                ImageView newWelcome = mListWelcomesGiven.get(i);
                                                                                int color = Color.parseColor("#B6B2B2"); //The color u want
                                                                                newWelcome.setColorFilter(color);
                                                                                mListWelcomesGiven.get(i).setVisibility(View.VISIBLE);
                                                                            } else {
                                                                                mListWelcomesGiven.get(i).setVisibility(View.INVISIBLE);
                                                                            }

                                                                            mListLinearGiven.get(i).setVisibility(View.VISIBLE);
                                                                            mListTextGiven.get(i).setVisibility(View.VISIBLE);
                                                                            mListLinearGiven.get(i).setVisibility(View.VISIBLE);

                                                                            mListTextGiven.get(i).setTypeface(null, Typeface.BOLD);
                                                                            mListImageGiven.get(i).setVisibility(View.VISIBLE);

                                                                            mListLinearRecent.get(i).setVisibility(View.VISIBLE);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        });

                                            }

                                            if (numberThanks > 0) {
                                                for (LinearLayout linearGiven : mListLinearGiven) {
                                                    linearGiven.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            Fragment thanksFragment = new ThanksFragment();
                                                            Bundle infoBundle = new Bundle();
                                                            infoBundle.putSerializable(USER_OBJECT, mUser);
                                                            infoBundle.putBoolean(IS_PREMIUM, mIsPremium);
                                                            infoBundle.putString(THANKS_DYNAMIC, DYNAMIC_GIVER);
                                                            infoBundle.putString(OUR_USER_COUNTRY, mCountry);
                                                            thanksFragment.setArguments(infoBundle);

                                                            if (getActivity() != null) {
                                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, thanksFragment).addToBackStack(null).commit();
                                                            }
                                                        }
                                                    });
                                                }
                                            }

                                        }
                                    });

                            List<ThanksItem> recentThanksReceived = new ArrayList<>();

                            mFirestore.collection(THANKS_DB).whereEqualTo("toUserId", mUser.getUserId())
                                    .orderBy("date", Query.Direction.DESCENDING)
                                    .limit(5)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            long numberThanks = queryDocumentSnapshots.size();
                                            mCountReceived = 0;
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                Thanks thanks = documentSnapshot.toObject(Thanks.class);
                                                String type = thanks.getThanksType();
                                                boolean isWelcomed = thanks.getWasWelcomed();

                                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading Recent Thanks we received From, on MyProfile");

                                                mFirestore.collection(USER_SNIPPET).document(thanks.getFromUserId())
                                                        .get()
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                if (documentSnapshot.exists()) {
                                                                    Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading UserSnippet\'s info from User we've receivedThanks from, MyProfile");
                                                                    String toName = DataUtils.capitalize(documentSnapshot.getString("name"));

                                                                    recentThanksReceived.add(new ThanksItem(toName, type, thanks.getDate(), isWelcomed));
                                                                    mCountReceived++;

                                                                    if (mCountReceived == 5 || mCountReceived == queryDocumentSnapshots.size()) {
                                                                        DataUtils.sortRecentThanks(recentThanksReceived);
                                                                        for (int i = 0; i != mCountReceived; i++) {
                                                                            mListTextReceived.get(i).setText(recentThanksReceived.get(i).getName());
                                                                            mListImageReceived.get(i).setImageDrawable(ImageUtils.getThanksDraw(getActivity(), recentThanksReceived.get(i).getThanksType()));

                                                                            if (recentThanksReceived.get(i).getWasWelcomed()) {
                                                                                ImageView newWelcome = mListWelcomesReceived.get(i);
                                                                                int color = Color.parseColor("#B6B2B2"); //The color u want
                                                                                newWelcome.setColorFilter(color);
                                                                                mListWelcomesReceived.get(i).setVisibility(View.VISIBLE);
                                                                            } else {
                                                                                mListWelcomesReceived.get(i).setVisibility(View.INVISIBLE);
                                                                            }

                                                                            mListLinearRecent.get(i).setVisibility(View.VISIBLE);
                                                                            mListTextReceived.get(i).setVisibility(View.VISIBLE);
                                                                            mListLinearReceived.get(i).setVisibility(View.VISIBLE);

                                                                            mListTextReceived.get(i).setTypeface(null, Typeface.BOLD);
                                                                            mListImageReceived.get(i).setVisibility(View.VISIBLE);

                                                                            mListLinearRecent.get(i).setVisibility(View.VISIBLE);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        });

                                            }

                                            if (numberThanks > 0) {
                                                for (LinearLayout linearGiven : mListLinearReceived) {
                                                    linearGiven.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            Fragment thanksFragment = new ThanksFragment();
                                                            Bundle infoBundle = new Bundle();
                                                            infoBundle.putSerializable(USER_OBJECT, mUser);
                                                            infoBundle.putBoolean(IS_PREMIUM, mIsPremium);
                                                            infoBundle.putString(THANKS_DYNAMIC, DYNAMIC_RECEIVER);
                                                            infoBundle.putString(OUR_USER_COUNTRY, mCountry);
                                                            thanksFragment.setArguments(infoBundle);

                                                            if (getActivity() != null) {
                                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, thanksFragment).addToBackStack(null).commit();
                                                            }
                                                        }
                                                    });
                                                }
                                            }

                                        }
                                    });


                            mNameView.setText(name);

                            getThanksStats();

                            List<UserValue> topUsersGiven = mUser.getTopUsersGiven();

                            if (topUsersGiven.size() > 0) {

                                Collections.sort(topUsersGiven, new Comparator<UserValue>() {
                                    @Override
                                    public int compare(UserValue o1, UserValue o2) {
                                        return Long.compare(o2.getValueThanks(), o1.getValueThanks());
                                    }
                                });

                                for (int i = 0; i != 10 && i != topUsersGiven.size(); i++) {
                                    final int pos = i;
                                    mFirestore.collection(USER_SNIPPET).document(topUsersGiven.get(pos).getUserId())
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        mCardTop.setVisibility(View.VISIBLE);
                                                        String userName = DataUtils.capitalize(documentSnapshot.getString("name"));
                                                        mListTextTopTenUsernamesGiven.get(pos).setText(userName);
                                                        mListLinearTopTenRankGiven.get(pos).setVisibility(View.VISIBLE);
                                                    }

                                                    Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading UserSnippet\'s info from User on our Top Given");
                                                }
                                            });

                                }

                            }

                            List<UserValue> topUsersReceived = mUser.getTopUsersReceived();

                            if (topUsersReceived.size() > 0) {

                                Collections.sort(topUsersReceived, new Comparator<UserValue>() {
                                    @Override
                                    public int compare(UserValue o1, UserValue o2) {
                                        return Long.compare(o2.getValueThanks(), o1.getValueThanks());
                                    }
                                });

                                for (int i = 0; i != 10 && i != topUsersReceived.size(); i++) {
                                    final int pos = i;
                                    mFirestore.collection(USER_SNIPPET).document(topUsersReceived.get(pos).getUserId())
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        mCardTop.setVisibility(View.VISIBLE);
                                                        String userName = DataUtils.capitalize(documentSnapshot.getString("name"));
                                                        mListTextTopTenUsernamesReceived.get(pos).setText(userName);
                                                        mListLinearTopTenRankReceived.get(pos).setVisibility(View.VISIBLE);
                                                    }

                                                    Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading UserSnippet\'s info from User on our Top Received");
                                                }
                                            });

                                }

                            }

                            getLists(birthday);

                            if (getActivity() != null && mUser != null) {
                                //String currencyText = "<b>" + getActivity().getString(R.string.thanks_currency) + ": " + thanksCurrency + "</b>";
                                String primCategory = "";
                                String secCategory = "";

                                if (mUser.getPrimaryCategory() != null) {
                                    primCategory = DataUtils.translateAndFormat(getActivity(), mUser.getPrimaryCategory());
                                }

                                if (mUser.getSecondaryCategory() != null) {
                                    if (!mUser.getSecondaryCategory().equals("")) {
                                        secCategory += " | " + DataUtils.translateAndFormat(getActivity(), mUser.getSecondaryCategory());
                                    }
                                }

                                mTextCategories.setText(primCategory + secCategory);

                                checkAndCreateBirthdayNotification(getActivity(), birthday);
                            }

                            mFirestore.collection(THANKS_DATA).document(mUserId)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading from our ThanksData info");
                                                mUserThanksData = documentSnapshot.toObject(ThanksData.class);
                                                final long thanksCurrency = mUserThanksData.getThanksCurrency();
                                                long thanksGivenValue = mUserThanksData.getGivenThanksValue();
                                                long thanksReceivedValue = mUserThanksData.getReceivedThanksValue();
                                                long thanksReceivedCount = mUserThanksData.getReceivedCount();
                                                long superThanksGiven = mUserThanksData.getSuperThanksGiven();
                                                long megaThanksGiven = mUserThanksData.getMegaThanksGiven();
                                                long powerThanksGiven = mUserThanksData.getPowerThanksGiven();
                                                long ultraThanksGiven = mUserThanksData.getUltraThanksGiven();
                                                long superThanksReceived = mUserThanksData.getSuperThanksReceived();
                                                long megaThanksReceived = mUserThanksData.getMegaThanksReceived();
                                                long powerThanksReceived = mUserThanksData.getPowerThanksReceived();
                                                long ultraThanksReceived = mUserThanksData.getUltraThanksReceived();
                                                long diaryThanks = mUserThanksData.getDiaryThanks();
                                                long lastRegisteredReceivedThanks = mUserThanksData.getLastRegisteredReceivedThanks();

                                                Log.v(TAG, "Creating Welcomes. Has Welcomes in MyProfileFragment: " + mHasWelcomes);

                                                mSuperGiven.setText(String.format("%,d", superThanksGiven));
                                                mSuperReceived.setText(String.format("%,d", superThanksReceived));
                                                mMegaGiven.setText(String.format("%,d", megaThanksGiven));
                                                mMegaReceived.setText(String.format("%,d", megaThanksReceived));
                                                mPowerGiven.setText(String.format("%,d", powerThanksGiven));
                                                mPowerReceived.setText(String.format("%,d", powerThanksReceived));
                                                mUltraGiven.setText(String.format("%,d", ultraThanksGiven));
                                                mUltraReceived.setText(String.format("%,d", ultraThanksReceived));
                                                mTextDiaryThanks.setText(String.format("%,d", diaryThanks));

                                                if (ultraThanksGiven > 0 || ultraThanksReceived > 0) {
                                                    mLinearUltra.setVisibility(View.VISIBLE);
                                                } else {
                                                    mLinearUltra.setVisibility(View.GONE);
                                                }

                                                if (thanksGivenValue > 1000) {
                                                    mFrameRecent.setVisibility(View.GONE);
                                                    mFrameTop.setVisibility(View.GONE);
                                                }

                                                if (getActivity() != null) {
                                                    mGivenThanks.setText(String.format("%,d", thanksGivenValue));
                                                    mReceivedThanks.setText(String.format("%,d", thanksReceivedValue));
                                                    mTextThanksCount.setText(String.format("%,d", mUserThanksData.getThanksCount()));
                                                }

                                                mTextThanksCurrency.setText(String.format("%,d", mUserThanksData.getThanksCurrency()));

                                                if (thanksCurrency > 1000) {
                                                    mImageInfoThankers.setVisibility(View.GONE);
                                                }

                                                if (mUserThanksData.getThanksCount() > 200) {
                                                    mImageInfoThanksCount.setVisibility(View.GONE);
                                                    mInfoStats.setVisibility(View.GONE);
                                                } else {
                                                    mInfoStats.setVisibility(View.VISIBLE);
                                                }

                                                if ((mUserThanksData.getReceivedCount() - (int) lastRegisteredReceivedThanks) > 0) {

                                                    int newThanksReceived = (int) mUserThanksData.getReceivedCount() - (int) lastRegisteredReceivedThanks;
                                                    Log.v(TAG, "Getting received count. Received count: " + mUserThanksData.getReceivedCount() + ". Registered count: " + lastRegisteredReceivedThanks);

                                                    if (newThanksReceived > 0) {

                                                        if (getActivity() != null) {
                                                            GradientDrawable border = (GradientDrawable) mLinearRecentMessage.getBackground();

                                                            //Drawable border = getActivity().getResources().getDrawable(R.drawable.custom_border);
                                                            int color = getActivity().getResources().getColor(R.color.colorAccent);
                                                            border.setStroke(3, color);
                                                        }

                                                        mTextNewThanksReceivedNumber.setText(String.valueOf(newThanksReceived));
                                                        mTextNewThanksReceivedNumber.setVisibility(View.VISIBLE);
                                                        mTextNewThanksReceivedLabel.setVisibility(View.VISIBLE);
                                                        //lastRegisteredThanksRef.setValue(mUser.getReceivedCount());
                                                        mLinearRecentMessage.setVisibility(View.VISIBLE);

                                                        mLinearRecentMessage.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                        /*Intent receivedIntent = new Intent(getActivity(), ThanksActivity.class);
                                        receivedIntent.putExtra(USER_OBJECT, mUser);
                                        receivedIntent.putExtra(IS_PREMIUM, mIsPremium);
                                        receivedIntent.putExtra(THANKS_DYNAMIC, DYNAMIC_RECEIVER);
                                        startActivity(receivedIntent);*/

                                                                Fragment thanksFragment = new ThanksFragment();
                                                                Bundle infoBundle = new Bundle();
                                                                infoBundle.putSerializable(USER_OBJECT, mUser);
                                                                infoBundle.putBoolean(IS_PREMIUM, mIsPremium);
                                                                infoBundle.putString(THANKS_DYNAMIC, DYNAMIC_RECEIVER);
                                                                infoBundle.putString(OUR_USER_COUNTRY, mCountry);
                                                                thanksFragment.setArguments(infoBundle);

                                                                if (getActivity() != null) {
                                                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, thanksFragment).addToBackStack(null).commit();
                                                                }
                                                            }
                                                        });

                                                        mUserThanksData.setLastRegisteredReceivedThanks(mUserThanksData.getReceivedCount());
                                                        mFirestore.collection(THANKS_DATA).document(mAuth.getCurrentUser().getUid()).update("lastRegisteredReceivedThanks", mUserThanksData.getReceivedCount());

                                                    } else {
                                                        mTextNewThanksReceivedNumber.setVisibility(View.GONE);
                                                        mTextNewThanksReceivedLabel.setVisibility(View.GONE);
                                                        mLinearRecentMessage.setVisibility(View.GONE);
                                                    }
                                                }

                                                //Check email invites
                                                mProgressBar.setVisibility(View.GONE);
                                                processInvites();

                                                //((MainActivity) getActivity()).updateMessages(mAuth.getCurrentUser().getUid());
                                            }
                                        }
                                    });
                        }
                    }

                });

        //Checking if we're Premium
        /*mFirestore.collection(PREMIUM_REFERENCE).document(mUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            mIsPremium = true;
                        }
                    }
                }); */

        mSharedPrefPremium = getActivity().getSharedPreferences(PREMIUM_REFERENCE + mAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
        mIsPremium = mSharedPrefPremium.getBoolean("isPremium", false);
    }

    public void processInvites() {
        String email = mUser.getEmail().replace('.', ',').toLowerCase();
        Query invitesQuery = mFirestore.collection(INVITE_REF).whereEqualTo("toEmail", email);

        invitesQuery.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0) {
                            mCountInvites = 0;
                            for (QueryDocumentSnapshot inviteSnapshot : queryDocumentSnapshots) {
                                ThanksInvite invite = inviteSnapshot.toObject(ThanksInvite.class);
                                Thanks thanks = new Thanks(mUser, invite);
                                inviteSnapshot.getReference().delete();
                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading Thanks from invite");
                                mFirestore.collection(THANKS_DB).add(thanks);
                                mFirestore.collection(DB_REFERENCE).document(thanks.getFromUserId())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading User Info from user who invited me");
                                                    User fromUser = documentSnapshot.toObject(User.class);
                                                    long value = DataUtils.thanksTypeToLong(thanks);
                                                    final long primaryValue = fromUser.getRightCategoryValue(thanks.getPrimaryCategory()) + (value * 2);
                                                    long secondaryValueWrite = 0;
                                                    fromUser.addValueOnCategory(primaryValue, thanks.getPrimaryCategory());
                                                    if (thanks.getSecondaryCategory() != null) {
                                                        if (!thanks.getSecondaryCategory().equals("")) {
                                                            secondaryValueWrite = fromUser.getRightCategoryValue(thanks.getSecondaryCategory()) + value;
                                                            fromUser.addValueOnCategory(secondaryValueWrite, thanks.getSecondaryCategory());
                                                        }
                                                    }

                                                    List<UserValue> topGivenThankers = fromUser.getTopUsersGiven();
                                                    topGivenThankers.add(new UserValue(mUser.getUserId(), DataUtils.thanksTypeToLong(thanks)));
                                                    fromUser.setTopUsersGiven(topGivenThankers);

                                                    mFirestore.collection(DB_REFERENCE).document(fromUser.getUserId()).set(fromUser);

                                                    mFirestore.collection(THANKS_DATA).document(fromUser.getUserId())
                                                            .get()
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    if (documentSnapshot.exists()) {
                                                                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading ThanksData from User who invited me");
                                                                        ThanksData data = documentSnapshot.toObject(ThanksData.class);
                                                                        long updatedThanksCurrency = data.getThanksCurrency() + 1;
                                                                        long updatedThanksCount = data.getThanksCount() + 1;
                                                                        long updatedGivenValue = data.getGivenThanksValue() + DataUtils.thanksTypeToLong(thanks);
                                                                        long superGiven = data.getSuperThanksGiven();
                                                                        long megaGiven = data.getMegaThanksGiven();
                                                                        long powerGiven = data.getPowerThanksGiven();
                                                                        long ultraGiven = data.getUltraThanksGiven();

                                                                        if ((updatedThanksCount % 10) == 0) {
                                                                            updatedThanksCurrency += 10;
                                                                        }

                                                                        if ((updatedThanksCount % 100) == 0) {
                                                                            updatedThanksCurrency += 100;
                                                                        }

                                                                        if ((updatedThanksCount % 1000) == 0) {
                                                                            updatedThanksCurrency += 1000;
                                                                        }

                                                                        if ((updatedThanksCount % 10000) == 0) {
                                                                            updatedThanksCurrency += 10000;
                                                                        }

                                                                        if ((updatedThanksCount % 1000000) == 0) {
                                                                            updatedThanksCurrency += 1000000;
                                                                        }

                                                                        data.updateSpecialGiven(thanks);

                                                                        data.setThanksCurrency(updatedThanksCurrency);
                                                                        data.setThanksCount(updatedThanksCount);
                                                                        data.setGivenThanksValue(updatedGivenValue);
                                                                        data.setSuperThanksGiven(superGiven);
                                                                        data.setMegaThanksGiven(megaGiven);
                                                                        data.setPowerThanksGiven(powerGiven);
                                                                        data.setUltraThanksGiven(ultraGiven);

                                                                        mFirestore.collection(THANKS_DATA).document(fromUser.getUserId()).set(data);

                                                                        //Send join message to Friend
                                                                        if (getActivity() != null) {
                                                                            String title = getActivity().getString(R.string.friend_joined, DataUtils.capitalize(mUser.getName()));
                                                                            String text = getActivity().getString(R.string.your_friend_joined_thanker,
                                                                                    DataUtils.capitalize(mUser.getName()));

                                                                            DataUtils.createMessage(thanks.getFromUserId(), title, text, mUser.getUserId(), 0);
                                                                        }

                                                                    }

                                                                    mCountInvites++;

                                                                    List<UserValue> topReceivedThankers = mUser.getTopUsersReceived();
                                                                    topReceivedThankers.add(new UserValue(thanks.getFromUserId(), DataUtils.thanksTypeToLong(thanks)));
                                                                    mUser.setTopUsersReceived(topReceivedThankers);
                                                                    long receivedThanksCount = mUserThanksData.getReceivedCount() + 1;
                                                                    long receivedThanksValue = mUserThanksData.getReceivedThanksValue() + DataUtils.thanksTypeToLong(thanks);
                                                                    mUserThanksData.setReceivedCount(receivedThanksCount);
                                                                    mUserThanksData.setReceivedThanksValue(receivedThanksValue);
                                                                    mUserThanksData.updateSpecialReceived(thanks);

                                                                    if (mCountInvites == queryDocumentSnapshots.size()) {
                                                                        mFirestore.collection(THANKS_DATA).document(mUser.getUserId()).set(mUserThanksData);
                                                                        mFirestore.collection(DB_REFERENCE).document(mUser.getUserId()).set(mUser);
                                                                    }

                                                                    String year = thanks.getYear();
                                                                    String month = thanks.getMonth();
                                                                    String day = thanks.getDay();

                                                                    Query countryQuery = mFirestore.collection(COUNTRIES_REFERENCE)
                                                                            .whereEqualTo("country", mCountry)
                                                                            .whereEqualTo("year", year)
                                                                            .whereEqualTo("month", month)
                                                                            .whereEqualTo("day", day)
                                                                            .limit(1);

                                                                    countryQuery
                                                                            .get()
                                                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                    StatsThanks stats = new StatsThanks(mCountry, thanks.getPrimaryCategory(), thanks.getSecondaryCategory(), thanks.getThanksType());
                                                                                    StatsThanks existingStats;
                                                                                    StatsThanks writeStats = stats;

                                                                                    if (queryDocumentSnapshots.size() > 0) {
                                                                                        for (QueryDocumentSnapshot statsSnapshot : queryDocumentSnapshots) {
                                                                                            String docKey = statsSnapshot.getId();
                                                                                            existingStats = statsSnapshot.toObject(StatsThanks.class);
                                                                                            existingStats.addStatsThanksOf(stats);
                                                                                            writeStats = existingStats;
                                                                                            mFirestore.collection(COUNTRIES_REFERENCE).document(docKey).set(writeStats);
                                                                                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading Stats Thanks from Date of Thanks/Invite");
                                                                                        }
                                                                                    } else {
                                                                                        mFirestore.collection(COUNTRIES_REFERENCE).add(writeStats);
                                                                                    }
                                                                                }

                                                                            });
                                                                }
                                                            });
                                                }
                                            }
                                        });

                            }
                        }
                    }
                });
    }

    public void getThanksStats() {

        mListThanksValues = new ArrayList<>();
        mListThanksValues.add(new ThanksValue("personThanks", mUser.getPersonThanks()));
        mListThanksValues.add(new ThanksValue("brandThanks", mUser.getBrandThanks()));
        mListThanksValues.add(new ThanksValue("businessThanks", mUser.getBusinessThanks()));
        mListThanksValues.add(new ThanksValue("natureThanks", mUser.getNatureThanks()));
        mListThanksValues.add(new ThanksValue("healthThanks", mUser.getHealthThanks()));
        mListThanksValues.add(new ThanksValue("foodThanks", mUser.getFoodThanks()));
        mListThanksValues.add(new ThanksValue("associationThanks", mUser.getAssociationThanks()));
        mListThanksValues.add(new ThanksValue("homeThanks", mUser.getHomeThanks()));
        mListThanksValues.add(new ThanksValue("scienceThanks", mUser.getScienceThanks()));
        mListThanksValues.add(new ThanksValue("religionThanks", mUser.getReligionThanks()));
        mListThanksValues.add(new ThanksValue("sportsThanks", mUser.getSportsThanks()));
        mListThanksValues.add(new ThanksValue("lifestyleThanks", mUser.getLifestyleThanks()));
        mListThanksValues.add(new ThanksValue("techThanks", mUser.getTechThanks()));
        mListThanksValues.add(new ThanksValue("fashionThanks", mUser.getFashionThanks()));
        mListThanksValues.add(new ThanksValue("educationThanks", mUser.getEducationThanks()));
        mListThanksValues.add(new ThanksValue("gamesThanks", mUser.getGamesThanks()));
        mListThanksValues.add(new ThanksValue("travelThanks", mUser.getTravelThanks()));
        mListThanksValues.add(new ThanksValue("govThanks", mUser.getGovThanks()));
        mListThanksValues.add(new ThanksValue("beautyThanks", mUser.getBeautyThanks()));
        mListThanksValues.add(new ThanksValue("financeThanks", mUser.getFinanceThanks()));
        mListThanksValues.add(new ThanksValue("cultureThanks", mUser.getCultureThanks()));

        long totalValues = countThanksValues(mListThanksValues);

        if (totalValues > 0) {
                    /*mTextHasMostlyThanked.setVisibility(View.VISIBLE);
                    if(getActivity() != null){
                        mTextHasMostlyThanked.setText(DataUtils.capitalize(mOtherUser.getName()) + " " + getActivity().getString(R.string.main_thanks_categories));
                    }*/
            //mMyThanksLabel.setVisibility(View.VISIBLE);
            mLinearCategoriesThanked.setVisibility(View.VISIBLE);
            sortThanksValues(mListThanksValues);
            boolean valid = true;
            final int MAX = 5;

            for (int i = 0; i != MAX && valid; i++) {
                ThanksValue tValue = mListThanksValues.get(i);
                if (tValue.getValue() > 0) {
                    float percentage = Math.round(100 * tValue.getValue() / totalValues);
                    int percentageInt = Math.round(percentage);
                    String category = DataUtils.thanksCategoryToStringCategory(tValue.getKey());
                    String categoryToWrite = category;
                    if (getActivity() != null) {
                        categoryToWrite = DataUtils.translateAndFormat(getActivity(), category);
                    }
                    mListLabelCategories.get(i).setText(categoryToWrite);
                    mListValueCategories.get(i).setText(percentageInt + "%");
                    mListLinearCategories.get(i).setVisibility(View.VISIBLE);

                    if (getActivity() != null) {
                                /*mCategoryImages.get(i).setBackground(ImageUtils.getIconImage(getActivity(), category));
                                Drawable iconDraw = mCategoryImages.get(i).getDrawable();
                                iconDraw.setColorFilter(getResources().getColor(R.color.defaultTextColor2), PorterDuff.Mode.MULTIPLY);*/
                        //mCategoryImages.get(i).setColorFilter( getActivity().getResources().getColor(R.color.defaultTextColor2), PorterDuff.Mode.MULTIPLY );
                        Drawable iconDraw = ImageUtils.getIconImage(getActivity(), category);
                        iconDraw.setColorFilter(getResources().getColor(R.color.defaultTextColor2), PorterDuff.Mode.MULTIPLY);
                        mCategoryImages.get(i).setBackground(iconDraw);

                    }

                    int viewWidth = 1000;
                    int viewHeight = 30;

                    Paint paint = new Paint();

                    if (getActivity() != null) {
                        paint.setColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
                    }

                    float rectangleLength = tValue.getValue() * viewWidth / totalValues;

                    int bitmapWidth = Math.round(rectangleLength);
                    int bitmapHeight = (int) Math.round(viewHeight * 0.8);
                    Bitmap bg = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);

                    Canvas canvas = new Canvas(bg);
                    float top = (float) (viewHeight - (viewHeight * 0.8));

                    if (tValue.getValue() > 0) {
                        canvas.drawRect(0, top, rectangleLength, bitmapHeight, paint);
                    }

                    mListRectangles.get(i).setBackground(new BitmapDrawable(bg));
                } else {
                    valid = false;
                }
            }
        } else {
                    /*if(getActivity() != null){
                        mMyThanksLabel.setText(getActivity().getString(R.string.stats_soon));
                    }*/
            if (getActivity() != null) {
                mCardTop.setBackground(getActivity().getResources().getDrawable(R.drawable.circlenone));
            }
        }
    }

    public void checkRecentThanks() {
        String status = "";
        String thankerLevel = "starter";
        MultiColorCircle.CustomStrokeObject s1 = null;
        MultiColorCircle.CustomStrokeObject s2 = null;
        long recentThanks = getActualRecentThanks();

        if (!mUser.getIsConceptual()) {

            if (getActivity() != null) {
                status = getActivity().getString(R.string.starter_thanker);

                mStatusCircle.setWidthOfCircleStroke(65);
                mStatusCircle.setWidthOfBoarderStroke(5);
                mStatusCircle.setColorOfBoarderStroke(ContextCompat.getColor(getActivity(), R.color.white));

                s1 = new MultiColorCircle.CustomStrokeObject(
                        80, 20, ContextCompat.getColor(getActivity(), R.color.lightGreyLevel)
                );
                s2 = new MultiColorCircle.CustomStrokeObject(
                        20, 0, ContextCompat.getColor(getActivity(), R.color.colorPrimary)
                );
            }

            if (recentThanks > 0) {
                long number = recentThanks;
                double numFactor = number / FIVE_DAYS;

                if (getActivity() != null) {
                    //mLinearStatsOne.setBackgroundColor(getActivity().getResources().getColor(R.color.starterGreen));
                    status = getActivity().getString(R.string.starter_thanker);
                    thankerLevel = "starter";
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
                        thankerLevel = "true";

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
            }

            Log.v(TAG, "Checking level. Level: " + status);
            mUser.setThankerLevel(thankerLevel);

            mTextStatusThanking.setText(DataUtils.capitalize(status));

            List<MultiColorCircle.CustomStrokeObject> myList = new ArrayList<>();
            myList.add(s1);
            myList.add(s2);

            mStatusCircle.setCircleStrokes(myList);
        } else {
            if (getActivity() != null) {
                mStatusCircle.setWidthOfCircleStroke(60);
                mStatusCircle.setWidthOfBoarderStroke(5);
                mStatusCircle.setColorOfBoarderStroke(ContextCompat.getColor(getActivity(), R.color.white));

                s1 = new MultiColorCircle.CustomStrokeObject(
                        100, 0, ContextCompat.getColor(getActivity(), R.color.colorAccent)
                );
                List<MultiColorCircle.CustomStrokeObject> myList = new ArrayList<>();
                myList.add(s1);

                mStatusCircle.setCircleStrokes(myList);
            }
        }
    }

    public long getActualRecentThanks() {
        long result = 0;
        long minimumTime = System.currentTimeMillis() - FIVE_DAYS_IN_MILLIS;

        if (mUser.getRecentThanks() != null) {
            for (long thanksDate : mUser.getRecentThanks()) {
                if (thanksDate > minimumTime) {
                    result++;
                }
            }

            return result;
        } else {
            return 0;
        }
    }

    public void initializeViews(View view) {
        mNameView = (TextView) view.findViewById(R.id.text_profile_name);
        mGivenThanks = (TextView) view.findViewById(R.id.text_thanks_given_user);
        mReceivedThanks = (TextView) view.findViewById(R.id.text_thanks_received_user);
        mTextDiaryThanks = view.findViewById(R.id.text_daily_thanks);
        mTextStatusThanking = (TextView) view.findViewById(R.id.text_thanker_level);
        mTextNumberFriends = (TextView) view.findViewById(R.id.text_number_friends);
        mTextThanksCurrency = (TextView) view.findViewById(R.id.text_number_thankers);
        mTextThanksCount = (TextView) view.findViewById(R.id.text_thanks_count);
        mTextCategories = (TextView) view.findViewById(R.id.text_categories);
        mTextNewThanksReceivedNumber = (TextView) view.findViewById(R.id.text_number_new_thanks);
        mTextNewThanksReceivedLabel = (TextView) view.findViewById(R.id.text_new_thanks_label);
        mCuteMessage = (TextView) view.findViewById(R.id.text_cute_message);
        mSuperGiven = (TextView) view.findViewById(R.id.text_super_thanks_given);
        mSuperReceived = (TextView) view.findViewById(R.id.text_super_thanks_received);
        mMegaGiven = (TextView) view.findViewById(R.id.text_mega_thanks_given);
        mMegaReceived = (TextView) view.findViewById(R.id.text_mega_thanks_received);
        mPowerGiven = (TextView) view.findViewById(R.id.text_power_thanks_given);
        mPowerReceived = (TextView) view.findViewById(R.id.text_power_thanks_received);
        mUltraGiven = (TextView) view.findViewById(R.id.text_ultra_thanks_given);
        mUltraReceived = (TextView) view.findViewById(R.id.text_ultra_thanks_received);
        mMyThanksLabel = (TextView) view.findViewById(R.id.text_thanks_label);
        mProfileImage = (ImageView) view.findViewById(R.id.profile_picture);
        mOuterCircle = (ImageView) view.findViewById(R.id.image_outer_circle);
        mImageInfoLevel = (ImageView) view.findViewById(R.id.image_info_level);
        mImageInfoThankers = (ImageView) view.findViewById(R.id.image_info_number_thankers);
        mImageInfoDiary = view.findViewById(R.id.image_info_daily_thanks);
        mImagePictureInfo = (ImageView) view.findViewById(R.id.image_info_picture);
        mImageInfoThanksCount = (ImageView) view.findViewById(R.id.image_info_thanks_count);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mFrameRecent = (FrameLayout) view.findViewById(R.id.frame_recent);
        mFrameTop = (FrameLayout) view.findViewById(R.id.frame_top);
        mInfoRecent = (ImageView) view.findViewById(R.id.image_info_recent);
        mInfoTop = (ImageView) view.findViewById(R.id.image_info_top);
        mInfoStats = (ImageView) view.findViewById(R.id.image_info_stats);
        mCardTop = (CardView) view.findViewById(R.id.cardview_top);
        mLinearTopGiven = (LinearLayout) view.findViewById(R.id.linear_layout_top_given);
        mLinearTopReceived = (LinearLayout) view.findViewById(R.id.linear_layout_top_thankers);
        mLinearThankers = (LinearLayout) view.findViewById(R.id.linear_thankers);
        mLinearCount = (LinearLayout) view.findViewById(R.id.linear_count);
        mLinearUltra = (LinearLayout) view.findViewById(R.id.linear_ultra_thanks);
        mStatusCircle = (MultiColorCircle) view.findViewById(R.id.status_circle);
        mLinearRecentMessage = (LinearLayout) view.findViewById(R.id.linear_recent_thanks_message);
        mLinearCategoriesThanked = (LinearLayout) view.findViewById(R.id.linear_categories_thanked);
        mLinearDiary = view.findViewById(R.id.linear_diary);
        mLinearImage = view.findViewById(R.id.linear_info_image);

        mProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        initCategoriesViews(view);
        initializeTop(view);
        implementClicks();
    }

    public void implementClicks() {

        mLinearTopGiven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment thankersFragment = new ThankersListFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(USER_OBJECT, mUser);
                bundle.putString(USER_ID_STRING, mUser.getUserId());
                bundle.putString(LISTING_TYPE, TOP_USERS_THANKS_GIVEN);
                bundle.putString(OUR_USER_COUNTRY, mCountry);
                thankersFragment.setArguments(bundle);

                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, thankersFragment).addToBackStack(null).commit();
                }
            }
        });

        mLinearTopReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment thankersFragment = new ThankersListFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(USER_OBJECT, mUser);
                bundle.putString(USER_ID_STRING, mUser.getUserId());
                bundle.putString(LISTING_TYPE, TOP_USERS_THANKS_RECEIVED);
                bundle.putString(OUR_USER_COUNTRY, mCountry);
                thankersFragment.setArguments(bundle);

                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, thankersFragment).addToBackStack(null).commit();
                }
            }
        });

        mLinearThankers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle(getActivity().getString(R.string.what_are_thankers));
                    builder.setMessage(getActivity().getString(R.string.this_is_thankers));
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

        mImageInfoLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle(getActivity().getString(R.string.what_is_level));
                    builder.setMessage(getActivity().getString(R.string.this_is_level));
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

        mImageInfoDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle(getActivity().getString(R.string.what_is_diary));
                    builder.setMessage(getActivity().getString(R.string.this_is_diary));
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

        mLinearDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle(getActivity().getString(R.string.what_is_diary));
                    builder.setMessage(getActivity().getString(R.string.this_is_diary));
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

        mLinearCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle(getActivity().getString(R.string.what_is_count));
                    builder.setMessage(getActivity().getString(R.string.this_is_count));
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

        mLinearImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle(getActivity().getString(R.string.info_picture));
                    builder.setMessage(getActivity().getString(R.string.this_is_picture));
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

        mInfoRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle(getActivity().getString(R.string.info_recent));
                    builder.setMessage(getActivity().getString(R.string.this_is_recent));
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

        mInfoTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle(getActivity().getString(R.string.info_top));
                    builder.setMessage(getActivity().getString(R.string.this_is_top));
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

        mInfoStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle(getActivity().getString(R.string.info_stats));
                    builder.setMessage(getActivity().getString(R.string.this_is_stats));
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

    public void initializeTop(View view) {
        mListTextTopTenUsernamesReceived = new ArrayList<>();
        mListTextTopTenUsernamesGiven = new ArrayList<>();
        mListLinearTopTenRankReceived = new ArrayList<>();
        mListLinearTopTenRankGiven = new ArrayList<>();

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

    public void initCategoriesViews(View v) {
        mListLinearCategories = new ArrayList<>();
        mListRectangles = new ArrayList<>();
        mListLabelCategories = new ArrayList<>();
        mListValueCategories = new ArrayList<>();
        mCategoryImages = new ArrayList<>();

        mListLinearCategories.add((LinearLayout) v.findViewById(R.id.linear_category_1));
        mListLinearCategories.add((LinearLayout) v.findViewById(R.id.linear_category_2));
        mListLinearCategories.add((LinearLayout) v.findViewById(R.id.linear_category_3));
        mListLinearCategories.add((LinearLayout) v.findViewById(R.id.linear_category_4));
        mListLinearCategories.add((LinearLayout) v.findViewById(R.id.linear_category_5));

        mListLabelCategories.add((TextView) v.findViewById(R.id.text_category_1));
        mListLabelCategories.add((TextView) v.findViewById(R.id.text_category_2));
        mListLabelCategories.add((TextView) v.findViewById(R.id.text_category_3));
        mListLabelCategories.add((TextView) v.findViewById(R.id.text_category_4));
        mListLabelCategories.add((TextView) v.findViewById(R.id.text_category_5));

        mListValueCategories.add((TextView) v.findViewById(R.id.text_category_value_1));
        mListValueCategories.add((TextView) v.findViewById(R.id.text_category_value_2));
        mListValueCategories.add((TextView) v.findViewById(R.id.text_category_value_3));
        mListValueCategories.add((TextView) v.findViewById(R.id.text_category_value_4));
        mListValueCategories.add((TextView) v.findViewById(R.id.text_category_value_5));

        mCategoryImages.add((ImageView) v.findViewById(R.id.image_category_1));
        mCategoryImages.add((ImageView) v.findViewById(R.id.image_category_2));
        mCategoryImages.add((ImageView) v.findViewById(R.id.image_category_3));
        mCategoryImages.add((ImageView) v.findViewById(R.id.image_category_4));
        mCategoryImages.add((ImageView) v.findViewById(R.id.image_category_5));

        mListRectangles.add((LinearLayout) v.findViewById(R.id.rectangle_category_1));
        mListRectangles.add((LinearLayout) v.findViewById(R.id.rectangle_category_2));
        mListRectangles.add((LinearLayout) v.findViewById(R.id.rectangle_category_3));
        mListRectangles.add((LinearLayout) v.findViewById(R.id.rectangle_category_4));
        mListRectangles.add((LinearLayout) v.findViewById(R.id.rectangle_category_5));

        mLinearCategoriesThanked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent statsIntent = new Intent(getActivity(), PersonalStatsActivity.class);
                statsIntent.putExtra(USER_OBJECT, mUser);
                statsIntent.putExtra(LIST_THANKS_VALUES, (Serializable) mListThanksValues);
                startActivity(statsIntent);
            }
        });
    }

    public void initializeRecentThanks(View v) {
        mListLinearRecent = new ArrayList<>();
        mListLinearGiven = new ArrayList<>();
        mListLinearReceived = new ArrayList<>();
        mListTextGiven = new ArrayList<>();
        mListTextReceived = new ArrayList<>();
        mListImageGiven = new ArrayList<>();
        mListImageReceived = new ArrayList<>();
        mListWelcomesGiven = new ArrayList<>();
        mListWelcomesReceived = new ArrayList<>();

        mListLinearRecent.add((LinearLayout) v.findViewById(R.id.linear_recent_1));
        mListLinearRecent.add((LinearLayout) v.findViewById(R.id.linear_recent_2));
        mListLinearRecent.add((LinearLayout) v.findViewById(R.id.linear_recent_3));
        mListLinearRecent.add((LinearLayout) v.findViewById(R.id.linear_recent_4));
        mListLinearRecent.add((LinearLayout) v.findViewById(R.id.linear_recent_5));

        mListLinearGiven.add((LinearLayout) v.findViewById(R.id.linear_layout_given_1));
        mListLinearGiven.add((LinearLayout) v.findViewById(R.id.linear_layout_given_2));
        mListLinearGiven.add((LinearLayout) v.findViewById(R.id.linear_layout_given_3));
        mListLinearGiven.add((LinearLayout) v.findViewById(R.id.linear_layout_given_4));
        mListLinearGiven.add((LinearLayout) v.findViewById(R.id.linear_layout_given_5));

        mListLinearReceived.add((LinearLayout) v.findViewById(R.id.linear_layout_received_1));
        mListLinearReceived.add((LinearLayout) v.findViewById(R.id.linear_layout_received_2));
        mListLinearReceived.add((LinearLayout) v.findViewById(R.id.linear_layout_received_3));
        mListLinearReceived.add((LinearLayout) v.findViewById(R.id.linear_layout_received_4));
        mListLinearReceived.add((LinearLayout) v.findViewById(R.id.linear_layout_received_5));

        mListTextGiven.add((TextView) v.findViewById(R.id.text_given_1));
        mListTextGiven.add((TextView) v.findViewById(R.id.text_given_2));
        mListTextGiven.add((TextView) v.findViewById(R.id.text_given_3));
        mListTextGiven.add((TextView) v.findViewById(R.id.text_given_4));
        mListTextGiven.add((TextView) v.findViewById(R.id.text_given_5));

        mListTextReceived.add((TextView) v.findViewById(R.id.text_received_1));
        mListTextReceived.add((TextView) v.findViewById(R.id.text_received_2));
        mListTextReceived.add((TextView) v.findViewById(R.id.text_received_3));
        mListTextReceived.add((TextView) v.findViewById(R.id.text_received_4));
        mListTextReceived.add((TextView) v.findViewById(R.id.text_received_5));

        mListImageGiven.add((ImageView) v.findViewById(R.id.image_thanks_type_1));
        mListImageGiven.add((ImageView) v.findViewById(R.id.image_thanks_type_2));
        mListImageGiven.add((ImageView) v.findViewById(R.id.image_thanks_type_3));
        mListImageGiven.add((ImageView) v.findViewById(R.id.image_thanks_type_4));
        mListImageGiven.add((ImageView) v.findViewById(R.id.image_thanks_type_5));

        mListImageReceived.add((ImageView) v.findViewById(R.id.image_thanks_type_received_1));
        mListImageReceived.add((ImageView) v.findViewById(R.id.image_thanks_type_received_2));
        mListImageReceived.add((ImageView) v.findViewById(R.id.image_thanks_type_received_3));
        mListImageReceived.add((ImageView) v.findViewById(R.id.image_thanks_type_received_4));
        mListImageReceived.add((ImageView) v.findViewById(R.id.image_thanks_type_received_5));

        mListWelcomesGiven.add((ImageView) v.findViewById(R.id.image_welcome_1));
        mListWelcomesGiven.add((ImageView) v.findViewById(R.id.image_welcome_2));
        mListWelcomesGiven.add((ImageView) v.findViewById(R.id.image_welcome_3));
        mListWelcomesGiven.add((ImageView) v.findViewById(R.id.image_welcome_4));
        mListWelcomesGiven.add((ImageView) v.findViewById(R.id.image_welcome_5));

        mListWelcomesReceived.add((ImageView) v.findViewById(R.id.image_welcome_received_1));
        mListWelcomesReceived.add((ImageView) v.findViewById(R.id.image_welcome_received_2));
        mListWelcomesReceived.add((ImageView) v.findViewById(R.id.image_welcome_received_3));
        mListWelcomesReceived.add((ImageView) v.findViewById(R.id.image_welcome_received_4));
        mListWelcomesReceived.add((ImageView) v.findViewById(R.id.image_welcome_received_5));
    }

    public void setupFirebaseInProfile() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mUserId = mAuth.getCurrentUser().getUid();
            mFireUser = mFirestore.collection(DB_REFERENCE).document(mUserId);
            mInvitesRef = mFirestore.collection(INVITE_EMAIL).document(mUserId);
        }
        mUsersImagesStorage = FirebaseStorage.getInstance();
        mStorageReference = mUsersImagesStorage.getReference().child(SAVE_IMAGES_USERS);
    }

    public String convertToStringCategory(String s) {
        String result = "";

        switch (s) {
            case "personThanks":
                result = "Person";
                break;
            case "brandThanks":
                result = "Brand";
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
                result = "Association";
                break;
            case "venueThanks":
                result = "Venue";
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
            case "musicThanks":
                result = "Music";
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
            case "LandThanks":
                result = "Land";
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

        return result;
    }

    public void requestStoragePermissions() {
        if (getActivity() != null) {

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_STORAGE);
            }
        }
    }

    private Bitmap rotateBitmap(Bitmap bmp) {
        Matrix matrix = new Matrix();

        matrix.postRotate(90);

        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    public void uploadImageToStorage(Intent dataIntent) throws IOException {
        Log.v(TAG, "Photos: Reached photo picker RC request");
        mProgressBar.setVisibility(View.VISIBLE);
        Uri imageUri = dataIntent.getData();
        Bitmap bitmap;
        if (getActivity() != null) {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

            File imageFile = new File(imageUri.getPath());

            Log.v(TAG, "Image Uri path: " + imageFile.getAbsolutePath());
            String path = PathUtils.getDriveFilePath(getActivity(), imageUri);

            Log.v(TAG, "Image File Path. First check: " + path);

            int angle;

            if (path != null) {
                angle = getCameraPhotoOrientation(path);
            } else {
                //Currently needed for Google Drive photos
                angle = 0;
            }

            Log.v(TAG, "Image File Path. Second check: " + path);
            //int angle = getCameraPhotoOrientation(path);
            /*
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            }*/
            Matrix matrix1 = new Matrix();
            //set image rotation value to 45 degrees in matrix.
            matrix1.postRotate(angle);
            //Create bitmap with new values.
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix1, true);
            //profilePhoto.setImageBitmap(imageBitmap); //Placing image in ImageView

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, PERC_QUALITY_IMAGE, baos);
            //bitmap = rotateBitmap(bitmap);
            byte[] data = baos.toByteArray();
            final StorageReference photoRef = mStorageReference.child(mUser.getUserId());
            UploadTask uploadTask = photoRef.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String stringUrl = uri.toString();
                            Log.v(TAG, "Photos, uploaded, path: " + stringUrl);

                            mFireUser.update("imageUrl", stringUrl);
                            mFirestore.collection(USER_SNIPPET).document(mUser.getUserId()).update("imageUrl", stringUrl);
                            ImageUtils.loadImageInto(getContext(), stringUrl, mProfileImage);
                            mProgressBar.setVisibility(View.GONE);
                            Log.v(TAG, "Photos: Added Message to database");
                        }
                    });

                }
            });

        }
        mProgressBar.setVisibility(View.GONE);
    }

    public void checkAndCreateBirthdayNotification(Context context, long birthday) {
        if (getActivity() != null) {
            Log.v(TAG, "Birthday: " + DataUtils.getDateString(getActivity(), birthday));
        }

        Calendar now = Calendar.getInstance();
        Calendar birthdate = Calendar.getInstance();
        Calendar notificationTime = Calendar.getInstance();

        birthdate.setTime(new Date(birthday));

        notificationTime.set(Calendar.MONTH, birthdate.get(Calendar.MONTH));
        notificationTime.set(Calendar.DAY_OF_MONTH, birthdate.get(Calendar.DAY_OF_MONTH));
        notificationTime.set(Calendar.HOUR_OF_DAY, 9);

        if (now.get(Calendar.MONTH) > birthdate.get(Calendar.MONTH)
                || (now.get(Calendar.MONTH) == birthdate.get(Calendar.MONTH) && now.get(Calendar.DAY_OF_MONTH) > birthdate.get(Calendar.DAY_OF_MONTH))) {
            notificationTime.add(Calendar.YEAR, 1);
        } else if (now.get(Calendar.MONTH) == birthdate.get(Calendar.MONTH)
                && now.get(Calendar.DAY_OF_MONTH) == birthdate.get(Calendar.DAY_OF_MONTH)) {
            notificationTime.setTime(new Date(System.currentTimeMillis()));
            //notificationTime.add(Calendar.MINUTE, 1);
            if (mBirthdayTimes.getTimes() < 1) {
                createBirthdayNotification(notificationTime);
            }

            if (getActivity() != null) {
                Log.v(TAG, "Birthday notification. Creating notification for " + DataUtils.getDateString(getActivity(), System.currentTimeMillis()));
            }
        }

        //createBirthdayNotification(notificationTime);

        if (getActivity() != null) {
            if (now.get(Calendar.MONTH) == birthdate.get(Calendar.MONTH) && now.get(Calendar.DAY_OF_MONTH) == birthdate.get(Calendar.DAY_OF_MONTH)) {
                if (mBirthdayTimes.getTimes() <= 3) {
                    String[] birthdayMessages = getActivity().getResources().getStringArray(R.array.birthday_messages);
                    int randomNumber = new Random().nextInt(birthdayMessages.length);
                    String content = birthdayMessages[randomNumber];
                    Toast.makeText(getActivity(), content, Toast.LENGTH_LONG).show();
                    mBirthdayTimes.setTimes(mBirthdayTimes.getTimes() + 1);
                    Log.v(TAG, "Birthday Times: " + mBirthdayTimes.getTimes());
                }
            }
        }
    }

    public void createBirthdayNotification(Calendar cal) {
        if (getActivity() != null) {

            //Calendar calendar = Calendar.getInstance();

            Intent intent = new Intent(getActivity(), MainActivity.class);

            String title = getActivity().getResources().getString(R.string.happy_birthday);
            String body = getActivity().getResources().getString(R.string.happy_birthday_body);


            /*NotifyMe.Builder notifyMe = new NotifyMe.Builder(getContext());
            notifyMe.title(title);
            notifyMe.content(body);
            notifyMe.key("Birthday");
            //notifyMe.color(R.color.colorPrimary);//Color of notification header
            //notifyMe.led_color(Int red,Int green,Int blue,Int alpha);//Color of LED when notification pops up
            notifyMe.time(calendar);//The time to popup notification
            //notifyMe.delay(Int delay);//Delay in ms
            notifyMe.large_icon(R.drawable.logot);//Icon resource by ID
            notifyMe.small_icon(R.drawable.logot);
            //notifyMe.rrule("FREQ=MINUTELY;INTERVAL=5;COUNT=1");//RRULE for frequency of notification
            notifyMe.addAction(intent, getActivity().getString(R.string.and_i_feel_like_thanking)); //The action will call the intent when pressed

            notifyMe.build();*/

            PugNotification.with(getActivity())
                    .load()
                    .title(title)
                    .message(body)
                    //.bigTextStyle(bigtext)
                    .smallIcon(R.drawable.logot)
                    .largeIcon(R.drawable.logot)
                    .flags(Notification.DEFAULT_ALL)
                    .when(cal.getTimeInMillis())
                    .simple()
                    .build();

            Log.v(TAG, "Calendar time: " + DataUtils.getDateString(getActivity(), cal.getTimeInMillis()));
            Log.v(TAG, "Birthday notification: built");
        }
    }

    private long countThanksValues(List<ThanksValue> list) {
        long result = 0;

        for (ThanksValue item : list) {
            result += item.getValue();
        }

        return result;
    }

    private void sortThanksValues(List<ThanksValue> list) {
        Collections.sort(list, new Comparator<ThanksValue>() {
            @Override
            public int compare(ThanksValue o1, ThanksValue o2) {
                return Long.compare(o2.getValue(), o1.getValue());
            }
        });
    }

    public static int getCameraPhotoOrientation(String imageFilePath) {
        int rotate = 0;
        try {

            ExifInterface exif;

            exif = new ExifInterface(imageFilePath);
            String exifOrientation = exif
                    .getAttribute(ExifInterface.TAG_ORIENTATION);
            Log.v("exifOrientation", exifOrientation);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rotate;
    }

    public <T> void setBirthdayCongratsCount(String key, BirthdayTimes bTimes) {
        Gson gson = new Gson();
        String json = gson.toJson(bTimes);

        set(key, json, mEditor);
    }

    public void set(String key, String value, SharedPreferences.Editor editor) {
        editor.putString(key, value);
        editor.commit();
    }

    public void getLists(long birthday) {

        if (mAuth.getCurrentUser() != null) {
            String serializedObjectUsers = mBirthdayPref.getString(PREFS_BIRTHDAY + mAuth.getCurrentUser().getUid(), null);

            if (serializedObjectUsers != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<BirthdayTimes>() {
                }.getType();
                mBirthdayTimes = gson.fromJson(serializedObjectUsers, type);
                Log.v(TAG, "New Search. Retrieved saved users list");

                Calendar now = Calendar.getInstance();
                Calendar birthDate = Calendar.getInstance();
                birthDate.setTime(new Date(mBirthdayTimes.getBirthday()));

                if (birthDate.get(Calendar.YEAR) != now.get(Calendar.YEAR)
                        && birthDate.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                        && birthDate.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {
                    mBirthdayTimes = new BirthdayTimes(System.currentTimeMillis(), 0);
                }
            } else {
                Calendar birthCalendar = Calendar.getInstance();
                Calendar nextBirthday = Calendar.getInstance();
                Calendar now = Calendar.getInstance();

                birthCalendar.setTime(new Date(birthday));
                nextBirthday.set(Calendar.MONTH, birthCalendar.get(Calendar.MONTH));
                nextBirthday.set(Calendar.DAY_OF_MONTH, birthCalendar.get(Calendar.DAY_OF_MONTH));

                if (now.get(Calendar.MONTH) > birthCalendar.get(Calendar.MONTH)
                        || (now.get(Calendar.MONTH) == birthCalendar.get(Calendar.MONTH) && now.get(Calendar.DAY_OF_MONTH) > birthCalendar.get(Calendar.DAY_OF_MONTH))) {
                    nextBirthday.add(Calendar.YEAR, 1);
                }

                mBirthdayTimes = new BirthdayTimes(nextBirthday.getTimeInMillis(), 0);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mProgressBar.setVisibility(View.VISIBLE);

        Log.v(TAG, "for Photos purpose, requestCode = " + requestCode + ", resultCode = " + resultCode);

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            mProgressBar.setVisibility(View.VISIBLE);

            try {
                uploadImageToStorage(data);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        state.putBoolean("control-given", mControlRecentGiven);
        state.putBoolean("control-received", mControlRecentReceived);

    }

    @Override
    public void onResume() {
        super.onResume();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        mActionBar = activity.getSupportActionBar();
        if (getActivity() != null) {
            Utils.changeBarTitle(getActivity(), mActionBar, "Thanker");
        }
        mActionBar.setSubtitle(null);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mUser != null && mEditor != null) {
            setBirthdayCongratsCount(PREFS_BIRTHDAY + mUserId, mBirthdayTimes);
        }

    }


}


