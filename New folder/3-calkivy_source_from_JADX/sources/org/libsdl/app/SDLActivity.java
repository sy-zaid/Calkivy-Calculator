package org.libsdl.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Hashtable;
import org.kamranzafar.jtar.TarConstants;

public class SDLActivity extends Activity implements View.OnSystemUiVisibilityChangeListener {
    static final int COMMAND_CHANGE_TITLE = 1;
    static final int COMMAND_CHANGE_WINDOW_STYLE = 2;
    static final int COMMAND_SET_KEEP_SCREEN_ON = 5;
    static final int COMMAND_TEXTEDIT_HIDE = 3;
    protected static final int COMMAND_USER = 32768;
    protected static final int SDL_ORIENTATION_LANDSCAPE = 1;
    protected static final int SDL_ORIENTATION_LANDSCAPE_FLIPPED = 2;
    protected static final int SDL_ORIENTATION_PORTRAIT = 3;
    protected static final int SDL_ORIENTATION_PORTRAIT_FLIPPED = 4;
    protected static final int SDL_ORIENTATION_UNKNOWN = 0;
    private static final int SDL_SYSTEM_CURSOR_ARROW = 0;
    private static final int SDL_SYSTEM_CURSOR_CROSSHAIR = 3;
    private static final int SDL_SYSTEM_CURSOR_HAND = 11;
    private static final int SDL_SYSTEM_CURSOR_IBEAM = 1;
    private static final int SDL_SYSTEM_CURSOR_NO = 10;
    private static final int SDL_SYSTEM_CURSOR_NONE = -1;
    private static final int SDL_SYSTEM_CURSOR_SIZEALL = 9;
    private static final int SDL_SYSTEM_CURSOR_SIZENESW = 6;
    private static final int SDL_SYSTEM_CURSOR_SIZENS = 8;
    private static final int SDL_SYSTEM_CURSOR_SIZENWSE = 5;
    private static final int SDL_SYSTEM_CURSOR_SIZEWE = 7;
    private static final int SDL_SYSTEM_CURSOR_WAIT = 2;
    private static final int SDL_SYSTEM_CURSOR_WAITARROW = 4;
    private static final String TAG = "SDL";
    private static Object expansionFile;
    private static Method expansionFileMethod;
    public static int keyboardInputType = 145;
    public static boolean mBrokenLibraries;
    protected static SDLClipboardHandler mClipboardHandler;
    public static NativeState mCurrentNativeState;
    protected static int mCurrentOrientation;
    protected static Hashtable<Integer, Object> mCursors;
    public static boolean mExitCalledFromJava;
    protected static boolean mFullscreenModeActive;
    protected static HIDDeviceManager mHIDDeviceManager;
    public static boolean mHasFocus;
    public static boolean mIsResumedCalled;
    public static boolean mIsSurfaceReady;
    protected static int mLastCursorID;
    protected static ViewGroup mLayout;
    protected static SDLGenericMotionListener_API12 mMotionListener;
    public static NativeState mNextNativeState;
    protected static Thread mSDLThread;
    protected static boolean mScreenKeyboardShown;
    public static boolean mSeparateMouseAndTouch;
    protected static SDLActivity mSingleton;
    protected static SDLSurface mSurface;
    protected static View mTextEdit;
    Handler commandHandler = new SDLCommandHandler();
    protected int dialogs = 0;
    protected final int[] messageboxSelection = new int[1];
    private final Runnable rehideSystemUi = new Runnable() {
        public void run() {
            SDLActivity.this.getWindow().getDecorView().setSystemUiVisibility(5894);
        }
    };

    public enum NativeState {
        INIT,
        RESUMED,
        PAUSED
    }

    public static native String nativeGetHint(String str);

    public static native void nativeLowMemory();

    public static native void nativePause();

    public static native void nativeQuit();

    public static native void nativeResume();

    public static native int nativeRunMain(String str, String str2, Object obj);

    public static native void nativeSetenv(String str, String str2);

    public static native int nativeSetupJNI();

    public static native void onNativeAccel(float f, float f2, float f3);

    public static native void onNativeClipboardChanged();

    public static native void onNativeDropFile(String str);

    public static native void onNativeKeyDown(int i);

    public static native void onNativeKeyUp(int i);

