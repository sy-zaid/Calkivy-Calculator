package org.libsdl.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HIDDeviceManager {
    private static final String ACTION_USB_PERMISSION = "org.libsdl.app.USB_PERMISSION";
    private static final String TAG = "hidapi";
    private static HIDDeviceManager sManager;
    private static int sManagerRefCount = 0;
    private final BroadcastReceiver mBluetoothBroadcast = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.bluetooth.device.action.ACL_CONNECTED")) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                Log.d(HIDDeviceManager.TAG, "Bluetooth device connected: " + device);
                if (HIDDeviceManager.this.isSteamController(device)) {
                    HIDDeviceManager.this.connectBluetoothDevice(device);
                }
            }
            if (action.equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
                BluetoothDevice device2 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                Log.d(HIDDeviceManager.TAG, "Bluetooth device disconnected: " + device2);
                HIDDeviceManager.this.disconnectBluetoothDevice(device2);
            }
        }
    };
    private HashMap<BluetoothDevice, HIDDeviceBLESteamController> mBluetoothDevices = new HashMap<>();
    private BluetoothManager mBluetoothManager;
    private Context mContext;
    private HashMap<Integer, HIDDevice> mDevicesById = new HashMap<>();
    private Handler mHandler;
    private boolean mIsChromebook = false;
    private List<BluetoothDevice> mLastBluetoothDevices;
    private int mNextDeviceId = 0;
    private SharedPreferences mSharedPreferences = null;
    private HashMap<UsbDevice, HIDDeviceUSB> mUSBDevices = new HashMap<>();
    private final BroadcastReceiver mUsbBroadcast = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                HIDDeviceManager.this.handleUsbDeviceAttached((UsbDevice) intent.getParcelableExtra("device"));
            } else if (action.equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
                HIDDeviceManager.this.handleUsbDeviceDetached((UsbDevice) intent.getParcelableExtra("device"));
            } else if (action.equals(HIDDeviceManager.ACTION_USB_PERMISSION)) {
                HIDDeviceManager.this.handleUsbDevicePermission((UsbDevice) intent.getParcelableExtra("device"), intent.getBooleanExtra("permission", false));
            }
        }
    };
    private UsbManager mUsbManager;

    private native void HIDDeviceRegisterCallback();

    private native void HIDDeviceReleaseCallback();

    /* access modifiers changed from: package-private */
    public native void HIDDeviceConnected(int i, String str, int i2, int i3, String str2, int i4, String str3, String str4, int i5);

    /* access modifiers changed from: package-private */
    public native void HIDDeviceDisconnected(int i);

    /* access modifiers changed from: package-private */
    public native void HIDDeviceFeatureReport(int i, byte[] bArr);

    /* access modifiers changed from: package-private */
    public native void HIDDeviceInputReport(int i, byte[] bArr);

    /* access modifiers changed from: package-private */
    public native void HIDDeviceOpenPending(int i);

    /* access modifiers changed from: package-private */
    public native void HIDDeviceOpenResult(int i, boolean z);

    public static HIDDeviceManager acquire(Context context) {
        if (sManagerRefCount == 0) {
            sManager = new HIDDeviceManager(context);
        }
        sManagerRefCount++;
        return sManager;
    }

    public static void release(HIDDeviceManager manager) {
        HIDDeviceManager hIDDeviceManager = sManager;
        if (manager == hIDDeviceManager) {
            int i = sManagerRefCount - 1;
            sManagerRefCount = i;
            if (i == 0) {
                hIDDeviceManager.close();
                sManager = null;
            }
        }
    }

    private HIDDeviceManager(final Context context) {
        this.mContext = context;
        try {
            SDL.loadLibrary(TAG);
            HIDDeviceRegisterCallback();
            this.mSharedPreferences = this.mContext.getSharedPreferences(TAG, 0);
            this.mIsChromebook = this.mContext.getPackageManager().hasSystemFeature("org.chromium.arc.device_management");
            this.mNextDeviceId = this.mSharedPreferences.getInt("next_device_id", 0);
            initializeUSB();
            initializeBluetooth();
        } catch (Throwable e) {
            Log.w(TAG, "Couldn't load hidapi: " + e.toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setTitle("SDL HIDAPI Error");
            builder.setMessage("Please report the following error to the SDL maintainers: " + e.getMessage());
            builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        ((Activity) context).finish();
                    } catch (ClassCastException e) {
                    }
                }
            });
            builder.show();
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    public int getDeviceIDForIdentifier(String identifier) {
        SharedPreferences.Editor spedit = this.mSharedPreferences.edit();
        int result = this.mSharedPreferences.getInt(identifier, 0);
        if (result == 0) {
            int i = this.mNextDeviceId;
            int i2 = i + 1;
            this.mNextDeviceId = i2;
            result = i;
            spedit.putInt("next_device_id", i2);
        }
        spedit.putInt(identifier, result);
        spedit.commit();
        return result;
    }

    private void initializeUSB() {
        this.mUsbManager = (UsbManager) this.mContext.getSystemService("usb");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        filter.addAction(ACTION_USB_PERMISSION);
        this.mContext.registerReceiver(this.mUsbBroadcast, filter);
        for (UsbDevice usbDevice : this.mUsbManager.getDeviceList().values()) {
            handleUsbDeviceAttached(usbDevice);
        }
    }

    /* access modifiers changed from: package-private */
    public UsbManager getUSBManager() {
        return this.mUsbManager;
    }

    private void shutdownUSB() {
        try {
            this.mContext.unregisterReceiver(this.mUsbBroadcast);
        } catch (Exception e) {
        }
    }

    private boolean isHIDDeviceUSB(UsbDevice usbDevice) {
        for (int interface_number = 0; interface_number < usbDevice.getInterfaceCount(); interface_number++) {
            if (isHIDDeviceInterface(usbDevice, interface_number)) {
                return true;
            }
        }
        return false;
    }

    private boolean isHIDDeviceInterface(UsbDevice usbDevice, int interface_number) {
        UsbInterface usbInterface = usbDevice.getInterface(interface_number);
        if (usbInterface.getInterfaceClass() == 3) {
            return true;
        }
        if (interface_number != 0) {
            return false;
        }
        if (isXbox360Controller(usbDevice, usbInterface) || isXboxOneController(usbDevice, usbInterface)) {
            return true;
        }
        return false;
    }

    private boolean isXbox360Controller(UsbDevice usbDevice, UsbInterface usbInterface) {
        int[] SUPPORTED_VENDORS = {121, 1103, 1118, 1133, 1390, 1699, 1848, 2047, 3695, 3853, 4553, 4779, 5168, 5227, 5426, 5604, 5678, 5769, 7085, 9414};
        if (usbInterface.getInterfaceClass() == 255 && usbInterface.getInterfaceSubclass() == 93 && usbInterface.getInterfaceProtocol() == 1) {
            int vendor_id = usbDevice.getVendorId();
            for (int supportedVid : SUPPORTED_VENDORS) {
                if (vendor_id == supportedVid) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isXboxOneController(UsbDevice usbDevice, UsbInterface usbInterface) {
        int[] SUPPORTED_VENDORS = {1118, 1848, 3695, 3853, 5426, 9414};
        if (usbInterface.getInterfaceClass() == 255 && usbInterface.getInterfaceSubclass() == 71 && usbInterface.getInterfaceProtocol() == 208) {
            int vendor_id = usbDevice.getVendorId();
            for (int supportedVid : SUPPORTED_VENDORS) {
                if (vendor_id == supportedVid) {
                    return true;
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void handleUsbDeviceAttached(UsbDevice usbDevice) {
        if (isHIDDeviceUSB(usbDevice)) {
            connectHIDDeviceUSB(usbDevice);
        }
    }

    /* access modifiers changed from: private */
    public void handleUsbDeviceDetached(UsbDevice usbDevice) {
        HIDDeviceUSB device = this.mUSBDevices.get(usbDevice);
        if (device != null) {
            int id = device.getId();
            this.mUSBDevices.remove(usbDevice);
            this.mDevicesById.remove(Integer.valueOf(id));
            device.shutdown();
            HIDDeviceDisconnected(id);
        }
    }

    /* access modifiers changed from: private */
    public void handleUsbDevicePermission(UsbDevice usbDevice, boolean permission_granted) {
        HIDDeviceUSB device = this.mUSBDevices.get(usbDevice);
        if (device != null) {
            boolean opened = false;
            if (permission_granted) {
                opened = device.open();
            }
            HIDDeviceOpenResult(device.getId(), opened);
        }
    }

    private void connectHIDDeviceUSB(UsbDevice usbDevice) {
        synchronized (this) {
            int interface_number = 0;
            while (true) {
                if (interface_number >= usbDevice.getInterfaceCount()) {
                    break;
                } else if (isHIDDeviceInterface(usbDevice, interface_number)) {
                    HIDDeviceUSB device = new HIDDeviceUSB(this, usbDevice, interface_number);
                    int id = device.getId();
                    this.mUSBDevices.put(usbDevice, device);
                    this.mDevicesById.put(Integer.valueOf(id), device);
                    HIDDeviceConnected(id, device.getIdentifier(), device.getVendorId(), device.getProductId(), device.getSerialNumber(), device.getVersion(), device.getManufacturerName(), device.getProductName(), interface_number);
                    break;
                } else {
                    interface_number++;
                }
            }
        }
    }

    private void initializeBluetooth() {
        BluetoothAdapter btAdapter;
        Log.d(TAG, "Initializing Bluetooth");
        if (this.mContext.getPackageManager().checkPermission("android.permission.BLUETOOTH", this.mContext.getPackageName()) != 0) {
            Log.d(TAG, "Couldn't initialize Bluetooth, missing android.permission.BLUETOOTH");
            return;
        }
        BluetoothManager bluetoothManager = (BluetoothManager) this.mContext.getSystemService("bluetooth");
        this.mBluetoothManager = bluetoothManager;
        if (bluetoothManager != null && (btAdapter = bluetoothManager.getAdapter()) != null) {
            for (BluetoothDevice device : btAdapter.getBondedDevices()) {
                Log.d(TAG, "Bluetooth device available: " + device);
                if (isSteamController(device)) {
                    connectBluetoothDevice(device);
                }
            }
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
            filter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
            this.mContext.registerReceiver(this.mBluetoothBroadcast, filter);
            if (this.mIsChromebook) {
                this.mHandler = new Handler(Looper.getMainLooper());
                this.mLastBluetoothDevices = new ArrayList();
            }
        }
    }

    private void shutdownBluetooth() {
        try {
            this.mContext.unregisterReceiver(this.mBluetoothBroadcast);
        } catch (Exception e) {
        }
    }

    public void chromebookConnectionHandler() {
        if (this.mIsChromebook) {
            ArrayList<BluetoothDevice> disconnected = new ArrayList<>();
            ArrayList<BluetoothDevice> connected = new ArrayList<>();
            List<BluetoothDevice> currentConnected = this.mBluetoothManager.getConnectedDevices(7);
            for (BluetoothDevice bluetoothDevice : currentConnected) {
                if (!this.mLastBluetoothDevices.contains(bluetoothDevice)) {
                    connected.add(bluetoothDevice);
                }
            }
            for (BluetoothDevice bluetoothDevice2 : this.mLastBluetoothDevices) {
                if (!currentConnected.contains(bluetoothDevice2)) {
                    disconnected.add(bluetoothDevice2);
                }
            }
            this.mLastBluetoothDevices = currentConnected;
            Iterator<BluetoothDevice> it = disconnected.iterator();
            while (it.hasNext()) {
                disconnectBluetoothDevice(it.next());
            }
            Iterator<BluetoothDevice> it2 = connected.iterator();
            while (it2.hasNext()) {
                connectBluetoothDevice(it2.next());
            }
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    this.chromebookConnectionHandler();
                }
            }, 10000);
        }
    }

    public boolean connectBluetoothDevice(BluetoothDevice bluetoothDevice) {
        Log.v(TAG, "connectBluetoothDevice device=" + bluetoothDevice);
        synchronized (this) {
            if (this.mBluetoothDevices.containsKey(bluetoothDevice)) {
                Log.v(TAG, "Steam controller with address " + bluetoothDevice + " already exists, attempting reconnect");
                this.mBluetoothDevices.get(bluetoothDevice).reconnect();
                return false;
            }
            HIDDeviceBLESteamController device = new HIDDeviceBLESteamController(this, bluetoothDevice);
            int id = device.getId();
            this.mBluetoothDevices.put(bluetoothDevice, device);
            this.mDevicesById.put(Integer.valueOf(id), device);
            return true;
        }
    }

    public void disconnectBluetoothDevice(BluetoothDevice bluetoothDevice) {
        synchronized (this) {
            HIDDeviceBLESteamController device = this.mBluetoothDevices.get(bluetoothDevice);
            if (device != null) {
                int id = device.getId();
                this.mBluetoothDevices.remove(bluetoothDevice);
                this.mDevicesById.remove(Integer.valueOf(id));
                device.shutdown();
                HIDDeviceDisconnected(id);
            }
        }
    }

    public boolean isSteamController(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null || bluetoothDevice.getName() == null || !bluetoothDevice.getName().equals("SteamController") || (bluetoothDevice.getType() & 2) == 0) {
            return false;
        }
        return true;
    }

    private void close() {
        shutdownUSB();
        shutdownBluetooth();
        synchronized (this) {
            for (HIDDevice device : this.mDevicesById.values()) {
                device.shutdown();
            }
            this.mDevicesById.clear();
            this.mBluetoothDevices.clear();
            HIDDeviceReleaseCallback();
        }
    }

    public void setFrozen(boolean frozen) {
        synchronized (this) {
            for (HIDDevice device : this.mDevicesById.values()) {
                device.setFrozen(frozen);
            }
        }
    }

    private HIDDevice getDevice(int id) {
        HIDDevice result;
        synchronized (this) {
            result = this.mDevicesById.get(Integer.valueOf(id));
            if (result == null) {
                Log.v(TAG, "No device for id: " + id);
                Log.v(TAG, "Available devices: " + this.mDevicesById.keySet());
            }
        }
        return result;
    }

    public boolean openDevice(int deviceID) {
        Iterator<HIDDeviceUSB> it = this.mUSBDevices.values().iterator();
        while (true) {
            if (it.hasNext()) {
                HIDDeviceUSB device = it.next();
                if (deviceID == device.getId()) {
                    UsbDevice usbDevice = device.getDevice();
                    if (!this.mUsbManager.hasPermission(usbDevice)) {
                        HIDDeviceOpenPending(deviceID);
                        try {
                            this.mUsbManager.requestPermission(usbDevice, PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_USB_PERMISSION), 0));
                        } catch (Exception e) {
                            Log.v(TAG, "Couldn't request permission for USB device " + usbDevice);
                            HIDDeviceOpenResult(deviceID, false);
                        }
                        return false;
                    }
                }
            }
        }
        try {
            Log.v(TAG, "openDevice deviceID=" + deviceID);
            HIDDevice device2 = getDevice(deviceID);
            if (device2 != null) {
                return device2.open();
            }
            HIDDeviceDisconnected(deviceID);
            return false;
        } catch (Exception e2) {
            Log.e(TAG, "Got exception: " + Log.getStackTraceString(e2));
            return false;
        }
    }

    public int sendOutputReport(int deviceID, byte[] report) {
        try {
            Log.v(TAG, "sendOutputReport deviceID=" + deviceID + " length=" + report.length);
            HIDDevice device = getDevice(deviceID);
            if (device != null) {
                return device.sendOutputReport(report);
            }
            HIDDeviceDisconnected(deviceID);
            return -1;
        } catch (Exception e) {
            Log.e(TAG, "Got exception: " + Log.getStackTraceString(e));
            return -1;
        }
    }

    public int sendFeatureReport(int deviceID, byte[] report) {
        try {
            Log.v(TAG, "sendFeatureReport deviceID=" + deviceID + " length=" + report.length);
            HIDDevice device = getDevice(deviceID);
            if (device != null) {
                return device.sendFeatureReport(report);
            }
            HIDDeviceDisconnected(deviceID);
            return -1;
        } catch (Exception e) {
            Log.e(TAG, "Got exception: " + Log.getStackTraceString(e));
            return -1;
        }
    }

    public boolean getFeatureReport(int deviceID, byte[] report) {
        try {
            Log.v(TAG, "getFeatureReport deviceID=" + deviceID);
            HIDDevice device = getDevice(deviceID);
            if (device != null) {
                return device.getFeatureReport(report);
            }
            HIDDeviceDisconnected(deviceID);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Got exception: " + Log.getStackTraceString(e));
            return false;
        }
    }

    public void closeDevice(int deviceID) {
        try {
            Log.v(TAG, "closeDevice deviceID=" + deviceID);
            HIDDevice device = getDevice(deviceID);
            if (device == null) {
                HIDDeviceDisconnected(deviceID);
            } else {
                device.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Got exception: " + Log.getStackTraceString(e));
        }
    }
}
