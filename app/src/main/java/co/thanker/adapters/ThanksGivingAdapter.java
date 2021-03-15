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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

import co.thanker.R;
import co.thanker.fragments.FindFragment;
import co.thanker.fragments.FriendsFragment;
import co.thanker.fragments.SearchFragment;

public class ThanksGivingAdapter extends ArrayAdapter<String> {

    private final String TAG = ThanksGivingAdapter.class.getSimpleName();
    private final String TYPE_NEAR = "type-near";
    private final String TYPE_FRIENDS = "type-friends";
    private final String TYPE_SEARCH = "type-search";
    private final String TYPE_FREE = "type-free";
    private final String USER_ID_STRING = "user-id-string";
    private final String OUR_USER_ID = "our-user-id";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String THANKER_ID_STRING = "thanker-id-string";

    private Context mContext;
    private String mUserId;
    private String mCountry;
    private Bundle mBundle;
    private Fragment mFragment;

    public ThanksGivingAdapter(@NonNull Context context, int resource, List<String> listTypes, String id, String country) {
        super(context, resource, listTypes);
        mContext = context;
        mUserId = id;
        mCountry = country;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_thanks_giving_type, parent, false);
        }

        final String currentType = getItem(position);
        final View clickableView = listItemView;

        LinearLayout linear = listItemView.findViewById(R.id.linear_type);
        TextView textType = listItemView.findViewById(R.id.text_thanks_type);
        ImageView imageType = listItemView.findViewById(R.id.icon_thanks);
        mBundle = new Bundle();

        switch(currentType){
            case TYPE_NEAR:
                textType.setText(mContext.getString(R.string.type_near));
                imageType.setImageResource(R.drawable.land);
                linear.setBackground(mContext.getResources().getDrawable(R.drawable.button_rounded_accent));
                break;

            case TYPE_FRIENDS:
                textType.setText(mContext.getString(R.string.type_friends));
                imageType.setImageResource(R.drawable.people);
                linear.setBackground(mContext.getResources().getDrawable(R.drawable.button_super_thanks));
                break;

            case TYPE_SEARCH:
                textType.setText(mContext.getString(R.string.type_search));
                imageType.setImageResource(R.drawable.search);
                linear.setBackground(mContext.getResources().getDrawable(R.drawable.button_mega_thanks));
                break;

            case TYPE_FREE:
                textType.setText(mContext.getString(R.string.type_free));
                imageType.setImageResource(R.drawable.lifestyle);
                linear.setBackground(mContext.getResources().getDrawable(R.drawable.button_power_thanks));
                break;
        }

        clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBundle.putString(OUR_USER_ID, mUserId);
                mBundle.putString(THANKER_ID_STRING, mUserId);
                mBundle.putString(OUR_USER_COUNTRY, mCountry);

                Log.v(TAG, "Country in ThanksGivingAdapter: " + mCountry);

                switch(currentType){
                    case TYPE_NEAR: mFragment = new FindFragment(); break;
                    case TYPE_FRIENDS: mFragment = new FriendsFragment(); break;
                    case TYPE_SEARCH: mFragment = new SearchFragment(); break;
                    //case TYPE_FREE: mFragment = new FreeFragment(); break;
                }

                mFragment.setArguments(mBundle);

                if(mContext != null){
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment).addToBackStack(null).commit();
                }
            }
        });

        return listItemView;

    }

}
