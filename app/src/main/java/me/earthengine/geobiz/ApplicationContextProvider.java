package me.earthengine.geobiz;

import android.app.Application;
import android.content.Context;

/**
 * Created by earth_000 on 2015/6/11.
 */
public class ApplicationContextProvider extends Application {

    private static Context context;

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
