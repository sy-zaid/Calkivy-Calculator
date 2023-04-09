package org.libsdl.app;

import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

/* compiled from: SDLControllerManager */
class SDLGenericMotionListener_API26 extends SDLGenericMotionListener_API24 {
    private boolean mRelativeModeEnabled;

    SDLGenericMotionListener_API26() {
    }

    public boolean onGenericMotion(View v, MotionEvent event) {
        switch (event.getSource()) {
            case 513:
            case 1025:
            case 16777232:
                return SDLControllerManager.handleJoystickMotionEvent(event);
            case 8194:
            case 12290:
                if (SDLActivity.mSeparateMouseAndTouch != 0) {
                    int action = event.getActionMasked();
                    switch (action) {
                        case 7:
                            SDLActivity.onNativeMouse(0, action, event.getX(0), event.getY(0), false);
                            return true;
                        case 8:
                            SDLActivity.onNativeMouse(0, action, event.getAxisValue(10, 0), event.getAxisValue(9, 0), false);
                            return true;
                    }
                }
                break;
            case 131076:
                if (SDLActivity.mSeparateMouseAndTouch) {
                    int action2 = event.getActionMasked();
                    switch (action2) {
                        case 7:
                            SDLActivity.onNativeMouse(0, action2, event.getX(0), event.getY(0), true);
                            return true;
                        case 8:
                            SDLActivity.onNativeMouse(0, action2, event.getAxisValue(10, 0), event.getAxisValue(9, 0), false);
                            return true;
                    }
                }
                break;
        }
        return false;
    }

    public boolean supportsRelativeMouse() {
        return !SDLActivity.isDeXMode() || Build.VERSION.SDK_INT >= 27;
    }

    public boolean inRelativeMode() {
        return this.mRelativeModeEnabled;
    }

    public boolean setRelativeMouseEnabled(boolean enabled) {
        if (SDLActivity.isDeXMode() && Build.VERSION.SDK_INT < 27) {
            return false;
        }
        if (enabled) {
            SDLActivity.getContentView().requestPointerCapture();
        } else {
            SDLActivity.getContentView().releasePointerCapture();
        }
        this.mRelativeModeEnabled = enabled;
        return true;
    }

    public void reclaimRelativeMouseModeIfNeeded() {
        if (this.mRelativeModeEnabled && !SDLActivity.isDeXMode()) {
            SDLActivity.getContentView().requestPointerCapture();
        }
    }

    public float getEventX(MotionEvent event) {
        return event.getX(0);
    }

    public float getEventY(MotionEvent event) {
        return event.getY(0);
    }
}
