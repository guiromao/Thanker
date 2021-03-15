package co.thanker.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import co.thanker.R;
import co.thanker.data.User;
import co.thanker.data.UserSnippet;
import co.thanker.data.UserValue;
import co.thanker.fragments.MyProfileFragment;
import co.thanker.fragments.OtherProfileFragment;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;

public class ThankersProfilesAdapter extends ArrayAdapter<UserValue> {

    private static final String TAG = "ThankersProfilesAdapter";
    private final String USER_SNIPPET = "user-snippet";
    private final String OUR_USER_ID = "our-user-id";
    private final String USER_OBJECT = "user-object";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String USER_ID_STRING = "user-id-string";

    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private List<UserValue> mListUserValues;
    private User mThisUser;
    private String mCountry;
    private CardView mCard;
    private LinearLayout mLinearUser;

    public ThankersProfilesAdapter(@NonNull Context context, int resource, List<UserValue> listUserValues, User thisUser, String country) {
        super(context, resource, listUserValues);
        mContext = context;
        mListUserValues = listUserValues;
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mThisUser = thisUser;
        mCountry = country;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_thanker_profile, parent, false);
        }

        final UserValue currentUserValue = getItem(position);

        final ImageView image = (ImageView) listItemView.findViewById(R.id.profile_picture);
        final TextView textRank = (TextView) listItemView.findViewById(R.id.text_rank_position);
        final TextView textName = (TextView) listItemView.findViewById(R.id.text_user_name);
        final TextView textCats = (TextView) listItemView.findViewById(R.id.text_user_categories);
        final TextView textThanks = (TextView) listItemView.findViewById(R.id.text_number_thanks);
        //mCard = (CardView) listItemView.findViewById(R.id.cardview_picture);
        mLinearUser = (LinearLayout) listItemView.findViewById(R.id.linear_user);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCountry != null) {
                    visit(currentUserValue.getUserId());
                }
            }
        });

        mLinearUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCountry != null) {
                    visit(currentUserValue.getUserId());
                }
            }
        });

        textRank.setText(String.valueOf(findListPosition(currentUserValue.getUserId())));
        textThanks.setText(String.format("%,d", currentUserValue.getValueThanks()) + " Thanks");

        mFirestore.collection(USER_SNIPPET).document(currentUserValue.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            UserSnippet user = documentSnapshot.toObject(UserSnippet.class);
                            String name = DataUtils.capitalize(user.getName());
                            String imageUrl = user.getImageUrl();
                            String catsString = DataUtils.translateAndFormat(mContext, user.getPrimaryCategory());
                            String secCat = user.getSecondaryCategory();
                            if (secCat != null) {
                                if (!secCat.equals("")) {
                                    catsString += ", " + DataUtils.translateAndFormat(mContext, secCat);
                                }
                            }

                            textCats.setText(catsString);
                            ImageUtils.loadImageIntoRound(mContext, imageUrl, image);
                            textName.setText(name);
                        }

                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading from Current User\'s UserSnippet");
                    }
                });

        return listItemView;
    }

    public void visit(String otherId) {
        Fragment profileFrag;

        if (!mAuth.getCurrentUser().getUid().equalsIgnoreCase(otherId)) {
            profileFrag = new OtherProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(USER_OBJECT, mThisUser);
            bundle.putString(OUR_USER_COUNTRY, mCountry);
            bundle.putString(USER_ID_STRING, otherId);
            profileFrag.setArguments(bundle);
        } else {
            profileFrag = new MyProfileFragment();
        }

        if (mContext != null) {
            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFrag).addToBackStack(null).commit();
        }
    }

    private long findListPosition(String userId) {
        for (int i = 0; i != mListUserValues.size(); i++) {
            if (userId.equalsIgnoreCase(mListUserValues.get(i).getUserId())) {
                return (i + 1);
            }
        }

        return 0;
    }
}
