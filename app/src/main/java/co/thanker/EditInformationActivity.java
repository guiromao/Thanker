package co.thanker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
//import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.thanker.data.Phone;
import co.thanker.data.StatsThanks;
import co.thanker.data.Thanks;
import co.thanker.data.ThanksData;
import co.thanker.data.ThanksValue;
import co.thanker.data.User;
import co.thanker.data.User.Category;
import co.thanker.data.UserSnippet;
import co.thanker.data.UserValue;
import co.thanker.utils.DataUtils;
import co.thanker.utils.Utils;

public class EditInformationActivity extends AppCompatActivity {

    private final String TAG = "EditInformationActivity";
    private final String THANKS_DB = "thanks-db";
    private final String THANKS_DATA = "thanks-data";
    private final String USER_OBJECT = "user-object";
    private static final String DB_REFERENCE = "users";
    private final String STATS_THANKS = "stats-thanks";
    private final String INVITE_REF = "invites";
    private static final String SMS_REFERENCE = "sms-invites";
    private final String IMAGES_DB = "images-db";
    private final String COUNTRIES_REFERENCE = "countries-values";
    private final String THANKS_GIVEN = "thanks-given";
    private final String THANKS_RECEIVED = "thanks-received";
    private final String TOP_REF = "tops";
    private final String TOP_USERS_THANKS_RECEIVED = "top-users-thanks-received";
    private final String TOP_USERS_THANKS_GIVEN = "top-users-thanks-given";
    private final String USER_ID_STRING = "user-id-string";
    private final String EMAIL_EXTRA = "extra-email";
    private final String BLANK = "";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String USER_PRIMARY_CATEGORY = "primary-category";
    private final String USER_SECONDARY_CATEGORY = "secondary-category";
    private final String USER_BIRTHDAY = "user-birthday";
    private final String USER_NAME = "user-name";
    private final String USER_IMAGE = "user-image";
    private final String APP_ID = "7teHDZan37TvoDd5slkhcT99gDz2";
    private final String PLATFORM_MESSAGE = "platform-message";
    private final String TO_ENG = "to-system";
    private final String TO_SYSTEM = "to-eng";

    private final long INIT_VALUE = 0;

    private FirebaseFirestore mFirestore;
    private DocumentReference mFirestoreUser;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataReference;
    private DatabaseReference mUserDataReference;
    private DatabaseReference mThankerAccountReference;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    private User mUserReference;

    private Activity mActivity;

    private LinearLayout mLinearPhoneInviteQuestion;
    private LinearLayout mLinearPhoneData;
    private LinearLayout mLinearPrivacy;
    private RadioGroup mRadioGroupPhone;
    private RadioButton mRadioYes;
    private RadioButton mRadioNo;
    private TextView mPhoneLabel;
    private TextView mCodeLabel;
    private TextView mTextPrivacy;
    private TextView mTextTop;
    private TextView mTextCountryCode;
    private EditText mNameEditText;
    private EditText mBirthdayText;
    private EditText mInputPhone;
    private EditText mInputCode;
    private Spinner mCountrySpinner;
    //private Spinner mCitySpinner;
    private Spinner mPrimSpinner;
    private Spinner mSecSpinner;
    private ProgressBar mProgressBar;
    private Button mSaveButton;
    private ImageView mImageEdit;
    private ImageView mImageLogo;
    private ImageView mImageInfoSec;
    private CardView mCardImage;
    //private CheckBox mCheckboxPrivacy;

    private String mCountry;
    private String mUserCountry;
    //private String mCity;
    private String mPrimaryCategory;
    private String mSecondaryCategory;
    private String mImageUrl;
    private String mTitle;

    private User mUser = null;
    private User mUserThanker;
    private ThanksData mThanksData;
    private ThanksData mThanksDataThanker;

    private String mCode;
    private boolean mRightCode;
    private String mPhone;
    private String mPhoneKey;
    //private CountryCodePicker mCountryCodeList;

    private String mUserId;
    private String mUserEmail;
    private long mBirthdayInMillis;

    private boolean mIsThereThankerAccount;
    private long mThankerGivenThanksNumber;
    private long mThankerThanksCount;

