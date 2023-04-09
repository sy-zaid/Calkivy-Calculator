package org.libsdl.app;

import android.util.Log;

/* compiled from: SDLActivity */
class SDLMain implements Runnable {
    SDLMain() {
    }

    public void run() {
        String library = SDLActivity.mSingleton.getMainSharedObject();
        String function = SDLActivity.mSingleton.getMainFunction();
        String[] arguments = SDLActivity.mSingleton.getArguments();
        Log.v("SDL", "Running main function " + function + " from library " + library);
        SDLActivity.mSingleton.appConfirmedActive();
        SDLActivity.nativeRunMain(library, function, arguments);
        Log.v("SDL", "Finished main function");
        if (!SDLActivity.mExitCalledFromJava) {
            SDLActivity.handleNativeExit();
        }
    }
}
