package com.rigid.colourpicker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created By: Munim Ahmad (03/11/20)
 *
 * Holds Hue Values and displays them
 *
 * */
public class HueDisplaySurfaceView extends SurfaceView implements Runnable, HueChangeInterface, SurfaceHolder.Callback {
    //If you are wondering why I used SurfaceView, it's because when starting out
    // I was literally drawing the entire HSV values on the canvas pixel by pixel (BAD PRACTICE) so needed threading.
    // Eventually I realised we could blend it with a bg PNG image and the performance improved drastically as one would expect.
    // Other colour pickers also use the same strategy.
    //we could opt out for normal view but I think with different UI elements changing at the same time this remains a better approach.

    private Thread thread;
    private Paint p;
    private float[] hsv;
    private int displayColour=Color.RED;
    private SurfaceHolder mSurfaceHolder;
    private RectF surfaceRect;
    private UserColourPreviewInterface userColourPreviewInterface;
    private EditText hexText;
    private float[] userHSV;
    private int userSelectedColour =Color.RED; //used by userHSV not this display
    private float radius; //radius of the thumb
    private float x,y;
    private HexChangedInterface hexChangedInterface;

    public HueDisplaySurfaceView(Context context) {
        super(context);
    }

    public HueDisplaySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p=new Paint();
        p.setAntiAlias(true);
        //hue slider dependant hsv
        //only responsible for displaying here
        hsv=new float[3];
        hsv[0]=0; //default (RED) - only value that changes
        hsv[1]=1; //s - 100%
        hsv[2]=1; //v - 100%
        mSurfaceHolder = getHolder();

        // user dependant hsv for preview
        //interacts with UserColourPreview, hexText, HueSlider
        userHSV=new float[3];
        userHSV[0]=0;
        userHSV[1]=1;
        userHSV[2]=1;
        invalidate();
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //we get the holder when its ready otherwise it can potentially not be valid
        mSurfaceHolder=holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    public void setUserColourPreviewInterface(UserColourPreviewInterface userColourPreviewInterface){
        this.userColourPreviewInterface = userColourPreviewInterface;
    }
    public void setHexChangedInterface(HexChangedInterface hexChangedInterface){
        this.hexChangedInterface=hexChangedInterface;
    }
    //from the hueColourSlider
    @Override
    public void OnHueChanged(float hue) {
        hexText.setEnabled(false);
        hsv[0] =hue;
        displayColour=Color.HSVToColor(hsv);
        userHSV[0] = hue; //other values remain unchanged as set by user
        userSelectedColour = Color.HSVToColor(userHSV);
        userColourPreviewInterface.onChanged(userSelectedColour);
        hexText.setText(getContext().getString(R.string.hexString,Integer.toHexString(userSelectedColour).substring(2)));
        hexText.setEnabled(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        /* Defaults */
        surfaceRect =new RectF(0,0,w,h);
        radius=Math.min(w,h)*0.05f;
        x=w;
        y=0;
        hexText=((Activity)getContext()).findViewById(R.id.hexvaluetext);
        hexText.setText(getContext().getString(R.string.hexString,Integer.toHexString(userSelectedColour).substring(2)));
        userColourPreviewInterface.onChanged(userSelectedColour);

        hexText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (hexText.isEnabled()) {
                    //always keep the '#' at the start
                    if (!s.toString().startsWith("#"))
                        s.insert(0, "#");
                    //7 including '#'
                    if (s.length() == 7) {
                        for (int i = 1; i < s.length(); i++) {
                            if (Character.digit(s.charAt(i), 16) == -1) { //check if valid hex
                                s.clear();
                                Toast.makeText(getContext(), "Invalid HEX colour code.", Toast.LENGTH_SHORT)
                                        .show();
                                return;
                            }
                        }
                        int c = Color.parseColor(s.toString()); //convert Hex to int
                        Color.colorToHSV(c, userHSV);
                        hsv[0] = userHSV[0];
                        displayColour=Color.HSVToColor(hsv);
                        //calc coords corresponding hsv values
                        x = userHSV[1] * getWidth();
                        y = getHeight() - (userHSV[2] * getHeight());
                        userColourPreviewInterface.onChanged(c);
                        hexChangedInterface.onHexChanged(userHSV[0]);
                    }
                }
            }
        });
    }

    @Override
    public void run() {
        Canvas canvas;
        while (!thread.isInterrupted()) {
            if (mSurfaceHolder.getSurface().isValid()) {
                canvas = mSurfaceHolder.lockCanvas();
                p.setStyle(Paint.Style.FILL);
                p.setColor(displayColour);
                canvas.drawRect(surfaceRect,p);

                /* Thumb */
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(5);
                p.setColor(Color.BLACK);
                canvas.drawCircle(x,y,radius,p);
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    //Kill thread on activity paused

    public void stop() {
        if(thread!=null && thread.isAlive()) {
//            try {
                // Stop the thread == rejoin the main thread.
                thread.interrupt(); //using interrupt instead of join because no need to wait for thread to finish task in this case
//            } catch (InterruptedException e) {
//            }
            mSurfaceHolder.removeCallback(this);
        }
    }

    //Start the thread when activity started
    public void resume() {
        if (thread == null || !thread.isAlive()) {
            mSurfaceHolder.addCallback(this);
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = Math.min(getWidth(),Math.max(0,event.getX()));
        y = Math.min(getHeight(),Math.max(0,event.getY()));
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //since hex text changes too we do this to avoid afterTextChanged being called and ruining it smh...
                hexText.setEnabled(false);

                userHSV[0]=hsv[0];
                userHSV[1] = x/getWidth();
                userHSV[2] = (getHeight()-y)/getHeight(); //we do this to avoid rotating our BG image to fit android coords
                userSelectedColour = Color.HSVToColor(userHSV);
                hexText.setText(getContext().getString(R.string.hexString,Integer.toHexString(userSelectedColour).substring(2)));
                userColourPreviewInterface.onChanged(userSelectedColour);
                break;
            case MotionEvent.ACTION_MOVE:
                userHSV[1] = x/getWidth();
                userHSV[2] = (getHeight()-y)/getHeight();
                userSelectedColour =Color.HSVToColor(userHSV);
                hexText.setText(getContext().getString(R.string.hexString,Integer.toHexString(userSelectedColour).substring(2)));
                userColourPreviewInterface.onChanged(userSelectedColour);
                break;
            case MotionEvent.ACTION_UP:
                hexText.setEnabled(true);
                break;
        }
        return true;
    }
}
