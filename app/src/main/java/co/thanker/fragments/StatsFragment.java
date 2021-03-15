package co.thanker.fragments;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.blongho.country_data.World;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.math.Stats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.thanker.R;
import co.thanker.adapters.StatsAdapter;
import co.thanker.data.StatsThanks;
import co.thanker.data.ThanksValueStats;
import co.thanker.utils.DataUtils;

public class StatsFragment extends Fragment {

    private final String COUNTRIES_REFERENCE = "countries-values";
    private final String ALL_ITEMS = "ALL";
    private final String TAG = "StatsFragment";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String INFO_HAS_PREMIUM = "info-has-premium";
    private final String SAVED_STATS = "saved-stats";
    private final String SAVED_COUNTRY = "saved-country";
    private final String SAVED_YEAR = "saved-year";
    private final String SAVED_MONTH = "saved-month";
    private final String SAVED_DAY = "saved-day";
    private final int MAX_DB_READS_FOR_UPDATING_SPINNERS = 3;
    private final long ONE_DAY = 86400000;
    private final long SEVEN_DAYS = 604800000;
    private final long THIRTY_DAYS = 2592000000L;
    private final long ONE_YEAR = 31556952000L;

    private Spinner mCountriesSpinner;
    private Spinner mTimeSpinner;

    private FirebaseFirestore mFirestore;
    private DatabaseReference mCountriesRef;
    private DatabaseReference mMonthRef;
    private FirebaseAuth mAuth;

    private ListView mListViewThanks;
    private Button mCheckButton;
    private TextView mTextTypeStats;
    private ImageView mFlag;
    private LinearLayout mEmptyView;
    private TextView mEmptyViewText;

    private List<String> mTranslatedCountries;
    private List<ThanksValueStats> mThanksStats;
    private StatsAdapter mStatsAdapter;

    private int mNumberValueListeners;

    private String mCurrentDaySelection;
    private String mPreviousDaySelection;
    private String mCurrentMonthSelection;
    private String mPreviousMonthSelection;
    private String mCurrentYearSelection;
    private String mPreviousYearSelection;
    private String mCurrentCountrySelection;
    private String mPreviousCountrySelection;
    private String mSelectedCountry;
    private String mSelectedYear;
    private String mSelectedMonth;
    private String mSelectedDay;
    private String mDateCode;
    private boolean mToLoadData;
    private boolean mLockFields;
    private int mCounterHelper;

    private String mUserCountry;
    private String mEnglishCountry;
    private long mTimeSpan;
    private boolean mIsYesterday;
    private Bundle mSavedInstanceState;
    private boolean mIsPremium;

