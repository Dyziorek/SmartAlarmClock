package com.example.smartalarmclock.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartalarmclock.R;

public class NotificationsFragment extends Fragment {

    private RecyclerView viewRecycler;

    private NotificationsViewModel notificationsViewModel;

    private NotificationsViewModelFactory factory;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        factory = new NotificationsViewModelFactory(getContext());
        notificationsViewModel =
                new ViewModelProvider(this, factory).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        viewRecycler = root.findViewById(R.id.recycler_view);
        //viewRecycler.setAdapter();
        return root;
    }
}