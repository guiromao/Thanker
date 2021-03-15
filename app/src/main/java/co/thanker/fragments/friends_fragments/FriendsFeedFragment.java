package co.thanker.fragments.friends_fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.thanker.R;
import co.thanker.adapters.ThanksFeedAdapter;
import co.thanker.data.FriendRank;
import co.thanker.data.Thanks;
import co.thanker.data.User;
import co.thanker.data.UserValue;
import co.thanker.utils.DataUtils;

public class FriendsFeedFragment extends Fragment {

    private final String TAG = FriendsFeedFragment.class.getSimpleName();
    private final String THANKS_DB = "thanks-db";
    private final String DB_REFERENCE = "users";
    private final String THANKS_GIVEN = "thanks-given";
    private final String THANKS_RECEIVED = "thanks-received";
    private final String TOP_USERS_THANKS_RECEIVED = "top-users-thanks-received";
    private final String TOP_USERS_THANKS_GIVEN = "top-users-thanks-given";
    private final String FRIENDS_DB = "friends-db";
    private final String USER_OBJECT = "user-object";
    private final String LIST_FRIENDS = "list-friends";
    private final String DYNAMIC_GIVER = "dynamic-giver";
    private final String DYNAMIC_RECEIVER = "dynamic-receiver";
    private final String TYPE_ADAPTER_NORMAL = "type-adapter-normal";
    private final String TYPE_ADAPTER_FEED = "type-adapter-feed";
    private final String FRIEND_RANKS = "friend-ranks";
    private final String FRIENDS_NUMBER = "friends-number";
    private final String FRIENDS_LIST = "friends-list";
    private final String OUR_USER_ID = "our-user-id";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String USER_ID_STRING = "user-id-string";

    private final long THRESHOLD_DAYS_IN_MILLIS = 259200000;
    private final int FRIENDS_FEED_THRESHOLD = 10;
    private final int THANKS_FEED_THRESHOLD = 6;
    private final int NUMBER_THANKS = 3;
    private final int NUMBER_FRIENDS = 3;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DatabaseReference mFriendsDataRef;
    private Query mGetFriendsByRankQuery;

    private User mUser;
    private List<FriendRank> mListFriends;
    private List<String> mListFriendsIds;
    private List<String> mListFriendsRanks;
    private List<Thanks> mListOfThanks;
    private List<Thanks> mHelperList;
    private List<String> mSavedThanksKeys;
    private List<String> mSavedIds;
    private List<Double> mSavedRanks;
    private List<UserValue> mFriendsIThankedList;
    private List<UserValue> mFriendsIWasThankedByList;
    private List<String> mFriendsList;
    private ProgressBar mProgressBar;
    private ListView mListView;
    private ThanksFeedAdapter mAdapter;
    private TextView mEmptyView;
    private TextView mEmptyViewInteraction;

    private long mCountThanks;
    private long mIndexFriends;
    private long mThanksGiven;
    private long mThanksReceived;
    private long mNumberFriends;
    private long mTotalThanks;
    private long mTotalRetrievedFriends;
    private int mCountControl;
    private double mLastRankFactor;
    private String mLastFriendKey;
    private String mUserId;
    private boolean mHasMoreNodes = true;
    private int mScrolls;
    private int mNumberThanksToRead;
    private boolean mKeepReading;
    private boolean mSaved;
    private String mCountry;
    private int mLastAdded;

