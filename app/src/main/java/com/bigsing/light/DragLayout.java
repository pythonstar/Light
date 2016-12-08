package com.bigsing.light;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class DragLayout extends RelativeLayout implements Runnable {
    private static float m_height;
    private static float m_width;
    private float a;
    private float b;
    private float c = 0.0F;
    private float d;
    private long g;
    private long h;
    private float i = 0.0F;
    private float j = 0.0F;
    private float k = 0.0F;
    private boolean m = true;
    private boolean n = false;
    private boolean o = false;
    private boolean p = false;
    private float q = getHeight();
    private float r = 100.0F;
    private float s;
    private float t;
    private int u = 30;

    public DragLayout(Context paramContext) {
        super(paramContext);
    }

    public DragLayout(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public void a(float paramFloat) {
        if (this.p)
            return;
        this.r = paramFloat;
        if (Math.abs(this.s - this.d) < 1.0F)
            this.s = 0.0F;
        this.d = 0.0F;
        if (Math.abs(this.s - this.c) < 1.0F)
            this.s = (-getHeight() + m_height * 0.0625F + this.r);
        this.c = (-getHeight() + m_height * 0.0625F + this.r);
        if (Math.abs(getScrollY()) > 1) {
            this.c = (-getHeight() + m_height * 0.0625F + this.r);
            scrollTo(0, (int) this.c);
        }
        this.p = true;
    }

    public void setScreenSize(int width, int height) {
        m_width = width;
        m_height = height;
        setVisibility(VISIBLE);
    }

    public void a(final boolean b) {
        this.b = 0.8020833f * DragLayout.m_height;
        this.c = -0.5208333f * DragLayout.m_height;
        this.c = (float) ((-this.getHeight())) + 0.083333336f * DragLayout.m_height;
        if (this.p) {
            this.c = (float) ((-this.getHeight())) + DragLayout.m_height * 0.0625f + this.r;
        } else {
            this.c = (float) ((-this.getHeight())) + DragLayout.m_height * 0.0625f;
        }
        this.d = DragLayout.m_height * 0.0f;
        if (!b) {
            this.j = this.c;
        } else {
            this.j = this.d;
        }
        this.scrollTo(0, (int) this.j);
        final long currentTimeMillis = System.currentTimeMillis();
        this.h = currentTimeMillis;
        this.g = currentTimeMillis;
        this.k = 0.0f;
        this.i = 0.0f;
        this.o = true;
        this.invalidate();
    }

    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final boolean b = true;
        final int action = motionEvent.getAction();
        motionEvent.getRawX();
        final float rawY = motionEvent.getRawY();
        boolean b2 = false;
        switch (action) {
            case 0: {
                Log.d("Touch", "DOWN");
                final float x = motionEvent.getX();
                final float y = motionEvent.getY();
                if (x > DragLayout.m_width * 1.0f / 3.0f * 1.0f && x < DragLayout.m_width * 1.0f / 3.0f * 2.0f && y < (float) ((-this.getScrollY())) + 0.104166664f * DragLayout.m_height && y > (float) ((-this.getScrollY())) + 0.0f && this.m) {
                    this.a = rawY;
                    b2 = b;
                    break;
                }
                b2 = false;
                break;
            }
            case 1: {
                if (this.n) {
                    this.n = false;
                    this.t = this.getScrollY();
                    this.s = this.c;
                    this.postDelayed((Runnable) this, 20L);
                } else {
                    this.n = true;
                    this.t = this.getScrollY();
                    this.s = this.d;
                    this.postDelayed((Runnable) this, 20L);
                }
                this.h = System.currentTimeMillis();
                b2 = b;
                break;
            }
            case 2: {
                b2 = b;
                if (Math.abs(rawY - this.a) > 10.0f) {
                    this.j = (float) this.getScrollY() - (rawY - this.a);
                    if (this.j < this.c) {
                        this.j = this.c;
                    }
                    if (this.j > this.d) {
                        this.j = this.d;
                    }
                    this.a = rawY;
                    this.scrollTo(0, (int) this.j);
                    b2 = b;
                    break;
                }
                break;
            }
            case 3: {
                Log.d("Touch", "CANCEL");
                b2 = b;
                break;
            }
            default: {
                b2 = b;
                break;
            }
        }
        if (b2) {
            this.invalidate();
        }
        return b2;
    }

    public void run() {
        long l1 = System.currentTimeMillis();
        float f1 = Math.min(1.0F, (float) (l1 - this.h) * 1.0F / 1000.0F);
        if ((float) (l1 - this.h) >= 1000.0F) {
            scrollTo(0, (int) this.s);
            return;
        }
        scrollTo(0, (int) ((this.s - this.t) * f1 + this.t));
        postDelayed(this, 20L);
    }

    public void setTouchable(boolean paramBoolean) {
        this.m = paramBoolean;
    }
}
