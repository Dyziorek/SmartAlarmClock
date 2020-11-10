package com.example.smartalarmclock.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartalarmclock.MainActivity;
import com.example.smartalarmclock.R;
import com.example.smartalarmclock.SmartClockStates;
import com.example.smartalarmclock.netCode.NetCommand;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.example.smartalarmclock.helper.Settings.settings;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private DashBoardViewModelFactory factory;
    private SwitchCompat monitor;
    private Button saveData;
    private Button resetData;
    private Button infoButton;
    private TextView infor;
    private TextView plugIP;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        factory = new DashBoardViewModelFactory(new DashboardRepository(Executors.newSingleThreadExecutor(), settings));
        dashboardViewModel =
                new ViewModelProvider(this, factory).get(DashboardViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        monitor = root.findViewById(R.id.switchMonitor);
        plugIP = root.findViewById(R.id.smartPlugIP);
        infor = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getMonitor().observe(getViewLifecycleOwner(), s -> monitor.setChecked(s));
        dashboardViewModel.getSmartPlugIP().observe(getViewLifecycleOwner(), s -> updatePluginIP(s));
        dashboardViewModel.getInformation().observe(getViewLifecycleOwner(), textVal ->
        {
            try {
                int resourceVal = Integer.parseInt(String.valueOf(textVal));
                infor.setText(getString(resourceVal));
                String valueText = textVal;
                Toast.makeText(getActivity(), valueText, Toast.LENGTH_LONG).show();
            }catch (NumberFormatException numberErr) {
                String[] parts = textVal.split(",");
                infor.setText(String.format(getString(R.string.smartPlugInfo), parts[0], parts[1], parts[2]));
            }
        });
        resetData = root.findViewById(R.id.resetButton);
        resetData.setOnClickListener( v -> updateMonitor());
        saveData = root.findViewById(R.id.btnSave);
        saveData.setOnClickListener( v -> saveData());
        infoButton =  root.findViewById(R.id.Info);
        infoButton.setOnClickListener( v-> getInfo());
        return root;
    }

    private void getInfo()
    {
        dashboardViewModel.getAsyncInfo();
    }



    private void updatePluginIP(CharSequence textVal)
    {
        try {
           int resourceVal = Integer.parseInt(String.valueOf(textVal));
           plugIP.setText(getString(resourceVal));
        }catch (NumberFormatException numberErr) {
            plugIP.setText(textVal);
        }
    }

    private void saveData()
    {
        String errorData = getString(R.string.error_save_settings);
        try {
            String newIPAddress = plugIP.getText().toString();
            Boolean monitoring = monitor.isChecked();
            dashboardViewModel.updateSmartPlugIP(newIPAddress);
            dashboardViewModel.updateMonitor(monitoring);
            settings.setMonitorCalls(monitoring);
            Log.d("DEBUG", "onClick: '" + newIPAddress + "'");
            settings.setSmartPlugHost(newIPAddress);
            ((MainActivity)getActivity()).save();
        }
        catch (Exception errors)
        {
            errors.printStackTrace();
            Log.e("ERR", "onClick: error " + errors.getMessage() + errors.getClass().getTypeName(), errors );
            Toast.makeText(getActivity(), errors.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateMonitor()
    {
        Runnable reset = () -> {
            dashboardViewModel.updateMonitor(false);
            dashboardViewModel.updateSmartPlugIP("0.0.0.0");
            dashboardViewModel.getSmartPlugHost(9999);
        };
        reset.run();
    }
}