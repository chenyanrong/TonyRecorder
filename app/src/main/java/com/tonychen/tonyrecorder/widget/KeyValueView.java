package com.tonychen.tonyrecorder.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.tonychen.tonyrecorder.R;
import com.tonychen.tonyrecorder.util.UIUtil;

import static android.view.TouchDelegate.TO_RIGHT;
import static android.widget.Spinner.MODE_DROPDOWN;


/**
 * Created by TonyChen on 2018/04/29;
 * Email : chenchenyanrong@163.com
 * Blog : http://blog.csdn.net/weixin_37484990
 * Description :
 */

public class KeyValueView extends LinearLayout {
    private static final String TAG = KeyValueView.class.getSimpleName();
    /**
     * 标题
     */
    private String mKeyStr;

    /**
     * 值
     */
    private String mValueStr;

    /**
     * 标题获取到焦点弹出关于该键的说明
     */
    private String mTipStr;

    private String[] dataArr;

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.d(TAG, "dispatchTouchEvent ev.getAction = " + ev.getAction() + " super.dispatchTouchEvent(ev)=" + super.dispatchTouchEvent(ev));
//        return super.dispatchTouchEvent(ev);
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.d(TAG, "onInterceptTouchEvent ev.getAction = " + ev.getAction() + " super.onInterceptTouchEvent(ev)=" + super.onInterceptTouchEvent(ev));
//        return super.onInterceptTouchEvent(ev);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        Log.d(TAG, "onTouchEvent ev.getAction = " + event.getAction() + " super.onTouchEvent(ev)=" + super.onTouchEvent(event));
//        return super.onTouchEvent(event);
//    }

    public void setTipStr(final String tipStr) {
        mTipStr = tipStr;
        mTVTitle.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange hasFocus= " + hasFocus);
                if (hasFocus) {
                    // 用于PopupWindow的View
                    View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_popup, null, false);
                    TextView tvTips = contentView.findViewById(R.id.tv_tips);
                    tvTips.setText(mTipStr);
                    // 创建PopupWindow对象，其中：
                    // 第一个参数是用于PopupWindow中的View，第二个参数是PopupWindow的宽度，
                    // 第三个参数是PopupWindow的高度，第四个参数指定PopupWindow能否获得焦点
                    PopupWindow window = new PopupWindow(contentView, 100, 100, true);
                    // 设置PopupWindow的背景
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    // 设置PopupWindow是否能响应外部点击事件
                    window.setOutsideTouchable(true);
                    // 设置PopupWindow是否能响应点击事件
                    window.setTouchable(true);
                    // 显示PopupWindow，其中：
                    // 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
                    window.showAsDropDown(v, v.getWidth(), v.getHeight() / 2, TO_RIGHT);
                    // 或者也可以调用此方法显示PopupWindow，其中：
                    // 第一个参数是PopupWindow的父View，第二个参数是PopupWindow相对父View的位置，
                    // 第三和第四个参数分别是PopupWindow相对父View的x、y偏移
                    // window.showAtLocation(parent, gravity, x, y);
                }
            }
        });
    }

    private TextView mTVTitle;
    private EditText mETValue;
    private Spinner mSpinner;

    private Paint mDivLinePaint;

    public KeyValueView(Context context) {
        super(context);
        initView();
    }

    public KeyValueView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KeyValueView);
        mKeyStr = a.getString(R.styleable.KeyValueView_title);
        mTipStr = a.getString(R.styleable.KeyValueView_tips);
        mValueStr = a.getString(R.styleable.KeyValueView_value);
        int resourceId = a.getResourceId(R.styleable.KeyValueView_data, 0);
        if (resourceId != 0) {
            dataArr = getContext().getResources().getStringArray(resourceId);
        }
        a.recycle();
        initView();
        mDivLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDivLinePaint.setColor(Color.parseColor("#ff0000"));
        mDivLinePaint.setStrokeWidth(UIUtil.dip2px(getContext(), 3));
        setTipStr(mTipStr);
    }

    public KeyValueView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mTVTitle = new TextView(getContext());
        mTVTitle.setFocusable(true);
        mTVTitle.setFocusableInTouchMode(true);
        mTVTitle.setText(TextUtils.isEmpty(mKeyStr) ? "默认的标题" : mKeyStr.trim());
        mTVTitle.setLines(1);
        mTVTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mTVTitle.setTextSize(14);
        mTVTitle.setBackgroundColor(Color.parseColor("#00ff00"));
        mETValue = new EditText(getContext());
        if (TextUtils.isEmpty(mValueStr)) {
            mETValue.setHint("请输入内容...");
        } else {
            mETValue.setText(mValueStr);
        }
//        LayoutParams etValueParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        mETValue.setMinWidth(getMeasuredWidth() / 8);
//        mETValue.setMaxWidth(getMeasuredWidth()/5*3);
        mETValue.setLines(1);
        mETValue.setTextSize(12);
//        mETValue.setLayoutParams(etValueParams);
        mETValue.setBackground(null);
        this.addView(mTVTitle);
//        this.addView(mETValue);

        if (dataArr != null && dataArr.length > 0) {
            mSpinner = new Spinner(getContext(), MODE_DROPDOWN);
            //适配器
            ArrayAdapter arr_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dataArr);
            //设置样式
            arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //加载适配器
            mSpinner.setAdapter(arr_adapter);
            LayoutParams spinnerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            this.addView(mSpinner, spinnerParams);
        }
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTVTitle.setMaxWidth(KeyValueView.this.getWidth() / 2);
                mTVTitle.setMinWidth(KeyValueView.this.getWidth() / 10 * 3);
//                Log.d(TAG, "onGlobalLayout-------KeyValueView.this=" + KeyValueView.this);
                if (mSpinner != null) {
                 }
                KeyValueView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    public String getValue() {
        return mETValue.getText().toString().trim();
    }

}
