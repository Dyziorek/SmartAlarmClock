package com.example.smartalarmclock.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartalarmclock.persistence.NotifyEntity;

import java.util.Arrays;
import java.util.List;

public class NotificationsViewModel extends ViewModel {

    private final NotifyDataSource dataSource;

    private final LiveData<List<NotifyEntity>> notes;

    public NotificationsViewModel(NotifyDataSource dbSource) {
        dataSource = dbSource;
        notes = dbSource.getNotifications();
    }



}