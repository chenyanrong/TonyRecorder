package com.tonychen.tonyrecorder.bean;

import android.media.AudioFormat;
import android.media.MediaRecorder;

/**
 * Created by TonyChen on 2018/04/29;
 * Email : chenchenyanrong@163.com
 * Blog : http://blog.csdn.net/weixin_37484990
 * Description :
 */

public class RecorderConfig {
    public int FREQUENCY = 16000;// 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    public int CHANNELCONGIFIGURATION = AudioFormat.CHANNEL_IN_MONO;// 设置单声道声道
    public int AUDIOENCODING = AudioFormat.ENCODING_PCM_16BIT;// 音频数据格式：每个样本16位
    public int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;// 音频获取源
    private int recBufSize;// 录音最小buffer大小
//    recBufSize = AudioRecord.getMinBufferSize(FREQUENCY,
//    CHANNELCONGIFIGURATION, AUDIOENCODING);// 录音组件
//    audioRecord = new AudioRecord(AUDIO_SOURCE,// 指定音频来源，这里为麦克风
//                                  FREQUENCY, // 16000HZ采样频率
//                                  CHANNELCONGIFIGURATION,// 录制通道
//                                  AUDIO_SOURCE,// 录制编码格式
//                                  recBufSize);// 录制缓冲区大小 //先修改
}
