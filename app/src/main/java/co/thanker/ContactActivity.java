package co.thanker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import co.nedim.maildroidx.MaildroidX;
import co.nedim.maildroidx.MaildroidXType;
import co.thanker.data.User;
import co.thanker.data.UserSnippet;
import co.thanker.utils.DataUtils;
import co.thanker.utils.TypefaceSpan;

public class ContactActivity extends AppCompatActivity {

    private final String TAG = "ContactActivity";
    private final String USER_SNIPPET = "user-snippet";
    private final String USER_COUNTRY = "user-country";
    private final String LOCATION_PREFS = "location-prefs";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DocumentReference mUserRef;

    private UserSnippet mUser;

    private EditText mEditSubject;
    private EditText mEditBody;
    private Button mButtonSend;
    private TextView mTextTitle;
    private TextView mTextSubtitle;
    private ImageView mImageSent;
    private TextView mTextSent;
    private ActionBar mActionBar;

    private String mUserCountry;
    private Intent resultIntent;
    private SharedPreferences mSharedPrefsLocation;

    private boolean mWasMessageSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        SpannableString s = new SpannableString(getString(R.string.contact_us));
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.defaultTextColor)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(s);

        resultIntent = getIntent();

        mWasMessageSent = false;

        initViews();

        if(savedInstanceState != null){
            mWasMessageSent = savedInstanceState.getBoolean("wasMessageSent", false);
        }

        if(mWasMessageSent){
            disableViews();
            mTextTitle.setText(getString(R.string.email_sent));
        }

        setupFirebase();

        mSharedPrefsLocation = getSharedPreferences(LOCATION_PREFS + mAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
        mUserCountry = mSharedPrefsLocation.getString("country", "");

        mUserRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            mUser = documentSnapshot.toObject(UserSnippet.class);
                        }
                    }
                });

    }

    public void initViews(){
        mEditSubject = (EditText) findViewById(R.id.input_text_subject);
        mEditBody = (EditText) findViewById(R.id.input_text_body);
        mButtonSend = (Button) findViewById(R.id.button_send);
        mTextTitle = (TextView) findViewById(R.id.text_email_title);
        mTextSubtitle = (TextView) findViewById(R.id.text_subtitle);
        mImageSent = (ImageView) findViewById(R.id.image_sent);
        mTextSent = (TextView) findViewById(R.id.text_sent);

        mEditSubject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0){
                    mTextTitle.setVisibility(View.GONE);
                    mTextSubtitle.setVisibility(View.GONE);
                }

                else if (s.length() == 0 && mEditBody.getText().toString().length() == 0){
                    mTextTitle.setVisibility(View.VISIBLE);
                    mTextSubtitle.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0){
                    mTextTitle.setVisibility(View.GONE);
                    mTextSubtitle.setVisibility(View.GONE);
                }

                else if (s.length() == 0 && mEditSubject.getText().toString().length() == 0){
                    mTextTitle.setVisibility(View.VISIBLE);
                    mTextSubtitle.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mButtonSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sendMaildroidInContactActivity();
                mWasMessageSent = true;
                disableViews();
                mImageSent.setVisibility(View.VISIBLE);
                mTextSent.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 3000);

            }
        });
    }

    public void disableViews(){
        mButtonSend.setClickable(false);
        mButtonSend.setTextColor(getResources().getColor(R.color.white));
        mEditSubject.setEnabled(false);
        mEditBody.setEnabled(false);
        mButtonSend.setVisibility(View.GONE);
        mEditSubject.setVisibility(View.GONE);
        mEditBody.setVisibility(View.GONE);
        mTextTitle.setVisibility(View.GONE);
        mTextSubtitle.setVisibility(View.GONE);
    }

    public void setupFirebase(){
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUserRef = mFirestore.collection(USER_SNIPPET).document(mAuth.getCurrentUser().getUid());
    }

    private void sendMaildroidInContactActivity(){

        String title = mEditSubject.getText().toString();
        String body = mEditBody.getText().toString() + "\n\nFrom: " + mUser.getEmail() + "\nName: " + DataUtils.capitalize(mUser.getName()) +
                "\nUser ID: " + mUser.getUserId() + "\nSending from Country: " + mUserCountry + "\nCountry in Thanker: " + mUser.getLivingCountry();

        new MaildroidX.Builder()
                .smtp("smtp.gmail.com")
                .smtpUsername("thanker.feedback@gmail.com")
                .smtpPassword("thefeedback2020yeah!")
                //.smtpAuthentication(true)
                .port("465")
                .type(MaildroidXType.PLAIN)
                .to("thanker.feedback@gmail.com")
                .from(mUser.getEmail())
                .subject(title)
                .body(body)
                .mail();

        mTextTitle.setText(getString(R.string.email_sent));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        state.putBoolean("wasMessageSent", mWasMessageSent);
    }

    @Override
    public void onResume(){
        super.onResume();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        mActionBar.setHomeAsUpIndicator(upArrow);
    }

    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public void onStop(){
        super.onStop();

    }
}
