package com.example.stepappv8;


import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
