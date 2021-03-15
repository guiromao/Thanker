package co.thanker.adapters;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import co.thanker.MainActivity;
import co.thanker.R;
import co.thanker.data.Thanks;
import co.thanker.data.User;
import co.thanker.fragments.MyProfileFragment;
import utils.DataUtils;

public class ThanksAdapter extends RecyclerView.Adapter {

    private static final String TYPE_GIVER = "type-giver";
    private static final String TYPE_RECEIVER = "type-receiver";
    private static final String USERS_DATABASE = "users-test";
    private static final String ACTIVITY_TYPE = "activity-type";
    public static final String FRAGMENT_TYPE = "fragment-type";

    private List<Thanks> mListThanks;
    private Context mContext;
    private String mAdapterType;
    private String mActivityType;
    private ThanksAdapterClickListener mListener;

    public ThanksAdapter(Context c, List<Thanks> thanksList,/* ThanksAdapterClickListener listener,*/ String type, String activity){
        mContext = c;
        mListThanks = thanksList;
        //mListener = listener;
        mAdapterType = type;
        mActivityType = activity;
    }

    public void addAll(List<Thanks> list){
        mListThanks = list;
    }

    public interface ThanksAdapterClickListener {

        public void onThanksItemClick(String userId, String email);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_thanks, parent, false);
        ThanksViewHolder thanksHolder = new ThanksViewHolder(view);

        return thanksHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ThanksViewHolder thanksViewHolder = (ThanksViewHolder) holder;
        Thanks thanks = mListThanks.get(position);

        thanksViewHolder.setDetails(mContext, thanks);
    }

    @Override
    public int getItemCount() {

        if(mActivityType.equals(FRAGMENT_TYPE) && mListThanks.size() > 5){
            return 5;
        }

        else if(mActivityType.equals(FRAGMENT_TYPE) && mListThanks.size() < 5){
            return mListThanks.size();
        }

        if(mListThanks.size() == 0){
            return 0;
        }

        return mListThanks.size();
    }

    public class ThanksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView giverImage;
        ImageView receiverImage;
        CardView cardGiver;
        CardView cardReceiver;
        TextView descriptionView;
        TextView dateView;
        String mGiver;
        String mReceiver;
        String mResult;
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

        public ThanksViewHolder(@NonNull View itemView){
            super(itemView);
            giverImage = (ImageView) itemView.findViewById(R.id.thanks_giver_profile_picture);
            receiverImage = (ImageView) itemView.findViewById(R.id.thanks_receiver_profile_picture);
            cardGiver = (CardView) itemView.findViewById(R.id.cardview_thanks_item_giver);
            cardReceiver = (CardView) itemView.findViewById(R.id.cardview_thanks_item_receiver);
            descriptionView = (TextView) itemView.findViewById(R.id.thanks_item_text);
            dateView = (TextView) itemView.findViewById(R.id.thanks_item_date);
            //mListener = listener;
            itemView.setOnClickListener(this);
        }


        public void setDetails(Context context, Thanks thanks){

            retrieveThanksInfo(context, thanks);

            if (mAdapterType.equals(TYPE_GIVER)) {
                cardGiver.setVisibility(View.GONE);
            } else {
                cardReceiver.setVisibility(View.GONE);
            }

            setDateOnView(thanks.getDate());

        }

        public void setDateOnView(long dateLong){
            Date date = new Date(dateLong);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dateToPresent = sdf.format(date);
            dateView.setText(dateToPresent);
        }

        public void retrieveThanksInfo(final Context context, final Thanks thanks){
            final String giverId = thanks.getFromUserId();
            final String receiverId = thanks.getToUserId();
            final String typeThanks = retrieveThanksType(thanks);
            mResult = "";

            final DatabaseReference dataRef = mDatabase.getReference().child(USERS_DATABASE);

            if(giverId != null) {

                DatabaseReference dataRefGiver = dataRef.child(giverId);

                dataRefGiver.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);
                            mGiver = user.getName();
                            mResult += mGiver + " " + typeThanks;

                            Glide.with(context)
                                    .load(user.getImageUrl())
                                    .into(giverImage);

                            DatabaseReference newDataRef = dataRef.child(receiverId);
                            newDataRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {

                                        User user = dataSnapshot.getValue(User.class);

                                        mReceiver = user.getName();
                                        mResult += " " + mReceiver;

                                        if (thanks.getDescription() != null && !thanks.getDescription().equals("")) {
                                            mResult += ": " + thanks.getDescription();
                                        }

                                        if(thanks.getDescription().length() > 0) {
                                            if (!DataUtils.isPunctuation(thanks.getDescription().charAt(thanks.getDescription().length() - 1))) {
                                                mResult += ".";
                                            }
                                        }

                                        else {
                                            mResult += ".";
                                        }

                                        Glide.with(context)
                                                .load(user.getImageUrl())
                                                .into(receiverImage);

                                        descriptionView.setText(Html.fromHtml(mResult));


                                    }


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
        }

        private String retrieveThanksType(Thanks thanks){
            String type = "";
            if(thanks.getThanksType() != null) {
                if(!thanks.getThanksType().equals("")) {
                    switch (thanks.getThanksType()) {
                        case "NORMAL":
                            type = "<font color='#4CAF50'>thanked</font>";
                            break;
                        case "SUPER":
                            type = "<font color='#FFC107'>super thanked</font>";
                            break;
                        case "MEGA":
                            type = "<font color='#1976D2'>mega thanked</font>";
                            break;
                        case "POWER":
                            type = "<font color='#673AB7'>power thanked</font>";
                            break;
                    }
                }
            }
            return type;
        }


        @Override
        public void onClick(View v) {
            if(getAdapterPosition() > -1) {
                int pos = getAdapterPosition();
                //mListener.onThanksItemClick(mListThanks.get(pos).getFromUserId(), mListThanks.get(pos).getToUserId());
            }

            //String userId = mListProfiles.get(pos).getUserId();
            /*Bundle userInfoBundle = new Bundle();
            userInfoBundle.putString(USER_ID_STRING, userId);
            Fragment fragment = new MyProfileFragment();
            fragment.setArguments(userInfoBundle);
            FragmentManager fManager = ((MainActivity)mContext).getSupportFragmentManager();
            fManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();  */
        }
    }
}
