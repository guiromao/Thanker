package co.thanker.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

import co.thanker.R;

public class Ring extends View {
    private Bitmap mBack;
    private Paint mPaint;
    private RectF mOval;
    private Paint mTextPaint;

    public Ring(Context context) {
        super(context);
        Resources res = getResources();
        mBack = BitmapFactory.decodeResource(res, R.drawable.circle_image);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap ring = BitmapFactory.decodeResource(res, R.drawable.circle_image);
        mPaint.setShader(new BitmapShader(ring, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        mOval = new RectF(0, 0, mBack.getWidth(), mBack.getHeight());
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(24);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate((getWidth() - mBack.getWidth()) / 2, (getHeight() - mBack.getHeight()) / 2);
        canvas.drawBitmap(mBack, 0, 0, null);
        float angle = 220;
        canvas.drawArc(mOval, -90, angle, true, mPaint);
        canvas.drawText("Text",
                mBack.getWidth() / 2,
                (mBack.getHeight() - mTextPaint.ascent()) / 2,
                mTextPaint);
    }
}