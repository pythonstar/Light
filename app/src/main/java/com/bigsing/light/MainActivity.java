package com.bigsing.light;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigsing.light.DragLayout;
import com.bigsing.light.FlashLightConfig;
import com.bigsing.light.R;
import com.bigsing.light.selHzView;
import com.bigsing.light.util.EasyPermissions;

import java.io.IOException;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    private static final int REQUEST_CODE_CAMERA = 1;

    private boolean m_bCameraShouldOff = false;    //是否应该关闭手电筒
    private boolean m_bLeftBtnOn = false;           //左侧按钮是否打开
    private boolean m_bRightBtnOn = false;          //右侧按钮是否打开
    private boolean m_bMainBtnOn = true;            //主按钮是否打开，默认打开
    private Thread m_threadSOS;
    private int A;
    private boolean B = false;
    private boolean C = false;
    private ImageView m_hzbar;
    private FlashLightConfig m_config;
    private boolean H = true;
    private boolean I = false;
    private boolean m_bCameraFailed = false;
    private boolean m_bAppExit = false;
    SurfaceHolder m_surfaceHolder;
    private Context m_context;
    private boolean m_bCameraPreviewed = false;
    private Button m_btn_light = null;
    private Camera m_camera = null;
    private Camera.Parameters m_camParams = null;
    public RelativeLayout m_draglayout_linear;
    private DragLayout m_draglayout;
    private RelativeLayout m_lock_setting;
    private ImageView m_imgLock;
    private Button m_sosbutton;
    private Button m_leftbutton;
    private Button m_rightbutton;
    private Button m_setbutton;
    // TODO: 2015/12/14
    //private AdView r;
    private TextView m_hznumber;
    private TextView m_lock_setting_text;
    private com.bigsing.light.selHzView m_hzlabel;

    private static final int MSG_UPDATEUI_SWITCHON = 4;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2: {
                    MainActivity.this.m_config.b((String) msg.obj);
                    break;
                }
                case 3: {
                    MainActivity.this.m_config.b((String) msg.obj);
                    break;
                }
                case MSG_UPDATEUI_SWITCHON:
                    m_btn_light.setBackgroundResource(R.drawable.mainbutton_on1);
                    break;
            }

            super.handleMessage(msg);
        }
    };

    /////////////////////////////////////////////////////////////////////////////
    public void onOpenCameraError() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setTitle("摄像头被其他应用占用，请先关闭照相机或者其他手电筒应用!");
        localBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        localBuilder.create().show();
    }

    public void onPermissionDenied() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setTitle("请在设置里运行使用摄像头权限，否则无法使用手电筒!");
        localBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        localBuilder.create().show();
    }

    public void turnOffCamera() {
        if (m_camera != null && m_bAppExit == false) {
            m_camParams.setFlashMode("off");
            m_camera.setParameters(m_camParams);
        }
    }

    public void turnOnCamera() {
        if (m_camera != null && m_bAppExit == false) {
            m_camParams.setFlashMode("torch");
            m_camera.setParameters(m_camParams);
            if (m_bCameraPreviewed == false) {
                try {
                    m_camera.setPreviewDisplay(m_surfaceHolder);
                    m_camera.startPreview();
                    m_bCameraPreviewed = true;
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        m_camera = Camera.open();
//        m_camParams = m_camera.getParameters();
//        m_camera.startPreview();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                try {
                    this.m_camera = Camera.open();
                    this.m_camParams = this.m_camera.getParameters();
                    this.m_camera.startPreview();
                } catch (Exception e) {
                    onOpenCameraError();
                    this.m_bCameraFailed = true;
                }
            } else {
                // Permission Denied
                onPermissionDenied();
            }
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        m_config = new FlashLightConfig(this, "setting");

        boolean bCanOpen = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (EasyPermissions.hasPermissions(this, perms)) {
                // Already have permission, do the thing
                bCanOpen = true;
            } else {
                // Do not have permissions, request them now
                requestPermissions(perms, REQUEST_CODE_CAMERA);
            }
        } else {
            bCanOpen = true;
        }
        if (bCanOpen == true) {
            try {
                this.m_camera = Camera.open();
                this.m_camParams = this.m_camera.getParameters();
                this.m_camera.startPreview();
            } catch (Exception e) {
                onOpenCameraError();
                this.m_bCameraFailed = true;
            }
        }

        final int width = getWindowManager().getDefaultDisplay().getWidth();
        final int height = getWindowManager().getDefaultDisplay().getHeight();

        SurfaceView sfPreview = (SurfaceView) findViewById(R.id.sfPreview);
        ViewGroup.LayoutParams layoutParams = sfPreview.getLayoutParams();
        layoutParams.width = 1;
        layoutParams.height = 1;
        sfPreview.setLayoutParams(layoutParams);
        sfPreview.setZOrderOnTop(true);
        sfPreview.setBackgroundColor(Color.WHITE);
        sfPreview.setKeepScreenOn(true);          //保持屏幕常亮

        m_surfaceHolder = sfPreview.getHolder();
        m_surfaceHolder.addCallback(this);
        m_surfaceHolder.setFormat(-2);

        //抽屉
        m_draglayout = (DragLayout) this.findViewById(R.id.draglayout);
        m_draglayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (!B) {
                    m_draglayout.a(m_config.getPageIndex());
                    m_draglayout.a(true);   //根据配置是否显示，这里先写死 // TODO: 2015/12/16
                }

                //FlashLightActivity.this.a(FlashLightActivity.this, true);
                return true;
            }
        });
        m_draglayout.setScreenSize(width, height);
        selHzView.setScreenSize(width, height);

        //赫兹HZ，闪光频率
        m_hzbar = (ImageView) this.findViewById(R.id.hzbar);
        m_hzbar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (!C) {
                    m_hzlabel.a(m_hzbar.getWidth());
                }
                // TODO: 2015/12/14
                //FlashLightActivity.mHandler(this.a, true);
                return true;
            }
        });

        m_lock_setting_text = (TextView) findViewById(R.id.lock_setting_text);
        m_hzlabel = (selHzView) findViewById(R.id.hzlabel);
        m_hznumber = (TextView) findViewById(R.id.hznumber);
        A = 2;
        m_hznumber.setText((CharSequence) new StringBuilder().append(this.A).toString());
        //m_hzlabel.setListener(new ab(this));
        m_draglayout_linear = (RelativeLayout) findViewById(R.id.draglayout_linear);
        m_btn_light = (Button) this.findViewById(R.id.btn_light);
        m_btn_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_bMainBtnOn == true) {
                    //关闭
                    m_bCameraShouldOff = true;
                    m_btn_light.setBackgroundResource(R.drawable.mainbutton_off1);
                    turnOffCamera();
                    m_bMainBtnOn = false;
                } else {
                    //打开
                    m_btn_light.setBackgroundResource(R.drawable.mainbutton_on1);
                    turnOnCamera();
                    m_bMainBtnOn = true;
                }
            }
        });

        //锁的按钮
        m_lock_setting = (RelativeLayout) this.findViewById(R.id.lock_setting);
        m_imgLock = (ImageView) this.findViewById(R.id.lock_setting1);
        m_lock_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_config.getPageIndex() == true) {
                    m_config.setPageIndex(false);
                    m_lock_setting_text.setText((CharSequence) "打开");
                    m_draglayout_linear.setBackgroundResource(R.drawable.tabbackground);
                    m_draglayout.setTouchable(true);
                    m_imgLock.setBackgroundResource(R.drawable.unlocked_icon);
                } else {
                    m_draglayout_linear.setBackgroundResource(R.color.transparent);
                    m_lock_setting_text.setText((CharSequence) "关闭");
                    m_config.setPageIndex(true);
                    m_draglayout.setTouchable(false);
                    m_imgLock.setBackgroundResource(R.drawable.locked_icon);
                }
            }
        });

        //警报灯
        m_sosbutton = (Button) findViewById(R.id.sosbutton);
        m_sosbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_threadSOS == null) {
                    m_threadSOS = new Thread() {
                        public void run() {
                            int adslineaer = 0;
                            while (m_bCameraShouldOff == false) {
                                try {
                                    turnOnCamera();
                                    Thread.sleep(700L);
                                    turnOffCamera();
                                    Thread.sleep(200L);

                                    turnOnCamera();
                                    Thread.sleep(300L);
                                    turnOffCamera();
                                    Thread.sleep(300L);

                                    turnOnCamera();
                                    Thread.sleep(800L);
                                    turnOffCamera();
                                    Thread.sleep(800L);
                                    adslineaer++;

                                    if (adslineaer >= 3) {
                                        Thread.sleep(800L);
                                        adslineaer = 0;
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }//end while

                            //退出循环
                            m_threadSOS = null;
                        }
                    };

                    m_bCameraShouldOff = false;
                    //m_sosbutton.setBackgroundResource(R.drawable.sosbutton_off);
                    m_threadSOS.start();
                } else {
                    //m_btn_light.setBackgroundResource(R.drawable.sosbutton_off);
                }

            }
        });
        m_leftbutton = (Button) this.findViewById(R.id.leftbutton);
        m_leftbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_bLeftBtnOn == false) {
                    //打开
                    m_bCameraShouldOff = false;
                    m_leftbutton.setBackgroundResource(R.drawable.switch_on);
                    m_btn_light.setBackgroundResource(R.drawable.mainbutton_on1);
                    new Thread() {
                        public void run() {
                            while (m_bCameraShouldOff == false) {
                                try {
                                    final int n = 1000 / A / 2;
                                    turnOnCamera();
                                    Thread.sleep(n);
                                    turnOffCamera();
                                    Thread.sleep(n);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }.start();
                } else {
                    //关闭
                    m_bCameraShouldOff = true;
                    m_leftbutton.setBackgroundResource(R.drawable.switch_off);
                    m_btn_light.setBackgroundResource(R.drawable.mainbutton_off1);
                }
                m_bLeftBtnOn = !m_bLeftBtnOn;
            }
        });
        m_rightbutton = (Button) this.findViewById(R.id.rightbutton);
        m_rightbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (m_bRightBtnOn == false) {
                    //打开
                    m_bCameraShouldOff = false;
                    m_rightbutton.setBackgroundResource(R.drawable.switch_on);
                    m_btn_light.setBackgroundResource(R.drawable.mainbutton_on1);
                    new Thread() {
                        public void run() {
                            while (m_bCameraShouldOff == false) {
                                try {
                                    final int n1 = 1000 / A / 5;
                                    final int n2 = 1000 / A / 2;
                                    turnOnCamera();
                                    Thread.sleep(n1);
                                    turnOffCamera();
                                    Thread.sleep(n2);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }.start();
                } else {
                    //关闭
                    m_bCameraShouldOff = true;
                    m_rightbutton.setBackgroundResource(R.drawable.switch_off);
                    m_btn_light.setBackgroundResource(R.drawable.mainbutton_off1);
                }
                m_bRightBtnOn = !m_bRightBtnOn;
            }
        });
        this.m_setbutton = (Button) this.findViewById(R.id.setbutton);
        this.m_setbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(FlashLightActivity.this, FlashLightSettingActivity.class);
