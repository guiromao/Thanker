package co.thanker.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import co.thanker.R;
import co.thanker.data.ThanksValue;
import co.thanker.data.User;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;

public class PersonalStatsAdapter extends ArrayAdapter<ThanksValue> {

    private List<ThanksValue> mListThanksValues;
    private Context mContext;
    private User mUser;
    private long mTotalThanksValues;

    public PersonalStatsAdapter(Context c, int resource, List<ThanksValue> list, User user){
        super(c, resource, list);
        mContext = c;
        mListThanksValues = list;
        mUser = user;
        mTotalThanksValues = retrieveTotalValues();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_stats_screen, parent, false);
        }

        final ThanksValue currentThanksValue = getItem(position);
        TextView textType = listItemView.findViewById(R.id.text_item_stats_thanks_type);
        TextView textValue = listItemView.findViewById(R.id.text_item_stats_value);
        ImageView icon = listItemView.findViewById(R.id.image_icon);
        LinearLayout rectangle = listItemView.findViewById(R.id.linear_rectangle);

        String category = DataUtils.thanksCategoryToStringCategory(currentThanksValue.getKey());
        String translatedCategory = DataUtils.translateAndFormat(mContext, category);

        textType.setText(translatedCategory);
        textValue.setText(String.valueOf(Math.round(100 * currentThanksValue.getValue() / mTotalThanksValues)) + "%");
        icon.setImageDrawable(ImageUtils.getIconImageGrey(mContext, category));

        if(currentThanksValue.getValue() > 0) {


            int viewWidth = 1000;
            int viewHeight = 50;

            Paint paint = new Paint();
            paint.setColor(getContext().getResources().getColor(R.color.colorPrimary));

            float rectangleLength = currentThanksValue.getValue() * viewWidth / mTotalThanksValues;


            int bitmapWidth = Math.round(rectangleLength);
            int bitmapHeight = (int) Math.round(viewHeight * 0.8);
            Bitmap bg = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bg);
            float top = (float) (viewHeight - (viewHeight * 0.8));

            if (currentThanksValue.getValue() > 0) {
                canvas.drawRect(0, top, rectangleLength, bitmapHeight, paint);
            }

            rectangle.setBackground(new BitmapDrawable(bg));
        }

        else {
            rectangle.setBackground(null);
        }


        return listItemView;
    }

    private long retrieveTotalValues(){
        long result = 0;
        for(ThanksValue thanksValue: mListThanksValues){
            result += thanksValue.getValue();
        }

        return result;
    }

}
