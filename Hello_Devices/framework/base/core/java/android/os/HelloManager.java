/*
 * 
 * Author:
 *
 * Kyle Lin
 */

package android.os;

import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.os.Handler;
import android.os.Message;
import android.os.ServiceManager;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IHelloService;


public class HelloManager
{
    private static final String TAG = "HelloManager";
    private IHelloService mHelloService;
    private final Context mContext;

    public HelloManager(Context context, IHelloService service) {
    	mContext = context;
        mHelloService = service;
        if (mHelloService != null) {
            Log.d(TAG, "The HelloManager object is ready.");
        }
    }

    public void setVal(int val){
        try{
            mHelloService.setVal(val);
        }catch(RemoteException e){
        }
    }

    public int getVal(){
        try{
            return mHelloService.getVal();
        }catch(RemoteException e){
        }
		return -1;
    }


}
