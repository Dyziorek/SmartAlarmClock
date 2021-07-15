package com.example.smartalarmclock.ui.notifications;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartalarmclock.persistence.NotifyDatabase;
import com.example.smartalarmclock.persistence.RoomNotifyDataSource;

public class NotificationsViewModelFactory implements ViewModelProvider.Factory {

    private final NotifyDataSource dataSource;

    public NotificationsViewModelFactory(Context ctx)
    {
        NotifyDatabase database = NotifyDatabase.getInstance(ctx);
        dataSource = new RoomNotifyDataSource(database.notifyDao());
    }

    public NotificationsViewModelFactory(NotifyDataSource provider) { dataSource = provider; }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NotificationsViewModel.class))
        {
            return (T) new NotificationsViewModel(dataSource);
        }
        throw new IllegalArgumentException("Wrong View Model class");
    }
}
