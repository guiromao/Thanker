package co.thanker.fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import co.thanker.R;

public class FindFragment extends Fragment {

    private final String TAG = "FindFragment";
    private final String OUR_USER_ID = "our-user-id";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String USER_ID_STRING = "user-id-string";
    private final String THANKER_ID_STRING = "thanker-id-string";
    private final String USER_COUNTRY = "user-country";
    private final String CONTINUE_SENDING_ID = "continue-sending-user-id";
    private final String ACTIVATED_THANKS = "activated-thanks";

    private Message mSendingMessage;
    private MessageListener mMessageListener;

    private FirebaseAuth mAuth;

    private String mCountry;
    private String mUserId;
    private boolean mActivatedThanks;
    private ImageView mGifFind;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_find, container, false);

        Log.v(TAG, "Entering FindFragment");

        mAuth = FirebaseAuth.getInstance();
        mActivatedThanks = true;
        mGifFind = (ImageView) view.findViewById(R.id.gif_find);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        if(getArguments() != null){
            Log.v(TAG, "Finding passing Country. Thanks Fragment. GetArguments() exists");
            mUserId = getArguments().getString(THANKER_ID_STRING);
            mCountry = getArguments().getString(OUR_USER_COUNTRY);
        }

        else {
            Log.v(TAG, "Finding passing Country. Thanks Fragment. GetArguments() does not exist");
            mUserId = mAuth.getCurrentUser().getUid();
        }

        Log.v(TAG, "Nearby. Thanks got in Thanks Fragment: " + mCountry + ". User ID: " + mUserId);

        /*
        if(getActivity() != null) {
            mCountry = getActivity().getApplicationContext().getResources().getConfiguration().locale.getDisplayCountry();
        }*/

        if(getActivity() != null){
            Glide.with(getActivity()).load(R.drawable.nearby_search).into(mGifFind);
            mProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN );
        }

        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.d(TAG, "Nearby. Found message: " + new String(message.getContent()));

                final String otherUserId = new String(message.getContent()).trim();

                if(!otherUserId.equalsIgnoreCase(mAuth.getCurrentUser().getUid())){
                    Fragment otherUserProfileFragment = new OtherProfileFragment();
                    Bundle userInfoBundle = new Bundle();
                    userInfoBundle.putString(USER_ID_STRING, otherUserId);
                    userInfoBundle.putString(OUR_USER_ID, mUserId);
                    userInfoBundle.putString(OUR_USER_COUNTRY, mCountry);
                    userInfoBundle.putBoolean(ACTIVATED_THANKS, mActivatedThanks);
                    userInfoBundle.putBoolean(CONTINUE_SENDING_ID, true);

                    otherUserProfileFragment.setArguments(userInfoBundle);

                    if(getActivity() != null){
                        mProgressBar.setVisibility(View.GONE);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, otherUserProfileFragment).addToBackStack(null).commit();
                    }
                }

             }

            @Override
            public void onLost(Message message) {
                Log.d(TAG, "Nearby. Lost sight of message: " + new String(message.getContent()));
            }
        };
        
        byte [] userIdInBytes = mUserId.getBytes();
        mSendingMessage = new Message(userIdInBytes);
        Log.v(TAG, "Nearby. Going to Send Message: " + new String(mSendingMessage.getContent()));

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();

        if(getActivity() != null){

            Nearby.getMessagesClient(getActivity()).publish(mSendingMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.v(TAG, "Nearby. Publishing key");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.v(TAG, "Nearby. Couldn\'t publish key. Error: " + e.toString());
                }
            });

            Nearby.getMessagesClient(getActivity()).subscribe(mMessageListener).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.v(TAG, "Nearby. Subscribing incoming message");
                }
            });

        }
    }

    @Override
    public void onStop(){

        if(getActivity() != null){
            Nearby.getMessagesClient(getActivity()).unpublish(mSendingMessage);
            Nearby.getMessagesClient(getActivity()).unsubscribe(mMessageListener);
            Log.v(TAG, "Nearby. Unpublished keys");
        }

        super.onStop();
    }

    /*
    @Override
    public void onPause(){
        super.onPause();

        Fragment f = (Fragment) getFragmentManager()
                .findFragmentById(R.id.fragment_container);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
    }*/

}
