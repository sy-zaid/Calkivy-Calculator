package org.kivy.android;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.renpy.android.AssetExtract;

public class PythonUtil {
    private static final String TAG = "pythonutil";

    protected static void addLibraryIfExists(ArrayList<String> libsList, String pattern, File libsDir) {
        File[] files = libsDir.listFiles();
        String pattern2 = "lib" + pattern + "\\.so";
        Pattern p = Pattern.compile(pattern2);
        for (File file : files) {
            String name = file.getName();
            Log.v(TAG, "Checking pattern " + pattern2 + " against " + name);
            if (p.matcher(name).matches()) {
                Log.v(TAG, "Pattern " + pattern2 + " matched file " + name);
                libsList.add(name.substring(3, name.length() - 3));
            }
        }
    }

    protected static ArrayList<String> getLibraries(File libsDir) {
        ArrayList<String> libsList = new ArrayList<>();
        addLibraryIfExists(libsList, "sqlite3", libsDir);
        addLibraryIfExists(libsList, "ffi", libsDir);
        addLibraryIfExists(libsList, "png16", libsDir);
        addLibraryIfExists(libsList, "ssl.*", libsDir);
        addLibraryIfExists(libsList, "crypto.*", libsDir);
        addLibraryIfExists(libsList, "SDL2", libsDir);
        addLibraryIfExists(libsList, "SDL2_image", libsDir);
        addLibraryIfExists(libsList, "SDL2_mixer", libsDir);
        addLibraryIfExists(libsList, "SDL2_ttf", libsDir);
        libsList.add("python3.5m");
        libsList.add("python3.6m");
        libsList.add("python3.7m");
        libsList.add("python3.8");
        libsList.add("python3.9");
        libsList.add("main");
        return libsList;
    }

    public static void loadLibraries(File filesDir, File libsDir) {
        boolean foundPython = false;
        Iterator<String> it = getLibraries(libsDir).iterator();
        while (it.hasNext()) {
            String lib = it.next();
            Log.v(TAG, "Loading library: " + lib);
            try {
                System.loadLibrary(lib);
                if (lib.startsWith("python")) {
                    foundPython = true;
                }
            } catch (UnsatisfiedLinkError e) {
                Log.v(TAG, "Library loading error: " + e.getMessage());
                if (lib.startsWith("python3.9") && !foundPython) {
                    throw new RuntimeException("Could not load any libpythonXXX.so");
                } else if (!lib.startsWith("python")) {
                    Log.v(TAG, "An UnsatisfiedLinkError occurred loading " + lib);
                    throw e;
                }
            }
        }
        Log.v(TAG, "Loaded everything!");
    }

    public static String getAppRoot(Context ctx) {
        return ctx.getFilesDir().getAbsolutePath() + "/app";
    }

    public static String getResourceString(Context ctx, String name) {
        Resources res = ctx.getResources();
        return res.getString(res.getIdentifier(name, "string", ctx.getPackageName()));
    }

    protected static void toastError(final Activity activity, final String msg) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, msg, 1).show();
            }
        });
        synchronized (activity) {
            try {
                activity.wait(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    protected static void recursiveDelete(File f) {
        if (f.isDirectory()) {
            for (File r : f.listFiles()) {
                recursiveDelete(r);
            }
        }
        f.delete();
    }

    public static void unpackAsset(Context ctx, String resource, File target, boolean cleanup_on_version_update) {
        String diskVersion;
        Log.v(TAG, "Unpacking " + resource + " " + target.getName());
        String dataVersion = getResourceString(ctx, resource + "_version");
        Log.v(TAG, "Data version is " + dataVersion);
        if (dataVersion != null) {
            String diskVersionFn = target.getAbsolutePath() + "/" + resource + ".version";
            try {
                byte[] buf = new byte[64];
                InputStream is = new FileInputStream(diskVersionFn);
                diskVersion = new String(buf, 0, is.read(buf));
                is.close();
            } catch (Exception e) {
                diskVersion = "";
            }
            if (!dataVersion.equals(diskVersion)) {
                Log.v(TAG, "Extracting " + resource + " assets.");
                if (cleanup_on_version_update) {
                    recursiveDelete(target);
                }
                target.mkdirs();
                if (!new AssetExtract(ctx).extractTar(resource + ".tar", target.getAbsolutePath(), "private")) {
                    String msg = "Could not extract " + resource + " data.";
                    if (ctx instanceof Activity) {
                        toastError((Activity) ctx, msg);
                    } else {
                        Log.v(TAG, msg);
                    }
                }
                try {
                    new File(target, ".nomedia").createNewFile();
                    FileOutputStream os = new FileOutputStream(diskVersionFn);
                    os.write(dataVersion.getBytes());
                    os.close();
                } catch (Exception e2) {
                    Log.w(TAG, e2);
                }
            }
        }
    }

    public static void unpackPyBundle(Context ctx, String resource, File target, boolean cleanup_on_version_update) {
        String diskVersion;
        Log.v(TAG, "Unpacking " + resource + " " + target.getName());
        String dataVersion = getResourceString(ctx, "private_version");
        Log.v(TAG, "Data version is " + dataVersion);
        if (dataVersion != null) {
            String diskVersionFn = target.getAbsolutePath() + "/libpybundle.version";
            try {
                byte[] buf = new byte[64];
                InputStream is = new FileInputStream(diskVersionFn);
                diskVersion = new String(buf, 0, is.read(buf));
                is.close();
            } catch (Exception e) {
                diskVersion = "";
            }
            if (!dataVersion.equals(diskVersion)) {
                Log.v(TAG, "Extracting " + resource + " assets.");
                if (cleanup_on_version_update) {
                    recursiveDelete(target);
                }
                target.mkdirs();
                if (!new AssetExtract(ctx).extractTar(resource + ".so", target.getAbsolutePath(), "pybundle")) {
                    String msg = "Could not extract " + resource + " data.";
                    if (ctx instanceof Activity) {
                        toastError((Activity) ctx, msg);
                    } else {
                        Log.v(TAG, msg);
                    }
                }
                try {
                    FileOutputStream os = new FileOutputStream(diskVersionFn);
                    os.write(dataVersion.getBytes());
                    os.close();
                } catch (Exception e2) {
                    Log.w(TAG, e2);
                }
            }
        }
    }
}
