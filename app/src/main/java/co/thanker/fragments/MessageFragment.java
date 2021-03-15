package co.thanker.fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import co.thanker.PremiumActivityNo;
import co.thanker.R;
import co.thanker.data.Message;
import co.thanker.data.UserSnippet;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;
import co.thanker.utils.Utils;

public class MessageFragment extends Fragment {

    private static final String TAG = "MessageFragment";
    private final String DB_REFERENCE = "users";
    private final String MESSAGE_OBJECT = "message-object";
    private final String DEFAULT_IMAGE = "https://firebasestorage.googleapis.com/v0/b/thanker-b301f.appspot.com/o/users-profile-pictures%2Fthankyou3.png?alt=media&token=edf26987-1b36-4a47-aaf0-a1d484681fee";
    private final String PLATFORM_MESSAGE = "platform-message";
    private final String USER_SNIPPET_VAR = "user-snippet-var";
    private final String USER_OBJECT = "user-object";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String OUR_USER_ID = "our-user-id";
    private final String USER_ID_STRING = "user-id-string";
    private final String LISTING_TYPE = "listing-type";
    private final String TOP_USERS_THANKS_RECEIVED = "top-users-thanks-received";
    private final String TOP_USERS_THANKS_GIVEN = "top-users-thanks-given";
    private final String FROM_USER_NAME = "from-user-name";
    private final String INFO_HAS_PREMIUM = "info-has-premium";

