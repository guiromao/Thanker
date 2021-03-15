package co.thanker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

import co.thanker.data.DeletedUser;
import co.thanker.data.Message;
import co.thanker.data.PremiumData;
import co.thanker.data.Thanks;
import co.thanker.data.ThanksData;
import co.thanker.data.User;
import co.thanker.data.UserSnippet;
import co.thanker.utils.DataUtils;

public class DeleteActivity extends AppCompatActivity {

    private final String TAG = DeleteActivity.class.getSimpleName();
    private final String THANKS_DB = "thanks-db";
    private final String THANKS_DATA = "thanks-data";
    private static final String DB_REFERENCE = "users";
    private final String USER_SNIPPET = "user-snippet";
    private static final String DELETED_ACCOUNTS = "deleted";
    private final String MESSAGES_DB = "messages-list";
    private final String PREMIUM_DB = "premium-info";
    private final String PLATFORM_MESSAGE = "platform-message";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private Button mButtonDont;
    private TextView mButtonDelete;
    private String mUserId;
    private DeletedUser mDeletedUser;
    private List<Thanks> mListGivenThanks;
    private List<Thanks> mListReceivedThanks;
    private List<Message> mMessagesGiven;
    private List<Message> mMessagesReceived;
    private Activity mActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        SpannableString s = new SpannableString(getString(R.string.delete_account));
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.defaultTextColor)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        mActivity = this;
        mButtonDelete = (TextView) findViewById(R.id.button_delete);
        mButtonDont = (Button) findViewById(R.id.button_no);

        mButtonDelete.setText(Html.fromHtml("<u>" + getString(R.string.yes_delete) + "</u>"));

        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.thank_you_for_the_time), Toast.LENGTH_LONG).show();
                setupFirebase();
                deleteUser();
            }
        });

        mButtonDont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setupFirebase(){
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();
    }

    public void deleteUser(){
        mFirestore.collection(DB_REFERENCE).document(mUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            final User user = documentSnapshot.toObject(User.class);
                            mDeletedUser = new DeletedUser(user);
                            Log.v(TAG, "Deleting user. Wrote User");
                        }

                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading our User info");

                        Log.v(TAG, "Deleting user. Passing on User");

                        mFirestore.collection(USER_SNIPPET).document(mUserId)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()){
                                            final UserSnippet userSnippet = documentSnapshot.toObject(UserSnippet.class);
                                            mDeletedUser.setUserSnippet(userSnippet);
                                            Log.v(TAG, "Deleting user. Wrote UserSnippet");

                                        }

                                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading our UserSnippet info");

                                        Log.v(TAG, "Deleting user. Passing on UserSnippet");

                                        mFirestore.collection(THANKS_DB)
                                                .whereEqualTo("fromUserId", mUserId)
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        mListGivenThanks = new ArrayList<>();
                                                        if(queryDocumentSnapshots.size() > 0){
                                                            Log.v(TAG, "Deleting user. Going to write Thanks Given");
                                                            List<String> deleteThanksKeys = new ArrayList<>();
                                                            for(QueryDocumentSnapshot givenSnapshot: queryDocumentSnapshots){
                                                                Thanks thanks = givenSnapshot.toObject(Thanks.class);
                                                                mListGivenThanks.add(thanks);
                                                                deleteThanksKeys.add(givenSnapshot.getId());
                                                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading a Thanks we gave");
                                                            }

                                                            mDeletedUser.setThanksGiven(mListGivenThanks);

                                                            mFirestore.runTransaction(new Transaction.Function<Void>() {
                                                                @Override
                                                                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                                                    for(String id: deleteThanksKeys){
                                                                        DocumentReference thanksRef = mFirestore.collection(THANKS_DB).document(id);
                                                                        thanksRef.delete();
                                                                    }

                                                                    // Success
                                                                    return null;
                                                                }
                                                            });

                                                            Log.v(TAG, "Deleting user. Passing on Thanks Given");

                                                        }

                                                        mFirestore.collection(THANKS_DB)
                                                                .whereEqualTo("toUserId", mUserId)
                                                                .get()
                                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                        mListReceivedThanks = new ArrayList<>();
                                                                        if(queryDocumentSnapshots.size() > 0){
                                                                            Log.v(TAG, "Deleting user. Going to write Thanks Received");
                                                                            List<String> deleteThanksKeys = new ArrayList<>();
                                                                            for(QueryDocumentSnapshot receivedSnapshot: queryDocumentSnapshots){
                                                                                Thanks thanks = receivedSnapshot.toObject(Thanks.class);
                                                                                mListReceivedThanks.add(thanks);
                                                                                deleteThanksKeys.add(receivedSnapshot.getId());
                                                                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading a Thanks we received");
                                                                            }

                                                                            mDeletedUser.setThanksReceived(mListReceivedThanks);

                                                                            mFirestore.runTransaction(new Transaction.Function<Void>() {
                                                                                @Override
                                                                                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                                                                    for(String id: deleteThanksKeys){
                                                                                        DocumentReference thanksRef = mFirestore.collection(THANKS_DB).document(id);
                                                                                        thanksRef.delete();
                                                                                    }

                                                                                    // Success
                                                                                    return null;
                                                                                }
                                                                            });

                                                                            Log.v(TAG, "Deleting user. Passing on Thanks Received");

                                                                        }

                                                                        mFirestore.collection(MESSAGES_DB)
                                                                                .whereEqualTo("toUserId", mUserId)
                                                                                .get()
                                                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                        mMessagesReceived = new ArrayList<>();
                                                                                        if(queryDocumentSnapshots.size() > 0){
                                                                                            Log.v(TAG, "Deleting user. Going to write Messages Received");
                                                                                            List<String> deleteMsgKeys = new ArrayList<>();
                                                                                            for(QueryDocumentSnapshot msgSnapshot: queryDocumentSnapshots){
                                                                                                Message message = msgSnapshot.toObject(Message.class);
                                                                                                mMessagesReceived.add(message);
                                                                                                deleteMsgKeys.add(msgSnapshot.getId());
                                                                                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading a Message we received");
                                                                                            }

                                                                                            mDeletedUser.setListMessagesReceived(mMessagesReceived);

                                                                                            mFirestore.runTransaction(new Transaction.Function<Void>() {
                                                                                                @Override
                                                                                                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                                                                                    for(String id: deleteMsgKeys){
                                                                                                        DocumentReference msgRef = mFirestore.collection(MESSAGES_DB).document(id);
                                                                                                        msgRef.delete();
                                                                                                    }

                                                                                                    // Success
                                                                                                    return null;
                                                                                                }
                                                                                            });

                                                                                            Log.v(TAG, "Deleting user. Passing on Messages Received");
                                                                                        }

                                                                                        mFirestore.collection(MESSAGES_DB)
                                                                                                .whereEqualTo("fromUserId", mUserId)
                                                                                                .get()
                                                                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                                        mMessagesGiven = new ArrayList<>();
                                                                                                        if(queryDocumentSnapshots.size() > 0){
                                                                                                            Log.v(TAG, "Deleting user. Going to write Messages Given");
                                                                                                            List<String> deleteMsgKeys = new ArrayList<>();
                                                                                                            for(QueryDocumentSnapshot msgSnapshot: queryDocumentSnapshots){
                                                                                                                Message message = msgSnapshot.toObject(Message.class);
                                                                                                                mMessagesGiven.add(message);
                                                                                                                deleteMsgKeys.add(msgSnapshot.getId());
                                                                                                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading a message we sent");
                                                                                                            }

                                                                                                            mDeletedUser.setListMessagesGiven(mMessagesGiven);

                                                                                                            mFirestore.runTransaction(new Transaction.Function<Void>() {
                                                                                                                @Override
                                                                                                                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                                                                                                    for(String id: deleteMsgKeys){
                                                                                                                        DocumentReference msgRef = mFirestore.collection(MESSAGES_DB).document(id);
                                                                                                                        msgRef.delete();
                                                                                                                    }

                                                                                                                    // Success
                                                                                                                    return null;
                                                                                                                }
                                                                                                            });

                                                                                                            Log.v(TAG, "Deleting user. Passing on Messages Given");

                                                                                                        }

                                                                                                        mFirestore.collection(PREMIUM_DB).document(mUserId)
                                                                                                                .get()
                                                                                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                                                    @Override
                                                                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                                                        if(documentSnapshot.exists()){
                                                                                                                            PremiumData premiumData = documentSnapshot.toObject(PremiumData.class);
                                                                                                                            mDeletedUser.setPremiumData(premiumData);
                                                                                                                            Log.v(TAG, "Deleting user. Wrote Premium");
                                                                                                                        }

                                                                                                                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading our Premium info");

                                                                                                                        Log.v(TAG, "Deleting user. Passing on Premium");

                                                                                                                        mFirestore.collection(THANKS_DATA).document(mUserId)
                                                                                                                                .get()
                                                                                                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                                                                    @Override
                                                                                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                                                                        if(documentSnapshot.exists()){
                                                                                                                                            ThanksData thanksData = documentSnapshot.toObject(ThanksData.class);
                                                                                                                                            mDeletedUser.setThanksData(thanksData);
                                                                                                                                            Log.v(TAG, "Deleting user. Wrote Thanks Data");
                                                                                                                                        }

                                                                                                                                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading our ThanksData");

                                                                                                                                        Log.v(TAG, "Deleting user. Passing on ThanksData");

                                                                                                                                        mFirestore.collection(DELETED_ACCOUNTS).document(mUserId).set(mDeletedUser);

                                                                                                                                        mFirestore.runTransaction(new Transaction.Function<Void>() {
                                                                                                                                            @Override
                                                                                                                                            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                                                                                                                                Log.v(TAG, "Deleting user. Passing on Going to delete documents!");
                                                                                                                                                DocumentReference userRef = mFirestore.collection(DB_REFERENCE).document(mUserId);
                                                                                                                                                DocumentReference snippetRef = mFirestore.collection(USER_SNIPPET).document(mUserId);
                                                                                                                                                DocumentReference premiumRef = mFirestore.collection(PREMIUM_DB).document(mUserId);
                                                                                                                                                DocumentReference dataRef = mFirestore.collection(THANKS_DATA).document(mUserId);

                                                                                                                                                userRef.delete();
                                                                                                                                                snippetRef.delete();
                                                                                                                                                premiumRef.delete();
                                                                                                                                                dataRef.delete();

                                                                                                                                                Log.v(TAG, "Deleting user. Deleted documents");

                                                                                                                                                // Success
                                                                                                                                                return null;
                                                                                                                                            }
                                                                                                                                        });



                                                                                                                                        AuthUI.getInstance().signOut(getApplicationContext());
                                                                                                                                        DataUtils.terminateActivity(mActivity);
                                                                                                                                    }
                                                                                                                                });
                                                                                                                    }
                                                                                                                });
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                });
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
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
    public void onResume(){
        super.onResume();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    @Override
    public void onStop(){
        super.onStop();

    }
}
