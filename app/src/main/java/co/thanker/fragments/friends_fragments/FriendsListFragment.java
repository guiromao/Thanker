package co.thanker.fragments.friends_fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.thanker.R;
import co.thanker.adapters.FriendTopAdapter;
import co.thanker.adapters.FriendsAdapter;
import co.thanker.data.FriendData;
import co.thanker.data.FriendRank;
import co.thanker.data.NameId;
import co.thanker.data.Thanks;
import co.thanker.data.User;
import co.thanker.data.UserFriend;
import co.thanker.data.UserSnippet;
import co.thanker.data.UserValue;
import co.thanker.fragments.SearchFragment;
import co.thanker.utils.DataUtils;
import co.thanker.utils.Utils;

public class FriendsListFragment extends Fragment implements FriendsAdapter.FriendsAdapterClickListener {

    private final String TAG = "FriendsListFragment";
    private static final String DB_REFERENCE = "users";
    private final String USER_OBJECT = "user-object";
    private final String USER_SNIPPET = "user-snippet";
    private final String LIST_FRIENDS = "list-friends";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String FRIEND_RANKS = "friend-ranks";
    private final String FRIENDS_NUMBER = "friends-number";
    private final String SAVED_FRIENDS = "saved-name-ids";
    private final String PURE_SEARCH_STRING = "pure-search-string";
    private final int USER_THRESHOLD = 3;

    private Context mContext;

    private ListView mListView;
    private LinearLayout mEmptyView;
    private TextView mEmptyViewSearch;
    private ProgressBar mProgressBar;
    private ImageView mInfoRanking;
    private FriendTopAdapter mListAdapter;
    private FriendTopAdapter mListAdapterSearch;
    private List<FriendRank> mFriendsRanks;
    private List<FriendRank> mPresentFriends;
    private List<FriendRank> mPresentFriendsSearch;
    private List<FriendRank> mSearchList;
    private List<UserSnippet> mSavedFriends;
    private List<String> mSavedSearchedQueriesList;
    private String mUserId;
    private int mFriendIndex;
    private long mNumberFriends;
    private String mUserCountry;
    private boolean mDidChanges;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    private SearchManager mSearchManager;
    private SearchView mSearchView;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public FriendsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mEmptyView = (LinearLayout) view.findViewById(R.id.empty_view_friends);
        mEmptyViewSearch = (TextView) view.findViewById(R.id.empty_view_friends_search);
        mInfoRanking = view.findViewById(R.id.image_info_ranking);
        mDidChanges = false;

        /*View mainView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main_bottomappbar, null);
        mNavigation = (BottomNavigationView) mainView.findViewById(R.id.bottom_navigation);

        mNavigation.setVisibility(View.GONE);*/
        mSavedFriends = new ArrayList<>();

        if (getArguments() != null) {
            //mUser = (User) getArguments().getSerializable(USER_OBJECT);
            mUserCountry = getArguments().getString(OUR_USER_COUNTRY);
            //mUserResults = getArguments().getStringArrayList(FRIEND_RANKS);
            mNumberFriends = getArguments().getLong(FRIENDS_NUMBER);
            mFriendsRanks = (List<FriendRank>) getArguments().getSerializable(LIST_FRIENDS);

            Log.v(TAG, "New feed making. Number Friends: " + mNumberFriends);
        }

        setupFirebase();

