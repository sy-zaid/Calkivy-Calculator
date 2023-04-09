package org.kivy.android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class PythonService extends Service implements Runnable {
    public static PythonService mService = null;
    private String androidArgument;
    private String androidPrivate;
    private boolean autoRestartService = false;
    private String pythonHome;
    private String pythonName;
    private String pythonPath;
    private String pythonServiceArgument;
    private Thread pythonThread = null;
    private String serviceEntrypoint;
    private Intent startIntent = null;

    public static native void nativeStart(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    public void setAutoRestartService(boolean restart) {
        this.autoRestartService = restart;
    }

    public int startType() {
        return 2;
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (this.pythonThread != null) {
            Log.v("python service", "service exists, do not start again");
            return startType();
        }
        if (intent == null) {
            intent = getThisDefaultIntent(getApplicationContext(), "");
        }
        this.startIntent = intent;
        Bundle extras = intent.getExtras();
        this.androidPrivate = extras.getString("androidPrivate");
        this.androidArgument = extras.getString("androidArgument");
        this.serviceEntrypoint = extras.getString("serviceEntrypoint");
        this.pythonName = extras.getString("pythonName");
        this.pythonHome = extras.getString("pythonHome");
        this.pythonPath = extras.getString("pythonPath");
        boolean serviceStartAsForeground = extras.getString("serviceStartAsForeground").equals("true");
        this.pythonServiceArgument = extras.getString("pythonServiceArgument");
        Thread thread = new Thread(this);
        this.pythonThread = thread;
        thread.start();
        if (serviceStartAsForeground) {
            doStartForeground(extras);
        }
        return startType();
    }

    /* access modifiers changed from: protected */
    public int getServiceId() {
        return 1;
    }

    /* access modifiers changed from: protected */
    public Intent getThisDefaultIntent(Context ctx, String pythonServiceArgument2) {
        return null;
    }

    /* access modifiers changed from: protected */
    public void doStartForeground(Bundle extras) {
        Notification notification;
        Bundle bundle = extras;
        String serviceTitle = bundle.getString("serviceTitle");
        String serviceDescription = bundle.getString("serviceDescription");
        Context context = getApplicationContext();
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, new Intent(context, PythonActivity.class), 201326592);
        if (Build.VERSION.SDK_INT < 26) {
            notification = new Notification(context.getApplicationInfo().icon, serviceTitle, System.currentTimeMillis());
            try {
                notification.getClass().getMethod("setLatestEventInfo", new Class[]{Context.class, CharSequence.class, CharSequence.class, PendingIntent.class}).invoke(notification, new Object[]{context, serviceTitle, serviceDescription, pIntent});
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
            }
        } else {
            NotificationChannel chan = new NotificationChannel("org.kivy.p4a", "Background Service", 0);
            chan.setLightColor(-16776961);
            chan.setLockscreenVisibility(0);
            ((NotificationManager) getSystemService("notification")).createNotificationChannel(chan);
            Notification.Builder builder = new Notification.Builder(context, "org.kivy.p4a");
            builder.setContentTitle(serviceTitle);
            builder.setContentText(serviceDescription);
            builder.setContentIntent(pIntent);
            builder.setSmallIcon(context.getApplicationInfo().icon);
            notification = builder.build();
        }
        startForeground(getServiceId(), notification);
    }

    public void onDestroy() {
        super.onDestroy();
        this.pythonThread = null;
        if (this.autoRestartService && this.startIntent != null) {
            Log.v("python service", "service restart requested");
            startService(this.startIntent);
        }
        Process.killProcess(Process.myPid());
    }

    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (startType() != 1) {
            stopSelf();
        }
    }

    public void run() {
        PythonUtil.loadLibraries(new File(getFilesDir().getAbsolutePath() + "/app"), new File(getApplicationInfo().nativeLibraryDir));
        mService = this;
        nativeStart(this.androidPrivate, this.androidArgument, this.serviceEntrypoint, this.pythonName, this.pythonHome, this.pythonPath, this.pythonServiceArgument);
        stopSelf();
    }
}
