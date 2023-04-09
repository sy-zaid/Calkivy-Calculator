package org.libsdl.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import org.libsdl.app.SDLActivity;

/* compiled from: SDLActivity */
class SDLSurface extends SurfaceView implements SurfaceHolder.Callback, View.OnKeyListener, View.OnTouchListener, SensorEventListener {
    protected static Display mDisplay;
    protected static float mHeight;
    protected static SensorManager mSensorManager;
    protected static float mWidth;

    public SDLSurface(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(this);
        setOnTouchListener(this);
        mDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        mSensorManager = (SensorManager) context.getSystemService("sensor");
        if (Build.VERSION.SDK_INT >= 12) {
            setOnGenericMotionListener(SDLActivity.getMotionListener());
        }
        mWidth = 1.0f;
        mHeight = 1.0f;
    }

    public void handlePause() {
        enableSensor(1, false);
    }

    public void handleResume() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(this);
        setOnTouchListener(this);
        enableSensor(1, true);
    }

    public Surface getNativeSurface() {
        return getHolder().getSurface();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.v("SDL", "surfaceCreated()");
        holder.setType(2);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v("SDL", "surfaceDestroyed()");
        SDLActivity.mNextNativeState = SDLActivity.NativeState.PAUSED;
        SDLActivity.handleNativeState();
        SDLActivity.mIsSurfaceReady = false;
        SDLActivity.onNativeSurfaceDestroyed();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        int sdlFormat;
        int i = format;
        int i2 = width;
        int i3 = height;
        Log.v("SDL", "surfaceChanged()");
        if (SDLActivity.mSingleton != null) {
            switch (i) {
                case 1:
                    Log.v("SDL", "pixel format RGBA_8888");
                    sdlFormat = 373694468;
                    break;
                case 2:
                    Log.v("SDL", "pixel format RGBX_8888");
                    sdlFormat = 371595268;
                    break;
                case 3:
                    Log.v("SDL", "pixel format RGB_888");
                    sdlFormat = 370546692;
                    break;
                case 4:
                    Log.v("SDL", "pixel format RGB_565");
                    sdlFormat = 353701890;
                    break;
                case 6:
                    Log.v("SDL", "pixel format RGBA_5551");
                    sdlFormat = 356782082;
                    break;
                case 7:
                    Log.v("SDL", "pixel format RGBA_4444");
                    sdlFormat = 356651010;
                    break;
                case 8:
                    Log.v("SDL", "pixel format A_8");
                    break;
                case 9:
                    Log.v("SDL", "pixel format L_8");
                    break;
                case 10:
                    Log.v("SDL", "pixel format LA_88");
                    break;
                case 11:
                    Log.v("SDL", "pixel format RGB_332");
                    sdlFormat = 336660481;
                    break;
                default:
                    Log.v("SDL", "pixel format unknown " + i);
                    break;
            }
            sdlFormat = 353701890;
            mWidth = (float) i2;
            mHeight = (float) i3;
            int nDeviceWidth = width;
            int nDeviceHeight = height;
            try {
                if (Build.VERSION.SDK_INT >= 17) {
                    DisplayMetrics realMetrics = new DisplayMetrics();
                    mDisplay.getRealMetrics(realMetrics);
                    nDeviceWidth = realMetrics.widthPixels;
                    nDeviceHeight = realMetrics.heightPixels;
                }
            } catch (Throwable th) {
            }
            int nDeviceWidth2 = nDeviceWidth;
            int nDeviceHeight2 = nDeviceHeight;
            synchronized (SDLActivity.getContext()) {
                SDLActivity.getContext().notifyAll();
            }
            Log.v("SDL", "Window size: " + i2 + "x" + i3);
            Log.v("SDL", "Device size: " + nDeviceWidth2 + "x" + nDeviceHeight2);
            SDLActivity.onNativeResize(width, height, nDeviceWidth2, nDeviceHeight2, sdlFormat, mDisplay.getRefreshRate());
            boolean skip = false;
            int requestedOrientation = SDLActivity.mSingleton.getRequestedOrientation();
            if (requestedOrientation != -1) {
                if (requestedOrientation == 1 || requestedOrientation == 7) {
                    if (mWidth > mHeight) {
                        skip = true;
                    }
                } else if ((requestedOrientation == 0 || requestedOrientation == 6) && mWidth < mHeight) {
                    skip = true;
                }
            }
            if (skip) {
                if (((double) Math.max(mWidth, mHeight)) / ((double) Math.min(mWidth, mHeight)) < 1.2d) {
                    Log.v("SDL", "Don't skip on such aspect-ratio. Could be a square resolution.");
                    skip = false;
                }
            }
            if (skip) {
                Log.v("SDL", "Skip .. Surface is not ready.");
                SDLActivity.mIsSurfaceReady = false;
                return;
            }
            SDLActivity.mIsSurfaceReady = true;
            SDLActivity.onNativeSurfaceChanged();
            SDLActivity.handleNativeState();
        }
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (SDLControllerManager.isDeviceSDLJoystick(event.getDeviceId())) {
            if (event.getAction() == 0) {
                if (SDLControllerManager.onNativePadDown(event.getDeviceId(), keyCode) == 0) {
                    return true;
                }
            } else if (event.getAction() == 1 && SDLControllerManager.onNativePadUp(event.getDeviceId(), keyCode) == 0) {
                return true;
            }
        }
        if ((event.getSource() & 257) != 0) {
            if (event.getAction() == 0) {
                if (SDLActivity.isTextInputEvent(event)) {
                    SDLInputConnection.nativeCommitText(String.valueOf((char) event.getUnicodeChar()), 1);
                }
                SDLActivity.onNativeKeyDown(keyCode);
                return true;
            } else if (event.getAction() == 1) {
                SDLActivity.onNativeKeyUp(keyCode);
                return true;
            }
        }
        if ((event.getSource() & 8194) == 0) {
            return false;
        }
        if (keyCode != 4 && keyCode != 125) {
            return false;
        }
        switch (event.getAction()) {
            case 0:
            case 1:
                return true;
            default:
                return false;
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        int i;
        float p;
        int mouseButton;
        MotionEvent motionEvent = event;
        int touchDevId = event.getDeviceId();
        int pointerCount = event.getPointerCount();
        int action = event.getActionMasked();
        int i2 = -1;
        if ((event.getSource() == 8194 || event.getSource() == 12290) && SDLActivity.mSeparateMouseAndTouch) {
            if (Build.VERSION.SDK_INT < 14) {
                mouseButton = 1;
            } else {
                try {
                    mouseButton = ((Integer) event.getClass().getMethod("getButtonState", new Class[0]).invoke(motionEvent, new Object[0])).intValue();
                } catch (Exception e) {
                    mouseButton = 1;
                }
            }
            SDLGenericMotionListener_API12 motionListener = SDLActivity.getMotionListener();
            SDLActivity.onNativeMouse(mouseButton, action, motionListener.getEventX(motionEvent), motionListener.getEventY(motionEvent), motionListener.inRelativeMode());
            return true;
        }
        switch (action) {
            case 0:
            case 1:
                i2 = 0;
                break;
            case 2:
                int i3 = 0;
                while (i3 < pointerCount) {
                    int pointerFingerId = motionEvent.getPointerId(i3);
                    float x = motionEvent.getX(i3) / mWidth;
                    float y = motionEvent.getY(i3) / mHeight;
                    float p2 = motionEvent.getPressure(i3);
                    if (p2 > 1.0f) {
                        p2 = 1.0f;
                    }
                    SDLActivity.onNativeTouch(touchDevId, pointerFingerId, action, x, y, p2);
                    i3++;
                }
                int i4 = i3;
                return true;
            case 3:
                int i5 = 0;
                while (i5 < pointerCount) {
                    int pointerFingerId2 = motionEvent.getPointerId(i5);
                    float x2 = motionEvent.getX(i5) / mWidth;
                    float y2 = motionEvent.getY(i5) / mHeight;
                    float p3 = motionEvent.getPressure(i5);
                    if (p3 > 1.0f) {
                        p3 = 1.0f;
                    }
                    SDLActivity.onNativeTouch(touchDevId, pointerFingerId2, 1, x2, y2, p3);
                    i5++;
                }
                int i6 = i5;
                return true;
            case 5:
            case 6:
                break;
            default:
                return true;
        }
        if (i2 == -1) {
            i = event.getActionIndex();
        } else {
            i = i2;
        }
        int pointerFingerId3 = motionEvent.getPointerId(i);
        float x3 = motionEvent.getX(i) / mWidth;
        float y3 = motionEvent.getY(i) / mHeight;
        float p4 = motionEvent.getPressure(i);
        if (p4 > 1.0f) {
            p = 1.0f;
        } else {
            p = p4;
        }
        SDLActivity.onNativeTouch(touchDevId, pointerFingerId3, action, x3, y3, p);
        int i7 = i;
        return true;
    }

    public void enableSensor(int sensortype, boolean enabled) {
        if (enabled) {
            SensorManager sensorManager = mSensorManager;
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(sensortype), 1, (Handler) null);
            return;
        }
        SensorManager sensorManager2 = mSensorManager;
        sensorManager2.unregisterListener(this, sensorManager2.getDefaultSensor(sensortype));
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        float x;
        float y;
        int newOrientation;
        if (event.sensor.getType() == 1) {
            switch (mDisplay.getRotation()) {
                case 1:
                    y = -event.values[1];
                    x = event.values[0];
                    newOrientation = 1;
                    break;
                case 2:
                    y = -event.values[1];
                    x = -event.values[0];
                    newOrientation = 4;
                    break;
                case 3:
                    y = event.values[1];
                    x = -event.values[0];
                    newOrientation = 2;
                    break;
                default:
                    newOrientation = 3;
                    float f = event.values[0];
                    x = event.values[1];
                    y = f;
                    break;
            }
            if (newOrientation != SDLActivity.mCurrentOrientation) {
                SDLActivity.mCurrentOrientation = newOrientation;
                SDLActivity.onNativeOrientationChanged(newOrientation);
            }
            SDLActivity.onNativeAccel((-y) / 9.80665f, x / 9.80665f, event.values[2] / 9.80665f);
        }
    }

    public boolean onCapturedPointerEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case 2:
            case 7:
                SDLActivity.onNativeMouse(0, action, event.getX(0), event.getY(0), true);
                return true;
            case 8:
                SDLActivity.onNativeMouse(0, action, event.getAxisValue(10, 0), event.getAxisValue(9, 0), false);
                return true;
            case 11:
            case 12:
                if (action == 11) {
                    action = 0;
                } else if (action == 12) {
                    action = 1;
                }
                SDLActivity.onNativeMouse(event.getButtonState(), action, event.getX(0), event.getY(0), true);
                return true;
            default:
                return false;
        }
    }
}
