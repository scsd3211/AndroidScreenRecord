package screenrecordstore;

import android.app.Service;
import android.content.Intent;
import android.media.MediaCodecInfo;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.util.Log;

public class ScreenRecordService extends Service{

	private static final String TAG = "ScreenRecordService";
    private static final int REQUEST_MEDIA_PROJECTION = 1;
    private static final int REQUEST_PERMISSIONS = 2;
    // members below will be initialized in onCreate()
    private MediaProjectionManager mMediaProjectionManager;
    private MediaCodecInfo[] mAvcCodecInfos; // avc codecs
    private MediaCodecInfo[] mAacCodecInfos; // aac codecs
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	

    @Override
    public void onCreate()
    {
        Log.i(TAG, "ScreenRecordService===>>onCreate");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        Log.i(TAG, "ScreenRecordService===>>onStart");

        super.onStart(intent, startId);
        
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        
        
        startService(captureIntent);
       // startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION);
        
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(TAG, "ScreenRecordService===>>onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        Log.i(TAG, "ScreenRecordService===>>onDestroy");
        super.onDestroy();
    }
	

}
