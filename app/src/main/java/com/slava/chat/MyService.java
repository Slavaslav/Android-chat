package com.slava.chat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.parse.ParseObject;

import java.io.Serializable;
import java.util.List;

public class MyService extends Service {

    public static final String DIALOGS_LIST = "Dialogs List";
    public static final String DIALOGS_LIST_UPDATED = "Take Dialogs List";

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
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Run load dialogs list
        if (intent.getStringExtra("message") != null) {
            switch (intent.getStringExtra("message")) {
                case "loadUserDialogs":
                    loadUsersDialogs();
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void loadUsersDialogs() {
        Account.loadUserDialogs(new Account.CallbackLoadDialogs() {

            @Override
            public void success(List<ParseObject> list) {
                // Send broadcast
                LocalBroadcastManager.getInstance(MyService.this).sendBroadcast(new Intent(DIALOGS_LIST_UPDATED).putExtra(DIALOGS_LIST, (Serializable) list));
            }

            @Override
            public void e(String s) {
                Log.d("mylog", "Error: " + s);

            }
        });

    }
}
