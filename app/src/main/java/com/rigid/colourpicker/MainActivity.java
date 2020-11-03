package com.rigid.colourpicker;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
/**
 * Created By: Munim Ahmad (03/11/20)
 *
 * Quick And Easy Colour Picker
 *
 * IMPROVEMENTS/NEW FEATURES WELCOME
 *
 * */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //link our interfaces
        View v = findViewById(R.id.huedisplay);
        View v2 = findViewById(R.id.hueslider);
        ((HueSlider)v2).setHueChangeInterFace((HueDisplaySurfaceView)v);
        ((HueDisplaySurfaceView)v).setUserColourPreviewInterface((UserColourPreviewInterface) findViewById(R.id.colourpreview));
        ((HueDisplaySurfaceView)v).setHexChangedInterface((HueSlider)v2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop the thread here
        ((HueDisplaySurfaceView)findViewById(R.id.huedisplay)).stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //resume here
        ((HueDisplaySurfaceView)findViewById(R.id.huedisplay)).resume();
    }
}
