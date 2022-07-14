package com.example.floatwindowservice;

import java.io.IOException;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.os.SystemProperties;
import android.util.Log;

public class FloatWindowManager {
	private boolean isLog=true;
	private final String TAG = "FloatWindowManager";
    private static Context mContext;
    //check menu view back home recent keyevent , in point view do action
    public boolean isKeyEvent=false;
    public boolean isKeyBack=false;
    public boolean isKeyHome=false;
    public boolean isKeyRecent=false;
    
    public PointView mPointView=null;
    public MenuView mMenuView=null;
    private WindowManager wm=null;
    private WindowManager.LayoutParams wmParams=null;
    //for record point view position
    private int PointView_X=0;
    private int PointView_y=0;
    
    private AudioManager mAudioManager=null; 
    public final int KEY_BACK=KeyEvent.KEYCODE_BACK;
    public final int KEY_HOME=KeyEvent.KEYCODE_HOME;
    public final int KEY_RECENT=KeyEvent.KEYCODE_APP_SWITCH;
    //three mode for select 
    public final String AUXILIARYTOOLS_SWITCH = "persist.sys.aux_switch";
    public final String AUXILIARYTOOLS_MODE = "persist.sys.aux_mode";
    public final int AUX_SETTINGS_ON=1;
    public final int AUX_SETTINGS_OFF=0;
    public int defaultAuxSettings=AUX_SETTINGS_ON;//default auxiliarysettings on
    private static final String AUXILIARY_ADD="persist.sys.aux_on";//onboot check settings auxiliary tool 0 remove 1 add
    public final int VIEW_ONLY_POINT=0;
    public final int VIEW_MENU_NO_VOLUME=1;
    public final int VIEW_ALL=2;
    public int defaultView=VIEW_ALL;//default menu view set all button
    public final int SWITCH_OFF=0;
    public final int SWITCH_ON=1;
    public int defaultSwitch=SWITCH_OFF;//default switch set off , not show point view
    //point view width and height
    private int sizePointViewWidth=60;
    private int sizePointViewHeight=60;

    public FloatWindowManager(Context context){
        mContext=context;
        getWindowManager();
        getLayoutParams();
        getAudioManager();
    }

    public WindowManager getWindowManager(){
        if(wm==null)
            wm=(WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        return wm;
    }

    public WindowManager.LayoutParams getLayoutParams(){
        if(wmParams==null)
            wmParams=((FloatApplication)mContext.getApplicationContext()).getWmParams();
        return wmParams;
    }

    public AudioManager getAudioManager(){
        if(mAudioManager==null)
            mAudioManager=(AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        return mAudioManager;
    }

    //creat point view
    public void creatPointView(){
        LogsD(TAG,"creatPointView");
        removeMenuView();
        /*check menu view keyevent , we sendkeyevent in here 
          because menu view is full screen view , if event
          send in menu view ,have some problem , so close
          menu view , and back in creat point view check 
          keyevent and send it
        */
        if(isKeyEvent){
            isKeyEvent=false;
            if(isKeyBack){
                sendEvent(KEY_BACK);
                isKeyBack=false;
            }else if(isKeyHome){
                sendEvent(KEY_HOME);
                isKeyHome=false;
            }else{
                sendEvent(KEY_RECENT);
                isKeyRecent=false;
            }
        }
        if(mPointView == null){
            mPointView=new PointView(mContext);
            if(wmParams == null)
                wmParams = ((FloatApplication)mContext.getApplicationContext()).getWmParams();
            mPointView.setImageResource(R.drawable.point);
            wmParams.type=LayoutParams.TYPE_PHONE;
            wmParams.format=PixelFormat.RGBA_8888;
            wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
            wmParams.gravity=Gravity.LEFT|Gravity.TOP;
            wmParams.x=PointView_X;
            wmParams.y=PointView_y;
            wmParams.width=sizePointViewWidth;
            wmParams.height=sizePointViewHeight;
            wm.addView(mPointView, wmParams);
        }
    }
    //creat menu view , display the middle of the screen
    public void createMenuView() {
        LogsD(TAG,"createMenuView");
        removePointView();
        int screenWidth = wm.getDefaultDisplay().getWidth();  
        int screenHeight = wm.getDefaultDisplay().getHeight();  
        if (mMenuView == null) {  
            mMenuView= new MenuView(mContext);  
            if(wmParams == null)
                wmParams = ((FloatApplication)mContext.getApplicationContext()).getWmParams();
            wmParams.flags=LayoutParams.FLAG_ALT_FOCUSABLE_IM;
            wmParams.x = screenWidth / 2 - mMenuView.viewWidth / 2;  
            wmParams.y = screenHeight / 2 - mMenuView.viewHeight / 2;  
            wmParams.type = LayoutParams.TYPE_PHONE;  
            wmParams.format = PixelFormat.RGBA_8888;  
            wmParams.gravity = Gravity.LEFT | Gravity.TOP;  
            wmParams.width = mMenuView.viewWidth;  
            wmParams.height = mMenuView.viewHeight;  
            wm.addView(mMenuView, wmParams);  
        }  
    }

    public void removePointView() {  
        if (mPointView != null) {  
            wm.removeView(mPointView);  
            mPointView = null;  
        }
    }

    public void removeMenuView(){
        if (mMenuView != null) {  
            wm.removeView(mMenuView);  
            mMenuView = null;  
        }
    }

    //record point view position
    public void setPointViewLocation(int x , int y){
        PointView_X=x;
        PointView_y=y;
    }

    //get status bar high , because must sub this value
    public static int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();  
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = mContext.getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        }catch (Exception e){
            e.printStackTrace();
        }
        return statusBarHeight;  
    }

