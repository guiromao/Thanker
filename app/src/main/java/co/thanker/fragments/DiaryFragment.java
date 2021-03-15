package co.thanker.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.thanker.MainActivity;
import co.thanker.R;
import co.thanker.adapters.ThanksDiaryAdapter;
import co.thanker.data.StatsThanks;
import co.thanker.data.Thanks;
import co.thanker.data.ThanksData;
import co.thanker.data.User;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;
import co.thanker.utils.Utils;

public class DiaryFragment extends Fragment {

    private static final String TAG = "DiaryFragment";
    private final String OWN_THANKS_DB = "own-thanks-db";
    private final String COUNTRIES_REFERENCE = "countries-values";
    private final String PLATFORM_MESSAGE = "platform-message";
    private final String THANKS_DATA = "thanks-data";
    private static final String DB_REFERENCE = "users";
    private final String COUNTRY_PREFS = "country-prefs";
    private final int NUMBER_THANKS = 5;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private CollectionReference mThanksCollection;

    private View mView;
    private ListView mListView;
    private ThanksDiaryAdapter mAdapter;
    private RelativeLayout mRelativeTitle;
    private ImageView mImageBook;
    private ImageView mImageInfo;
    private Spinner mSpinnerCategory;
    private EditText mInputThanks;
    private FrameLayout mButtonThanks;
    private TextView mEmptyView;
    private ProgressBar mProgressBar;
    private List<Thanks> mListThanks;
    private List<String> mListThanksKeys;
    private DocumentSnapshot mLastThanksSnapshot;
    private User mUser;
    private ThanksData mThanksData;
    private String mCountry;
    private int mLastAdded;
    private SharedPreferences mSharedCountryPrefs;

