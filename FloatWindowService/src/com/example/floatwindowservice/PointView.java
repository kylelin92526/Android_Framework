package com.example.floatwindowservice;

import java.io.IOException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

public class PointView extends ImageView{
    private final String TAG="PointView";
    private Context mContext;
    private float mTouchX;
    private float mTouchY;
    private float x;
    private float y;
    private float mTouchDownX;
    private float mTouchDownY;
    private float mTouchUpX;
    private float mTouchUpY;
    private float rx;
    private float ry;
    private FloatWindowManager mFloatWindowManager=null;
    private WindowManager wm= null;
    private WindowManager.LayoutParams wmParams = null;
    private static int status=0;//let action up ,select status move or show some thing
    private final String ACTION_ROOTCMD="run_root_cmd";
    private final String EXTRA_KEY_ROOTCMD="user_cmd";
    
    public PointView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mContext=context;
        mFloatWindowManager=((FloatApplication)mContext.getApplicationContext()).getFloatWindowManager(mContext);
        wm=mFloatWindowManager.getWindowManager();
        wmParams=mFloatWindowManager.getLayoutParams();
    }

    @SuppressLint("ClickableViewAccessibility")
    @SuppressWarnings("static-access")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getRawX();
        y = event.getRawY()-(mFloatWindowManager.getStatusBarHeight());   //status bar high
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            LogsD(TAG,"touch down");
            status=0;
            mTouchX = event.getX();
            mTouchY = event.getY();
            updateViewPosition();
            break;
        case MotionEvent.ACTION_MOVE:
            LogsD(TAG,"touch move");
            status=1;
            updateViewPosition();
            break;
        case MotionEvent.ACTION_UP:
            LogsD(TAG,"touch up");
            status=2;
            updateViewPosition();
            rx=calculationTouchMove(mTouchDownX, mTouchUpX);
            ry=calculationTouchMove(mTouchDownY, mTouchUpY);
            if(rx<1 || ry<1){
                if( mFloatWindowManager.getViewMode()==mFloatWindowManager.VIEW_ALL |
                    mFloatWindowManager.getViewMode()==mFloatWindowManager.VIEW_MENU_NO_VOLUME){ 
                    mFloatWindowManager.createMenuView();
                    LogsD(TAG,"creat menu view");
            }else{
                try {
                    mFloatWindowManager.sendKeyEvent(mFloatWindowManager.KEY_BACK);
                    LogsD(TAG,"send back");
                }catch (IOException e){
                    // TODO Auto-generated catch block
                    LogsD(TAG,"send back error");
                }
            }
        }
        mTouchX=mTouchY=0;
        break;
        }
        return true;
    }

    private float calculationTouchMove(float a,float b){
        if(a>b)
            return a-b;
        else
            return b-a;
    }

    private void updateViewPosition(){
        //update view move
        if(status==0){
        //Record the location of the touch down
            mTouchDownX=x-mTouchX;
            mTouchDownY=y-mTouchY;
        }else{
            if(status==2){
                //Record the location of the touch up
                mTouchUpX=x-mTouchX;
                mTouchUpY=y-mTouchY;
                //Record the location of the point , when menu view change back use
                mFloatWindowManager.setPointViewLocation((int)(x-mTouchX),(int)(y-mTouchY));
            }
            wmParams.x=(int)(x-mTouchX);
            wmParams.y=(int)(y-mTouchY);
            wm.updateViewLayout(this, wmParams);
        }
    }

    private void LogsD(String name , String meg){
        mFloatWindowManager.LogsD(name,meg);
    }

}
