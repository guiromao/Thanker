package co.thanker.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.thanker.InviteActivity;
import co.thanker.MainActivity;
import co.thanker.R;
import co.thanker.adapters.CategoryValueAdapter;
import co.thanker.adapters.SearchAdapter;
import co.thanker.data.CategoryValue;
import co.thanker.data.StatsThanks;
import co.thanker.data.User;
import co.thanker.data.UserResult;
import co.thanker.data.UserSnippet;
import co.thanker.utils.DataUtils;

public class SearchFragment extends Fragment implements SearchAdapter.SearchAdapterClickListener {

    private final String TAG = "SearchFragment";
    private static final String OPEN_ANOTHER_PROFILE = "open-another-profile";
    private final String OUR_USER_ID = "our-user-id";
    private final String USER_SNIPPET = "user-snippet";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String DB_REFERENCE = "users";
    private final String COUNTRIES_REFERENCE = "countries-values";
    private final String USERS_NAME = "name";
    private final String USER_COUNTRY = "user-country";
    private final String USER_ID_STRING = "user-id-string";
    private final String OTHER_USER_EMAIL = "other-user-email";
    private final String OUR_USER = "our-user";
    private final String INVITE_EXTRA = "invite-friends";
    private final String SEARCH_TYPE_USER = "search-type-user";
    private final String SEARCH_TYPE_EMAIL = "search-type-email";
    private final String SAVED_SEARCH_QUERIES = "saved-search-queries";
    private final String SAVED_SEARCH_USERS = "saved-search-users";
    private final String PURE_SEARCH_STRING = "pure-search-string";
    private final String SAVED_QUERIES = "saved-queries";
    private final String SAVED_USERS = "saved-users";

    private final int CATEGORY_NUMBER_THRESHOLD = 6;
    private final int MAX_RESULTS = 10;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DocumentReference mProfileReference;
    private CollectionReference mUserSnippetRef;
    private Query mCountryTrendingQuery;
    private Query mSearchQuery;
    private Query mSearchTranslatableQuery;
    private RecyclerView mSearchRecyclerView;
    private GridView mGridView;
    private CategoryValueAdapter mAdapter;
    private List<CategoryValue> mListCategories;
    private LinearLayoutManager mLayoutManager;
    private SearchAdapter mSearchAdapter;
    private FirebaseRecyclerOptions<User> mOptions;
    private EditText mSearchView;
    private SearchManager mSearchManager;
    private Query mQuery;
    private TextView mTitleView;
    private TextView mSubtitleView;
    private TextView mTextTrending;
    private ImageView mImagePerson;
    private ImageView mImageLogo;
    private ImageView mImageErase;
    private ProgressBar mProgressBar;
    private BottomNavigationView mNavigation;

    private View mView;
    private StatsThanks mStatsDate;
    private Bundle mBundle;
    private String mQueryText;
    private String mSearchType;
    private String mUserCountry;

    private List<User> mListProfiles;
    private List<UserSnippet> mUserResults;
    private List<UserSnippet> mSavedSearchedUsersList;
    private List<String> mSavedSearchedQueriesList;
    private List<String> mThemePages;

    private List<String> mTranslatablePages;

    private User mUser;
    private boolean mIsPureSearch = false;
    private boolean mGridViewResized = false;
    private int mCountControl;

    private SharedPreferences mSharedPreferencesQueries;
    private SharedPreferences.Editor mEditorQueries;
    private SharedPreferences mSharedPreferencesUsers;
    private SharedPreferences.Editor mEditorUsers;

