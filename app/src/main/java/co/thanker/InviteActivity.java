package co.thanker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import co.nedim.maildroidx.MaildroidX;
import co.nedim.maildroidx.MaildroidXType;
import co.thanker.adapters.PersonalStatsAdapter;
import co.thanker.data.ThanksData;
import co.thanker.data.ThanksInvite;
import co.thanker.data.ThanksValue;
import co.thanker.data.User;
import co.thanker.utils.DataUtils;
import co.thanker.utils.Utils;

public class InviteActivity extends AppCompatActivity {

    private final String TAG = "InviteActivity";
    private final String DB_REFERENCE = "users";
    private final String INVITE_REF = "invites";
    private final String THANKS_DATA = "thanks-data";
    private final String INVITE_EMAIL = "invite-email";
    private final String OTHER_USER_EMAIL = "other-user-email";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String OUR_USER = "our-user";
    private final int SELECTED_THANKS = 1;
    private final int SELECTED_SUPER = 2;
    private final int SELECTED_MEGA = 3;
    private final int SELECTED_POWER = 4;

    private final int THANKS_VALUE = 1;
    private final int SUPER_THANKS_VALUE = 10;
    private final int MEGA_THANKS_VALUE = 100;
    private final int POWER_THANKS_VALUE = 1000;

    private DatabaseReference mCurrencyReference;

    private User mUser;
    private String mEmailOtherUser;
    private String mMessageString;
    private int mTypeThanks;

    private TextView mTextTitle;
    private TextView mTextSubtitle;
    private TextView mTextAdditionalInfo;
    private TextView mTextSendEmail;
    private TextView mTextValue;
    private ImageView mButtonThanks;
    private ImageView mButtonSuperThanks;
    private ImageView mButtonMegaThanks;
    private ImageView mButtonPowerThanks;
    private CardView mCardThanks;
    private CardView mCardSuper;
    private CardView mCardMega;
    private CardView mCardPower;
    private EditText mInputDescription;
    private Button mButtonSend;
    private ProgressBar mProgressBar;
    private LinearLayout mLinearDescription;

    private Context mContext;

    private FirebaseFirestore mFirestore;
    private CollectionReference mInvitesRef;
    private Query mOurUserQuery;

    private String mDay;
    private String mMonth;
    private String mYear;
    private String mDateCode;
    private String mUserCountry;

    private String mChildKey;

    private boolean mHasThanked;

    private ThanksInvite mThanksInvite;

    private ColorStateList mDisabledColor;
    private ColorStateList mThanksColor;
    private ColorStateList mSuperThanksColor;
    private ColorStateList mMegaThanksColor;
    private ColorStateList mPowerThanksColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        mContext = getBaseContext();
        mHasThanked = false;

