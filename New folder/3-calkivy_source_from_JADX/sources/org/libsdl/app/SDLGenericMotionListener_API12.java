package org.libsdl.app;

import android.view.MotionEvent;
import android.view.View;

/* compiled from: SDLControllerManager */
class SDLGenericMotionListener_API12 implements View.OnGenericMotionListener {
    SDLGenericMotionListener_API12() {
    }

    public boolean onGenericMotion(View v, MotionEvent event) {
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
                            SDLActivity.onNativeMouse(0, action, event.getX(0), event.getY(0), false);
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
        return false;
    }

    public boolean inRelativeMode() {
        return false;
    }

    public boolean setRelativeMouseEnabled(boolean enabled) {
        return false;
    }

    public void reclaimRelativeMouseModeIfNeeded() {
    }

    public float getEventX(MotionEvent event) {
        return event.getX(0);
    }

    public float getEventY(MotionEvent event) {
        return event.getY(0);
    }
}
