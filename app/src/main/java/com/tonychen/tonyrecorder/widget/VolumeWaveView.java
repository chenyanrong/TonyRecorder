package com.tonychen.tonyrecorder.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.tonychen.tonyrecorder.bean.LimitQueue;
import com.tonychen.tonyrecorder.util.UIUtil;

/**
 * Created by TonyChen on 2018/05/06;
 * Email : chenchenyanrong@163.com
 * Blog : http://blog.csdn.net/weixin_37484990
 * Description :
 */

public class VolumeWaveView extends SurfaceView implements SurfaceHolder.Callback {
    public static final String TAG = VolumeWaveView.class.getSimpleName();

    private SurfaceHolder mHolder;
    private HandlerThread mDrawThread;
    private Handler mDrawHandler;
    private int circleRadio;
    private int mDataDrawWidth;

    private int mDataSize;
    private LimitQueue<Integer> mQueue;

    private Paint mPaintLine;
    private Paint mPaintSubLine;
    private Paint mCenterLine;
    private Paint mDataPaint;
    private Paint mCirclePaint;

    public boolean isStopDrawing() {
        return isStopDrawing;
    }

    public void setStopDrawing(boolean stopDrawing) {
        isStopDrawing = stopDrawing;
        if (!isStopDrawing) {
            mDrawHandler.post(mDrawTask);
        }
    }

    private boolean isStopDrawing;

    private int fps = 10;

    private Runnable mDrawTask = new Runnable() {
        @Override
        public void run() {
            draw();
            if (!isStopDrawing) {
                mDrawHandler.postDelayed(mDrawTask, 1000 / fps);
            }
        }
    };

    public VolumeWaveView(Context context) {
        this(context, null);
    }

    public VolumeWaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VolumeWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mDrawThread = new HandlerThread("SurfaceViewDrawThread");
        mDrawThread.start();
        mDrawHandler = new Handler(mDrawThread.getLooper());

        circleRadio = UIUtil.dip2px(getContext(), 5); // 小圆球半径
        mDataDrawWidth = UIUtil.dip2px(getContext(), 1); // 数据线宽度

        mPaintLine = new Paint();
        mPaintSubLine = new Paint();
        mCenterLine = new Paint();
        mDataPaint = new Paint();
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);

        mCirclePaint.setColor(Color.rgb(246, 131, 126));
        mPaintLine.setColor(Color.rgb(169, 169, 169));
        mPaintSubLine.setColor(Color.argb(220, 188, 188, 188));
        mPaintSubLine.setStyle(Paint.Style.STROKE);
        mPaintSubLine.setPathEffect(new DashPathEffect(new float[]{UIUtil.dip2px(getContext(), 8), UIUtil.dip2px(getContext(), 4)}, 0));
        mCenterLine.setColor(Color.rgb(39, 199, 175));
        mDataPaint.setColor(Color.rgb(39, 199, 175));
        mDataPaint.setStrokeWidth(mDataDrawWidth);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VolumeWaveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(TAG, "surfaceChanged width=" + width + " height=" + height);
        mDataSize = getWidth() / mDataDrawWidth;
        mQueue = new LimitQueue<>(mDataSize);
        Log.d(TAG, "数据队列理论长度=" + getWidth() / mDataDrawWidth);
        mDrawHandler.removeCallbacks(mDrawTask);
        mDrawHandler.post(mDrawTask);
    }

    public synchronized void addData(int data) {
        if (data < 0) {
            data = 0;
        }
        if (data > 100) {
            data = 100;
        }
        mQueue.offer(data);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(TAG, "surfaceDestroyed");
    }


    private synchronized void draw() {
        Canvas canvas = mHolder.lockCanvas(
                new Rect(0, 0, getWidth(), getHeight()));// 关键:获取画布
        if (canvas == null) {
            return;
        }
        canvas.drawARGB(255, 239, 239, 239); // 解决残影问题
        canvas.drawLine(0, circleRadio * 2, getWidth(), circleRadio * 2, mPaintLine);//最上面的那根线
        canvas.drawLine(0, getHeight() - circleRadio * 2, getWidth(), getHeight() - circleRadio * 2, mPaintLine);//最下面的那根线
        canvas.drawLine(0, (getHeight() - circleRadio * 4) / 4 + circleRadio * 2, getWidth(), (getHeight() - circleRadio * 4) / 4 + circleRadio * 2, mPaintSubLine);//第二根线
        canvas.drawLine(0, (getHeight() - circleRadio * 4) / 4 * 3 + circleRadio * 2, getWidth(), (getHeight() - circleRadio * 4) / 4 * 3 + circleRadio * 2, mPaintSubLine);//第3根线
        canvas.drawLine(0, getHeight() * 0.5f, getWidth(), getHeight() * 0.5f, mCenterLine);//中心线

        if (mQueue.size() <= 0) {
            canvas.drawCircle(0, circleRadio, circleRadio, mCirclePaint);// 上面小圆
            canvas.drawCircle(0, getHeight() - circleRadio, circleRadio, mCirclePaint);// 下面小圆
            canvas.drawLine(0, 0, 0, getHeight(), mCirclePaint);//垂直的线
        }
        for (int index = 0; index < mQueue.size(); index++) {
            int i = mQueue.get(index);
            int startX = index * mDataDrawWidth;
            int startY = (int) (2*circleRadio+(1 - i / 100.0) * (getHeight()-4*circleRadio) / 2);
            int endX = index * mDataDrawWidth;
            int endY = (int) (2*circleRadio+(1 + i / 100.0) * (getHeight()-4*circleRadio) / 2 );
            Log.d(TAG, "draw data index = "+index+" data=" + i + " startX=" + startX + " startY=" + startY + " endX=" + endX + " endY=" + endY);
            canvas.drawLine(startX, startY, endX, endY, mDataPaint);
            if (index == mQueue.size() - 1) {
                canvas.drawCircle(startX, circleRadio, circleRadio, mCirclePaint);// 上面小圆
                canvas.drawCircle(startX, getHeight() - circleRadio, circleRadio, mCirclePaint);// 下面小圆
                canvas.drawLine(startX, 0, startX, getHeight(), mCirclePaint);//垂直的线
            }
        }
        mHolder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
    }

}