    private boolean mIsEditActivity;
    private boolean mHasEdited;
    private int mPosCountry;
    private int mPosPrimCat;
    private int mPosSecCat;
    private String mName;
    private String mBirthday;

    private ActionBar mActionBar;
    private ColorStateList mDisabledColor;
    private ColorStateList mPrimaryColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_information);

        mActivity = EditInformationActivity.this;

        mActionBar = getSupportActionBar();

        mAuth = FirebaseAuth.getInstance();

        mHasEdited = false;

        mIsEditActivity = true;

        mSaveButton = (Button) findViewById(R.id.save_information_button);
        mNameEditText = (EditText) findViewById(R.id.input_text_name);
        mBirthdayText = (EditText) findViewById(R.id.input_birthday);
        mCountrySpinner = (Spinner) findViewById(R.id.spinner_country);
        /*mInputPhone = (EditText) findViewById(R.id.input_phone_number);
        mInputCode = (EditText) findViewById(R.id.input_code);*/
        //mCitySpinner = (Spinner) findViewById(R.id.spinner_city);
        mPrimSpinner = (Spinner) findViewById(R.id.spinner_primary_category);
        mSecSpinner = (Spinner) findViewById(R.id.spinner_secondary_category);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mLinearPhoneInviteQuestion = (LinearLayout) findViewById(R.id.linear_phone_checking);
        //mLinearPhoneData = (LinearLayout) findViewById(R.id.linear_phone_fields);
        mLinearPrivacy = (LinearLayout) findViewById(R.id.linear_privacy);
        mRadioGroupPhone = (RadioGroup) findViewById(R.id.radio_group_phone);
        mRadioYes = (RadioButton) findViewById(R.id.radio_button_yes);
        mRadioNo = (RadioButton) findViewById(R.id.radio_button_no);
        mPhoneLabel = (TextView) findViewById(R.id.text_phone_label);
        //mCodeLabel = (TextView) findViewById(R.id.text_code_label);
        mTextPrivacy = (TextView) findViewById(R.id.text_register_privacy);
        mTextTop = (TextView) findViewById(R.id.text_complete_registration);
        mTextCountryCode = (TextView) findViewById(R.id.text_country_code);
        mImageEdit = (ImageView) findViewById(R.id.image_edit_activity);
        mImageLogo = (ImageView) findViewById(R.id.image_logo);
        mImageInfoSec = (ImageView) findViewById(R.id.image_info_sec_category);
        mCardImage = (CardView) findViewById(R.id.cardview_image);
        //mCountryCodeList = (CountryCodePicker) findViewById(R.id.country_list_spinner);
        //mCheckboxPrivacy = (CheckBox) findViewById(R.id.checkbox_privacy);

        mImageUrl = null;

        mProgressBar.setVisibility(View.GONE);

        mDisabledColor = getResources().getColorStateList(R.color.disabled_button);
        mPrimaryColor = getResources().getColorStateList(R.color.colorPrimary);

        /*mCountryCodeList.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                updateCountryCode();
            }
        });*/

        mCountry = null;
        //mCity = null;
        mPrimaryCategory = "";
        mSecondaryCategory = "";
        mName = "";
        mBirthday = "";

        initializeBirthdayInput();
        initializeCountrySpinner();
        initializeCategorySpinners();
        initializeFirebase();

        mFirestore.collection(DB_REFERENCE).document(APP_ID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            mUserThanker = documentSnapshot.toObject(User.class);
                            mIsThereThankerAccount = true;
                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading User info of Thanker\'s account");
                            mFirestore.collection(THANKS_DATA).document(APP_ID)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                mThanksDataThanker = documentSnapshot.toObject(ThanksData.class);
                                            }

                                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading Thanks Data from User Thanker (app ID)");
                                        }
                                    });
                        }
                    }
                });

        mImageInfoSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setCancelable(true);
                builder.setTitle(getString(R.string.what_are_sec_category));
                builder.setMessage(getString(R.string.this_is_sec_category));
                builder.setPositiveButton(getString(R.string.got_it),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        Intent createdUserIntent = getIntent();
        if (createdUserIntent != null) {
            Log.v("MainSnapshot", "EditActivity has Intent");

            //check if user already exists
            if (createdUserIntent.hasExtra(USER_OBJECT) || createdUserIntent.hasExtra(USER_ID_STRING)) {

                if (createdUserIntent.hasExtra("is-edit")) {

                    mUser = (User) createdUserIntent.getSerializableExtra(USER_OBJECT);
                    mUserId = mUser.getUserId();
                    String id = mUser.getUserId();
                    String country = DataUtils.getTranslatedCountry(getApplicationContext(), mUser.getLivingCountry());

                    Log.v(TAG, "Country stuff. Going in Edit with: " + createdUserIntent.getStringExtra(OUR_USER_COUNTRY));
                    Log.v(TAG, "Country stuff. Going in Edit with Translation: " + country);
                    mName = DataUtils.capitalize(mUser.getName());

                    mTextTop.setText(getString(R.string.update_account));
                    mNameEditText.setText(mName);
                    mCountrySpinner.setSelection(getIndex(mCountrySpinner, country));
                    mPrimSpinner.setSelection(getIndex(mPrimSpinner, translateBack(mUser.getPrimaryCategory())));
                    mSecSpinner.setSelection(getIndex(mSecSpinner, translateBack(mUser.getSecondaryCategory())));
                    updateBirthdayFromIntent(mUser.getBirthday());
                    mImageUrl = mUser.getImageUrl();
                    Log.v("MainSnapEdit", mImageUrl);
                    mBirthday = mBirthdayText.getText().toString();
                    Log.v(TAG, "Checking birthday from Intent: " + mBirthday);

                    Glide.with(this).load(mImageUrl).into(mImageEdit);
                    mImageLogo.setVisibility(View.GONE);

                    mPosCountry = mCountrySpinner.getSelectedItemPosition();
                    mPosPrimCat = mPrimSpinner.getSelectedItemPosition();
                    mPosSecCat = mSecSpinner.getSelectedItemPosition();

                    SpannableString s = new SpannableString(getString(R.string.edit_information_label));
                    /*s.setSpan(new TypefaceSpan(this, "greatwishes.otf"), 0, s.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
                    s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.defaultTextColor)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mActionBar.setTitle(s);
                    //mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
                    mHasEdited = false;
                    initEditListeners();
                } else {
                    mIsEditActivity = false;

                    SpannableString s = new SpannableString(getString(R.string.complete_info));
                    s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.defaultTextColor)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mActionBar.setTitle(s);
                    //mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
                    mActionBar.setHomeButtonEnabled(false);
                    mActionBar.setDisplayHomeAsUpEnabled(false);
                    mLinearPrivacy.setVisibility(View.VISIBLE);
                    //mCheckboxPrivacy.setEnabled(true);
                    initPrivacyClick();
                    mSaveButton.setText(getString(R.string.create));
                    mCardImage.setVisibility(View.GONE);

                    initPhoneData();
                    initCodeRetrievalListener();

                    if (mAuth.getCurrentUser() != null) {
                        if (mAuth.getCurrentUser().getDisplayName() != null) {
                            mNameEditText.setText(mAuth.getCurrentUser().getDisplayName());
                        }
                    }
                    //to reinitialize Country Spinner with our Country
                }

                Log.v("MainSnapshot", "EditActivity has Extra for STRING");
                if (mAuth != null) {
                    firebaseUser = mAuth.getCurrentUser();
                    //mUserId = createdUserIntent.getStringExtra(USER_ID_STRING);
                    mUserCountry = DataUtils.getTranslatedCountry(this, createdUserIntent.getStringExtra(OUR_USER_COUNTRY));
                    mUserEmail = firebaseUser.getEmail();
                    mUserId = mAuth.getCurrentUser().getUid();//createdUserIntent.getStringExtra(USER_ID_STRING);
                    Log.v("MainSnapshot", mUserId + ", " + mUserEmail);
                    Log.v(TAG, "Checking user's location, from Edit Activity: " + mUserCountry);

                    if (mUserCountry != null) {
                        if (mUserCountry.equals("")) {
                            mUserCountry = getResources().getConfiguration().locale.getDisplayCountry();
                        }
                    } else {
                        mUserCountry = getResources().getConfiguration().locale.getDisplayCountry();
                        Log.v(TAG, "Checking user's location, from Edit Activity, after getting Locale: " + mUserCountry);
                    }

                    if(!mIsEditActivity){
                        initializeCountrySpinner();
                    }
                }

                mSaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mNameEditText.getText().toString().length() >= 3 &&
                                !mNameEditText.getText().toString().contains("@")) {
                            mCountry = (String) mCountrySpinner.getSelectedItem();
                            mPrimaryCategory = translateToEnglish((String) mPrimSpinner.getSelectedItem());
                            mSecondaryCategory = translateToEnglish((String) mSecSpinner.getSelectedItem());
                            if (mPrimaryCategory.equalsIgnoreCase(mSecondaryCategory)) {
                                mSecondaryCategory = "";
                            }
                            if (allFieldsAreFilled()) {
                                Log.v("EditInfoAct", "Going to save user");
                                saveUser();
                                if (mIsEditActivity) {
                                    terminateActivity();
                                    //finish();
                                }
                                //
                                else {
                                    mSaveButton.setEnabled(false);
                                    mSaveButton.setBackground(getResources().getDrawable(R.drawable.button_rounded_grey));
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "Please fill out all fields!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Name has to have more than 3 characters and no \"@\"!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }

    }


    public void initializeFirebase() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    public void initEditListeners() {

        mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mName.equalsIgnoreCase(s.toString())) {
                    Log.v(TAG, "Changes: changed on Name. mName: " + mName + ", s: " + s);
                }
                mHasEdited = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBirthdayText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mBirthday.trim().equalsIgnoreCase(s.toString().trim())) {
                    mHasEdited = true;
                    Log.v(TAG, "Changes: changed on Birthday");
                    Log.v(TAG, "Checking birthday from change Listener: " + s.toString());
                    Log.v(TAG, "Checking birthday. Compare s: " + s.toString() + ", with mBirthday: " + mBirthday + ".");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String currentLocation = DataUtils.getTranslatedCountry(getApplicationContext(), mUser.getLivingCountry());

                if (mCountrySpinner.getSelectedItem() != null && currentLocation != null) {
                    if (!currentLocation.equalsIgnoreCase(mCountrySpinner.getSelectedItem().toString())) {
                        mHasEdited = true;
                        Log.v(TAG, "Changes: changed on Country. Current location: " + currentLocation + ", country spinner item: " + mCountrySpinner.getSelectedItem().toString());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mPrimSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mPosPrimCat != position) {
                    mPosPrimCat = position;
                    mHasEdited = true;
                    Log.v(TAG, "Changes: changed on Primary Category");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSecSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mPosSecCat != position) {
                    mPosSecCat = position;
                    mHasEdited = true;
                    Log.v(TAG, "Changes: changed on Secondary Category");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initPhoneData() {
        //mPhoneLabel.setVisibility(View.VISIBLE);
        //mRadioGroupPhone.setVisibility(View.VISIBLE);
        mLinearPhoneInviteQuestion.setVisibility(View.GONE);
        mRightCode = false;

        mRadioGroupPhone.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radio_button_yes:
                        if (!mRightCode) {
                            mLinearPhoneData.setVisibility(View.VISIBLE);
                            mSaveButton.setBackgroundTintList(mDisabledColor);
                            mSaveButton.setClickable(false);
                            //mCountryCodeList.setVisibility(View.VISIBLE);
                        }
                        break;

                    case R.id.radio_button_no:
                        mLinearPhoneData.setVisibility(View.GONE);
                        mSaveButton.setBackgroundTintList(mPrimaryColor);
                        mSaveButton.setClickable(true);
                        //mCountryCodeList.setVisibility(View.GONE);

                        break;
                }
            }
        });
    }

    private void initCodeRetrievalListener() {

        /*mInputPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (mInputPhone.getText().toString().trim().length() >= 9) {
                    mInputCode.setEnabled(true);
                    mCodeLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    mInputCode.setEnabled(false);
                    mCodeLabel.setTextColor(getResources().getColor(R.color.black));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        /*mInputCode.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mInputPhone.getText().toString().trim().length() >= 9 && mInputCode.getText().toString().trim().length() == 4) {
                    mProgressBar.setVisibility(View.VISIBLE);

                    //String countryCode = mCountryCodeList.getSelectedCountryCode();
                    final String number = countryCode + mInputPhone.getText().toString().trim();
                    final String code = mInputCode.getText().toString().trim();
                    DatabaseReference numberRef = mDatabase.getReference().child(INVITE_REF).child(SMS_REFERENCE).child(number);

                    numberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Phone phone = dataSnapshot.getValue(Phone.class);
                                String trueCode = phone.getCode();

                                if (trueCode.equalsIgnoreCase(code)) {
                                    mSaveButton.setBackgroundTintList(mPrimaryColor);
                                    mSaveButton.setClickable(true);
                                    mCode = code;
                                    mPhone = number;
                                    mRightCode = false;
                                    Toast.makeText(getApplicationContext(), getString(R.string.right_code), Toast.LENGTH_LONG).show();

                                    View view = mActivity.getCurrentFocus();
                                    if (view != null) {
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.wrong_code), Toast.LENGTH_LONG).show();
                                }
                            }
                            Utils.hideKeyboardFrom(mActivity);
                            mProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });*/

        /*mInputCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mInputCode.isEnabled()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.please_write_phone), Toast.LENGTH_LONG).show();
                }
            }
        });*/
    }

    private void initPrivacyClick() {
        mTextPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent privacyIntent = new Intent(EditInformationActivity.this, PrivacyActivity.class);
                startActivity(privacyIntent);
            }
        });
    }

    public void retrieveUser(String userId) {
        final DatabaseReference userReference = mDatabase.getReference().child(DB_REFERENCE).child(userId);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mUser = dataSnapshot.getValue(User.class);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateBirthdayFromIntent(long dateInMillis) {
        Date date = new Date(dateInMillis);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        updateLabel(calendar);
    }

    private int getIndex(Spinner spinner, String value) {

        for (int i = 0; i != spinner.getCount(); i++) {
            Log.v(TAG, "Country loading. Spinner value: " + spinner.getItemAtPosition(i) + ". Value: " + value);
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                Log.v(TAG, "Country loading. Going to return i: " + i);
                return i;
            }
        }
        return 0;
    }

    public void terminateActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ActivityCompat.finishAffinity(this);
        intent.putExtra(USER_OBJECT, mUser);
        intent.putExtra("is-edit", true);
        startActivity(intent);
    }

    public void saveUser() {
        User user;
        String country = DataUtils.getEnglishCountry(this, mCountry);
        String name = mNameEditText.getText().toString().toLowerCase();
        mFirestoreUser = mFirestore.collection(DB_REFERENCE).document(mAuth.getCurrentUser().getUid());
        Log.v("MainSnapEdit", "Image again: " + mImageUrl);

        Log.v(TAG, "English country: " + country);

        if (mUser != null) {
            mUser.setName(name);
            mUser.setBirthday(mBirthdayInMillis);
            mUser.setPrimaryCategory(mPrimaryCategory);
            mUser.setSecondaryCategory(mSecondaryCategory);
            mUser.setLivingCountry(country);
            mFirestoreUser.set(mUser);
            UserSnippet snippet = new UserSnippet(name, mUserId, mUser.getEmail(), mUser.getImageUrl(), mPrimaryCategory, mSecondaryCategory, mUser.getLivingCountry());
            mFirestore.collection("user-snippet").document(mUser.getUserId()).set(snippet);
        } else {
            mUser = new User(mUserId, mUserEmail, mPhone, name, mBirthdayInMillis, mImageUrl, mPrimaryCategory.toString(), mSecondaryCategory.toString()/*, mCity*/, country);
            mThanksData = new ThanksData(true);
            UserSnippet snippet = new UserSnippet(name, mUserId, mUser.getEmail(), User.DEFAULT_IMAGE, mPrimaryCategory, mSecondaryCategory, country);
            mFirestore.collection("user-snippet").document(mUser.getUserId()).set(snippet);
            //addThanksValuesReference(mUserId);

            DataUtils.createMessage(mUserId, getString(R.string.welcome_name, DataUtils.capitalize(mNameEditText.getText().toString().trim())),
                    getString(R.string.welcome_text), PLATFORM_MESSAGE, 0);

            mProgressBar.setVisibility(View.VISIBLE);

            if (mIsThereThankerAccount) {
                firstThanks();
            } else {
                mFirestore.collection(THANKS_DATA).document(mUserId).set(mThanksData);
                mFirestoreUser.set(mUser);
                terminateActivity();
            }
        }

    }

    public void firstThanks() {

        final Thanks thanks = new Thanks(APP_ID, mUserId, getString(R.string.thank_you_for_joining_thanker), System.currentTimeMillis(),
                mPrimaryCategory.toString(), mSecondaryCategory.toString(), DataUtils.generateYear(), DataUtils.generateMonth(),
                DataUtils.generateDay(), DataUtils.getEnglishCountry(this, mUserCountry), "NORMAL");

        final FirebaseFirestore database = mFirestore;

        database.collection(THANKS_DB)
                .whereEqualTo("toUserId", mUserId)
                .whereEqualTo("fromUserId", APP_ID)
                .whereEqualTo("year", DataUtils.generateYear())
                .whereEqualTo("month", DataUtils.generateMonth())
                .whereEqualTo("day", DataUtils.generateDay())
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() == 0) {
                            database.collection(THANKS_DB).add(thanks)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            List<UserValue> ourTopReceived = new ArrayList<>();
                                            List<UserValue> thankerTopGiven = mUserThanker.getTopUsersGiven();

                                            ourTopReceived.add(new UserValue(APP_ID, 1));
                                            thankerTopGiven.add(new UserValue(mUserId, 1));

                                            mUser.setTopUsersReceived(ourTopReceived);
                                            mUserThanker.setTopUsersGiven(thankerTopGiven);

                                            mThanksData.setReceivedThanksValue(1);
                                            mThanksData.setReceivedCount(1);

                                            mThanksDataThanker.setGivenThanksValue(mThanksDataThanker.getGivenThanksValue() + 1);
                                            mThanksDataThanker.setThanksCount(mThanksDataThanker.getThanksCount() + 1);
                                            mThanksDataThanker.setThanksCurrency(mThanksDataThanker.getThanksCurrency() + 1);

                                            long thankerCurrency = mThanksDataThanker.getThanksCurrency();
                                            int toAdd = 0;

                                            if ((thankerCurrency % 10) == 0) {
                                                toAdd += 10;
                                            }

                                            if ((thankerCurrency % 100) == 0) {
                                                toAdd += 100;
                                            }

                                            if ((thankerCurrency % 1000) == 0) {
                                                toAdd += 1000;
                                            }

                                            if ((thankerCurrency % 10000) == 0) {
                                                toAdd += 10000;
                                            }

                                            if ((thankerCurrency % 100000) == 0) {
                                                toAdd += 100000;
                                            }

                                            thankerCurrency += toAdd;

                                            mThanksDataThanker.setThanksCurrency(thankerCurrency);

                                            mFirestore.collection(THANKS_DATA).document(mUserId).set(mThanksData);
                                            mFirestore.collection(THANKS_DATA).document(APP_ID).set(mThanksDataThanker);

                                            String tempPrimary = mPrimaryCategory;
                                            String tempSecondary = mSecondaryCategory;


                                            final String primaryCategory = tempPrimary;
                                            final String secondaryCategory = tempSecondary;

                                            Log.v(TAG, "Primary Category: " + tempPrimary);
                                            Log.v(TAG, "Secondary Category: " + tempSecondary);

                                            mUserThanker.addValueOnCategory(2, primaryCategory);
                                            mUserThanker.addValueOnCategory(1, secondaryCategory);

                                            mFirestore.collection(DB_REFERENCE).document(APP_ID).set(mUserThanker);
                                            mFirestoreUser.set(mUser);

                                            mFirestore.runTransaction(new Transaction.Function<Void>() {
                                                @Nullable
                                                @Override
                                                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                                    final String country = DataUtils.getEnglishCountry(getApplicationContext(), mUserCountry);
                                                    Log.v(TAG, "New user. Check country: " + country + ". Original: " + mUserCountry);
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
                                                                    StatsThanks stats = new StatsThanks(country, primaryCategory, secondaryCategory, thanks.getThanksType());
                                                                    StatsThanks existingStats;
                                                                    StatsThanks writeStats = stats;

                                                                    if (queryDocumentSnapshots.size() > 0) {
                                                                        for (QueryDocumentSnapshot statsSnapshot : queryDocumentSnapshots) {
                                                                            String docKey = statsSnapshot.getId();
                                                                            existingStats = statsSnapshot.toObject(StatsThanks.class);
                                                                            existingStats.addStatsThanksOf(stats);
                                                                            writeStats = existingStats;
                                                                            mFirestore.collection(COUNTRIES_REFERENCE).document(docKey).set(writeStats);
                                                                        }
                                                                    } else {
                                                                        mFirestore.collection(COUNTRIES_REFERENCE).add(writeStats);
                                                                    }

                                                                    terminateActivity();
                                                                    Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading Stats Thanks of today");
                                                                }

                                                            });

                                                    return null;
                                                }
                                            });
                                        }
                                    });
                        }
                    }
                });

    }

    public void addThanksValuesReference(String id) {
        List<ThanksValue> values = new ArrayList<>();
        List<String> categories = DataUtils.retrieveThanksTypesInList();

        for (String cat : categories) {
            values.add(new ThanksValue(cat, INIT_VALUE));
        }

        for (ThanksValue v : values) {
            mDataReference.child(v.getKey()).setValue(v.getValue());
        }
    }

    public boolean allFieldsAreFilled() {

        Log.v("EditInfoAct", "Going to allFieldsAreFilled");

        if (mPrimaryCategory == mSecondaryCategory) {
            mSecondaryCategory = Category.BLANK.toString();
        }

        Log.v("EditInfoAct", "Going to allFieldsAreFilled Part Two");
        Log.v("EditInfoAct", "" + mPrimaryCategory.toString());

        return (mNameEditText.getText().toString() != null
                //|| !(mNameEditText.getText().toString()).equals("")))
                && mBirthdayText.getText().toString().length() > 4
                //|| !(mBirthdayText.getText().toString().equals(""))
                && !(mPrimaryCategory.toString().equals(BLANK)) /* &&
                (mCheckboxPrivacy.isEnabled() ? mCheckboxPrivacy.isChecked() : true)*/);
    }

    public void initializeBirthdayInput() {
        final Calendar myCalendar = Calendar.getInstance();

        //mBirthdayText = (EditText) findViewById(R.id.input_birthday);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(myCalendar);
            }

        };

        mBirthdayText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EditInformationActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel(Calendar myCalendar) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        //mBirthdayText.setText(sdf.format(myCalendar.getTime()));
        mBirthdayText.setText(DataUtils.getDateString(this, myCalendar.getTimeInMillis()));

        mBirthdayInMillis = convertCalendarToMillis(myCalendar);
    }

    public String translateToEnglish(String categoryString) {
        String result = "";

        Log.v(TAG, "Going to translate: " + categoryString);

        Configuration conf = getResources().getConfiguration();
        conf = new Configuration(conf);
        conf.setLocale(new Locale("en"));
        Context englishContext = createConfigurationContext(conf);

        if (categoryString.equals("")) {
            result = categoryString;
        } else if (categoryString.equalsIgnoreCase(getString(R.string.person))) {
            result = englishContext.getString(R.string.person);
        } else if (categoryString.equalsIgnoreCase(getString(R.string.brand))) {
            result = englishContext.getString(R.string.brand);
        } else if (categoryString.equalsIgnoreCase(getString(R.string.association))) {
            result = englishContext.getString(R.string.association);
        } else {
            String[] stringArray = getResources().getStringArray(R.array.translatable_pages);
            String[] englishArray = englishContext.getResources().getStringArray(R.array.translatable_pages);
            boolean found = false;

            for (int i = 0; i != stringArray.length && !found; i++) {
                if (categoryString.equalsIgnoreCase(stringArray[i])) {
                    result = DataUtils.decapitalize(englishArray[i]);
                    found = true;
                }
            }
        }

        Log.v(TAG, "Going to translate: " + categoryString + ". Translated to: " + result);

        return result;
    }

    public String translateBack(String category) {
        String result = "";

        Log.v(TAG, "Going to translate back: " + category);

        if (!category.equals("")) {
            if (category.equalsIgnoreCase("Person")) {
                result = getString(R.string.person);
            } else if (category.equalsIgnoreCase("Brand")) {
                result = getString(R.string.brand);
            } else if (category.equalsIgnoreCase("Association")) {
                result = getString(R.string.association);
            } else {
                result = DataUtils.translateAndFormat(this, category);
            }
        }

        Log.v(TAG, "Going to translate back: " + category + ". Translated to: " + result);
        ;

        return result;
    }

    public void updateCountryCode() {
        /*String countryCode = mCountryCodeList.getSelectedCountryCode();
        mTextCountryCode.setText("+" + countryCode);*/
    }

    public long convertCalendarToMillis(Calendar calendar) {
        long timeInMillis;
        Date date = calendar.getTime();
        timeInMillis = date.getTime();
        return timeInMillis;
    }

    public void initializeCountrySpinner() {
        String currentCountry;
        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();

        for (Locale loc : locales) {
            String country = loc.getDisplayCountry();
            if ((country.length() > 0) && !(countries.contains(country))) {
                countries.add(country);
            }
        }

        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter<String> adapterCountries = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, countries);
        mCountrySpinner.setAdapter(adapterCountries);

        currentCountry = mUserCountry;

        mCountrySpinner.setSelection(DataUtils.retrieveItem(countries, currentCountry));

        if (currentCountry != null) {
            //mCountryCodeList.setCountryForNameCode(DataUtils.getTwoCodeCountry(currentCountry));
            updateCountryCode();
        }
    }


    public void initializeCategorySpinners() {
        List<String> categoriesPrimary = new ArrayList<>();
        List<String> categoriesSecondary = new ArrayList<>();
        //categories.add(new Enum());

        for (Category cat : Category.values()) {
            if (cat != Category.BLANK) {
                String category = cat.toString();

                if (category.equalsIgnoreCase("Person")) {
                    category = getString(R.string.person);
                } else if (category.equalsIgnoreCase("Brand")) {
                    category = getString(R.string.brand);
                } else if (category.equalsIgnoreCase("Association")) {
                    category = getString(R.string.association);
                } else {
                    category = DataUtils.translateAndFormat(this, cat.toString());
                }

                categoriesPrimary.add(category);
            }
        }

        for (Category cat : Category.values()) {
            String category = cat.toString();

            if (category.equals("")) {

            } else if (category.equalsIgnoreCase("Person")) {
                category = getString(R.string.person);
            } else if (category.equalsIgnoreCase("Brand")) {
                category = getString(R.string.brand);
            } else if (category.equalsIgnoreCase("Association")) {
                category = getString(R.string.association);
            } else {
                category = DataUtils.translateAndFormat(this, cat.toString());
            }

            categoriesSecondary.add(category);
        }

        ArrayAdapter<Category> categoriesPrimaryAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, categoriesPrimary);
        ArrayAdapter<Category> categoriesSecondaryAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, categoriesSecondary);

        mPrimSpinner.setAdapter(categoriesPrimaryAdapter);
        mSecSpinner.setAdapter(categoriesSecondaryAdapter);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();

        MenuInflater inflater = getMenuInflater();

        if (!mIsEditActivity) {
            inflater.inflate(R.menu.menu_new_account, menu);
        } else {
            inflater.inflate(R.menu.menu_void, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.item_logout) {
            AuthUI.getInstance().signOut(this);
            finish();
            return true;
        } else if (itemId == android.R.id.home) {
            if (mHasEdited) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle(getString(R.string.you_made_changes));
                builder.setMessage(getString(R.string.are_you_sure));
                builder.setPositiveButton(getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                finish();
            }

            return true;
        }

        return onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        mActionBar.setHomeAsUpIndicator(upArrow);
    }

    @Override
    public void onStart() {
        super.onStart();

        mFirestore.collection(DB_REFERENCE).document(APP_ID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            mUserThanker = documentSnapshot.toObject(User.class);
                        }
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