    public DiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_diary, container, false);

        initializeViews(mView);
        initializeCategorySpinner();
        initFirebase();

        if (getActivity() != null) {
            mSharedCountryPrefs = getActivity().getSharedPreferences(COUNTRY_PREFS + mAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
            mCountry = mSharedCountryPrefs.getString("country", "");
            Log.v(TAG, "Diary Country: " + mCountry);
        }

        openUserInfo();
        openUserData();

        return mView;
    }

    public void openUserInfo(){
        mFirestore.collection(DB_REFERENCE).document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            mUser = documentSnapshot.toObject(User.class);

                            initListView();
                            setupListViewScroll();
                        }
                    }
                });
    }

    public void openUserData(){
        mFirestore.collection(THANKS_DATA).document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            mThanksData = documentSnapshot.toObject(ThanksData.class);
                        }
                    }
                });
    }

    public void initListView() {
        mListThanks = new ArrayList<>();
        mListThanksKeys = new ArrayList<>();
        mLastAdded = 0;

        Query thanksQuery = mThanksCollection.whereEqualTo("fromUserId", mAuth.getCurrentUser().getUid())
                .orderBy("date", Query.Direction.DESCENDING).limit(NUMBER_THANKS);

        thanksQuery.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot thanksSnapshot : queryDocumentSnapshots) {
                                Thanks thanks = thanksSnapshot.toObject(Thanks.class);
                                mListThanks.add(thanks);
                                mListThanksKeys.add(thanksSnapshot.getId());
                                mLastAdded++;
                            }

                            mLastThanksSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                            Collections.sort(mListThanks, new Comparator<Thanks>() {
                                @Override
                                public int compare(Thanks o1, Thanks o2) {
                                    return Long.compare(o2.getDate(), o1.getDate());
                                }
                            });

                            mAdapter = new ThanksDiaryAdapter(getActivity(), 0, mListThanks, mListThanksKeys, mUser);
                            mListView.setAdapter(mAdapter);
                        } else {
                            //mListView.setEmptyView(mEmptyView);
                        }

                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mThanksCollection = mFirestore.collection(OWN_THANKS_DB);
    }

    public void initializeViews(View view) {
        mListView = view.findViewById(R.id.listview_thanks);
        mRelativeTitle = view.findViewById(R.id.relative_title);
        mImageBook = view.findViewById(R.id.image_book);
        mImageInfo = view.findViewById(R.id.image_info);
        mSpinnerCategory = view.findViewById(R.id.spinner_category);
        mInputThanks = view.findViewById(R.id.input_thanks);
        mButtonThanks = view.findViewById(R.id.button_thank);
        mEmptyView = view.findViewById(R.id.text_empty_view);
        mProgressBar = view.findViewById(R.id.progress_bar);

        Drawable bookDraw = mImageBook.getDrawable();
        bookDraw.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);

        Drawable infoDraw = mImageInfo.getDrawable();
        infoDraw.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);

        mRelativeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle(getActivity().getString(R.string.what_is_diary));
                    builder.setMessage(getActivity().getString(R.string.hint_diary));
                    builder.setPositiveButton(getActivity().getString(R.string.got_it),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                showBottomBar();
            }
        });

        mInputThanks.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //hideBottomBar();
                return false;
            }
        });

        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showBottomBar();
                return false;
            }
        });

        mInputThanks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() == 0){
                    mButtonThanks.setEnabled(false);
                    mButtonThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_grey));
                }
                else {
                    mButtonThanks.setEnabled(true);
                    mButtonThanks.setBackground(getActivity().getResources().getDrawable(R.drawable.button_rounded_green));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mButtonThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = mInputThanks.getText().toString().trim();
                String category = DataUtils.translateToEnglish(getActivity(), mSpinnerCategory.getSelectedItem().toString());
                Thanks thanks = new Thanks(mAuth.getCurrentUser().getUid(), null, description, System.currentTimeMillis(), category, null, DataUtils.generateYear(), DataUtils.generateMonth(),
                        DataUtils.generateDay(), mCountry, "NORMAL", true);
                mThanksCollection.add(thanks).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        if(documentReference != null){
                            String key = documentReference.getId();
                            //documentReference.update("thanksId", key);
                            mInputThanks.setText("");
                            mSpinnerCategory.setSelection(0);
                            showBottomBar();

                            mListThanks.add(0, thanks);
                            mListThanksKeys.add(0, key);

                            if (mListThanks.size() == 1) {
                                mAdapter = new ThanksDiaryAdapter(getActivity(), 0, mListThanks, mListThanksKeys, mUser);
                                mListView.setAdapter(mAdapter);
                            } else {
                                mAdapter.notifyDataSetChanged();
                            }

                            mListView.smoothScrollToPosition(0);

                            long diaryThanks = mThanksData.getDiaryThanks() + 1;
                            mThanksData.setDiaryThanks(diaryThanks);
                            mFirestore.collection(THANKS_DATA).document(mUser.getUserId()).update("diaryThanks", diaryThanks);

                            long categoryValue = mUser.getRightCategoryValue(category) + 1;
                            String categoryString = DataUtils.categoryToString(category);
                            mUser.addValueOnCategory(1, category);
                            mFirestore.collection(DB_REFERENCE).document(mUser.getUserId()).update(categoryString, categoryValue);

                            List<Long> recentThanks = mUser.getRecentThanks();
                            recentThanks.add(thanks.getDate());
                            mUser.setRecentThanks(recentThanks);
                            mFirestore.collection(DB_REFERENCE).document(mUser.getUserId()).update("recentThanks", recentThanks);

                            showThanksAnimation(thanks);
                            checkIfMessageUpdateThanksCount();

                            mFirestore.runTransaction(new Transaction.Function<Void>() {
                                @Nullable
                                @Override
                                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                    Log.v(TAG, "Diary Country. Now entering query. Country: " + mCountry);
                                    Query countryQuery = mFirestore.collection(COUNTRIES_REFERENCE)
                                            .whereEqualTo("country", mCountry)
                                            .whereEqualTo("year", DataUtils.generateYear())
                                            .whereEqualTo("month", DataUtils.generateMonth())
                                            .whereEqualTo("day", DataUtils.generateDay())
                                            .limit(1);

                                    countryQuery
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    StatsThanks stats = new StatsThanks();
                                                    stats.setValueOnCategory(category, 1, thanks.getThanksType());
                                                    StatsThanks existingStats;
                                                    StatsThanks writeStats = stats;

                                                    if (queryDocumentSnapshots.size() > 0) {
                                                        for (QueryDocumentSnapshot statsSnapshot : queryDocumentSnapshots) {
                                                            String docKey = statsSnapshot.getId();
                                                            existingStats = statsSnapshot.toObject(StatsThanks.class);
                                                            existingStats.addStatsThanksOf(stats);
                                                            writeStats = existingStats;
                                                            mFirestore.collection(COUNTRIES_REFERENCE).document(docKey).set(writeStats);
                                                        }
                                                    } else {
                                                        mFirestore.collection(COUNTRIES_REFERENCE).add(writeStats).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                if(documentReference != null){
                                                                    documentReference.update("country", mCountry);
                                                                }
                                                            }
                                                        });
                                                    }

                                                    Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading Stats Thanks of today, in DiaryFragment");
                                                }

                                            });

                                    return null;
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    public void checkIfMessageUpdateThanksCount() {
        if (getActivity() != null) {
            String title = getActivity().getString(R.string.upped_level);
            String body = "";
            boolean sendMessage = false;
            double averageThanks = mUser.getNumberRecentThanks() / 5; //the average of the last 5 days
            String levelString = mUser.getThankerLevel();

            Log.v(TAG, "Checking up level situations. Recent Thanks Count (5 days): " + (mUser.getNumberRecentThanks()) + ". Average/day: " + averageThanks);

            if (levelString != null) {
                if (!levelString.equals("")) {

                    if (levelString.equalsIgnoreCase("starter") && averageThanks >= 2) {
                        sendMessage = true;
                        body = getActivity().getString(R.string.upped_to_walker);
                    } else if (levelString.equalsIgnoreCase("walker") && averageThanks >= 5) {
                        sendMessage = true;
                        body = getActivity().getString(R.string.upped_to_explorer);
                    } else if (levelString.equalsIgnoreCase("explorer") && averageThanks >= 7) {
                        sendMessage = true;
                        body = getActivity().getString(R.string.upped_to_true);
                    } else if (levelString.equalsIgnoreCase("true") && averageThanks >= 10) {
                        sendMessage = true;
                        body = getActivity().getString(R.string.upped_to_master);
                    }

                    if (sendMessage) {
                        DataUtils.createMessage(mUser.getUserId(), title, body, PLATFORM_MESSAGE, DataUtils.MSG_SEE_PREMIUM);
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showThanksAnimation(Thanks thanks) {

        if (getActivity() != null) {

            View thanksView = Utils.getThanksView(getActivity(), thanks);
            ImageView imageThanks = thanksView.findViewById(R.id.fullimage);

            PopupWindow pw = new PopupWindow(thanksView, 480, 480, true);
            pw.showAtLocation(mView, Gravity.CENTER, 0, 0);
            Glide.with(getActivity()).load(ImageUtils.getThanksAnimation(getActivity(), thanks)).into(imageThanks);
            imageThanks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.dismiss();
                }
            });
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pw.dismiss();
                }
            }, 5000);
        }
    }

    public void showBottomBar() {
        ((MainActivity) getActivity()).setNavigationVisibility(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                getDps(88)
        );
        params.setMargins(0, 0, 0, getDps(56));
        //mLinearBottom.setLayoutParams(params);
    }

    public void hideBottomBar() {
        ((MainActivity) getActivity()).setNavigationVisibility(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                getDps(88)
        );
        params.setMargins(0, 0, 0, 0);


        //mLinearBottom.setLayoutParams(params);
    }

    public int getDps(int measure) {
        Resources r = getActivity().getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                measure,
                r.getDisplayMetrics()
        );

        return px;
    }

    public void initializeCategorySpinner() {
        List<String> categoriesPrimary = new ArrayList<>();

        for (User.Category cat : User.Category.values()) {
            if (cat != User.Category.BLANK) {
                String category = cat.toString();

                if (category.equalsIgnoreCase("Person")) {
                    category = getString(R.string.person);
                } else if (category.equalsIgnoreCase("Brand")) {
                    category = getString(R.string.brand);
                } else if (category.equalsIgnoreCase("Association")) {
                    category = getString(R.string.association);
                } else {
                    category = DataUtils.translateAndFormat(getActivity(), cat.toString());
                }

                categoriesPrimary.add(category);
            }
        }

        ArrayAdapter<User.Category> categoriesPrimaryAdapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, categoriesPrimary);

        mSpinnerCategory.setAdapter(categoriesPrimaryAdapter);
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

                    newQuery = mThanksCollection.whereEqualTo("fromUserId", mAuth.getCurrentUser().getUid())
                            .orderBy("date", Query.Direction.DESCENDING).startAfter(mLastThanksSnapshot).limit(NUMBER_THANKS);

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
                                                    if(!DataUtils.doesListContainItem(mListThanksKeys, thanksSnapshot.getId())){
                                                        helperList.add(thanks);
                                                        mListThanksKeys.add(thanksSnapshot.getId());
                                                        mLastAdded++;
                                                        Log.v(TAG, "Adding thanks ID: " + thanksSnapshot.getId());
                                                    }

                                                    Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading Thanks document, onScroll");
                                                }

                                                if (helperList.size() > 0) {
                                                    Collections.sort(helperList, new Comparator<Thanks>() {
                                                        @Override
                                                        public int compare(Thanks o1, Thanks o2) {
                                                            return Long.compare(o2.getDate(), o1.getDate());
                                                        }
                                                    });

                                                    for (Thanks thanks : helperList) {
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
        });

    }


    @Override
    public void onResume() {
        super.onResume();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle(null);

        if (getActivity() != null) {
            Utils.changeBarTitle(getActivity(), actionBar, getActivity().getString(R.string.hint_diary_title));
        }

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

    }

    @Override
    public void onPause() {
        super.onPause();

        ((MainActivity) getActivity()).setNavigationVisibility(true);
    }

}
