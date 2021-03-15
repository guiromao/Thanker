package co.thanker.adapters;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.blongho.country_data.World;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import co.thanker.R;
import co.thanker.data.IdData;
import co.thanker.data.UserSnippet;
import co.thanker.fragments.MyProfileFragment;
import co.thanker.fragments.OtherProfileFragment;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;

public class RankAdapterOverall extends ArrayAdapter<IdData> {

    private static final String TAG = "RankAdapter";
    private final String USERS_DATABASE = "users";
    private final String USER_SNIPPET = "user-snippet";
    private final String OUR_USER_ID = "our-user-id";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String USER_ID_STRING = "user-id-string";
    private final String OTHER_USER_OBJECT = "other-user-object";

    private Context mContext;
    private FirebaseFirestore mFirestore;
    private String mCountry;
    private String mUserId;

    public RankAdapterOverall(@NonNull Context context, int resource, List<IdData> listUsers, String country, String id) {
        super(context, resource, listUsers);

        mContext = context;
        mFirestore = FirebaseFirestore.getInstance();
        mCountry = country;
        mUserId = id;
        World.init(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_rank_overall, parent, false);
        }

        final IdData currentUser = getItem(position);

        final TextView textRank = (TextView) listItemView.findViewById(R.id.text_rank_number);
        final TextView textName  = (TextView) listItemView.findViewById(R.id.text_name);
        final TextView textThanksGiven = (TextView) listItemView.findViewById(R.id.text_thanks);
        final TextView textCategories = (TextView) listItemView.findViewById(R.id.text_categories);
        final ImageView picture = (ImageView) listItemView.findViewById(R.id.profile_pic);
        final LinearLayout linearInfo = (LinearLayout) listItemView.findViewById(R.id.linear_info);
        final ImageView flag = (ImageView) listItemView.findViewById(R.id.image_flag);
        final TextView textCountry = listItemView.findViewById(R.id.text_country);
        /*final TextView givenView = (TextView) listItemView.findViewById(R.id.text_given);
        final TextView receivedView = (TextView) listItemView.findViewById(R.id.text_received);*/

        textRank.setText(String.valueOf(position + 1) + "º");

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visit(currentUser.getId());
            }
        });

        textThanksGiven.setText(Html.fromHtml("<b>" + String.format("%,d", currentUser.getData().getGivenThanksValue()) + " " +
                mContext.getString(R.string.thanks) + "</b>"));

        mFirestore.collection(USER_SNIPPET).document(currentUser.getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            UserSnippet user = documentSnapshot.toObject(UserSnippet.class);
                            textName.setText(DataUtils.capitalize(user.getName()));
                            /*textThanksGiven.setText(Html.fromHtml("<b>" + String.format("%,d", user.getGivenThanksValue() + " " +
                                    mContext.getString(R.string.thanks) + "</b>")));*/
                            ImageUtils.loadImageIntoRound(mContext, user.getImageUrl(), picture);

                            String categories = DataUtils.translateAndFormat(mContext, user.getPrimaryCategory());

                            if(user.getSecondaryCategory() != null){
                                if(!user.getSecondaryCategory().equals("")){
                                    categories += " | " + DataUtils.translateAndFormat(mContext, user.getSecondaryCategory());
                                }
                            }

                            textCategories.setText(categories);

                            int countryFlag = World.getFlagOf(user.getLivingCountry());
                            flag.setImageResource(countryFlag);
                            textCountry.setText(DataUtils.getTranslatedCountry(mContext, user.getLivingCountry()));
                        }

                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading from Current User\'s UserSnippet");
                    }
                });

        return listItemView;
    }

    public void visit(String id){
        Fragment profileFrag;

        if(!mUserId.equalsIgnoreCase(id)){
            profileFrag = new OtherProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putString(OUR_USER_ID, mUserId);
            bundle.putString(OUR_USER_COUNTRY, mCountry);
            bundle.putSerializable(USER_ID_STRING, id);
            profileFrag.setArguments(bundle);
        }

        else {
            profileFrag = new MyProfileFragment();
        }

        if(mContext != null){
            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFrag).addToBackStack(null).commit();
        }
    }

}
