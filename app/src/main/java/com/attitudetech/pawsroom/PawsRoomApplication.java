package com.attitudetech.pawsroom;

import android.app.Application;
import android.content.Context;

/**
 * Created by phrc on 7/20/17.
 */

public class PawsRoomApplication extends Application {

    public static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
    }
}
