package org.kivy.android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.kivy.android.launcher.Project;
import org.libsdl.app.SDLActivity;
import org.renpy.android.ResourceManager;

public class PythonActivity extends SDLActivity {
    private static final String TAG = "PythonActivity";
    public static PythonActivity mActivity = null;
    public static ImageView mImageView = null;
    public static View mLottieView = null;
    private List<ActivityResultListener> activityResultListeners = null;
    private boolean havePermissionsCallback = false;
    protected Timer loadingScreenRemovalTimer = null;
    protected boolean mAppConfirmedActive = false;
    /* access modifiers changed from: private */
    public Bundle mMetaData = null;
    /* access modifiers changed from: private */
    public PowerManager.WakeLock mWakeLock = null;
    private List<NewIntentListener> newIntentListeners = null;
    private PermissionsCallback permissionCallback;
    private ResourceManager resourceManager = null;

    public interface ActivityResultListener {
        void onActivityResult(int i, int i2, Intent intent);
    }

    public interface NewIntentListener {
        void onNewIntent(Intent intent);
    }

    public interface PermissionsCallback {
        void onRequestPermissionsResult(int i, String[] strArr, int[] iArr);
    }

    public String getAppRoot() {
        return getFilesDir().getAbsolutePath() + "/app";
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "PythonActivity onCreate running");
        this.resourceManager = new ResourceManager(this);
        Log.v(TAG, "About to do super onCreate");
        super.onCreate(savedInstanceState);
        Log.v(TAG, "Did super onCreate");
        mActivity = this;
        showLoadingScreen(getLoadingScreen());
        new UnpackFilesTask().execute(new String[]{getAppRoot()});
    }

    public void loadLibraries() {
        PythonUtil.loadLibraries(new File(new String(getAppRoot())), new File(getApplicationInfo().nativeLibraryDir));
    }

    public void toastError(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(this, msg, 1).show();
            }
        });
        synchronized (this) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    private class UnpackFilesTask extends AsyncTask<String, Void, String> {
        private UnpackFilesTask() {
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... params) {
            File app_root_file = new File(params[0]);
            Log.v(PythonActivity.TAG, "Ready to unpack");
            PythonUtil.unpackAsset(PythonActivity.mActivity, "private", app_root_file, true);
            PythonUtil.unpackPyBundle(PythonActivity.mActivity, PythonActivity.this.getApplicationInfo().nativeLibraryDir + "/libpybundle", app_root_file, false);
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String result) {
            PythonActivity.mActivity.finishLoad();
            PythonActivity.mActivity.showLoadingScreen(PythonActivity.this.getLoadingScreen());
            String app_root_dir = PythonActivity.this.getAppRoot();
            if (PythonActivity.this.getIntent() == null || PythonActivity.this.getIntent().getAction() == null || !PythonActivity.this.getIntent().getAction().equals("org.kivy.LAUNCH")) {
                SDLActivity.nativeSetenv("ANDROID_ENTRYPOINT", PythonActivity.this.getEntryPoint(app_root_dir));
                SDLActivity.nativeSetenv("ANDROID_ARGUMENT", app_root_dir);
                SDLActivity.nativeSetenv("ANDROID_APP_PATH", app_root_dir);
            } else {
                File path = new File(PythonActivity.this.getIntent().getData().getSchemeSpecificPart());
                Project p = Project.scanDirectory(path);
                SDLActivity.nativeSetenv("ANDROID_ENTRYPOINT", p.dir + "/" + PythonActivity.this.getEntryPoint(p.dir));
                SDLActivity.nativeSetenv("ANDROID_ARGUMENT", p.dir);
                SDLActivity.nativeSetenv("ANDROID_APP_PATH", p.dir);
                if (p != null) {
                    if (p.landscape) {
                        PythonActivity.this.setRequestedOrientation(0);
                    } else {
                        PythonActivity.this.setRequestedOrientation(1);
                    }
                }
                try {
                    FileWriter f = new FileWriter(new File(path, ".launch"));
                    f.write("started");
                    f.close();
                } catch (IOException e) {
                }
            }
            String mFilesDirectory = PythonActivity.mActivity.getFilesDir().getAbsolutePath();
            Log.v(PythonActivity.TAG, "Setting env vars for start.c and Python to use");
            SDLActivity.nativeSetenv("ANDROID_PRIVATE", mFilesDirectory);
            SDLActivity.nativeSetenv("ANDROID_UNPACK", app_root_dir);
            SDLActivity.nativeSetenv("PYTHONHOME", app_root_dir);
            SDLActivity.nativeSetenv("PYTHONPATH", app_root_dir + ":" + app_root_dir + "/lib");
            SDLActivity.nativeSetenv("PYTHONOPTIMIZE", "2");
            try {
                Log.v(PythonActivity.TAG, "Access to our meta-data...");
                Bundle unused = PythonActivity.mActivity.mMetaData = PythonActivity.mActivity.getPackageManager().getApplicationInfo(PythonActivity.mActivity.getPackageName(), 128).metaData;
                PowerManager pm = (PowerManager) PythonActivity.mActivity.getSystemService("power");
                if (PythonActivity.mActivity.mMetaData.getInt("wakelock") == 1) {
                    PowerManager.WakeLock unused2 = PythonActivity.mActivity.mWakeLock = pm.newWakeLock(10, "Screen On");
                    PythonActivity.mActivity.mWakeLock.acquire();
                }
                if (PythonActivity.mActivity.mMetaData.getInt("surface.transparent") != 0) {
                    Log.v(PythonActivity.TAG, "Surface will be transparent.");
                    PythonActivity.getSurface().setZOrderOnTop(true);
                    PythonActivity.getSurface().getHolder().setFormat(-2);
                } else {
                    Log.i(PythonActivity.TAG, "Surface will NOT be transparent");
                }
            } catch (PackageManager.NameNotFoundException e2) {
            }
            PythonActivity pythonActivity = PythonActivity.mActivity;
            if (PythonActivity.mHasFocus) {
                PythonActivity pythonActivity2 = PythonActivity.mActivity;
                if (PythonActivity.mCurrentNativeState != SDLActivity.NativeState.INIT) {
                    PythonActivity pythonActivity3 = PythonActivity.mActivity;
                    if (PythonActivity.mCurrentNativeState == SDLActivity.NativeState.RESUMED) {
                        PythonActivity pythonActivity4 = PythonActivity.mActivity;
                        if (PythonActivity.mSDLThread != null) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                PythonActivity.mActivity.onResume();
            }
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Void... values) {
        }
    }

    public static ViewGroup getLayout() {
        return mLayout;
    }

    public static SurfaceView getSurface() {
        return mSurface;
    }

    public void registerNewIntentListener(NewIntentListener listener) {
        if (this.newIntentListeners == null) {
            this.newIntentListeners = Collections.synchronizedList(new ArrayList());
        }
        this.newIntentListeners.add(listener);
    }

    public void unregisterNewIntentListener(NewIntentListener listener) {
        List<NewIntentListener> list = this.newIntentListeners;
        if (list != null) {
            list.remove(listener);
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        if (this.newIntentListeners != null) {
            onResume();
            synchronized (this.newIntentListeners) {
                for (NewIntentListener onNewIntent : this.newIntentListeners) {
                    onNewIntent.onNewIntent(intent);
                }
            }
        }
    }

    public void registerActivityResultListener(ActivityResultListener listener) {
        if (this.activityResultListeners == null) {
            this.activityResultListeners = Collections.synchronizedList(new ArrayList());
        }
        this.activityResultListeners.add(listener);
    }

    public void unregisterActivityResultListener(ActivityResultListener listener) {
        List<ActivityResultListener> list = this.activityResultListeners;
        if (list != null) {
            list.remove(listener);
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (this.activityResultListeners != null) {
            onResume();
            synchronized (this.activityResultListeners) {
                for (ActivityResultListener onActivityResult : this.activityResultListeners) {
                    onActivityResult.onActivityResult(requestCode, resultCode, intent);
                }
            }
        }
    }

    public static void start_service(String serviceTitle, String serviceDescription, String pythonServiceArgument) {
        _do_start_service(serviceTitle, serviceDescription, pythonServiceArgument, true);
    }

    public static void start_service_not_as_foreground(String serviceTitle, String serviceDescription, String pythonServiceArgument) {
        _do_start_service(serviceTitle, serviceDescription, pythonServiceArgument, false);
    }

    public static void _do_start_service(String serviceTitle, String serviceDescription, String pythonServiceArgument, boolean showForegroundNotification) {
        Intent serviceIntent = new Intent(mActivity, PythonService.class);
        String argument = mActivity.getFilesDir().getAbsolutePath();
        String app_root_dir = mActivity.getAppRoot();
        String entry_point = mActivity.getEntryPoint(app_root_dir + "/service");
        serviceIntent.putExtra("androidPrivate", argument);
        serviceIntent.putExtra("androidArgument", app_root_dir);
        serviceIntent.putExtra("serviceEntrypoint", "service/" + entry_point);
        serviceIntent.putExtra("pythonName", "python");
        serviceIntent.putExtra("pythonHome", app_root_dir);
        serviceIntent.putExtra("pythonPath", app_root_dir + ":" + app_root_dir + "/lib");
        serviceIntent.putExtra("serviceStartAsForeground", showForegroundNotification ? "true" : "false");
        serviceIntent.putExtra("serviceTitle", serviceTitle);
        serviceIntent.putExtra("serviceDescription", serviceDescription);
        serviceIntent.putExtra("pythonServiceArgument", pythonServiceArgument);
        mActivity.startService(serviceIntent);
    }

    public static void stop_service() {
        mActivity.stopService(new Intent(mActivity, PythonService.class));
    }

    /* access modifiers changed from: protected */
    public boolean sendCommand(int command, Object data) {
        boolean result = super.sendCommand(command, data);
        considerLoadingScreenRemoval();
        return result;
    }

    public void appConfirmedActive() {
        if (!this.mAppConfirmedActive) {
            Log.v(TAG, "appConfirmedActive() -> preparing loading screen removal");
            this.mAppConfirmedActive = true;
            considerLoadingScreenRemoval();
        }
    }

    public void considerLoadingScreenRemoval() {
        if (this.loadingScreenRemovalTimer == null) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (((PythonActivity) PythonActivity.mSingleton).mAppConfirmedActive && PythonActivity.this.loadingScreenRemovalTimer == null) {
                        TimerTask removalTask = new TimerTask() {
                            public void run() {
                                PythonActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        PythonActivity activity = (PythonActivity) PythonActivity.mSingleton;
                                        if (activity != null) {
                                            activity.removeLoadingScreen();
                                        }
                                    }
                                });
                            }
                        };
                        PythonActivity.this.loadingScreenRemovalTimer = new Timer();
                        PythonActivity.this.loadingScreenRemovalTimer.schedule(removalTask, 5000);
                    }
                }
            });
        }
    }

    public void removeLoadingScreen() {
        runOnUiThread(new Runnable() {
            public void run() {
                View view = PythonActivity.mLottieView != null ? PythonActivity.mLottieView : PythonActivity.mImageView;
                if (view != null && view.getParent() != null) {
                    ((ViewGroup) view.getParent()).removeView(view);
                    PythonActivity.mLottieView = null;
                    PythonActivity.mImageView = null;
                }
            }
        });
    }

    public String getEntryPoint(String search_dir) {
        List<String> entryPoints = new ArrayList<>();
        entryPoints.add("main.pyo");
        entryPoints.add("main.pyc");
        for (String value : entryPoints) {
            if (new File(search_dir + "/" + value).exists()) {
                return value;
            }
        }
        return "main.py";
    }

    /* access modifiers changed from: protected */
    public void showLoadingScreen(View view) {
        try {
            if (mLayout == null) {
                setContentView(view);
            } else if (view.getParent() == null) {
                mLayout.addView(view);
            }
        } catch (IllegalStateException e) {
        }
    }

    /* access modifiers changed from: protected */
    public void setBackgroundColor(View view) {
        String backgroundColor = this.resourceManager.getString("presplash_color");
        if (backgroundColor != null) {
            try {
                view.setBackgroundColor(Color.parseColor(backgroundColor));
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /* access modifiers changed from: protected */
    public View getLoadingScreen() {
        InputStream is;
        View view = mLottieView;
        if (view != null || mImageView != null) {
            return view != null ? view : mImageView;
        }
        try {
            mLottieView = getLayoutInflater().inflate(this.resourceManager.getIdentifier("lottie", "layout"), mLayout, false);
            try {
                if (mLayout == null) {
                    setContentView(mLottieView);
                } else if (mLottieView.getParent() == null) {
                    mLayout.addView(mLottieView);
                }
            } catch (IllegalStateException e) {
            }
            setBackgroundColor(mLottieView);
            return mLottieView;
        } catch (Resources.NotFoundException e2) {
            Log.v("SDL", "couldn't find lottie layout or animation, trying static splash");
            is = getResources().openRawResource(this.resourceManager.getIdentifier("presplash", "drawable"));
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            try {
                is.close();
            } catch (IOException e3) {
            }
            ImageView imageView = new ImageView(this);
            mImageView = imageView;
            imageView.setImageBitmap(bitmap);
            setBackgroundColor(mImageView);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            return mImageView;
        } catch (Throwable th) {
            try {
                is.close();
            } catch (IOException e4) {
            }
            throw th;
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        PowerManager.WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null && wakeLock.isHeld()) {
            this.mWakeLock.release();
        }
        Log.v(TAG, "onPause()");
        try {
            super.onPause();
        } catch (UnsatisfiedLinkError e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        PowerManager.WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null) {
            wakeLock.acquire();
        }
        Log.v(TAG, "onResume()");
        try {
            super.onResume();
        } catch (UnsatisfiedLinkError e) {
        }
        considerLoadingScreenRemoval();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        try {
            super.onWindowFocusChanged(hasFocus);
        } catch (UnsatisfiedLinkError e) {
        }
        considerLoadingScreenRemoval();
    }

    public void addPermissionsCallback(PermissionsCallback callback) {
        this.permissionCallback = callback;
        this.havePermissionsCallback = true;
        Log.v(TAG, "addPermissionsCallback(): Added callback for onRequestPermissionsResult");
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.v(TAG, "onRequestPermissionsResult()");
        if (this.havePermissionsCallback) {
            Log.v(TAG, "onRequestPermissionsResult passed to callback");
            this.permissionCallback.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public boolean checkCurrentPermission(String permission) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        try {
            if (Integer.parseInt(Activity.class.getMethod("checkSelfPermission", new Class[]{String.class}).invoke(this, new Object[]{permission}).toString()) == 0) {
                return true;
            }
            return false;
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
        }
    }

    public void requestPermissionsWithRequestCode(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            Class<Activity> cls = Activity.class;
            try {
                cls.getMethod("requestPermissions", new Class[]{String[].class, Integer.TYPE}).invoke(this, new Object[]{permissions, Integer.valueOf(requestCode)});
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            }
        }
    }

    public void requestPermissions(String[] permissions) {
        requestPermissionsWithRequestCode(permissions, 1);
    }

    public static void changeKeyboard(int inputType) {
        if (SDLActivity.keyboardInputType != inputType) {
            SDLActivity.keyboardInputType = inputType;
            ((InputMethodManager) getContext().getSystemService("input_method")).restartInput(mTextEdit);
        }
    }
}
