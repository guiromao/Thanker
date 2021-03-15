package co.thanker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import co.thanker.R;
import co.thanker.adapters.PersonalStatsAdapter;
import co.thanker.data.ThanksValue;
import co.thanker.data.User;
import co.thanker.utils.DataUtils;
import co.thanker.utils.Utils;

public class PersonalStatsActivity extends AppCompatActivity {

    private final String LIST_THANKS_VALUES = "list-thanks-values";
    private final String USER_OBJECT = "user-object";

    private User mUser;
    private String mUserName;
    private List<ThanksValue> mListThanksValues;
    private ListView mListView;
    private PersonalStatsAdapter mAdapter;
    private TextView mTitleView;
    private ActionBar mActionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View view;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_stats);

        mActionBar = getSupportActionBar();

        mListView = findViewById(R.id.list_personal_stats);
        mTitleView = (TextView) findViewById(R.id.text_title);


        if (getIntent() != null) {
            mUser = (User) getIntent().getSerializableExtra(USER_OBJECT);
            mListThanksValues = (List<ThanksValue>) getIntent().getSerializableExtra(LIST_THANKS_VALUES);

            mUserName = DataUtils.capitalize(mUser.getName());
            mTitleView.setText(getString(R.string.how_user_thanks, mUserName));

            mAdapter = new PersonalStatsAdapter(this, 0, mListThanksValues, mUser);
            mListView.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onResume() {
        super.onResume();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        SpannableString s = new SpannableString(getString(R.string.status_of, DataUtils.capitalize(DataUtils.firstName(mUser.getName()))));
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.defaultTextColor)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

}

