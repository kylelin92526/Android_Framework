package com.example.floatwindowservice;

import android.app.Application;
import android.content.Context;
import android.view.WindowManager;

public class FloatApplication extends Application{
    private FloatWindowManager mFloatWindowManager=null;
    private WindowManager.LayoutParams wmParams=new WindowManager.LayoutParams();
    public WindowManager.LayoutParams getWmParams(){
        return wmParams;
    }

    public FloatWindowManager getFloatWindowManager(Context context){
        if(mFloatWindowManager==null){
            mFloatWindowManager=new FloatWindowManager(context);
        }
        return mFloatWindowManager;
    }

    private void LogsD(String name , String meg){
        mFloatWindowManager.LogsD(name,meg);
    }
}
