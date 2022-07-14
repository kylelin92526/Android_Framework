package com.example.floatwindowservice;



import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class MenuView extends FrameLayout {
	private final String TAG="MenuView";
    private Context mContext;
    public static int viewWidth;
    public static int viewHeight;
    private ImageButton btn_back,btn_home,btn_recent,btn_volume_sub,btn_volume_add;
    private FloatWindowManager mFloatWindowManager=null;
    private AudioManager mAudioManager=null;
    View popupWindowView=null;

    public MenuView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mContext=context;
        mFloatWindowManager=((FloatApplication)mContext.getApplicationContext()).getFloatWindowManager(mContext);
        mAudioManager=mFloatWindowManager.getAudioManager();
        //if have volume button view or not
        if(mFloatWindowManager.getViewMode()==mFloatWindowManager.VIEW_ALL){
            LayoutInflater.from(context).inflate(R.layout.float_menu, this);
        }else if(mFloatWindowManager.getViewMode()==mFloatWindowManager.VIEW_MENU_NO_VOLUME){
            LayoutInflater.from(context).inflate(R.layout.float_menu_no_volume, this);
        }
        View view = findViewById(R.id.menu_layout);  
        popupWindowView=findViewById(R.id.popup_window);
        viewWidth = view.getLayoutParams().width;  
        viewHeight = view.getLayoutParams().height;
        btn_back=(ImageButton)findViewById(R.id.imageButtonback);
        btn_home=(ImageButton)findViewById(R.id.imageButtonhome);
        btn_recent=(ImageButton)findViewById(R.id.imageButtonrecent);
        //if have volume button view
        if(mFloatWindowManager.getViewMode()==mFloatWindowManager.VIEW_ALL){
            btn_volume_sub=(ImageButton)findViewById(R.id.imageButtonvolumesub);
            btn_volume_add=(ImageButton)findViewById(R.id.imageButtonvolumeadd);
        }

        btn_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { 
                // TODO Auto-generated method stub
                LogsD(TAG,"press back");
                mFloatWindowManager.isKeyEvent=true;
                mFloatWindowManager.isKeyBack=true;
                mFloatWindowManager.creatPointView();
            }
        });

        btn_home.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LogsD(TAG,"presss home");
                mFloatWindowManager.isKeyEvent=true;
                mFloatWindowManager.isKeyHome=true;
                mFloatWindowManager.creatPointView();
            }
        });

        btn_recent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LogsD(TAG,"press recent");
                mFloatWindowManager.isKeyEvent=true;
                mFloatWindowManager.isKeyRecent=true;
                mFloatWindowManager.creatPointView();
            }
        });
        //if have volume button view
        if(mFloatWindowManager.getViewMode()==mFloatWindowManager.VIEW_ALL){
            btn_volume_sub.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View v) {
                   // TODO Auto-generated method stub
                   LogsD(TAG,"press volume sub");
                   mAudioManager.adjustVolume(AudioManager.ADJUST_LOWER, 0);
                   } 
            });

            btn_volume_add.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    LogsD(TAG,"press volume add");
                    mAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, 0);
                }
            });
        }
    }

    //if touch menu view outside , go back point view 
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            int x = (int) event.getX();
            int y = (int) event.getY();
            Rect rect = new Rect();
            popupWindowView.getGlobalVisibleRect(rect);
            if (!rect.contains(x, y)) {
                LogsD(TAG,"press menu view outside , close menu view and creat point view");
                mFloatWindowManager.creatPointView();
            }
            break;
        }
        return false;
    }

    private void LogsD(String name , String meg){
        mFloatWindowManager.LogsD(name,meg);
    }

}
