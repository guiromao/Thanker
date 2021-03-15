package co.thanker.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Silmarilos on 2017-05-22.
 */

public class MultiColorCircle extends View {

    private RectF rect, outerRect, innerRect;
    private Paint perimeterPaint;
    private List<CustomStrokeObject> strokeObjects;
    private int widthOfCircleStroke, widthOfBoarderStroke,
            colorOfBoarderStroke, onePercentPixels;

    public MultiColorCircle(Context context) {
        super(context);
        init();
    }

    public MultiColorCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiColorCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MultiColorCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * Setter for the width of the circle stroke. Affects all arcs drawn. This is the width of
     * the various arcs that make up the actual circle, this is NOT the boarder, that is different
     * @param widthOfCircleStroke
     */
    public void setWidthOfCircleStroke(int widthOfCircleStroke){
        this.widthOfCircleStroke = widthOfCircleStroke;
    }

    /**
     * Setter for the width of the boarder stroke. This is the width of the boarder strokes used
     * to make the inner and outer boarder of the rings that surround the main body circle.
     * They will default to black and 1 pixel in width. To hide them, pass null as the color
     * @param widthOfBoarderStroke
     */
    public void setWidthOfBoarderStroke(int widthOfBoarderStroke){
        this.widthOfBoarderStroke = widthOfBoarderStroke;
        this.perimeterPaint.setStrokeWidth(this.widthOfBoarderStroke);
    }

    /**
     * Set the color of the boarder stroke. Send in null if you want it to be hidden
     * @param colorOfBoarderStroke
     */
    public void setColorOfBoarderStroke(Integer colorOfBoarderStroke){
        if(colorOfBoarderStroke == null){
            //Set to transparent
            this.colorOfBoarderStroke = Color.parseColor("#00000000");
        } else {
            this.colorOfBoarderStroke = colorOfBoarderStroke;
        }
        this.perimeterPaint.setColor(this.colorOfBoarderStroke);
    }

    private void init(){
        this.strokeObjects = new ArrayList<>();
        this.onePercentPixels = 0;
        this.widthOfCircleStroke = 1; //Default
        this.widthOfBoarderStroke = 1; //Default
        this.colorOfBoarderStroke = Color.parseColor("#000000"); //Default, black
        this.rect = new RectF();
        this.outerRect = new RectF();
        this.innerRect = new RectF();
        this.perimeterPaint = new Paint();
        this.perimeterPaint.setStrokeWidth(widthOfBoarderStroke);
        this.perimeterPaint.setColor(colorOfBoarderStroke);
        this.perimeterPaint.setAntiAlias(true);
        this.perimeterPaint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = this.getWidth();
        int left = 0;
        int top = 0;
        int right = (left + width);
        int bottom = (top + width);

        onePercentPixels = (int)(this.getWidth() * 0.01);
        left = left + onePercentPixels + widthOfCircleStroke;
        top = top + onePercentPixels + widthOfCircleStroke;
        right = right - onePercentPixels - widthOfCircleStroke;
        bottom = bottom - onePercentPixels - widthOfCircleStroke;

        drawCircle(canvas, left, top, right, bottom);
    }

    private void drawCircle(Canvas canvas, int left, int top, int right, int bottom){
        //Base rect for sides of circle parameters
        rect.set(left, top, right, bottom);

        if(this.strokeObjects.size() <= 0){
            return;
        }
        for(CustomStrokeObject strokeObject : this.strokeObjects){
            if(strokeObject == null){
                continue;
            }
            Paint paint = strokeObject.paint;
            paint.setStrokeWidth(this.widthOfCircleStroke);
            canvas.drawArc(rect, strokeObject.percentToStartAt,
                    strokeObject.percentOfCircle, false, paint);
        }
        drawPerimeterCircle(canvas, left, top, right, bottom);
    }

    /**
     * Draws the outer and inner boarder arcs of black to create a boarder
     */
    private void drawPerimeterCircle(Canvas canvas, int left, int top, int right, int bottom){
        //Base inner and outer rectanges for circles to be drawn
        outerRect.set(
                (left - (widthOfCircleStroke / 2)),
                (top - (widthOfCircleStroke / 2)),
                (right + (widthOfCircleStroke / 2)),
                (bottom + (widthOfCircleStroke / 2))
        );
        innerRect.set(
                (left + (widthOfCircleStroke / 2)),
                (top + (widthOfCircleStroke / 2)),
                (right - (widthOfCircleStroke / 2)),
                (bottom - (widthOfCircleStroke / 2))
        );
        canvas.drawArc(outerRect, 0, 360, false, perimeterPaint);
        canvas.drawArc(innerRect, 0, 360, false, perimeterPaint);
    }

    /**
     * Setter method for setting the various strokes on the circle
     * @param strokeObjects {@link CustomStrokeObject}
     */
    public void setCircleStrokes(List<CustomStrokeObject> strokeObjects){
        if(strokeObjects == null){
            return;
        }
        if(strokeObjects.size() == 0){
            return;
        }
        this.strokeObjects = new ArrayList<>();
        this.strokeObjects = strokeObjects;
        invalidate();
    }

    /**
     * Class used in drawing arcs of circle
     */
    public static class CustomStrokeObject {

        float percentOfCircle;
        float percentToStartAt;
        Integer colorOfLine;
        Paint paint;

        /**
         * Constructor. This will also do the calculations to convert the percentages into the
         * circle numbers so that passing in 50 will be converted into 180 for mapping on to a
         * circle. Also, I am adding in a very tiny amount of overlap (a couple pixels) so that
         * there will not be a gap between the arcs because the whitespace gap of a couple pixels
         * does not look very good. To remove this, just remove the -.1 and .1 to startAt and circle
         * @param percentOfCircle Percent of the circle to fill.
         *                        NOTE! THIS IS BASED OFF OF 100%!
         *                        This is not based off of a full 360 circle so if you want something
         *                        to fill half the circle, pass 50, not 180.
         * @param percentToStartAt Percent to start at (for filling multiple colors).
         *                         NOTE! THIS IS BASED OFF OF 100%!
         *                         This is not based off of a full 360 circle so if you want something
         *                         to fill half the circle, pass 50, not 180.
         * @param colorOfLine Int color of the line to use
         */
        public CustomStrokeObject(float percentOfCircle, float percentToStartAt, Integer colorOfLine){
            this.percentOfCircle = percentOfCircle;
            this.percentToStartAt = percentToStartAt;
            this.colorOfLine = colorOfLine;
            if(this.percentOfCircle < 0 || this.percentOfCircle > 100){
                this.percentOfCircle = 100; //Default to 100%
            }
            this.percentOfCircle = (float)((360 * (percentOfCircle + 0.1)) / 100);
            if(this.percentToStartAt < 0 || this.percentToStartAt > 100){
                this.percentToStartAt = 0;
            }
            //-90 so it will start at top, Ex: http://www.cumulations.com/images/blog/screen1.png
            this.percentToStartAt = (float)((360 * (percentToStartAt - 0.1)) / 100) - 90;
            if(this.colorOfLine == null){
                this.colorOfLine = Color.parseColor("#000000"); //Default to black
            }

            paint = new Paint();
            paint.setColor(colorOfLine);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
        }

        /**
         * Overloaded setter, in case you want to set a custom paint object here
         * @param paint Paint object to overwrite one set by constructor
         */
        public void setPaint(Paint paint){
            this.paint = paint;
        }
    }
}