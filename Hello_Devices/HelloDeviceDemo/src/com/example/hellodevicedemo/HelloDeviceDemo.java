package com.example.hellodevicedemo;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.os.HelloManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class HelloDeviceDemo extends Activity implements OnClickListener{
	private HelloManager mHelloManager;
	private final String HELLO_SERVICE = "helloservice";
	private EditText edit;
	private Button btn_set,btn_get;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_device_demo);
        mHelloManager=(HelloManager)getSystemService(HELLO_SERVICE);
        
        edit=(EditText)findViewById(R.id.editText1);
        btn_set=(Button)findViewById(R.id.buttonset);
        btn_get=(Button)findViewById(R.id.buttonget);
        btn_set.setOnClickListener(this);
        btn_get.setOnClickListener(this);
    }
	@Override
	public void onClick(View v) { 
		// TODO Auto-generated method stub
		if(v.getId()==R.id.buttonset){
			try{
				int val = Integer.parseInt(edit.getText().toString().trim());
				mHelloManager.setVal(val);
			}catch(Exception e){
				
			}
		}
		
		if(v.getId()==R.id.buttonget){ 
			Log.d("kyle","value="+mHelloManager.getVal());
			try{
				Toast.makeText(this,""+mHelloManager.getVal(),Toast.LENGTH_SHORT).show();
			}catch(Exception e){
				
			}
		}
		
	}

   
}
