package com.example.ecommerce;

import android.app.Application;

public class App extends Application {

    public static AppModule appModule;

    @Override
    public void onCreate() {
        super.onCreate();
        appModule = new AppModule(this);
    }

}
