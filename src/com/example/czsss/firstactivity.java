package com.example.czsss;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import screenrecordstore.screenmain;

public class firstactivity extends Activity{
	
	 private static final String TAG = "chenzhuo ";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.firstmain);

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
}
