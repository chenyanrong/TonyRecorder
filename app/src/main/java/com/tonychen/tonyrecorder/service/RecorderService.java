package com.tonychen.tonyrecorder.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioRecord;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.tonychen.tonyrecorder.recorder.TonyRecorder;

import static android.media.AudioFormat.ENCODING_PCM_16BIT;
import static android.media.AudioFormat.ENCODING_PCM_8BIT;

public class RecorderService extends Service {
    public static final String TAG = RecorderService.class.getSimpleName();
    /**
     * 参数启动录音机的action
     */
    public static final String ACTION_STARTBYARGUMENT = "startrecord_byargument";

    public static final String AUDIOSOURC = "audioSourc";
    public static final String SAMPLERATEINHZ = "sampleRateInHz";
    public static final String CHANNELCONFIG = "channelConfig";
    public static final String AUDIOFORMAT = "audioFormat";
    public static final String BUFFERSIZEINBYTES = "bufferSizeInBytes";
    public static final String SAMPLINGINTERVALTIME = "samplingintervaltime";
    public static final String SAVEALLRECORDERBUFF = "isSaveAllRecorderBuff";

    private AudioRecord mAudioRecord;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate ------");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand ------");
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            if (action.equals(ACTION_STARTBYARGUMENT)) {
                int audioSource = intent.getIntExtra(AUDIOSOURC, -1);
                int sampleRateinhz = intent.getIntExtra(SAMPLERATEINHZ, -1);
                int channelConfig = intent.getIntExtra(CHANNELCONFIG, -1);
                int audioFormat = intent.getIntExtra(AUDIOFORMAT, -1);
                int bufferSizeInbytes = intent.getIntExtra(BUFFERSIZEINBYTES, -1);
                int samplingIntervalTime = intent.getIntExtra(SAMPLINGINTERVALTIME, -1);
                boolean saveAllBuff = intent.getBooleanExtra(SAVEALLRECORDERBUFF, false);
                int readBuffSize = -1;
                if (audioSource == -1 || sampleRateinhz == -1 ||
                        channelConfig == -1 || audioFormat == -1) {
                    Log.d(TAG, "必要参数缺失,将使用默认参数启动录音机");
                    mAudioRecord = TonyRecorder.getInstance().createRecorder();
                } else {
                    if (bufferSizeInbytes == -1) {
                        bufferSizeInbytes = AudioRecord.getMinBufferSize(sampleRateinhz, channelConfig, audioFormat);
                    }
                    if (samplingIntervalTime < 0) {
                        throw new IllegalArgumentException("时间间隔必须为正值");
                    } else {
                        if (audioFormat == ENCODING_PCM_16BIT) {
                            readBuffSize = channelConfig * sampleRateinhz * 2 * samplingIntervalTime / 1000 / 8;
                            bufferSizeInbytes = readBuffSize;
                        } else if (audioFormat == ENCODING_PCM_8BIT) {
                            readBuffSize = channelConfig * sampleRateinhz * samplingIntervalTime / 1000 / 8;
                            bufferSizeInbytes = readBuffSize;
                        }
                    }
                    mAudioRecord = TonyRecorder.getInstance().createRecorder(audioSource,
                            sampleRateinhz, channelConfig, audioFormat,
                            bufferSizeInbytes, readBuffSize, saveAllBuff);
                }
//                Log.d(TAG, "录音机的参数如下:\n" + TonyRecorder.getInstance().toString());
            } else {
                Log.e(TAG, "未知的启动action = " + action);
            }
        } else {
            Log.e(TAG, "不是通过action启动的情况");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy ------");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
