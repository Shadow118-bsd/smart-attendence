package com.example.attendance.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.example.attendance.data.local.dao.AttendanceRecordDao;
import com.example.attendance.data.local.dao.AttendanceRecordDao_Impl;
import com.example.attendance.data.local.dao.PendingEventDao;
import com.example.attendance.data.local.dao.PendingEventDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile PendingEventDao _pendingEventDao;

  private volatile AttendanceRecordDao _attendanceRecordDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `pending_events` (`eventId` TEXT NOT NULL, `eventType` TEXT, `payloadJson` TEXT, `timestamp` TEXT, `status` TEXT, `retryCount` INTEGER NOT NULL, PRIMARY KEY(`eventId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `attendance_records` (`id` TEXT NOT NULL, `eventType` TEXT, `timestamp` TEXT, `dateString` TEXT, `timeString` TEXT, `faceVerified` INTEGER NOT NULL, `gpsVerified` INTEGER NOT NULL, `wifiVerified` INTEGER NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `wifiSsid` TEXT, `statusMessage` TEXT, `faceImagePath` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'a7bee08169aa044e500debbb4a264d17')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `pending_events`");
        db.execSQL("DROP TABLE IF EXISTS `attendance_records`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsPendingEvents = new HashMap<String, TableInfo.Column>(6);
        _columnsPendingEvents.put("eventId", new TableInfo.Column("eventId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingEvents.put("eventType", new TableInfo.Column("eventType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingEvents.put("payloadJson", new TableInfo.Column("payloadJson", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingEvents.put("timestamp", new TableInfo.Column("timestamp", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingEvents.put("status", new TableInfo.Column("status", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingEvents.put("retryCount", new TableInfo.Column("retryCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPendingEvents = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPendingEvents = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPendingEvents = new TableInfo("pending_events", _columnsPendingEvents, _foreignKeysPendingEvents, _indicesPendingEvents);
        final TableInfo _existingPendingEvents = TableInfo.read(db, "pending_events");
        if (!_infoPendingEvents.equals(_existingPendingEvents)) {
          return new RoomOpenHelper.ValidationResult(false, "pending_events(com.example.attendance.data.local.entity.PendingEventEntity).\n"
                  + " Expected:\n" + _infoPendingEvents + "\n"
                  + " Found:\n" + _existingPendingEvents);
        }
        final HashMap<String, TableInfo.Column> _columnsAttendanceRecords = new HashMap<String, TableInfo.Column>(13);
        _columnsAttendanceRecords.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceRecords.put("eventType", new TableInfo.Column("eventType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceRecords.put("timestamp", new TableInfo.Column("timestamp", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceRecords.put("dateString", new TableInfo.Column("dateString", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceRecords.put("timeString", new TableInfo.Column("timeString", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceRecords.put("faceVerified", new TableInfo.Column("faceVerified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceRecords.put("gpsVerified", new TableInfo.Column("gpsVerified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceRecords.put("wifiVerified", new TableInfo.Column("wifiVerified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceRecords.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceRecords.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceRecords.put("wifiSsid", new TableInfo.Column("wifiSsid", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceRecords.put("statusMessage", new TableInfo.Column("statusMessage", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceRecords.put("faceImagePath", new TableInfo.Column("faceImagePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAttendanceRecords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAttendanceRecords = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAttendanceRecords = new TableInfo("attendance_records", _columnsAttendanceRecords, _foreignKeysAttendanceRecords, _indicesAttendanceRecords);
        final TableInfo _existingAttendanceRecords = TableInfo.read(db, "attendance_records");
        if (!_infoAttendanceRecords.equals(_existingAttendanceRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "attendance_records(com.example.attendance.data.local.entity.AttendanceRecordEntity).\n"
                  + " Expected:\n" + _infoAttendanceRecords + "\n"
                  + " Found:\n" + _existingAttendanceRecords);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "a7bee08169aa044e500debbb4a264d17", "e1c9abba4b68578053ee94073a7145d6");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "pending_events","attendance_records");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `pending_events`");
      _db.execSQL("DELETE FROM `attendance_records`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(PendingEventDao.class, PendingEventDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AttendanceRecordDao.class, AttendanceRecordDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public PendingEventDao pendingEventDao() {
    if (_pendingEventDao != null) {
      return _pendingEventDao;
    } else {
      synchronized(this) {
        if(_pendingEventDao == null) {
          _pendingEventDao = new PendingEventDao_Impl(this);
        }
        return _pendingEventDao;
      }
    }
  }

  @Override
  public AttendanceRecordDao attendanceRecordDao() {
    if (_attendanceRecordDao != null) {
      return _attendanceRecordDao;
    } else {
      synchronized(this) {
        if(_attendanceRecordDao == null) {
          _attendanceRecordDao = new AttendanceRecordDao_Impl(this);
        }
        return _attendanceRecordDao;
      }
    }
  }
}
