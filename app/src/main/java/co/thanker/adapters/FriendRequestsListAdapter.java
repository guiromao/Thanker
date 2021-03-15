package co.thanker.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import co.thanker.R;
import co.thanker.data.FriendRank;
import co.thanker.data.FriendRequest;
import co.thanker.data.ThanksData;
import co.thanker.data.User;
import co.thanker.data.UserSnippet;
import co.thanker.fragments.MyProfileFragment;
import co.thanker.fragments.OtherProfileFragment;
import co.thanker.fragments.friends_fragments.FriendsRequestsFragment;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;

public class FriendRequestsListAdapter extends ArrayAdapter<FriendRequest> {

    private final String TAG = "FriendRequestsAdapter";
    private static final String DB_REFERENCE = "users";
    private final String TOP_USERS_THANKS_RECEIVED = "top-users-thanks-received";
    private final String TOP_USERS_THANKS_GIVEN = "top-users-thanks-given";
    private final String THANKS_DATA = "thanks-data";
    private final String FRIENDS_DB = "friends-db";
    private final String USER_SNIPPET = "user-snippet";
    private final String TOP_REF = "tops";
    private final String FRIEND_REQUESTS = "friend-requests";
    private final String OUR_USER_ID = "our-user-id";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String USER_ID_STRING = "user-id-string";

    private List<FriendRequest> mFriendRequests;
    private FirebaseAuth mAuth;
    private Context mContext;
    private FirebaseFirestore mDatabase;
    private String mUserId;
    private User mUser;
    private View mView;
    private ThanksData mUserData;
    private String mCountry;
    EventListener mListener;

    //private SearchAdapterClickListener mListener;

    public FriendRequestsListAdapter(Context c, int resource, List<FriendRequest> requests, String id, User user, ThanksData userData, String country, EventListener listener/*, SearchAdapterClickListener listener*/) {
        super(c, resource, requests);
        mContext = c;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();
        mFriendRequests = requests;
        mUserId = id;
        mUser = user;
        mUserData = userData;
        mCountry = country;
        mListener = listener;
        //mListener = listener;
    }

    public interface EventListener {
        void onEvent(int data);
    }

    public void removeItem(FriendRequest request) {
        remove(request);
        notifyDataSetChanged();

        if(getCount() == 0){
            mListener.onEvent(0);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        mView = convertView;
        if (mView == null) {
            mView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_friend_request, parent, false);
        }

        final View clickAbleView = mView;

        final FriendRequest currentRequest = getItem(position);

        TextView textUser = mView.findViewById(R.id.text_request_username);
        TextView textPrimCat = mView.findViewById(R.id.text_primary_category);
        TextView textSecCat = mView.findViewById(R.id.text_secondary_category);
        ImageView image = mView.findViewById(R.id.user_request_pic);
        Button accept = mView.findViewById(R.id.button_accept);
        ImageView decline = mView.findViewById(R.id.button_decline);

        decline.setColorFilter(ContextCompat.getColor(mContext, R.color.grey));

        setDetails(mContext, mView, currentRequest, textUser, textPrimCat, textSecCat, image, accept, decline);

        clickAbleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle userInfoBundle = new Bundle();
                userInfoBundle.putSerializable(USER_ID_STRING, currentRequest.getUserId());
                userInfoBundle.putString(OUR_USER_ID, mAuth.getCurrentUser().getUid());
                userInfoBundle.putString(OUR_USER_COUNTRY, mCountry);

                Fragment fragment = new OtherProfileFragment();

                fragment.setArguments(userInfoBundle);
                if(mContext != null){
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                }
            }
        });

        return mView;
    }

    public void setDetails(final Context context, final View listItemView, final FriendRequest request, final TextView textUsername, final TextView textPrimaryCategory,
                           final TextView textSecondaryCategory, final ImageView imageView,
                           final Button acceptButton, final ImageView declineButton) {

        Log.v("FriendRequests", "Will read data for displaying request!");

        if (request != null) {
            mDatabase.collection(USER_SNIPPET).document(request.getUserId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                UserSnippet user = documentSnapshot.toObject(UserSnippet.class);

                                textUsername.setText(DataUtils.capitalize(user.getName()));
                                textPrimaryCategory.setText(DataUtils.translateAndFormat(mContext, user.getPrimaryCategory()));
                                String secCategory = user.getSecondaryCategory();

                                if (!secCategory.equals("")) {
                                    String addSecCat = " | " + DataUtils.translateAndFormat(mContext, secCategory);
                                    textSecondaryCategory.setText(addSecCat);
                                }

                                ImageUtils.loadImageInto(context, user.getImageUrl(), imageView);
                            }

                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading from User\'s UserSnippet");
                        }
                    });


            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final String friendId = request.getUserId();
                    mDatabase.collection(THANKS_DATA).document(friendId)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        final ThanksData data = documentSnapshot.toObject(ThanksData.class);
                                        mDatabase.collection(DB_REFERENCE).document(friendId)
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if (documentSnapshot.exists()) {
                                                            User friendUser = documentSnapshot.toObject(User.class);
                                                            FriendRank usFriend = new FriendRank(mUser.getUserId(), mUser.getName(), friendUser.getThanksGivenTopTo(mUser.getUserId()), friendUser.getThanksFromTop(mUser.getUserId()));
                                                            List<FriendRank> currentFriends = friendUser.getFriends();
                                                            currentFriends.add(usFriend);
                                                            mDatabase.collection(DB_REFERENCE).document(friendId).update("friends", currentFriends);

                                                            FriendRank friendOther = new FriendRank(friendId, friendUser.getName(), mUser.getThanksGivenTopTo(friendId), mUser.getThanksFromTop(friendId));
                                                            List<FriendRank> ourFriends = mUser.getFriends();
                                                            ourFriends.add(friendOther);
                                                            mUser.setFriends(ourFriends);
                                                            mDatabase.collection(DB_REFERENCE).document(mUser.getUserId()).update("friends", ourFriends);

                                                            //Then remove from Requests
                                                            mUser.removeRequest(request);
                                                            List<FriendRequest> newRequests = mUser.getFriendRequests();
                                                            mDatabase.collection(DB_REFERENCE).document(mUser.getUserId()).update("friendRequests", newRequests);
                                                            removeItem(request);
                                                        }

                                                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading from Friend\'s ThanksData");
                                                    }
                                                });

                                    }
                                }
                            });

                }
            });

            declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mUser.removeRequest(request);
                    List<FriendRequest> newRequests = mUser.getFriendRequests();
                    mDatabase.collection(DB_REFERENCE).document(mUser.getUserId()).update("friendRequests", newRequests);
                    removeItem(request);
                }
            });

        }
    }

    public void visit(Activity activity, String otherId) {
        Fragment profileFrag;

        if (!mAuth.getCurrentUser().getUid().equalsIgnoreCase(otherId)) {
            profileFrag = new OtherProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putString(OUR_USER_ID, mAuth.getCurrentUser().getUid());
            bundle.putString(OUR_USER_COUNTRY, mCountry);
            bundle.putString(USER_ID_STRING, otherId);
            profileFrag.setArguments(bundle);
        } else {
            profileFrag = new MyProfileFragment();
        }

        if (activity != null) {
            ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFrag).addToBackStack(null).commit();
        }
    }

}

