package screenrecordstore;

import android.R.integer;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaCodecInfo;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Range;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;



import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.czsss.R;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION_CODES.M;






public class screenmain extends Activity {
    private static final int REQUEST_MEDIA_PROJECTION = 1;
    private static final int REQUEST_PERMISSIONS = 2;
    // members below will be initialized in onCreate()
    private MediaProjectionManager mMediaProjectionManager;
    private Button mButton;
    private ToggleButton mAudioToggle;
    private NamedSpinner mVieoResolution;
    private NamedSpinner mVideoFramerate;
    private NamedSpinner mIFrameInterval;
    private NamedSpinner mVideoBitrate;
    private NamedSpinner mAudioBitrate;
    private NamedSpinner mAudioSampleRate;
    private NamedSpinner mAudioChannelCount;
    private NamedSpinner mVideoCodec;
    private NamedSpinner mAudioCodec;
    private NamedSpinner mVideoProfileLevel;
    private NamedSpinner mAudioProfile;
    private NamedSpinner mOrientation;
    private MediaCodecInfo[] mAvcCodecInfos; // avc codecs
    private MediaCodecInfo[] mAacCodecInfos; // aac codecs
   
    private ScreenRecorder mRecorder;
    private static final String TAG = "chenzhuo  screenmain";
    
