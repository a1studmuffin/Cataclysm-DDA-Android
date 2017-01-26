package com.MichaelDavies.CataclysmDDA;

import org.libsdl.app.SDLActivity; 

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.*;
import android.view.*;
import android.util.Log;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;

public class CataclysmDDA extends SDLActivity {
    private static final String TAG = "CDDA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		
        mSeparateMouseAndTouch = true;

    		// If the game hasn't been installed yet, copy assets from the APK to app's files directory.
    		// This allows all data to be read/written from C++ via std::io operations as per desktop builds.
    		// It would be cool if this wasn't necessary, but this is the path of least resistance to get it running on Android.
    		// I certainly don't fancy rewriting all of CDDA's file IO operations, how about you? :)
        String this_version = "0.C-20818-g6ec9931";
        if (!this_version.equals(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("installed", ""))) {
            deleteRecursive(new File(getExternalFilesDir(null) + "/data"));
            deleteRecursive(new File(getExternalFilesDir(null) + "/gfx"));
            deleteRecursive(new File(getExternalFilesDir(null) + "/lua"));
            copyAssetFolder(getAssets(), "data", getExternalFilesDir(null) + "/data");
            copyAssetFolder(getAssets(), "gfx", getExternalFilesDir(null) + "/gfx");
            copyAssetFolder(getAssets(), "lua", getExternalFilesDir(null) + "/lua");
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("installed", this_version).commit();
        }
        super.onCreate(savedInstanceState);
    }

    // Pinched from http://stackoverflow.com/questions/4943629/how-to-delete-a-whole-folder-and-content
    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

	// Pinched from http://stackoverflow.com/questions/16983989/copy-directory-from-assets-to-data-folder
    private static boolean copyAssetFolder(AssetManager assetManager, String fromAssetPath, String toPath) {
        try {
            String[] files = assetManager.list(fromAssetPath);
            new File(toPath).mkdirs();
            boolean res = true;
            for (String file : files)
            {
                if (file.contains("."))
                    res &= copyAsset(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
                else 
                    res &= copyAssetFolder(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean copyAsset(AssetManager assetManager, String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
          in = assetManager.open(fromAssetPath);
          new File(toPath).createNewFile();
          out = new FileOutputStream(toPath);
          copyFile(in, out);
          in.close();
          in = null;
          out.flush();
          out.close();
          out = null;
          return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1) {
          out.write(buffer, 0, read);
        }
    }
}