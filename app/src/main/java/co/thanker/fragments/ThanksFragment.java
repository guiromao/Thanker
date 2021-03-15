package co.thanker.fragments;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.thanker.R;
import co.thanker.adapters.ThanksListAdapter;
import co.thanker.data.Thanks;
import co.thanker.data.User;
import co.thanker.data.UserValue;
import co.thanker.utils.DataUtils;
import co.thanker.utils.Utils;

public class ThanksFragment extends Fragment {

    private final String TAG = "ThanksFragment";
    private static final String DB_REFERENCE = "users";
    private final String THANKS_DB = "thanks-db";
    private final String THANKS_GIVEN = "thanks-given";
    private final String THANKS_RECEIVED = "thanks-received";
    private final String DYNAMIC_GIVER = "dynamic-giver";
    private final String DYNAMIC_RECEIVER = "dynamic-receiver";
    private final String THANKS_DYNAMIC = "thanks-dynamic";
    private final String USER_OBJECT = "user-object";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String TYPE_ADAPTER_NORMAL = "type-adapter-normal";
    private final String TYPE_ADAPTER_FEED = "type-adapter-feed";
    private final String EXCHANGE_THANKS = "exchange-thanks";
    private final String OTHER_USER_THANKS = "other-user-thanks";
    private final String IS_PREMIUM = "has-welcomes";
    private final String SAVED_SNAPSHOT = "saved-snapshot";
    private final int NUMBER_RESULTS = 6;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DatabaseReference mUserRef;
    private DatabaseReference mTotalThanksRef;
    private Query mThanksQuery;

    private User mUser;
    private ListView mListView;
    private TextView mEmptyViewGiver;
    private TextView mEmptyViewGiverOther;
    private TextView mEmptyViewReceiver;
    private ThanksListAdapter mAdapter;
    private List<Thanks> mListThanks;
    private List<String> mThanksIds;
    private String mLastThanksId;
    private String mDescription;
    private String mDynamic;
    private AppCompatActivity mActivity;
    private ActionBar mActionBar;
    private ProgressBar mProgressBar;
    private String mLastThanksSnapshotString;
    private DocumentSnapshot mLastThanksSnapshot;
    private SharedPreferences mPrefsThankssnapshot;
    private SharedPreferences.Editor mPrefsEditor;

