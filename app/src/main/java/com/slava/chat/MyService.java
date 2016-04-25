package com.slava.chat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class MyService extends Service {

    public static final String INTENT_MESSAGE = "Intent Message";
    public static final String DIALOGS_LIST = "Dialogs List";
    public static final String DIALOGS_LIST_UPDATED = "Download Dialogs List";
    private static final String MESSAGES_LIST = "Messages List";
    private static final String MESSAGES_LIST_UPDATED = "Download Messages List";

    public MyService() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Run load dialogs list
        if (intent.getStringExtra(INTENT_MESSAGE) != null) {
            switch (intent.getStringExtra(INTENT_MESSAGE)) {
                case DIALOGS_LIST_UPDATED:
                    loadUsersDialogs();
                    break;
                case MESSAGES_LIST_UPDATED:
                    loadMessageList();
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void loadUsersDialogs() {
        Account.loadUserDialogs(new Account.CallbackLoad() {

            @Override
            public void success(List<ParseObject> list) {
                ArrayList<ParseObject> dList = new ArrayList<>(list);
                // Send broadcast
                LocalBroadcastManager.getInstance(MyService.this).sendBroadcast(new Intent(DIALOGS_LIST_UPDATED).putExtra(DIALOGS_LIST, dList));
            }

            @Override
            public void e(String s) {
                Log.d("LOG", "Error: " + s);

            }
        });
    }

    private void loadMessageList() {
        Account.loadMessageList("add here dialog id", new Account.CallbackLoad() {

            @Override
            public void success(List<ParseObject> list) {
                ArrayList<ParseObject> dList = new ArrayList<>(list);
                // Send broadcast
                LocalBroadcastManager.getInstance(MyService.this).sendBroadcast(new Intent(MESSAGES_LIST_UPDATED).putExtra(MESSAGES_LIST, dList));
            }

            @Override
            public void e(String s) {
                Log.d("LOG", "Error: " + s);

            }
        });
    }

}
