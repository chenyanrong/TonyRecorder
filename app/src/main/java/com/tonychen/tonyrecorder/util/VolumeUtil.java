package com.tonychen.tonyrecorder.util;

import android.util.Log;

/**
 * Created by TonyChen on 2018/05/06;
 * Email : chenchenyanrong@163.com
 * Blog : http://blog.csdn.net/weixin_37484990
 * Description : 参考资料 https://blog.csdn.net/greatpresident/article/details/38402147
 */

public class VolumeUtil {
    public static final String TAG = VolumeUtil.class.getSimpleName();
    private static  int BASE = 1; // 噪声振幅平方数

    public static final double getVolume(byte[] buf, int length) {
        double db = 0;
        long quadraticSum = 0;
        // 将 buffer 内容取出，进行平方和运算
        for (int i = 0; i < length; i++) {
            quadraticSum += buf[i] * buf[i];
        }
        // 平方和除以数据总长度，得到音量大小。
        double mean = quadraticSum / (double) length;
        db = 10 * Math.log10(mean / BASE);
        Log.d(TAG, "音量:" + db);
        return db;
    }

//    public static int getVolume(byte[] buffer,int lenght){
//        int volume = 0;
//        long v = 0;
//        int readLen =lenght;
//        short[] buffer2 = new short[readLen];
//
//        for (int i = 0; i < readLen; i+=2) {
//            buffer2[i/2] = (short) (((buffer[i + 1] << 8) | buffer[i + 0] & 0xff));
//        }
//
//        Arrays.sort(buffer2);
//        Log.v(TAG, "buffer中的最小值--->"+buffer2[0]);
//
//        short min = buffer2[0];
//        Log.v(TAG,"buffer中的数据最小的大小--->"+buffer2[0]);
//
//        double v1= min*min;
//        double r1 = 32768 * 32768;
//
//        //结果应该是-90~0db
//        int db = (int) Math.round((10 * Math.log10(v1 / r1)+80)*120/(double)90);// 单位是dB
//        Log.d(TAG, "分贝数--->"+db);
//
//        return db;
//    }
}
