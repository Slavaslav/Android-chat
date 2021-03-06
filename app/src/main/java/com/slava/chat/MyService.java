package com.slava.chat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends Service {

    public static final String DIALOGS_LIST = "Dialogs List";
    public static final String UPDATE_DIALOGS_LIST = "Download Dialogs List";
    private static final String INTENT_MESSAGE = "Intent Message";
    private static final String MESSAGES_LIST = "Messages List";
    private static final String UPDATE_MESSAGES_LIST = "Download Messages List";

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Run load dialogs list
        if (intent != null) {
            if (intent.getStringExtra(INTENT_MESSAGE) != null) {
                switch (intent.getStringExtra(INTENT_MESSAGE)) {
                    case UPDATE_DIALOGS_LIST:
                        loadUsersDialogs();
                        break;
                    case UPDATE_MESSAGES_LIST:
                        loadMessageList();
                        break;
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void loadUsersDialogs() {
        /*Account.loadUserDialogs(new Account.CallbackLoadObject() {

            @Override
            public void success(List<ParseObject> list) {
                ArrayList<ParseObject> dList = new ArrayList<>(list);
                // Send broadcast
                LocalBroadcastManager.getInstance(MyService.this).sendBroadcast(new Intent(UPDATE_DIALOGS_LIST).putExtra(DIALOGS_LIST, dList));
            }

            @Override
            public void e(String s) {
                Log.d("LOG", "Error: " + s);

            }
        });*/
    }

    private void loadMessageList() {
        /*Account.loadMessageList("add here dialog id", new Account.CallbackLoadObject() {

            @Override
            public void success(List<ParseObject> list) {
                ArrayList<ParseObject> dList = new ArrayList<>(list);
                // Send broadcast
                LocalBroadcastManager.getInstance(MyService.this).sendBroadcast(new Intent(UPDATE_MESSAGES_LIST).putExtra(MESSAGES_LIST, dList));
            }

            @Override
            public void e(String s) {
                Log.d("LOG", "Error: " + s);

            }
        });*/
    }

}
