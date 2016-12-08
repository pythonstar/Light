package com.bigsing.light;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.bigsing.light.R;

/*
荧光棒效果
 */
public class LightStickActivity extends Activity {

    private View viewLightStickBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightstick);

        viewLightStickBack = findViewById(R.id.viewLightStickBack);
        viewLightStickBack.setBackgroundResource(R.color.Blue2);
    }
}
