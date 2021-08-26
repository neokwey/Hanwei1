package com.example.demo_demo.ui.app_info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.demo_demo.R;

public class AppInfoFragment extends Fragment {

    private AppInfoViewModel appInfoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        appInfoViewModel =
                ViewModelProviders.of(this).get(AppInfoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_app_info, container, false);

        return root;
    }
}