package com.joiplay.joiplay.rpgm;

import android.app.Application;
import android.content.Context;

public class RPGMPlugin extends Application {

    private RPGMPlugin instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public Context getContext(){
        return instance.getApplicationContext();
    }
}
