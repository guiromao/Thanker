package co.thanker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PrivacyActivity extends AppCompatActivity {

    private final String REGISTER_INTENT = "register-intent";

    private TextView mTextPrivacy;
    private Button mButtonAgree;
    private ActionBar mActionBar;
    private Activity mActivity;
    private boolean mComesFromRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        mActionBar = getSupportActionBar();
        mActivity = this;

        mComesFromRegister = false;

        SpannableString s = new SpannableString(getString(R.string.title_privacy));
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.defaultTextColor)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        mActionBar.setTitle(s);

        mTextPrivacy = (TextView) findViewById(R.id.text_privacy);
        mButtonAgree = (Button) findViewById(R.id.button_agree);

        if(getIntent() != null){

        }

        String text = getString(R.string.gdpr);
        mTextPrivacy.setText(Html.fromHtml(text));

        mButtonAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mComesFromRegister){
                    ActivityCompat.finishAffinity(mActivity);
                }
                else {
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if(id == android.R.id.home){
            if(mComesFromRegister){
                ActivityCompat.finishAffinity(mActivity);
            }
            else {
                finish();
            }
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
        mActionBar.setHomeAsUpIndicator(upArrow);
    }

}