    private long mNumberThanks;
    private int mLastAdded;
    private long mLastDate;
    private boolean mIsExchangeThanks = false;
    private boolean mIsPremium;
    private String mOtherUserId;
    private int mCount;
    private String mCountry;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_thanks, container, false);

        mListView = (ListView) view.findViewById(R.id.list_thanks);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mEmptyViewGiver = (TextView) view.findViewById(R.id.empty_view_giver);
        mEmptyViewGiverOther = (TextView) view.findViewById(R.id.empty_view_giver_other);
        mEmptyViewReceiver = (TextView) view.findViewById(R.id.empty_view_receiver);

        mLastAdded = 0;
        //Intent gotIntent = getIntent();
        Bundle bundle = getArguments();

        if (bundle != null) {
            mUser = (User) bundle.getSerializable(USER_OBJECT);
            mDynamic = bundle.getString(THANKS_DYNAMIC);
            mIsPremium = bundle.getBoolean(IS_PREMIUM, false);
            mCountry = bundle.getString(OUR_USER_COUNTRY);

            if (bundle.getString(EXCHANGE_THANKS) != null) {
                if (bundle.getString(EXCHANGE_THANKS).equalsIgnoreCase("yes")) {
                    mIsExchangeThanks = true;
                    mOtherUserId = bundle.getString(OTHER_USER_THANKS);
                }
            }
        }

        Log.v(TAG, "Checking Country in ThanksFragment: " + mCountry);

        setupFirebase();

        mPrefsThankssnapshot = getActivity().getSharedPreferences(SAVED_SNAPSHOT + mAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
        mPrefsEditor = mPrefsThankssnapshot.edit();

        if (!mDynamic.equals("")) {

            String dynamic;

            if (mDynamic.equals(DYNAMIC_GIVER)) {
                mThanksQuery = mFirestore.collection(THANKS_DB).whereEqualTo("fromUserId", mUser.getUserId()).orderBy("date", Query.Direction.DESCENDING).limit(NUMBER_RESULTS);
                dynamic = DYNAMIC_GIVER;
                if (getActivity() != null) {
                    mDescription = getActivity().getString(R.string.givento, DataUtils.capitalize(mUser.getName()));
                }

                if (mIsExchangeThanks) {
                    mThanksQuery = mFirestore.collection(THANKS_DB).whereEqualTo("fromUserId", mUser.getUserId())
                            .whereEqualTo("toUserId", mOtherUserId).orderBy("date", Query.Direction.DESCENDING).limit(NUMBER_RESULTS);
                }
            } else {
                mThanksQuery = mFirestore.collection(THANKS_DB).whereEqualTo("toUserId", mUser.getUserId()).orderBy("date", Query.Direction.DESCENDING).limit(NUMBER_RESULTS);
                dynamic = DYNAMIC_RECEIVER;
                if (getActivity() != null) {
                    mDescription = getActivity().getString(R.string.receivedby, DataUtils.capitalize(mUser.getName()));
                }

                if (mIsExchangeThanks) {
                    mThanksQuery = mFirestore.collection(THANKS_DB).whereEqualTo("toUserId", mUser.getUserId())
                            .whereEqualTo("fromUserId", mOtherUserId).orderBy("date", Query.Direction.DESCENDING).limit(NUMBER_RESULTS);
                }

            }

            Log.v(TAG, "Checking Title description: " + mDescription);

            //mActionBar.setTitle(null);
            //mActionBar.setSubtitle(mDescription);

            final String dynamicConstant = dynamic;

            if (savedInstanceState != null) {
                mListThanks = (List<Thanks>) savedInstanceState.getSerializable("thanks-list");
                mThanksIds = (List<String>) savedInstanceState.getSerializable("thanks-ids");
                mLastThanksId = savedInstanceState.getString("last-thanks-id");
                mNumberThanks = savedInstanceState.getLong("number-thanks");
                mLastDate = savedInstanceState.getLong("last-date");
                mLastAdded = savedInstanceState.getInt("last-added");
                //mLastThanksSnapshot = savedInstanceState.getParcelable("last-snapshot", null);

                if (mListThanks != null) {
                    Log.v(TAG, "ThanksFragment. Checking savedInstanceState. Found List of Thanks");
                    if (mListThanks.size() > 0 && getActivity() != null) {
                        mAdapter = new ThanksListAdapter(getActivity(), 0, mListThanks, mThanksIds, mUser, mCountry, mDynamic, TYPE_ADAPTER_NORMAL, mIsPremium);
                        mListView.setAdapter(mAdapter);
                    } else {
                        if (mDynamic.equals(DYNAMIC_GIVER)) {
                            if (!mAuth.getCurrentUser().getUid().equals(mUser.getUserId())) {
                                mEmptyViewGiverOther.setText(getString(R.string.start_giving_thanks_other, DataUtils.capitalize(mUser.getName())));
                                mListView.setEmptyView(mEmptyViewGiverOther);
                            } else {
                                mListView.setEmptyView(mEmptyViewGiver);
                            }

                        } else {
                            mListView.setEmptyView(mEmptyViewReceiver);
                        }
                    }

                } else {
                    mEmptyViewGiverOther.setText(getString(R.string.start_giving_thanks_other, DataUtils.capitalize(mUser.getName())));
                    mListView.setEmptyView(mEmptyViewGiverOther);
                }

                mProgressBar.setVisibility(View.GONE);

            } else {
                mCount = 0;
                mListThanks = new ArrayList<>();
                mThanksIds = new ArrayList<>();
                mThanksQuery.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                mListThanks = new ArrayList<>();
                                mLastAdded = 0;
                                if (queryDocumentSnapshots.size() > 0) {
                                    for (QueryDocumentSnapshot thanksSnapshot : queryDocumentSnapshots) {
                                        Thanks thanks = thanksSnapshot.toObject(Thanks.class);
                                        if (!DataUtils.doesListContainItem(mThanksIds, thanksSnapshot.getId())) {
                                            mListThanks.add(thanks);
                                            mThanksIds.add(thanksSnapshot.getId());
                                            mLastAdded++;

                                        }
                                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading Thanks document");
                                    }
                                    if(mListThanks.size() > 0){
                                        Collections.sort(mListThanks, new Comparator<Thanks>() {
                                            @Override
                                            public int compare(Thanks o1, Thanks o2) {
                                                return Long.compare(o2.getDate(), o1.getDate());
                                            }
                                        });
                                        mLastThanksSnapshotString = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1).getId();
                                        mLastThanksSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                        mAdapter = new ThanksListAdapter(getActivity(), 0, mListThanks, mThanksIds, mUser, mCountry, mDynamic,
                                                TYPE_ADAPTER_NORMAL, mIsPremium);
                                        mListView.setAdapter(mAdapter);
                                    }

                                    mProgressBar.setVisibility(View.GONE);
                                } else {
                                    mProgressBar.setVisibility(View.GONE);
                                    if (mDynamic.equals(DYNAMIC_GIVER)) {
                                        if (!mAuth.getCurrentUser().getUid().equals(mUser.getUserId())) {
                                            mEmptyViewGiverOther.setText(getString(R.string.start_giving_thanks_other, DataUtils.capitalize(mUser.getName())));
                                            mListView.setEmptyView(mEmptyViewGiverOther);
                                        } else {
                                            mListView.setEmptyView(mEmptyViewGiver);
                                        }
                                    } else {
                                        mListView.setEmptyView(mEmptyViewReceiver);
                                    }
                                }
                            }
                        });
                Log.v(TAG, "ThanksFragment. Checking savedInstanceState. Did NOT find List of Thanks saved.");
            }

            setupListViewScroll();
        } else {

            //if a description of the type of Thanks listing hasn't been provided, end Activity
            //finish();
        }

        return view;
    }

    public void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }

    public void setupListViewScroll() {
        mListView.setOnScrollListener(new ListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;
            private LinearLayout lBelow;
            private Query newQuery;
            private long count = 0;


            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;


            }

            private void isScrollCompleted() {

                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {

                    if(mLastAdded > 0 && mLastThanksSnapshot != null){
                        if (mDynamic.equals(DYNAMIC_GIVER)) {
                            newQuery = mFirestore.collection(THANKS_DB).whereEqualTo("fromUserId", mUser.getUserId())
                                    .orderBy("date", Query.Direction.DESCENDING).startAfter(mLastThanksSnapshot).limit(NUMBER_RESULTS);
                            Log.v(TAG, "Last Key before scroll: " + mLastThanksId);


                            if (mIsExchangeThanks) {
                                newQuery = mFirestore.collection(THANKS_DB).whereEqualTo("fromUserId", mUser.getUserId()).whereEqualTo("toUserId", mOtherUserId)
                                        .orderBy("date", Query.Direction.DESCENDING).startAfter(mLastThanksSnapshot).limit(NUMBER_RESULTS);
                            }

                        } else {
                            newQuery = mFirestore.collection(THANKS_DB).whereEqualTo("toUserId", mUser.getUserId())
                                    .orderBy("date", Query.Direction.DESCENDING).startAfter(mLastThanksSnapshot).limit(NUMBER_RESULTS);

                            if (mIsExchangeThanks) {
                                newQuery = mFirestore.collection(THANKS_DB).whereEqualTo("toUserId", mUser.getUserId()).whereEqualTo("fromUserId", mOtherUserId)
                                        .orderBy("date", Query.Direction.DESCENDING).startAfter(mLastThanksSnapshot).limit(NUMBER_RESULTS);
                            }
                        }

                        newQuery
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (queryDocumentSnapshots.size() > 0) {
                                            mProgressBar.setVisibility(View.VISIBLE);
                                            mLastAdded = 0;
                                            List<Thanks> helperList = new ArrayList<>();
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    for (QueryDocumentSnapshot thanksSnapshot : queryDocumentSnapshots) {
                                                        Thanks thanks = thanksSnapshot.toObject(Thanks.class);
                                                        Log.v(TAG, "Adding thanks onscroll. Found new thanks");
                                                        if (!DataUtils.doesListContainItem(mThanksIds, thanksSnapshot.getId())) {
                                                            helperList.add(thanks);
                                                            mThanksIds.add(thanksSnapshot.getId());
                                                            mLastAdded++;
                                                            Log.v(TAG, "Adding thanks ID: " + thanksSnapshot.getId());
                                                        }

                                                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading Thanks document, onScroll");
                                                    }

                                                    if(helperList.size() > 0){
                                                        Collections.sort(helperList, new Comparator<Thanks>() {
                                                            @Override
                                                            public int compare(Thanks o1, Thanks o2) {
                                                                return Long.compare(o2.getDate(), o1.getDate());
                                                            }
                                                        });

                                                        for(Thanks thanks: helperList){
                                                            mListThanks.add(thanks);
                                                        }

                                                        mLastThanksSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                                        mAdapter.notifyDataSetChanged();
                                                    }

                                                    mProgressBar.setVisibility(View.GONE);
                                                }
                                            }, 1000);
                                        }
                                    }
                                });
                    }
                }
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity = (AppCompatActivity) getActivity();
        mActionBar = mActivity.getSupportActionBar();
        mActionBar.setTitle(null);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        if (getActivity() != null) {
            Utils.changeBarSubTitle(getActivity(), mActionBar, mDescription);
        }

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        mActionBar.setHomeAsUpIndicator(upArrow);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("thanks-list", (Serializable) mListThanks);
        outState.putSerializable("thanks-ids", (Serializable) mThanksIds);
        outState.putLong("number-thanks", mNumberThanks);
        outState.putSerializable("last-thanks-id", mLastThanksId);
        outState.putLong("last-date", mLastDate);
        //outState.putParcelable("last-snapshot", (Parcelable) mLastThanksSnapshot);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();
        mActionBar.setDisplayHomeAsUpEnabled(false);
    }

    public void onStop() {
        super.onStop();

    }

}