package com.slava.chat;


import android.app.Application;
import android.content.Context;

import com.parse.Parse;

public class App extends Application {

    public static volatile Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "78vxcrQI4qOuwsNDMOWNovUqGOaGNREHGGMSChUL", "jXJXeTKSURpgqijsqkfAhgGQkDJbwxMNgEFusFwE");
        applicationContext = getApplicationContext();
    }
}
