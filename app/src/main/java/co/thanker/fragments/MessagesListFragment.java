package co.thanker.fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.thanker.R;
import co.thanker.adapters.MessageAdapter;
import co.thanker.data.Message;
import co.thanker.data.Thanks;
import co.thanker.utils.Utils;

public class MessagesListFragment extends Fragment {

    private final String TAG = "MessagesListActivity";
    private final String MESSAGES_REFERENCE = "messages-list";
    private static final String DB_REFERENCE = "users";
    private final String USER_OBJECT = "user-object";
    private final String SAVED_MESSAGES = "saved-messages";
    private final String OUR_PREFS = "our-prefs";
    private final String BOOL_HAS_NEW_MESSAGES = "has-new-messages";
    private final String PLATFORM_MESSAGE = "platform-message";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String INFO_HAS_PREMIUM = "info-has-premium";
    private final int NUMBER_MESSAGES = 12;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private Query mMessagesQuery;
    private DocumentSnapshot mLastMessageSnapshot;
    private String mLastsnapshotString;

    private Context mContext;

    private ListView mListViewMessages;
    private MessageAdapter mAdapter;
    private List<Message> mListMessages;
    private ProgressBar mProgressBar;
    private ActionBar mActionBar;

    private List<String> mMessagesKeys;
    private long mNumberMessages;
    private boolean mHasMessagesOpened = false;
    private boolean mHasNewMessages;
    private String mCountry;
    private int mLastAdded;
    private boolean mIsPremium;

    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mPrefsEditor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_messages_list, container, false);

        if(getActivity() != null){
            mContext = getActivity();
        }

        Bundle msgBundle = getArguments();
        if(msgBundle != null){
            mHasNewMessages = msgBundle.getBoolean(BOOL_HAS_NEW_MESSAGES, false);
            mCountry = msgBundle.getString(OUR_USER_COUNTRY);
            mIsPremium = msgBundle.getBoolean(INFO_HAS_PREMIUM, false);
        }

        setupFirebase();
        initViews(view);

        mSharedPref = mContext.getSharedPreferences(OUR_PREFS + mAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
        mPrefsEditor = mSharedPref.edit();

        retrieveMessagesInPreferences();

        mLastAdded = 0;

        if(savedInstanceState != null){
            Log.v(TAG, "Entering new messages. Entered savedInstance");
            mNumberMessages = savedInstanceState.getLong("number-messages");
            mListMessages = (List<Message>) savedInstanceState.getSerializable("messages-list");
            mMessagesKeys = savedInstanceState.getStringArrayList("messages-keys");
            Log.v(TAG, "Getting message keys. Size of list: " + mMessagesKeys.size());
            //mLastMessageSnapshot = (DocumentSnapshot) savedInstanceState.getSerializable("last-msg-snapshot");
            mAdapter = new MessageAdapter(mContext, 0, mListMessages, mMessagesKeys, mAuth.getCurrentUser().getUid(), mCountry, mIsPremium);
            mListViewMessages.setAdapter(mAdapter);
            mProgressBar.setVisibility(View.GONE);
            mMessagesKeys = new ArrayList<>();
            mLastAdded = savedInstanceState.getInt("last-added", 0);
            mLastsnapshotString = savedInstanceState.getString("snapshot-string");
        }

        else {
            Log.v(TAG, "Entering new messages. Entered else (doesn\'t have savedInstance)");
            mMessagesQuery.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.size() > 0){
                                Log.v(TAG, "Entering new messages. Entered query");
                                mListMessages = new ArrayList<>();
                                mMessagesKeys = new ArrayList<>();
                                for(QueryDocumentSnapshot messageSnapshot: queryDocumentSnapshots){
                                    Message message = messageSnapshot.toObject(Message.class);
                                    String msgKey = messageSnapshot.getId();
                                    mListMessages.add(message);
                                    mMessagesKeys.add(msgKey);
                                    mLastAdded++;
                                    Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading new Message");
                                }
                                Log.v(TAG, "Checking Messages Keys list size: " + mMessagesKeys.size());

                                if(mListMessages.size() > 0){
                                    Collections.sort(mListMessages, new Comparator<Message>() {
                                        @Override
                                        public int compare(Message o1, Message o2) {
                                            return Long.compare(o2.getDate(), o1.getDate());
                                        }
                                    });
                                    mLastMessageSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                    mLastsnapshotString = mLastMessageSnapshot.getId();
                                    mAdapter = new MessageAdapter(mContext, 0, mListMessages, mMessagesKeys, mAuth.getCurrentUser().getUid(), mCountry, mIsPremium);
                                    mListViewMessages.setAdapter(mAdapter);
                                }
                            }
                            mProgressBar.setVisibility(View.GONE);
                        }
                    })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.v(TAG, "Entering new messages. Couldn\'t read query");
                }
            });
        }

        setupListViewScroll();

        return view;
    }

    public void setupFirebase(){
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mMessagesQuery = mFirestore.collection(MESSAGES_REFERENCE).whereEqualTo("toUserId", mAuth.getCurrentUser().getUid())
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(NUMBER_MESSAGES);
    }

    public void initViews(View v){
        mListViewMessages = (ListView) v.findViewById(R.id.list_messages);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
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
            private int countScrolls = 0;


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

                    if(mLastAdded > 0 && mLastMessageSnapshot != null){
                        mProgressBar.setVisibility(View.VISIBLE);
                        mLastAdded = 0;
                        newQuery = mMessagesQuery.startAfter(mLastMessageSnapshot);
                        Log.v(TAG, "Seeing messages. mListMessages size: " + mListMessages.size() + ", Number of Messages: " + mNumberMessages);
                        List <Message> newMessages = new ArrayList<>();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                newQuery.get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if(queryDocumentSnapshots.size() > 0){
                                                    for(QueryDocumentSnapshot messageSnapshot: queryDocumentSnapshots){
                                                        Message message = messageSnapshot.toObject(Message.class);
                                                        String key = messageSnapshot.getId();
                                                        newMessages.add(message);
                                                        mMessagesKeys.add(key);
                                                        mLastAdded++;
                                                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading new Message, MessagesListFrament onScroll");
                                                    }

                                                    if(newMessages.size() > 0){
                                                        Collections.sort(newMessages, new Comparator<Message>() {
                                                            @Override
                                                            public int compare(Message o1, Message o2) {
                                                                return Long.compare(o2.getDate(), o1.getDate());
                                                            }
                                                        });
                                                        mListMessages.addAll(newMessages);
                                                        mLastMessageSnapshot = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                                        mLastsnapshotString = mLastMessageSnapshot.getId();
                                                        mAdapter.notifyDataSetChanged();
                                                    }

                                                }
                                                mProgressBar.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        }, 1000);
                    }
                }
            };

        });
    }

    public boolean doesListContainDate(List<Message> list, Message message){
        for(Message item: list){
            if(item.getDate() == message.getDate()){
                return true;
            }
        }

        return false;
    }

    public long lastMessageDate(){
        long date = mListMessages.get(0).getDate();

        for(Message item: mListMessages){
            if(item.getDate() < date){
                date = item.getDate();
            }
        }

        return date;
    }

    private void addAllKeys(){
        mMessagesKeys = new ArrayList<>();
        for(Message message: mListMessages){
            mMessagesKeys.add(message.getKey());
        }
    }

    public void savedMessagesInPreferences(){
        /*
        Gson gson = new Gson();

        String jsonMessages = gson.toJson(mListMessages);
        Log.v(TAG, "Retrieving messages. Saving messages");

        mPrefsEditor.putString(SAVED_MESSAGES, jsonMessages);
        mPrefsEditor.commit(); */
    }

    public void retrieveMessagesInPreferences(){
        /*Gson gson = new Gson();

        String savedMessages = mSharedPref.getString(SAVED_MESSAGES, "");

        if(savedMessages.length() > 1){
            Log.v(TAG, "Retrieving messages. There are messages!");
            mListMessages = gson.fromJson(savedMessages, new TypeToken<List<Message>>(){}.getType());
            mHasMessagesOpened = true;
            mLastMessageKey = mListMessages.get(mListMessages.size() - 1).getKey();
            addAllKeys();
            mAdapter = new MessageAdapter(this, 0, mListMessages, mAuth.getCurrentUser().getUid());
            mListViewMessages.setAdapter(mAdapter);
            mProgressBar.setVisibility(View.GONE);
        }
        else {
            Log.v(TAG, "Retrieving messages. There aren't messages saved!");
            mHasMessagesOpened = false;
        }*/
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("messages-list", (Serializable) mListMessages);
        outState.putStringArrayList("messages-keys", (ArrayList) mMessagesKeys);
        outState.putLong("number-messages", mNumberMessages);
        outState.putInt("last-added", mLastAdded);
        outState.putString("snapshot-string", mLastsnapshotString);
        //outState.putSerializable("last-msg-snapshot", (Serializable) mLastMessageSnapshot);
    }

    @Override
    public void onResume(){
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        mActionBar = activity.getSupportActionBar();
        mActionBar.setSubtitle(null);
        //mActionBar.setDisplayHomeAsUpEnabled(true);

        if(getActivity() != null){
            Utils.changeBarTitle(getActivity(), mActionBar, getActivity().getString(R.string.messages));
        }

        /*final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        mActionBar.setHomeAsUpIndicator(upArrow);*/
    }

    @Override
    public void onStart(){
        super.onStart();

        if(!mHasMessagesOpened || mHasNewMessages){
            Log.v(TAG, "Retrieving messages. Going to read new messages");
            //mMessagesQuery.addListenerForSingleValueEvent(mRecentMessagesListener);
        }
        else {
            Log.v(TAG, "Retrieving messages. Not reading new messages");
        }

    }

    @Override
    public void onPause(){
        super.onPause();
        //mActionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onStop(){
        super.onStop();

        savedMessagesInPreferences();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        //mPrefsEditor.clear().commit();
    }
}

