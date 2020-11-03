package com.rigid.colourpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
/**
 * Created By: Munim Ahmad (03/11/20)
 *
 * Previews the user selected colour variant
 *
 * */
public class UserColourPreview extends View implements UserColourPreviewInterface {
    private RectF rectF;
    private Paint p;

    public UserColourPreview(Context context) {
        super(context);
    }

    public UserColourPreview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        p=new Paint();
        p.setColor(Color.RED);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rectF = new RectF(0,0,w,h);
    }
    //hue display
    @Override
    public void onChanged(int colour){
        p.setColor(colour);
        invalidate();
        //get hsv values and leave the sat and val the same only changing the hue and get colour from that
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(rectF,p);
    }
}
