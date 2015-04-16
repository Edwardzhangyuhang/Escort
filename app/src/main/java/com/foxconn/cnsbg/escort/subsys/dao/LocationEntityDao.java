package com.foxconn.cnsbg.escort.subsys.dao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table LOCATION_ENTITY.
*/
public class LocationEntityDao extends AbstractDao<LocationEntity, Long> {

    public static final String TABLENAME = "LOCATION_ENTITY";

    /**
     * Properties of entity LocationEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Datetimestamp = new Property(1, Long.class, "datetimestamp", false, "DATETIMESTAMP");
        public final static Property Distance = new Property(2, Float.class, "distance", false, "DISTANCE");
        public final static Property FirstPoint = new Property(3, Boolean.class, "firstPoint", false, "FIRST_POINT");
        public final static Property Latitude = new Property(4, Double.class, "latitude", false, "LATITUDE");
        public final static Property Longitude = new Property(5, Double.class, "longitude", false, "LONGITUDE");
        public final static Property UserId = new Property(6, Long.class, "userId", false, "USER_ID");
    };

    private DaoSession daoSession;

    private Query<LocationEntity> userEntity_LocationsQuery;

    public LocationEntityDao(DaoConfig config) {
        super(config);
    }
    
    public LocationEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'LOCATION_ENTITY' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'DATETIMESTAMP' INTEGER," + // 1: datetimestamp
                "'DISTANCE' REAL," + // 2: distance
                "'FIRST_POINT' INTEGER," + // 3: firstPoint
                "'LATITUDE' REAL," + // 4: latitude
                "'LONGITUDE' REAL," + // 5: longitude
                "'USER_ID' INTEGER);"); // 6: userId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'LOCATION_ENTITY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, LocationEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long datetimestamp = entity.getDatetimestamp();
        if (datetimestamp != null) {
            stmt.bindLong(2, datetimestamp);
        }
 
        Float distance = entity.getDistance();
        if (distance != null) {
            stmt.bindDouble(3, distance);
        }
 
        Boolean firstPoint = entity.getFirstPoint();
        if (firstPoint != null) {
            stmt.bindLong(4, firstPoint ? 1l: 0l);
        }
 
        Double latitude = entity.getLatitude();
        if (latitude != null) {
            stmt.bindDouble(5, latitude);
        }
 
        Double longitude = entity.getLongitude();
        if (longitude != null) {
            stmt.bindDouble(6, longitude);
        }
 
        Long userId = entity.getUserId();
        if (userId != null) {
            stmt.bindLong(7, userId);
        }
    }

    @Override
    protected void attachEntity(LocationEntity entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public LocationEntity readEntity(Cursor cursor, int offset) {
        LocationEntity entity = new LocationEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // datetimestamp
            cursor.isNull(offset + 2) ? null : cursor.getFloat(offset + 2), // distance
            cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0, // firstPoint
            cursor.isNull(offset + 4) ? null : cursor.getDouble(offset + 4), // latitude
            cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5), // longitude
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6) // userId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, LocationEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDatetimestamp(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setDistance(cursor.isNull(offset + 2) ? null : cursor.getFloat(offset + 2));
        entity.setFirstPoint(cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0);
        entity.setLatitude(cursor.isNull(offset + 4) ? null : cursor.getDouble(offset + 4));
        entity.setLongitude(cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5));
        entity.setUserId(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(LocationEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(LocationEntity entity) {
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
    
    /** Internal query to resolve the "locations" to-many relationship of UserEntity. */
    public List<LocationEntity> _queryUserEntity_Locations(Long userId) {
        synchronized (this) {
            if (userEntity_LocationsQuery == null) {
                QueryBuilder<LocationEntity> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.UserId.eq(null));
                queryBuilder.orderRaw("DATETIMESTAMP ASC");
                userEntity_LocationsQuery = queryBuilder.build();
            }
        }
        Query<LocationEntity> query = userEntity_LocationsQuery.forCurrentThread();
        query.setParameter(0, userId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getUserEntityDao().getAllColumns());
            builder.append(" FROM LOCATION_ENTITY T");
            builder.append(" LEFT JOIN USER_ENTITY T0 ON T.'USER_ID'=T0.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected LocationEntity loadCurrentDeep(Cursor cursor, boolean lock) {
        LocationEntity entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        UserEntity user = loadCurrentOther(daoSession.getUserEntityDao(), cursor, offset);
        entity.setUser(user);

        return entity;    
    }

    public LocationEntity loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<LocationEntity> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<LocationEntity> list = new ArrayList<LocationEntity>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<LocationEntity> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<LocationEntity> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}