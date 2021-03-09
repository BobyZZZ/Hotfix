package com.bb.hotfix;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class App extends Application {
    String TAG = "App";
    @Override
    public void onCreate() {
        int checkSelfPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.d(TAG, "checkSelfPermission: " + checkSelfPermission);
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
            boolean shouldFix = HotFixUtils.checkShouldFix();
            Log.d(TAG, "shouldFix: " + shouldFix);
            HotFixUtils.hotfix(this);
        }
        super.onCreate();
    }
}
