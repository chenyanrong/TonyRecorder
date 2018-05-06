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
    private static final int BASE = 1;

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
}
