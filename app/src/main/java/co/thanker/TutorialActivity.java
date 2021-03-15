package co.thanker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.jama.carouselview.CarouselView;
import com.jama.carouselview.CarouselViewListener;
import com.jama.carouselview.enums.IndicatorAnimationType;
import com.jama.carouselview.enums.OffsetType;

public class TutorialActivity extends AppCompatActivity {

    private final String TAG = TutorialActivity.class.getSimpleName();
    private final String TUTORIAL_PREFS = "tutorial-prefs";
    private final String TUTORIAL_READ = "tutorial-read";

    private FirebaseAuth mAuth;
    private CarouselView mCarousel;
    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        SpannableString s = new SpannableString(getString(R.string.tutorial));
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.defaultTextColor)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        getSupportActionBar().setTitle(s);

        mAuth = FirebaseAuth.getInstance();
        mSharedPref = getSharedPreferences(TUTORIAL_PREFS + mAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
        mPrefsEditor = mSharedPref.edit();

        mCarousel = (CarouselView) findViewById(R.id.carousel_tutorial);

        final String [] arrayTitles = getResources().getStringArray(R.array.titles_tutorial);
        final String [] arraySubtitles = getResources().getStringArray(R.array.subtitles_tutorial);
        final int [] arrayImages = {R.drawable.thanks, R.drawable.thanks, R.drawable.thanks, R.drawable.thanks, R.drawable.thanks, R.drawable.thanks};

        mCarousel.setSize(arrayTitles.length);
        mCarousel.setResource(R.layout.item_carousel_tutorial);
        mCarousel.setAutoPlay(false);
        mCarousel.setIndicatorAnimationType(IndicatorAnimationType.SLIDE);
        mCarousel.setCarouselOffset(OffsetType.CENTER);
        mCarousel.setCarouselViewListener(new CarouselViewListener() {
            @Override
            public void onBindView(View view, int position) {
                // Example here is setting up a full image carousel
                TextView textView = view.findViewById(R.id.carousel_subtitle);
                textView.setText(Html.fromHtml(arraySubtitles[position]));

                TextView titleView = view.findViewById(R.id.text_title);
                titleView.setText(arrayTitles[position]);

                ImageView imageView = view.findViewById(R.id.image_carousel);
                imageView.setImageResource(arrayImages[position]);

                if(position == (mCarousel.getSize() - 1)){
                    Button button = view.findViewById(R.id.button_got_it);
                    button.setVisibility(View.VISIBLE);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPrefsEditor.putBoolean(TUTORIAL_READ, true);
                            mPrefsEditor.commit();
                            finish();
                        }
                    });
                }
            }
        });
        // After you finish setting up, show the CarouselView
        mCarousel.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if(id == android.R.id.home){
            mPrefsEditor.putBoolean(TUTORIAL_READ, true);
            mPrefsEditor.commit();
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
