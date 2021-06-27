package edu.hcmuaf.tourrecommendationapp.util;

import android.app.Application;

public class App extends Application {
    private static App mSelf;

    public static App self() {
        return mSelf;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSelf = this;
    }
}
