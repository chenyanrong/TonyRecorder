package com.tonychen.tonyrecorder.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonychen.tonyrecorder.R;

import static com.tonychen.tonyrecorder.service.RecorderService.TAG;

/**
 * Created by TonyChen on 2018/04/29;
 * Email : chenchenyanrong@163.com
 * Blog : http://blog.csdn.net/weixin_37484990
 * Description :
 */

public class KeyValueView extends LinearLayout {
    /**
     * 标题
     */
    private String mKeyStr;

    /**
     * 值
     */
    private String mValueStr;

    private TextView mTVTitle;
    private EditText mETValue;

    private Paint mDivLinePaint;

    public KeyValueView(Context context) {
        super(context);
        initView();
    }

    public KeyValueView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KeyValueView);
        mKeyStr = a.getString(R.styleable.KeyValueView_title);
        Log.d(TAG, "mKeyStr=" + mKeyStr);
        mValueStr = a.getString(R.styleable.KeyValueView_value);
        a.recycle();
        initView();
        mDivLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDivLinePaint.setColor(Color.parseColor("#ff0000"));

    }

    public KeyValueView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mTVTitle = new TextView(getContext());
        Log.d(TAG, "TextUtils.isEmpty(mKeyStr) = " + TextUtils.isEmpty(mKeyStr));
        mTVTitle.setText(TextUtils.isEmpty(mKeyStr) ? "默认的标题" : mKeyStr.trim());
        mTVTitle.setLines(1);
        mETValue = new EditText(getContext());
        if (TextUtils.isEmpty(mValueStr)) {
            mETValue.setHint("请输入内容...");
        } else {
            mETValue.setText(mValueStr);
        }
        LayoutParams etValueParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mETValue.setLayoutParams(etValueParams);
        mETValue.setBackground(null);
        this.addView(mTVTitle);
        this.addView(mETValue);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTVTitle.setMaxWidth(getMeasuredWidth() / 4);
                mTVTitle.setMinWidth(getMeasuredWidth() / 8);
                KeyValueView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    public String getValue() {
        return mETValue.getText().toString().trim();
    }

}
