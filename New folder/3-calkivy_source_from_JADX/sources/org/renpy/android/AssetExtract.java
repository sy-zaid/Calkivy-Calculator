package org.renpy.android;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;

public class AssetExtract {
    private AssetManager mAssetManager = null;

    public AssetExtract(Context context) {
        this.mAssetManager = context.getAssets();
    }

    public boolean extractTar(String asset, String target, String method) {
        byte[] buf = new byte[1048576];
        InputStream assetStream = null;
        if (method == "private") {
            try {
                assetStream = this.mAssetManager.open(asset, 2);
            } catch (IOException e) {
                Log.e("python", "opening up extract tar", e);
                return false;
            }
        } else if (method == "pybundle") {
            assetStream = new FileInputStream(asset);
        }
        TarInputStream tis = new TarInputStream(new BufferedInputStream(new GZIPInputStream(new BufferedInputStream(assetStream, 8192)), 8192));
        while (true) {
            try {
                TarEntry entry = tis.getNextEntry();
                if (entry == null) {
                    try {
                        tis.close();
                        assetStream.close();
                        return true;
                    } catch (IOException e2) {
                        return true;
                    }
                } else {
                    Log.v("python", "extracting " + entry.getName());
                    if (entry.isDirectory()) {
                        try {
                            new File(target + "/" + entry.getName()).mkdirs();
                        } catch (SecurityException e3) {
                        }
                    } else {
                        OutputStream out = null;
                        String path = target + "/" + entry.getName();
                        try {
                            out = new BufferedOutputStream(new FileOutputStream(path), 8192);
                        } catch (FileNotFoundException | SecurityException e4) {
                        }
                        if (out == null) {
                            Log.e("python", "could not open " + path);
                            return false;
                        }
                        while (true) {
                            try {
                                int len = tis.read(buf);
                                if (len == -1) {
                                    break;
                                }
                                out.write(buf, 0, len);
                            } catch (IOException e5) {
                                Log.e("python", "extracting zip", e5);
                                return false;
                            }
                        }
                        out.flush();
                        out.close();
                    }
                }
            } catch (IOException e6) {
                Log.e("python", "extracting tar", e6);
                return false;
            }
        }
    }
}
