package com.tonychen.tonyrecorder.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.tonychen.tonyrecorder.RecorderApplication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.media.AudioRecord.STATE_INITIALIZED;
import static android.os.Environment.DIRECTORY_MUSIC;

/**
 * Created by TonyChen on 2018/04/30;
 * Email : chenchenyanrong@163.com
 * Blog : http://blog.csdn.net/weixin_37484990
 * Description :
 */

public class TonyRecorder implements ITonyRecorder {
    public static final String TAG = TonyRecorder.class.getSimpleName();

    /**
     * 录音机实例
     */
    private AudioRecord mAudioRecord;

    /**
     * 取录音缓冲区的容器
     */
    private byte[] readBuff;

    private SaveRecordBufTask mSaveRecordBufTask;

    /**
     * 读取缓冲区数据的时间间隔
     */
    private int mSamplingIntervalTime;

    public void setSamplingIntervalTime(int samplingIntervalTime) {
        mSamplingIntervalTime = samplingIntervalTime;
    }

    /**
     * 录音来源
     */
    private int mAudioSource;
    /**
     * 频率
     */
    private int mFrequency;
    /**
     * 声道
     */
    private int mChannelCongifiGuration;
    /**
     * 录制格式
     */
    private int mAudioFormat;

    /**
     * 装载系统录音机数据的容器的长度
     */
    private int mBufferSizeInBytes;

    /**
     * 读取录音缓存的线程
     */
    private HandlerThread mReadRecordBufThread;
    /**
     * 专职处理录音的handler
     */
    private Handler mReadHanlder;

    /**
     * 保存录音缓存的线程
     */
    private HandlerThread mSaveRecordBufThread;
    /**
     * 专职保存录音的handler
     */
    private Handler mSaveRecordBufHanlder;

    /**
     * 是否已经结束了录音
     */
    private boolean isStopRecord;

    /**
     * 读取录音文件的任务
     */
    private Runnable mReadBufTask = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "mReadBufTask----------------");
            int readLength = mAudioRecord.read(readBuff, 0, readBuff.length);
            Log.d(TAG, " mAudioRecord.read readLength=" + readLength + " isStopRecord=" + isStopRecord);
            if (readLength > 0 && !isStopRecord) {
                if (shouldSaveAllRecordBuffer) {
                    synchronized (mSaveRecordBufTask) {
                        mSaveRecordBufTask.upData(readBuff, readLength);
                        mSaveRecordBufHanlder.post(mSaveRecordBufTask);
                    }
                }
                if (mRecorderReadBuffListener != null) {
                    mRecorderReadBuffListener.onRead(readBuff, readLength);
                }
            }
            if (!isStopRecord) {
                mReadHanlder.post(mReadBufTask);
            }
