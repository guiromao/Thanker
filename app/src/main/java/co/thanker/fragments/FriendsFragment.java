package co.thanker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import co.thanker.R;
import co.thanker.adapters.FriendsFragmentPagerAdapter;
import co.thanker.data.FriendRank;
import co.thanker.data.User;
import co.thanker.fragments.friends_fragments.FriendsFeedFragment;
import co.thanker.fragments.friends_fragments.FriendsListFragment;
import co.thanker.fragments.friends_fragments.FriendsRequestsFragment;


public class FriendsFragment extends Fragment {

    private final String TAG = "FriendsFragment";
    private static final String DB_REFERENCE = "users";
    private final String FRIEND_REQUESTS = "friend-requests";
    private final String STATE_DATABASE_READ = "state-database-read";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String USER_OBJECT = "user-object";
    private final String FRIEND_RANKS = "friend-ranks";
    private final String FRIENDS_NUMBER = "friends-number";
    private final String NUMBER_REQUESTS = "number-requests";
    private final String LIST_REQUESTS = "list-requests";
    private final String LIST_FRIENDS = "list-friends";
    private final String FRIENDS_LIST = "friends-list";
    private final String OUR_USER_ID = "our-user-id";

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    private ViewPager mViewPager;
    private TabLayout mTab;
    private FriendsFragmentPagerAdapter mPagerAdapter;

    private TextView mTextNotifications;

    private User mUser;
    private boolean mHasReadDatabase = false;
    private String mUserCountry;
    private String mUserId;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        mViewPager = (ViewPager) view.findViewById(R.id.friends_view_pager);
        mAuth = FirebaseAuth.getInstance();

        Bundle bundle = getArguments();
        List<FriendRank> friendRanksList = new ArrayList<>();


        if (bundle != null) {
            mUserCountry = bundle.getString(OUR_USER_COUNTRY);
            mUserId = bundle.getString(OUR_USER_ID);
        }

        setupFirebase();

        if (getActivity() != null && mAuth.getCurrentUser() != null) {

            //Create new Miwork Fragment Page Adapter, and add the Fragments to it
            mFirestore.collection(DB_REFERENCE).document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading our User\'s info in FriendsFragment");
                                mUser = documentSnapshot.toObject(User.class);

                                long numberFriends = mUser.getFriends().size();
                                long numberRequests = mUser.getUnseenRequests();
                                Fragment friendsFragment = new FriendsListFragment();
                                Fragment feedFragment = new FriendsFeedFragment();
                                Bundle friendsBundle = new Bundle();
                                Bundle friendsCompleteBundle = new Bundle();
                                friendsBundle.putString(OUR_USER_COUNTRY, mUserCountry);
                                friendsBundle.putString(OUR_USER_ID, mUserId);
                                friendsBundle.putLong(NUMBER_REQUESTS, numberRequests);
                                //friendsBundle.putStringArrayList(FRIEND_RANKS, (ArrayList<String>) mListFriendsRanks);
                                friendsBundle.putLong(FRIENDS_NUMBER, numberFriends);
                                friendsBundle.putSerializable(USER_OBJECT, mUser);
                                Bundle reqBundle = friendsBundle;
                                reqBundle.putSerializable(LIST_REQUESTS, (Serializable) mUser.getFriendRequests());
                                reqBundle.putSerializable(USER_OBJECT, mUser);
                                reqBundle.putString(OUR_USER_COUNTRY, mUserCountry);

                                friendsCompleteBundle = friendsBundle;
                                friendsCompleteBundle.putSerializable(LIST_FRIENDS, (Serializable) mUser.getFriends());
                                friendsFragment.setArguments(friendsBundle);
                                feedFragment.setArguments(friendsBundle);

                                FriendsRequestsFragment reqFragment = new FriendsRequestsFragment();
                                reqFragment.setArguments(reqBundle);

                                mPagerAdapter = new FriendsFragmentPagerAdapter(getChildFragmentManager());
                                mPagerAdapter.addFragment(friendsFragment, getString(R.string.friends_list));
                                mPagerAdapter.addFragment(feedFragment, getString(R.string.friends_feed));
                                //mPagerAdapter.addFragment(new FriendsTopFragment(), getString(R.string.more));
                                mPagerAdapter.addFragment(reqFragment, getString(R.string.friends_requests));

                                //mPagerAdapter.getItem(1).setArguments(bundle);
            /*
            mPagerAdapter.getItem(0).setArguments(friendsBundle);
            mPagerAdapter.getItem(1).setArguments(friendsBundle);*/

                                mViewPager.setAdapter(mPagerAdapter);

                                // Give the TabLayout the ViewPager
                                mTab = (TabLayout) view.findViewById(R.id.friends_tabs);
                                mTab.setupWithViewPager(mViewPager);
                                mTab.setTabTextColors(getActivity().getResources().getColorStateList(R.color.colorPrimary));
                                mTab.setBackgroundColor(getActivity().getResources().getColor(R.color.backgroundLight));
                                mTab.getTabAt(2).setCustomView(R.layout.friend_request_notifications_badge);
                                mTextNotifications = (TextView) mTab.getTabAt(2).getCustomView().findViewById(R.id.text_notifications_number);
                                checkNewRequests();

                                mTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                    @Override
                                    public void onTabSelected(TabLayout.Tab tab) {
                                        Log.v(TAG, "Checking friend requests\' badge. Position: " + tab.getPosition());
                                        if(tab.getPosition() == 2){
                                           mTextNotifications.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onTabUnselected(TabLayout.Tab tab) {

                                    }

                                    @Override
                                    public void onTabReselected(TabLayout.Tab tab) {

                                    }
                                });
                            }
                        }
                    });

        }

        return view;
    }

    public void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }

    public void checkNewRequests(){
        long unseenRequests = mUser.getUnseenRequests();
        if(unseenRequests > 0){
            if (getActivity() != null) {
                mTextNotifications.setText(String.valueOf(unseenRequests));
                mTextNotifications.setVisibility(View.VISIBLE);
            }
        }
        else {
            mTextNotifications.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        state.putBoolean(STATE_DATABASE_READ, mHasReadDatabase);
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
