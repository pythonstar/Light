package com.bigsing.light;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class selHzView extends RelativeLayout {
    private static float m_height;
    private static float m_width;
    private float a;
    private float b;
    private float f;

    public selHzView(final Context context) {
        super(context);
    }

    public selHzView(final Context context, final AttributeSet set) {
        super(context, set);
    }

    public static void setScreenSize(final int width, final int height) {
        selHzView.m_height = height;
        selHzView.m_width = width;
    }

    public void a(final int n) {
        this.f = (float) n - 0.0625f * selHzView.m_width;
    }

    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final float n = 0.0f;
        final int action = motionEvent.getAction();
        final float rawX = motionEvent.getRawX();
        final float rawY = motionEvent.getRawY();
        this.getLayoutParams();
        switch (action) {
            case 0: {
                Log.d("Touch", "DOWN");
                this.a = rawX;
                this.b = rawY;
                break;
            }
            case 2: {
                final float n2 = (float) this.getScrollX() - (rawX - this.a);
                Log.d("getHZ", new StringBuilder().append(n2).toString());
                float n3 = n2;
                if (n2 < -this.f) {
                    n3 = -this.f;
                }
                if (n3 > 0.0f) {
                    n3 = n;
                }
                this.scrollTo((int) n3, 0);
                //this.e.a((int)(-n3 / (this.f / 18.0f) + 2.0f));
                this.b = rawY;
                this.a = rawX;
                break;
            }
            case 3: {
                Log.d("Touch", "CANCEL");
                break;
            }
        }
        this.invalidate();
        return true;
    }

}
