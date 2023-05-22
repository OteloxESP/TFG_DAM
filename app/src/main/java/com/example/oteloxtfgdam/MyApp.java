package com.example.oteloxtfgdam;

import android.app.Application;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

public class MyApp extends Application {
    private static App appInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        appInstance = new App(new AppConfiguration.Builder("bdoinfo-wwrmh")
                .build());
    }

    public static App getAppInstance() {
        return appInstance;
    }
}

