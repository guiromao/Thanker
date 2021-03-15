package co.thanker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import co.thanker.adapters.ThanksAdapter;
import co.thanker.data.Thanks;

public class SeeFullThanksActivity extends AppCompatActivity {

    private final int USER_READ_THRESHOLD = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_full_thanks);
    }

   /* public void initializeListsOfThanks(){

        Log.v(TAG, "RecyclerView Given or Received: OtherUser ID: " + mOtherUser.getUserId());
        Query thanksGivenQuery = mThanksRef.orderByChild(FROM_USER).equalTo(mOtherUser.getUserId()).limitToLast(USER_READ_THRESHOLD);
        Query thanksReceivedQuery = mThanksRef.orderByChild(TO_USER).equalTo(mOtherUser.getUserId()).limitToLast(USER_READ_THRESHOLD);

        //Query for populating Given Thanks
        thanksGivenQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOtherUserThanksGiven = new ArrayList<Thanks>();
                if(dataSnapshot.hasChildren()){

                    Log.v(TAG, "RecyclerView Given, number of children in dataSnapshot: " + dataSnapshot.getChildrenCount());
                    Log.v(TAG, "RecyclerView Given: entering it");
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        Thanks thanks = data.getValue(Thanks.class);
                        mOtherUserThanksGiven.add(thanks);

                    }

                    Collections.sort(mOtherUserThanksGiven, new Comparator<Thanks>() {
                        @Override
                        public int compare(Thanks o1, Thanks o2) {
                            return Long.compare(o2.getDate(), o1.getDate());
                        }
                    });

                    if(getActivity() != null) {
                        mThanksGivenAdapter = new ThanksAdapter(getActivity().getBaseContext(), mOtherUserThanksGiven, TYPE_GIVER);
                        mThanksGivenRecyclerView.setAdapter(mThanksGivenAdapter);
                    }
                }
                else {
                    Log.v(TAG, "RecyclerView Given: didn't find datasnapshot");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Query for populating Received Thanks
        thanksReceivedQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOtherUserThanksReceived = new ArrayList<Thanks>();
                Log.v(TAG, "RecyclerView Received. Received count: " + dataSnapshot.getChildrenCount());
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        Thanks thanks = data.getValue(Thanks.class);
                        mOtherUserThanksReceived.add(thanks);
                    }

                    Collections.sort(mOtherUserThanksReceived, new Comparator<Thanks>() {
                        @Override
                        public int compare(Thanks o1, Thanks o2) {
                            return Long.compare(o2.getDate(), o1.getDate());
                        }
                    });

                    if(getActivity() != null) {
                        mThanksReceivedAdapter = new ThanksAdapter(getActivity().getBaseContext(), mOtherUserThanksReceived, TYPE_RECEIVER);
                        mThanksReceivedRecyclerView.setAdapter(mThanksReceivedAdapter);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } */
}
