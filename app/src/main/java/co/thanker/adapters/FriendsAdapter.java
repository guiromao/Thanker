package co.thanker.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import co.thanker.R;
import co.thanker.data.User;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;

public class FriendsAdapter extends RecyclerView.Adapter {

    private final String TAG = "FriendRequestsAdapter";
    private static final String THANKS_REFERENCE = "thanks-test";
    private final String THANKS_GIVEN = "thanks-given";
    private final String THANKS_RECEIVED = "thanks-received";
    private static final String DB_REFERENCE = "users";
    private final String FRIENDS_DB = "friends-db";
    private final String FRIEND_INVITES = "friend-invites";
    private final String USER_ID_STRING = "user-id-string";
    private final String TOP_USERS_THANKS_RECEIVED = "top-users-thanks-received";
    private final String TOP_USERS_THANKS_GIVEN = "top-users-thanks-given";

    private List<String> mFriendsIds;
    private Context mContext;
    private String mUserId;
    private FriendsAdapterClickListener mListener;

    public FriendsAdapter(Context c, List<String> friendsIds, String id, FriendsAdapterClickListener listener){
        mContext = c;
        mFriendsIds = friendsIds;
        mUserId = id;
        mListener = listener;
    }

    public FriendsAdapter(Context c, List<String> friendsIds, String id){
        mContext = c;
        mFriendsIds = friendsIds;
        mUserId = id;
        mListener = null;
    }

    public interface FriendsAdapterClickListener {

        public void onFriendItemClick(String userId);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.v("FriendRequests", "Doing something here");
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_friend, parent, false);
        FriendsViewHolder friendHolder = new FriendsViewHolder(view, mListener);

        return friendHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FriendsViewHolder friendViewHolder = (FriendsViewHolder) holder;
        String friendId = mFriendsIds.get(position);

        friendViewHolder.setDetails(mContext, friendId);
    }

    public void setFriendsClickListener(FriendsAdapterClickListener friendClickListener){
        mListener = friendClickListener;
    }

    @Override
    public int getItemCount() {
        if(mFriendsIds.size() == 0){
            return 0;
        }

        return mFriendsIds.size();
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mUsername;
        TextView mPrimaryCategory;
        TextView mSecondaryCategory;
        TextView mThanksReceived;
        TextView mThanksGiven;
        TextView mLevel;
        ImageView mImage;

        public FriendsViewHolder(@NonNull View itemView, FriendsAdapterClickListener listener){
            super(itemView);
            mUsername = (TextView) itemView.findViewById(R.id.text_friend_username);
            mPrimaryCategory = (TextView) itemView.findViewById(R.id.text_primary_category);
            mSecondaryCategory = (TextView) itemView.findViewById(R.id.text_secondary_category);
            mImage = (ImageView) itemView.findViewById(R.id.friend_pic);
            mThanksReceived = (TextView) itemView.findViewById(R.id.text_thanks_received);
            mThanksGiven  = (TextView) itemView.findViewById(R.id.text_thanks_given);
            mLevel = (TextView) itemView.findViewById(R.id.text_level);
            mListener = listener;
            itemView.setOnClickListener(this);
        }


        public void setDetails(final Context context, String otherUserId){

            Log.v("Friends", "Will read data for displaying userId!");

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(DB_REFERENCE).child(otherUserId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Log.v("Friends", "Found data");
                        final User user = dataSnapshot.getValue(User.class);

                        mUsername.setText(DataUtils.capitalize(user.getName()));
                        mPrimaryCategory.setText(user.getPrimaryCategory());

                        if(user.getThankerLevel() != null){
                            mLevel.setText(DataUtils.capitalize(user.getThankerLevel()));
                        }

                        if(!user.getSecondaryCategory().equals("")){
                            String addSecCat = ", " + user.getSecondaryCategory();
                            mSecondaryCategory.setText(addSecCat);
                        }

                        ImageUtils.loadImageInto(mContext, user.getImageUrl(), mImage);
                        /*
                        Glide.with(context)
                                .load(user.getImageUrl())
                                .into(mImage);*/

                        /*String fromUsToOtherUser = mUserId + "_" + user.getUserId();
                        String fromOtherUserToUs = user.getUserId() + "_" + mUserId;*/

                        final Query givenThanksToOtherUserQuery = FirebaseDatabase.getInstance().getReference().child(DB_REFERENCE).child(mUserId).child(TOP_USERS_THANKS_GIVEN)
                                .child(user.getUserId());

                        final Query receivedThanksByOtherUserQuery = FirebaseDatabase.getInstance().getReference().child(DB_REFERENCE).child(mUserId).child(TOP_USERS_THANKS_RECEIVED)
                                .child(user.getUserId());

                        givenThanksToOtherUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long thanksValues = 0;
                                if(dataSnapshot.exists()){
                                    thanksValues = dataSnapshot.getValue(Long.class);
                                }

                                String givenThanks = mContext.getString(R.string.given) + " " + thanksValues + " " +
                                        mContext.getString(R.string.thanks);
                                mThanksGiven.setText(givenThanks);
                                givenThanksToOtherUserQuery.removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        receivedThanksByOtherUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long thanksValues = 0;
                                if(dataSnapshot.exists()){
                                    thanksValues = dataSnapshot.getValue(Long.class);
                                }

                                String receivedThanks = mContext.getString(R.string.received) + " " + thanksValues + " " +
                                            mContext.getString(R.string.thanks);
                                mThanksReceived.setText(receivedThanks);
                                receivedThanksByOtherUserQuery.removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @Override
        public void onClick(View v) {
            if(getAdapterPosition() > -1) {
                int pos = getAdapterPosition();
                if(mListener != null){
                    mListener.onFriendItemClick(mFriendsIds.get(pos));
                }
            }

            /*
            String userId = mFriendsIds.get(pos);
            Bundle userInfoBundle = new Bundle();
            userInfoBundle.putString(USER_ID_STRING, userId);
            Fragment fragment = new MyProfileFragment();
            fragment.setArguments(userInfoBundle);
            FragmentManager fManager = ((MainActivity)mContext).getSupportFragmentManager();
            fManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }*/
        }

    }

}