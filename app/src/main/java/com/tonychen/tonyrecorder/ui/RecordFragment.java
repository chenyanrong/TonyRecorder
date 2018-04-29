package com.tonychen.tonyrecorder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonychen.tonyrecorder.R;
import com.tonychen.tonyrecorder.service.RecorderService;


public class RecordFragment extends Fragment {

    public RecordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent itStartRecorderService = new Intent(getContext(), RecorderService.class);
        getContext().startService(itStartRecorderService);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

}
