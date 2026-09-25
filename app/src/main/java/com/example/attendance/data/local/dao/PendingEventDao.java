package com.example.attendance.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.attendance.data.local.entity.PendingEventEntity;

import java.util.List;

@Dao
public interface PendingEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PendingEventEntity entity);

    @Query("SELECT * FROM pending_events WHERE status = 'PENDING' ORDER BY timestamp ASC")
    List<PendingEventEntity> getPendingEvents();

    @Update
    void update(PendingEventEntity entity);

    @Query("DELETE FROM pending_events WHERE status = 'SYNCED'")
    void deleteSyncedEvents();
}
