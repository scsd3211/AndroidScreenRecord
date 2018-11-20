package com.example.czsss;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.TextView;


public class AnimationTextView extends TextView {
	private static int POINT_NUMBER = 3;	//
	private static int ANIMATION_SPEED = 300;//
	private String POINT_STRING = "...";//
	private int stringResId = 0;
	private MyRunnable mRunnable = new MyRunnable(this);

	public AnimationTextView(Context arg0, AttributeSet arg1, int arg2, int arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}
	
	public AnimationTextView(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}
	
	public AnimationTextView(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	
	public AnimationTextView(Context arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	
	public void setAnimationText(String text){
		if(text.contains(POINT_STRING)){
			if(mRunnable.getLatestContent().equals(text))
				return;
			mRunnable.setCanPlayAnimation(true);
			mRunnable.resetAnimation();//
			mRunnable.startAnimation(text,true);
		}else{
			mRunnable.setCanPlayAnimation(false);
			super.setText(text);
		}
		mRunnable.setLatestContent(text);
	}
	
	public void setPointString(String point_string){
		POINT_NUMBER = point_string.length();
		POINT_STRING = point_string;
	}
	
	public void setAnimationSpeed(int ms){
		ANIMATION_SPEED = ms;
	}
	
	public void setStringResId(int stringResId){
		this.stringResId = stringResId;
	}
	
	public int getStringResId(){
		return stringResId;
	}
	

	
	private static class MyRunnable implements Runnable{
		private String mContent;
		private WeakReference<TextView> mWeakReference;
		private String tempText = "";		//鐢ㄤ簬淇濆瓨琚鐞嗗悗瑕佹樉绀虹殑鏂囧瓧
		private int tempNumber = POINT_NUMBER;//POINT_NUMBER-tempNumber鍗充负瑕佹挱鏀惧姩鐢荤殑瀛楃涓茬殑褰撳墠鐐圭殑鏁伴噺
		private boolean canPlayAnimation = false;//鐢ㄤ簬鎺у埗鏄惁鎾斁鍔ㄧ敾
		private String latestContent = "";
		private Handler handler = new Handler(Looper.getMainLooper());
		
		public MyRunnable(TextView textView){
			mWeakReference = new WeakReference<TextView>(textView);
		}
		
		public void setContent(String content){
			mContent = content;
		}
		
		public void setCanPlayAnimation(boolean canPlay){
			canPlayAnimation = canPlay;
		}
		
		public void resetAnimation(){
			this.tempNumber = POINT_NUMBER;
		}
		
		public void setLatestContent(String content){
			latestContent = content;
		}
		
		public String getLatestContent(){
			return latestContent;
		}
		
		public void startAnimation(final String text,boolean isFirst){
			if(canPlayAnimation){
				setContent(text);
				if(isFirst){
					handler.post(this);
				}else{
					handler.postDelayed(this, ANIMATION_SPEED);
				}
			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(canPlayAnimation){
				tempNumber--;
				tempText = mContent.substring(0, mContent.length() - tempNumber);
				for(int i=0;i<tempNumber;i++){
					tempText = tempText + '\u0020';//鐢ㄤ簬淇濊瘉灞呬腑甯冨眬鏃剁敱浜庣偣鐨勬暟閲忓湪鍙樺寲锛岃�岀偣宸﹁竟鐨勬枃瀛椾綅缃笉浼氬彉鍖�
				}
				if(mWeakReference!=null){
					TextView tempTextView = mWeakReference.get();
					if(tempTextView!=null){
						if(mContent.equals(latestContent)){
							tempTextView.setText(tempText);
						}
					}
				}
				
				if(mContent.equals(latestContent)){
					startAnimation(mContent,false);
				}
				
				if (tempNumber == 0) {
					tempNumber = POINT_NUMBER;
				}
			}
		}
		
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
//		return super.toString();
		StringBuilder builder = new StringBuilder();
		builder.append("stringResId=").append(stringResId);
		return builder.toString();
	}
	
	

}
