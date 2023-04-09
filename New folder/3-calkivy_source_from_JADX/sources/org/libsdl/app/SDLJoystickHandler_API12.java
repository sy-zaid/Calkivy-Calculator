package org.libsdl.app;

import android.view.InputDevice;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* compiled from: SDLControllerManager */
class SDLJoystickHandler_API12 extends SDLJoystickHandler {
    private ArrayList<SDLJoystick> mJoysticks = new ArrayList<>();

    /* compiled from: SDLControllerManager */
    static class SDLJoystick {
        public ArrayList<InputDevice.MotionRange> axes;
        public String desc;
        public int device_id;
        public ArrayList<InputDevice.MotionRange> hats;
        public String name;

        SDLJoystick() {
        }
    }

    /* compiled from: SDLControllerManager */
    static class RangeComparator implements Comparator<InputDevice.MotionRange> {
        RangeComparator() {
        }

        public int compare(InputDevice.MotionRange arg0, InputDevice.MotionRange arg1) {
            int arg0Axis = arg0.getAxis();
            int arg1Axis = arg1.getAxis();
            if (arg0Axis == 22) {
                arg0Axis = 23;
            } else if (arg0Axis == 23) {
                arg0Axis = 22;
            }
            if (arg1Axis == 22) {
                arg1Axis = 23;
            } else if (arg1Axis == 23) {
                arg1Axis = 22;
            }
            return arg0Axis - arg1Axis;
        }
    }

    public void pollInputDevices() {
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int i = 0; i < deviceIds.length; i++) {
            if (getJoystick(deviceIds[i]) == null) {
                SDLJoystick joystick = new SDLJoystick();
                InputDevice joystickDevice = InputDevice.getDevice(deviceIds[i]);
                if (SDLControllerManager.isDeviceSDLJoystick(deviceIds[i])) {
                    joystick.device_id = deviceIds[i];
                    joystick.name = joystickDevice.getName();
                    joystick.desc = getJoystickDescriptor(joystickDevice);
                    joystick.axes = new ArrayList<>();
                    joystick.hats = new ArrayList<>();
                    List<InputDevice.MotionRange> ranges = joystickDevice.getMotionRanges();
                    Collections.sort(ranges, new RangeComparator());
                    for (InputDevice.MotionRange range : ranges) {
                        if ((range.getSource() & 16) != 0) {
                            if (range.getAxis() == 15 || range.getAxis() == 16) {
                                joystick.hats.add(range);
                            } else {
                                joystick.axes.add(range);
                            }
                        }
                    }
                    this.mJoysticks.add(joystick);
                    SDLControllerManager.nativeAddJoystick(joystick.device_id, joystick.name, joystick.desc, getVendorId(joystickDevice), getProductId(joystickDevice), false, getButtonMask(joystickDevice), joystick.axes.size(), joystick.hats.size() / 2, 0);
                }
            }
        }
        ArrayList<Integer> removedDevices = new ArrayList<>();
        for (int i2 = 0; i2 < this.mJoysticks.size(); i2++) {
            int device_id = this.mJoysticks.get(i2).device_id;
            int j = 0;
            while (j < deviceIds.length && device_id != deviceIds[j]) {
                j++;
            }
            if (j == deviceIds.length) {
                removedDevices.add(Integer.valueOf(device_id));
            }
        }
        for (int i3 = 0; i3 < removedDevices.size(); i3++) {
            int device_id2 = removedDevices.get(i3).intValue();
            SDLControllerManager.nativeRemoveJoystick(device_id2);
            int j2 = 0;
            while (true) {
                if (j2 >= this.mJoysticks.size()) {
                    break;
                } else if (this.mJoysticks.get(j2).device_id == device_id2) {
                    this.mJoysticks.remove(j2);
                    break;
                } else {
                    j2++;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public SDLJoystick getJoystick(int device_id) {
        for (int i = 0; i < this.mJoysticks.size(); i++) {
            if (this.mJoysticks.get(i).device_id == device_id) {
                return this.mJoysticks.get(i);
            }
        }
        return null;
    }

    public boolean handleMotionEvent(MotionEvent event) {
        if ((event.getSource() & 16777232) == 0) {
            return true;
        }
        int actionPointerIndex = event.getActionIndex();
        switch (event.getActionMasked()) {
            case 2:
                SDLJoystick joystick = getJoystick(event.getDeviceId());
                if (joystick == null) {
                    return true;
                }
                for (int i = 0; i < joystick.axes.size(); i++) {
                    InputDevice.MotionRange range = joystick.axes.get(i);
                    SDLControllerManager.onNativeJoy(joystick.device_id, i, (((event.getAxisValue(range.getAxis(), actionPointerIndex) - range.getMin()) / range.getRange()) * 2.0f) - 1.0f);
                }
                for (int i2 = 0; i2 < joystick.hats.size(); i2 += 2) {
                    SDLControllerManager.onNativeHat(joystick.device_id, i2 / 2, Math.round(event.getAxisValue(joystick.hats.get(i2).getAxis(), actionPointerIndex)), Math.round(event.getAxisValue(joystick.hats.get(i2 + 1).getAxis(), actionPointerIndex)));
                }
                return true;
            default:
                return true;
        }
    }

    public String getJoystickDescriptor(InputDevice joystickDevice) {
        return joystickDevice.getName();
    }

    public int getProductId(InputDevice joystickDevice) {
        return 0;
    }

    public int getVendorId(InputDevice joystickDevice) {
        return 0;
    }

    public int getButtonMask(InputDevice joystickDevice) {
        return -1;
    }
}