    private BroadcastReceiver mStopActionReceiver = new BroadcastReceiver() {
    	
    	
        @Override
        public void onReceive(Context context, Intent intent) {
            File file = new File(mRecorder.getSavedPath());
            if (ACTION_STOP.equals(intent.getAction())) {
                stopRecorder();
            }
            Toast.makeText(context, "Recorder stopped!\n Saved file " + file, Toast.LENGTH_LONG).show();
            StrictMode.VmPolicy vmPolicy = StrictMode.getVmPolicy();
            try {
                // disable detecting FileUriExposure on public file
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
                viewResult(file);
            } finally {
                StrictMode.setVmPolicy(vmPolicy);
            }
        }

        private void viewResult(File file) {
            Intent view = new Intent(Intent.ACTION_VIEW);
            view.addCategory(Intent.CATEGORY_DEFAULT);
            view.setDataAndType(Uri.fromFile(file), ScreenRecorder.VIDEO_AVC);
            view.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(view);
            } catch (ActivityNotFoundException e) {
                // no activity can open this video
            }
        }
    };

    @Override

    
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screenlayout);
        mMediaProjectionManager = (MediaProjectionManager)getApplicationContext().getSystemService(MEDIA_PROJECTION_SERVICE);
        
        Log.d(TAG,"onCreate 1");
        bindViews();
        
        mAudioToggle.setChecked(
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .getBoolean(getResources().getResourceEntryName(mAudioToggle.getId()), true));
        
        Log.d(TAG,"onCreate 12");
        Utils.Callback  mCallback = new  Utils.Callback() {
			
			@Override
			public void onResult(MediaCodecInfo[] infos) {
				// TODO Auto-generated method stub
				logCodecInfos(infos, ScreenRecorder.VIDEO_AVC);
				mAvcCodecInfos = infos;
				SpinnerAdapter codecsAdapter = createCodecsAdapter(mAvcCodecInfos);
				 mVideoCodec.setAdapter(codecsAdapter);
		         restoreSelections(mVideoCodec, mVieoResolution, mVideoFramerate, mIFrameInterval, mVideoBitrate);
		         
		         Log.d(TAG,"VIDEO Code Format");
		         
			}
		};
		
		Utils.findEncodersByTypeAsync(ScreenRecorder.VIDEO_AVC, mCallback);
		
		 Log.d(TAG,"onCreate 2");
        Utils.Callback  mVoiceCallback = new  Utils.Callback() {
			
			@Override
			public void onResult(MediaCodecInfo[] infos) {
				// TODO Auto-generated method stub
	            logCodecInfos(infos, ScreenRecorder.AUDIO_AAC);
	            mAacCodecInfos = infos;
	            SpinnerAdapter codecsAdapter = createCodecsAdapter(mAacCodecInfos);
	            mAudioCodec.setAdapter(codecsAdapter);
	            restoreSelections(mAudioCodec, mAudioChannelCount);
		         
		         Log.d(TAG,"Voice Code Format");
		         
			}
		};
		
        Utils.findEncodersByTypeAsync(ScreenRecorder.AUDIO_AAC,mVoiceCallback);
		
        Log.d(TAG,"onCreate 3");

        
        new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
		};
        Log.d(TAG,"onCreate 4");
   


    }
    
    
    private void restoreSelectionFromPreferences(SharedPreferences preferences, NamedSpinner spinner) {
        int resId = spinner.getId();
        String key = getResources().getResourceEntryName(resId);
        int value = preferences.getInt(key, -1);
        if (value >= 0 && spinner.getAdapter() != null) {
            spinner.setSelectedPosition(value);
        }
    }
    
    private void restoreSelections(NamedSpinner... spinners) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        for (NamedSpinner spinner : spinners) {
            restoreSelectionFromPreferences(preferences, spinner);
        }
    }
    
    private SpinnerAdapter createCodecsAdapter(MediaCodecInfo[] codecInfos) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, codecInfoNames(codecInfos));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
    private static String[] codecInfoNames(MediaCodecInfo[] codecInfos) {
        String[] names = new String[codecInfos.length];
        for (int i = 0; i < codecInfos.length; i++) {
            names[i] = codecInfos[i].getName();
        }
        return names;
    }
    /**
     * Print information of all MediaCodec on this device.
     */
    private static void logCodecInfos(MediaCodecInfo[] codecInfos, String mimeType) {
        for (MediaCodecInfo info : codecInfos) {
            StringBuilder builder = new StringBuilder(512);
            MediaCodecInfo.CodecCapabilities caps = info.getCapabilitiesForType(mimeType);
            builder.append("Encoder '").append(info.getName()).append('\'')
                    .append("\n  supported : ")
                    .append(Arrays.toString(info.getSupportedTypes()));
            MediaCodecInfo.VideoCapabilities videoCaps = caps.getVideoCapabilities();
            if (videoCaps != null) {
                builder.append("\n  Video capabilities:")
                        .append("\n  Widths: ").append(videoCaps.getSupportedWidths())
                        .append("\n  Heights: ").append(videoCaps.getSupportedHeights())
                        .append("\n  Frame Rates: ").append(videoCaps.getSupportedFrameRates())
                        .append("\n  Bitrate: ").append(videoCaps.getBitrateRange());
                if (ScreenRecorder.VIDEO_AVC.equals(mimeType)) {
                    MediaCodecInfo.CodecProfileLevel[] levels = caps.profileLevels;

                    builder.append("\n  Profile-levels: ");
                    for (MediaCodecInfo.CodecProfileLevel level : levels) {
                        builder.append("\n  ").append(Utils.avcProfileLevelToString(level));
                    }
                }
                builder.append("\n  Color-formats: ");
                for (int c : caps.colorFormats) {
                    builder.append("\n  ").append(Utils.toHumanReadable(c));
                }
            }
            MediaCodecInfo.AudioCapabilities audioCaps = caps.getAudioCapabilities();
            if (audioCaps != null) {
                builder.append("\n Audio capabilities:")
                        .append("\n Sample Rates: ").append(Arrays.toString(audioCaps.getSupportedSampleRates()))
                        .append("\n Bit Rates: ").append(audioCaps.getBitrateRange())
                        .append("\n Max channels: ").append(audioCaps.getMaxInputChannelCount());
            }
            Log.i(TAG, builder.toString());
        }
    }
    
    private void bindViews() {
    	
    	Log.d(TAG,"bindViews");
        mButton = (Button) findViewById(R.id.record_button);
        mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			      Log.i(TAG, "R.id.record_butto");
				// TODO Auto-generated method stub
		        if (mRecorder != null) {
		            stopRecorder();
		        } else if (hasPermissions()) {
		            startCaptureIntent();
		        } else if (Build.VERSION.SDK_INT >= M) {
		            requestPermissions();
		        } else {
		            toast("No permission to write sd card");
		        }
				
			}
		});

        mVideoCodec = (NamedSpinner) findViewById(R.id.video_codec);
        mVieoResolution = (NamedSpinner) findViewById(R.id.resolution);
        mVideoFramerate = (NamedSpinner) findViewById(R.id.framerate);
        mIFrameInterval = (NamedSpinner) findViewById(R.id.iframe_interval);
        mVideoBitrate = (NamedSpinner) findViewById(R.id.video_bitrate);
        mOrientation = (NamedSpinner) findViewById(R.id.orientation);

        mAudioCodec = (NamedSpinner) findViewById(R.id.audio_codec);
        mVideoProfileLevel = (NamedSpinner) findViewById(R.id.avc_profile);
        mAudioBitrate = (NamedSpinner) findViewById(R.id.audio_bitrate);
        mAudioSampleRate = (NamedSpinner) findViewById(R.id.sample_rate);
        mAudioProfile = (NamedSpinner) findViewById(R.id.aac_profile);
        mAudioChannelCount = (NamedSpinner) findViewById(R.id.audio_channel_count);

        mAudioToggle = (ToggleButton) findViewById(R.id.with_audio);
        
        
        