    private FirebaseAuth mAuth;
    private ActionBar mActionBar;
    private TextView mTextTitle;
    private TextView mTextBody;
    private TextView mTextDate;
    private TextView mTextCta;
    private Button mButtonCta;
    private LinearLayout mLinearCta;
    private ImageView mImageView;
    private Message mMessage;
    private UserSnippet mThisUser;
    private String mUserId;
    private String mOtherUserId;
    private String mCountry;
    private List<Message> mListMessage;
    private boolean misPremium;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_message, container, false);

        mAuth = FirebaseAuth.getInstance();

        mTextTitle = (TextView) view.findViewById(R.id.text_message_title);
        mTextBody = (TextView) view.findViewById(R.id.text_message_body);
        mTextDate = (TextView) view.findViewById(R.id.text_message_date);
        mTextCta = view.findViewById(R.id.text_cta);
        mButtonCta = view.findViewById(R.id.button_cta);
        mLinearCta = view.findViewById(R.id.linear_cta);
        mImageView = (ImageView) view.findViewById(R.id.friend_pic);

        Bundle bundle = getArguments();

        if (bundle != null && getActivity() != null) {
            mUserId = bundle.getString(OUR_USER_ID);
            mOtherUserId = bundle.getString(USER_ID_STRING);
            mCountry = bundle.getString(OUR_USER_COUNTRY);
            mThisUser = (UserSnippet) bundle.getSerializable(USER_SNIPPET_VAR);
            mMessage = (Message) bundle.getSerializable(MESSAGE_OBJECT);
            mListMessage = (List<Message>) bundle.getSerializable("messages");
            misPremium = bundle.getBoolean(INFO_HAS_PREMIUM, false);

            String message = mMessage.getText();
            message = message.replace("\n", "<br>");

            mTextTitle.setText(Html.fromHtml(mMessage.getTitle()));
            mTextBody.setText(Html.fromHtml(message));
            mTextDate.setText(DataUtils.getDateString(getActivity(), mMessage.getDate()));

            if (mMessage.getFromUserId() != null) {
                if (!mMessage.getFromUserId().equals("") && !mMessage.getFromUserId().equals(PLATFORM_MESSAGE)) {

                    if (mThisUser != null) {
                        ImageUtils.loadImageIntoRound(getActivity(), mThisUser.getImageUrl(), mImageView);
                        Log.v(TAG, "Message Fragment. Added image: " + mThisUser.getImageUrl());
                        mImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mCountry != null && !mOtherUserId.equalsIgnoreCase(PLATFORM_MESSAGE)) {
                                    if (!mCountry.equals("")) {
                                        Fragment userFragment;
                                        if (mOtherUserId.equalsIgnoreCase(mAuth.getCurrentUser().getUid())) {
                                            userFragment = new MyProfileFragment();
                                        } else {
                                            userFragment = new OtherProfileFragment();
                                            Bundle thisBundle = new Bundle();
                                            thisBundle.putString(OUR_USER_ID, mUserId);
                                            thisBundle.putString(USER_ID_STRING, mOtherUserId);
                                            thisBundle.putString(OUR_USER_COUNTRY, mCountry);
                                            userFragment.setArguments(bundle);
                                        }
                                        if (getActivity() != null) {
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, userFragment).addToBackStack(null).commit();
                                        }
                                    }
                                }
                            }
                        });
                    }

                } else {
                    ImageUtils.loadImageIntoRound(getActivity(), DEFAULT_IMAGE, mImageView);
                }
            }

            if (mMessage.getType() == 1 || mMessage.getType() == 2 || mMessage.getType() == 3) {

                if (!(mMessage.getType() == DataUtils.MSG_SEE_PREMIUM && misPremium)) {

                    mLinearCta.setVisibility(View.VISIBLE);
                    populateCta(mMessage.getType());
                    Fragment fragment = new ThankersListFragment();
                    Bundle fragBundle = new Bundle();

                    switch (mMessage.getType()) {
                        case DataUtils.MSG_SEE_MY_TOP:
                            //fragBundle.putSerializable(USER_OBJECT, mUser);
                            fragBundle.putString(USER_ID_STRING, mUserId);
                            fragBundle.putString(LISTING_TYPE, TOP_USERS_THANKS_RECEIVED);
                            fragBundle.putString(OUR_USER_COUNTRY, mCountry);
                            fragment.setArguments(fragBundle);
                            Log.v(TAG, "Thankers List. Referring to My Top in Message. User: " + mUserId);

                            break;

                        case DataUtils.MSG_SEE_OTHER_TOP:
                            bundle.putString(USER_ID_STRING, mOtherUserId);
                            bundle.putString(LISTING_TYPE, TOP_USERS_THANKS_RECEIVED);
                            bundle.putString(OUR_USER_COUNTRY, mCountry);
                            fragment.setArguments(bundle);
                            break;

                        case DataUtils.MSG_SEE_PREMIUM:
                            Intent premiumIntent = new Intent(getActivity(), PremiumActivityNo.class);
                            mButtonCta.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(premiumIntent);
                                }
                            });
                    }

                    if (getActivity() != null && (mMessage.getType() == DataUtils.MSG_SEE_MY_TOP || mMessage.getType() == DataUtils.MSG_SEE_OTHER_TOP)) {
                        mButtonCta.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.v(TAG, "Thankers List. Opening top for Type: " + mMessage.getType());
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                            }
                        });
                    }
                }
                else {
                    mLinearCta.setVisibility(View.GONE);
                }
            }
        }
        return view;
    }

    public void populateCta(int type) {
        if (getActivity() != null) {
            String textCta = "";
            String textButton = "";

            switch (type) {
                case DataUtils.MSG_SEE_MY_TOP:
                    textCta = getActivity().getString(R.string.see_my_top);
                    textButton = getActivity().getString(R.string.see_top);
                    break;

                case DataUtils.MSG_SEE_OTHER_TOP:
                    textCta = getActivity().getString(R.string.see_other_top);
                    textButton = getActivity().getString(R.string.see_top);
                    break;

                case DataUtils.MSG_SEE_PREMIUM:
                    textCta = getActivity().getString(R.string.see_premium);
                    textButton = getActivity().getString(R.string.check_premium);
                    break;
            }

            mTextCta.setText(textCta);
            mButtonCta.setText(textButton);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        mActionBar = activity.getSupportActionBar();
        mActionBar.setTitle(null);

        if (getActivity() != null) {
            /*if(mFromUsername != null && !mOtherUserId.equalsIgnoreCase(PLATFORM_MESSAGE)){
                Utils.changeBarSubTitle(getActivity(), mActionBar, getActivity().getString(R.string.message_from, mFromUsername));
            }
            else {*/
            Utils.changeBarSubTitle(getActivity(), mActionBar, getActivity().getString(R.string.thanker_messages));
            // }
        }

        mActionBar.setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        mActionBar.setHomeAsUpIndicator(upArrow);
    }

    @Override
    public void onPause() {
        super.onPause();
        mActionBar.setSubtitle(null);
        mActionBar.setDisplayHomeAsUpEnabled(false);
    }
}
