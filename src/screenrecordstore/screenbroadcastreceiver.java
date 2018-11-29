package screenrecordstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.util.Log;
import android.widget.Toast;

public class screenbroadcastreceiver extends BroadcastReceiver{
	private String TAG = "screenbroadcastreceiver ";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Toast mToast = Toast.makeText(context, "recivesomething", Toast.LENGTH_SHORT);
		mToast.show();
		
		Intent mIntent = new Intent(context,screenmain.class);
        String Start = "Start";
        mIntent.putExtra("setOrstart",Start);
        
        mIntent.putExtra("setOrstartfrombroadcast",Start);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        context.startActivity(mIntent);
		Log.d(TAG, "  context.startActivity(mIntent); ");
	}

}
