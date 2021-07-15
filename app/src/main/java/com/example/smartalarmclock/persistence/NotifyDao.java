package com.example.smartalarmclock.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.smartalarmclock.persistence.NotifyEntity;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface NotifyDao {

    @Query("SELECT * from NotifyEntity LIMIT 20")
    LiveData<List<NotifyEntity>> getNotifications();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertNotify(NotifyEntity newItem);

    @Delete
    void deleteNotifications(NotifyEntity... notes);
}
