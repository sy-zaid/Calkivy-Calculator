package org.libsdl.app;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.os.Build;
import android.util.Log;
import java.util.Arrays;
import org.kamranzafar.jtar.TarHeader;

class HIDDeviceUSB implements HIDDevice {
    private static final String TAG = "hidapi";
    protected UsbDeviceConnection mConnection;
    protected UsbDevice mDevice;
    protected int mDeviceId;
    protected boolean mFrozen;
    protected UsbEndpoint mInputEndpoint;
    protected InputThread mInputThread;
    protected int mInterface;
    protected HIDDeviceManager mManager;
    protected UsbEndpoint mOutputEndpoint;
    protected boolean mRunning = false;

    public HIDDeviceUSB(HIDDeviceManager manager, UsbDevice usbDevice, int interface_number) {
        this.mManager = manager;
        this.mDevice = usbDevice;
        this.mInterface = interface_number;
        this.mDeviceId = manager.getDeviceIDForIdentifier(getIdentifier());
    }

    public String getIdentifier() {
        return String.format("%s/%x/%x", new Object[]{this.mDevice.getDeviceName(), Integer.valueOf(this.mDevice.getVendorId()), Integer.valueOf(this.mDevice.getProductId())});
    }

    public int getId() {
        return this.mDeviceId;
    }

    public int getVendorId() {
        return this.mDevice.getVendorId();
    }

    public int getProductId() {
        return this.mDevice.getProductId();
    }

    public String getSerialNumber() {
        String result = null;
        if (Build.VERSION.SDK_INT >= 21) {
            result = this.mDevice.getSerialNumber();
        }
        if (result == null) {
            return "";
        }
        return result;
    }

    public int getVersion() {
        return 0;
    }

    public String getManufacturerName() {
        String result = null;
        if (Build.VERSION.SDK_INT >= 21) {
            result = this.mDevice.getManufacturerName();
        }
        if (result != null) {
            return result;
        }
        return String.format("%x", new Object[]{Integer.valueOf(getVendorId())});
    }

    public String getProductName() {
        String result = null;
        if (Build.VERSION.SDK_INT >= 21) {
            result = this.mDevice.getProductName();
        }
        if (result != null) {
            return result;
        }
        return String.format("%x", new Object[]{Integer.valueOf(getProductId())});
    }

    public UsbDevice getDevice() {
        return this.mDevice;
    }

    public String getDeviceName() {
        return getManufacturerName() + " " + getProductName() + "(0x" + String.format("%x", new Object[]{Integer.valueOf(getVendorId())}) + "/0x" + String.format("%x", new Object[]{Integer.valueOf(getProductId())}) + ")";
    }

    public boolean open() {
        UsbDeviceConnection openDevice = this.mManager.getUSBManager().openDevice(this.mDevice);
        this.mConnection = openDevice;
        if (openDevice == null) {
            Log.w(TAG, "Unable to open USB device " + getDeviceName());
            return false;
        }
        for (int i = 0; i < this.mDevice.getInterfaceCount(); i++) {
            if (!this.mConnection.claimInterface(this.mDevice.getInterface(i), true)) {
                Log.w(TAG, "Failed to claim interfaces on USB device " + getDeviceName());
                close();
                return false;
            }
        }
        UsbInterface iface = this.mDevice.getInterface(this.mInterface);
        for (int j = 0; j < iface.getEndpointCount(); j++) {
            UsbEndpoint endpt = iface.getEndpoint(j);
            switch (endpt.getDirection()) {
                case 0:
                    if (this.mOutputEndpoint != null) {
                        break;
                    } else {
                        this.mOutputEndpoint = endpt;
                        break;
                    }
                case 128:
                    if (this.mInputEndpoint != null) {
                        break;
                    } else {
                        this.mInputEndpoint = endpt;
                        break;
                    }
            }
        }
        if (this.mInputEndpoint == null || this.mOutputEndpoint == null) {
            Log.w(TAG, "Missing required endpoint on USB device " + getDeviceName());
            close();
            return false;
        }
        this.mRunning = true;
        InputThread inputThread = new InputThread();
        this.mInputThread = inputThread;
        inputThread.start();
        return true;
    }

