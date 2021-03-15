package co.thanker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import co.thanker.adapters.ThanksListAdapter;
import co.thanker.data.Thanks;
import co.thanker.data.User;
import utils.DataUtils;

public class ThanksActivity extends AppCompatActivity {

    private final String TAG = "ThanksActivity";
    private static final String DB_REFERENCE = "users-test";
    private static final String THANKS_REFERENCE = "thanks-test";
    private final String THANKS_GIVEN = "thanks-given";
    private final String THANKS_RECEIVED = "thanks-received";
    private final String DYNAMIC_GIVER = "dynamic-giver";
    private final String DYNAMIC_RECEIVER = "dynamic-receiver";
    private final String THANKS_DYNAMIC = "thanks-dynamic";
    private final String USER_OBJECT = "user-object";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserRef;
    private DatabaseReference mTotalThanksRef;
    private Query mThanksQuery;

    private User mUser;
    private ListView mListView;
    private ThanksListAdapter mAdapter;
    private List<Thanks> mListThanks;
    private List<String> mThanksIds;
    private String mLastThanksId;
    private String mDescription;
    private String mDynamic;
    private ActionBar mActionBar;
    private ProgressBar mProgressBar;

    private long mNumberThanks;

    private ValueEventListener mCountThanksListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.hasChildren()){
                mNumberThanks = dataSnapshot.getChildrenCount();
                Log.v(TAG, "Last Thanks ID thing. Total Thanks: " + mNumberThanks);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanks);

        mListView = (ListView) findViewById(R.id.list_thanks);
        mActionBar = getSupportActionBar();
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Intent gotIntent = getIntent();

        if(gotIntent != null){
            Log.v(TAG, "Thanks full listing. Got intent!");

            Bundle bundle = gotIntent.getExtras();
            mUser = (User) getIntent().getSerializableExtra(USER_OBJECT);
            mDynamic = gotIntent.getStringExtra(THANKS_DYNAMIC);

            Log.v(TAG, "Thanks full listing. User name got: " + mUser.getName());
            Log.v(TAG, "Thanks full listing. Dynamic type received: " + mDynamic);
        }

        setupFirebase();

        if(!mDynamic.equals("")){

            final Query thanksQuery;
            String dynamic;

            if(mDynamic.equals(DYNAMIC_GIVER)){
                mThanksQuery = mDatabase.getReference().child(DB_REFERENCE).child(mUser.getUserId())
                    .child(THANKS_GIVEN).limitToLast(10);
                dynamic = DYNAMIC_GIVER;
                mDescription = getString(R.string.thanks_given_by) + " " + DataUtils.capitalize(mUser.getName());
            }

            else {
                mThanksQuery = mDatabase.getReference().child(DB_REFERENCE).child(mUser.getUserId())
                    .child(THANKS_RECEIVED).limitToLast(10);
                dynamic = DYNAMIC_RECEIVER;
                mDescription = getString(R.string.thanks_given_to) + " " + DataUtils.capitalize(mUser.getName());
            }

            mActionBar.setTitle(null);
            mActionBar.setSubtitle(mDescription);

            final String dynamicConstant = dynamic;

            mThanksQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        mListThanks = new ArrayList<>();
                        mThanksIds = new ArrayList<>();
                        for(DataSnapshot thanksData: dataSnapshot.getChildren()){
                            Thanks thanks = thanksData.getValue(Thanks.class);
                            if(thanks != null){
                                mLastThanksId = thanksData.getKey();
                                mThanksIds.add(mLastThanksId);
                                mListThanks.add(thanks);
                                Log.v(TAG, "Last Thanks ID: " + mLastThanksId);
                            }
                        }
                        if(mListThanks.size() > 0){
                            DataUtils.sortThanksByDateDesc(mListThanks);
                            mAdapter = new ThanksListAdapter(getApplication(), 0, mListThanks, mUser, dynamicConstant);
                            mListView.setAdapter(mAdapter);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            setupListViewScroll();
        }

        else {

            //if a description of the type of Thanks listing hasn't been provided, end Activity
            finish();
        }
    }

    public void setupFirebase(){
        mDatabase = FirebaseDatabase.getInstance();
        mUserRef = mDatabase.getReference().child(DB_REFERENCE).child(mUser.getUserId());

        if(mDynamic != null){
            if(mDynamic.equals(DYNAMIC_GIVER)){
                mTotalThanksRef = mUserRef.child(THANKS_GIVEN);
            }
            else {
                mTotalThanksRef = mUserRef.child(THANKS_RECEIVED);
            }
        }
    }

    public void setupListViewScroll(){
        mListView.setOnScrollListener(new ListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;
            private LinearLayout lBelow;
            private Query newQuery;
            private long count = 0;


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
                    if(mDynamic.equals(DYNAMIC_GIVER)){
                        newQuery = mUserRef.child(THANKS_GIVEN).orderByKey().endAt(mLastThanksId).limitToFirst(10);
                    }
                    else {
                        newQuery = mUserRef.child(THANKS_RECEIVED).orderByKey().endAt(mLastThanksId).limitToFirst(10);
                    }

                    if(mListThanks.size() < mNumberThanks){
                        mProgressBar.setVisibility(View.VISIBLE);
                        newQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {

                                    mLastThanksId = child.getKey();
                                    Thanks t = child.getValue(Thanks.class);
                                    if(!DataUtils.doesListContainItem(mThanksIds, mLastThanksId)){
                                        mListThanks.add(t);
                                        mThanksIds.add(mLastThanksId);
                                    }
                                }
                                count += 10;
                                DataUtils.sortThanksByDateDesc(mListThanks);
                                mAdapter = new ThanksListAdapter(getApplicationContext(), 0, mListThanks, mUser, mDynamic);
                                mListView.setAdapter(mAdapter);
                                mListView.setSelection((int)count);

                                mProgressBar.setVisibility(View.GONE);

                                newQuery.removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }
            };

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onStart(){
        super.onStart();

        if(mDynamic != null){
            mTotalThanksRef.addValueEventListener(mCountThanksListener);
        }
    }

    public void onStop(){
        super.onStop();

        if(mDynamic != null){
            mTotalThanksRef.removeEventListener(mCountThanksListener);
        }
    }

}
