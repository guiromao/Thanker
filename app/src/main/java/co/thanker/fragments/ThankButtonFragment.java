package co.thanker.fragments;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import co.thanker.R;
import co.thanker.adapters.ThanksGivingAdapter;
import co.thanker.utils.Utils;


public class ThankButtonFragment extends Fragment {

    private final String TAG = ThankButtonFragment.class.getSimpleName();
    private final String TYPE_NEAR = "type-near";
    private final String TYPE_FRIENDS = "type-friends";
    private final String TYPE_SEARCH = "type-search";
    private final String TYPE_FREE = "type-free";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String OUR_USER_ID = "our-user-id";
    private final String THANKER_ID_STRING = "thanker-id-string";
    private final String PURE_SEARCH_STRING = "pure-search-string";

    private Bundle mBundle;
    private String mCountry;
    private String mUserId;
    private LinearLayout mLinearNearby;
    private LinearLayout mLinearFriends;
    private LinearLayout mLinearSearch;
    private LinearLayout mLinearDiary;
    private TextView mTextQuote;
    private TextView mTextAuthor;
    private ListView mListView;
    private ThanksGivingAdapter mAdapter;
    private Fragment mFragment;

    public ThankButtonFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thank, container, false);

        mBundle = getArguments();
        //mListView = view.findViewById(R.id.list_thanks_types);
        List<String> thanksTypes = new ArrayList<>();
        mTextQuote = (TextView) view.findViewById(R.id.text_quote);
        mTextAuthor = (TextView) view.findViewById(R.id.text_quote_author);

        if(mTextQuote != null && mTextAuthor != null){
            if(getActivity() != null){
                String [] arrayQuotes = getActivity().getResources().getStringArray(R.array.gratitude_quotes);
                String [] arrayAuthors = getActivity().getResources().getStringArray(R.array.gratitude_quotes_authors);
                int number = new Random().nextInt(arrayQuotes.length);

                mTextQuote.setText("\"" + arrayQuotes[number] + "\"");
                mTextAuthor.setText(arrayAuthors[number]);
            }
        }

        if(mBundle != null){
            mCountry = mBundle.getString(OUR_USER_COUNTRY);
            mUserId = mBundle.getString(THANKER_ID_STRING);
            mBundle.putString(OUR_USER_ID, mUserId);
            Log.v(TAG, "Country in ThankButtonFragment: " + mCountry);
        }

        initButtons(view);

        return view;
    }

    public void initButtons(View v){
        mLinearNearby = (LinearLayout) v.findViewById(R.id.linear_nearby);
        mLinearFriends = (LinearLayout) v.findViewById(R.id.linear_friends);
        mLinearSearch = (LinearLayout) v.findViewById(R.id.relative_search);
        mLinearDiary = (LinearLayout) v.findViewById(R.id.linear_diary);

        mLinearNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){
                    mFragment = new FindFragment();
                    mFragment.setArguments(mBundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment).addToBackStack(null).commit();
                }
            }
        });

        mLinearFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){
                    mFragment = new FriendsFragment();
                    mFragment.setArguments(mBundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment).addToBackStack(null).commit();
                }
            }
        });

        mLinearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){
                    mFragment = new SearchFragment();
                    mFragment.setArguments(mBundle);
                    mBundle.putBoolean(PURE_SEARCH_STRING, true);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment).addToBackStack(null).commit();
                }
            }
        });

        if(mLinearDiary != null)
        {
            mLinearDiary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getActivity() != null){
                        mFragment = new DiaryFragment();
                        mFragment.setArguments(mBundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment).addToBackStack(null).commit();
                        //Toast.makeText(getActivity(), getActivity().getString(R.string.soon), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle(null);

        if(getActivity() != null){
            Utils.changeBarTitle(getActivity(), actionBar, getActivity().getString(R.string.thank));
        }

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

    }

}