    private SearchAdapter.SearchAdapterClickListener mSearchClickListener = new SearchAdapter.SearchAdapterClickListener() {
        @Override
        public void onSearchItemClick(String userId, String email) {
            if (userId != null) {
                String isAnotherUser;

                if (userId.equals(mAuth.getCurrentUser().getUid())) {
                    isAnotherUser = getString(R.string.no);
                } else {
                    isAnotherUser = getString(R.string.yes);
                }

                //String country = DataUtils.retrieveCountry(getActivity(), mUser);
                Bundle userInfoBundle = new Bundle();
                userInfoBundle.putString(USER_ID_STRING, userId);
                userInfoBundle.putString(OUR_USER_ID, mUser.getUserId());
                userInfoBundle.putString(OUR_USER_COUNTRY, mUserCountry);
                Fragment fragment;

                Log.v(TAG, "This is the country in the upper SearchClick Cenas: " + mUserCountry);

                if (mAuth.getCurrentUser().getUid().equals(userId)) {
                    fragment = new MyProfileFragment();
                } else {
                    fragment = new OtherProfileFragment();
                }

                fragment.setArguments(userInfoBundle);
                //FragmentManager fManager = ((MainActivity)mContext).getSupportFragmentManager();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            } else {
                if (email != null) {
                    if (getActivity() != null) {
                        Log.v(TAG, "Inviting friends. Email to invite: " + email);
                        Intent inviteIntent = new Intent(getContext(), InviteActivity.class);
                        inviteIntent.putExtra(OTHER_USER_EMAIL, email);
                        inviteIntent.putExtra(OUR_USER, mUser);
                        inviteIntent.putExtra(OUR_USER_COUNTRY, mUserCountry);
                        startActivity(inviteIntent);
                        Toast.makeText(getActivity(), (getString(R.string.send_to_email) + " " + email), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mView = view;

        setupFirebase();

        String ourUserId = mAuth.getCurrentUser().getUid();

        if (getActivity() != null) {
            mThemePages = getThemePages();
            mSharedPreferencesQueries = getActivity().getSharedPreferences(SAVED_QUERIES, Context.MODE_PRIVATE);
            mSharedPreferencesUsers = getActivity().getSharedPreferences(SAVED_USERS, Context.MODE_PRIVATE);
            mEditorQueries = mSharedPreferencesQueries.edit();
            mEditorUsers = mSharedPreferencesUsers.edit();
        }


        mProfileReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            mUser = documentSnapshot.toObject(User.class);
                        }
                    }
                });

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mIsPureSearch = bundle.getBoolean(PURE_SEARCH_STRING, false);
            mUserCountry = bundle.getString(OUR_USER_COUNTRY, "");
            Log.v(TAG, "Country location of user. Got in Search Fragment: " + mUserCountry);
        }


        mTextTrending = view.findViewById(R.id.text_trending_categories);
        mGridView = view.findViewById(R.id.gridview_trending_categories);

        Log.v(TAG, "Making the trending categories. Country: " + mUserCountry);
        Log.v(TAG, "Making the trending categories. Date Code: " + DataUtils.generateDateCode());
        Log.v(TAG, "Making the trending categories. Day: " + DataUtils.generateDay());

        if (mIsPureSearch) {
            mCountryTrendingQuery = mFirestore.collection(COUNTRIES_REFERENCE)
                    .whereEqualTo("country", DataUtils.getEnglishCountry(getActivity(), mUserCountry))
                    .whereEqualTo("year", DataUtils.generateYear())
                    .whereEqualTo("month", DataUtils.generateMonth())
                    .whereEqualTo("day", DataUtils.generateDay())
                    .limit(1);

            mCountryTrendingQuery.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.size() > 0){
                                for(QueryDocumentSnapshot statsSnapshot: queryDocumentSnapshots){
                                    mStatsDate = statsSnapshot.toObject(StatsThanks.class);
                                    displayStats();
                                }

                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading StatsThanks of today in our Country, for Top Thanked Categories");
                            }
                            else {
                                Log.v(TAG, "Logo in Search: Logo to be Visible");
                                mImageLogo.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }

        mListProfiles = new ArrayList<User>();

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar_search);

        if (getActivity() != null) {
            mSearchRecyclerView = (RecyclerView) view.findViewById(R.id.search_recycler_view);
            mLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
            mSearchRecyclerView.setLayoutManager(mLayoutManager);
        }

        //setupListProfiles();
        initializeViews(view);

