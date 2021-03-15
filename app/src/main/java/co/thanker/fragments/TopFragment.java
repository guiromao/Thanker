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
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.blongho.country_data.World;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import co.thanker.R;
import co.thanker.adapters.RankAdapter;
import co.thanker.adapters.RankAdapterOverall;
import co.thanker.data.IdData;
import co.thanker.data.ThanksData;
import co.thanker.data.User;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;
import co.thanker.utils.Utils;

public class TopFragment extends Fragment {

    private final String TAG = "TopActivity";
    private final String THANKS_DATA = "thanks-data";
    private static final String DB_REFERENCE = "users";
    private final String STATS_THANKS = "stats-thanks";
    private final String TOP_CATEGORY = "top-category";
    private final String TOP_CATEGORY_TRANSLATED = "top-category-translated";
    private final String THANKS_VALUES_REFERENCE = "thanks-values";
    private final String OUR_USER_ID = "our-user-id";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String COUNTRY_PREFS = "country-prefs";
    private final int NUMBER_USERS = 10;

    private FirebaseFirestore mFirestore;
    private Query mDataQuery;
    private FirebaseAuth mAuth;

    private ListView mListView;
    private RankAdapter mAdapter;
    private RankAdapterOverall mAdapterOverall;
    private ProgressBar mProgressBar;
    private ActionBar mActionBar;
    private Spinner mCountrySpinner;
    private LinearLayout mLinearCountries;
    private ImageView mImageFlag;
    private TextView mTextCategory;
    private TextView mEmptyView;
    private ImageView mImageCategory;