//                startActivity(intent);
            }
        });
        m_context = (Context) this;
        final RelativeLayout relativeLayout = (RelativeLayout) this.findViewById(R.id.relative1);
        final RelativeLayout relativeLayout2 = (RelativeLayout) this.findViewById(R.id.relative2);
        final RelativeLayout relativeLayout3 = (RelativeLayout) this.findViewById(R.id.relative3);
        final RelativeLayout relativeLayout4 = (RelativeLayout) this.findViewById(R.id.relative4);

        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams1.setMargins(0, (int) ((float) height * 0.104166664f), 0, 0);
        relativeLayout.setLayoutParams((ViewGroup.LayoutParams) layoutParams1);

        float n = 0.104166664f + 0.104166664f;
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams2.setMargins(0, (int) ((float) height * n), 0, 0);
        relativeLayout2.setLayoutParams((ViewGroup.LayoutParams) layoutParams2);
        n += 0.104166664f;

        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams3.setMargins(0, (int) ((float) height * n), 0, 0);
        relativeLayout3.setLayoutParams((ViewGroup.LayoutParams) layoutParams3);

        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams4.setMargins(0, (int) ((n + 0.078125f) * (float) height), 0, 0);
        relativeLayout4.setLayoutParams((ViewGroup.LayoutParams) layoutParams4);

        RelativeLayout.LayoutParams layoutParams5 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams5.setMargins(0, (int) (0.09375f * (float) height), 0, 0);
        layoutParams5.addRule(RelativeLayout.CENTER_HORIZONTAL);
        m_btn_light.setLayoutParams(layoutParams5);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (m_camera != null) {
            m_camera.stopPreview();
            m_camera.release();
            m_camera = null;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.m_camera.stopPreview();
            this.m_camera.release();
            this.m_camera = null;
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    private class TaskUISwitchOn extends AsyncTask {

        @Override
        protected Void doInBackground(Object[] params) {
            try {
                Thread.sleep(300L);
                MainActivity.this.turnOnCamera();
                mHandler.sendEmptyMessage(MSG_UPDATEUI_SWITCHON);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public void onWindowFocusChanged(boolean paramBoolean) {
        super.onWindowFocusChanged(paramBoolean);
        if (m_bCameraFailed)
            finish();
        if (this.I == true || m_bCameraFailed || m_config.getQickStart() == false)
            return;
        new TaskUISwitchOn().execute();
        this.I = true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }


}

