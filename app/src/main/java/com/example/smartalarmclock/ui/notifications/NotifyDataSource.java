package com.example.smartalarmclock.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.smartalarmclock.persistence.NotifyEntity;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface NotifyDataSource {
    @Query( "SELECT * FROM NotifyEntity")
    LiveData<List<NotifyEntity>> getNotifications();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertNotify(NotifyEntity newItem);

    void deleteNotifications(NotifyEntity... notes);
}
