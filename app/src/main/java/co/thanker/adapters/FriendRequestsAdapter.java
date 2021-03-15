package co.thanker.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import co.thanker.R;
import co.thanker.data.FriendRequest;
import co.thanker.data.User;
import co.thanker.fragments.FriendsFragment;
import co.thanker.fragments.friends_fragments.FriendsRequestsFragment;
import utils.DataUtils;

public class FriendRequestsAdapter extends RecyclerView.Adapter {

    private final String TAG = "FriendRequestsAdapter";
    private static final String DB_REFERENCE = "users-test";
    private final String FRIENDS_DB = "friends-db";
    private final String FRIEND_INVITES = "friend-invites";

    private List<FriendRequest> mFriendRequests;
    private Context mContext;
    private String mUserId;
    //private SearchAdapterClickListener mListener;

    public FriendRequestsAdapter(Context c, List<FriendRequest> requests, String id/*, SearchAdapterClickListener listener*/){
        mContext = c;
        mFriendRequests = requests;
        mUserId = id;
        //mListener = listener;
    }

    /*public interface SearchAdapterClickListener {

        public void onSearchItemClick(String userId, String email);
    }*/

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.v("FriendRequests", "Doing something here");
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_request, parent, false);
        FriendRequestsViewHolder requestHolder = new FriendRequestsViewHolder(view/*, mListener*/);

        return requestHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FriendRequestsViewHolder requestViewHolder = (FriendRequestsViewHolder) holder;
        FriendRequest request = mFriendRequests.get(position);

        if(request != null && mFriendRequests.size() > 0){
            requestViewHolder.setDetails(mContext, request);
        }
    }

    @Override
    public int getItemCount() {

        if(mFriendRequests.size() == 0) {
            return 0;
        }

        return mFriendRequests.size();
    }

    public class FriendRequestsViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        TextView mUsername;
        TextView mPrimaryCategory;
        TextView mSecondaryCategory;
        ImageView mImage;
        Button mAcceptButton;
        Button mDeclineButton;

        public FriendRequestsViewHolder(@NonNull View itemView/*, SearchAdapterClickListener listener*/){
            super(itemView);
            mUsername = (TextView) itemView.findViewById(R.id.text_request_username);
            mPrimaryCategory = (TextView) itemView.findViewById(R.id.text_primary_category);
            mSecondaryCategory = (TextView) itemView.findViewById(R.id.text_secondary_category);
            mImage = (ImageView) itemView.findViewById(R.id.user_request_pic);
            mAcceptButton = (Button) itemView.findViewById(R.id.button_accept);
            mDeclineButton  = (Button) itemView.findViewById(R.id.button_decline);
            //mListener = listener;
            //itemView.setOnClickListener(this);

        }


        public void setDetails(final Context context, FriendRequest request){

            Log.v("FriendRequests", "Will read data for displaying request!");

            if(request != null) {
                final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(DB_REFERENCE).child(request.getUserId());

                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Log.v("FriendRequests", "Found data");
                            final User user = dataSnapshot.getValue(User.class);
                            //Query checkInviteQuery = FirebaseDatabase.getInstance().getReference().child(DB_REFERENCE)
                              //      .child(mUserId).child(FRIEND_INVITES).orderByValue().equalTo(user.getUserId());

                            //Friend Request has been seen
                            final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(DB_REFERENCE)
                                    .child(mUserId).child(FRIEND_INVITES).child(user.getUserId());

                            mUsername.setText(DataUtils.capitalize(user.getName()));
                            mPrimaryCategory.setText(user.getPrimaryCategory());

                            if(!user.getSecondaryCategory().equals("")){
                                String addSecCat = ", " + user.getSecondaryCategory();
                                mSecondaryCategory.setText(addSecCat);
                            }

                            Glide.with(context)
                                    .load(user.getImageUrl())
                                    .into(mImage);

                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        FriendRequest request = dataSnapshot.getValue(FriendRequest.class);
                                        if(!request.getSeen()){
                                            userRef.child("seen").setValue(true);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                            /*
                            Log.v(TAG, "FriendRequests. Just before updating \'seen\' value.");
                            checkInviteQuery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Log.v(TAG, "FriendRequests. Entered query value event listener.");
                                    if(dataSnapshot.hasChildren()){
                                        Log.v(TAG, "FriendRequests. Going to update Seen value.");
                                        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child(DB_REFERENCE)
                                                .child(mUserId).child(FRIEND_INVITES).child(user.getUserId()).child("seen");
                                        dataRef.setValue(true);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }); */

                            mAcceptButton.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View view){

                                    DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference().child(DB_REFERENCE)
                                            .child(mUserId).child(FRIENDS_DB);
                                    DatabaseReference otherUserFriendsRef = FirebaseDatabase.getInstance().getReference().child(DB_REFERENCE)
                                            .child(user.getUserId()).child(FRIENDS_DB);

                                    //write friends info on our page, and also ther other user's
                                    friendRef.push().setValue(user.getUserId());
                                    otherUserFriendsRef.push().setValue(mUserId);

                                    Query deleteQuery = FirebaseDatabase.getInstance().getReference().child(DB_REFERENCE).child(mUserId)
                                            .child(FRIEND_INVITES).orderByChild("userId").equalTo(user.getUserId());

                                    deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                for(DataSnapshot dataUser: dataSnapshot.getChildren()){
                                                    Log.v(TAG, "FriendsRequests. Found Value to delete after accepting invite!");
                                                    dataUser.getRef().removeValue();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });

                            mDeclineButton.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View view){
                                    Query deleteQuery = FirebaseDatabase.getInstance().getReference().child(DB_REFERENCE).child(mUserId)
                                            .child(FRIEND_INVITES).orderByChild("userId").equalTo(user.getUserId());

                                    deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                for(DataSnapshot dataUser: dataSnapshot.getChildren()){
                                                    dataUser.getRef().removeValue();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                }
        }

        }

       /* @Override
        public void onClick(View v) {
            if(getAdapterPosition() > -1) {
                int pos = getAdapterPosition();
                mListener.onSearchItemClick(mListProfiles.get(pos).getUserId(), mListProfiles.get(pos).getEmail());
            }

            /*String userId = mListProfiles.get(pos).getUserId();
            Bundle userInfoBundle = new Bundle();
            userInfoBundle.putString(USER_ID_STRING, userId);
            Fragment fragment = new MyProfileFragment();
            fragment.setArguments(userInfoBundle);
            FragmentManager fManager = ((MainActivity)mContext).getSupportFragmentManager();
            fManager.beginTransaction().replace(R.id.fragment_container, fragment).commit(); */
}

