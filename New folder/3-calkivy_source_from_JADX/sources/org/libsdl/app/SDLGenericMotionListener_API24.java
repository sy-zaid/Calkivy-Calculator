package org.libsdl.app;

import android.view.MotionEvent;
import android.view.View;

/* compiled from: SDLControllerManager */
class SDLGenericMotionListener_API24 extends SDLGenericMotionListener_API12 {
    private boolean mRelativeModeEnabled;

    SDLGenericMotionListener_API24() {
    }

    public boolean onGenericMotion(View v, MotionEvent event) {
        float y;
        float x;
        switch (event.getSource()) {
            case 513:
            case 1025:
            case 16777232:
                return SDLControllerManager.handleJoystickMotionEvent(event);
            case 8194:
                if (SDLActivity.mSeparateMouseAndTouch) {
                    int action = event.getActionMasked();
                    switch (action) {
                        case 7:
                            if (this.mRelativeModeEnabled) {
                                x = event.getAxisValue(27);
                                y = event.getAxisValue(28);
                            } else {
                                x = event.getX(0);
                                y = event.getY(0);
                            }
                            SDLActivity.onNativeMouse(0, action, x, y, this.mRelativeModeEnabled);
                            return true;
                        case 8:
                            SDLActivity.onNativeMouse(0, action, event.getAxisValue(10, 0), event.getAxisValue(9, 0), false);
                            return true;
                    }
                }
                break;
        }
        return false;
    }

    public boolean supportsRelativeMouse() {
        return true;
    }

    public boolean inRelativeMode() {
        return this.mRelativeModeEnabled;
    }

    public boolean setRelativeMouseEnabled(boolean enabled) {
        this.mRelativeModeEnabled = enabled;
        return true;
    }

    public float getEventX(MotionEvent event) {
        if (this.mRelativeModeEnabled) {
            return event.getAxisValue(27);
        }
        return event.getX(0);
    }

    public float getEventY(MotionEvent event) {
        if (this.mRelativeModeEnabled) {
            return event.getAxisValue(28);
        }
        return event.getY(0);
    }
}
