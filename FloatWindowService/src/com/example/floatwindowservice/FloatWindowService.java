package com.example.floatwindowservice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class FloatWindowService extends Service {
    private final String TAG = "FloatWindowService";
    private final String ACTION_OPEN_Float="open_flaot";//action for open point view
    private final String ACTION_CLOSE_Float="close_flaot";//action for close point view
    private final String ACTION_CHANGE_VIEW_MODE="change_view_mode";//action for change menu view ui
    private final String EXTRA_KEY_CHANGE_VIEW_MODE="key_change_view_mode";//bring key for menu view select ui
    private final String ACTION_STATUS_CHANGE="float.status.change";//send to AuxiliarySettings update ui
    private final String ACTION_AUXSETTINGS="aux.settings";//control AuxiliarySettings add or remove action
    private final String EXTRA_KEY_AUX_ADD="key.aux.add";//bring key for AuxiliarySettings if 0 remove , 1 add
    private FloatWindowManager mFloatWindowManager=null;
    private final int USER_EVENT = 0x01;
    private static boolean isShowView=false;//check point view show or not
    private ClientHandler mClientHandler=null;
    private class ClientHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case USER_EVENT: {
                Intent intent = (Intent) msg.obj;
                String action = intent.getAction();
                if(action.equals(ACTION_OPEN_Float)){
                    if(isShowView==false){
                        LogsD(TAG,"open float");
                        mFloatWindowManager.setSwitch(mFloatWindowManager.SWITCH_ON);
                        isShowView=true;
                        sendStatusChangeAction();
                    }
                }else if(action.equals(ACTION_CLOSE_Float)){
                    if(isShowView){
                        LogsD(TAG,"close float");
                        mFloatWindowManager.setSwitch(mFloatWindowManager.SWITCH_OFF);
                        isShowView=false;
                        sendStatusChangeAction();
                    }
                }else if(action.equals(ACTION_CHANGE_VIEW_MODE)){
                    LogsD(TAG,"set view mode");
                    //action set view mode
                    try{
                        int mode = intent.getIntExtra(EXTRA_KEY_CHANGE_VIEW_MODE,0);
                        mFloatWindowManager.setViewMode(mode);
                        sendStatusChangeAction();
                    }catch(Exception e){
                        LogsD(TAG,"set view mode error");
                    }
                }else{
                    LogsD(TAG,"set aux settings on off");
                    try{
                        //if 0 off , 1 on
                        int mode = intent.getIntExtra(EXTRA_KEY_AUX_ADD,0);
                        mFloatWindowManager.setAuxSettingsOn(mode);
                    }catch(Exception e){
                        LogsD(TAG,"set aux settings on off error");
                    }
                }
                break;
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        if(mClientHandler==null)
            mClientHandler = new ClientHandler();
        mFloatWindowManager=((FloatApplication)getApplicationContext()).getFloatWindowManager(FloatWindowService.this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_OPEN_Float);
        filter.addAction(ACTION_CLOSE_Float);
        filter.addAction(ACTION_CHANGE_VIEW_MODE);
        filter.addAction(ACTION_AUXSETTINGS);
        registerReceiver(mActionReceiver, filter);
        //on boot check point view on or off , and make sure prop has value
        try{
           int f_aux_setting=mFloatWindowManager.getAuxSettings();
           int f_switch=mFloatWindowManager.getSwitch();
           int f_mode=mFloatWindowManager.getViewMode();
           if(f_switch==1){
               mFloatWindowManager.setAuxSettingsOn(f_aux_setting);
               mFloatWindowManager.setSwitch(f_switch);
               mFloatWindowManager.setViewMode(f_mode);
               isShowView=true;
           }
        }catch(Exception e){
            LogsD(TAG,"error check switch and view ");
        }
        LogsD(TAG,"onCreate finish");
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        LogsD(TAG,"onDestroy");
        if(mClientHandler!=null)
            mClientHandler=null;
        if(mFloatWindowManager!=null)
            mFloatWindowManager=null;
        unregisterReceiver(mActionReceiver);
    }

    private void sendStatusChangeAction(){
        Intent intent = new Intent(ACTION_STATUS_CHANGE);
        sendBroadcast(intent);
    }

    BroadcastReceiver mActionReceiver= new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if( action.equals(ACTION_OPEN_Float)||
                action.equals(ACTION_CLOSE_Float)||
                action.equals(ACTION_CHANGE_VIEW_MODE)||
                action.equals(ACTION_AUXSETTINGS)){
                mClientHandler.sendMessage(mClientHandler.obtainMessage(USER_EVENT, intent));
            }
        }
    };

    private void LogsD(String name , String meg){
        mFloatWindowManager.LogsD(name,meg);
    }

}
