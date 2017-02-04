package com.MichaelDavies.CataclysmDDA;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.*;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;

public class SplashScreen extends Activity {
    private static final String VERSIONNAME = "0.C-20818-g6ec9931";
    private static final String TAG = "Splash";
    private static final int INSTALL_DIALOG_ID = 0;
    private ProgressDialog installDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        // Start the game if already installed, otherwise start installing...
        if (VERSIONNAME.equals(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("installed", ""))) {
            startGameActivity(false);
        }
        else {
            new InstallProgramTask().execute();             
        }
    }

    @Override
    public Dialog onCreateDialog(int id) {
        switch (id) {
            case INSTALL_DIALOG_ID:
                installDialog = new ProgressDialog(this);
                installDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                boolean clean_install = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("installed", "").isEmpty();
                installDialog.setTitle(getString(clean_install ? R.string.installing : R.string.upgrading));
                installDialog.setIndeterminate(true);
                installDialog.setCancelable(false);
                return installDialog;
            default:
                return null;
        }
    }

    private void startGameActivity(boolean delay) {
        if (!delay) {
            runOnUiThread(new StartGameRunnable());
        }
        else {
            // Wait 1.5 seconds, then start game
            Timer timer = new Timer();
            TimerTask gameStartTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new StartGameRunnable());
                }
            };
            timer.schedule(gameStartTask, 1500);
        }
    }

    private final class StartGameRunnable implements Runnable {
        @Override
        public void run() {
            Intent intent = new Intent(SplashScreen.this, CataclysmDDA.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        }
    }

    private class InstallProgramTask extends AsyncTask<Void, Integer, Void> {
        private static final int TOTAL_FILES = 1410;
        private int installedFiles = 0;

        @Override
        protected void onPreExecute() {
            showDialog(INSTALL_DIALOG_ID);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (installDialog != null) {
                installDialog.setIndeterminate(false);
                installDialog.setMax(TOTAL_FILES);
            }
            publishProgress(installedFiles);

            // Clear out the old data if it exists
            deleteRecursive(new File(getExternalFilesDir(null) + "/data"));
            deleteRecursive(new File(getExternalFilesDir(null) + "/gfx"));
            deleteRecursive(new File(getExternalFilesDir(null) + "/lua"));

            // Install the new data
            copyAssetFolder(getAssets(), "data", getExternalFilesDir(null) + "/data");
            copyAssetFolder(getAssets(), "gfx", getExternalFilesDir(null) + "/gfx");
            copyAssetFolder(getAssets(), "lua", getExternalFilesDir(null) + "/lua");

            // Remember which version the installed data is 
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("installed", VERSIONNAME).commit();

            publishProgress(++installedFiles);
            Log.d(TAG, "Total number of files copied: " + installedFiles);
            return null;
        }

        // Pinched from http://stackoverflow.com/questions/4943629/how-to-delete-a-whole-folder-and-content
        void deleteRecursive(File fileOrDirectory) {
            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles())
                    deleteRecursive(child);

            fileOrDirectory.delete();
        }

        // Pinched from http://stackoverflow.com/questions/16983989/copy-directory-from-assets-to-data-folder
        private boolean copyAssetFolder(AssetManager assetManager, String fromAssetPath, String toPath) {
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

        private boolean copyAsset(AssetManager assetManager, String fromAssetPath, String toPath) {
            publishProgress(++installedFiles, TOTAL_FILES);
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

        private void copyFile(InputStream in, OutputStream out) throws IOException {
            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1) {
              out.write(buffer, 0, read);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (installDialog == null) {
                return;
            }
            installDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            removeDialog(INSTALL_DIALOG_ID);
            startGameActivity(true);
        }
    }
}