    public int sendFeatureReport(byte[] report) {
        int offset = 0;
        int length = report.length;
        boolean skipped_report_id = false;
        byte report_number = report[0];
        if (report_number == 0) {
            offset = 0 + 1;
            length--;
            skipped_report_id = true;
        }
        int res = this.mConnection.controlTransfer(33, 9, report_number | TarHeader.LF_OLDNORM, 0, report, offset, length, 1000);
        if (res < 0) {
            Log.w(TAG, "sendFeatureReport() returned " + res + " on device " + getDeviceName());
            return -1;
        } else if (skipped_report_id) {
            return length + 1;
        } else {
            return length;
        }
    }

    public int sendOutputReport(byte[] report) {
        int r = this.mConnection.bulkTransfer(this.mOutputEndpoint, report, report.length, 1000);
        if (r != report.length) {
            Log.w(TAG, "sendOutputReport() returned " + r + " on device " + getDeviceName());
        }
        return r;
    }

    public boolean getFeatureReport(byte[] report) {
        boolean skipped_report_id;
        int length;
        int offset;
        byte[] data;
        byte[] bArr = report;
        int length2 = bArr.length;
        byte report_number = bArr[0];
        if (report_number == 0) {
            offset = 0 + 1;
            length = length2 - 1;
            skipped_report_id = true;
        } else {
            offset = 0;
            length = length2;
            skipped_report_id = false;
        }
        int res = this.mConnection.controlTransfer(161, 1, report_number | TarHeader.LF_OLDNORM, 0, report, offset, length, 1000);
        if (res < 0) {
            Log.w(TAG, "getFeatureReport() returned " + res + " on device " + getDeviceName());
            return false;
        }
        if (skipped_report_id) {
            res++;
            length++;
        }
        if (res == length) {
            data = report;
        } else {
            data = Arrays.copyOfRange(bArr, 0, res);
        }
        this.mManager.HIDDeviceFeatureReport(this.mDeviceId, data);
        return true;
    }

    public void close() {
        this.mRunning = false;
        if (this.mInputThread != null) {
            while (this.mInputThread.isAlive()) {
                this.mInputThread.interrupt();
                try {
                    this.mInputThread.join();
                } catch (InterruptedException e) {
                }
            }
            this.mInputThread = null;
        }
        if (this.mConnection != null) {
            for (int i = 0; i < this.mDevice.getInterfaceCount(); i++) {
                this.mConnection.releaseInterface(this.mDevice.getInterface(i));
            }
            this.mConnection.close();
            this.mConnection = null;
        }
    }

    public void shutdown() {
        close();
        this.mManager = null;
    }

    public void setFrozen(boolean frozen) {
        this.mFrozen = frozen;
    }

    protected class InputThread extends Thread {
        protected InputThread() {
        }

        public void run() {
            byte[] data;
            int packetSize = HIDDeviceUSB.this.mInputEndpoint.getMaxPacketSize();
            byte[] packet = new byte[packetSize];
            while (HIDDeviceUSB.this.mRunning) {
                try {
                    int r = HIDDeviceUSB.this.mConnection.bulkTransfer(HIDDeviceUSB.this.mInputEndpoint, packet, packetSize, 1000);
                    if (r > 0) {
                        if (r == packetSize) {
                            data = packet;
                        } else {
                            data = Arrays.copyOfRange(packet, 0, r);
                        }
                        if (!HIDDeviceUSB.this.mFrozen) {
                            HIDDeviceUSB.this.mManager.HIDDeviceInputReport(HIDDeviceUSB.this.mDeviceId, data);
                        }
                    }
                } catch (Exception e) {
                    Log.v(HIDDeviceUSB.TAG, "Exception in UsbDeviceConnection bulktransfer: " + e);
                    return;
                }
            }
        }
    }
}
