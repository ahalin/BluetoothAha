package com.coordiwise.bluetoothaha;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RfcommService extends Service {
    public final static String TAG = "AAAA";
    public RfcServer svc = null;
    public RfcommService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        svc = new RfcServer();
        svc.Start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