        if (getActivity() != null) {
            mSharedPreferences = getActivity().getSharedPreferences(SAVED_FRIENDS + mAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
            mEditor = mSharedPreferences.edit();
        }

        mListView = (ListView) view.findViewById(R.id.list_friends);
        mSavedSearchedQueriesList = new ArrayList<>();

        //initListView();

        initSearch(view);

        if (getActivity() != null) {
            mContext = getActivity();
            View anotherView = getActivity().getCurrentFocus();
            if (anotherView != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            Utils.hideKeyboardFrom(getActivity());
        }

        /*/
        mRecyclerViewFriends = (RecyclerView) view.findViewById(R.id.recycler_view_friends_list);
        mRecyclerViewFriends.setLayoutManager(new LinearLayoutManager(getContext()));*/

        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    Fragment searchFragment = new SearchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(PURE_SEARCH_STRING, true);
                    bundle.putString(OUR_USER_COUNTRY, mUserCountry);
                    searchFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchFragment).addToBackStack(null).commit();
                }
            }
        });

        mInfoRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle(getActivity().getString(R.string.what_is_ranking));
                    builder.setMessage(getActivity().getString(R.string.this_is_ranking));
                    builder.setPositiveButton(getActivity().getString(R.string.got_it),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        if(savedInstanceState != null){
            mDidChanges = savedInstanceState.getBoolean("did-changes");
            mSavedSearchedQueriesList = savedInstanceState.getStringArrayList("list-queries");
            mSavedFriends = (List<UserSnippet>) savedInstanceState.getSerializable("saved-friends");
            List<FriendRank> listFriends = (List<FriendRank>) savedInstanceState.getSerializable("friends-list");
            if(listFriends.size() > 2){
                mPresentFriends = listFriends;
                mFriendIndex = mPresentFriends.size() - 1;
                mListAdapter = new FriendTopAdapter(getContext(), 0, mPresentFriends, mUserCountry);
                mListView.setAdapter(mListAdapter);
            }

            else {
                initList();
            }
        }

        else {
            initList();
        }

        setupListViewScroll();

        return view;
    }

    public void initList() {

        mPresentFriends = new ArrayList<>();

        sortFriendRanks();

        for(mFriendIndex = 0; mFriendIndex != USER_THRESHOLD && mFriendIndex != mFriendsRanks.size(); mFriendIndex++){
            mPresentFriends.add(mFriendsRanks.get(mFriendIndex));
        }

        if(mPresentFriends.size() > 0){
            mListAdapter = new FriendTopAdapter(getContext(), 0, mPresentFriends, mUserCountry);
            mListView.setAdapter(mListAdapter);
        }

        else {
            mListView.setEmptyView(mEmptyView);
        }

        mProgressBar.setVisibility(View.GONE);
    }


    public void setupFirebase() {
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();
    }

    public void sortFriendRanks(){
        Collections.sort(mFriendsRanks, new Comparator<FriendRank>() {
            @Override
            public int compare(FriendRank o1, FriendRank o2) {
                return Double.compare(o2.getRankFactor(), o1.getRankFactor());
            }
        });
    }

    public <T> void setList(String key, List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);

        set(key, json);
    }

    public void set(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public void getList(){
        String serializedObject = mSharedPreferences.getString(SAVED_FRIENDS + mAuth.getCurrentUser().getUid(), null);

        if(serializedObject != null)
        {
            Gson gson = new Gson();
            Type type = new TypeToken<List<FriendData>>() {
            }.getType();
            //mSavedSearchedFriends = gson.fromJson(serializedObject, type);
        }

        else {
            //mSavedSearchedFriends = new ArrayList<>();
        }
    }

    public void initSearch(View v) {
        //mSavedSearchedFriends = new ArrayList<>();
        //mSavedSearchResults = new ArrayList<>();
        mSavedSearchedQueriesList = new ArrayList<>();
        mSearchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) v.findViewById(R.id.search_friends);
        mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setQueryHint(getContext().getString(R.string.search_hint_friends));

        mSearchView.setIconified(false);
        mSearchView.clearFocus();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                querySearch(newText);
                if (newText != null) {
                    if (newText.equals("")) {
                        mListView.setAdapter(mListAdapter);
                    }
                }
                return false;
            }
        });
    }


    public /*List<User>*/ void querySearch(final String text) {
        //List<User> presentResults = new ArrayList<User>();

        mProgressBar.setVisibility(View.VISIBLE);

        if (text.length() >= 1) {
            mListView.setAdapter(null);
            mSearchList = retrieveUsersFromFriends(text.toLowerCase());
            mPresentFriendsSearch = new ArrayList<>();

            if(!doesTextExistInSnippet(text)){

                mSavedSearchedQueriesList.add(text);

                for(int i = 0; i != mSearchList.size(); i++){
                    final int searchIndex = i;
                    int index = getIndex(mSearchList.get(i));
                    mFirestore.collection(USER_SNIPPET).document(mSearchList.get(i).getUserId())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        UserSnippet user = documentSnapshot.toObject(UserSnippet.class);
                                        if(!mFriendsRanks.get(index).getName().equalsIgnoreCase(user.getName())){
                                            mFriendsRanks.get(index).setName(user.getName());
                                            mDidChanges = true;
                                        }
                                        else {
                                            if(!DataUtils.doesListContainFriendRank(mPresentFriendsSearch, mSearchList.get(searchIndex)) && mSearchList.get(searchIndex) != null){
                                                mPresentFriendsSearch.add(mSearchList.get(searchIndex));
                                                mSavedFriends.add(user);
                                            }
                                        }
                                        mSavedSearchedQueriesList.add(user.getName());
                                    }

                                    if((searchIndex == (mSearchList.size() - 1)) && getActivity() != null){
                                        if(mPresentFriendsSearch.size() > 0){
                                            mListAdapterSearch = new FriendTopAdapter(getActivity(), 0, mPresentFriendsSearch, mUserCountry);
                                            mListView.setAdapter(mListAdapterSearch);
                                        }
                                        else {
                                            mListView.setEmptyView(mEmptyViewSearch);
                                        }
                                    }

                                    Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading from User\'s UserSnippet, for Search In Friends List");
                                }
                            });
                }
            }

            else {
                if(mSearchList.size() > 0){
                    mListAdapterSearch = new FriendTopAdapter(getActivity(), 0, mSearchList, mUserCountry);
                    mListView.setAdapter(mListAdapterSearch);
                }
                else {
                    mEmptyViewSearch.setText(getActivity().getString(R.string.no_friends_found, text));
                    mListView.setEmptyView(mEmptyViewSearch);
                }
            }
        }

        else {
            mListView.setAdapter(mListAdapter);
        }


        mProgressBar.setVisibility(View.GONE);

    }

    public boolean doesTextExistInSnippet(String text){
        text = text.toLowerCase();

        for(UserSnippet snippet: mSavedFriends){
            if(snippet.getName().toLowerCase().contains(text)){
                return true;
            }
        }

        return false;
    }

    public int getIndex(FriendRank friend){
        for(int i = 0; i != mFriendsRanks.size(); i++){
            if(friend.getUserId().equalsIgnoreCase(mFriendsRanks.get(i).getUserId())){
                return i;
            }
        }
        return -1;
    }

    public List<FriendRank> retrieveUsersFromFriends(String s) {
        List<FriendRank> friendResults = new ArrayList<>();

        for (FriendRank friend : mFriendsRanks) {
            if (friend.getName().toLowerCase().contains(s)) {
                friendResults.add(friend);
                Log.v(TAG, "Friends List: found Saved result");
            }
        }

        return friendResults;
    }

    public void setupListViewScroll() {
        mListView.setOnScrollListener(new ListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;
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

                    for( ; mFriendIndex != (USER_THRESHOLD * count) && mFriendIndex != mFriendsRanks.size(); mFriendIndex++){
                        mPresentFriends.add(mFriendsRanks.get(mFriendIndex));
                    }

                    mListAdapter.notifyDataSetChanged();
                    count++;
                }

                mProgressBar.setVisibility(View.GONE);

            }

        });
    }

    public boolean doesItContainUserFriend(List<UserFriend> friendsList, UserFriend friend) {

        for (UserFriend item : friendsList) {
            if (item.getUser().getUserId().equals(friend.getUser().getUserId())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onFriendItemClick(String userId) {
      /*  if(userId != null){
            Bundle userInfoBundle = new Bundle();
            userInfoBundle.putString(USER_ID_STRING, userId);

            Fragment fragment = new OtherProfileFragment();

            fragment.setArguments(userInfoBundle);
            //FragmentManager fManager = ((MainActivity)mContext).getSupportFragmentManager();
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("friends-list", (Serializable) mPresentFriends);
        outState.putStringArrayList("list-queries", (ArrayList<String>) mSavedSearchedQueriesList);
        outState.putBoolean("did-changes", mDidChanges);
        outState.putSerializable("saved-friends", (Serializable) mSavedFriends);
    }

    @Override
    public void onResume() {
        super.onResume();
        //mUserFriendsReference.addListenerForSingleValueEvent(mFriendsListener);
        //mUserFriendsReference.addListenerForSingleValueEvent(mInitializeFriendsListener);
        //getList();
        //printList();
    }

    @Override
    public void onPause(){
        super.onPause();

        if(mAuth != null){
            if(mAuth.getCurrentUser() != null){
                //setList(SAVED_FRIENDS + mAuth.getCurrentUser().getUid(), mSavedSearchedFriends);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if(mDidChanges){
            mFirestore.collection(DB_REFERENCE).document(mUserId).update("friends", mFriendsRanks);
        }
    }

}
