package co.thanker.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import co.thanker.R;
import co.thanker.data.FriendRank;
import co.thanker.data.ThanksData;
import co.thanker.data.ThanksValue;
import co.thanker.data.User;
import co.thanker.fragments.OtherProfileFragment;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;

public class FriendTopAdapter extends ArrayAdapter<FriendRank> {

    private final String TAG = FriendTopAdapter.class.getSimpleName();
    private static final String DB_REFERENCE = "users";
    private final String THANKS_DATA = "thanks-data";
    private final String STATS_THANKS = "stats-thanks";
    private final String USER_ID_STRING = "user-id-string";
    private final String OUR_USER_ID = "our-user-id";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String OTHER_USER_OBJECT = "other-user-object";
    private final String THANKS_GIVEN = "thanks-given";
    private final String THANKS_RECEIVED = "thanks-received";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private Context mContext;
    private User mUser;
    private String mCountry;
    private View mView;
    private int mLayout;

    public FriendTopAdapter(@NonNull Context context, int resource, List<FriendRank> listFriends, String country) {
        super(context, resource, listFriends);
        mContext= context;
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCountry = country;

        paintIcons();
    }


    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        mView = convertView;
        if (mView == null) {
            mView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_top_friend, parent, false);
        }

        mLayout = 1;
        final View topView = mView;
        final View clickableView = mView;

        final FriendRank currentFriend = getItem(position);
        //final User user = currentUser.getUser();
        //final FriendRank friend = currentUser.getFriendRank();

        final ImageView picture = (ImageView) topView.findViewById(R.id.profile_picture);
        final ImageView catPicOne = (ImageView) topView.findViewById(R.id.image_first_category);
        final ImageView catPicTwo = (ImageView) topView.findViewById(R.id.image_second_category);
        final ImageView catPicThree = (ImageView) topView.findViewById(R.id.image_third_category);
        final ImageView imageFriend = (ImageView) topView.findViewById(R.id.image_friend_type);
        final TextView catTextOne = (TextView) topView.findViewById(R.id.text_first_category);
        final TextView catTextTwo = (TextView) topView.findViewById(R.id.text_second_category);
        final TextView catTextThree = (TextView) topView.findViewById(R.id.text_third_category);
        final TextView nameView = (TextView) topView.findViewById(R.id.text_username);
        final TextView catView = (TextView) topView.findViewById(R.id.text_categories);
        final TextView levelView = (TextView) topView.findViewById(R.id.text_thanker_level);
        final TextView givenView = (TextView) topView.findViewById(R.id.text_thanks_given);
        final TextView receivedView = (TextView) topView.findViewById(R.id.text_thanks_received);
        final TextView categoryViewOne = (TextView) topView.findViewById(R.id.text_category_1);
        final TextView categoryViewTwo = (TextView) topView.findViewById(R.id.text_category_2);
        final TextView categoryViewThree = (TextView) topView.findViewById(R.id.text_category_3);
        final TextView textFriend = (TextView) topView.findViewById(R.id.text_friend_type);

        final LinearLayout linearThanks = (LinearLayout) topView.findViewById(R.id.linear_thanks);
        final LinearLayout linearThanksSubOne = (LinearLayout) topView.findViewById(R.id.linear_thanks_1);
        final LinearLayout linearThanksSubTwo = (LinearLayout) topView.findViewById(R.id.linear_thanks_2);
        final LinearLayout linearThanksSubThree = (LinearLayout) topView.findViewById(R.id.linear_thanks_3);
        final LinearLayout linearRecentThanks = (LinearLayout) topView.findViewById(R.id.linear_recent_thanks);
        final TextView recentGivenOne = (TextView) topView.findViewById(R.id.text_thanks_given_1);
        final TextView recentGivenTwo = (TextView) topView.findViewById(R.id.text_thanks_given_2);
        final TextView recentReceivedOne = (TextView) topView.findViewById(R.id.text_thanks_received_1);
        final TextView recentReceivedTwo = (TextView) topView.findViewById(R.id.text_thanks_received_2);
        final FrameLayout imageInfo = (FrameLayout) topView.findViewById(R.id.image_info);

        mFirestore.collection(DB_REFERENCE).document(currentFriend.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            mUser = documentSnapshot.toObject(User.class);
                            User thisUser = mUser;

                            defineFriend(imageFriend, textFriend, thisUser.getThanksGivenTopTo(mAuth.getCurrentUser().getUid()));

                            List<ThanksValue> userThanks = new ArrayList<>();

                            userThanks.add(new ThanksValue("personThanks", thisUser.getPersonThanks()));
                            userThanks.add(new ThanksValue("brandThanks", thisUser.getBrandThanks()));
                            userThanks.add(new ThanksValue("businessThanks", thisUser.getBusinessThanks()));
                            userThanks.add(new ThanksValue("healthThanks", thisUser.getHealthThanks()));
                            userThanks.add(new ThanksValue("foodThanks", thisUser.getFoodThanks()));
                            userThanks.add(new ThanksValue("associationThanks", thisUser.getAssociationThanks()));
                            userThanks.add(new ThanksValue("homeThanks", thisUser.getHomeThanks()));
                            userThanks.add(new ThanksValue("scienceThanks", thisUser.getScienceThanks()));
                            userThanks.add(new ThanksValue("religionThanks", thisUser.getReligionThanks()));
                            userThanks.add(new ThanksValue("sportsThanks", thisUser.getSportsThanks()));
                            userThanks.add(new ThanksValue("lifestyleThanks", thisUser.getLifestyleThanks()));
                            userThanks.add(new ThanksValue("techThanks", thisUser.getTechThanks()));
                            userThanks.add(new ThanksValue("fashionThanks", thisUser.getFashionThanks()));
                            userThanks.add(new ThanksValue("educationThanks", thisUser.getEducationThanks()));
                            userThanks.add(new ThanksValue("gamesThanks", thisUser.getGamesThanks()));
                            userThanks.add(new ThanksValue("govThanks", thisUser.getGovThanks()));
                            userThanks.add(new ThanksValue("beautyThanks", thisUser.getBeautyThanks()));
                            userThanks.add(new ThanksValue("financeThanks", thisUser.getFinanceThanks()));
                            userThanks.add(new ThanksValue("cultureThanks", thisUser.getCultureThanks()));
                            userThanks.add(new ThanksValue("natureThanks", thisUser.getNatureThanks()));
                            userThanks.add(new ThanksValue("travelThanks", thisUser.getTravelThanks()));

                            DataUtils.sortThanksValues(userThanks);

                            List<ImageView> categoryImageList = new ArrayList<>();
                            List<TextView> categoryTextList = new ArrayList<>();
                            List<TextView> categoryTextThanked = new ArrayList<>();
                            final List<TextView> recentGivenList = new ArrayList<>();
                            final List<TextView> recentReceivedList = new ArrayList<>();

                            categoryImageList.add(catPicOne);
                            categoryImageList.add(catPicTwo);
                            categoryImageList.add(catPicThree);

                            categoryTextList.add(catTextOne);
                            categoryTextList.add(catTextTwo);
                            categoryTextList.add(catTextThree);

                            categoryTextThanked.add(categoryViewOne);
                            categoryTextThanked.add(categoryViewTwo);
                            categoryTextThanked.add(categoryViewThree);

                            List<LinearLayout> listLinears = new ArrayList<>();
                            listLinears.add(linearThanksSubOne);
                            listLinears.add(linearThanksSubTwo);
                            listLinears.add(linearThanksSubThree);

                            int maxIndex = userThanks.size() > 3 ? 3 : userThanks.size();

                            if(maxIndex > 0){
                                linearThanks.setVisibility(View.VISIBLE);
                            }

                            for(int i = 0; i < maxIndex; i++){
                                String category = DataUtils.thanksCategoryToStringCategory(userThanks.get(i).getKey());
                                categoryImageList.get(i).setImageDrawable(ImageUtils.getIconImageGrey(mContext, category));
                                categoryTextList.get(i).setText(String.format("%,d", userThanks.get(i).getValue()));
                                categoryTextThanked.get(i).setText(DataUtils.translateAndFormat(mContext, category));

                                Log.v(TAG, "Category name: " + DataUtils.translateAndFormat(mContext, category));

                                listLinears.get(i).setVisibility(View.VISIBLE);
                            }

                            recentGivenList.add(recentGivenOne);
                            recentGivenList.add(recentGivenTwo);
                            recentReceivedList.add(recentReceivedOne);
                            recentReceivedList.add(recentReceivedTwo);

                            ImageUtils.loadImageInto(mContext, thisUser.getImageUrl(), picture);
                            nameView.setText(DataUtils.capitalize(thisUser.getName()));
                            levelView.setText(DataUtils.returnLevel(mContext, thisUser.getThankerLevel()));


                            String categories = DataUtils.translateAndFormat(mContext, thisUser.getPrimaryCategory());
                            if(mUser.getSecondaryCategory() != null){
                                if(!mUser.getSecondaryCategory().equals("")){
                                    categories += " | " + DataUtils.translateAndFormat(mContext, thisUser.getSecondaryCategory());
                                }
                            }

                            catView.setText(categories);

                            mFirestore.collection(THANKS_DATA).document(thisUser.getUserId())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if(documentSnapshot.exists()){
                                                ThanksData data = documentSnapshot.toObject(ThanksData.class);

                                                givenView.setText(mContext.getString(R.string.given_to, data.getGivenThanksValue())); //Commented to run
                                                receivedView.setText(mContext.getString(R.string.received_by, data.getReceivedThanksValue())); //Commented to run

                                            }

                                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading from Current Friend\'s Thanks Data");
                                        }
                                    });

                            clickableView.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View view){

                                    Bundle userInfoBundle = new Bundle();
                                    userInfoBundle.putSerializable(OTHER_USER_OBJECT, thisUser);
                                    //userInfoBundle.putString(USER_ID_STRING, currentFriend.getUserId());
                                    userInfoBundle.putString(OUR_USER_ID, mAuth.getCurrentUser().getUid());
                                    userInfoBundle.putString(OUR_USER_COUNTRY, mCountry);

                                    Fragment fragment = new OtherProfileFragment();

                                    fragment.setArguments(userInfoBundle);
                                    //FragmentManager fManager = ((MainActivity)mContext).getSupportFragmentManager();
                                    if(mContext != null){
                                        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                                    }

                                }

                            });
                        }

                        else {
                            remove(currentFriend);
                        }

                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading from Current Friend\'s User Data");
                    }

                });

        imageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setCancelable(true);
                builder.setTitle(mContext.getString(R.string.what_are_top_stats));
                builder.setMessage(mContext.getString(R.string.this_is_top_stats));
                builder.setPositiveButton(mContext.getString(R.string.got_it),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return topView;
    }

    public void defineFriend(ImageView image, TextView text, long thanksReceived){
        int color = mContext.getResources().getColor(R.color.colorPrimary);
        String friendType = mContext.getString(R.string.friend);

        if (thanksReceived >= 0 && thanksReceived < 10) {
            color = mContext.getResources().getColor(R.color.colorPrimary);
            friendType = mContext.getString(R.string.friend);
        } else if (thanksReceived >= 10 && thanksReceived < 100) {
            color = mContext.getResources().getColor(R.color.superThanksCoin);
            friendType = mContext.getString(R.string.super_friend);
        } else if (thanksReceived >= 100 && thanksReceived < 1000) {
            color = mContext.getResources().getColor(R.color.megaThanksCoin);
            friendType = mContext.getString(R.string.mega_friend);
        } else if (thanksReceived >= 1000) {
            color = mContext.getResources().getColor(R.color.powerThanksCoin);
            friendType = mContext.getString(R.string.power_friend);
        } else if (thanksReceived >= 10000) {
            color = mContext.getResources().getColor(R.color.ultraThanksCoin);
            friendType = mContext.getString(R.string.ultra_friend);
        } else {
            color = mContext.getResources().getColor(R.color.colorPrimary);
        }

        text.setTextColor(color);
        text.setText(friendType);
        Drawable typeDraw = image.getDrawable();
        typeDraw.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

    }

    public String getCountry(){
        return mCountry;
    }

    private void paintIcons(){
        TypedArray arrayIcons = mContext.getResources().obtainTypedArray(R.array.category_images);

        for(int i = 0; i != arrayIcons.length(); i++){
            Drawable iconDraw = arrayIcons.getDrawable(i);
            iconDraw.setColorFilter(mContext.getResources().getColor(R.color.defaultTextColor2), PorterDuff.Mode.MULTIPLY);
        }
    }


}
