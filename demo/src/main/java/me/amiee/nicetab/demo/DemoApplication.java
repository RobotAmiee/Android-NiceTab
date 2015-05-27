package me.amiee.nicetab.demo;


import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
