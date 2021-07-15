package com.example.smartalarmclock.persistence;

import androidx.lifecycle.LiveData;

import com.example.smartalarmclock.ui.notifications.NotifyDataSource;

import java.util.List;

import io.reactivex.Completable;

public class RoomNotifyDataSource  implements NotifyDataSource {

    private final NotifyDao notifyDao;


    public RoomNotifyDataSource(NotifyDao loader)
    {
        notifyDao = loader;
    }

    @Override
    public LiveData<List<NotifyEntity>> getNotifications() {
        return notifyDao.getNotifications();
    }

    @Override
    public Completable insertNotify(NotifyEntity newItem) {
        return notifyDao.insertNotify(newItem);
    }

    @Override
    public void deleteNotifications(NotifyEntity... notes) {
        notifyDao.deleteNotifications(notes);
    }
}
