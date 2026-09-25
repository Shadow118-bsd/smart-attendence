package com.example.attendance.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.attendance.data.local.entity.PendingEventEntity;
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
public final class PendingEventDao_Impl implements PendingEventDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PendingEventEntity> __insertionAdapterOfPendingEventEntity;

  private final EntityDeletionOrUpdateAdapter<PendingEventEntity> __updateAdapterOfPendingEventEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSyncedEvents;

  public PendingEventDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPendingEventEntity = new EntityInsertionAdapter<PendingEventEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `pending_events` (`eventId`,`eventType`,`payloadJson`,`timestamp`,`status`,`retryCount`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final PendingEventEntity entity) {
        if (entity.getEventId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getEventId());
        }
        if (entity.getEventType() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getEventType());
        }
        if (entity.getPayloadJson() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getPayloadJson());
        }
        if (entity.getTimestamp() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getTimestamp());
        }
        if (entity.getStatus() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getStatus());
        }
        statement.bindLong(6, entity.getRetryCount());
      }
    };
    this.__updateAdapterOfPendingEventEntity = new EntityDeletionOrUpdateAdapter<PendingEventEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `pending_events` SET `eventId` = ?,`eventType` = ?,`payloadJson` = ?,`timestamp` = ?,`status` = ?,`retryCount` = ? WHERE `eventId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final PendingEventEntity entity) {
        if (entity.getEventId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getEventId());
        }
        if (entity.getEventType() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getEventType());
        }
        if (entity.getPayloadJson() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getPayloadJson());
        }
        if (entity.getTimestamp() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getTimestamp());
        }
        if (entity.getStatus() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getStatus());
        }
        statement.bindLong(6, entity.getRetryCount());
        if (entity.getEventId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getEventId());
        }
      }
    };
    this.__preparedStmtOfDeleteSyncedEvents = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM pending_events WHERE status = 'SYNCED'";
        return _query;
      }
    };
  }

  @Override
  public void insert(final PendingEventEntity entity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfPendingEventEntity.insert(entity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final PendingEventEntity entity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfPendingEventEntity.handle(entity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteSyncedEvents() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSyncedEvents.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteSyncedEvents.release(_stmt);
    }
  }

  @Override
  public List<PendingEventEntity> getPendingEvents() {
    final String _sql = "SELECT * FROM pending_events WHERE status = 'PENDING' ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfEventId = CursorUtil.getColumnIndexOrThrow(_cursor, "eventId");
      final int _cursorIndexOfEventType = CursorUtil.getColumnIndexOrThrow(_cursor, "eventType");
      final int _cursorIndexOfPayloadJson = CursorUtil.getColumnIndexOrThrow(_cursor, "payloadJson");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
      final List<PendingEventEntity> _result = new ArrayList<PendingEventEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final PendingEventEntity _item;
        final String _tmpEventId;
        if (_cursor.isNull(_cursorIndexOfEventId)) {
          _tmpEventId = null;
        } else {
          _tmpEventId = _cursor.getString(_cursorIndexOfEventId);
        }
        final String _tmpEventType;
        if (_cursor.isNull(_cursorIndexOfEventType)) {
          _tmpEventType = null;
        } else {
          _tmpEventType = _cursor.getString(_cursorIndexOfEventType);
        }
        final String _tmpPayloadJson;
        if (_cursor.isNull(_cursorIndexOfPayloadJson)) {
          _tmpPayloadJson = null;
        } else {
          _tmpPayloadJson = _cursor.getString(_cursorIndexOfPayloadJson);
        }
        final String _tmpTimestamp;
        if (_cursor.isNull(_cursorIndexOfTimestamp)) {
          _tmpTimestamp = null;
        } else {
          _tmpTimestamp = _cursor.getString(_cursorIndexOfTimestamp);
        }
        final String _tmpStatus;
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _tmpStatus = null;
        } else {
          _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
        }
        final int _tmpRetryCount;
        _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
        _item = new PendingEventEntity(_tmpEventId,_tmpEventType,_tmpPayloadJson,_tmpTimestamp,_tmpStatus,_tmpRetryCount);
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
