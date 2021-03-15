package co.thanker;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import co.thanker.data.DeletedUser;
import co.thanker.data.Message;
import co.thanker.data.PremiumData;
import co.thanker.data.Thanks;
import co.thanker.data.ThanksData;
import co.thanker.data.User;
import co.thanker.data.UserSnippet;
import co.thanker.utils.DataUtils;

public class ReviveActivity extends AppCompatActivity {

    private final String TAG = ReviveActivity.class.getSimpleName();
    private final String THANKS_DB = "thanks-db";
    private final String THANKS_DATA = "thanks-data";
    private static final String DB_REFERENCE = "users";
    private static final String DELETED_ACCOUNTS = "deleted";
    private final String USER_SNIPPET = "user-snippet";
    private final String PLATFORM_MESSAGE = "platform-message";
    private final String MESSAGES_DB = "messages-list";
    private final String PREMIUM_DB = "premium-info";
    //private final String FRIENDS_DB = "friends-db";

    private Button mButtonRevive;
    private TextView mButtonNo;
    private String mUserId;
    private DeletedUser mDeletedUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DocumentReference mDeletedRef;
    private Activity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revive);

        mActivity = this;

        setupFirebase();
        initViews();
    }

    public void setupFirebase(){
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth != null){
            if(mAuth.getCurrentUser() != null){
                mUserId = mAuth.getCurrentUser().getUid();
                mDeletedRef = mFirestore.collection(DELETED_ACCOUNTS).document(mUserId);
            }
        }

    }

    public void initViews(){
        mButtonRevive = (Button) findViewById(R.id.button_yes);
        mButtonNo = (TextView) findViewById(R.id.button_no);

        mButtonRevive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.revived_shall_be), Toast.LENGTH_LONG).show();
                reviveUser();
            }
        });

        mButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeletedRef.delete();
                finish();
            }
        });

        mButtonNo.setText(Html.fromHtml("<u>" + getString(R.string.no_thanks) + "</u>"));
    }

    public void reviveUser(){
        mDeletedRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading our DeletedUser result in Delete folder");
                            mDeletedUser = documentSnapshot.toObject(DeletedUser.class);

                            String userId = mDeletedUser.getUser().getUserId();

                            User user = mDeletedUser.getUser();
                            mFirestore.collection(DB_REFERENCE).document(userId).set(user);

                            UserSnippet userSnippet = mDeletedUser.getUserSnippet();
                            mFirestore.collection(USER_SNIPPET).document(userId).set(userSnippet);

                            List<Thanks> thanksGiven = mDeletedUser.getThanksGiven();
                            for(Thanks thanks: thanksGiven){
                                mFirestore.collection(THANKS_DB).add(thanks);
                            }

                            List<Thanks> thanksReceived = mDeletedUser.getThanksReceived();
                            for(Thanks thanks: thanksReceived){
                                mFirestore.collection(THANKS_DB).add(thanks);
                            }

                            ThanksData data = mDeletedUser.getThanksData();
                            mFirestore.collection(THANKS_DATA).document(userId).set(data);

                            PremiumData premiumData = mDeletedUser.getPremiumData();
                            mFirestore.collection(PREMIUM_DB).document(userId).set(premiumData);

                            List<Message> messagesGiven = mDeletedUser.getListMessagesGiven();
                            for(Message message: messagesGiven){
                                mFirestore.collection(MESSAGES_DB).add(message);
                            }

                            List<Message> messagesReceived = mDeletedUser.getListMessagesReceived();
                            for(Message message: messagesReceived){
                                mFirestore.collection(MESSAGES_DB).add(message);
                            }

                            mDeletedRef.delete();

                            String title = getString(R.string.welcome_back_back, DataUtils.capitalize(user.getName()));
                            String body = getString(R.string.welcome_back_again, DataUtils.capitalize(user.getName()), DataUtils.capitalize(user.getName()));
                            DataUtils.createMessage(mUserId, title, body, PLATFORM_MESSAGE, 0);

                            DataUtils.terminateActivity(mActivity);
                        }
                    }
                });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){

        menu.clear();

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_account, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if(itemId == R.id.item_logout){
            AuthUI.getInstance().signOut(this);
            finish();
            return true;
        }

        return onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));

        SpannableString s = new SpannableString(getString(R.string.hello_again));
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.defaultTextColor)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

}
