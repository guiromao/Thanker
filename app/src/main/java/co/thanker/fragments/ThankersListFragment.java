package co.thanker.fragments;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import co.thanker.R;
import co.thanker.adapters.ThankersProfilesAdapter;
import co.thanker.data.User;
import co.thanker.data.UserValue;
import co.thanker.utils.DataUtils;
import co.thanker.utils.Utils;

public class ThankersListFragment extends Fragment {

    private final String TAG = "ThankersListActivity";
    private static final String DB_REFERENCE = "users";
    private final String TOP_REF = "tops";
    private final String TOP_USERS_THANKS_RECEIVED = "top-users-thanks-received";
    private final String TOP_USERS_THANKS_GIVEN = "top-users-thanks-given";
    private final String USER_OBJECT = "user-object";
    private final String LISTING_TYPE = "listing-type";
    private final String USER_ID_STRING = "user-id-string";
    private final String OUR_USER_COUNTRY = "our-user-country";

    private final int NUMBER_RESULTS = 14;

    private FirebaseFirestore mFirestore;

    private ActionBar mActionBar;
    private ListView mListView;
    private ThankersProfilesAdapter mAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyGiving;
    private TextView mEmptyReceiving;

    private User mThisUser;
    private String mUserId;
    private String mType;
    private String mTitle;
    private String mCountry;

    private List<String> mListNodeKeys;
    private List<UserValue> mListThankers;
    private List<UserValue> mListThankersToPresent;

    private long mNumberThankers;
    private long mControl;
    private long mCurrentIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_thankers_list, container, false);

        initViews(view);
        mNumberThankers = 0;
        mListThankers = new ArrayList<>();

        Bundle bundle = getArguments();

        if(bundle != null && getActivity() != null){
            mThisUser = (User) bundle.getSerializable(USER_OBJECT);
            mType = bundle.getString(LISTING_TYPE);
            mUserId = bundle.getString(USER_ID_STRING);
            mCountry = bundle.getString(OUR_USER_COUNTRY);

            if(mType != null && mThisUser != null){
                if(mType.equals(TOP_USERS_THANKS_GIVEN)){
                    mTitle = getActivity().getString(R.string.top_given, DataUtils.firstName(mThisUser.getName()));
                    mListThankers = mThisUser.getTopUsersGiven();
                    mNumberThankers = mListThankers.size();
                }
                else {
                    mTitle = getActivity().getString(R.string.top_received, DataUtils.firstName(mThisUser.getName()));
                    mListThankers = mThisUser.getTopUsersReceived();
                    mNumberThankers = mListThankers.size();
                }
            }
        }

        setupFirebase();

        Log.v(TAG, "Thankers List. User: " + mUserId + ". Country: " + mCountry + ". Type: " + mType);

        if(savedInstanceState != null){
            mListThankersToPresent = (List<UserValue>) savedInstanceState.getSerializable("thankers-list");
            mNumberThankers = savedInstanceState.getLong("number-thankers");
            mCurrentIndex = savedInstanceState.getLong("current-index");

            if(mListThankersToPresent.size() > 0 && getActivity() != null){
                mAdapter = new ThankersProfilesAdapter(getActivity(), 0, mListThankersToPresent, mThisUser, mCountry);
                mListView.setAdapter(mAdapter);
            }

            else {
                initEmptyView();
            }
            mProgressBar.setVisibility(View.GONE);
        }

        else if(mThisUser != null && mType != null){
            Log.v(TAG, "Thankers List. Found a user user: " + mThisUser.getName());
            initList();
        }

        else if(mThisUser == null && mType != null){
            Log.v(TAG, "Thankers List. Going to create Thankers list for user: " + mUserId);
            createFirstList();
        }

        setupListViewScroll();

        return view;
    }

    private void setupFirebase(){
        mFirestore = FirebaseFirestore.getInstance();
    }

    public void createFirstList(){
        mFirestore.collection(DB_REFERENCE).document(mUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            mThisUser = documentSnapshot.toObject(User.class);
                            mTitle = getActivity().getString(R.string.top_received, DataUtils.firstName(mThisUser.getName()));
                            Utils.changeBarSubTitle(getActivity(), mActionBar, mTitle);
                            mListThankers = mThisUser.getTopUsersReceived();
                            mNumberThankers = mListThankers.size();
                            initList();
                        }
                    }
                });
    }

    public void initList(){
        mControl = 1;
        mListThankersToPresent = new ArrayList<>();
        for(int i = 0; i != NUMBER_RESULTS && i != mListThankers.size(); i++){
            mListThankersToPresent.add(mListThankers.get(i));
            mCurrentIndex = i;
        }

        if(getActivity() != null){
            mAdapter = new ThankersProfilesAdapter(getActivity(), 0, mListThankersToPresent, mThisUser, mCountry);
            mListView.setAdapter(mAdapter);
        }

        mProgressBar.setVisibility(View.GONE);
    }

    public void initViews(View v){
        mListView = (ListView) v.findViewById(R.id.list_thankers);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        mEmptyGiving = (TextView) v.findViewById(R.id.empty_view_giving);
        mEmptyReceiving = (TextView) v.findViewById(R.id.empty_view_receiving);
    }

    public void initEmptyView(){
        View emptyView;
        switch(mType){
            case TOP_USERS_THANKS_GIVEN: emptyView = mEmptyGiving; break;
            case TOP_USERS_THANKS_RECEIVED: emptyView = mEmptyReceiving; break;
            default: emptyView = null; break;
        }
        mListView.setEmptyView(emptyView);
    }

    public void setupListViewScroll(){
        mListView.setOnScrollListener(new ListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;
            private LinearLayout lBelow;
            private Query newQuery;
            private long count = 2;
            private int numberScrolls = 2;

            @Override
            public void onScrollStateChanged (AbsListView view, int scrollState){
                // TODO Auto-generated method stub
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll (AbsListView view,int firstVisibleItem,
                                  int visibleItemCount, int totalItemCount){
                // TODO Auto-generated method stub
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;


            }

            private void isScrollCompleted () {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {

                    if(mListThankersToPresent.size() < mNumberThankers && mListThankers.size() > NUMBER_RESULTS){
                        mProgressBar.setVisibility(View.VISIBLE);

                        for(int i = (int) mCurrentIndex + 1; i != (NUMBER_RESULTS * count) && i != mListThankers.size(); i++){
                            mCurrentIndex = i;
                            mListThankersToPresent.add(mListThankers.get(i));
                        }
                        count++;

                        mAdapter.notifyDataSetChanged();

                        mProgressBar.setVisibility(View.GONE);
                    }

                }
            };

        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("thankers-list", (Serializable) mListThankersToPresent);
        outState.putLong("number-thankers", mNumberThankers);
        outState.putLong("current-index", mCurrentIndex);
    }

    @Override
    public void onResume(){
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        mActionBar = activity.getSupportActionBar();
        mActionBar.setTitle(null);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        if(getActivity() != null && mTitle != null){
            Utils.changeBarSubTitle(getActivity(), mActionBar, mTitle);
        }

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        mActionBar.setHomeAsUpIndicator(upArrow);
    }

    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public void onPause(){
        super.onPause();
        mActionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onStop(){
        super.onStop();

    }
}