    //this funtion must system permission android.permission.INJECT_EVENTS
    public void sendKeyEvent(int command) throws IOException{
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("input keyevent " + command);
    }

    private void sendEvent(int keyevent){
        LogsD(TAG,"send ket event number = "+keyevent);
        try {
            sendKeyEvent(keyevent);
        }catch(IOException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*select mode 
      VIEW_ONLY_POINT=0;
      VIEW_MENU_NO_VOLUME=1;
      VIEW_ALL=2;
    */
    public void setViewMode(int mode){
        LogsD(TAG,"set view mode ");
        if(getSwitch()==SWITCH_ON){
            creatPointView();
        }
        defaultView=mode;
        setProp(AUXILIARYTOOLS_MODE,defaultView);
    }

    public int getViewMode(){
        try{
            return getProp(AUXILIARYTOOLS_MODE);
        }catch(Exception e){
            LogsD(TAG,"get view mode error");
            return defaultView;
        }
    }
    
    public void setSwitch(int value){
        LogsD(TAG,"set switch");
        defaultSwitch=value;
        if(defaultSwitch==SWITCH_OFF){
            removeMenuView();
            removePointView();
        }else{
            creatPointView();
        }
        setProp(AUXILIARYTOOLS_SWITCH,defaultSwitch);
    }

    public int getSwitch(){
        try{
            return getProp(AUXILIARYTOOLS_SWITCH);
        }catch(Exception e){
            LogsD(TAG,"get switch error");
            return defaultSwitch;
        }
    }

    public void setAuxSettingsOn(int value){
        defaultAuxSettings=value;
        setProp(AUXILIARY_ADD,defaultAuxSettings);
    }

    public int getAuxSettings(){
        try{
            return getProp(AUXILIARY_ADD);
        }catch(Exception e){
            LogsD(TAG,"get switch error");
            return defaultAuxSettings;
        }
    }

    public void setProp(String prop , int value){
        try{
            String s=String.valueOf(value);
            SystemProperties.set(prop,s);
        }catch(Exception e){
            LogsD(TAG,"setprop error");
        }
    }
    
    public int getProp(String prop){
        try{
            int i=Integer.parseInt(SystemProperties.get(prop));
            return i;
        }catch(Exception e){
            LogsD(TAG,"getprop error , creat  "+prop+"  and set default value");
            if(prop.equals(AUXILIARYTOOLS_SWITCH)){
                setProp(AUXILIARYTOOLS_SWITCH,defaultSwitch);
                return defaultSwitch;
            }else if(prop.equals(AUXILIARYTOOLS_MODE)){
                setProp(AUXILIARYTOOLS_MODE,defaultView);
                return defaultView;
            }else{
                setProp(AUXILIARY_ADD,defaultAuxSettings);
                return defaultAuxSettings;
            }
        }
    }

    public void LogsD(String name , String meg){
        if(isLog)
            Log.d("Float_debug--"+name,meg);
    }

}