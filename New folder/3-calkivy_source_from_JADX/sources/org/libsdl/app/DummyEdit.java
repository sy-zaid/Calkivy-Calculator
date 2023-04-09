package org.libsdl.app;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

/* compiled from: SDLActivity */
class DummyEdit extends View implements View.OnKeyListener {

    /* renamed from: ic */
    InputConnection f0ic;

    public DummyEdit(Context context) {
        super(context);
        setFocusableInTouchMode(true);
        setFocusable(true);
        setOnKeyListener(this);
    }

    public boolean onCheckIsTextEditor() {
        return true;
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == 0) {
            if (SDLActivity.isTextInputEvent(event)) {
                this.f0ic.commitText(String.valueOf((char) event.getUnicodeChar()), 1);
                return true;
            }
            SDLActivity.onNativeKeyDown(keyCode);
            return true;
        } else if (event.getAction() != 1) {
            return false;
        } else {
            SDLActivity.onNativeKeyUp(keyCode);
            return true;
        }
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getAction() == 1 && keyCode == 4 && SDLActivity.mTextEdit != null && SDLActivity.mTextEdit.getVisibility() == 0) {
            SDLActivity.onNativeKeyboardFocusLost();
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        this.f0ic = new SDLInputConnection(this, true);
        outAttrs.inputType = SDLActivity.keyboardInputType;
        outAttrs.imeOptions = 301989888;
        return this.f0ic;
    }
}
