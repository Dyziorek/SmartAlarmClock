package com.example.smartalarmclock.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartalarmclock.MainActivity;
import com.example.smartalarmclock.R;

import static com.example.smartalarmclock.helper.Settings.settings;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private boolean alarmSetup;
    private String timeAlarm;
    private TextView alarmTimeText;
    private SwitchCompat alarmSet;
    private SwitchCompat debugSet;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
               new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        alarmTimeText = root.findViewById(R.id.editAlarmTime);
        alarmSet = root.findViewById(R.id.swAlarmClock);
        debugSet = root.findViewById(R.id.swDebugFlag);

        homeViewModel.getAlarmTime().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                alarmTimeText.setText(s);
            }
        });

        homeViewModel.getAlarmSet().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                alarmSet.setChecked(aBoolean);
            }
        });

        homeViewModel.getDebugFlag().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) { debugSet.setChecked(aBoolean); }
        });

        ((TextView)root.findViewById(R.id.text_home)).setText("This is home fragment");
        String alarmTime = root.getResources().getText(R.string.alarmTime).toString();
        ((TextView)root.findViewById(R.id.textAlarmTime)).setText(alarmTime);

        final Button btnSave = root.findViewById(R.id.btnUpdateAlarm);

        btnSave.setOnClickListener( v -> saveSettings());

        homeViewModel.updateAlarmSet(settings.isAlarmSet() > 0);
        homeViewModel.updateAlarmTime(settings.getAlarmTime());

        return root;
    }

    private void saveSettings()
    {
        settings.setAlarmStart(alarmSet.isChecked());
        settings.setAlarmTime(alarmTimeText.getText().toString());
        settings.setDebug(debugSet.isChecked());
        ((MainActivity)getActivity()).setAlarm();
    }
}