        if (savedInstanceState != null) {
            mSavedSearchedUsersList = (List<UserSnippet>) savedInstanceState.getSerializable("saved-results");
            mSavedSearchedQueriesList = (List<String>) savedInstanceState.getSerializable("queried-searches");
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((MainActivity)getActivity()).setNavigationVisibility(true);
                mSearchView.clearFocus();
                //mSearchView.setSelected(false);
                Log.v(TAG, "Hiding or Showing the BottomNavigationView. Called in in OnTouchListener, in View");

                return false;
            }
        });

        mGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((MainActivity)getActivity()).setNavigationVisibility(true);
                //mSearchView.setSelected(false);
                return false;
            }
        });

        return view;
    }

    public void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUserSnippetRef = mFirestore.collection(USER_SNIPPET);
        mProfileReference = mFirestore.collection(DB_REFERENCE).document(mAuth.getCurrentUser().getUid());
    }

    public void initializeViews(View layoutView) {
        mSearchRecyclerView = (RecyclerView) layoutView.findViewById(R.id.search_recycler_view);
        mImageLogo = layoutView.findViewById(R.id.image_logo);
        mImageErase = layoutView.findViewById(R.id.image_erase);
        //mSearchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        mSearchView = layoutView.findViewById(R.id.search_view);
        //mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(getActivity().getComponentName()));
        //mSearchView.setQueryHint(getContext().getString(R.string.search_hint));
        mSearchRecyclerView.setVisibility(View.GONE);
        RelativeLayout searchLayout =  layoutView.findViewById(R.id.relative_search);

        //mSearchView.setIconified(false);
        mSearchView.clearFocus();

        mSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((MainActivity)getActivity()).setNavigationVisibility(false);
                mProgressBar.setVisibility(View.VISIBLE);
                Log.v(TAG, "Search & Progress Bar. Visibility of Progress Bar: " + mProgressBar.getVisibility());
                querySearch(s.toString());
                if (s.toString() != null) {
                    if (s.toString().equals("")) {
                        mProgressBar.setVisibility(View.GONE);
                        mSearchRecyclerView.setAdapter(null);
                        mSearchRecyclerView.setVisibility(View.GONE);
                    }
                }

                if(s.toString().length() > 0){
                    mImageErase.setVisibility(View.VISIBLE);
                }
                else {
                    mImageErase.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mImageErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setText(null);
            }
        });

        mSearchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    ((MainActivity)getActivity()).setNavigationVisibility(false);
                }
                else {
                    ((MainActivity)getActivity()).setNavigationVisibility(true);
                }
            }
        });

        /*mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((MainActivity)getActivity()).setNavigationVisibility(false);
                mProgressBar.setVisibility(View.VISIBLE);
                Log.v(TAG, "Search & Progress Bar. Visibility of Progress Bar: " + mProgressBar.getVisibility());
                querySearch(newText);
                if (newText != null) {
                    if (newText.equals("")) {
                        mProgressBar.setVisibility(View.GONE);
                        mSearchRecyclerView.setAdapter(null);
                        mSearchRecyclerView.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });*/

        //checking if it's a call for a friend invite, and adapt layout if so
        mBundle = getArguments();

        mSavedSearchedQueriesList = new ArrayList<>();
        mSavedSearchedUsersList = new ArrayList<>();

        if (mBundle != null) {
            if (mBundle.getString(INVITE_EXTRA) != null) {
                if (mBundle.getString(INVITE_EXTRA).equals(getString(R.string.yes))) {
                    mTitleView = (TextView) layoutView.findViewById(R.id.text_title);
                    mSubtitleView = (TextView) layoutView.findViewById(R.id.text_subtitle);
                    mImagePerson = (ImageView) layoutView.findViewById(R.id.image_person);

                    mTitleView.setVisibility(View.VISIBLE);
                    mSubtitleView.setVisibility(View.VISIBLE);
                    mImagePerson.setVisibility(View.VISIBLE);

                    mSearchView.setHint(getString(R.string.search_for_friend_email));

                    mSavedSearchedQueriesList = new ArrayList<>();
                    //mSavedSearchedUsersList = new ArrayList<>();
                }
            }

        } else {
            mSavedSearchedQueriesList = new ArrayList<>();
            //mSavedSearchedUsersList = new ArrayList<>();
            Log.v(TAG, "Saved search tag. Neither queries nor users were previously saved, and now retrieved");
        }

        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_main_bottomappbar, null);
        mNavigation = (BottomNavigationView) view.findViewById(R.id.bottom_navigation);

        Log.v(TAG, "Checking parent View in Search. View = " + (view != null) + ". mNavigation ID: " + mNavigation.getId());

        /*mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((MainActivity)getActivity()).setNavigationVisibility(false);
                    Log.v(TAG, "Hiding or Showing the BottomNavigationView. Set to Hide, in SearchView");
                } else {
                    ((MainActivity)getActivity()).setNavigationVisibility(true);
                    Log.v(TAG, "Hiding or Showing the BottomNavigationView. Set to Show, in SearchView");
                }
            }
        });*/


    }

    public void setupListProfiles() {
        //mProfileReference.addValueEventListener(profilesEventListener);
    }

    public /*List<User>*/ void querySearch(final String text) {
        //List<User> presentResults = new ArrayList<User>();

        if (text.length() >= 3) {

            if (!doesSavedListContainNameOrEmail(text.trim())) {
                Log.v(TAG, "Searches And Saves. Text searched is completely new");
                if (!doesListContainName(mSavedSearchedQueriesList, text.trim()) || text.trim().contains("@")) {
                    Log.v(TAG, "Searches And Saves. Text isn't on Searched Queries yet");
                    mProgressBar.setVisibility(View.VISIBLE);
                    Log.v(TAG, "Search & Progress Bar. Visibility of Progress Bar: " + mProgressBar.getVisibility());

                    Log.v(TAG, "New Search. Retrieving new results");

                    final String queryText = text.toLowerCase().trim();

                    if (DataUtils.isEmail(queryText)) {
                        mSearchType = SEARCH_TYPE_EMAIL;
                        mSearchQuery = mUserSnippetRef.orderBy("email").startAt(queryText).endAt(queryText + "\uf8ff").limit(MAX_RESULTS);
                        Log.v(TAG, "New Search. Email query");
                    } else {
                        mSearchType = SEARCH_TYPE_USER;
                        mSearchQuery = mUserSnippetRef.orderBy("name").startAt(queryText).endAt(queryText + "\uf8ff").limit(MAX_RESULTS);
                    }

                    if (!queryText.equals("")) {
                        mUserResults = new ArrayList<>();

                        mProgressBar.setVisibility(View.VISIBLE);
                        Log.v(TAG, "Search & Progress Bar. Visibility of Progress Bar: " + mProgressBar.getVisibility());

                        mSavedSearchedQueriesList.add(queryText);

                        //mSearchRecyclerView.setAdapter(null);

                        //NEW STUFF

                        final String originalText = text.trim();

                        mSearchQuery.get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if(queryDocumentSnapshots.size() > 0){
                                            for (QueryDocumentSnapshot userDocument : queryDocumentSnapshots) {
                                                UserSnippet user = userDocument.toObject(UserSnippet.class);
                                                if (!DataUtils.doesListContainUserSnippetResult(mSavedSearchedUsersList, user)) {
                                                    mUserResults.add(user);
                                                    mSavedSearchedUsersList.add(user);
                                                    Log.v(TAG, "Search. Adding user to results: " + user.getName() + ", ID: " + user.getUserId());
                                                } else {
                                                    Log.v(TAG, "New Search. Already have this NameId");
                                                }

                                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading UserSnippet as Search Result from Query");
                                            }

                                            mProgressBar.setVisibility(View.GONE);
                                            Log.v(TAG, "Search & Progress Bar. Visibility of Progress Bar: " + mProgressBar.getVisibility());
                                            mSearchAdapter = new SearchAdapter(getContext(), mUserResults, mSearchClickListener);
                                            mSearchRecyclerView.setAdapter(mSearchAdapter);
                                            mSearchRecyclerView.setVisibility(View.VISIBLE);
                                        }
                                        else if(DataUtils.isEmail(queryText)){
                                            Log.v(TAG, "Found results that are considered emails");
                                            //EMAIL STUFF
                                            mUserResults = new ArrayList<UserSnippet>();
                                            UserSnippet searchUser = new UserSnippet(null, null, text, null, null, null, null);

                                            mUserResults.add(searchUser);

                                            mProgressBar.setVisibility(View.GONE);
                                            Log.v(TAG, "Search & Progress Bar. Visibility of Progress Bar: " + mProgressBar.getVisibility());
                                            mSearchAdapter = new SearchAdapter(getContext(), mUserResults, mSearchClickListener);
                                            mSearchRecyclerView.setAdapter(mSearchAdapter);
                                            mSearchRecyclerView.setVisibility(View.VISIBLE);

                                        }

                                        else {
                                            mSearchRecyclerView.setAdapter(null);
                                            mProgressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });

                    }

                }

            } else {
                Log.v(TAG, "Searches And Saves. Text exists in Saved User Results");
                Log.v(TAG, "New Search. Retrieving from previous searched results");
                mProgressBar.setVisibility(View.GONE);
                Log.v(TAG, "Search & Progress Bar. Visibility of Progress Bar: " + mProgressBar.getVisibility());
                mUserResults = retrieveUsersFromRecentSearches(text.trim());
                if (mSearchAdapter != null && mUserResults != null) {
                    mSearchAdapter = new SearchAdapter(getContext(), mUserResults, this);
                    mSearchRecyclerView.setAdapter(mSearchAdapter);
                    mSearchRecyclerView.setVisibility(View.VISIBLE);
                }
            }

        } else {
            mProgressBar.setVisibility(View.GONE);
            Log.v(TAG, "Search & Progress Bar. Visibility of Progress Bar: " + mProgressBar.getVisibility());
            mSearchRecyclerView.setAdapter(null);
        }

        //mProgressBar.setVisibility(View.GONE);

    }

    public void displayStats() {

        mTextTrending.setVisibility(View.VISIBLE);
        mGridView.setVisibility(View.VISIBLE);

        mListCategories = new ArrayList<>();

        mListCategories.add(new CategoryValue("personThanks", mStatsDate.getPersonThanks()));
        mListCategories.add(new CategoryValue("brandThanks", mStatsDate.getBrandThanks()));
        mListCategories.add(new CategoryValue("businessThanks", mStatsDate.getBusinessThanks()));
        mListCategories.add(new CategoryValue("natureThanks", mStatsDate.getNatureThanks()));
        mListCategories.add(new CategoryValue("healthThanks", mStatsDate.getHealthThanks()));
        mListCategories.add(new CategoryValue("foodThanks", mStatsDate.getFoodThanks()));
        mListCategories.add(new CategoryValue("associationThanks", mStatsDate.getAssociationThanks()));
        mListCategories.add(new CategoryValue("homeThanks", mStatsDate.getHomeThanks()));
        mListCategories.add(new CategoryValue("scienceThanks", mStatsDate.getScienceThanks()));
        mListCategories.add(new CategoryValue("religionThanks", mStatsDate.getReligionThanks()));
        mListCategories.add(new CategoryValue("sportsThanks", mStatsDate.getSportsThanks()));
        mListCategories.add(new CategoryValue("lifestyleThanks", mStatsDate.getLifestyleThanks()));
        mListCategories.add(new CategoryValue("techThanks", mStatsDate.getTechThanks()));
        mListCategories.add(new CategoryValue("fashionThanks", mStatsDate.getFashionThanks()));
        mListCategories.add(new CategoryValue("educationThanks", mStatsDate.getEducationThanks()));
        mListCategories.add(new CategoryValue("gamesThanks", mStatsDate.getGamesThanks()));
        mListCategories.add(new CategoryValue("travelThanks", mStatsDate.getTravelThanks()));
        mListCategories.add(new CategoryValue("govThanks", mStatsDate.getGovThanks()));
        mListCategories.add(new CategoryValue("beautyThanks", mStatsDate.getBeautyThanks()));
        mListCategories.add(new CategoryValue("financeThanks", mStatsDate.getFinanceThanks()));
        mListCategories.add(new CategoryValue("cultureThanks", mStatsDate.getCultureThanks()));

        Collections.sort(mListCategories, new Comparator<CategoryValue>() {
            @Override
            public int compare(CategoryValue o1, CategoryValue o2) {
                return Long.compare(o2.getValue(), o1.getValue());
            }
        });

        List<CategoryValue> tempList = new ArrayList<CategoryValue>();

        for (int i = 0; i != 6; i++) {
            if (mListCategories.get(i).getValue() > 0) {
                tempList.add(mListCategories.get(i));
                Log.v(TAG, "Making the Tops in Search. Category: " + mListCategories.get(i).getName() + " - " + (i + 1));
            }
        }

        mListCategories = tempList;

        if (mListCategories.size() > 0) {
            Log.v(TAG, "Logo in Search: Logo NOT to be Visible");
            mImageLogo.setVisibility(View.GONE);
            if (getActivity() != null) {
                mAdapter = new CategoryValueAdapter(getActivity(), 0, mListCategories, mAuth.getCurrentUser().getUid(), mUserCountry);
                mGridView.setAdapter(mAdapter);

                mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (!mGridViewResized) {
                            mGridViewResized = true;
                            int numberItems = mListCategories.size();
                            resizeGridView(mGridView, numberItems, 3);
                        }
                    }
                });
            }
        }
    }

    public List<UserSnippet> retrieveUsersFromRecentSearches(String s) {
        List<UserSnippet> userResults = new ArrayList<>();

        Log.v(TAG, "Comparing words. Calling recent searches saves. Size of Saved Users: " + mSavedSearchedUsersList.size());

        if (!DataUtils.isEmail(s.trim())) {
            for (int i = 0; i != mSavedSearchedUsersList.size(); i++) {
                if (mSavedSearchedUsersList.get(i).getUserId() != null) {
                    if (mSavedSearchedUsersList.get(i).getName().toLowerCase().contains(s.trim().toLowerCase())) {
                        Log.v(TAG, "Found result in the General field. Name: " + mSavedSearchedUsersList.get(i).getName());
                        userResults.add(mSavedSearchedUsersList.get(i));
                        //cleanSavedResults(i);
                    }
                }
            }
        } else {
            for (int n = 0; n != mSavedSearchedUsersList.size(); n++) {
                Log.v(TAG, "New Search. Looking in saved Email results");
                if (mSavedSearchedUsersList.get(n).getEmail() != null) {
                    Log.v(TAG, "New Search. Looking in saved Email results. This email: " + mSavedSearchedUsersList.get(n).getEmail());
                    if (mSavedSearchedUsersList.get(n).getEmail().toLowerCase().contains(s.toLowerCase()) && DataUtils.isEmail(s) /* && !s.contains("@")*/) {
                        Log.v(TAG, "Found result in the Emails field");
                        userResults.add(mSavedSearchedUsersList.get(n));
                        //cleanSavedResults(n);
                    }
                }
            }
            if (userResults.size() == 0 && DataUtils.isEmail(s)) {
                userResults.add(new UserSnippet(null, null, s, null, null, null, null));
            }
        }

        return userResults;
    }


    public boolean doesSavedListContainNameOrEmail(String text) {
        text = text.toLowerCase().trim();

        if (DataUtils.isEmail(text)) {
            for (UserSnippet item : mSavedSearchedUsersList) {
                if (item.getEmail() != null) {
                    if (item.getEmail().toLowerCase().contains(text)) {
                        return true;
                    }
                }
            }
        } else {
            for (UserSnippet item : mSavedSearchedUsersList) {
                if (item.getName() != null) {
                    if (item.getName().toLowerCase().contains(text)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    //for resizing the Gridview, according to the amount of items
    private void resizeGridView(GridView gridView, int items, int columns) {
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        int oneRowHeight = gridView.getHeight();
        int rows = items > 3 ? 2 : 1;
        params.height = (oneRowHeight * rows) + 80;
        gridView.setLayoutParams(params);
    }


    @Override
    public void onSearchItemClick(String userId, String email) {
        if (userId != null) {
            String isAnotherUser;

            if (userId.equals(mAuth.getCurrentUser().getUid())) {
                isAnotherUser = getString(R.string.no);
            } else {
                isAnotherUser = getString(R.string.yes);
            }

            String country = mUserCountry;//DataUtils.retrieveCountry(getActivity(), mUser);
            Bundle userInfoBundle = new Bundle();
            userInfoBundle.putString(USER_ID_STRING, userId);
            userInfoBundle.putString(OUR_USER_ID, mUser.getUserId());
            userInfoBundle.putString(OUR_USER_COUNTRY, country);
            Log.v(TAG, "This is the country in the down SearchClick Cenas: " + country);
            Fragment fragment;

            if (mAuth.getCurrentUser().getUid().equals(userId)) {
                fragment = new MyProfileFragment();
            } else {
                fragment = new OtherProfileFragment();
            }

            fragment.setArguments(userInfoBundle);
            //FragmentManager fManager = ((MainActivity)mContext).getSupportFragmentManager();
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        } else {
            if (email != null) {
                if (getActivity() != null) {
                    Log.v(TAG, "Inviting friends. Email to invite: " + email);
                    Intent inviteIntent = new Intent(getContext(), InviteActivity.class);
                    inviteIntent.putExtra(OTHER_USER_EMAIL, email);
                    inviteIntent.putExtra(OUR_USER, mUser);
                    inviteIntent.putExtra(OUR_USER_COUNTRY, mUserCountry);
                    startActivity(inviteIntent);
                    //Toast.makeText(getActivity(), (getString(R.string.send_to_email) + " " + email), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private List<String> getThemePages() {
        List<String> results = new ArrayList<>();

        if (getActivity() != null) {
            String[] arrayPages = getActivity().getResources().getStringArray(R.array.translatable_pages);
            for (String word : arrayPages) {
                results.add(word);
                Log.v(TAG, "SearchFragment. Word added to Themes: " + word);
            }
        }
        return results;
    }

    private String getTranslatedName(String originalWord) {
        String noResult = "";

        if (getActivity() != null) {
            List<String> originalNames = DataUtils.getTranslatableStrings(getActivity());
            for (int i = 0; i != originalNames.size(); i++) {
                Log.v(TAG, "Translations: comparing the word \"" + originalWord + "\" with \"" + originalNames.get(i) + "\"");
                if (originalWord.toUpperCase().equals(originalNames.get(i).toUpperCase())) {
                    return mThemePages.get(i);
                }
            }
        }
        return noResult;
    }

    public boolean doesListContainName(List<String> list, String text) {
        for (String item : list) {
            if (item.toLowerCase().contains(text.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public <T> void setList(String key, List<T> list, SharedPreferences.Editor editor) {
        Gson gson = new Gson();
        String json = gson.toJson(list);

        set(key, json, editor);
    }

    public void set(String key, String value, SharedPreferences.Editor editor) {
        editor.putString(key, value);
        editor.commit();
    }

    public void getLists() {
        /*String serializedObjectQueries = mSharedPreferencesQueries.getString(SAVED_QUERIES, null);

        if(serializedObjectQueries != null)
        {
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {
            }.getType();
            mSavedSearchedQueriesList = gson.fromJson(serializedObjectQueries, type);
        }

        else {
            mSavedSearchedQueriesList = new ArrayList<>();
        }*/

        String serializedObjectUsers = mSharedPreferencesUsers.getString(SAVED_USERS, null);

        if (serializedObjectUsers != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<UserResult>>() {
            }.getType();
            mSavedSearchedUsersList = gson.fromJson(serializedObjectUsers, type);
            Log.v(TAG, "New Search. Retrieved saved users list");
        } else {
            mSavedSearchedUsersList = new ArrayList<>();
        }
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

        //getLists();
        ((MainActivity)getActivity()).setNavigationVisibility(true);

        if (!mIsPureSearch) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            ActionBar actionBar = activity.getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);


            final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //setList(SAVED_QUERIES, mSavedSearchedQueriesList, mEditorQueries);
        //setList(SAVED_USERS, mSavedSearchedUsersList, mEditorUsers);
        ((MainActivity)getActivity()).setNavigationVisibility(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        // mProfileReference.removeEventListener(profilesEventListener);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        state.putString("queryText", mQueryText);
        state.putSerializable("saved-results", (Serializable) mSavedSearchedUsersList);
        state.putSerializable("queried-searches", (Serializable) mSavedSearchedQueriesList);

    }

}
