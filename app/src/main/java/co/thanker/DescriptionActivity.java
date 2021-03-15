package co.thanker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import co.thanker.data.User;

public class DescriptionActivity extends AppCompatActivity {

    private static final String TAG = "DescriptionActivity";
    private static final String DB_REFERENCE = "users";
    private final String USER_OBJECT = "user-object";

    private FirebaseFirestore mFirestore;
    private DocumentReference mUserRef;
    private Switch mSwitch;
    private TextView mTextSwitch;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        SpannableString s = new SpannableString(getString(R.string.show_thanks_description));
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.defaultTextColor)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        getSupportActionBar().setTitle(s);

        if(getIntent() != null){
            mUser = (User) getIntent().getSerializableExtra(USER_OBJECT);
        }

        mFirestore = FirebaseFirestore.getInstance();
        mUserRef = mFirestore.collection(DB_REFERENCE).document(mUser.getUserId());
        mSwitch = findViewById(R.id.switch_description);
        mTextSwitch = findViewById(R.id.text_switch);

        mSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mSwitch.isChecked()){
                    mTextSwitch.setText(getString(R.string.no));
                    mUserRef.update("showThanksDescription", false);
                }
                else {
                    mTextSwitch.setText(getString(R.string.yes));
                    mUserRef.update("showThanksDescription", true);
                }
                return false;
            }
        });

        //mSwitch.setChecked(mUser.getShowThanksDescription());
        //String text = (mUser.getShowThanksDescription() ? getString(R.string.yes) : getString(R.string.no));
        //mTextSwitch.setText(text);

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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);
    }
}
