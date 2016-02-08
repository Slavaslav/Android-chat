package com.slava.chat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.parse.ParseObject;

import java.util.List;

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

        // Run load dialogs list
        if (intent.getStringExtra("message") != null && intent.getStringExtra("message").equals("loadUserDialogs"))
            loadUsersDialogs();

        return super.onStartCommand(intent, flags, startId);
    }

    private void loadUsersDialogs() {
        Account.loadUserDialogs(new MainActivity.MyCallback() {
            @Override
            public void success() {
            }

            @Override
            public void success(List<ParseObject> list) {
                Log.d("mylog", "FragmentMain " + list.size());
                LocalBroadcastManager.getInstance(MyService.this).sendBroadcast(new Intent("custom-event").putExtra("message", "This is my message!"));
            }

            @Override
            public void e(String s) {
                Log.d("mylog", "Error: " + s);

            }
        });

    }
}