    private String mCategory;
    private String mCategoryString;
    private String mTranslatedCategory;
    private List<User> mListUsers;
    private List<IdData> mListData;
    private DocumentSnapshot mLastSnapshot;
    private String mCountry;
    private String mDisplayTopCountry;
    private String mEnglishCountry;
    private String mUserId;
    private boolean mIsOverall;
    private int mLastAdded;
    private SharedPreferences mSharedCountryPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_top, container, false);

        if (getArguments() != null) {
            mCategory = getArguments().getString(TOP_CATEGORY);
            mTranslatedCategory = getArguments().getString(TOP_CATEGORY_TRANSLATED);
            mUserId = getArguments().getString(OUR_USER_ID);
            mCountry = getArguments().getString(OUR_USER_COUNTRY);
            mDisplayTopCountry = mCountry;
            Log.v(TAG, "New top in Firestore. mCategory = " + mCategory);
        }

        if (getActivity() != null) {
            World.init(getActivity());
        }

        initViews(view);

        if(getActivity() != null)
        {
            mSharedCountryPrefs = getActivity().getSharedPreferences(COUNTRY_PREFS + mUserId, Context.MODE_PRIVATE);
            mCountry = mSharedCountryPrefs.getString("country", "");
            Log.v(TAG, "Country in Top: " + mCountry);
        }

        mCategoryString = DataUtils.thanksCategoryToStringCategory(mCategory);

        mTextCategory.setText(Html.fromHtml("<b>" + mTranslatedCategory.toUpperCase() + "</b>"));

        if (getActivity() != null) {
            mImageCategory.setImageDrawable(ImageUtils.getCategoryDrawable(getActivity(), mCategoryString));
        }

        setupFirebase();

        initTop();
        setupScroll();

        return view;
    }

    public void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        if (!mCategory.equalsIgnoreCase("Overall")) {
            mIsOverall = false;
        } else {
            mIsOverall = true;
        }

    }

    public void initTop() {

        mLastAdded = 0;

        if (!mIsOverall) {
            mListUsers = new ArrayList<>();
            mDataQuery = mFirestore.collection(DB_REFERENCE).whereEqualTo("livingCountry", mEnglishCountry).orderBy(mCategory, Query.Direction.DESCENDING).limit(NUMBER_USERS);
            mDataQuery
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Log.v(TAG, "Creating Hall of Famers. Size of query: " + queryDocumentSnapshots.size());
                            if (queryDocumentSnapshots.size() > 0) {
                                for (QueryDocumentSnapshot userSnapshot : queryDocumentSnapshots) {
                                    User user = userSnapshot.toObject(User.class);
                                    if (!DataUtils.doesListContainUser(mListUsers, user)) {
                                        mListUsers.add(user);
                                        mLastAdded++;
                                    }

                                    Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading User info for Hall of Fame, in specific Category");
                                }

                                mAdapter = new RankAdapter(getActivity(), 0, mListUsers, mCategory, mCountry, mAuth.getCurrentUser().getUid());
                                mListView.setAdapter(mAdapter);
                                mLastSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                            } else {
                                mEmptyView.setText(getActivity().getString(R.string.no_top_found, mDisplayTopCountry, DataUtils.translateAndFormat(getActivity(), mCategoryString)));
                                mListView.setAdapter(null);
                                mListView.setEmptyView(mEmptyView);
                            }
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
        } else {

            mLinearCountries.setVisibility(View.GONE);
            mDataQuery = mFirestore.collection(THANKS_DATA).orderBy("givenThanksValue", Query.Direction.DESCENDING).limit(NUMBER_USERS);

            mListData = new ArrayList<>();
            mDataQuery
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Log.v(TAG, "Creating Hall of Famers. Size of query: " + queryDocumentSnapshots.size());
                            if (queryDocumentSnapshots.size() > 0) {
                                for (QueryDocumentSnapshot dataSnapshot : queryDocumentSnapshots) {
                                    ThanksData data = dataSnapshot.toObject(ThanksData.class);
                                    String id = dataSnapshot.getId();
                                    IdData idData = new IdData(id, data);
                                    if (!DataUtils.doesListContainIdData(mListData, idData)) {
                                        mListData.add(idData);
                                        mLastAdded++;
                                    }

                                    Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading User info for Hall of Fame, in Overall");
                                }

                                mAdapterOverall = new RankAdapterOverall(getActivity(), 0, mListData, mCountry, mAuth.getCurrentUser().getUid());
                                mListView.setAdapter(mAdapterOverall);
                                mLastSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                            } else {
                                mEmptyView.setText(getActivity().getString(R.string.no_top_found_overall, mDisplayTopCountry));
                                mListView.setAdapter(null);
                                mListView.setEmptyView(mEmptyView);
                            }
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }

    public void initViews(View v) {
        mListView = (ListView) v.findViewById(R.id.list_ranks);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        mCountrySpinner = (Spinner) v.findViewById(R.id.spinner_country);
        mLinearCountries = (LinearLayout) v.findViewById(R.id.linear_countries);
        mTextCategory = (TextView) v.findViewById(R.id.text_category);
        mEmptyView = (TextView) v.findViewById(R.id.empty_view_top);
        mImageCategory = (ImageView) v.findViewById(R.id.image_category);
        mImageFlag = (ImageView) v.findViewById(R.id.image_flag);

        initializeCountrySpinner();

        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCountryInfo(mCountrySpinner.getSelectedItem().toString());
                initTop();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void initializeCountrySpinner() {
        if (getActivity() != null) {
            String currentCountry;
            Locale[] locales = Locale.getAvailableLocales();
            ArrayList<String> countries = new ArrayList<String>();

            for (Locale loc : locales) {
                String country = loc.getDisplayCountry();
                if ((country.length() > 0) && !(countries.contains(country))) {
                    countries.add(country);
                }
            }

            Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

            ArrayAdapter<String> adapterCountries = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, countries);
            mCountrySpinner.setAdapter(adapterCountries);

            currentCountry = DataUtils.getTranslatedCountry(getActivity(), mCountry);

            mCountrySpinner.setSelection(DataUtils.retrieveItem(countries, currentCountry));

            updateCountryInfo(currentCountry);
        }
    }

    public void updateCountryInfo(String country) {
        if (getActivity() != null) {
            int countryFlag;
            mEnglishCountry = DataUtils.getEnglishCountry(getActivity(), country);
            if (mEnglishCountry.equalsIgnoreCase("World")) {
                countryFlag = World.getWorldFlag();
            } else {
                countryFlag = World.getFlagOf(mEnglishCountry.toLowerCase());
            }
            mImageFlag.setImageResource(countryFlag);
            mDisplayTopCountry = DataUtils.getTranslatedCountry(getActivity(), mEnglishCountry);
        }
    }

    public void setupScroll() {
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

                    Query scrollQuery;

                    if(mLastAdded > 0){

                        mLastAdded = 0;

                        if (!mIsOverall) {
                            scrollQuery = mFirestore.collection(DB_REFERENCE)
                                    .whereEqualTo("livingCountry", mEnglishCountry).orderBy(mCategory, Query.Direction.DESCENDING).startAfter(mLastSnapshot).limit(NUMBER_USERS);

                            scrollQuery
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            Log.v(TAG, "Creating Hall of Famers. Size of query: " + queryDocumentSnapshots.size());
                                            if (queryDocumentSnapshots.size() > 0) {
                                                mProgressBar.setVisibility(View.VISIBLE);
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mProgressBar.setVisibility(View.VISIBLE);
                                                        for (QueryDocumentSnapshot userSnapshot : queryDocumentSnapshots) {
                                                            User user = userSnapshot.toObject(User.class);
                                                            if (!DataUtils.doesListContainUser(mListUsers, user)) {
                                                                mListUsers.add(user);
                                                                mLastAdded++;
                                                            }

                                                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading User info for Hall of Fame, in specific category, onScroll");
                                                        }

                                                        mAdapter.notifyDataSetChanged();
                                                        mLastSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                                        mProgressBar.setVisibility(View.GONE);
                                                    }
                                                }, 1000);

                                            }

                                        }
                                    });

                        } else {
                            scrollQuery = mFirestore.collection(THANKS_DATA)
                                    .orderBy("givenThanksValue", Query.Direction.DESCENDING).startAfter(mLastSnapshot).limit(NUMBER_USERS);

                            scrollQuery
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (queryDocumentSnapshots.size() > 0) {
                                                mProgressBar.setVisibility(View.VISIBLE);
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        for (QueryDocumentSnapshot dataSnapshot : queryDocumentSnapshots) {
                                                            ThanksData data = dataSnapshot.toObject(ThanksData.class);
                                                            String id = dataSnapshot.getId();
                                                            IdData idData = new IdData(id, data);
                                                            if (!DataUtils.doesListContainIdData(mListData, idData)) {
                                                                mListData.add(idData);
                                                                mLastAdded++;
                                                            }

                                                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading User info for Hall of Fame, Overall, onScroll");
                                                        }
                                                        mAdapterOverall.notifyDataSetChanged();
                                                        mLastSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                                        mProgressBar.setVisibility(View.GONE);
                                                    }
                                                }, 1000);

                                            }

                                        }
                                    });

                        }
                    }
                }
            }

            ;

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        mActionBar = activity.getSupportActionBar();
        mActionBar.setTitle(null);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        if (getActivity() != null) {
            Utils.changeBarSubTitle(getActivity(), mActionBar, "Fame: " + DataUtils.decapitalize(mTranslatedCategory));
        }

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        mActionBar.setHomeAsUpIndicator(upArrow);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mIsOverall) {

        } else {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mActionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mIsOverall) {

        } else {

        }
    }
}
