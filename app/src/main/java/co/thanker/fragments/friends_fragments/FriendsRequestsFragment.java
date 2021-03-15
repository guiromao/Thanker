package co.thanker.fragments.friends_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import co.thanker.R;
import co.thanker.adapters.FriendRequestsListAdapter;
import co.thanker.data.FriendRequest;
import co.thanker.data.ThanksData;
import co.thanker.data.User;

public class FriendsRequestsFragment extends Fragment implements FriendRequestsListAdapter.EventListener{

    private final String TAG = "FriendRequestsFragment";
    private static final String DB_REFERENCE = "users";
    private final String FRIEND_REQUESTS = "friend-requests";
    private final String THANKS_DATA = "thanks-data";
    private final String USER_OBJECT = "user-object";
    private final String LIST_REQUESTS = "list-requests";
    private final String NUMBER_REQUESTS = "number-requests";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final int NUMBER_INVITES = 12;

    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;

    //private RecyclerView mRecyclerViewInvites;
    private ListView mListView;
    private FriendRequestsListAdapter mAdapter;
    private TextView mEmptyViewRequests;


    private User mUser;
    private ThanksData mUserData;
    private List<FriendRequest> mRequests;
    private List<String> mAllRequestsKeys;
    private List<FriendRequest> mPresentRequests;
    private long mNumberRequests;
    private String mLastRequestId;
    private long mCountRequests;
    private long mNewRequests;
    private String mCountry;

    private ProgressBar mProgressBar;

    public FriendsRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends_requests, container, false);

        Log.v(TAG, "Entered onCreateView of Friend requests fragment");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        mProgressBar = (ProgressBar) view.findViewById(R.id.friend_requests_progress_bar);
        //mRecyclerViewInvites = (RecyclerView) view.findViewById(R.id.recycler_view_friends_requests);
        //mRecyclerViewInvites.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView = (ListView) view.findViewById(R.id.list_friend_requests);
        mEmptyViewRequests = (TextView) view.findViewById(R.id.empty_view_requests);

        if (getArguments() != null) {
            mRequests = (List<FriendRequest>) getArguments().getSerializable(LIST_REQUESTS);
            mUser = (User) getArguments().getSerializable(USER_OBJECT);
            mNewRequests = getArguments().getLong(NUMBER_REQUESTS);
            mCountry = getArguments().getString(OUR_USER_COUNTRY);
        }

        mCountRequests = 0;

        if (savedInstanceState != null && getActivity() != null) {
            mRequests = (List<FriendRequest>) savedInstanceState.getSerializable("requests");
            mAllRequestsKeys = (List<String>) savedInstanceState.getSerializable("all-requests-keys");
            mNumberRequests = savedInstanceState.getLong("number-requests");
            mCountRequests = savedInstanceState.getLong("count-requests");
            mLastRequestId = savedInstanceState.getString("last-request-id");
            mUserData = (ThanksData) savedInstanceState.getSerializable("thanks-data");

            if (mRequests.size() > 0) {
                mAdapter = new FriendRequestsListAdapter(getActivity(), 0, mRequests, mAuth.getCurrentUser().getUid(), mUser, mUserData, mCountry, this);
                mListView.setAdapter(mAdapter);
            }
        } else {

            mDatabase.collection(THANKS_DATA).document(mUser.getUserId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                mUserData = documentSnapshot.toObject(ThanksData.class);
                                initListRequests();
                            }
                        }
                    });

        }

        setupListViewScroll();

        return view;
    }

    public void initListRequests() {
        mPresentRequests = new ArrayList<>();
        for (int i = 0; i != NUMBER_INVITES && i != mRequests.size(); i++) {
            mPresentRequests.add(mRequests.get(i));
            mCountRequests++;
        }

        if (getActivity() != null && mPresentRequests.size() > 0) {
            mAdapter = new FriendRequestsListAdapter(getActivity(), 0, mRequests, mAuth.getCurrentUser().getUid(), mUser, mUserData, mCountry, this);
            mListView.setAdapter(mAdapter);

            if(mNewRequests > 0){
                mDatabase.collection(DB_REFERENCE).document(mUser.getUserId()).update("unseenRequests", 0);
                mNewRequests = 0;
            }
        }

        else if(mPresentRequests.size() == 0){
            mListView.setEmptyView(mEmptyViewRequests);
        }

        mProgressBar.setVisibility(View.GONE);

    }

    public void onEvent() {
        setEmptyView();
    }

    public void setEmptyView(){
        mListView.setEmptyView(mEmptyViewRequests);
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

                    if (mCountRequests < mRequests.size()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        for (int i = (int) mCountRequests; i != (NUMBER_INVITES * count) && i != mRequests.size(); i++) {
                            mPresentRequests.add(mRequests.get(i));
                            mCountRequests++;
                        }

                        mAdapter.notifyDataSetChanged();

                        mProgressBar.setVisibility(View.GONE);
                    }
                }


            }
        });
    }

    @Override
    public void onEvent(int data) {
        setEmptyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("requests", (Serializable) mPresentRequests);
        outState.putSerializable("all-requests-keys", (Serializable) mAllRequestsKeys);
        outState.putSerializable("thanks-data", (Serializable) mUserData);
        outState.putString("last-request-id", mLastRequestId);
        outState.putLong("number-requests", mNumberRequests);
        outState.putLong("count-requests", mCountRequests);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
