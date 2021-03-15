package co.thanker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.takusemba.spotlight.OnSpotlightEndedListener;
import com.takusemba.spotlight.OnSpotlightStartedListener;
import com.takusemba.spotlight.OnTargetStateChangedListener;
import com.takusemba.spotlight.Spotlight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.thanker.data.FriendRank;
import co.thanker.data.ThanksData;
import co.thanker.data.User;
import co.thanker.data.UserSnippet;
import co.thanker.fragments.ContactsInviteFragment;
import co.thanker.fragments.FameFragment;
import co.thanker.fragments.FriendsFragment;
import co.thanker.fragments.MessagesListFragment;
import co.thanker.fragments.MyProfileFragment;
import co.thanker.fragments.OtherProfileFragment;
import co.thanker.fragments.PremiumFragmentYes;
import co.thanker.fragments.SearchFragment;
import co.thanker.fragments.StatsFragment;
import co.thanker.fragments.ThankButtonFragment;
import q.rorbin.badgeview.QBadgeView;
import co.thanker.utils.DataUtils;
import co.thanker.utils.notifications.NotificationUtils;
import co.thanker.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private static final String DB_REFERENCE = "users";
    private static final String STORAGE_REFERENCE = "content-test";
    private final String FRIEND_REQUESTS = "friend-requests";
    private final String FRIENDS_DB = "friends-db";
    private final String IMAGES_DB = "images-db";
    private static final String DELETED_ACCOUNTS = "deleted";
    private final String TAG = "MainActivityThanker";
    private final String STRING_NAME = "username-string";
    private final String USER_ID_STRING = "user-id-string";
    private final String USER_SNIPPET = "user-snippet";
    private final String OUR_USER_ID = "our-user-id";
    private final String THANKS_DATA = "thanks-data";
    private final String USER_OBJECT = "user-object";
    private final String THANKER_ID_STRING = "thanker-id-string";
    private final String MESSAGES_REFERENCE = "messages-list";
    private final String PREMIUM_REFERENCE = "premium-info";
    private final String EMAIL_EXTRA = "extra-email";
    private final String TOP_REF = "tops";
    private final String TOP_USERS_THANKS_RECEIVED = "top-users-thanks-received";
    private final String TOP_USERS_THANKS_GIVEN = "top-users-thanks-given";
    private final String USER_NAME = "user-name";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String USER_PRIMARY_CATEGORY = "primary-category";
    private final String USER_SECONDARY_CATEGORY = "secondary-category";
    private final String USER_BIRTHDAY = "user-birthday";
    private final String USER_IMAGE = "user-image";
    private final String INVITE_EXTRA = "invite-friends";
    private final String FRIEND_RANKS = "friend-ranks";
    private final String FRIENDS_NUMBER = "friends-number";
    private final String DEFAULT_IMAGE = "https://firebasestorage.googleapis.com/v0/b/thanker-b301f.appspot.com/o/users-profile-pictures%2Fthankyou3.png?alt=media&token=edf26987-1b36-4a47-aaf0-a1d484681fee";
    private final String OUR_PREFS = "our-prefs";
    private final String BOOL_HAS_NEW_MESSAGES = "has-new-messages";
    private final String INFO_HAS_PREMIUM = "info-has-premium";
    private final String PURE_SEARCH_STRING = "pure-search-string";
    private final String LAST_REGISTERED_RECEIVED_THANKS = "last-registered-received-thanks";
    private final String IS_USER_DELETED = "is-user-deleted";
    private final String PLATFORM_MESSAGE = "platform-message";
    private final String TAB_PROFILE = "tab-profile";
    private final String TAB_FRIENDS = "tab-friends";
    private final String TAB_THANK = "tab-thank";
    private final String TAB_SEARCH = "tab-search";
    private final String TAB_FAME = "tab-fame";
    private final String THANKS_CURRENCY = "thanks-currency";
    private final String TUTORIAL_PREFS = "tutorial-prefs";
    private final String TUTORIAL_READ = "tutorial-read";
    private final String LOCATION_PREFS = "location-prefs";
    private final String COUNTRY_PREFS = "country-prefs";
    private static final String SUBSCRIPTION_ID = "thankerpremium";
    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA25pInp1cwHmis++7Ig0gDQlg8VQjbVe9sCVtCCPmJcNeTdvOclXnHJU2aUkpmcYEsiw9NbNN3+hAPusEOYHd4S9U7HlAhXnf0sSgaaMex3YJmvK0NF9XKpqivaMu+CXgc0iRubpjcHble2xOaKhsEekljkXAYuIFjiJ+bprhdentVevZRG2DH6tc6QP5kmP4S+rq0iIkeIVfrlfBY/ovbfoCRbSRbbh4+cRw1OjVxd3cJP9bUtAe2hcR2pQkpiCrPBTFlVW5Kvm1czbZErmF4ffFqXDwDQjdxSYVj7MSY3dGdgoNbvAnMz7KhKAuPyCDsHIF0Rpgsf1kX+30B5cpHQIDAQAB";
    private static final String MERCHANT_ID = "BCR2DN6TVPF2VHYL";

    private static final int RC_SIGN_IN = 1;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 333;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 11;
    private final long TIME_TO_SEND_NOTIFICATION = 172800000;

    private final int FRAGMENT_SELF_PROFILE = 1;
    private final int FRAGMENT_OTHER_PROFILE = 2;
    private final int FRAGMENT_SEARCH_PROFILE = 3;
    private final int FRAGMENT_STATS_PROFILE = 4;
    private final int FRAGMENT_PREMIUM_PROFILE = 5;
    private final int FRAGMENT_THANK_PROFILE = 6;

    private LocationManager mLocationManager;

    private FrameLayout mMainFrame;
    private BottomAppBar mBar;
    private BottomNavigationView mNavigation;
    private LinearLayout mLinearConnection;
    private BottomNavigationView mBottomNavigationView;
    private TextView mMsgBadge;
    private BottomNavigationMenuView mMenuView;
    private BottomNavigationItemView mItemView;
    private SearchView mSearchView;
    private View mNotificationBadge;
    private FragmentManager mFragmentManager;
    private Fragment mFragment;
    private FloatingActionButton mFabThank;
    private ProgressBar mProgressBar;
    private CoordinatorLayout mRootView;
    private TextView mTextConnect;
    private ActionBar mActionBar;
    private MenuItem mItemMessages;
    private MenuItem mItemFame;
    private MenuItem mItemMore;
    private MenuItem mItemMyPremium;
    private BottomNavigationItemView mItemProfile;
    private BottomNavigationItemView mItemFriends;
    private BottomNavigationItemView mItemThank;
    private BottomNavigationItemView mItemSearch;
    private BottomNavigationItemView mItemPremium;
    private View mViewItemMore;

    private FirebaseFirestore mFirestore;
    private DocumentReference mUserRef;
    private DocumentReference mThanksDataRef;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mUserData;
    private DatabaseReference mRequestsRef;
    private DatabaseReference mUserCountryRef;
    private DatabaseReference mImageRef;
    private DatabaseReference mUsersThatIThankedRef;
    private DatabaseReference mUsersThatThankedMeRef;
    private DatabaseReference mFriendsRef;
    private DocumentReference mPremiumRef;
    private DatabaseReference mCurrencyRef;
    private DatabaseReference mRecentThanksReceivedRef;
    private DatabaseReference mRegisteredCountRef;
    //private DatabaseReference mUserRef;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Query mMessagesQuery;
    private Query mUserQuery;

    private Fragment mMyProfileFragment;
    private Fragment mOtherProfileFragment;
    private Fragment mPremiumFragment;
    private Fragment mSearchFragment;
    private Fragment mStatsFragment;
    private Fragment mThanksFragment;

    private MenuItem mMessagesItem;
    private MenuItem mOptionsItem;
    private Menu mMenu;
    private View mOptionsView;

    private User mUserReference;
    private boolean mExists;
    private boolean mHasNewMessages;
    private boolean mIsPremium;
    private boolean mIsSimplyPremium;
    private boolean mIsPureSearch;
    private boolean mIsReviving;
    private boolean mIsLoggedIn;
    private ColorStateList mSelectedColour;

    private QBadgeView mBadgeFriends;
    private QBadgeView mBadgeRecentThanks;
    private QBadgeView mBadgeProfile;

    private User mUser;
    private Message mSendUserId;
    private String mUserId;
    private String mUserCountry;
    private String mUserCountryLocation;
    private String mUserCountryGps;
    private boolean mHasLocationPermission;
    private boolean mIsDeleted;
    private boolean mIsFameChecked;
    private String mTitle;
    private String mFragmentString;
    private boolean mFromEdit;

    private long mReceivedCount;
    private long mThanksCurrency;
    private int mCountStack;

    private List<FriendRank> mFriendRanks;
    private long mNumberFriends;
    private ThanksData mThanksData;

    private boolean mIsRunningTutorial;
    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mPrefsEditor;
    private SharedPreferences mSharedPrefPremium;
    private SharedPreferences.Editor mPremiumPrefsEditor;
    private Activity mActivity;
    private SharedPreferences mSharedPrefsLocation;
    private SharedPreferences.Editor mEditorLocation;
    private boolean mGivenLocationPermissions;
    private SharedPreferences mSharedCountryPrefs;
    private SharedPreferences.Editor mSharedCountryEditor;
    private ListenerRegistration mPremiumListener;
    private ListenerRegistration mMessagesListener;

    private BillingProcessor mBillingProcessor;

    private List<AuthUI.IdpConfig> mAuthProviders = Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            //new AuthUI.IdpConfig.FacebookBuilder().build(),
            new AuthUI.IdpConfig.EmailBuilder().build());

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bottomappbar);

        Log.v(TAG, "Reading from Firestore | " + TAG + " | Opened App!");

        mActivity = this;
        initViews();
        mSelectedColour = getResources().getColorStateList(R.color.colorPrimaryDark);
        mCountStack = 0;
        mIsRunningTutorial = false;
        mIsReviving = false;
        mFromEdit = false;

        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));

        View searchView = LayoutInflater.from(this).inflate(R.layout.fragment_search, null);

        setActionTitle("Thanker");

        View currentView = getWindow().getDecorView().findViewById(android.R.id.content);
        currentView.clearFocus();

        mBadgeFriends = new QBadgeView(getApplicationContext());
        mBadgeRecentThanks = new QBadgeView(this);
        mBadgeProfile = new QBadgeView(this);
        mBadgeRecentThanks.setBadgeBackgroundColor(getResources().getColor(R.color.colorAccent));
        mBadgeFriends.setBadgeBackgroundColor(getResources().getColor(R.color.colorAccent));
        //mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        if (savedInstanceState != null) {
            mIsReviving = savedInstanceState.getBoolean("is-reviving");
        }

        setupFirebase();
        initializeAuthStateListener();
        setupBottomNavigation();
        setupFab();
        setupFragmentManager();
        setupFragments();
        allUnchecked();

        mItemProfile.setTextColor(mSelectedColour);
        mItemProfile.setIconTintList(mSelectedColour);
        mIsFameChecked = false;

        if (savedInstanceState != null) {
            mIsRunningTutorial = savedInstanceState.getBoolean("is-running-tutorial", false);
            mCountStack = savedInstanceState.getInt("count-stack");
            mTitle = savedInstanceState.getString("title");
            if (mTitle != null) {
                if (!mTitle.equals("")) {
                    setActionTitle(mTitle);
                }
            }

            mFragmentString = savedInstanceState.getString("fragment-name");
            if (mFragmentString != null) {
                switch (mFragmentString) {
                    case TAB_PROFILE:
                        allUnchecked();
                        mItemProfile.setTextColor(mSelectedColour);
                        mItemProfile.setIconTintList(mSelectedColour);
                        break;

                    case TAB_FRIENDS:
                        allUnchecked();
                        mItemFriends.setTextColor(mSelectedColour);
                        mItemFriends.setIconTintList(mSelectedColour);
                        break;

                    case TAB_THANK:
                        allUnchecked();
                        mItemThank.setTextColor(mSelectedColour);
                        mItemThank.setIconTintList(mSelectedColour);
                        break;

                    case TAB_SEARCH:
                        allUnchecked();
                        mItemSearch.setTextColor(mSelectedColour);
                        mItemSearch.setIconTintList(mSelectedColour);
                        break;

                    case TAB_FAME:
                        allUnchecked();
                        mIsFameChecked = true;
                        break;
                }
            }
        }

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (mCountStack == 1) {
                    allUnchecked();
                    mCountStack = 0;
                } else {
                    mCountStack++;
                }
            }
        });

        //for showing internet connection message
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTextConnect.setVisibility(View.VISIBLE);
            }
        }, 7000);

        if (getIntent().hasExtra(USER_OBJECT) && getIntent().hasExtra("is-edit")) {
            mFromEdit = true;
            mUser = (User) getIntent().getSerializableExtra(USER_OBJECT);
        }

        if (savedInstanceState != null) {
            Log.v("Main Activity", "savedInstance isn't null.");
            mFragment = getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");
            Log.v("Main Activity", "Value of fragment: " + mFragment.toString());
            mFragmentManager.beginTransaction().replace(R.id.fragment_container, mFragment).addToBackStack(null).commit();
        } else {
            Log.v(TAG, "Checking Country in Main: " + mUserCountryLocation);
            mFragment = new MyProfileFragment();
            Bundle profileBundle;
            if (mFromEdit) {
                profileBundle = new Bundle();
                profileBundle.putSerializable(USER_OBJECT, mUser);
                mFragment.setArguments(profileBundle);
            }
            mFragmentManager.beginTransaction().replace(R.id.fragment_container, mFragment).addToBackStack(null).commit();
        }

        if (mFirebaseAuth != null) {
            if (mFirebaseAuth.getCurrentUser() != null) {
                createSendingId();
            }
        }

        NotificationUtils.setupNotification(this);

        //to hide keyboard on Activity start
        Utils.hideKeyboardFrom(this);

    }

    public void initViews() {
        mRootView = (CoordinatorLayout) findViewById(R.id.coordinator_root_view);
        mIsLoggedIn = false;
        mFriendRanks = new ArrayList<>();
        mNumberFriends = 0;
        mMainFrame = (FrameLayout) findViewById(R.id.fragment_container);
        mBar = findViewById(R.id.bottom_app_bar);
        mNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mLinearConnection = (LinearLayout) findViewById(R.id.linear_find_connection);
        mTextConnect = (TextView) findViewById(R.id.text_connect);
        mActionBar = getSupportActionBar();
        //mMsgBadge = findViewById(R.id.msg_badge);

    }

    public void setupFragments() {
        mMyProfileFragment = new MyProfileFragment();
        mOtherProfileFragment = new OtherProfileFragment();
        mSearchFragment = new SearchFragment();
        mStatsFragment = new StatsFragment();
        mThanksFragment = new ThankButtonFragment();
    }

    public void setupFragmentManager() {
        mFragmentManager = getSupportFragmentManager();
    }

    public void setupFirebase() {
        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mStorageReference = mFirebaseStorage.getReference().child(STORAGE_REFERENCE);


        if (mFirebaseAuth != null) {
            if (mFirebaseAuth.getCurrentUser() != null) {
                mUserRef = mFirestore.collection(DB_REFERENCE).document(mFirebaseAuth.getCurrentUser().getUid());
                mPremiumRef = mFirestore.collection(PREMIUM_REFERENCE).document(mFirebaseAuth.getCurrentUser().getUid());
                mThanksDataRef = mFirestore.collection(THANKS_DATA).document(mFirebaseAuth.getCurrentUser().getUid());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void initLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        checkLocationPermission();

    }

    private void createSendingId() {
        mUserId = mFirebaseAuth.getCurrentUser().getUid();
        byte[] userIdInBytes = mUserId.getBytes();
        mSendUserId = new Message(userIdInBytes);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void initializeAuthStateListener() {

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) { //the user is logged in
                    String name = user.getDisplayName();
                    String userId = user.getUid();
                    Log.v("Login", user.getDisplayName());
                    Log.v("User ID", userId);
                    //check if User exists on our Database
                    checkIfUserExistsOrCreateNewOne(userId);

                } else { //the user isn't logged in yet. Gotta launch FirebaseAuthUI

                    startActivityForResult(AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(mAuthProviders)
                                    .setLogo(R.drawable.logot)
                                    .build(),
                            RC_SIGN_IN);

                }
            }
        };
    }

    public void checkIfUserExistsOrCreateNewOne(final String userId) {
        Log.v("MainSnapshot", "UserID: " + userId);
        Log.v(TAG, "Checking if user exists");

        DocumentReference deletedRef = mFirestore.collection(DELETED_ACCOUNTS).document(userId);
        Log.v(TAG, "Checking if user exists. deleted Ref == null? " + (deletedRef == null));
        deletedRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Log.v(TAG, "Checking if user exists. Found in deleted users: " + userId);
                            mIsReviving = true;
                            Intent reviveIntent = new Intent(MainActivity.this, ReviveActivity.class);
                            startActivity(reviveIntent);
                        } else {
                            Log.v(TAG, "Checking if user exists. Didn't find in Deleted: " + userId);
                            if (mFirebaseAuth.getCurrentUser() != null) {
                                DocumentReference userRef = mFirestore.collection(DB_REFERENCE).document(mFirebaseAuth.getCurrentUser().getUid());
                                userRef.get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @RequiresApi(api = Build.VERSION_CODES.M)
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (!documentSnapshot.exists() && !mIsReviving) {
                                                    Log.v(TAG, "Checking if user exists. Entered Snapshot for creating new user: " + userId);
                                                    Log.v(TAG, "New login feature. Datasnapshot key: " + documentSnapshot.getId());
                                                    //for(DataSnapshot userData: dataSnapshot.getChildren()){
                                                    Log.v(TAG, "New login feature. Datasnapshot key of userData: " + documentSnapshot.getId());
                                                    Intent registrationActivity = new Intent(getApplicationContext(), EditInformationActivity.class);
                                                    registrationActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    ActivityCompat.finishAffinity(mActivity);
                                                    registrationActivity.putExtra(USER_ID_STRING, mFirebaseAuth.getCurrentUser().getUid());
                                                    registrationActivity.putExtra(OUR_USER_COUNTRY, mUserCountryLocation);
                                                    startActivity(registrationActivity);
                                                    //finish();
                                                    // }
                                                } else {
                                                    mUser = documentSnapshot.toObject(User.class);
                                                    Log.v(TAG, "Checking if user exists. Didn't enter area to create new user: " + userId);
                                                    mIsLoggedIn = true;
                                                    int unseenRequests = (int) mUser.getUnseenRequests();

                                                    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
                                                    //navigation.setOnNavigationItemSelectedListener(this);
                                                    //navigation.setSelectedItemId(R.id.navigation_store);
                                                    BottomNavigationMenuView bottomNavigationMenuView =
                                                            (BottomNavigationMenuView) navigation.getChildAt(0);
                                                    View v = bottomNavigationMenuView.getChildAt(1); // number of menu from left

                                                    if (unseenRequests > 0) {
                                                        mBadgeFriends.bindTarget(v).setBadgeNumber(unseenRequests);

                                                    } else {
                                                        mBadgeFriends.hide(true);
                                                    }

                                                    mActionBar.show();
                                                    mMainFrame.setVisibility(View.VISIBLE);
                                                    mBar.setVisibility(View.VISIBLE);
                                                    mNavigation.setVisibility(View.VISIBLE);
                                                    mFabThank.setVisibility(View.VISIBLE);
                                                    mItemMessages.setVisible(true);
                                                    mItemFame.setVisible(true);
                                                    mItemMore.setVisible(true);
                                                    mLinearConnection.setVisibility(View.GONE);
                                                    //mFriendsRef = mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child(FRIENDS_DB);
                                                    //mFriendsRef.addValueEventListener(mFriendsListener);
                                                    mSharedPrefsLocation = getSharedPreferences(LOCATION_PREFS + mFirebaseAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
                                                    mEditorLocation = mSharedPrefsLocation.edit();
                                                    mGivenLocationPermissions = mSharedPrefsLocation.getBoolean("given-location-permission", true);

                                                    Log.v(TAG, "To get Location permissions. Value of mGivenLocationPermissions: " + mGivenLocationPermissions);

                                                    if (mGivenLocationPermissions) {
                                                        initLocation();
                                                    }

                                                    //Checking Country
                                                    mUserCountry = mUser.getLivingCountry();

                                                    if (mUserCountryGps != null) {
                                                        if (mUserCountryGps.equalsIgnoreCase("Not Found")) {
                                                            mUserCountryLocation = mUserCountry;
                                                        } else {
                                                            mUserCountryLocation = mUserCountryGps;
                                                        }
                                                    } else {
                                                        mUserCountryLocation = mUserCountry;
                                                    }

                                                    Log.v(TAG, "Going to write Country in Shared Preferences. Country: " + mUserCountryLocation);
                                                    mSharedCountryPrefs = getSharedPreferences(COUNTRY_PREFS + mFirebaseAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
                                                    mSharedCountryEditor = mSharedCountryPrefs.edit();
                                                    mSharedCountryEditor.putString("country", mUserCountryLocation);
                                                    mSharedCountryEditor.commit();

                                                    //If user exists, also see if showing tutorial is needed
                                                    mSharedPref = getSharedPreferences(TUTORIAL_PREFS + mFirebaseAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
                                                    mPrefsEditor = mSharedPref.edit();
                                                    boolean hasReadTutorial = mSharedPref.getBoolean(TUTORIAL_READ, false);
                                                    Log.v(TAG, "Creating the tutorial. Value of hasReadTutorial: " + hasReadTutorial);
                                                    if (!hasReadTutorial && !mIsRunningTutorial) {
                                                        showTutorial();
                                                    }

                                                    /*Query messagesQuery = mFirestore.collection(MESSAGES_REFERENCE).whereEqualTo("toUserId", mUser.getUserId())
                                                            .orderBy("date", Query.Direction.DESCENDING).whereEqualTo("seen", false)
                                                            .limit(1);
                                                    mMessagesListener = messagesQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                                            if (e != null) {
                                                                return;
                                                            }

                                                            if (mMenu != null) {
                                                                mMessagesItem = mMenu.findItem(R.id.item_messages);
                                                                if (queryDocumentSnapshots.size() > 0) {
                                                                    Drawable mailDraw = mMessagesItem.getIcon();
                                                                    mailDraw.setColorFilter(getResources().getColor(R.color.gold), PorterDuff.Mode.SRC_ATOP);
                                                                    mBadgeProfile.setBadgeBackgroundColor(getResources().getColor(R.color.gold));
                                                                    MenuItem optionsItem = mMenu.findItem(R.id.item_action_more);
                                                                    if (mOptionsView != null) {
                                                                        mBadgeProfile.bindTarget(mOptionsView);
                                                                    }
                                                                    mHasNewMessages = true;
                                                                } else {
                                                                    Drawable mailDraw = mMessagesItem.getIcon();
                                                                    mailDraw.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
                                                                    mBadgeProfile.hide(true);
                                                                    mHasNewMessages = false;
                                                                }

                                                                Log.v(TAG, "Oncreate Options Menu. Now reading messages. Value of has Messages: " + mHasNewMessages);
                                                            }

                                                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Doing one read, to check if there is any unread Message");
                                                        }
                                                    });*/
                                                }

                                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading our User info");
                                            }
                                        });

                                if (mFirestore != null && mFirebaseAuth.getCurrentUser() != null) {
                                    mFirestore.collection(USER_SNIPPET).document(mFirebaseAuth.getCurrentUser().getUid())
                                            .addSnapshotListener(mActivity, new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                                    if (documentSnapshot != null) {
                                                        if (documentSnapshot.exists()) {
                                                            Log.v(TAG, "Read user snippet in Main Activity");
                                                            UserSnippet snippet = documentSnapshot.toObject(UserSnippet.class);
                                                            if (mMenu != null) {
                                                                mOptionsItem = mMenu.findItem(R.id.item_action_more);
                                                                String imageUrl = snippet.getImageUrl();
                                                                Glide
                                                                        .with(getApplicationContext())
                                                                        .load(imageUrl)
                                                                        .circleCrop()
                                                                        .apply(RequestOptions.circleCropTransform())
                                                                        .into(new SimpleTarget<Drawable>() {
                                                                            @Override
                                                                            public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
                                                                                mOptionsItem.setIcon(resource);
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    }

                                                    Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading our UserSnippet, and adding Snapshot to upate the Menu image");
                                                }
                                            });
                                }


                                checkIfIsPremium();

                                if (mFirebaseAuth.getCurrentUser() != null && mThanksDataRef != null) {
                                    mThanksDataRef
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        mThanksData = documentSnapshot.toObject(ThanksData.class);
                                                        long receivedCount = mThanksData.getReceivedCount();
                                                        long registeredCount = mThanksData.getLastRegisteredReceivedThanks();
                                                        long newThanks = receivedCount - registeredCount;

                                                        if (newThanks > 0) {
                                                            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
                                                            BottomNavigationMenuView bottomNavigationMenuView =
                                                                    (BottomNavigationMenuView) navigation.getChildAt(0);
                                                            View v = bottomNavigationMenuView.getChildAt(0); // number of menu from left

                                                            mBadgeRecentThanks.bindTarget(v).setBadgeNumber((int) newThanks);
                                                            mThanksData.setLastRegisteredReceivedThanks(receivedCount);
                                                        }
                                                    }

                                                    Log.v(TAG, "Reading from Firestore | " + TAG + " | Doing a read to check for unseen recent Thanks received");
                                                }
                                            });
                                }

                                mFirestore.collection("time").document("now").update("time", FieldValue.serverTimestamp());

                                /*if (mFromEdit) {
                                    mFragmentManager.beginTransaction().replace(R.id.fragment_container, new MyProfileFragment()).addToBackStack(null).commit();
                                }*/
                            }
                        }

                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Checking if our User is a deleted one");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v(TAG, "Checking if user exists. Failed to read");
                    }
                });

    }

    public void updateMessages(String userId, Menu menu) {
        Log.v(TAG, "Oncreate Options Menu. ENTERING THIS METHOD!");
        Query messagesQuery = mFirestore.collection(MESSAGES_REFERENCE).whereEqualTo("toUserId", userId)
                .orderBy("date", Query.Direction.DESCENDING).whereEqualTo("seen", false)
                .limit(1);
        mMessagesListener = messagesQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (mMenu != null) {
                    mMessagesItem = mMenu.findItem(R.id.item_messages);
                    if (queryDocumentSnapshots.size() > 0) {
                        Drawable mailDraw = mMessagesItem.getIcon();
                        mailDraw.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                        mBadgeProfile.setBadgeBackgroundColor(getResources().getColor(R.color.colorAccent));
                        MenuItem moreItem = menu.findItem(R.id.item_action_more);

                        Log.v(TAG, "Messages badge. Options View exists");
                        //mMsgBadge.setVisibility(View.VISIBLE);

                        mHasNewMessages = true;
                    } else {

                        //mMsgBadge.setVisibility(View.GONE);
                        Drawable mailDraw = mMessagesItem.getIcon();
                        mailDraw.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
                        mBadgeProfile.hide(true);
                        mHasNewMessages = false;
                    }

                    Log.v(TAG, "Oncreate Options Menu. Now reading messages. Value of has Messages: " + mHasNewMessages);
                }

                Log.v(TAG, "Reading from Firestore | " + TAG + " | Doing one read, to check if there is any unread Message");
            }
        });
    }

    public View getActionBarView() {
        Window window = getWindow();
        View v = window.getDecorView();
        int resId = getResources().getIdentifier("action_bar_container", "id", "android");
        return v.findViewById(resId);
    }

    @SuppressLint("RestrictedApi")
    public void setupBottomNavigation() {
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        mBottomNavigationView.setItemIconTintList(getResources().getColorStateList(R.color.defaultTextColor));
        mBottomNavigationView.setItemTextColor(getResources().getColorStateList(R.color.defaultTextColor));

        mItemProfile = (BottomNavigationItemView) mBottomNavigationView.findViewById(R.id.item_profile);
        mItemFriends = (BottomNavigationItemView) mBottomNavigationView.findViewById(R.id.item_friends);
        mItemThank = (BottomNavigationItemView) mBottomNavigationView.findViewById(R.id.item_thank);
        mItemSearch = (BottomNavigationItemView) mBottomNavigationView.findViewById(R.id.item_search);
        mItemPremium = (BottomNavigationItemView) mBottomNavigationView.findViewById(R.id.item_premium);

        mItemProfile.setChecked(false);

        //hideItemTitles();

        //mBottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);

        final String country = mUserCountryLocation;
        Log.v(TAG, "Country got in Setup Bottom Navigation: " + country);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("RestrictedApi")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                mFragment = null;

                //initLocation();

                switch (itemId) {

                    case (R.id.item_profile):
                        allUnchecked();
                        mBadgeRecentThanks.hide(true);
                        mActionBar.setDisplayHomeAsUpEnabled(false);
                        mTitle = "Thanker";
                        mFragmentString = TAB_PROFILE;
                        setActionTitle(mTitle);
                        mItemProfile.setTextColor(mSelectedColour);
                        mItemProfile.setIconTintList(mSelectedColour);
                        mItemProfile.setTitle(getString(R.string.profile));
                        mItemProfile.setChecked(false);
                        mFragment = mMyProfileFragment;
                        break;

                    case (R.id.item_search):
                        allUnchecked();
                        mBadgeRecentThanks.hide(true);
                        mActionBar.setDisplayHomeAsUpEnabled(false);
                        mFragmentString = TAB_SEARCH;
                        mItemSearch.setTextColor(mSelectedColour);
                        mItemSearch.setIconTintList(mSelectedColour);
                        mTitle = getString(R.string.search);
                        setActionTitle(mTitle);
                        mFragment = mSearchFragment;
                        mIsPureSearch = true;
                                             /*String country = getResources().getConfiguration().locale.getDisplayCountry();
                                             if(country == null){
                                                 country = mUserCountry;
                                             }*/
                        Bundle searchBundle = new Bundle();
                        searchBundle.putBoolean(PURE_SEARCH_STRING, mIsPureSearch);
                        searchBundle.putString(OUR_USER_COUNTRY, mUserCountryLocation);
                        mFragment.setArguments(searchBundle);
                        Log.v(TAG, "Country location of user. Passing the argument: " + mUserCountryLocation);
                        break;

                    case (R.id.item_thank):
                        allUnchecked();
                        mBadgeRecentThanks.hide(true);
                        mActionBar.setDisplayHomeAsUpEnabled(false);
                        mFragmentString = TAB_THANK;
                        mItemThank.setTextColor(mSelectedColour);
                        mItemThank.setIconTintList(mSelectedColour);
                        mTitle = getString(R.string.thank);
                        setActionTitle(mTitle);
                        mFragment = mThanksFragment;
                        Log.v(TAG, "Country got before sending to ThanksFragment: " + country);
                        Bundle thankBundle = new Bundle();
                        thankBundle.putString(THANKER_ID_STRING, mUserId);
                        thankBundle.putString(OUR_USER_COUNTRY, mUserCountryLocation);
                        mFragment.setArguments(thankBundle);
                        break;

                    case (R.id.item_friends):
                        mBadgeFriends.hide(true);
                        mBadgeRecentThanks.hide(true);
                        allUnchecked();
                        mActionBar.setDisplayHomeAsUpEnabled(false);
                        mFragmentString = TAB_FRIENDS;
                        mItemFriends.setTextColor(mSelectedColour);
                        mItemFriends.setIconTintList(mSelectedColour);
                        mTitle = getString(R.string.friends);
                        setActionTitle(mTitle);

                        mFragment = new FriendsFragment();
                        Bundle friendsBundle = new Bundle();
                        friendsBundle.putString(OUR_USER_COUNTRY, mUserCountryLocation);
                        friendsBundle.putString(OUR_USER_ID, mFirebaseAuth.getCurrentUser().getUid());
                        mFragment.setArguments(friendsBundle);

                        break;

                    case R.id.item_premium:
                        allUnchecked();
                        mBadgeRecentThanks.hide(true);
                        Intent premiumIntent;
                        mIsSimplyPremium = mSharedPrefPremium.getBoolean("isPremium", false);
                        Log.v(TAG, "In-App premium. Going to PremiumActivity. mIsSimplyPremium = " + mIsSimplyPremium);
                        if (mIsSimplyPremium) {
                            mItemPremium.setTextColor(mSelectedColour);
                            mItemPremium.setIconTintList(mSelectedColour);
                            Fragment premiumFragment = new PremiumFragmentYes();
                            Bundle premiumBundle = new Bundle();
                            premiumBundle.putBoolean(INFO_HAS_PREMIUM, mIsSimplyPremium);
                            premiumFragment.setArguments(premiumBundle);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, premiumFragment).addToBackStack(null).commit();

                        } else {
                            premiumIntent = new Intent(MainActivity.this, PremiumActivityNo.class);
                            premiumIntent.putExtra(INFO_HAS_PREMIUM, mIsSimplyPremium);
                            startActivity(premiumIntent);
                        }

                        break;

                }

                if (mFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment).addToBackStack(null).commit();
                }

                return true;
            }
        });
    }

    @SuppressLint("RestrictedApi")
    public void hideItemTitles() {
        mItemProfile.setTitle("");
        mItemFriends.setTitle("");
        mItemThank.setTitle("");
        mItemSearch.setTitle("");
        mItemPremium.setTitle("");
    }

    @SuppressLint("RestrictedApi")
    public void allUnchecked() {
        mNavigation.getMenu().setGroupCheckable(0, false, true);

        mItemProfile.setChecked(false);

        ColorStateList color = getResources().getColorStateList(R.color.grey);
        mItemProfile.setTextColor(color);
        mItemFriends.setTextColor(color);
        mItemThank.setTextColor(color);
        mItemSearch.setTextColor(color);
        mItemPremium.setTextColor(getResources().getColorStateList(R.color.defaultTextColor2));

        mItemProfile.setIconTintList(color);
        mItemFriends.setIconTintList(color);
        mItemThank.setIconTintList(color);
        mItemSearch.setIconTintList(color);
        mItemSearch.setIconTintList(color);
        mItemPremium.setIconTintList(getResources().getColorStateList(R.color.defaultTextColor2));

        if (mItemFame != null) {
            Drawable fameDraw = mItemFame.getIcon();
            fameDraw.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.MULTIPLY);
        }

        mCountStack = 0;
    }

    public void setActionTitle(String title) {
        SpannableString s = new SpannableString(title);
        /*s.setSpan(new TypefaceSpan(this, "greatwishes.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.defaultTextColor)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        //ActionBar actionBar = getSupportActionBar();
        //mActionBar.setTitle(Html.fromHtml("<font color=\"#808080\">" + s + "</font>"));
        mActionBar.setTitle(s);
        mActionBar.setSubtitle(null);
    }

    public void setupFab() {
        mFabThank = (FloatingActionButton) findViewById(R.id.fab_thank);

        mFabThank.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                //checkThanksAndUncheckOtherButtons(); When we have the badge active, and click the FAB, the app crashes
                allUnchecked();
                mItemThank.setIconTintList(mSelectedColour);
                mItemThank.setTextColor(mSelectedColour);
                setActionTitle(getString(R.string.thank));
                mFragment = new ThankButtonFragment();
                Log.v(TAG, "Country got before sending to ThanksFragment: " + mUserCountryLocation);
                Bundle thankBundle = new Bundle();
                thankBundle.putString(THANKER_ID_STRING, mUserId);
                thankBundle.putString(OUR_USER_COUNTRY, mUserCountryLocation);
                mFragment.setArguments(thankBundle);
                mFragmentManager.beginTransaction().replace(R.id.fragment_container, mFragment).addToBackStack(null).commit();
            }
        });
    }

    @SuppressLint("RestrictedApi")
    public void checkThanksAndUncheckOtherButtons() {
        BottomNavigationItemView searchItem = (BottomNavigationItemView) findViewById(R.id.item_search);
        BottomNavigationItemView premiumItem = (BottomNavigationItemView) findViewById(R.id.item_premium);
        BottomNavigationItemView profileItem = (BottomNavigationItemView) findViewById(R.id.item_profile);
        BottomNavigationItemView friendsItem = (BottomNavigationItemView) findViewById(R.id.item_friends);
        BottomNavigationItemView thanksItem = (BottomNavigationItemView) findViewById(R.id.item_thank);

        Drawable profileDraw = profileItem.getBackground();
        Drawable friendsDraw = friendsItem.getBackground();
        Drawable thankDraw = thanksItem.getBackground();
        Drawable searchDraw = searchItem.getBackground();
        Drawable premiumDraw = premiumItem.getBackground();

        profileDraw.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.MULTIPLY);
        friendsDraw.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.MULTIPLY);
        thankDraw.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.MULTIPLY);
        searchDraw.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.MULTIPLY);
        premiumDraw.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.MULTIPLY);

        searchItem.setChecked(false);
        premiumItem.setChecked(false);
        profileItem.setChecked(false);
        //friendsItem.setChecked(false);
        thanksItem.setChecked(true);
    }

    public void setNavigationVisibility(boolean visible) {
        if (/*mNavigation.isShown() && */!visible) {
            mNavigation.setVisibility(View.GONE);
            mFabThank.setVisibility(View.GONE);
        } else if (/*!mNavigation.isShown() && */visible) {
            mNavigation.setVisibility(View.VISIBLE);
            mFabThank.setVisibility(View.VISIBLE);
        }
    }

    public void showTutorial() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                mViewItemMore = (View) mActivity.findViewById(R.id.item_action_more);
                final View moreView = (View) mActivity.findViewById(R.id.item_action_more);
                final View fabView = (View) mActivity.findViewById(R.id.fab_thank);
                final View pictureView = mActivity.findViewById(R.id.profile_picture);
                final View searchView = mActivity.findViewById(R.id.item_search);
                final View fameView = mActivity.findViewById(R.id.item_fame);
                final View premiumView = mActivity.findViewById(R.id.item_premium);
                //mViewItemMore = mItemMore.getActionView();

                mIsRunningTutorial = true;

                Log.v(TAG, "Checking which items are null. mActivity: " + (mActivity == null));
                Log.v(TAG, "Checking which items are null. Item More: " + (mViewItemMore == null));
                Log.v(TAG, "Checking which items are null. Fab Item: " + (fabView == null));
                Log.v(TAG, "Checking which items are null. Picture item: " + (pictureView == null));
                Log.v(TAG, "Checking which items are null. search item: " + (searchView == null));
                Log.v(TAG, "Checking which items are null. fame View: " + (fameView == null));
                Log.v(TAG, "Checking which items are null. Premium View: " + (premiumView == null));

                com.takusemba.spotlight.SimpleTarget firstTarget = new com.takusemba.spotlight.SimpleTarget.Builder(mActivity)
                        .setPoint(fabView) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                        .setRadius(90f) // radius of the Target
                        .setTitle("Thanker") // title
                        .setDescription(getString(R.string.tutorial_1)) // description
                        .setOnSpotlightStartedListener(new OnTargetStateChangedListener<com.takusemba.spotlight.SimpleTarget>() {
                            @Override
                            public void onStarted(com.takusemba.spotlight.SimpleTarget target) {
                                // do something
                            }

                            @Override
                            public void onEnded(com.takusemba.spotlight.SimpleTarget target) {

                            }
                        })
                        .build();

                com.takusemba.spotlight.SimpleTarget secondTarget = new com.takusemba.spotlight.SimpleTarget.Builder(mActivity)
                        //.setPoint(665f, 125f) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                        .setPoint(mViewItemMore)
                        .setRadius(50f) // radius of the Target
                        .setTitle(getString(R.string.options)) // title
                        .setDescription(getString(R.string.tutorial_2)) // description
                        .setOnSpotlightStartedListener(new OnTargetStateChangedListener<com.takusemba.spotlight.SimpleTarget>() {
                            @Override
                            public void onStarted(com.takusemba.spotlight.SimpleTarget target) {
                                // do something
                            }

                            @Override
                            public void onEnded(com.takusemba.spotlight.SimpleTarget target) {
                                // do something
                            }
                        })
                        .build();

                com.takusemba.spotlight.SimpleTarget thirdTarget = new com.takusemba.spotlight.SimpleTarget.Builder(mActivity)
                        .setPoint(searchView) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                        .setRadius(80f) // radius of the Target
                        .setTitle(DataUtils.capitalize(getString(R.string.search))) // title
                        .setDescription(getString(R.string.tutorial_3)) // description
                        .setOnSpotlightStartedListener(new OnTargetStateChangedListener<com.takusemba.spotlight.SimpleTarget>() {
                            @Override
                            public void onStarted(com.takusemba.spotlight.SimpleTarget target) {
                                // do something
                            }

                            @Override
                            public void onEnded(com.takusemba.spotlight.SimpleTarget target) {
                                // do something
                            }
                        })
                        .build();

                /*com.takusemba.spotlight.SimpleTarget fourthTarget = new com.takusemba.spotlight.SimpleTarget.Builder(mActivity)
                        .setPoint(fameView) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                        .setRadius(60f) // radius of the Target
                        .setTitle("Hall of Fame") // title
                        .setDescription(getString(R.string.tutorial_4)) // description
                        .setOnSpotlightStartedListener(new OnTargetStateChangedListener<com.takusemba.spotlight.SimpleTarget>() {
                            @Override
                            public void onStarted(com.takusemba.spotlight.SimpleTarget target) {
                                // do something
                            }

                            @Override
                            public void onEnded(com.takusemba.spotlight.SimpleTarget target) {
                                // do something
                            }
                        })
                        .build();*/

                com.takusemba.spotlight.SimpleTarget fifthTarget = new com.takusemba.spotlight.SimpleTarget.Builder(mActivity)
                        .setPoint(premiumView) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                        .setRadius(100f) // radius of the Target
                        .setTitle("Premium") // title
                        .setDescription(getString(R.string.tutorial_5)) // description
                        .setOnSpotlightStartedListener(new OnTargetStateChangedListener<com.takusemba.spotlight.SimpleTarget>() {
                            @Override
                            public void onStarted(com.takusemba.spotlight.SimpleTarget target) {
                                // do something
                            }

                            @Override
                            public void onEnded(com.takusemba.spotlight.SimpleTarget target) {
                                // do something
                            }
                        })
                        .build();

                com.takusemba.spotlight.SimpleTarget sixthTarget = new com.takusemba.spotlight.SimpleTarget.Builder(mActivity)
                        .setPoint(pictureView) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                        .setRadius(230f) // radius of the Target
                        .setTitle(getString(R.string.explore_thanker)) // title
                        .setDescription(getString(R.string.tutorial_6)) // description
                        .setOnSpotlightStartedListener(new OnTargetStateChangedListener<com.takusemba.spotlight.SimpleTarget>() {
                            @Override
                            public void onStarted(com.takusemba.spotlight.SimpleTarget target) {
                                // do something
                            }

                            @Override
                            public void onEnded(com.takusemba.spotlight.SimpleTarget target) {
                                // do something
                            }
                        })
                        .build();

                Spotlight.with(mActivity)
                        .setOverlayColor(ContextCompat.getColor(mActivity, R.color.colorPrimaryDarkTransparent)) // background overlay color
                        .setDuration(333L) // duration of Spotlight emerging and disappearing in ms
                        .setAnimation(new DecelerateInterpolator(1f)) // animation of Spotlight
                        .setTargets(firstTarget, secondTarget, thirdTarget, /*fourthTarget,*/ fifthTarget, sixthTarget) // set targets. see below for more info
                        .setClosedOnTouchedOutside(true) // set if target is closed when touched outside
                        .setOnSpotlightStartedListener(new OnSpotlightStartedListener() { // callback when Spotlight starts
                            @Override
                            public void onStarted() {
                                //Toast.makeText(getApplicationContext(), "spotlight is started", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setOnSpotlightEndedListener(new OnSpotlightEndedListener() { // callback when Spotlight ends
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onEnded() {
                                //Toast.makeText(getApplicationContext(), "spotlight is ended", Toast.LENGTH_SHORT).show();
                                //Mark as the user has seen the Tutorial
                                mPrefsEditor.putBoolean(TUTORIAL_READ, true);
                                mPrefsEditor.commit();
                            }
                        })
                        .start(); // start Spotlight
            }
        }, 500);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        mItemMessages = menu.findItem(R.id.item_messages);
        mItemFame = menu.findItem(R.id.item_fame);
        mItemMore = menu.findItem(R.id.item_action_more);
        mItemMyPremium = menu.findItem(R.id.item_my_premium);

        mItemMessages.setVisible(false);
        mItemFame.setVisible(false);
        mItemMore.setVisible(false);

        mOptionsView = findViewById(R.id.item_action_more);
        Log.v(TAG, "Oncreate Options Menu. Value of has Messages: " + mHasNewMessages);

        Drawable messagesDraw = mItemMessages.getIcon();
        Drawable fameDraw = mItemFame.getIcon();

        messagesDraw.setColorFilter(getResources().getColor(R.color.defaultTextColor2), PorterDuff.Mode.MULTIPLY);
        fameDraw.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.MULTIPLY);

        if (mIsFameChecked) {
            fameDraw.setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.MULTIPLY);
        }

        if (mFirebaseAuth.getCurrentUser() != null) {
            Log.v(TAG, "Messages badge. Auth exists");
            updateMessages(mFirebaseAuth.getCurrentUser().getUid(), menu);
        } else {
            Log.v(TAG, "Messages badge. Auth does NOT exist");
        }

        /*new Handler().post(new Runnable() {
            @Override
            public void run() {

                if(mFirebaseAuth.getCurrentUser() != null && mImageMore != null){
                    Log.v(TAG, "Messages badge. Auth exists");
                    updateMessages(mFirebaseAuth.getCurrentUser().getUid());
                }
                else {
                    Log.v(TAG, "Messages badge. Auth does NOT exist");
                }

            }
        });*/

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment selectedFragment;

        //initLocation();

        switch (item.getItemId()) {

            case (R.id.item_invite_email):
                allUnchecked();
                mBadgeRecentThanks.hide(true);
                mTitle = getString(R.string.email_invites_title);
                setActionTitle(mTitle);
                Bundle inviteBundle = new Bundle();
                inviteBundle.putString(INVITE_EXTRA, getString(R.string.yes));
                inviteBundle.putString(OUR_USER_COUNTRY, mUserCountryLocation);
                selectedFragment = new SearchFragment();
                selectedFragment.setArguments(inviteBundle);
                Log.v(TAG, "Country location of user. Passing the argument: " + mUserCountryLocation);
                mFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();
                break;

            case R.id.item_invite_contacts:
                allUnchecked();
                mBadgeRecentThanks.hide(true);
                Fragment inviteFragment = new ContactsInviteFragment();
                Bundle contactsBundle = new Bundle();
                contactsBundle.putString(OUR_USER_COUNTRY, mUserCountryLocation);
                contactsBundle.putSerializable(USER_OBJECT, mUser);
                inviteFragment.setArguments(contactsBundle);
                mFragmentManager.beginTransaction().replace(R.id.fragment_container, inviteFragment).addToBackStack(null).commit();
                break;

            /*case R.id.item_invite_sms:
                allUnchecked();
                mTitle = getString(R.string.sms_invites_title);
                setActionTitle(mTitle);
                Bundle smsBundle = new Bundle();
                smsBundle.putString(OUR_USER_COUNTRY, mUserCountryLocation);
                smsBundle.putString(USER_ID_STRING, mUserId);
                selectedFragment = new SmsFragment();
                selectedFragment.setArguments(smsBundle);
                Log.v(TAG, "Country location of user. Passing the argument: " + mUserCountryLocation);
                mFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();
                break;*/

            case R.id.item_messages:
                allUnchecked();
                mBadgeRecentThanks.hide(true);
                Drawable mailDraw = mMessagesItem.getIcon();
                mailDraw.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
                mActionBar.setDisplayHomeAsUpEnabled(false);/*
                                     Intent messagesIntent = new Intent(this, MessagesListActivity.class);
                                     messagesIntent.putExtra(BOOL_HAS_NEW_MESSAGES, mHasNewMessages);
                                     startActivity(messagesIntent);*/
                Fragment msgFragment = new MessagesListFragment();
                Bundle msgBundle = new Bundle();
                msgBundle.putBoolean(INFO_HAS_PREMIUM, mIsSimplyPremium);
                msgBundle.putBoolean(BOOL_HAS_NEW_MESSAGES, mHasNewMessages);
                msgBundle.putString(OUR_USER_COUNTRY, mUserCountryLocation);
                msgFragment.setArguments(msgBundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, msgFragment).addToBackStack(null).commit();
                break;

            case R.id.item_stats_menu:
                allUnchecked();
                mBadgeRecentThanks.hide(true);
                mTitle = getString(R.string.stats);
                setActionTitle(mTitle);
                selectedFragment = new StatsFragment();
                Bundle statsBundle = new Bundle();
                statsBundle.putString(OUR_USER_COUNTRY, mUserCountryLocation);
                statsBundle.putBoolean(INFO_HAS_PREMIUM, mIsSimplyPremium);
                selectedFragment.setArguments(statsBundle);
                Log.v(TAG, "Updating Stats. Passing the country of " + mUserCountryLocation);
                mFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();
                break;

            case R.id.item_earn_thankers:
                allUnchecked();
                mBadgeRecentThanks.hide(true);
                Intent rewardIntent = new Intent(MainActivity.this, RewardActivity.class);
                rewardIntent.putExtra(THANKS_CURRENCY, mThanksCurrency);
                if (mFirebaseAuth.getCurrentUser() != null) {
                    rewardIntent.putExtra(USER_ID_STRING, mFirebaseAuth.getCurrentUser().getUid());
                }
                startActivity(rewardIntent);
                break;

            case R.id.item_logout:
                AuthUI.getInstance().signOut(this);
                return true;

            case R.id.item_edit:
                allUnchecked();
                mBadgeRecentThanks.hide(true);
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                /*mUserData = mDatabaseReference.child(user.getUid());
                mUserData.addValueEventListener(mEditEventListener);*/
                Intent editIntent = new Intent(MainActivity.this, EditInformationActivity.class);
                editIntent.putExtra(USER_OBJECT, mUser);
                editIntent.putExtra("is-edit", true);
                startActivity(editIntent);
                break;

            case R.id.item_hints:
                allUnchecked();
                mBadgeRecentThanks.hide(true);
                Intent hintsIntent = new Intent(MainActivity.this, HintsActivity.class);
                startActivity(hintsIntent);
                break;

            case R.id.item_contact:
                allUnchecked();
                mBadgeRecentThanks.hide(true);
                Intent contactIntent = new Intent(this, ContactActivity.class);
                contactIntent.putExtra(OUR_USER_COUNTRY, mUserCountryLocation);
                startActivity(contactIntent);
                break;

            case R.id.item_fame:
                Log.v(TAG, "Checking Country going to Fame: " + mUserCountryLocation);
                allUnchecked();
                mBadgeRecentThanks.hide(true);
                mActionBar.setDisplayHomeAsUpEnabled(false);
                mFragmentString = TAB_FAME;
                Drawable fameDraw = mItemFame.getIcon();
                fameDraw.setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.MULTIPLY);
                mTitle = getString(R.string.hall_of_fame_literal);
                setActionTitle(mTitle);
                selectedFragment = new FameFragment();
                Bundle bundle = new Bundle();
                bundle.putString(OUR_USER_COUNTRY, mUserCountryLocation);
                bundle.putString(OUR_USER_ID, mUserId);
                selectedFragment.setArguments(bundle);
                mFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();
                break;

            case R.id.item_show_description:
                Intent descriptionIntent = new Intent(MainActivity.this, DescriptionActivity.class);
                descriptionIntent.putExtra(USER_OBJECT, mUser);
                startActivity(descriptionIntent);
                break;

            case R.id.item_delete:
                allUnchecked();
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.deleting_account))
                        .setMessage(getString(R.string.sure_to_delete))
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                Intent deleteIntent = new Intent(MainActivity.this, DeleteActivity.class);
                                startActivity(deleteIntent);
                            }
                        })

                        .create()
                        .show();

                break;

            case R.id.item_location:
                Log.v(TAG, "Ativando localização: carregou no botão. Valor de locationpermission: " + mHasLocationPermission);
                if (!mHasLocationPermission) {
                    initLocation();
                    Log.v(TAG, "Ativando localização");
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.location_permission_already_granted), Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.item_policy:
                Intent policyIntent = new Intent(MainActivity.this, PrivacyActivity.class);
                startActivity(policyIntent);
                break;

            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        mMenu = menu;

        MenuItem actionViewItem = menu.findItem(R.id.item_action_more);
        mOptionsView = actionViewItem.getActionView();

        return super.onPrepareOptionsMenu(menu);
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.location_permissions))
                        .setMessage(getString(R.string.enable_permissions_location_question))
                        .setPositiveButton(getString(R.string.ok_exclamation), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mEditorLocation.putBoolean("given-location-permission", false);
                                mEditorLocation.commit();
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {

            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mHasLocationPermission = true;
            if (location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                mUserCountryGps = DataUtils.getLocationCountry(this, latitude, longitude);
                mEditorLocation.putBoolean("given-location-permission", true);
                mEditorLocation.commit();
                Log.v(TAG, "Checking user's location: " + mUserCountryGps);
                //mUserCountryLocation = DataUtils.getEnglishCountry(this, mUserCountryLocation);


            } else {
                Log.v(TAG, "Checking user's location. Couldn't find any location");
            }
        }
        return true;
    }

    public void checkIfIsPremium() {
        mBillingProcessor = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                //showToast("onProductPurchased: " + productId);

            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                //showToast("onBillingError: " + Integer.toString(errorCode));
            }

            @Override
            public void onBillingInitialized() {
                //showToast("onBillingInitialized");
                Log.v(TAG, "In-App premium. Entering check");
                boolean hasTransactions = mBillingProcessor.loadOwnedPurchasesFromGoogle();
                if (hasTransactions) {
                    Log.v(TAG, "In-App premium. Has Transactions");
                    TransactionDetails premiumDetails = mBillingProcessor.getSubscriptionTransactionDetails(SUBSCRIPTION_ID);

                    if (premiumDetails != null) {
                        Log.v(TAG, "In-App premium. Premium Account!");
                        Log.v(TAG, "In-App premium. " + premiumDetails.purchaseInfo.toString());
                        mIsSimplyPremium = true;
                    } else {
                        Log.v(TAG, "In-App premium. Not Premium");
                        mIsSimplyPremium = false;
                    }
                }

                if (mFirebaseAuth.getCurrentUser() != null && mPremiumRef != null) {

                    mSharedPrefPremium = getSharedPreferences(PREMIUM_REFERENCE + mFirebaseAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
                    mPremiumPrefsEditor = mSharedPrefPremium.edit();

                    boolean wasPremium = mSharedPrefPremium.getBoolean("isPremium", false);
                    Log.v(TAG, "PremiumRef and Buy. Value of wasPremium: " + wasPremium);

                    if (!mIsSimplyPremium) {
                        //mPremiumRef.delete();
                        mPremiumPrefsEditor.putBoolean("isPremium", false);
                    } else {
                        mPremiumPrefsEditor.putBoolean("isPremium", true);
                    }

                    mPremiumPrefsEditor.commit();

                    if (hasTransactions && wasPremium && !mIsSimplyPremium && mPremiumRef != null) {
                        mPremiumRef.delete();
                        Log.v(TAG, "PremiumRef and Buy: Deleted Premium Ref");
                    } else {
                        Log.v(TAG, "PremiumRef and Buy: Did NOT delete Premium Ref");
                    }

                    if (mPremiumListener == null) {
                        mPremiumListener = mPremiumRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                if (documentSnapshot != null) {
                                    if (documentSnapshot.exists()) {
                                        checkIfIsPremium();
                                        Log.v(TAG, "In-App premium. Confirmed premium in Firestore");
                                    } else {
                                        mIsSimplyPremium = false;
                                        mPremiumPrefsEditor.putBoolean("isPremium", false);
                                        mPremiumPrefsEditor.commit();
                                        Log.v(TAG, "In-App premium. Confirmed NOT premium in Firestore");
                                    }
                                }

                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading our Premium info");
                            }
                        });
                    }
                }
            }

            @Override
            public void onPurchaseHistoryRestored() {
                //showToast("onPurchaseHistoryRestored");

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(TAG, "Results of login. Request Code: " + requestCode + ". Result Code: " + resultCode + ". Data: " + data.toString());

        Activity activity = mActivity;

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK || resultCode == -1) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }

        mActivity = activity;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        mHasLocationPermission = true;
                        if (location != null) {
                            double longitude = location.getLongitude();
                            double latitude = location.getLatitude();
                            mUserCountryGps = DataUtils.getLocationCountry(this, latitude, longitude);
                            mEditorLocation.putBoolean("given-location-permission", true);
                            mEditorLocation.commit();
                            //mUserCountryLocation = DataUtils.getEnglishCountry(this, mUserCountryLocation);
                            Log.v(TAG, "Checking user's location: " + mUserCountryGps);
                        } else {
                            Log.v(TAG, "Checking user's location. Couldn't find any location");
                        }

                        //Request location updates:
                        //String provider = mLocationManager.getBestProvider(new Criteria(), false);
                        //mLocationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }


    //to hide keyboard when a Fragment is opened
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            mActionBar.setDisplayHomeAsUpEnabled(false);
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mPremiumListener != null) {
            mPremiumListener.remove();
            mPremiumListener = null;
            Log.v(TAG, "In-App premium. Is mPremiumListener null: " + (mPremiumListener == null));
        }

        if (mMessagesListener != null) {
            mMessagesListener.remove();
            mMessagesListener = null;
        }
    }


    /*
    @Override
    public void onDestroy(){
        super.onDestroy();

        SharedPreferences sharedPref = getSharedPreferences(OUR_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.clear().commit();
    }*/


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //outState.putBoolean(IS_USER_DELETED, mIsDeleted);

        //Save the fragment's instance
        outState.putString("title", mTitle);
        outState.putString("fragment-name", mFragmentString);
        outState.putInt("count-stack", mCountStack);
        outState.putBoolean("is-running-tutorial", mIsRunningTutorial);
        outState.putBoolean("is-reviving", mIsReviving);

        getSupportFragmentManager().putFragment(outState, "myFragmentName", getSupportFragmentManager().findFragmentById(R.id.fragment_container));
        Log.v("Main Activity", "Name of fragment: m" + getSupportFragmentManager().findFragmentById(R.id.fragment_container).toString());

    }

}
