package com.example.czsss;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import screenrecordstore.screenmain;

public class firstactivity extends Activity{
	
	 private static final String TAG = "chenzhuo ";
	 
	 TextView ceshi;
	 TextView ceshi2;
	 AnimationTextView hahaText;
	 
	 private Handler mHandler=new Handler(){
		 @Override
		 public void handleMessage(Message msg) {
				 // TODO Auto-generated method stub
			 
					 super.handleMessage(msg);
					 
					 Log.i(TAG, "new Handler()  handleMessage(Message msg) ");
					 if(msg.what==0x123456){
						 ceshi.setText("ceshi222897987" + msg.arg1);
						 
						 Log.i(TAG, "new Handler()  if(msg.what==0x123456)");
					 	}
					 else if(msg.what==0x123)
					 {
						 ceshi2.setText("ceshi1111823423423423987" + msg.arg1 );
						 Log.i(TAG, "new Handler()   else if(msg.what==0x123)");
					 }
				 
				 }

		 };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.firstmain);

		
		ceshi = (TextView) findViewById(R.id.textView1);
		
		
		ceshi2 = (TextView) findViewById(R.id.textView1);
		
		
		hahaText = (AnimationTextView)findViewById(R.id.testText);
		
		

		findViewById(R.id.viewbutton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(firstactivity.this, MainActivity.class);
          
                startActivity(intent);
                
                Log.i(TAG, "startActivity(intent) intent.setClass(firstactivity.this, MainActivity.class)");
			}
		});
		
		
		findViewById(R.id.buttonStartQuickly).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
                Intent intent = new Intent();
                intent.setClass(firstactivity.this, screenmain.class);
          
                String Start = "Start";
                intent.putExtra("setOrstart",Start);
                startActivity(intent);
                
                Log.i(TAG, " buttonStartQuicklystartActivity(intent) intent.setClass(firstactivity.this, screenmain.class)");
				
				

			}
		});
		
		
		findViewById(R.id.storebutton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
                Intent intent = new Intent();
                intent.setClass(firstactivity.this, screenmain.class);
          
                startActivity(intent);
                
                Log.i(TAG, "startActivity(intent) intent.setClass(firstactivity.this, screenmain.class)");
				
			}
		});
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		ceshi.setText("hahahah1");
		
		
		/*ceshi2.setText("ceshi222ssssss");*/
		
		Runnable ok = new Runnable() {
			int ok2 = 2;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				while(true)
				{
					 ok2++;
					Log.i(TAG, "Runnable ok = new Runnable()" + ok2);
					Message msg=Message.obtain();
					msg.what=0x123456;
					msg.arg1 = ok2;
					mHandler.sendMessage(msg);
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		};
		
		//new Thread(ok).start(); 
		//ok.run();
		

        //开启一个子线程
        new Thread(new Runnable() {
            @Override
				            public void run() {
            						
            					int ok2 = 2;
								while(true)
								{
									ok2++;
									Log.i(TAG, "new Thread(new Runnable() " + ok2);
					                //新建一个Message对象，存储需要发送的消息
					                Message message=new Message();
					                message.what=0x123456;
					                message.arg1 = ok2;
					                //然后将消息发送出去
					                mHandler.sendMessage(message);
								       try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								       
								                
								}
				            }
				        }
        		).start();


		
		Runnable no = new Runnable() {
			int ok2 = 2;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				while(true)
				{
					ok2++;
					Log.i(TAG, "Runnable no = new Runnable()" + ok2);
					Message msg=Message.obtain();
					msg.what=0x123;
					msg.arg1 = ok2;
					mHandler.sendMessage(msg);
					try {
						Thread.sleep(110);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		};
		
        
        
      //  new Thread(no).start(); 
		
	}
	
	
	
}