//            Log.d(TAG, "mReadBufTask=============== mSamplingIntervalTime="+mSamplingIntervalTime+"\n");
        }
    };

    public boolean isShouldSaveAllRecordBuffer() {
        return shouldSaveAllRecordBuffer;
    }

    private File mAllRecordBufferFile;

    private final Object mLock = new Object();

    public void setShouldSaveAllRecordBuffer(boolean shouldSaveAllRecordBuffer) {
        synchronized (mLock) {
            this.shouldSaveAllRecordBuffer = shouldSaveAllRecordBuffer;
            if (shouldSaveAllRecordBuffer && mSaveRecordBufHanlder == null) {
                if (mSaveRecordBufHanlder == null) {
                    mSaveRecordBufThread = new HandlerThread("sava_allrecord");
                    mSaveRecordBufThread.start();
                    mSaveRecordBufHanlder = new Handler(mSaveRecordBufThread.getLooper());
                }
            }
        }
    }

    /**
     * 保存所有的音频数据,默认值 true
     */
    private boolean shouldSaveAllRecordBuffer;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd.HHmmSS");

    private static class SaveRecordBufTask implements Runnable {
        private byte[] mBuff;
        private int mLength;
        private BufferedOutputStream mOutputStream;

        public SaveRecordBufTask(BufferedOutputStream outputStream) {
            mOutputStream = outputStream;
        }

        public void upData(byte[] buf, int length) {
            this.mBuff = buf;
            this.mLength = length;
        }

        @Override
        public void run() {
            try {
                mOutputStream.write(mBuff, 0, mLength);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private TonyRecorder() {
        Log.d(TAG, "new instance ------");
        mReadRecordBufThread = new HandlerThread("readRecordBufThread", Process.THREAD_PRIORITY_DEFAULT);
        mReadRecordBufThread.start();
        mReadHanlder = new Handler(mReadRecordBufThread.getLooper());
    }

    /**
     * 创建一个AudioRecorder
     *
     * @param audioSource
     * @param sampleRateInHz
     * @param channelConfig
     * @param audioFormat
     * @param bufferSizeInBytes
     */
    public AudioRecord createRecorder(int audioSource, int sampleRateInHz,
                                      int channelConfig, int audioFormat,
                                      int bufferSizeInBytes, int samplingIntervalTime,
                                      int readBuffSize, boolean isSaveAllRecorderBuff) {
        Log.d(TAG, "createRecorder confing===>>> \n " +
                "audioSource=" + audioSource + "\n" +
                "sampleRateInHz=" + sampleRateInHz + "\n" +
                "channelConfig=" + channelConfig + "\n" +
                "audioFormat=" + audioFormat + "\n" +
                "bufferSizeInBytes=" + bufferSizeInBytes + "\n" +
                "samplingIntervalTime=" + samplingIntervalTime + "\n" +
                "readBuffSize=" + readBuffSize + "\n" +
                "isSaveAllRecorderBuff=" + isSaveAllRecorderBuff
        );
        if (mAudioRecord != null && audioSource == mAudioSource &&
                sampleRateInHz == mFrequency && channelConfig == mChannelCongifiGuration &&
                audioFormat == mAudioFormat && bufferSizeInBytes == mBufferSizeInBytes) {
            Log.d(TAG, "请求新建的录音机参数与当期录音机参数相同,直接返回当前录音机,录音机状态:" +
                    (mAudioRecord.getState() == STATE_INITIALIZED ? "Ready" : "No Ready"));
            return mAudioRecord;
        }
        if (mAudioRecord != null) { // 先释放,再创建
            if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                isStopRecord = true;
                if (mReadHanlder != null) {
                    mReadHanlder.removeCallbacks(mReadBufTask);
                }
                mAudioRecord.stop();
                Log.d(TAG, "停止旧的录音机");
                if (mBops != null) {
                    try {
                        mBops.close();
                        mBops = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            mAudioRecord.release();
            Log.d(TAG, "释放旧的录音机");

        }
        mAudioSource = audioSource;
        mFrequency = sampleRateInHz;
        mChannelCongifiGuration = channelConfig;
        mAudioFormat = audioFormat;
        mBufferSizeInBytes = bufferSizeInBytes;
        int minBufferSize = AudioRecord.getMinBufferSize(mFrequency, mChannelCongifiGuration, mAudioFormat); // 录音机得出的最小缓冲大小
        if (minBufferSize > bufferSizeInBytes) {
            bufferSizeInBytes = minBufferSize;
        }
        if (samplingIntervalTime < 0) {
            mSamplingIntervalTime = 0;
        } else {
            mSamplingIntervalTime = samplingIntervalTime;
        }
        if (readBuffSize == -1 || readBuffSize <= 0) { // 没有赋值,默认与系统的缓冲区一样大小
            readBuff = new byte[bufferSizeInBytes];
        } else {
            if (readBuffSize > minBufferSize) {
                readBuff = new byte[minBufferSize];
            } else {
                readBuff = new byte[readBuffSize];
            }
        }
        Log.d(TAG, "系统填充录音机buff bufferSizeInBytes=" + bufferSizeInBytes + "\n我们每次读取的buff的readBuff.length=" + readBuff.length);
        Log.d(TAG, "新录音机的mSamplingIntervalTime=" + mSamplingIntervalTime);
        mAudioRecord = new AudioRecord(audioSource,
                mFrequency,
                mChannelCongifiGuration,
                audioFormat,
                bufferSizeInBytes);
        Log.d(TAG, "实例化新的AudioRecorder" + this.toString() + "\n录音机状态:" + (mAudioRecord.getState() == STATE_INITIALIZED ? "Ready" : "No Ready"));
        if (isSaveAllRecorderBuff) {
            setShouldSaveAllRecordBuffer(true);
        }
        return mAudioRecord;
    }

    public AudioRecord createRecorder(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, boolean isSaveAllRecorderBuff) {
        return createRecorder(MediaRecorder.AudioSource.MIC, 16000,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT), -1, -1,
                isSaveAllRecorderBuff
        );
    }

    public AudioRecord createRecorder(boolean isSaveAllRecorderBuff) {
        return createRecorder(MediaRecorder.AudioSource.MIC, 16000,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT),
                -1, -1, isSaveAllRecorderBuff
        );
    }

    public AudioRecord createRecorder() {
        return createRecorder(MediaRecorder.AudioSource.MIC, 16000,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT),
                -1, -1, true
        );
    }

    @Override
    public String toString() {
        return "AudioSource=" + mAudioSource + "\n"
                + "sampleRateInHz=" + mFrequency + "\n"
                + "channelConfig=" + mChannelCongifiGuration + "\n"
                + "audioFormat=" + mAudioFormat + "\n"
                + "bufferSizeInBytes=" + mBufferSizeInBytes + "\n"
                + "samplingIntervalTime=" + mSamplingIntervalTime + "\n"
                + "readBuffSize=" + readBuff.length;

    }

    /**
     * 录音机开始录音的音频数据保存的文件
     */
    private File mRecordDataFile;
    private BufferedOutputStream mBops;

    public void setSaveRecordDir(String saveRecordDirStr) {
        mSaveRecordDirStr = saveRecordDirStr;
    }

    private String mSaveRecordDirStr;

    /**
     * 清空所有已保存的文件
     */
    public void clearSaveFile() {
        if (mAudioRecord != null) {
            if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                mAudioRecord.stop();
                isStopRecord = true;
                if (mBops != null) {
                    try {
                        mBops.close();
                        mBops = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            mReadHanlder.removeCallbacks(mReadBufTask);
        }

        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name != null && name.endsWith(".pcm")) {
                    return true;
                }
                return false;
            }
        };
        if (mSaveRecordDirStr != null) {
            File recordFileDir = new File(mSaveRecordDirStr);
            deleFileByNameFilter(recordFileDir, filenameFilter);
        }
        deleFileByNameFilter(RecorderApplication.getmInstance().getExternalFilesDir(DIRECTORY_MUSIC), filenameFilter);
        deleFileByNameFilter(RecorderApplication.getmInstance().getFilesDir(), filenameFilter);
        Log.d(TAG, "清理完毕----------------");
    }

    private void deleFileByNameFilter(File dir, FilenameFilter filter) {
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles(filter);
            for (File file : files) {
                if (file.isFile()) {
                    Log.d(TAG, "deleFileByNameFilter file = " + file.getPath());
                    file.delete();
                }
            }
        }
    }

    @Override
    public void startRecord() {
        if (mAudioRecord != null) {
            mAudioRecord.startRecording();
            if (shouldSaveAllRecordBuffer) {
                if (!TextUtils.isEmpty(mSaveRecordDirStr)) {
                    mRecordDataFile = new File(mSaveRecordDirStr + File.separator + sdf.format(new Date()) + ".pcm");
                } else {
                    try {
                        mRecordDataFile = new File(RecorderApplication.getmInstance().getExternalFilesDir(DIRECTORY_MUSIC) + File.separator + sdf.format(new Date()) + ".pcm");
                    } catch (Exception e) {
                        e.printStackTrace();
                        mRecordDataFile = new File(RecorderApplication.getmInstance().getFilesDir() + File.separator + sdf.format(new Date()) + ".pcm");
                    }
                }
                File parentFile = mRecordDataFile.getParentFile();
                Log.d(TAG, "parentFile=" + parentFile.getPath());
                if (!parentFile.exists() && !parentFile.mkdirs()) {
                    Log.e(TAG, "创建目录失败, 无法保存录音！为程序正常运行,强制设置shouldSaveAllRecordBuffer = false");
                    shouldSaveAllRecordBuffer = false;
                    mRecordDataFile = null;
                } else {
                    Log.d(TAG, "录音音频保存目录:" + mRecordDataFile.getPath());
                    try {
                        mBops = new BufferedOutputStream(new FileOutputStream(mRecordDataFile, true));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                mSaveRecordBufTask = new SaveRecordBufTask(mBops);
            }
            isStopRecord = false;
            mReadHanlder.postDelayed(mReadBufTask,mSamplingIntervalTime);
            Log.d(TAG, "startRecord");
        } else {
            throw new NullPointerException("当前audioRecord对象为空");
        }
    }

    @Override
    public void stopRecord() {
        if (mAudioRecord == null) {
            throw new NullPointerException("当前audioRecord对象为空");
        }
        if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            mAudioRecord.stop();
            isStopRecord = true;
            if (mBops != null) {
                try {
                    mBops.close();
                    mBops = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mReadHanlder.removeCallbacks(mReadBufTask);
            Log.d(TAG, "stopRecord" + (shouldSaveAllRecordBuffer == true ? "\n音频文件保存路径:\n" + mRecordDataFile.getPath() + "\n本次录制参数如下:\n" + TonyRecorder.this.toString() : "没有保存音频"));
        }
    }

    private static class Inner {
        private static final TonyRecorder mTonyRecorder = new TonyRecorder();
    }

    public static final TonyRecorder getInstance() {
        return Inner.mTonyRecorder;
    }

    public interface IRecorderReadBuff {
        void onRead(byte[] buf, int length);
    }

    public void setRecorderReadBuffListener(IRecorderReadBuff recorderReadBuffListener) {
        mRecorderReadBuffListener = recorderReadBuffListener;
    }

    /**
     * 获取录音数据的监听对象
     */
    private IRecorderReadBuff mRecorderReadBuffListener;
}
