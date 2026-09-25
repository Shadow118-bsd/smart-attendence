package com.example.attendance.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.attendance.data.local.dao.AttendanceRecordDao;
import com.example.attendance.data.local.dao.PendingEventDao;
import com.example.attendance.data.local.entity.AttendanceRecordEntity;
import com.example.attendance.data.local.entity.PendingEventEntity;

@Database(entities = {PendingEventEntity.class, AttendanceRecordEntity.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract PendingEventDao pendingEventDao();
    public abstract AttendanceRecordDao attendanceRecordDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "smart_attendance.db"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