    public String getOriginalMonth(String translated) {
        String noResult = "";

        if (getActivity() != null) {
            Resources engResources = DataUtils.getEnglishResources(getActivity());
            String[] engMonths = engResources.getStringArray(R.array.months_array);
            String[] ourMonths = getActivity().getResources().getStringArray(R.array.months_array);

            for (int i = 0; i != ourMonths.length - 1; i++) {
                if (translated.equalsIgnoreCase(ourMonths[i])) {
                    Log.v(TAG, "Selected month. (getOriginal) Found translated: " + ourMonths[i] + ". Original: " + engMonths[i]);
                    return engMonths[i];
                }
            }
        }

        return noResult;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        mNumberValueListeners = 0;
        mToLoadData = true;
        mLockFields = false;
        mCounterHelper = 0;
        mIsPremium = false;

        mListViewThanks = (ListView) view.findViewById(R.id.listview_stats);
        mCheckButton = (Button) view.findViewById(R.id.check_stats_button);
        mTextTypeStats = (TextView) view.findViewById(R.id.text_type_search);
        mFlag = (ImageView) view.findViewById(R.id.image_flag);
        mEmptyView = (LinearLayout) view.findViewById(R.id.empty_view_stats);
        mEmptyViewText = (TextView) view.findViewById(R.id.text_empty_view_stats);

        World.init(getActivity());

        if (getArguments() != null) {
            mUserCountry = getArguments().getString(OUR_USER_COUNTRY);
            mEnglishCountry = DataUtils.getEnglishCountry(getActivity(), mUserCountry);
            mIsPremium = getArguments().getBoolean(INFO_HAS_PREMIUM);
            int countryValue = World.getFlagOf(mEnglishCountry.toLowerCase());
            mFlag.setImageResource(countryValue);
            Log.v(TAG, "Updating Stats. Inside StatsFragment. Variable was passed: " + mUserCountry);
        }

        setupFirebase();
        initViews(view);
        //initializeClickActions();

        if (savedInstanceState != null) {
            mSavedInstanceState = savedInstanceState;
            mThanksStats = (List<ThanksValueStats>) savedInstanceState.getSerializable(SAVED_STATS);
            int timePos = savedInstanceState.getInt("saved-time");
            String country = DataUtils.getTranslatedCountry(getActivity(), savedInstanceState.getString(SAVED_COUNTRY));
            String timeLabel = savedInstanceState.getString("time-label");

            mTimeSpinner.setSelection(timePos);
            mCountriesSpinner.setSelection(DataUtils.retrieveItem(mTranslatedCountries, country));
            mTextTypeStats.setText(timeLabel);
            mTextTypeStats.setVisibility(View.VISIBLE);

            mStatsAdapter = new StatsAdapter(getActivity(), mThanksStats);
            mListViewThanks.setAdapter(mStatsAdapter);
        }

        return view;
    }

    public void setupFirebase() {
        mFirestore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
    }


    public void initViews(View v) {
        mCountriesSpinner = (Spinner) v.findViewById(R.id.spinner_stats_country);
        mTimeSpinner = (Spinner) v.findViewById(R.id.spinner_stats_time);

        initializeCountrySpinner();
        initializeTimespansSpinner();

        mCountriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mEnglishCountry = DataUtils.getEnglishCountry(getActivity(), mCountriesSpinner.getSelectedItem().toString());
                int countryValue = World.getFlagOf(mEnglishCountry.toLowerCase());
                mFlag.setImageResource(countryValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        mCheckButton.setEnabled(true);
                        mCheckButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_green));
                        mTimeSpan = getTimespan(System.currentTimeMillis());
                        mIsYesterday = false;
                        break;

