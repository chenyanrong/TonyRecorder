package com.tonychen.tonyrecorder.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioRecord;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class RecorderService extends Service {
    public static final String TAG = RecorderService.class.getSimpleName();

    private TonyRecorder mRecorderMananger;

    public RecorderService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate ------");
        mRecorderMananger = new TonyRecorder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand ------");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy ------");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mRecorderMananger;
    }


    private static class TonyRecorder extends Binder implements IRecorder {
        public static final String TAG = TonyRecorder.class.getSimpleName();

        private AudioRecord mAudioRecord;

        public TonyRecorder() {
            Log.d(TAG, "new instance ------");
            initRecorder();
        }

        private void initRecorder() {

        }
    }

    /**
     * 初始化录音机
     */
    public void initAudio(){
//        recBufSize = AudioRecord.getMinBufferSize(FREQUENCY,
//                CHANNELCONGIFIGURATION, AUDIOENCODING);// 录音组件
//        audioRecord = new AudioRecord(AUDIO_SOURCE,// 指定音频来源，这里为麦克风
//                FREQUENCY, // 16000HZ采样频率
//                CHANNELCONGIFIGURATION,// 录制通道
//                AUDIO_SOURCE,// 录制编码格式
//                recBufSize);// 录制缓冲区大小 //先修改
    }
}
