package com.tonychen.tonyrecorder.ui;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tonychen.tonyrecorder.R;
import com.tonychen.tonyrecorder.recorder.TonyRecorder;
import com.tonychen.tonyrecorder.service.RecorderService;

import static com.tonychen.tonyrecorder.service.RecorderService.AUDIOFORMAT;
import static com.tonychen.tonyrecorder.service.RecorderService.AUDIOSOURC;
import static com.tonychen.tonyrecorder.service.RecorderService.CHANNELCONFIG;
import static com.tonychen.tonyrecorder.service.RecorderService.SAMPLERATEINHZ;
import static com.tonychen.tonyrecorder.service.RecorderService.SAMPLINGINTERVALTIME;
import static com.tonychen.tonyrecorder.service.RecorderService.SAVEALLRECORDERBUFF;


public class RecordFragment extends Fragment {

    public RecordFragment() {
    }

    private View mContentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_record, container, false);
        initView();
        return mContentView;
    }

    private void initView() {
        Button btnCreate = mContentView.findViewById(R.id.btn_create);
        Button btnStart = mContentView.findViewById(R.id.btn_start);
        Button btnStop = mContentView.findViewById(R.id.btn_stop);
        Button btnClear = mContentView.findViewById(R.id.btn_clear);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRecorderByArugment();
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    TonyRecorder.getInstance().startRecord();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    TonyRecorder.getInstance().stopRecord();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    TonyRecorder.getInstance().clearSaveFile();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createRecorderByArugment() {
        Intent it = new Intent(getContext(), RecorderService.class);
        it.setAction(RecorderService.ACTION_STARTBYARGUMENT);
        it.putExtra(AUDIOSOURC, MediaRecorder.AudioSource.MIC);
        it.putExtra(SAMPLERATEINHZ, 16000);
        it.putExtra(CHANNELCONFIG, AudioFormat.CHANNEL_IN_MONO);
        it.putExtra(AUDIOFORMAT, AudioFormat.ENCODING_PCM_16BIT);
        it.putExtra(SAMPLINGINTERVALTIME, 50);
        it.putExtra(SAVEALLRECORDERBUFF, true);
        getContext().startService(it);
//        ServiceConnection mRecorderConnection = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//
//            }
//        };
//        getContext().bindService(it, mRecorderConnection, Context.BIND_AUTO_CREATE);
    }

}
