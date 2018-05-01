package com.tonychen.tonyrecorder;

import android.app.Application;
import android.util.Log;

/**
 * Created by TonyChen on 2018/04/29;
 * Email : chenchenyanrong@163.com
 * Blog : http://blog.csdn.net/weixin_37484990
 * Description :
 */

public class RecorderApplication extends Application {
    public static final String TAG = RecorderApplication.class.getSimpleName();

    public static RecorderApplication getmInstance() {
        return mInstance;
    }

    private static RecorderApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    @Override
    public void onLowMemory() {
        Log.d(TAG, "onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        Log.d(TAG, "onTrimMemory level = "+level);
        super.onTrimMemory(level);
    }

}