                    case 1:
                        mCheckButton.setEnabled(true);
                        mCheckButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_green));
                        mTimeSpan = getTimespan(System.currentTimeMillis() - ONE_DAY);
                        mIsYesterday = true;
                        break;

                    case 2:
                        mCheckButton.setEnabled(true);
                        mCheckButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_green));
                        mTimeSpan = getTimespan(System.currentTimeMillis() - SEVEN_DAYS);
                        mIsYesterday = false;
                        break;

                    case 3:
                        if(mIsPremium){
                            mCheckButton.setEnabled(true);
                            mCheckButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_green));
                            mTimeSpan = getTimespan(System.currentTimeMillis() - THIRTY_DAYS);
                            mIsYesterday = false;
                        }

                        else {
                            mCheckButton.setEnabled(false);
                            mCheckButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_grey));
                            Toast.makeText(getActivity(), getActivity().getString(R.string.must_be_premium_to_see_these_stats), Toast.LENGTH_LONG).show();
                        }

                        break;

                    case 4:
                        if(mIsPremium){
                            mCheckButton.setEnabled(true);
                            mCheckButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_green));
                            mTimeSpan = getTimespan(System.currentTimeMillis() - ONE_YEAR);
                            mIsYesterday = false;
                        }

                        else {
                            mCheckButton.setEnabled(false);
                            mCheckButton.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_grey));
                            Toast.makeText(getActivity(), getActivity().getString(R.string.must_be_premium_to_see_these_stats), Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){

                    mTextTypeStats.setText(mTimeSpinner.getSelectedItem().toString());
                    mTextTypeStats.setVisibility(View.VISIBLE);

                    Query statsQuery;
                    if(!mIsYesterday){
                        statsQuery = mFirestore.collection(COUNTRIES_REFERENCE).whereEqualTo("country", mEnglishCountry)
                                .whereGreaterThan("date", mTimeSpan);
                    }
                    else {
                        statsQuery = mFirestore.collection(COUNTRIES_REFERENCE).whereEqualTo("country", mEnglishCountry)
                                .whereGreaterThan("date", mTimeSpan)
                                .whereLessThan("date", (mTimeSpan + ONE_DAY));
                    }

                    statsQuery
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(queryDocumentSnapshots.size() > 0){
                                        mThanksStats = new ArrayList<>();
                                        StatsThanks stats = new StatsThanks();

                                        for(QueryDocumentSnapshot statsSnapshot: queryDocumentSnapshots){
                                            StatsThanks newStats = statsSnapshot.toObject(StatsThanks.class);
                                            stats.addStatsThanksOf(newStats);
                                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading StatsThanks for StatsFragment\'s ListView");
                                        }

                                        mThanksStats.add(new ThanksValueStats("People", 0, stats.getPersonThanks()));
                                        mThanksStats.add(new ThanksValueStats("Brands", 0, stats.getBrandThanks()));
                                        mThanksStats.add(new ThanksValueStats("Business", 0, stats.getBusinessThanks()));
                                        mThanksStats.add(new ThanksValueStats("Nature", 0, stats.getNatureThanks()));
                                        mThanksStats.add(new ThanksValueStats("Health", 0, stats.getHealthThanks()));
                                        mThanksStats.add(new ThanksValueStats("Food", 0, stats.getFoodThanks()));
                                        mThanksStats.add(new ThanksValueStats("Associations", 0, stats.getAssociationThanks()));
                                        mThanksStats.add(new ThanksValueStats("Home", 0, stats.getHomeThanks()));
                                        mThanksStats.add(new ThanksValueStats("Science", 0, stats.getScienceThanks()));
                                        mThanksStats.add(new ThanksValueStats("Religion", 0, stats.getReligionThanks()));
                                        mThanksStats.add(new ThanksValueStats("Sports", 0, stats.getSportsThanks()));
                                        mThanksStats.add(new ThanksValueStats("Lifestyle", 0, stats.getLifestyleThanks()));
                                        mThanksStats.add(new ThanksValueStats("Technology", 0, stats.getTechThanks()));
                                        mThanksStats.add(new ThanksValueStats("Fashion", 0, stats.getFashionThanks()));
                                        mThanksStats.add(new ThanksValueStats("Education", 0, stats.getEducationThanks()));
                                        mThanksStats.add(new ThanksValueStats("Games", 0, stats.getGamesThanks()));
                                        mThanksStats.add(new ThanksValueStats("Travel", 0, stats.getTravelThanks()));
                                        mThanksStats.add(new ThanksValueStats("Institutional", 0, stats.getGovThanks()));
                                        mThanksStats.add(new ThanksValueStats("Beauty", 0, stats.getBeautyThanks()));
                                        mThanksStats.add(new ThanksValueStats("Culture", 0, stats.getCultureThanks()));
                                        mThanksStats.add(new ThanksValueStats("Finance", 0, stats.getFinanceThanks()));

                                        sortThanksValuesStats();

                                        mStatsAdapter = new StatsAdapter(getActivity(), mThanksStats);
                                        mListViewThanks.setAdapter(mStatsAdapter);
                                    }

                                    else {
                                        String message = getActivity().getString(R.string.no_stats, DataUtils.getTranslatedCountry(getActivity(), mEnglishCountry));
                                        mEmptyViewText.setText(message);
                                        mListViewThanks.setAdapter(null);
                                        mListViewThanks.setEmptyView(mEmptyView);
                                    }
                                }
                            });
                }
            }
        });
    }

    public long getTimespan(long value){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(value));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return cal.getTimeInMillis();
    }

    public void initializeTimespansSpinner(){
        String [] spans = getActivity().getResources().getStringArray(R.array.times_stats);
        List<String> spansOptions = new ArrayList<>();

        for(String item: spans){
            spansOptions.add(item);
        }

        ArrayAdapter<String> adapterSpans = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, spansOptions);
        mTimeSpinner.setAdapter(adapterSpans);
        mTimeSpinner.setSelection(0);

        mTimeSpan = getTimespan(System.currentTimeMillis());
        mIsYesterday = false;
    }

    public void initializeCountrySpinner() {
        String currentCountry;
        Locale[] locales = Locale.getAvailableLocales();
        mTranslatedCountries = new ArrayList<String>();

        for (Locale loc : locales) {
            String country = loc.getDisplayCountry();
            if ((country.length() > 0) && !(mTranslatedCountries.contains(country))) {
                mTranslatedCountries.add(country);
            }
        }

        Collections.sort(mTranslatedCountries, String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter<String> adapterCountries = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, mTranslatedCountries);
        mCountriesSpinner.setAdapter(adapterCountries);

        currentCountry = DataUtils.getTranslatedCountry(getActivity(),mUserCountry);//DataUtils.getEnglishCountry(getActivity(), mUserCountry);
        Log.v(TAG, "Checking stats Country: " + currentCountry);

        mCountriesSpinner.setSelection(DataUtils.retrieveItem(mTranslatedCountries, currentCountry));
    }


    private void sortThanksValuesStats() {
        Collections.sort(mThanksStats, new Comparator<ThanksValueStats>() {
            @Override
            public int compare(ThanksValueStats o1, ThanksValueStats o2) {
                return Long.compare(o2.getThanksValue(), o1.getThanksValue());
            }
        });

    }

    private String convertTypeToString(String type) {
        String string = "";

        switch (type) {
            case "personThanks":
                string = "People";
                break;
            case "brandThanks":
                string = "Brands";
                break;
            case "businessThanks":
                string = "Business";
                break;
            case "natureThanks":
                string = "Nature";
                break;
            case "healthThanks":
                string = "Health";
                break;
            case "foodThanks":
                string = "Food";
                break;
            case "associationThanks":
                string = "Associations";
                break;
            case "homeThanks":
                string = "Home";
                break;
            case "scienceThanks":
                string = "Science";
                break;
            case "religionThanks":
                string = "Religion";
                break;
            case "sportsThanks":
                string = "Sports";
                break;
            case "lifestyleThanks":
                string = "Lifestyle";
                break;
            case "techThanks":
                string = "Technology";
                break;
            case "fashionThanks":
                string = "Fashion";
                break;
            case "educationThanks":
                string = "Education";
                break;
            case "gamesThanks":
                string = "Games";
                break;
            case "travelThanks":
                string = "Travel";
                break;
            case "govThanks":
                string = "Institutional";
                break;
            case "beautyThanks":
                string = "Beauty";
                break;
            case "cultureThanks":
                string = "Culture";
                break;
            case "financeThanks":
                string = "Finance";
                break;
        }

        return string;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        //Log.v(TAG, "Selected month. Saving month: " + mSelectedMonth);

        if (mThanksStats != null) {
            state.putSerializable(SAVED_STATS, (Serializable) mThanksStats);
            Log.v(TAG, "Saved Instance Stats. Saved current stats");
        } else {
            Log.v(TAG, "Saved Instance Stats. Didn't find stats to save");
        }

        if (mCurrentCountrySelection != null && getActivity() != null) {
            state.putString(SAVED_COUNTRY, DataUtils.getEnglishCountry(getActivity(), mSelectedCountry));
        }

        state.putInt("saved-time", mTimeSpinner.getSelectedItemPosition());
        state.putString("time-label", mTextTypeStats.getText().toString());
    }

    @Override
    public void onResume(){
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);
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
