package com.rigid.colourpicker;

import android.Manifest;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class HueDisplay extends View implements HueChangeInterface {
//    private RectF rectF;
    private float hue=0;
    private Paint p;
    private float[] hsv;
    private Thread thread;

    public HueDisplay(Context context) {
        super(context);
    }

    public HueDisplay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        hsv=new float[3];
        p = new Paint();
        hsv[0]=0;
        hsv[1]=1;
        hsv[2]=1;
    }

    //receive the hue value here
//    private float ii=0, zz =0;
    @Override
    public void OnHueChanged(float hue) {
        this.hue=hue;
        hsv[0]=hue;
        invalidate();
//        synchronized (this) {
//            notifyAll();
//        }
//        //initiate a thread here and
//        if(thread==null){
//            thread= new Thread(()->{
//                synchronized (this) {
//                    while (true) {
//                        for (float i = 0; i <= getHeight()/10f; i++) {
//                            for (float z = 0; z <= getWidth()/10f; z++) {
//                                ii = i;
//                                zz = z;
//                                //0 -> 1
//                                hsv[1] = z / (getWidth()/10f); //saturation
//                                hsv[2] = i / (getHeight()/10f); //value
//                                p.setColor(
//                                        Color.HSVToColor(hsv)
//                                );
//                                postInvalidate();
//                            }
//                        }
//                        try {
//                            wait();
//                        } catch (InterruptedException e) { }
//                    }
//                }
//            },"HueDisplay");
//            thread.start();
//        }

    }
    private RectF rectF;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rectF=new RectF(0,0,w,h);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        p.setColor(Color.HSVToColor(hsv));
        canvas.drawRect(rectF,p);

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if(x>getWidth() || y>getHeight())
            return false;
//        float[] hsv = new float[3];
//        Color.colorToHSV(Color.RED,hsv);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                hsv[1] = x/getWidth();
                hsv[2] = y/getHeight();
                Log.d("COLOUR ","down "+Integer.toHexString(Color.HSVToColor(hsv))+" "+y);
                break;
            case MotionEvent.ACTION_MOVE:
                hsv[1] = x/getWidth();
                hsv[2] = y/getHeight();
                Log.d("COLOUR ","c "+Integer.toHexString(Color.HSVToColor(hsv))+" "+y);
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }
}
