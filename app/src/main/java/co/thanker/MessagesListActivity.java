package co.thanker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.thanker.adapters.MessageAdapter;
import co.thanker.adapters.ThanksListAdapter;
import co.thanker.data.Message;
import co.thanker.data.Thanks;
import co.thanker.data.User;
import utils.DataUtils;

public class MessagesListActivity extends AppCompatActivity {

    private final String TAG = "MessagesListActivity";
    private final String MESSAGES_REFERENCE = "messages-list";
    private static final String DB_REFERENCE = "users-test";
    private final String USER_OBJECT = "user-object";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mMessagesReference;
    private Query mMessagesQuery;

    private Context mContext;

    private ListView mListViewMessages;
    private MessageAdapter mAdapter;
    private List<Message> mListMessages;
    private ProgressBar mProgressBar;
    private ActionBar mActionBar;

    private List<String> mMessagesKeys;
    private String mLastMessageKey;
    private long mNumberMessages;

    private ValueEventListener mCountMessagesListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.hasChildren()){
                mNumberMessages = dataSnapshot.getChildrenCount();
                Log.v(TAG, "Last Thanks ID thing. Total Thanks: " + mNumberMessages);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener mRecentMessagesListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.hasChildren()){
                mListMessages = new ArrayList<>();
                mMessagesKeys = new ArrayList<>();
                for(DataSnapshot messageData: dataSnapshot.getChildren()){
                    Message message = messageData.getValue(Message.class);
                    mLastMessageKey = messageData.getKey();
                    mMessagesKeys.add(mLastMessageKey);
                    mListMessages.add(message);
                }

                Collections.sort(mListMessages, new Comparator<Message>() {
                    @Override
                    public int compare(Message o1, Message o2) {
                        return Long.compare(o2.getDate(), o1.getDate());
                    }
                });

                mAdapter = new MessageAdapter(mContext, 0, mListMessages, mAuth.getCurrentUser().getUid());
                mListViewMessages.setAdapter(mAdapter);
            }
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);

        mContext = getApplicationContext();

        mActionBar = getSupportActionBar();
        mActionBar.setTitle(R.string.thanker_messages);

        setupFirebase();
        initViews();

        setupListViewScroll();
    }

    public void setupFirebase(){
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mMessagesReference = mDatabase.getReference().child(DB_REFERENCE).child(mAuth.getCurrentUser().getUid()).child(MESSAGES_REFERENCE);
        mMessagesQuery = mMessagesReference.limitToLast(10);
    }

    public void initViews(){
        mListViewMessages = (ListView) findViewById(R.id.list_messages);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    public void setupListViewScroll(){
        mListViewMessages.setOnScrollListener(new ListView.OnScrollListener() {
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

                    newQuery = mMessagesReference.endAt(mLastMessageKey).limitToFirst(10);

                    if(mListMessages.size() < mNumberMessages){
                        mProgressBar.setVisibility(View.VISIBLE);
                        newQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {

                                    mLastMessageKey = child.getKey();
                                    Message m = child.getValue(Message.class);
                                    if(!DataUtils.doesListContainItem(mMessagesKeys, mLastMessageKey)){
                                        mListMessages.add(m);
                                        mMessagesKeys.add(mLastMessageKey);
                                    }
                                }
                                count += 10;
                                Collections.sort(mListMessages, new Comparator<Message>() {
                                    @Override
                                    public int compare(Message o1, Message o2) {
                                        return Long.compare(o2.getDate(), o1.getDate());
                                    }
                                });
                                mAdapter = new MessageAdapter(getApplicationContext(), 0, mListMessages, mAuth.getCurrentUser().getUid());
                                mListViewMessages.setAdapter(mAdapter);
                                mListViewMessages.setSelection((int)count);

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

        mMessagesQuery.addListenerForSingleValueEvent(mRecentMessagesListener);
        mMessagesReference.addValueEventListener(mCountMessagesListener);
    }

    @Override
    public void onStop(){
      super.onStop();

      mMessagesQuery.removeEventListener(mRecentMessagesListener);
      mMessagesReference.removeEventListener(mCountMessagesListener);
    }
}