        SpannableString s = new SpannableString(getString(R.string.email_invites));
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.defaultTextColor)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        getSupportActionBar().setTitle(s);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        String title = getString(R.string.thank_literal) + " ";

        initViews();
        initializeDate();
        initializeColors();

        Intent resultIntent = getIntent();

        if (resultIntent != null) {
            mUser = (User) resultIntent.getSerializableExtra(OUR_USER);
            mEmailOtherUser = resultIntent.getStringExtra(OTHER_USER_EMAIL);
            mEmailOtherUser = mEmailOtherUser.replace('.', ',');
            mUserCountry = resultIntent.getStringExtra(OUR_USER_COUNTRY);
            title += "<b>" + mEmailOtherUser + "</b> " + getString(R.string.so_we_send_invite);
            mTextTitle.setText(Html.fromHtml(title));

            Log.v(TAG, "Inviting and seeing country: " + mUserCountry);
        }

        mThanksInvite = new ThanksInvite(mUser.getUserId(), mEmailOtherUser, System.currentTimeMillis(), "NORMAL", mUserCountry);

        if (savedInstanceState != null) {
            mHasThanked = savedInstanceState.getBoolean("has-thanked");
            mTypeThanks = savedInstanceState.getInt("selected-thanks");
            mMessageString = savedInstanceState.getString("thanks-message");

            switch (mTypeThanks) {
                case SELECTED_THANKS:
                    whiteCardViews();
                    mTypeThanks = SELECTED_THANKS;
                    mCardThanks.setBackground(getResources().getDrawable(R.drawable.circlethanks));
                    mTextValue.setText("Thanks x1");
                    mTextValue.setTextColor(getResources().getColor(R.color.colorPrimary));
                    mThanksInvite = new ThanksInvite(mUser.getUserId(), mEmailOtherUser, System.currentTimeMillis(), "NORMAL", mUserCountry);
                    break;

                case SELECTED_SUPER:
                    whiteCardViews();
                    mTypeThanks = SELECTED_SUPER;
                    mCardSuper.setBackground(getResources().getDrawable(R.drawable.circlesuper));
                    mTextValue.setText("Thanks x10");
                    mTextValue.setTextColor(getResources().getColor(R.color.superThanksCoin));
                    mThanksInvite = new ThanksInvite(mUser.getUserId(), mEmailOtherUser, System.currentTimeMillis(), "SUPER", mUserCountry);
                    break;

                case SELECTED_MEGA:
                    whiteCardViews();
                    mTypeThanks = SELECTED_MEGA;
                    mCardMega.setBackground(getResources().getDrawable(R.drawable.circlemega));
                    mTextValue.setText("Thanks x100");
                    mTextValue.setTextColor(getResources().getColor(R.color.megaThanksCoin));
                    mThanksInvite = new ThanksInvite(mUser.getUserId(), mEmailOtherUser, System.currentTimeMillis(), "MEGA", mUserCountry);
                    break;

                case SELECTED_POWER:
                    whiteCardViews();
                    mTypeThanks = SELECTED_POWER;
                    mCardPower.setBackground(getResources().getDrawable(R.drawable.circlepower));
                    mTextValue.setText("Thanks x1000");
                    mTextValue.setTextColor(getResources().getColor(R.color.powerThanksCoin));
                    mThanksInvite = new ThanksInvite(mUser.getUserId(), mEmailOtherUser, System.currentTimeMillis(), "POWER", mUserCountry);
                    break;
            }
        }

        setupFirebase();
    }

    public void initViews() {
        mTextTitle = (TextView) findViewById(R.id.text_title_invite);
        mTextSubtitle = (TextView) findViewById(R.id.text_subtitle_invite);
        mTextAdditionalInfo = (TextView) findViewById(R.id.text_additional_info);
        mTextSendEmail = (TextView) findViewById(R.id.text_send_email);
        mTextValue = (TextView) findViewById(R.id.text_thanks_value);
        mButtonThanks = (ImageView) findViewById(R.id.button_thanks);
        mButtonSuperThanks = (ImageView) findViewById(R.id.button_super_thanks);
        mButtonMegaThanks = (ImageView) findViewById(R.id.button_mega_thanks);
        mButtonPowerThanks = (ImageView) findViewById(R.id.button_power_thanks);
        mButtonSend = (Button) findViewById(R.id.button_send);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mCardThanks = (CardView) findViewById(R.id.cardview_button_thanks);
        mCardSuper = (CardView) findViewById(R.id.cardview_button_super_thanks);
        mCardMega = (CardView) findViewById(R.id.cardview_button_mega_thanks);
        mCardPower = (CardView) findViewById(R.id.cardview_button_power_thanks);
        mInputDescription = (EditText) findViewById(R.id.input_thanks_description);

        whiteCardViews();
        mCardThanks.setBackground(getResources().getDrawable(R.drawable.circlethanks));
        mTextSubtitle.setText(getString(R.string.earn_on_invite));

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String description = mInputDescription.getText().toString();
                if (description.length() > 0) {
                    mThanksInvite.setDescription(description);
                }

                saveThanksInInvites(mThanksInvite);

                mButtonSend.setBackground(getResources().getDrawable(R.drawable.button_rounded_grey));
                mButtonSend.setClickable(false);
                mInputDescription.setEnabled(false);
                mButtonThanks.setClickable(false);
                mButtonThanks.setImageDrawable(getResources().getDrawable(R.drawable.thanksoff));
                mButtonSuperThanks.setClickable(false);
                mButtonSuperThanks.setImageDrawable(getResources().getDrawable(R.drawable.superthanksoff));
                mButtonMegaThanks.setClickable(false);
                mButtonMegaThanks.setImageDrawable(getResources().getDrawable(R.drawable.megathanksoff));
                mButtonPowerThanks.setClickable(false);
                mButtonPowerThanks.setImageDrawable(getResources().getDrawable(R.drawable.powerthanksoff));
                mButtonSend.setVisibility(View.GONE);

                sendMaildroid();
            }
        });

        if (mHasThanked) {
            mButtonSend.setClickable(false);
            mButtonSend.setBackground(getResources().getDrawable(R.drawable.button_rounded_grey));
        }
    }

    public void setupFirebase() {
        mFirestore = FirebaseFirestore.getInstance();
        mInvitesRef = mFirestore.collection(INVITE_REF);
    }

    public void initializeColors() {
        mDisabledColor = getResources().getColorStateList(R.color.disabled_button);
        mThanksColor = getResources().getColorStateList(R.color.colorPrimary);
        mSuperThanksColor = getResources().getColorStateList(R.color.amber);
        mMegaThanksColor = getResources().getColorStateList(R.color.blue);
        mPowerThanksColor = getResources().getColorStateList(R.color.indigo);
    }

    public void initializeDate() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat day = new SimpleDateFormat("dd");
        SimpleDateFormat month = new SimpleDateFormat("MMMM", Locale.US);
        SimpleDateFormat year = new SimpleDateFormat("YYYY");

        mDay = day.format(cal.getTime());
        mMonth = month.format(cal.getTime()).toLowerCase();
        mYear = year.format(cal.getTime());

        mDateCode = mMonth + "_" + mYear;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void powerUpButtons() {

        mFirestore.collection(THANKS_DATA).document(mUser.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            ThanksData thanksData = documentSnapshot.toObject(ThanksData.class);
                            long ourThanksCurrency = thanksData.getThanksCurrency();

                            if (!mHasThanked) {
                                mButtonThanks.setBackgroundTintList(mThanksColor);
                                mButtonThanks.setClickable(true);
                                mButtonThanks.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        whiteCardViews();
                                        mTypeThanks = SELECTED_THANKS;
                                        mCardThanks.setBackground(getResources().getDrawable(R.drawable.circlethanks));
                                        mTextValue.setText("Thanks x1");
                                        mTextValue.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        mThanksInvite = new ThanksInvite(mUser.getUserId(), mEmailOtherUser, System.currentTimeMillis(), "NORMAL", mUserCountry);

                                    }
                                });

                                if (ourThanksCurrency >= SUPER_THANKS_VALUE) {
                                    mButtonSuperThanks.setBackgroundTintList(mSuperThanksColor);
                                    mButtonSuperThanks.setClickable(true);
                                    mButtonSuperThanks.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            whiteCardViews();
                                            mTypeThanks = SELECTED_SUPER;
                                            mCardSuper.setBackground(getResources().getDrawable(R.drawable.circlesuper));
                                            mTextValue.setText("Thanks x10");
                                            mTextValue.setTextColor(getResources().getColor(R.color.superThanksCoin));
                                            mThanksInvite = new ThanksInvite(mUser.getUserId(), mEmailOtherUser, System.currentTimeMillis(), "SUPER", mUserCountry);

                                        }
                                    });
                                } else {
                                    mButtonSuperThanks.setEnabled(false);
                                    mButtonSuperThanks.setImageDrawable(getResources().getDrawable(R.drawable.superthanksoff));
                                }

                                if (ourThanksCurrency >= MEGA_THANKS_VALUE) {
                                    mButtonMegaThanks.setBackgroundTintList(mMegaThanksColor);
                                    mButtonMegaThanks.setClickable(true);
                                    mButtonMegaThanks.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            whiteCardViews();
                                            mTypeThanks = SELECTED_MEGA;
                                            mCardMega.setBackground(getResources().getDrawable(R.drawable.circlemega));
                                            mTextValue.setText("Thanks x100");
                                            mTextValue.setTextColor(getResources().getColor(R.color.megaThanksCoin));
                                            mThanksInvite = new ThanksInvite(mUser.getUserId(), mEmailOtherUser, System.currentTimeMillis(), "MEGA", mUserCountry);

                                            //I think it's good that user invites (to non yet registered users) do not cost Thanker Currency
                                            //mCurrencyReference.setValue(ourThanksCurrency - MEGA_THANKS_VALUE);
                                        }
                                    });
                                } else {
                                    mButtonMegaThanks.setEnabled(false);
                                    mButtonMegaThanks.setImageDrawable(getResources().getDrawable(R.drawable.megathanksoff));
                                }

                                if (ourThanksCurrency >= POWER_THANKS_VALUE) {
                                    mButtonPowerThanks.setBackgroundTintList(mPowerThanksColor);
                                    mButtonPowerThanks.setClickable(true);
                                    mButtonPowerThanks.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            whiteCardViews();
                                            mTypeThanks = SELECTED_POWER;
                                            mCardPower.setBackground(getResources().getDrawable(R.drawable.circlepower));
                                            mTextValue.setText("Thanks x1000");
                                            mTextValue.setTextColor(getResources().getColor(R.color.powerThanksCoin));
                                            mThanksInvite = new ThanksInvite(mUser.getUserId(), mEmailOtherUser, System.currentTimeMillis(), "POWER", mUserCountry);
                                        }
                                    });
                                } else {
                                    mButtonPowerThanks.setEnabled(false);
                                    mButtonPowerThanks.setImageDrawable(getResources().getDrawable(R.drawable.powerthanksoff));
                                }
                            } else {
                                greyAllButtons();
                            }
                        }

                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading our User\'s Thanks Data");
                    }
                });

        mProgressBar.setVisibility(View.GONE);
        Log.v("MainSnapshot", "Run all powerButtons method");
    }

    public void saveThanksInInvites(ThanksInvite thanks) {
        mInvitesRef.add(thanks);
        String email = mEmailOtherUser.replace(',', '.');
        Toast.makeText(getApplicationContext(), getString(R.string.thanks_for_inviting, email), Toast.LENGTH_LONG).show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);

    }

    public void greyAllThanks() {
        mCardThanks.setVisibility(View.GONE);
        mButtonSend.setVisibility(View.GONE);
        mTextValue.setVisibility(View.GONE);
        mInputDescription.setVisibility(View.GONE);
        mButtonThanks.setVisibility(View.GONE);
        mButtonSuperThanks.setVisibility(View.GONE);
        mButtonMegaThanks.setVisibility(View.GONE);
        mButtonPowerThanks.setVisibility(View.GONE);
        mCardThanks.setVisibility(View.GONE);
        mCardSuper.setVisibility(View.GONE);
        mCardMega.setVisibility(View.GONE);
        mCardPower.setVisibility(View.GONE);
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
    }

    private void sendMaildroid() {

        mTextSendEmail.setVisibility(View.VISIBLE);
        mTextSendEmail.setText(getString(R.string.sending_email) + ": " + getString(R.string.sending) + "...");

        String title = "";
        String body = "";
        String typeThanks = "";
        String typeThanksHtml = "";

        switch (mThanksInvite.getThanksType()) {
            case "NORMAL":
                typeThanks = getString(R.string.thanked_email);
                typeThanksHtml = getString(R.string.thanked_html);
                break;

            case "SUPER":
                typeThanks = getString(R.string.super_thanked_email);
                typeThanksHtml = getString(R.string.super_thanked_html);
                break;

            case "MEGA":
                typeThanks = getString(R.string.mega_thanked_email);
                typeThanksHtml = getString(R.string.mega_thanked_html);
                break;

            case "POWER":
                typeThanks = getString(R.string.power_thanked_email);
                typeThanksHtml = getString(R.string.power_thanked_html);
                break;

            default:
                break;
        }

        if (!typeThanks.equals("")) {

            String logo = "<img src=\"https://i.ibb.co/LCGHW7K/Thanker-HUG-Signature-Solid-Green-2x.png\" alt=\"Thanker-HUG-Signature-Solid-Green-2x\" width=200 height=200 border=\"0\">";
            title += getString(R.string.title_invite_thanker, DataUtils.capitalize(mUser.getName()), typeThanks);
            //body += getString(R.string.body_email_invite_thanker, typeThanks, DataUtils.capitalize(mUser.getName()));
            body += "<center><b><font size = 20>" + DataUtils.capitalize(mUser.getName()) + " " + typeThanksHtml + " " + getString(R.string.on) + "</font></b> " +
                    "<br>" + logo + "</center>" + getString(R.string.emailinvite);
            /*int index = body.indexOf('@');
            SpannableString bodySpan = new SpannableString(body);
            bodySpan.setSpan(new ImageSpan(getResources().getDrawable(R.drawable.thankinvite)), index, index+1, ImageSpan.ALIGN_BASELINE);*/

            new MaildroidX.Builder()
                    .smtp("smtpout.secureserver.net")//.smtp("smtp.gmail.com")
                    .smtpUsername("thankyou@thanker.co")
                    //.smtpPassword("thankerinviteshello2020!")
                    .smtpPassword("thisisthemakers2020!")
                    //.smtpAuthentication(true)
                    .port("465")
                    .type(MaildroidXType.HTML)
                    .to(mEmailOtherUser)
                    .from("ThankYou@thanker.co")//("thanker.invites@gmail.com")
                    .subject(title)
                    .body(body)
                    .mail();

            mTextSendEmail.setText(Html.fromHtml(getString(R.string.sending_email) + ": <b>" + getString(R.string.sent) + "</b>!"));
        } else {
            mTextSendEmail.setText(Html.fromHtml(getString(R.string.sending_email) + ": " + getString(R.string.could_not_send_email) + "."));
        }
    }

    public void whiteCardViews() {
        mCardThanks.setBackground(getResources().getDrawable(R.drawable.circlenone));
        mCardSuper.setBackground(getResources().getDrawable(R.drawable.circlenone));
        mCardMega.setBackground(getResources().getDrawable(R.drawable.circlenone));
        mCardPower.setBackground(getResources().getDrawable(R.drawable.circlenone));
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("selected-thanks", mTypeThanks);
        outState.putString("thanks-message", mMessageString);
        outState.putBoolean("has-thanked", mHasThanked);
    }

    @Override
    public void onStart() {
        super.onStart();

        Query inviteQuery = mInvitesRef.whereEqualTo("fromUserId", mUser.getUserId())
                            .whereEqualTo("toEmail", mEmailOtherUser)
                            .limit(1);

        inviteQuery.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() == 0){
                            powerUpButtons();
                        }

                        else {
                            greyAllThanks();
                            mInputDescription.setEnabled(false);
                            mHasThanked = true;
                            if (mEmailOtherUser != null) {
                                String email = mEmailOtherUser.replace(',', '.');
                                String alreadyInvited = getString(R.string.already_invited) + " <b>" + email + "</b>";
                                mTextAdditionalInfo.setText(Html.fromHtml(alreadyInvited));
                                mTextAdditionalInfo.setTextSize(16);
                                mTextTitle.setVisibility(View.GONE);
                            }
                        }

                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading invites, to check if we\'ve already invited email");

                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public static class PersonalStatsFragment extends Fragment {

        private final String LIST_THANKS_VALUES = "list-thanks-values";
        private final String USER_OBJECT = "user-object";

        private User mUser;
        private String mUserName;
        private List<ThanksValue> mListThanksValues;
        private ListView mListView;
        private PersonalStatsAdapter mAdapter;
        private TextView mTitleView;
        private ActionBar mActionBar;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.activity_personal_stats, container, false);

            mListView = view.findViewById(R.id.list_personal_stats);
            mTitleView = (TextView) view.findViewById(R.id.text_title);

            Bundle bundle = getArguments();

            if(bundle != null){
                mUser = (User) bundle.getSerializable(USER_OBJECT);
                mListThanksValues = (List<ThanksValue>) bundle.getSerializable(LIST_THANKS_VALUES);

                mUserName = DataUtils.capitalize(mUser.getName());
                mTitleView.setText(getActivity().getString(R.string.how_user_thanks, mUserName));

                if(getActivity() != null){
                    mAdapter = new PersonalStatsAdapter(getActivity(), 0, mListThanksValues, mUser);
                    mListView.setAdapter(mAdapter);
                }

            }

            return view;
        }

        @Override
        public void onResume(){
            super.onResume();
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            mActionBar = activity.getSupportActionBar();
            mActionBar.setTitle(null);
            mActionBar.setDisplayHomeAsUpEnabled(true);

            if(getActivity() != null){
                Utils.changeBarSubTitle(getActivity(), mActionBar, getActivity().getString(R.string.status_of, mUserName));
            }

            final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
            mActionBar.setHomeAsUpIndicator(upArrow);
        }

        @Override
        public void onPause(){
            super.onPause();
            mActionBar.setSubtitle(null);
            mActionBar.setDisplayHomeAsUpEnabled(false);
        }

    }
}
