package co.thanker.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import co.thanker.R;
import co.thanker.data.ThanksValueStats;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;

public class StatsAdapter extends ArrayAdapter<ThanksValueStats> {

    public final String TAG = "StatsAdapter";

    private Context mContext;
    private long mTotalThanksValues;

    public StatsAdapter(@NonNull Context context, List<ThanksValueStats> listValueStats){
        super(context, 0, listValueStats);
        mContext = context;
        mTotalThanksValues = retrieveTotalThanksOf(listValueStats);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_stats_screen, parent, false);
        }

        final ThanksValueStats currentThanksValue = getItem(position);

        Log.v(TAG, "New Stats. Current Thanks Type: " + currentThanksValue.getThanksType());

        TextView type = (TextView) listItemView.findViewById(R.id.text_item_stats_thanks_type);
        TextView value = (TextView) listItemView.findViewById(R.id.text_item_stats_value);
        ImageView icon = (ImageView) listItemView.findViewById(R.id.image_icon);
        final LinearLayout rectangle = (LinearLayout) listItemView.findViewById(R.id.linear_rectangle);

        final float percentageOfThanksValueFloat = currentThanksValue.getThanksValue() * 100 / mTotalThanksValues;
        final int percentageOfThanksValue = Math.round(percentageOfThanksValueFloat);

        if(currentThanksValue.getThanksValue() > 0) {

            int viewWidth = 1000;
            int viewHeight = 50;

            Paint paint = new Paint();
            paint.setColor(getContext().getResources().getColor(R.color.colorPrimary));

            float rectangleLength = currentThanksValue.getThanksValue() * viewWidth / mTotalThanksValues;

            Log.v(TAG, "Stats Adapter design. Total width: " + viewWidth + ", height: " + viewHeight);
            Log.v(TAG, "Stats Adapter designing rectangle. Values: currentThanks Type: " + currentThanksValue.getThanksType() +
                            ", percentageOfThanksValues " + percentageOfThanksValue + "%");
            Log.v(TAG, "Stats Adapter designing. Width of " + currentThanksValue.getThanksType() + ": " + rectangleLength
                    + ", Height: " + (viewHeight * 0.8));

            int bitmapWidth = Math.round(rectangleLength);
            int bitmapHeight = (int) Math.round(viewHeight * 0.8);
            Bitmap bg = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bg);
            float top = (float) (viewHeight - (viewHeight * 0.8));

            if (currentThanksValue.getThanksValue() > 0) {
                canvas.drawRect(0, top, rectangleLength, bitmapHeight, paint);
            }

            rectangle.setBackground(new BitmapDrawable(bg));
        }

        else {
            rectangle.setBackground(null);
        }

        type.setText(DataUtils.translateAndFormat(mContext, currentThanksValue.getThanksType()));
        value.setText(String.valueOf(percentageOfThanksValue) + "%");

        /*Drawable iconDraw = ImageUtils.getIconImage(mContext, currentThanksValue.getThanksType());
        iconDraw.setColorFilter(mContext.getResources().getColor(R.color.defaultTextColor2), PorterDuff.Mode.MULTIPLY);*/
        icon.setImageDrawable(ImageUtils.getIconImageGrey(mContext, currentThanksValue.getThanksType()));

        return listItemView;
    }


    public long retrieveTotalThanksOf(List<ThanksValueStats> listThanksStats){
        long result = 0;

        for(ThanksValueStats thanksStats: listThanksStats){
            result += thanksStats.getThanksValue();
        }

        return result;
    }
}
