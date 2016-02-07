package com.slava.chat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyService.class", "service was stopped");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyService.class", "service was started");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyService.class", "Send broadcasting message");
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("custom-event").putExtra("message", "This is my message!"));
        return super.onStartCommand(intent, flags, startId);
    }
}
