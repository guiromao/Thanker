package co.thanker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jama.carouselview.CarouselView;
import com.jama.carouselview.CarouselViewListener;
import com.jama.carouselview.enums.IndicatorAnimationType;
import com.jama.carouselview.enums.OffsetType;

import co.thanker.utils.TypefaceSpan;

public class HintsActivity extends AppCompatActivity {

    private TextView mTextHint0;
    private TextView mTextHint1;
    private TextView mTextHint2;
    private TextView mTextHint2Sub1;
    private TextView mTextHint2Sub2;
    private TextView mTextHint2Sub3;
    private CarouselView mCarousel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hints);

        SpannableString s = new SpannableString(getString(R.string.hints));
        /*s.setSpan(new TypefaceSpan(this, "greatwishes.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.defaultTextColor)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        //ActionBar actionBar = getSupportActionBar();
        //mActionBar.setTitle(Html.fromHtml("<font color=\"#808080\">" + s + "</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        getSupportActionBar().setTitle(s);

        mCarousel = findViewById(R.id.carousel);
        final String [] arrayTitles = getResources().getStringArray(R.array.titles_hints);
        final String [] arrayHints = getResources().getStringArray(R.array.hints_array);
        final int [] arrayImages = {R.drawable.dicaone, R.drawable.dicatwo, R.drawable.dicathree, R.drawable.dicafour, R.drawable.dicafive, R.drawable.dicasix,
                R.drawable.ic_note, R.drawable.ic_cup, R.drawable.dicaseven, R.drawable.dicaeight, R.drawable.dicanine, R.drawable.welcomewink};

        mCarousel.setSize(arrayHints.length);
        mCarousel.setResource(R.layout.item_carousel_hint);
        mCarousel.setAutoPlay(true);
        mCarousel.setIndicatorAnimationType(IndicatorAnimationType.SLIDE);
        mCarousel.setCarouselOffset(OffsetType.CENTER);
        mCarousel.setCarouselViewListener(new CarouselViewListener() {
            @Override
            public void onBindView(View view, int position) {
                // Example here is setting up a full image carousel
                TextView textView = view.findViewById(R.id.text_carousel);
                textView.setText(Html.fromHtml(arrayHints[position]));

                TextView titleView = view.findViewById(R.id.text_title_carousel);
                titleView.setText(arrayTitles[position]);

                ImageView imageView = view.findViewById(R.id.image_hint);

                if(arrayImages[position] == R.drawable.welcomewink || arrayImages[position] == R.drawable.ic_cup || arrayImages[position] == R.drawable.ic_note){
                    //imageView.setScaleType(ImageView.ScaleType.CENTER);
                    imageView.getLayoutParams().height = 300;
                    imageView.getLayoutParams().width = 300;
                    imageView.requestLayout();
                    int color = Color.parseColor("#4CAF50"); //The color u want
                    imageView.setColorFilter(color);
                }

                else {
                    //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }

                if(arrayImages[position] != 0){
                    imageView.setImageResource(arrayImages[position]);
                    imageView.setVisibility(View.VISIBLE);

                    if (arrayImages[position] == R.drawable.logo){
                        imageView.setMinimumWidth(120);
                        imageView.setMinimumHeight(120);
                    }

                }

                else {
                    imageView.setVisibility(View.INVISIBLE);
                }


            }
        });
        // After you finish setting up, show the CarouselView
        mCarousel.show();

    }

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 50, 50, false);
        return new BitmapDrawable(getResources(), bitmapResized);
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

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }
}
