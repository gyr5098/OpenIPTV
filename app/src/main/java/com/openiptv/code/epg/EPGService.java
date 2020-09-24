package com.openiptv.code.epg;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.openiptv.code.Constants;
import com.openiptv.code.DatabaseActions;


import static com.openiptv.code.Constants.DEBUG;

public class EPGService extends Service {
    private EPGCaptureTask epgCaptureTask;
    private Bundle accountDetails;

    @Override
    public void onCreate() {


        super.onCreate();
        DatabaseActions databaseActions = new DatabaseActions(getApplicationContext());
        databaseActions.syncActiveAccount();
        databaseActions.close();
        if (DEBUG) {
            Log.d("EPGService", "called!");
        }

        if(isSetupComplete(this)) {
            Log.d("EPGService", "Creating Capture Task");
            epgCaptureTask = new EPGCaptureTask(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        epgCaptureTask.stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // TODO: use utils class methods
    public static boolean isSetupComplete(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.ACCOUNT, Context.MODE_PRIVATE);

        Log.d("EPG", "Setup complete: " + sharedPreferences.getBoolean("SETUP", false));
        return sharedPreferences.getBoolean("SETUP", false);
    }

    // TODO: use utils class methods
    public static void setSetupComplete(Context context, boolean isSetupComplete) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.ACCOUNT, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("SETUP", isSetupComplete);
        editor.apply();
    }
}
