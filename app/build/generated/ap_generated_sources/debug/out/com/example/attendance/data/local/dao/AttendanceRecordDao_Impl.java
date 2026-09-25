package com.example.attendance.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.attendance.data.local.entity.AttendanceRecordEntity;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AttendanceRecordDao_Impl implements AttendanceRecordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AttendanceRecordEntity> __insertionAdapterOfAttendanceRecordEntity;

  public AttendanceRecordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAttendanceRecordEntity = new EntityInsertionAdapter<AttendanceRecordEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `attendance_records` (`id`,`eventType`,`timestamp`,`dateString`,`timeString`,`faceVerified`,`gpsVerified`,`wifiVerified`,`latitude`,`longitude`,`wifiSsid`,`statusMessage`,`faceImagePath`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final AttendanceRecordEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getEventType() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getEventType());
        }
        if (entity.getTimestamp() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getTimestamp());
        }
        if (entity.getDateString() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDateString());
        }
        if (entity.getTimeString() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getTimeString());
        }
        final int _tmp = entity.isFaceVerified() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final int _tmp_1 = entity.isGpsVerified() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        final int _tmp_2 = entity.isWifiVerified() ? 1 : 0;
        statement.bindLong(8, _tmp_2);
        statement.bindDouble(9, entity.getLatitude());
        statement.bindDouble(10, entity.getLongitude());
        if (entity.getWifiSsid() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getWifiSsid());
        }
        if (entity.getStatusMessage() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getStatusMessage());
        }
        if (entity.getFaceImagePath() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getFaceImagePath());
        }
      }
    };
  }

  @Override
  public void insert(final AttendanceRecordEntity entity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfAttendanceRecordEntity.insert(entity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<AttendanceRecordEntity> getAllRecords() {
    final String _sql = "SELECT * FROM attendance_records ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfEventType = CursorUtil.getColumnIndexOrThrow(_cursor, "eventType");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final int _cursorIndexOfDateString = CursorUtil.getColumnIndexOrThrow(_cursor, "dateString");
      final int _cursorIndexOfTimeString = CursorUtil.getColumnIndexOrThrow(_cursor, "timeString");
      final int _cursorIndexOfFaceVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "faceVerified");
      final int _cursorIndexOfGpsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "gpsVerified");
      final int _cursorIndexOfWifiVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "wifiVerified");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfWifiSsid = CursorUtil.getColumnIndexOrThrow(_cursor, "wifiSsid");
      final int _cursorIndexOfStatusMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "statusMessage");
      final int _cursorIndexOfFaceImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "faceImagePath");
      final List<AttendanceRecordEntity> _result = new ArrayList<AttendanceRecordEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final AttendanceRecordEntity _item;
        final String _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getString(_cursorIndexOfId);
        }
        final String _tmpEventType;
        if (_cursor.isNull(_cursorIndexOfEventType)) {
          _tmpEventType = null;
        } else {
          _tmpEventType = _cursor.getString(_cursorIndexOfEventType);
        }
        final String _tmpTimestamp;
        if (_cursor.isNull(_cursorIndexOfTimestamp)) {
          _tmpTimestamp = null;
        } else {
          _tmpTimestamp = _cursor.getString(_cursorIndexOfTimestamp);
        }
        final String _tmpDateString;
        if (_cursor.isNull(_cursorIndexOfDateString)) {
          _tmpDateString = null;
        } else {
          _tmpDateString = _cursor.getString(_cursorIndexOfDateString);
        }
        final String _tmpTimeString;
        if (_cursor.isNull(_cursorIndexOfTimeString)) {
          _tmpTimeString = null;
        } else {
          _tmpTimeString = _cursor.getString(_cursorIndexOfTimeString);
        }
        final boolean _tmpFaceVerified;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfFaceVerified);
        _tmpFaceVerified = _tmp != 0;
        final boolean _tmpGpsVerified;
        final int _tmp_1;
        _tmp_1 = _cursor.getInt(_cursorIndexOfGpsVerified);
        _tmpGpsVerified = _tmp_1 != 0;
        final boolean _tmpWifiVerified;
        final int _tmp_2;
        _tmp_2 = _cursor.getInt(_cursorIndexOfWifiVerified);
        _tmpWifiVerified = _tmp_2 != 0;
        final double _tmpLatitude;
        _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
        final double _tmpLongitude;
        _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
        final String _tmpWifiSsid;
        if (_cursor.isNull(_cursorIndexOfWifiSsid)) {
          _tmpWifiSsid = null;
        } else {
          _tmpWifiSsid = _cursor.getString(_cursorIndexOfWifiSsid);
        }
        final String _tmpStatusMessage;
        if (_cursor.isNull(_cursorIndexOfStatusMessage)) {
          _tmpStatusMessage = null;
        } else {
          _tmpStatusMessage = _cursor.getString(_cursorIndexOfStatusMessage);
        }
        final String _tmpFaceImagePath;
        if (_cursor.isNull(_cursorIndexOfFaceImagePath)) {
          _tmpFaceImagePath = null;
        } else {
          _tmpFaceImagePath = _cursor.getString(_cursorIndexOfFaceImagePath);
        }
        _item = new AttendanceRecordEntity(_tmpId,_tmpEventType,_tmpTimestamp,_tmpDateString,_tmpTimeString,_tmpFaceVerified,_tmpGpsVerified,_tmpWifiVerified,_tmpLatitude,_tmpLongitude,_tmpWifiSsid,_tmpStatusMessage,_tmpFaceImagePath);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<AttendanceRecordEntity> getRecordsByDate(final String dateString) {
    final String _sql = "SELECT * FROM attendance_records WHERE dateString = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (dateString == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, dateString);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfEventType = CursorUtil.getColumnIndexOrThrow(_cursor, "eventType");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final int _cursorIndexOfDateString = CursorUtil.getColumnIndexOrThrow(_cursor, "dateString");
      final int _cursorIndexOfTimeString = CursorUtil.getColumnIndexOrThrow(_cursor, "timeString");
      final int _cursorIndexOfFaceVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "faceVerified");
      final int _cursorIndexOfGpsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "gpsVerified");
      final int _cursorIndexOfWifiVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "wifiVerified");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfWifiSsid = CursorUtil.getColumnIndexOrThrow(_cursor, "wifiSsid");
      final int _cursorIndexOfStatusMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "statusMessage");
      final int _cursorIndexOfFaceImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "faceImagePath");
      final List<AttendanceRecordEntity> _result = new ArrayList<AttendanceRecordEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final AttendanceRecordEntity _item;
        final String _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getString(_cursorIndexOfId);
        }
        final String _tmpEventType;
        if (_cursor.isNull(_cursorIndexOfEventType)) {
          _tmpEventType = null;
        } else {
          _tmpEventType = _cursor.getString(_cursorIndexOfEventType);
        }
        final String _tmpTimestamp;
        if (_cursor.isNull(_cursorIndexOfTimestamp)) {
          _tmpTimestamp = null;
        } else {
          _tmpTimestamp = _cursor.getString(_cursorIndexOfTimestamp);
        }
        final String _tmpDateString;
        if (_cursor.isNull(_cursorIndexOfDateString)) {
          _tmpDateString = null;
        } else {
          _tmpDateString = _cursor.getString(_cursorIndexOfDateString);
        }
        final String _tmpTimeString;
        if (_cursor.isNull(_cursorIndexOfTimeString)) {
          _tmpTimeString = null;
        } else {
          _tmpTimeString = _cursor.getString(_cursorIndexOfTimeString);
        }
        final boolean _tmpFaceVerified;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfFaceVerified);
        _tmpFaceVerified = _tmp != 0;
        final boolean _tmpGpsVerified;
        final int _tmp_1;
        _tmp_1 = _cursor.getInt(_cursorIndexOfGpsVerified);
        _tmpGpsVerified = _tmp_1 != 0;
        final boolean _tmpWifiVerified;
        final int _tmp_2;
        _tmp_2 = _cursor.getInt(_cursorIndexOfWifiVerified);
        _tmpWifiVerified = _tmp_2 != 0;
        final double _tmpLatitude;
        _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
        final double _tmpLongitude;
        _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
        final String _tmpWifiSsid;
        if (_cursor.isNull(_cursorIndexOfWifiSsid)) {
          _tmpWifiSsid = null;
        } else {
          _tmpWifiSsid = _cursor.getString(_cursorIndexOfWifiSsid);
        }
        final String _tmpStatusMessage;
        if (_cursor.isNull(_cursorIndexOfStatusMessage)) {
          _tmpStatusMessage = null;
        } else {
          _tmpStatusMessage = _cursor.getString(_cursorIndexOfStatusMessage);
        }
        final String _tmpFaceImagePath;
        if (_cursor.isNull(_cursorIndexOfFaceImagePath)) {
          _tmpFaceImagePath = null;
        } else {
          _tmpFaceImagePath = _cursor.getString(_cursorIndexOfFaceImagePath);
        }
        _item = new AttendanceRecordEntity(_tmpId,_tmpEventType,_tmpTimestamp,_tmpDateString,_tmpTimeString,_tmpFaceVerified,_tmpGpsVerified,_tmpWifiVerified,_tmpLatitude,_tmpLongitude,_tmpWifiSsid,_tmpStatusMessage,_tmpFaceImagePath);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