    public static native void onNativeKeyboardFocusLost();

    public static native void onNativeMouse(int i, int i2, float f, float f2, boolean z);

    public static native void onNativeOrientationChanged(int i);

    public static native void onNativeResize(int i, int i2, int i3, int i4, int i5, float f);

    public static native void onNativeSurfaceChanged();

    public static native void onNativeSurfaceDestroyed();

    public static native void onNativeTouch(int i, int i2, int i3, float f, float f2, float f3);

    protected static SDLGenericMotionListener_API12 getMotionListener() {
        if (mMotionListener == null) {
            if (Build.VERSION.SDK_INT >= 26) {
                mMotionListener = new SDLGenericMotionListener_API26();
            } else if (Build.VERSION.SDK_INT >= 24) {
                mMotionListener = new SDLGenericMotionListener_API24();
            } else {
                mMotionListener = new SDLGenericMotionListener_API12();
            }
        }
        return mMotionListener;
    }

    /* access modifiers changed from: protected */
    public String getMainSharedObject() {
        String library;
        String[] libraries = mSingleton.getLibraries();
        if (libraries.length > 0) {
            library = "lib" + libraries[libraries.length + SDL_SYSTEM_CURSOR_NONE] + ".so";
        } else {
            library = "libmain.so";
        }
        return getContext().getApplicationInfo().nativeLibraryDir + "/" + library;
    }

    /* access modifiers changed from: protected */
    public String getMainFunction() {
        return "SDL_main";
    }

    /* access modifiers changed from: protected */
    public String[] getLibraries() {
        return new String[]{"SDL2", "main"};
    }

    public void loadLibraries() {
        for (String lib : getLibraries()) {
            SDL.loadLibrary(lib);
        }
    }

    /* access modifiers changed from: protected */
    public String[] getArguments() {
        return new String[0];
    }

    public static void initialize() {
        mSingleton = null;
        mSurface = null;
        mTextEdit = null;
        mLayout = null;
        mClipboardHandler = null;
        mCursors = new Hashtable<>();
        mLastCursorID = 0;
        mSDLThread = null;
        mExitCalledFromJava = false;
        mBrokenLibraries = false;
        mIsResumedCalled = false;
        mIsSurfaceReady = false;
        mHasFocus = true;
        mNextNativeState = NativeState.INIT;
        mCurrentNativeState = NativeState.INIT;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "Device: " + Build.DEVICE);
        Log.v(TAG, "Model: " + Build.MODEL);
        Log.v(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        initialize();
        mSingleton = this;
    }

