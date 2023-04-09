package org.renpy.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import java.util.List;
import org.kivy.android.PythonActivity;

public class Hardware {
    public static generic3AxisSensor accelerometerSensor = null;
    static Context context;
    public static final float[] defaultRv = {0.0f, 0.0f, 0.0f};
    static List<ScanResult> latestResult;
    public static generic3AxisSensor magneticFieldSensor = null;
    public static DisplayMetrics metrics = new DisplayMetrics();
    public static boolean network_state = false;
    public static generic3AxisSensor orientationSensor = null;
    static View view;

    public static void vibrate(double s) {
        Vibrator v = (Vibrator) context.getSystemService("vibrator");
        if (v != null) {
            v.vibrate((long) ((int) (1000.0d * s)));
        }
    }

    public static String getHardwareSensors() {
        List<Sensor> allSensors = ((SensorManager) context.getSystemService("sensor")).getSensorList(-1);
        if (allSensors == null) {
            return "";
        }
        String resultString = "";
        for (Sensor s : allSensors) {
            resultString = (((((resultString + String.format("Name=" + s.getName(), new Object[0])) + String.format(",Vendor=" + s.getVendor(), new Object[0])) + String.format(",Version=" + s.getVersion(), new Object[0])) + String.format(",MaximumRange=" + s.getMaximumRange(), new Object[0])) + String.format(",Power=" + s.getPower(), new Object[0])) + String.format(",Type=" + s.getType() + "\n", new Object[0]);
        }
        return resultString;
    }

    public static class generic3AxisSensor implements SensorEventListener {
        private final Sensor sSensor;
        SensorEvent sSensorEvent;
        private final SensorManager sSensorManager;
        private final int sSensorType;

        public generic3AxisSensor(int sensorType) {
            this.sSensorType = sensorType;
            SensorManager sensorManager = (SensorManager) Hardware.context.getSystemService("sensor");
            this.sSensorManager = sensorManager;
            this.sSensor = sensorManager.getDefaultSensor(sensorType);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            this.sSensorEvent = event;
        }

        public void changeStatus(boolean enable) {
            if (enable) {
                this.sSensorManager.registerListener(this, this.sSensor, 3);
            } else {
                this.sSensorManager.unregisterListener(this, this.sSensor);
            }
        }

        public float[] readSensor() {
            SensorEvent sensorEvent = this.sSensorEvent;
            if (sensorEvent != null) {
                return sensorEvent.values;
            }
            return Hardware.defaultRv;
        }
    }

    public static void accelerometerEnable(boolean enable) {
        if (accelerometerSensor == null) {
            accelerometerSensor = new generic3AxisSensor(1);
        }
        accelerometerSensor.changeStatus(enable);
    }

    public static float[] accelerometerReading() {
        generic3AxisSensor generic3axissensor = accelerometerSensor;
        if (generic3axissensor == null) {
            return defaultRv;
        }
        return generic3axissensor.readSensor();
    }

    public static void orientationSensorEnable(boolean enable) {
        if (orientationSensor == null) {
            orientationSensor = new generic3AxisSensor(3);
        }
        orientationSensor.changeStatus(enable);
    }

    public static float[] orientationSensorReading() {
        generic3AxisSensor generic3axissensor = orientationSensor;
        if (generic3axissensor == null) {
            return defaultRv;
        }
        return generic3axissensor.readSensor();
    }

    public static void magneticFieldSensorEnable(boolean enable) {
        if (magneticFieldSensor == null) {
            magneticFieldSensor = new generic3AxisSensor(2);
        }
        magneticFieldSensor.changeStatus(enable);
    }

    public static float[] magneticFieldSensorReading() {
        generic3AxisSensor generic3axissensor = magneticFieldSensor;
        if (generic3axissensor == null) {
            return defaultRv;
        }
        return generic3axissensor.readSensor();
    }

    public static int getDPI() {
        PythonActivity.mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.densityDpi;
    }

    public static void hideKeyboard() {
        ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void enableWifiScanner() {
        IntentFilter i = new IntentFilter();
        i.addAction("android.net.wifi.SCAN_RESULTS");
        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context c, Intent i) {
                Hardware.latestResult = ((WifiManager) c.getSystemService("wifi")).getScanResults();
            }
        }, i);
    }

    public static String scanWifi() {
        List<ScanResult> list = latestResult;
        if (list == null) {
            return "";
        }
        String latestResultString = "";
        for (ScanResult result : list) {
            latestResultString = latestResultString + String.format("%s\t%s\t%d\n", new Object[]{result.SSID, result.BSSID, Integer.valueOf(result.level)});
        }
        return latestResultString;
    }

    public static boolean checkNetwork() {
        NetworkInfo activeNetwork = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnected()) {
            return false;
        }
        return true;
    }

    public static void registerNetworkCheck() {
        IntentFilter i = new IntentFilter();
        i.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context c, Intent i) {
                Hardware.network_state = Hardware.checkNetwork();
            }
        }, i);
    }
}