    private Bundle mBundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends_feed, container, false);

        mListView = (ListView) view.findViewById(R.id.list_view_feed);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mEmptyView = (TextView) view.findViewById(R.id.empty_view_feed);
        mEmptyViewInteraction = (TextView) view.findViewById(R.id.empty_view_feed_interaction);

        mLastAdded = 1;

        setupFirebase();

        mBundle = getArguments();

        if (mBundle != null) {
            mCountry = mBundle.getString(OUR_USER_COUNTRY);
            mListFriends = (List<FriendRank>) mBundle.getSerializable(LIST_FRIENDS);
            mUser = (User) mBundle.getSerializable(USER_OBJECT);
            mListFriendsIds = friendsToArray();

        } else {
            //Log.v(TAG, "Making the feed. Friend didn't arrive...");
        }

        if (savedInstanceState != null && getActivity() != null) {
            mListOfThanks = (List<Thanks>) savedInstanceState.getSerializable("list-thanks");
            mSavedThanksKeys = (List<String>) savedInstanceState.getSerializable("thanks-keys");
            mNumberFriends = savedInstanceState.getLong("number-friends");
            mLastFriendKey = savedInstanceState.getString("last-friend-key");
            mLastRankFactor = savedInstanceState.getDouble("last-rank-factor");
            mLastAdded = savedInstanceState.getInt("last-added");

            if (mListOfThanks.size() > 0) {
                mAdapter = new ThanksFeedAdapter(getActivity(), 0, mListOfThanks, mUser, mCountry);
                mListView.setAdapter(mAdapter);
            } else {
                mListView.setEmptyView(mEmptyViewInteraction);
            }
            mProgressBar.setVisibility(View.GONE);
        } else {
            initListView();
        }

        setupListViewScroll();

        return view;
    }

    public void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();
    }

    public void initListView() {
        mListOfThanks = new ArrayList<>();
        mSavedThanksKeys = new ArrayList<>();

        Collections.sort(mListFriends, new Comparator<FriendRank>() {
            @Override
            public int compare(FriendRank f1, FriendRank f2) {
                return Double.compare(f2.getRankFactor(), f1.getRankFactor());
            }
        });

        mCountThanks = 0;

        if(mListFriends.size() == 0){
            mProgressBar.setVisibility(View.GONE);
            mListView.setEmptyView(mEmptyViewInteraction);
        }

        /*mFirestore
                .collection(THANKS_DB)
                .where("fromUserId", mListFriendsIds)
                .orderBy("date", Query.Direction.DESCENDING)
                .whereGreaterThan("date", (System.currentTimeMillis() - THRESHOLD_DAYS_IN_MILLIS))
                .limit(NUMBER_THANKS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot thanksSnapshot : queryDocumentSnapshots) {
                                Thanks thanks = thanksSnapshot.toObject(Thanks.class);
                                if (!DataUtils.doesListContainItem(mSavedThanksKeys, thanksSnapshot.getId())) {
                                    mListOfThanks.add(thanks);
                                    mSavedThanksKeys.add(thanksSnapshot.getId());
                                    mCountThanks++;
                                    Log.v(TAG, "New feed technique. Thanks " + thanksSnapshot.getId() + " added!");
                                }
                            }

                            if (mListOfThanks.size() > 0) {

                                if (getActivity() != null) {
                                    mAdapter = new ThanksFeedAdapter(getActivity(), 0, mListOfThanks, mUser, mCountry);
                                    mListView.setAdapter(mAdapter);
                                }

                            } else {
                                mListView.setEmptyView(mEmptyViewInteraction);
                            }

                        }

                        else {
                            mListView.setEmptyView(mEmptyViewInteraction);
                        }

                        mProgressBar.setVisibility(View.GONE);
                    }
                });*/

        for (int i = 0; i != NUMBER_FRIENDS && i != mListFriends.size(); i++) {
            mIndexFriends = i;
            mFirestore.collection(THANKS_DB)
                    .whereEqualTo("fromUserId", mListFriends.get(i).getUserId())
                    .orderBy("date", Query.Direction.DESCENDING)
                    .whereGreaterThan("date", (System.currentTimeMillis() - THRESHOLD_DAYS_IN_MILLIS))
                    .limit(NUMBER_THANKS)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.size() > 0) {
                                for (QueryDocumentSnapshot thanksSnapshot : queryDocumentSnapshots) {
                                    Thanks thanks = thanksSnapshot.toObject(Thanks.class);
                                    if (!DataUtils.doesListContainItem(mSavedThanksKeys, thanksSnapshot.getId())) {
                                        mListOfThanks.add(thanks);
                                        mSavedThanksKeys.add(thanksSnapshot.getId());
                                        mCountThanks++;
                                        Log.v(TAG, "New feed technique. Thanks " + thanksSnapshot.getId() + " added!");
                                    }

                                    Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading Thanks information for Feed");
                                }
                            }

                            else {
                                mListView.setEmptyView(mEmptyViewInteraction);
                            }

                            if ((mIndexFriends == (NUMBER_FRIENDS - 1)) || (mIndexFriends == (mListFriends.size() - 1))) {
                                if (mListOfThanks.size() > 0) {
                                    Collections.sort(mListOfThanks, new Comparator<Thanks>() {
                                        @Override
                                        public int compare(Thanks o1, Thanks o2) {
                                            return Long.compare(o2.getDate(), o1.getDate());
                                        }
                                    });
                                    if (getActivity() != null) {
                                        mAdapter = new ThanksFeedAdapter(getActivity(), 0, mListOfThanks, mUser, mCountry);
                                        mListView.setAdapter(mAdapter);
                                    }

                                } else {
                                    mListView.setEmptyView(mEmptyViewInteraction);
                                }

                                mProgressBar.setVisibility(View.GONE);
                            }
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }

    public List<String> friendsToArray(){
        List<String> result = new ArrayList<>();

        for(FriendRank friend: mListFriends){
            result.add(friend.getUserId());
        }

        return result;
    }


    public boolean doesListContainDouble(List<Double> list, double rank) {
        for (double d : list) {
            if (d == rank) {
                return true;
            }
        }
        return false;
    }

    public void setupListViewScroll() {
        mListView.setOnScrollListener(new ListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;
            private LinearLayout lBelow;
            private Query newQuery;
            private long count = 2;
            private long mVariableThreshold = 3;
            private int spins = 2;


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

                    mHelperList = new ArrayList<>();
                    Log.v(TAG, "Firestore Feed. Entered scroll");

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if((mIndexFriends < mListFriends.size() - 1) && (mLastAdded > 0)){
                                mProgressBar.setVisibility(View.VISIBLE);
                                mLastAdded = 0;
                                Log.v(TAG, "Firestore Feed. There are more friends to search. Index of Friends: " + mIndexFriends);
                                for (int i = (int) mIndexFriends; i != mListFriends.size() && mIndexFriends != (NUMBER_FRIENDS * count); i++) {
                                    mIndexFriends = i;
                                    mFirestore.collection(THANKS_DB)
                                            .whereEqualTo("fromUserId", mListFriends.get(i).getUserId())
                                            .orderBy("date", Query.Direction.DESCENDING)
                                            .whereGreaterThan("date", (System.currentTimeMillis() - THRESHOLD_DAYS_IN_MILLIS))
                                            .limit(NUMBER_THANKS)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    if (queryDocumentSnapshots.size() > 0) {
                                                        for (QueryDocumentSnapshot thanksSnapshot : queryDocumentSnapshots) {
                                                            Thanks thanks = thanksSnapshot.toObject(Thanks.class);
                                                            if (!DataUtils.doesListContainItem(mSavedThanksKeys, thanksSnapshot.getId())) {
                                                                mHelperList.add(thanks);
                                                                mSavedThanksKeys.add(thanksSnapshot.getId());
                                                                mCountThanks++;
                                                                mLastAdded++;
                                                                Log.v(TAG, "New feed technique. Thanks " + thanksSnapshot.getId() + " added!");
                                                            }

                                                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading Thanks information for Feed, on Scroll");
                                                        }
                                                    }

                                                    Log.v(TAG, "Making the new feed in Firestore. Value of mIndexFriends: " + mIndexFriends);

                                                    if (mIndexFriends == (NUMBER_FRIENDS * 2 - 1) || (mIndexFriends == (mListFriends.size() - 1))) {
                                                        if (mHelperList.size() > 0) {
                                                            Collections.sort(mHelperList, new Comparator<Thanks>() {
                                                                @Override
                                                                public int compare(Thanks o1, Thanks o2) {
                                                                    return Long.compare(o2.getDate(), o1.getDate());
                                                                }
                                                            });
                                                            if (getActivity() != null) {
                                                                mListOfThanks.addAll(mHelperList);
                                                                mAdapter.notifyDataSetChanged();
                                                            }

                                                        } else {
                                                            mListView.setEmptyView(mEmptyViewInteraction);
                                                        }

                                                        count++;
                                                        mProgressBar.setVisibility(View.GONE);

                                                    }
                                                }
                                            });
                                }
                            }
                            else {
                                mProgressBar.setVisibility(View.GONE);
                                Log.v(TAG, "Firestore Feed. No more friends to search");
                            }
                        }
                    }, 1000);
                }

            }

        });
    }

    public void addList(List<Thanks> receivingList, List<Thanks> addingList) {
        for (Thanks item : addingList) {
            receivingList.add(item);
        }
    }

    public long getOtherThanks(String userId, List<UserValue> list) {
        for (UserValue userValue : list) {
            if (userValue.getUserId().equalsIgnoreCase(userId)) {
                return userValue.getValueThanks();
            }
        }

        return 0;
    }

    public boolean containingInRanks(String userId) {
        for (String item : mListFriendsRanks) {
            if (item.equalsIgnoreCase(userId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("list-thanks", (Serializable) mListOfThanks);
        outState.putSerializable("thanks-keys", (Serializable) mSavedThanksKeys);
        outState.putLong("number-friends", mNumberFriends);
        outState.putString("last-friend-key", mLastFriendKey);
        outState.putDouble("last-rank-factor", mLastRankFactor);
        outState.putInt("last-added", mLastAdded);
    }

    @Override
    public void onStart() {
        super.onStart();

        //mGetFriendsByRankQuery.addListenerForSingleValueEvent(mCheckFriendRanksListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        //mGetFriendsByRankQuery.removeEventListener(mCheckFriendRanksListener);
    }

}