    /* access modifiers changed from: protected */
    public void finishLoad() {
        String filename;
        String errorMsgBrokenLib = "";
        try {
            loadLibraries();
        } catch (UnsatisfiedLinkError e) {
            System.err.println(e.getMessage());
            mBrokenLibraries = true;
            errorMsgBrokenLib = e.getMessage();
        } catch (Exception e2) {
            System.err.println(e2.getMessage());
            mBrokenLibraries = true;
            errorMsgBrokenLib = e2.getMessage();
        }
        if (mBrokenLibraries) {
            mSingleton = this;
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage("An error occurred while trying to start the application. Please try again and/or reinstall." + System.getProperty("line.separator") + System.getProperty("line.separator") + "Error: " + errorMsgBrokenLib);
            dlgAlert.setTitle("SDL Error");
            dlgAlert.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SDLActivity.mSingleton.finish();
                }
            });
            dlgAlert.setCancelable(false);
            dlgAlert.create().show();
            return;
        }
        SDL.setupJNI();
        SDL.initialize();
        mSingleton = this;
        SDL.setContext(this);
        if (Build.VERSION.SDK_INT >= SDL_SYSTEM_CURSOR_HAND) {
            mClipboardHandler = new SDLClipboardHandler_API11();
        } else {
            mClipboardHandler = new SDLClipboardHandler_Old();
        }
        mHIDDeviceManager = HIDDeviceManager.acquire(this);
        mSurface = new SDLSurface(getApplication());
        RelativeLayout relativeLayout = new RelativeLayout(this);
        mLayout = relativeLayout;
        relativeLayout.addView(mSurface);
        int currentOrientation = getCurrentOrientation();
        mCurrentOrientation = currentOrientation;
        onNativeOrientationChanged(currentOrientation);
        setContentView(mLayout);
        setWindowStyle(false);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(this);
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null && (filename = intent.getData().getPath()) != null) {
            Log.v(TAG, "Got filename: " + filename);
            onNativeDropFile(filename);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        Log.v(TAG, "onPause()");
        super.onPause();
        mNextNativeState = NativeState.PAUSED;
        mIsResumedCalled = false;
        if (!mBrokenLibraries) {
            HIDDeviceManager hIDDeviceManager = mHIDDeviceManager;
            if (hIDDeviceManager != null) {
                hIDDeviceManager.setFrozen(true);
            }
            handleNativeState();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        Log.v(TAG, "onResume()");
        super.onResume();
        mNextNativeState = NativeState.RESUMED;
        mIsResumedCalled = true;
        if (!mBrokenLibraries) {
            HIDDeviceManager hIDDeviceManager = mHIDDeviceManager;
            if (hIDDeviceManager != null) {
                hIDDeviceManager.setFrozen(false);
            }
            handleNativeState();
        }
    }

    public static int getCurrentOrientation() {
        switch (((WindowManager) getContext().getSystemService("window")).getDefaultDisplay().getRotation()) {
            case 0:
                return 3;
            case 1:
                return 1;
            case 2:
                return 4;
            case 3:
                return 2;
            default:
                return 0;
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.v(TAG, "onWindowFocusChanged(): " + hasFocus);
        if (!mBrokenLibraries) {
            mHasFocus = hasFocus;
            if (hasFocus) {
                mNextNativeState = NativeState.RESUMED;
                getMotionListener().reclaimRelativeMouseModeIfNeeded();
            } else {
                mNextNativeState = NativeState.PAUSED;
            }
            handleNativeState();
        }
    }

    public void onLowMemory() {
        Log.v(TAG, "onLowMemory()");
        super.onLowMemory();
        if (!mBrokenLibraries) {
            nativeLowMemory();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        Log.v(TAG, "onDestroy()");
        HIDDeviceManager hIDDeviceManager = mHIDDeviceManager;
        if (hIDDeviceManager != null) {
            HIDDeviceManager.release(hIDDeviceManager);
            mHIDDeviceManager = null;
        }
        if (mBrokenLibraries) {
            super.onDestroy();
            initialize();
            return;
        }
        mNextNativeState = NativeState.PAUSED;
        handleNativeState();
        mExitCalledFromJava = true;
        nativeQuit();
        Thread thread = mSDLThread;
        if (thread != null) {
            try {
                thread.join();
            } catch (Exception e) {
                Log.v(TAG, "Problem stopping thread: " + e);
            }
            mSDLThread = null;
        }
        super.onDestroy();
        initialize();
    }

    public void onBackPressed() {
        String trapBack = nativeGetHint("SDL_ANDROID_TRAP_BACK_BUTTON");
        if (trapBack == null || !trapBack.equals("1")) {
            super.onBackPressed();
        }
    }

    public static void manualBackButton() {
        mSingleton.pressBackButton();
    }

    public void pressBackButton() {
        runOnUiThread(new Runnable() {
            public void run() {
                SDLActivity.this.superOnBackPressed();
            }
        });
    }

    public void superOnBackPressed() {
        super.onBackPressed();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode;
        if (mBrokenLibraries || (keyCode = event.getKeyCode()) == 25 || keyCode == 24 || keyCode == 27 || keyCode == 168 || keyCode == 169) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    public static void handleNativeState() {
        NativeState nativeState = mNextNativeState;
        if (nativeState != mCurrentNativeState) {
            if (nativeState == NativeState.INIT) {
                mCurrentNativeState = mNextNativeState;
            } else if (mNextNativeState == NativeState.PAUSED) {
                nativePause();
                SDLSurface sDLSurface = mSurface;
                if (sDLSurface != null) {
                    sDLSurface.handlePause();
                }
                mCurrentNativeState = mNextNativeState;
            } else if (mNextNativeState == NativeState.RESUMED && mIsSurfaceReady && mHasFocus && mIsResumedCalled) {
                if (mSDLThread == null) {
                    mSDLThread = new Thread(new SDLMain(), "SDLThread");
                    mSurface.enableSensor(1, true);
                    mSDLThread.start();
                }
                nativeResume();
                mSurface.handleResume();
                mCurrentNativeState = mNextNativeState;
            }
        }
    }

    public static void handleNativeExit() {
        mSDLThread = null;
        SDLActivity sDLActivity = mSingleton;
        if (sDLActivity != null) {
            sDLActivity.finish();
        }
    }

    /* access modifiers changed from: protected */
    public boolean onUnhandledMessage(int command, Object param) {
        return false;
    }

    protected static class SDLCommandHandler extends Handler {
        protected SDLCommandHandler() {
        }

        public void handleMessage(Message msg) {
            Window window;
            Context context = SDL.getContext();
            if (context == null) {
                Log.e(SDLActivity.TAG, "error handling message, getContext() returned null");
                return;
            }
            switch (msg.arg1) {
                case 1:
                    if (context instanceof Activity) {
                        ((Activity) context).setTitle((String) msg.obj);
                        return;
                    } else {
                        Log.e(SDLActivity.TAG, "error handling message, getContext() returned no Activity");
                        return;
                    }
                case 2:
                    if (Build.VERSION.SDK_INT >= 19) {
                        if (context instanceof Activity) {
                            Window window2 = ((Activity) context).getWindow();
                            if (window2 == null) {
                                return;
                            }
                            if (!(msg.obj instanceof Integer) || ((Integer) msg.obj).intValue() == 0) {
                                window2.getDecorView().setSystemUiVisibility(256);
                                window2.addFlags(2048);
                                window2.clearFlags(TarConstants.EOF_BLOCK);
                                SDLActivity.mFullscreenModeActive = false;
                                return;
                            }
                            window2.getDecorView().setSystemUiVisibility(5894);
                            window2.addFlags(TarConstants.EOF_BLOCK);
                            window2.clearFlags(2048);
                            SDLActivity.mFullscreenModeActive = true;
                            return;
                        }
                        Log.e(SDLActivity.TAG, "error handling message, getContext() returned no Activity");
                        return;
                    }
                    return;
                case 3:
                    if (SDLActivity.mTextEdit != null) {
                        SDLActivity.mTextEdit.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
                        ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(SDLActivity.mTextEdit.getWindowToken(), 0);
                        SDLActivity.mScreenKeyboardShown = false;
                        return;
                    }
                    return;
                case 5:
                    if ((context instanceof Activity) && (window = ((Activity) context).getWindow()) != null) {
                        if (!(msg.obj instanceof Integer) || ((Integer) msg.obj).intValue() == 0) {
                            window.clearFlags(128);
                            return;
                        } else {
                            window.addFlags(128);
                            return;
                        }
                    } else {
                        return;
                    }
                default:
                    if ((context instanceof SDLActivity) && !((SDLActivity) context).onUnhandledMessage(msg.arg1, msg.obj)) {
                        Log.e(SDLActivity.TAG, "error handling message, command is " + msg.arg1);
                        return;
                    }
                    return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean sendCommand(int command, Object data) {
        Message msg = this.commandHandler.obtainMessage();
        msg.arg1 = command;
        msg.obj = data;
        boolean result = this.commandHandler.sendMessage(msg);
        if (Build.VERSION.SDK_INT >= 19 && command == 2) {
            boolean bShouldWait = false;
            if (data instanceof Integer) {
                Display display = ((WindowManager) getSystemService("window")).getDefaultDisplay();
                DisplayMetrics realMetrics = new DisplayMetrics();
                display.getRealMetrics(realMetrics);
                boolean z = false;
                boolean bFullscreenLayout = realMetrics.widthPixels == mSurface.getWidth() && realMetrics.heightPixels == mSurface.getHeight();
                if (((Integer) data).intValue() == 1) {
                    if (!bFullscreenLayout) {
                        z = true;
                    }
                    bShouldWait = z;
                } else {
                    bShouldWait = bFullscreenLayout;
                }
            }
            if (bShouldWait) {
                synchronized (getContext()) {
                    try {
                        getContext().wait(500);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    public static boolean setActivityTitle(String title) {
        return mSingleton.sendCommand(1, title);
    }

    public static void setWindowStyle(boolean fullscreen) {
        mSingleton.sendCommand(2, Integer.valueOf(fullscreen));
    }

    public static void setOrientation(int w, int h, boolean resizable, String hint) {
        SDLActivity sDLActivity = mSingleton;
        if (sDLActivity != null) {
            sDLActivity.setOrientationBis(w, h, resizable, hint);
        }
    }

    public void setOrientationBis(int w, int h, boolean resizable, String hint) {
        int orientation = SDL_SYSTEM_CURSOR_NONE;
        if (hint.contains("LandscapeRight") && hint.contains("LandscapeLeft")) {
            orientation = SDL_SYSTEM_CURSOR_SIZENESW;
        } else if (hint.contains("LandscapeRight")) {
            orientation = 0;
        } else if (hint.contains("LandscapeLeft")) {
            orientation = 8;
        } else if (hint.contains("Portrait") && hint.contains("PortraitUpsideDown")) {
            orientation = SDL_SYSTEM_CURSOR_SIZEWE;
        } else if (hint.contains("Portrait")) {
            orientation = 1;
        } else if (hint.contains("PortraitUpsideDown")) {
            orientation = SDL_SYSTEM_CURSOR_SIZEALL;
        }
        if (orientation == SDL_SYSTEM_CURSOR_NONE && !resizable) {
            if (w > h) {
                orientation = SDL_SYSTEM_CURSOR_SIZENESW;
            } else {
                orientation = SDL_SYSTEM_CURSOR_SIZEWE;
            }
        }
        Log.v(TAG, "setOrientation() orientation=" + orientation + " width=" + w + " height=" + h + " resizable=" + resizable + " hint=" + hint);
        if (orientation != SDL_SYSTEM_CURSOR_NONE) {
            mSingleton.setRequestedOrientation(orientation);
        }
    }

    public static boolean isScreenKeyboardShown() {
        if (mTextEdit != null && mScreenKeyboardShown) {
            return ((InputMethodManager) SDL.getContext().getSystemService("input_method")).isAcceptingText();
        }
        return false;
    }

    public static boolean supportsRelativeMouse() {
        if (isChromebook()) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 27 || !isDeXMode()) {
            return getMotionListener().supportsRelativeMouse();
        }
        return false;
    }

    public static boolean setRelativeMouseEnabled(boolean enabled) {
        if (!enabled || supportsRelativeMouse()) {
            return getMotionListener().setRelativeMouseEnabled(enabled);
        }
        return false;
    }

    public static boolean sendMessage(int command, int param) {
        SDLActivity sDLActivity = mSingleton;
        if (sDLActivity == null) {
            return false;
        }
        return sDLActivity.sendCommand(command, Integer.valueOf(param));
    }

    public static Context getContext() {
        return SDL.getContext();
    }

    public static boolean isAndroidTV() {
        if (((UiModeManager) getContext().getSystemService("uimode")).getCurrentModeType() == 4) {
            return true;
        }
        if (Build.MANUFACTURER.equals("MINIX") && Build.MODEL.equals("NEO-U1")) {
            return true;
        }
        if (!Build.MANUFACTURER.equals("Amlogic") || !Build.MODEL.equals("X96-W")) {
            return false;
        }
        return true;
    }

    public static boolean isTablet() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        double dWidthInches = ((double) metrics.widthPixels) / ((double) metrics.xdpi);
        double dHeightInches = ((double) metrics.heightPixels) / ((double) metrics.ydpi);
        return Math.sqrt((dWidthInches * dWidthInches) + (dHeightInches * dHeightInches)) >= 7.0d;
    }

    public static boolean isChromebook() {
        return getContext().getPackageManager().hasSystemFeature("org.chromium.arc.device_management");
    }

    public static boolean isDeXMode() {
        if (Build.VERSION.SDK_INT < 24) {
            return false;
        }
        try {
            Configuration config = getContext().getResources().getConfiguration();
            Class configClass = config.getClass();
            if (configClass.getField("SEM_DESKTOP_MODE_ENABLED").getInt(configClass) == configClass.getField("semDesktopModeEnabled").getInt(config)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static DisplayMetrics getDisplayDPI() {
        return getContext().getResources().getDisplayMetrics();
    }

    public static boolean getManifestEnvironmentVariables() {
        try {
            Bundle bundle = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), 128).metaData;
            if (bundle == null) {
                return false;
            }
            int trimLength = "SDL_ENV.".length();
            for (String key : bundle.keySet()) {
                if (key.startsWith("SDL_ENV.")) {
                    nativeSetenv(key.substring(trimLength), bundle.get(key).toString());
                }
            }
            return true;
        } catch (Exception e) {
            Log.v(TAG, "exception " + e.toString());
            return false;
        }
    }

    public static View getContentView() {
        return mLayout;
    }

    static class ShowTextInputTask implements Runnable {
        static final int HEIGHT_PADDING = 15;

        /* renamed from: h */
        public int f2h;

        /* renamed from: w */
        public int f3w;

        /* renamed from: x */
        public int f4x;

        /* renamed from: y */
        public int f5y;

        public ShowTextInputTask(int x, int y, int w, int h) {
            this.f4x = x;
            this.f5y = y;
            this.f3w = w;
            this.f2h = h;
        }

        public void run() {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(this.f3w, this.f2h + HEIGHT_PADDING);
            params.leftMargin = this.f4x;
            params.topMargin = this.f5y;
            if (SDLActivity.mTextEdit == null) {
                SDLActivity.mTextEdit = new DummyEdit(SDL.getContext());
                SDLActivity.mLayout.addView(SDLActivity.mTextEdit, params);
            } else {
                SDLActivity.mTextEdit.setLayoutParams(params);
            }
            SDLActivity.mTextEdit.setVisibility(0);
            SDLActivity.mTextEdit.requestFocus();
            ((InputMethodManager) SDL.getContext().getSystemService("input_method")).showSoftInput(SDLActivity.mTextEdit, 0);
            SDLActivity.mScreenKeyboardShown = true;
        }
    }

    public static boolean showTextInput(int x, int y, int w, int h) {
        return mSingleton.commandHandler.post(new ShowTextInputTask(x, y, w, h));
    }

    public static boolean isTextInputEvent(KeyEvent event) {
        if (Build.VERSION.SDK_INT >= SDL_SYSTEM_CURSOR_HAND && event.isCtrlPressed()) {
            return false;
        }
        if (event.isPrintingKey() || event.getKeyCode() == 62) {
            return true;
        }
        return false;
    }

    public static Surface getNativeSurface() {
        SDLSurface sDLSurface = mSurface;
        if (sDLSurface == null) {
            return null;
        }
        return sDLSurface.getNativeSurface();
    }

    public static int[] inputGetInputDeviceIds(int sources) {
        int[] ids = InputDevice.getDeviceIds();
        int[] filtered = new int[ids.length];
        int used = 0;
        for (int device : ids) {
            InputDevice device2 = InputDevice.getDevice(device);
            if (!(device2 == null || (device2.getSources() & sources) == 0)) {
                filtered[used] = device2.getId();
                used++;
            }
        }
        return Arrays.copyOf(filtered, used);
    }

    public static void triggerAppConfirmedActive() {
        mSingleton.appConfirmedActive();
    }

    public void appConfirmedActive() {
    }

    public static InputStream openAPKExpansionInputStream(String fileName) throws IOException {
        String patchHint;
        if (expansionFile == null) {
            String mainHint = nativeGetHint("SDL_ANDROID_APK_EXPANSION_MAIN_FILE_VERSION");
            if (mainHint == null || (patchHint = nativeGetHint("SDL_ANDROID_APK_EXPANSION_PATCH_FILE_VERSION")) == null) {
                return null;
            }
            try {
                Integer mainVersion = Integer.valueOf(mainHint);
                Integer patchVersion = Integer.valueOf(patchHint);
                try {
                    Object invoke = Class.forName("com.android.vending.expansion.zipfile.APKExpansionSupport").getMethod("getAPKExpansionZipFile", new Class[]{Context.class, Integer.TYPE, Integer.TYPE}).invoke((Object) null, new Object[]{SDL.getContext(), mainVersion, patchVersion});
                    expansionFile = invoke;
                    expansionFileMethod = invoke.getClass().getMethod("getInputStream", new Class[]{String.class});
                } catch (Exception ex) {
                    ex.printStackTrace();
                    expansionFile = null;
                    expansionFileMethod = null;
                    throw new IOException("Could not access APK expansion support library", ex);
                }
            } catch (NumberFormatException ex2) {
                ex2.printStackTrace();
                throw new IOException("No valid file versions set for APK expansion files", ex2);
            }
        }
        try {
            InputStream fileStream = (InputStream) expansionFileMethod.invoke(expansionFile, new Object[]{fileName});
            if (fileStream != null) {
                return fileStream;
            }
            throw new IOException("Could not find path in APK expansion file");
        } catch (Exception ex3) {
            ex3.printStackTrace();
            throw new IOException("Could not open stream from APK expansion file", ex3);
        }
    }

    public int messageboxShowMessageBox(int flags, String title, String message, int[] buttonFlags, int[] buttonIds, String[] buttonTexts, int[] colors) {
        this.messageboxSelection[0] = SDL_SYSTEM_CURSOR_NONE;
        if (buttonFlags.length != buttonIds.length && buttonIds.length != buttonTexts.length) {
            return SDL_SYSTEM_CURSOR_NONE;
        }
        final Bundle args = new Bundle();
        args.putInt("flags", flags);
        args.putString("title", title);
        args.putString("message", message);
        args.putIntArray("buttonFlags", buttonFlags);
        args.putIntArray("buttonIds", buttonIds);
        args.putStringArray("buttonTexts", buttonTexts);
        args.putIntArray("colors", colors);
        runOnUiThread(new Runnable() {
            public void run() {
                SDLActivity sDLActivity = SDLActivity.this;
                int i = sDLActivity.dialogs;
                sDLActivity.dialogs = i + 1;
                sDLActivity.showDialog(i, args);
            }
        });
        synchronized (this.messageboxSelection) {
            try {
                this.messageboxSelection.wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                return SDL_SYSTEM_CURSOR_NONE;
            }
        }
        return this.messageboxSelection[0];
    }

    /* access modifiers changed from: protected */
    public Dialog onCreateDialog(int ignore, Bundle args) {
        int buttonBackgroundColor;
        int textColor;
        int backgroundColor;
        int i;
        SparseArray<Button> mapping;
        int buttonSelectedColor;
        Bundle bundle = args;
        int[] colors = bundle.getIntArray("colors");
        if (colors != null) {
            int i2 = SDL_SYSTEM_CURSOR_NONE + 1;
            backgroundColor = colors[i2];
            int i3 = i2 + 1;
            textColor = colors[i3];
            int i4 = i3 + 1;
            int i5 = colors[i4];
            int i6 = i4 + 1;
            buttonBackgroundColor = colors[i6];
            i = colors[i6 + 1];
        } else {
            backgroundColor = 0;
            textColor = 0;
            buttonBackgroundColor = 0;
            i = 0;
        }
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(bundle.getString("title"));
        dialog.setCancelable(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface unused) {
                synchronized (SDLActivity.this.messageboxSelection) {
                    SDLActivity.this.messageboxSelection.notify();
                }
            }
        });
        TextView message = new TextView(this);
        message.setGravity(17);
        message.setText(bundle.getString("message"));
        if (textColor != 0) {
            message.setTextColor(textColor);
        }
        int[] buttonFlags = bundle.getIntArray("buttonFlags");
        int[] buttonIds = bundle.getIntArray("buttonIds");
        String[] buttonTexts = bundle.getStringArray("buttonTexts");
        SparseArray<Button> mapping2 = new SparseArray<>();
        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(0);
        buttons.setGravity(17);
        int i7 = 0;
        while (i7 < buttonTexts.length) {
            Button button = new Button(this);
            final int id = buttonIds[i7];
            int[] colors2 = colors;
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SDLActivity.this.messageboxSelection[0] = id;
                    dialog.dismiss();
                }
            });
            if (buttonFlags[i7] != 0) {
                if ((buttonFlags[i7] & 1) != 0) {
                    int i8 = id;
                    mapping = mapping2;
                    mapping.put(66, button);
                } else {
                    mapping = mapping2;
                }
                if ((buttonFlags[i7] & 2) != 0) {
                    mapping.put(111, button);
                }
            } else {
                mapping = mapping2;
            }
            button.setText(buttonTexts[i7]);
            if (textColor != 0) {
                button.setTextColor(textColor);
            }
            if (buttonBackgroundColor != 0) {
                Drawable drawable = button.getBackground();
                if (drawable == null) {
                    button.setBackgroundColor(buttonBackgroundColor);
                    buttonSelectedColor = i;
                } else {
                    buttonSelectedColor = i;
                    drawable.setColorFilter(buttonBackgroundColor, PorterDuff.Mode.MULTIPLY);
                }
            } else {
                buttonSelectedColor = i;
            }
            buttons.addView(button);
            i7++;
            i = buttonSelectedColor;
            colors = colors2;
            mapping2 = mapping;
            Bundle bundle2 = args;
        }
        final SparseArray<Button> mapping3 = mapping2;
        int i9 = i;
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(1);
        content.addView(message);
        content.addView(buttons);
        if (backgroundColor != 0) {
            content.setBackgroundColor(backgroundColor);
        }
        dialog.setContentView(content);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
                Button button = (Button) mapping3.get(keyCode);
                if (button == null) {
                    return false;
                }
                if (event.getAction() == 1) {
                    button.performClick();
                }
                return true;
            }
        });
        return dialog;
    }

    public void onSystemUiVisibilityChange(int visibility) {
        Handler handler;
        if (!mFullscreenModeActive) {
            return;
        }
        if (((visibility & 4) == 0 || (visibility & 2) == 0) && (handler = getWindow().getDecorView().getHandler()) != null) {
            handler.removeCallbacks(this.rehideSystemUi);
            handler.postDelayed(this.rehideSystemUi, 2000);
        }
    }

    public static boolean clipboardHasText() {
        return mClipboardHandler.clipboardHasText();
    }

    public static String clipboardGetText() {
        return mClipboardHandler.clipboardGetText();
    }

    public static void clipboardSetText(String string) {
        mClipboardHandler.clipboardSetText(string);
    }

    public static int createCustomCursor(int[] colors, int width, int height, int hotSpotX, int hotSpotY) {
        int[] iArr = colors;
        Bitmap bitmap = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
        mLastCursorID++;
        try {
            mCursors.put(Integer.valueOf(mLastCursorID), Class.forName("android.view.PointerIcon").getMethod("create", new Class[]{Bitmap.class, Float.TYPE, Float.TYPE}).invoke((Object) null, new Object[]{bitmap, Integer.valueOf(hotSpotX), Integer.valueOf(hotSpotY)}));
            return mLastCursorID;
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean setCustomCursor(int cursorID) {
        try {
            SDLSurface.class.getMethod("setPointerIcon", new Class[]{Class.forName("android.view.PointerIcon")}).invoke(mSurface, new Object[]{mCursors.get(Integer.valueOf(cursorID))});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean setSystemCursor(int cursorID) {
        int cursor_type = 0;
        switch (cursorID) {
            case 0:
                cursor_type = 1000;
                break;
            case 1:
                cursor_type = 1008;
                break;
            case 2:
                cursor_type = 1004;
                break;
            case 3:
                cursor_type = 1007;
                break;
            case 4:
                cursor_type = 1004;
                break;
            case 5:
                cursor_type = 1017;
                break;
            case SDL_SYSTEM_CURSOR_SIZENESW /*6*/:
                cursor_type = 1016;
                break;
            case SDL_SYSTEM_CURSOR_SIZEWE /*7*/:
                cursor_type = 1014;
                break;
            case 8:
                cursor_type = 1015;
                break;
            case SDL_SYSTEM_CURSOR_SIZEALL /*9*/:
                cursor_type = 1020;
                break;
            case SDL_SYSTEM_CURSOR_NO /*10*/:
                cursor_type = 1012;
                break;
            case SDL_SYSTEM_CURSOR_HAND /*11*/:
                cursor_type = 1002;
                break;
        }
        try {
            Class PointerIconClass = Class.forName("android.view.PointerIcon");
            Method getSystemIcon = PointerIconClass.getMethod("getSystemIcon", new Class[]{Context.class, Integer.TYPE});
            SDLSurface.class.getMethod("setPointerIcon", new Class[]{PointerIconClass}).invoke(mSurface, new Object[]{getSystemIcon.invoke((Object) null, new Object[]{SDL.getContext(), Integer.valueOf(cursor_type)})});
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
