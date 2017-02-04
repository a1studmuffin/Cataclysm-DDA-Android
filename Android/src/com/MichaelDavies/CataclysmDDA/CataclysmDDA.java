package com.MichaelDavies.CataclysmDDA;

import org.libsdl.app.SDLActivity;
import android.util.Log;
import android.content.Context;
import android.os.Vibrator;

public class CataclysmDDA extends SDLActivity {
    private static final String TAG = "CDDA";

    public void vibrate(int duration) {
        Vibrator v = (Vibrator)SDLActivity.mSingleton.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(duration);
    }
}