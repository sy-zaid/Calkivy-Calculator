package org.renpy.android;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class ResourceManager {
    private Activity act;
    private Resources res;

    public ResourceManager(Activity activity) {
        this.act = activity;
        this.res = activity.getResources();
    }

    public int getIdentifier(String name, String kind) {
        Log.v("SDL", "getting identifier");
        Log.v("SDL", "kind is " + kind + " and name " + name);
        Log.v("SDL", "result is " + this.res.getIdentifier(name, kind, this.act.getPackageName()));
        return this.res.getIdentifier(name, kind, this.act.getPackageName());
    }

    public String getString(String name) {
        try {
            Log.v("SDL", "asked to get string " + name);
            return this.res.getString(getIdentifier(name, "string"));
        } catch (Exception e) {
            Log.v("SDL", "got exception looking for string!");
            return null;
        }
    }

    public View inflateView(String name) {
        return this.act.getLayoutInflater().inflate(getIdentifier(name, "layout"), (ViewGroup) null);
    }

    public View getViewById(View v, String name) {
        return v.findViewById(getIdentifier(name, "id"));
    }
}
