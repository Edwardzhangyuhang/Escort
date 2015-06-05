package com.foxconn.cnsbg.escort.subsys.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.foxconn.cnsbg.escort.subsys.dao.CachedAlertMsg;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table CACHED_ALERT_MSG.
*/
public class CachedAlertMsgDao extends AbstractDao<CachedAlertMsg, Long> {

    public static final String TABLENAME = "CACHED_ALERT_MSG";

    /**
     * Properties of entity CachedAlertMsg.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DeviceID = new Property(1, String.class, "deviceID", false, "DEVICE_ID");
        public final static Property Time = new Property(2, java.util.Date.class, "time", false, "TIME");
        public final static Property Type = new Property(3, String.class, "type", false, "TYPE");
        public final static Property Level = new Property(4, String.class, "level", false, "LEVEL");
        public final static Property Info = new Property(5, String.class, "info", false, "INFO");
    };


    public CachedAlertMsgDao(DaoConfig config) {
        super(config);
    }
    
    public CachedAlertMsgDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'CACHED_ALERT_MSG' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'DEVICE_ID' TEXT," + // 1: deviceID
                "'TIME' INTEGER," + // 2: time
                "'TYPE' TEXT," + // 3: type
                "'LEVEL' TEXT," + // 4: level
                "'INFO' TEXT);"); // 5: info
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'CACHED_ALERT_MSG'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, CachedAlertMsg entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String deviceID = entity.getDeviceID();
        if (deviceID != null) {
            stmt.bindString(2, deviceID);
        }
 
        java.util.Date time = entity.getTime();
        if (time != null) {
            stmt.bindLong(3, time.getTime());
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(4, type);
        }
 
        String level = entity.getLevel();
        if (level != null) {
            stmt.bindString(5, level);
        }
 
        String info = entity.getInfo();
        if (info != null) {
            stmt.bindString(6, info);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public CachedAlertMsg readEntity(Cursor cursor, int offset) {
        CachedAlertMsg entity = new CachedAlertMsg( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // deviceID
            cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)), // time
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // type
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // level
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // info
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, CachedAlertMsg entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDeviceID(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTime(cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)));
        entity.setType(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setLevel(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setInfo(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(CachedAlertMsg entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(CachedAlertMsg entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
