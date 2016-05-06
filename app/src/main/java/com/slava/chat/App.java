package com.slava.chat;


import android.app.Application;
import android.content.Context;

public class App extends Application {

    public static volatile Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
    }
}
