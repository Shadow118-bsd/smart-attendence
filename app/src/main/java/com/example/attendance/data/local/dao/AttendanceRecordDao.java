package com.example.attendance.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.attendance.data.local.entity.AttendanceRecordEntity;

import java.util.List;

@Dao
public interface AttendanceRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AttendanceRecordEntity entity);

    @Query("SELECT * FROM attendance_records ORDER BY timestamp DESC")
    List<AttendanceRecordEntity> getAllRecords();

    @Query("SELECT * FROM attendance_records WHERE dateString = :dateString ORDER BY timestamp DESC")
    List<AttendanceRecordEntity> getRecordsByDate(String dateString);
}
