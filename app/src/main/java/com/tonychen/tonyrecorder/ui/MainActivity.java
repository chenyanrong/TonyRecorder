package com.tonychen.tonyrecorder.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.tonychen.tonyrecorder.R;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;


public class MainActivity extends FragmentActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUESTCODE = 1111;
    private String[] mPermissionArr = new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private ViewPager mViewPager;
    private PagerTabStrip mPagerTabStrip;
    private PagerAdapter mAdapter;

    private List<String> mTitleList;// 标题
    private List<Fragment> mFragmentList;// 页面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissioncheck();
        }
    }

    /**
     * 权限检测
     */
    private void permissioncheck() {
        List<String> shouldRequestPermissionList = new ArrayList();
        for (String permission : mPermissionArr) {
            if (PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission)) {
                shouldRequestPermissionList.add(permission);
            }
        }
        if (shouldRequestPermissionList.size() > 0) {
            for (String permission : shouldRequestPermissionList) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) { // 跳转设置页
                    openAppDetails();
                    return;
                }
            }
            String[] shourldRequestPermissionArr = new String[shouldRequestPermissionList.size()];
            shouldRequestPermissionList.toArray(shourldRequestPermissionArr);
            // 请求权限
            ActivityCompat.requestPermissions(this, shourldRequestPermissionArr, REQUESTCODE);
        } else { // 已获得所有的权限
            Log.d(TAG, "已获得所有的权限");
//            Intent itStartRecorderService = new Intent(this, RecorderService.class);
//            startService(itStartRecorderService);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        boolean shouldShowSettingActiviy = false;
        if (REQUESTCODE == requestCode) {
            for (int result : grantResults) {
                if (PERMISSION_GRANTED != result) {
                    shouldShowSettingActiviy = true;
                    break;
                }
            }
        }
        if (shouldShowSettingActiviy) {
            openAppDetails();
        }
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("权限申请");
        builder.setMessage("程序需要访问 “录音机” 和 “外部存储器”，请到 “应用信息 -> 权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "缺失必要权限,程序自动关闭!", Toast.LENGTH_SHORT).show();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                }, 1500);
            }
        });
        builder.show();
    }

    private void initView() {
        // 绑定组件
        mViewPager = (ViewPager) findViewById(R.id.vp_content);
        mPagerTabStrip = (PagerTabStrip) findViewById(R.id.pts_tab);
        mPagerTabStrip.setTextColor(getResources().getColor(R.color.colorPrimary));  //设置 字体颜色
//        //取消Tab 下面的长横线
//        mPagerTabStrip.setDrawFullUnderline(false);
        //设置每个Tab的下划线颜色
        mPagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorPrimaryDark));
        mPagerTabStrip.setTextSize(0, 40);
        mPagerTabStrip.setTextSpacing(10);

        mTitleList = new ArrayList<>();
        mTitleList.add("Recorder");
        mTitleList.add("Setting");

        mFragmentList = new ArrayList<>();
        mFragmentList.add(new RecordFragment());
        mFragmentList.add(new SettingFragment());

        mAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        mViewPager.setCurrentItem(0);
        mViewPager.setFocusable(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter = null;
    }

    class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }
    }
}