//        mAudioToggle.setOnCheckedChangeListener((buttonView, isChecked) ->
//                findViewById(R.id.audio_format_chooser)
//                        .setVisibility(isChecked ? View.VISIBLE : View.GONE)
//        );

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mOrientation.setSelectedPosition(1);
        }

        NamedSpinner.OnItemSelectedListener mListenerVideoCodecSelected = new NamedSpinner.OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(NamedSpinner view, int position) {
				// TODO Auto-generated method stub
				onVideoCodecSelected((String) view.getSelectedItem());
				
				
			}
		};
        
		mVideoCodec.setOnItemSelectedListener(mListenerVideoCodecSelected);
		
		
		//视频编码  选项
		
        NamedSpinner.OnItemSelectedListener mListenerAudioCodec = new NamedSpinner.OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(NamedSpinner view, int position) {
				// TODO Auto-generated method stub
				
				 onAudioCodecSelected((String) view.getSelectedItem());
				 
			}
		};
		
		mAudioCodec.setOnItemSelectedListener(mListenerAudioCodec);
		
		//音频编码 编码选项
		
		
		
//        mVideoCodec.setOnItemSelectedListener((view, position) -> onVideoCodecSelected(view.getSelectedItem()));
//        mAudioCodec.setOnItemSelectedListener((view, position) -> onAudioCodecSelected(view.getSelectedItem()));
        
        NamedSpinner.OnItemSelectedListener mListenerVideoFramerate = new NamedSpinner.OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(NamedSpinner view, int position) {
				// TODO Auto-generated method stub
				onFramerateChanged(position,(String) view.getSelectedItem());
				
			}
		};
		
		
		
		
		
		
        NamedSpinner.OnItemSelectedListener mListenerVieoResolution = new NamedSpinner.OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(NamedSpinner view, int position) {
				// TODO Auto-generated method stub
				onResolutionChanged(position,(String) view.getSelectedItem());
				
			}
		};
        		
        mVieoResolution.setOnItemSelectedListener(mListenerVieoResolution);	
        		
        mVideoFramerate.setOnItemSelectedListener(mListenerVideoFramerate);
        
        	//分辨率和 帧数
        
        NamedSpinner.OnItemSelectedListener mListenerVideoBitrate = new NamedSpinner.OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(NamedSpinner view, int position) {
				// TODO Auto-generated method stub
				 onBitrateChanged(position, (String) view.getSelectedItem());
				
			}
		};
        
        mVideoBitrate.setOnItemSelectedListener(mListenerVideoBitrate);
        
        //比特率设置
        
        NamedSpinner.OnItemSelectedListener mListenerOrientation = new NamedSpinner.OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(NamedSpinner view, int position) {
				// TODO Auto-generated method stub
				onOrientationChanged(position, (String) view.getSelectedItem());
				
			}
		};
        
        
        mOrientation.setOnItemSelectedListener(mListenerOrientation);
        
        
        
    }
    
    private void onAudioCodecSelected(String codecName) {
        MediaCodecInfo codec = getAudioCodecInfo(codecName);
        if (codec == null) {
            mAudioProfile.setAdapter(null);
            mAudioSampleRate.setAdapter(null);
            mAudioBitrate.setAdapter(null);
            return;
        }
        MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType(ScreenRecorder.AUDIO_AAC);

        resetAudioBitrateAdapter(capabilities);
        resetSampleRateAdapter(capabilities);
        resetAacProfileAdapter(capabilities);
        restoreSelections(mAudioBitrate, mAudioSampleRate, mAudioProfile);
    }
    
    
    private void resetAacProfileAdapter(MediaCodecInfo.CodecCapabilities capabilities) {
        String[] profiles = Utils.aacProfiles();
        SpinnerAdapter old = mAudioProfile.getAdapter();
        if (old == null || !(old instanceof ArrayAdapter)) {
            ArrayAdapter<Object> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapter.addAll(profiles);
            mAudioProfile.setAdapter(adapter);
        } else {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) old;
            adapter.setNotifyOnChange(false);
            adapter.clear();
            adapter.addAll(profiles);
            adapter.notifyDataSetChanged();
        }

    }
    
    private void resetAudioBitrateAdapter(MediaCodecInfo.CodecCapabilities capabilities) {
        Range<Integer> bitrateRange = capabilities.getAudioCapabilities().getBitrateRange();
        int lower = Math.max(bitrateRange.getLower() / 1000, 80);
        int upper = bitrateRange.getUpper() / 1000;
        List<Integer> rates = new ArrayList<>();
        for (int rate = lower; rate < upper; rate += lower) {
            rates.add(rate);
        }
        rates.add(upper);

        SpinnerAdapter old = mAudioBitrate.getAdapter();
        if (old == null || !(old instanceof ArrayAdapter)) {
            ArrayAdapter<Object> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapter.addAll(rates);
            mAudioBitrate.setAdapter(adapter);
        } else {
            ArrayAdapter<Integer> adapter = (ArrayAdapter<Integer>) old;
            adapter.setNotifyOnChange(false);
            adapter.clear();
            adapter.addAll(rates);
            adapter.notifyDataSetChanged();
        }
        mAudioSampleRate.setSelectedPosition(rates.size() / 2);
    }
       
    private void resetSampleRateAdapter(MediaCodecInfo.CodecCapabilities capabilities) {
        int[] sampleRates = capabilities.getAudioCapabilities().getSupportedSampleRates();
        List<Integer> rates = new ArrayList<>(sampleRates.length);
        int preferred = -1;
        for (int i = 0; i < sampleRates.length; i++) {
            int sampleRate = sampleRates[i];
            if (sampleRate == 44100) {
                preferred = i;
            }
            rates.add(sampleRate);
        }

        SpinnerAdapter old = mAudioSampleRate.getAdapter();
        if (old == null || !(old instanceof ArrayAdapter)) {
            ArrayAdapter<Object> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapter.addAll(rates);
            mAudioSampleRate.setAdapter(adapter);
        } else {
            ArrayAdapter<Integer> adapter = (ArrayAdapter<Integer>) old;
            adapter.setNotifyOnChange(false);
            adapter.clear();
            adapter.addAll(rates);
            adapter.notifyDataSetChanged();
        }
        mAudioSampleRate.setSelectedPosition(preferred);
    }

    
    private MediaCodecInfo getAudioCodecInfo(String codecName) {
        if (codecName == null) return null;
        if (mAacCodecInfos == null) {
            mAacCodecInfos = Utils.findEncodersByType(ScreenRecorder.AUDIO_AAC);
        }
        MediaCodecInfo codec = null;
        for (int i = 0; i < mAacCodecInfos.length; i++) {
            MediaCodecInfo info = mAacCodecInfos[i];
            if (info.getName().equals(codecName)) {
                codec = info;
                break;
            }
        }
        if (codec == null) return null;
        return codec;
    }
    
    
    private void onVideoCodecSelected(String codecName) {
        MediaCodecInfo codec = getVideoCodecInfo(codecName);
        if (codec == null) {
            mVideoProfileLevel.setAdapter(null);
            return;
        }
        MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType(ScreenRecorder.VIDEO_AVC);

        resetAvcProfileLevelAdapter(capabilities);
    }
    
    private void resetAvcProfileLevelAdapter(MediaCodecInfo.CodecCapabilities capabilities) {
        MediaCodecInfo.CodecProfileLevel[] profiles = capabilities.profileLevels;
        if (profiles == null || profiles.length == 0) {
            mVideoProfileLevel.setEnabled(false);
            return;
        }
        mVideoProfileLevel.setEnabled(true);
        String[] profileLevels = new String[profiles.length + 1];
        profileLevels[0] = "Default";
        for (int i = 0; i < profiles.length; i++) {
            profileLevels[i + 1] = Utils.avcProfileLevelToString(profiles[i]);
        }

        SpinnerAdapter old = mVideoProfileLevel.getAdapter();
        if (old == null || !(old instanceof ArrayAdapter)) {
            ArrayAdapter<Object> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapter.addAll(profileLevels);
            mVideoProfileLevel.setAdapter(adapter);
        } else {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) old;
            adapter.setNotifyOnChange(false);
            adapter.clear();
            adapter.addAll(profileLevels);
            adapter.notifyDataSetChanged();
        }
    }
    
    private void onResolutionChanged(int selectedPosition, String resolution) {
        String codecName = getSelectedVideoCodec();
        MediaCodecInfo codec = getVideoCodecInfo(codecName);
        if (codec == null) return;
        MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType(ScreenRecorder.VIDEO_AVC);
        MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();
        String[] xes = resolution.split("x");
        Log.d(TAG,"resolution" + resolution + "xes" + xes);
        
        if (xes.length != 2) throw new IllegalArgumentException();
        boolean isLandscape = isLandscape();
        int width = Integer.parseInt(xes[isLandscape ? 0 : 1]);
        int height = Integer.parseInt(xes[isLandscape ? 1 : 0]);

        double selectedFramerate = getSelectedFramerate();
        int resetPos = Math.max(selectedPosition - 1, 0);
        if (!videoCapabilities.isSizeSupported(width, height)) {
            mVieoResolution.setSelectedPosition(resetPos);
            toast("codec '%s' unsupported size %dx%d (%s)",
                    codecName, width, height, mOrientation.getSelectedItem());
            Log.w("@@", codecName +
                    " height range: " + videoCapabilities.getSupportedHeights() +
                    "\n width range: " + videoCapabilities.getSupportedHeights());
        } else if (!videoCapabilities.areSizeAndRateSupported(width, height, selectedFramerate)) {
            mVieoResolution.setSelectedPosition(resetPos);
            toast("codec '%s' unsupported size %dx%d(%s)\nwith framerate %d",
                    codecName, width, height, mOrientation.getSelectedItem(), (int) selectedFramerate);
        }
    }

    private void onBitrateChanged(int selectedPosition, String bitrate) {
        String codecName = getSelectedVideoCodec();
        MediaCodecInfo codec = getVideoCodecInfo(codecName);
        if (codec == null) return;
        MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType(ScreenRecorder.VIDEO_AVC);
        MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();
        int selectedBitrate = Integer.parseInt(bitrate) * 1000;

        int resetPos = Math.max(selectedPosition - 1, 0);
        if (!videoCapabilities.getBitrateRange().contains(selectedBitrate)) {
            mVideoBitrate.setSelectedPosition(resetPos);
            toast("codec '%s' unsupported bitrate %d", codecName, selectedBitrate);
            Log.w("@@", codecName +
                    " bitrate range: " + videoCapabilities.getBitrateRange());
        }
    }
    
    
    private void onFramerateChanged(int selectedPosition, String rate) {
        String codecName = getSelectedVideoCodec();
        MediaCodecInfo codec = getVideoCodecInfo(codecName);
        if (codec == null) return;
        MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType(ScreenRecorder.VIDEO_AVC);
        MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();
        int[] selectedWithHeight = getSelectedWithHeight();
        int selectedFramerate = Integer.parseInt(rate);
        boolean isLandscape = isLandscape();
        int width = selectedWithHeight[isLandscape ? 0 : 1];
        int height = selectedWithHeight[isLandscape ? 1 : 0];

        int resetPos = Math.max(selectedPosition - 1, 0);
        if (!videoCapabilities.getSupportedFrameRates().contains(selectedFramerate)) {
            mVideoFramerate.setSelectedPosition(resetPos);
            toast("codec '%s' unsupported framerate %d", codecName, selectedFramerate);
        } else if (!videoCapabilities.areSizeAndRateSupported(width, height, selectedFramerate)) {
            mVideoFramerate.setSelectedPosition(resetPos);
            toast("codec '%s' unsupported size %dx%d\nwith framerate %d",
                    codecName, width, height, selectedFramerate);
        }
    }

    

    private void onOrientationChanged(int selectedPosition, String orientation) {
        String codecName = getSelectedVideoCodec();
        MediaCodecInfo codec = getVideoCodecInfo(codecName);
        if (codec == null) return;
        MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType(ScreenRecorder.VIDEO_AVC);
        MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();
        int[] selectedWithHeight = getSelectedWithHeight();
        boolean isLandscape = selectedPosition == 1;
        int width = selectedWithHeight[isLandscape ? 0 : 1];
        int height = selectedWithHeight[isLandscape ? 1 : 0];
        int resetPos = Math.max(mVieoResolution.getSelectedItemPosition() - 1, 0);
        if (!videoCapabilities.isSizeSupported(width, height)) {
            mVieoResolution.setSelectedPosition(resetPos);
            toast("codec '%s' unsupported size %dx%d (%s)",
                    codecName, width, height, orientation);
            return;
        }

        int current = getResources().getConfiguration().orientation;
        if (isLandscape && current == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (!isLandscape && current == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    
    
    
    
    
    private MediaCodecInfo getVideoCodecInfo(String codecName) {
        if (codecName == null) return null;
        if (mAvcCodecInfos == null) {
            mAvcCodecInfos = Utils.findEncodersByType(ScreenRecorder.VIDEO_AVC);
        }
        MediaCodecInfo codec = null;
        for (int i = 0; i < mAvcCodecInfos.length; i++) {
            MediaCodecInfo info = mAvcCodecInfos[i];
            if (info.getName().equals(codecName)) {
                codec = info;
                break;
            }
        }
        if (codec == null) return null;
        return codec;
    }

    
    private void startCaptureIntent() {
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION);
    }
    @TargetApi(M)
    private void requestPermissions() {
        final String[] permissions = mAudioToggle.isChecked()
                ? new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}
                : new String[]{WRITE_EXTERNAL_STORAGE};
        boolean showRationale = false;
        for (String perm : permissions) {
            showRationale |= shouldShowRequestPermissionRationale(perm);
        }
        if (!showRationale) {
            requestPermissions(permissions, REQUEST_PERMISSIONS);
            return;
        }
        
        DialogInterface.OnClickListener  mOnClickListener = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				requestPermissions(permissions, REQUEST_PERMISSIONS);
			}
		};
        new AlertDialog.Builder(this)
                .setMessage("Using your mic to record audio and your sd card to save video file")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, mOnClickListener)
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mOrientation.setSelectedPosition(1);
        } else {
            mOrientation.setSelectedPosition(0);
        }
        // reset padding
        int horizontal = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
        int vertical = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
        findViewById(R.id.container).setPadding(horizontal, vertical, horizontal, vertical);
    }

    private void toast(String message, Object... args) {
        final Toast toast = Toast.makeText(this,
                (args.length == 0) ? message : String.format(Locale.US, message, args),
                Toast.LENGTH_SHORT);
        if (Looper.myLooper() != Looper.getMainLooper()) {
            runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					toast.show();
				}
			});
        } else {
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            // NOTE: Should pass this result data into a Service to run ScreenRecorder.
            // The following codes are merely exemplary.

            MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            if (mediaProjection == null) {
                Log.e("@@", "media projection is null");
                return;
            }

            VideoEncodeConfig video = createVideoConfig();
            AudioEncodeConfig audio = createAudioConfig(); // audio can be null
            if (video == null) {
                toast("Create ScreenRecorder failure");
                mediaProjection.stop();
                return;
            }

            File dir = getSavingDir();
            if (!dir.exists() && !dir.mkdirs()) {
                cancelRecorder();
                return;
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);
            final File file = new File(dir, "Screen-" + format.format(new Date())
                    + "-" + video.width + "x" + video.height + ".mp4");
            Log.d("@@", "Create recorder with :" + video + " \n " + audio + "\n " + file);
            mRecorder = newRecorder(mediaProjection, video, audio, file);
            if (hasPermissions()) {
                startRecorder();
            } else {
                cancelRecorder();
            }
        }
        

        

        
    }
    /*__________________________________________________________________________________________________*/
    private static File getSavingDir() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                "ScreenCaptures");
    }
    
    
    private VideoEncodeConfig createVideoConfig() {
        final String codec = getSelectedVideoCodec();
        if (codec == null) {
            // no selected codec ??
            return null;
        }
        // video size
        int[] selectedWithHeight = getSelectedWithHeight();
        boolean isLandscape = isLandscape();
        int width = selectedWithHeight[isLandscape ? 0 : 1];
        int height = selectedWithHeight[isLandscape ? 1 : 0];
        int framerate = getSelectedFramerate();
        int iframe = getSelectedIFrameInterval();
        int bitrate = getSelectedVideoBitrate();
        MediaCodecInfo.CodecProfileLevel profileLevel = getSelectedProfileLevel();
        return new VideoEncodeConfig(width, height, bitrate,
                framerate, iframe, codec, ScreenRecorder.VIDEO_AVC, profileLevel);
    }
    private String getSelectedVideoCodec() {
        return  mVideoCodec.getSelectedItem();
    }
    private int[] getSelectedWithHeight() {
        if (mVieoResolution == null) throw new IllegalStateException();
        String selected = mVieoResolution.getSelectedItem();
        String[] xes = selected.split("x");
        if (xes.length != 2) throw new IllegalArgumentException();
        return new int[]{Integer.parseInt(xes[0]), Integer.parseInt(xes[1])};

    }
    
    private MediaCodecInfo.CodecProfileLevel getSelectedProfileLevel() {
        return mVideoProfileLevel != null ? Utils.toProfileLevel((String) mVideoProfileLevel.getSelectedItem()) : null;
    }

    
    private boolean isLandscape() {
        return mOrientation != null && mOrientation.getSelectedItemPosition() == 1;
    }
    
    private int getSelectedFramerate() {
        if (mVideoFramerate == null) throw new IllegalStateException();
        return Integer.parseInt( (String) mVideoFramerate.getSelectedItem());
    }
    
    private int getSelectedIFrameInterval() {
        return (mIFrameInterval != null) ? Integer.parseInt((String) mIFrameInterval.getSelectedItem()) : 5;
    }
    
    private int getSelectedVideoBitrate() {
        if (mVideoBitrate == null) throw new IllegalStateException();
        String selectedItem = mVideoBitrate.getSelectedItem(); //kbps
        return Integer.parseInt(selectedItem) * 1000;
    }
    
    private AudioEncodeConfig createAudioConfig() {
        if (!mAudioToggle.isChecked()) return null;
        String codec = getSelectedAudioCodec();
        if (codec == null) {
            return null;
        }
        int bitrate = getSelectedAudioBitrate();
        int samplerate = getSelectedAudioSampleRate();
        int channelCount = getSelectedAudioChannelCount();
        int profile = getSelectedAudioProfile();

        return new AudioEncodeConfig(codec, ScreenRecorder.AUDIO_AAC, bitrate, samplerate, channelCount, profile);
    }
    private String getSelectedAudioCodec() {
    	
    	Object ok = mAudioCodec.getSelectedItem();
    	
        return  mAudioCodec.getSelectedItem();
        
        ////////////////////////////////////
    }
    
    private int getSelectedAudioBitrate() {
        if (mAudioBitrate == null) throw new IllegalStateException();
        Integer selectedItem = mAudioBitrate.getSelectedItem();
        return selectedItem * 1000; // bps
    }
    
    private int getSelectedAudioSampleRate() {
        if (mAudioSampleRate == null) throw new IllegalStateException();
        Integer selectedItem = mAudioSampleRate.getSelectedItem();
        return selectedItem;
    }
    private int getSelectedAudioProfile() {
        if (mAudioProfile == null) throw new IllegalStateException();
        String selectedItem = mAudioProfile.getSelectedItem();
        MediaCodecInfo.CodecProfileLevel profileLevel = Utils.toProfileLevel(selectedItem);
        return profileLevel == null ? MediaCodecInfo.CodecProfileLevel.AACObjectMain : profileLevel.profile;
    }
    private int getSelectedAudioChannelCount() {
        if (mAudioChannelCount == null) throw new IllegalStateException();
        String selectedItem = mAudioChannelCount.getSelectedItem().toString();
        return Integer.parseInt(selectedItem);
    }
    
    private void cancelRecorder() {
        if (mRecorder == null) return;
        Toast.makeText(this, "Permission denied! Screen recorder is cancel", Toast.LENGTH_SHORT).show();
        stopRecorder();
    }
    static final String ACTION_STOP = "Hytera.action.STOP";
    private void startRecorder() {
        if (mRecorder == null) return;
        mRecorder.start();
        mButton.setText("Stop Recorder");
        registerReceiver(mStopActionReceiver, new IntentFilter(ACTION_STOP));
        moveTaskToBack(true);
    }
    
    private void stopRecorder() {
        
        if (mRecorder != null) {
            mRecorder.quit();
        }
        mRecorder = null;
        mButton.setText("Restart recorder");
        try {
            unregisterReceiver(mStopActionReceiver);
        } catch (Exception e) {
            //ignored
        }
    }
    
    private boolean hasPermissions() {
        PackageManager pm = getPackageManager();
        String packageName = getPackageName();
        int granted = (mAudioToggle.isChecked() ? pm.checkPermission(RECORD_AUDIO, packageName) : PackageManager.PERMISSION_GRANTED)
                | pm.checkPermission(WRITE_EXTERNAL_STORAGE, packageName);
        return granted == PackageManager.PERMISSION_GRANTED;
    }
    
    private ScreenRecorder newRecorder(MediaProjection mediaProjection, VideoEncodeConfig video,
            AudioEncodeConfig audio, final File output) {
    	
    	 ScreenRecorder r = new ScreenRecorder(video, audio,
                 1, mediaProjection, output.getAbsolutePath());
         r.setCallback(new ScreenRecorder.Callback() {
             long startTime = 0;

             @Override
             public void onStop(Throwable error) {
            	 runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						stopRecorder();
					}
				});
            	 
                 
                 if (error != null) {
                     toast("Recorder error ! See logcat for more details");
                     error.printStackTrace();
                     output.delete();
                 } else {
                     Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                             .addCategory(Intent.CATEGORY_DEFAULT)
                             .setData(Uri.fromFile(output));
                     sendBroadcast(intent);
                 }
             }

             @Override
             public void onStart() {
//                 mNotifications.recording(0);
             }

             @Override
             public void onRecording(long presentationTimeUs) {
                 if (startTime <= 0) {
                     startTime = presentationTimeUs;
                 }
                 long time = (presentationTimeUs - startTime) / 1000;
//                 mNotifications.recording(time);
             }
         });
         return r;
     }
}

