package com.foxconn.cnsbg.escort.subsys.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.foxconn.cnsbg.escort.subsys.dao.CachedAlertMsg;
import com.foxconn.cnsbg.escort.subsys.dao.CachedAlertMsgDao;
import com.foxconn.cnsbg.escort.subsys.dao.CachedBleData;
import com.foxconn.cnsbg.escort.subsys.dao.CachedBleDataDao;
import com.foxconn.cnsbg.escort.subsys.dao.CachedLocData;
import com.foxconn.cnsbg.escort.subsys.dao.CachedLocDataDao;
import com.foxconn.cnsbg.escort.subsys.dao.DaoMaster;
import com.foxconn.cnsbg.escort.subsys.dao.DaoMaster.DevOpenHelper;
import com.foxconn.cnsbg.escort.subsys.dao.DaoSession;
import com.foxconn.cnsbg.escort.subsys.model.AlertMsg;
import com.foxconn.cnsbg.escort.subsys.model.BLEData;
import com.foxconn.cnsbg.escort.subsys.model.LocData;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.Query;

public class CacheDao {
    public static final int QUERY_LIMIT = 5000;

    private SQLiteDatabase db;

    private CachedLocDataDao cachedLocDao;
    private CachedBleDataDao cachedBleDao;
    private CachedAlertMsgDao cachedAlertDao;

    private Query<CachedLocData> cachedLocQuery;
    private Query<CachedBleData> cachedBleQuery;
    private Query<CachedAlertMsg> cachedAlertQuery;

    public CacheDao(Context appContext, String dbName) {
        closeDao();

        DevOpenHelper helper = new DaoMaster.DevOpenHelper(appContext, dbName, null);
        db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        cachedLocDao = daoSession.getCachedLocDataDao();
        cachedBleDao = daoSession.getCachedBleDataDao();
        cachedAlertDao = daoSession.getCachedAlertMsgDao();

        cachedLocQuery = cachedLocDao.queryBuilder().limit(QUERY_LIMIT).offset(0).build();
        cachedBleQuery = cachedBleDao.queryBuilder().limit(QUERY_LIMIT).offset(0).build();
        cachedAlertQuery = cachedAlertDao.queryBuilder().limit(QUERY_LIMIT).offset(0).build();
    }

    public void closeDao() {
        if (db != null && db.isOpen())
            db.close();
    }

    public synchronized List<LocData> queryCachedLocData() {
        List<LocData> cachedList = new ArrayList<LocData>();
        Query<CachedLocData> query = cachedLocQuery.forCurrentThread();
        int offset = 0;

        query.setOffset(offset);
        List<CachedLocData> queryList = query.list();
        while (queryList != null && !queryList.isEmpty()) {
            for (CachedLocData data : queryList) {
                LocData newData = new LocData();
                newData.device_id = data.getDeviceID();
                newData.time = data.getTime();
                newData.battery_level = data.getBatteryLevel();
                newData.signal_strength = data.getSignalStrength();
                newData.lock_status = data.getLockStatus();
                newData.door_status = data.getDoorStatus();

                newData.location = new LocData.GPSLoc();
                newData.location.data = new LocData.GPSData();
                newData.location.data.latitude = data.getLatitude();
                newData.location.data.longitude = data.getLongitude();

                newData.location.data.provider = data.getProvider();
                newData.location.data.accuracy = data.getAccuracy();
                newData.location.data.altitude = data.getAltitude();
                newData.location.data.bearing = data.getBearing();
                newData.location.data.speed = data.getSpeed();
                newData.location.data.mock = data.getMock();

                cachedList.add(newData);
            }

            offset += QUERY_LIMIT;
            query.setOffset(offset);
            queryList = query.list();
        }

        return cachedList;
    }

    public synchronized void saveCachedLocData(LocData data) {
        if (data == null)
            return;

        CachedLocData newData = new CachedLocData();

        newData.setDeviceID(data.device_id);
        newData.setTime(data.time);
        newData.setBatteryLevel(data.battery_level);
        newData.setSignalStrength(data.signal_strength);
        newData.setLockStatus(data.lock_status);
        newData.setDoorStatus(data.door_status);
        newData.setLongitude(data.location.data.longitude);
        newData.setLatitude(data.location.data.latitude);

        newData.setProvider(data.location.data.provider);
        newData.setAccuracy(data.location.data.accuracy);
        newData.setAltitude(data.location.data.altitude);
        newData.setBearing(data.location.data.bearing);
        newData.setSpeed(data.location.data.speed);
        newData.setMock(data.location.data.mock);

        cachedLocDao.insert(newData);
    }

    public synchronized void deleteCachedLocData(List<LocData> data) {
        if (data == null || data.isEmpty())
            return;

        List<CachedLocData> cachedList = new ArrayList<CachedLocData>();
        Query<CachedLocData> query = cachedLocQuery.forCurrentThread();
        int offset = 0;

        query.setOffset(offset);
        List<CachedLocData> queryList = query.list();
        while (queryList != null && !queryList.isEmpty()) {
            cachedList.addAll(queryList);

            offset += QUERY_LIMIT;
            query.setOffset(offset);
            queryList = query.list();
        }

        if (cachedList.isEmpty())
            return;

        List<CachedLocData> deleteList = new ArrayList<CachedLocData>();
        for (CachedLocData cachedData : cachedList) {
            LocData deleteData = new LocData();
            deleteData.device_id = cachedData.getDeviceID();
            deleteData.time = cachedData.getTime();
            deleteData.battery_level = cachedData.getBatteryLevel();
            deleteData.signal_strength = cachedData.getSignalStrength();
            deleteData.lock_status = cachedData.getLockStatus();
            deleteData.door_status = cachedData.getDoorStatus();

            deleteData.location = new LocData.GPSLoc();
            deleteData.location.data = new LocData.GPSData();
            deleteData.location.data.latitude = cachedData.getLatitude();
            deleteData.location.data.longitude = cachedData.getLongitude();

            deleteData.location.data.provider = cachedData.getProvider();
            deleteData.location.data.accuracy = cachedData.getAccuracy();
            deleteData.location.data.altitude = cachedData.getAltitude();
            deleteData.location.data.bearing = cachedData.getBearing();
            deleteData.location.data.speed = cachedData.getSpeed();
            deleteData.location.data.mock = cachedData.getMock();

            //to avoid ConcurrentModificationException
            if (data.contains(deleteData))
                deleteList.add(cachedData);
        }

        for (CachedLocData delData : deleteList)
            cachedLocDao.delete(delData);
    }

    public synchronized List<BLEData> queryCachedBleData() {
        List<BLEData> cachedList = new ArrayList<BLEData>();
        Query<CachedBleData> query = cachedBleQuery.forCurrentThread();
        int offset = 0;

        query.setOffset(offset);
        List<CachedBleData> queryList = query.list();
        while (queryList != null && !queryList.isEmpty()) {
            for (CachedBleData data : queryList) {
                BLEData newData = new BLEData();
                newData.device_id = data.getDeviceID();
                newData.time = data.getTime();
                newData.battery_level = data.getBatteryLevel();
                newData.signal_strength = data.getSignalStrength();
                newData.lock_status = data.getLockStatus();
                newData.door_status = data.getDoorStatus();

                newData.location = new BLEData.BLELoc();
                newData.location.data = new BLEData.DeviceData();
                newData.location.data.mac = data.getMac();
                newData.location.data.rssi = data.getRssi();

                cachedList.add(newData);
            }

            offset += QUERY_LIMIT;
            query.setOffset(offset);
            queryList = query.list();
        }

        return cachedList;
    }

    public synchronized void saveCachedBleData(BLEData data) {
        if (data == null)
            return;

        CachedBleData newData = new CachedBleData();

        newData.setDeviceID(data.device_id);
        newData.setTime(data.time);
        newData.setBatteryLevel(data.battery_level);
        newData.setSignalStrength(data.signal_strength);
        newData.setLockStatus(data.lock_status);
        newData.setDoorStatus(data.door_status);
        newData.setMac(data.location.data.mac);
        newData.setRssi(data.location.data.rssi);

        cachedBleDao.insert(newData);
    }

    public synchronized void deleteCachedBleData(List<BLEData> data) {
        if (data == null || data.isEmpty())
            return;

        List<CachedBleData> cachedList = new ArrayList<CachedBleData>();
        Query<CachedBleData> query = cachedBleQuery.forCurrentThread();
        int offset = 0;

        query.setOffset(offset);
        List<CachedBleData> queryList = query.list();
        while (queryList != null && !queryList.isEmpty()) {
            cachedList.addAll(queryList);

            offset += QUERY_LIMIT;
            query.setOffset(offset);
            queryList = query.list();
        }

        if (cachedList.isEmpty())
            return;

        List<CachedBleData> deleteList = new ArrayList<CachedBleData>();
        for (CachedBleData cachedData : cachedList) {
            BLEData deleteData = new BLEData();
            deleteData.device_id = cachedData.getDeviceID();
            deleteData.time = cachedData.getTime();
            deleteData.battery_level = cachedData.getBatteryLevel();
            deleteData.signal_strength = cachedData.getSignalStrength();
            deleteData.lock_status = cachedData.getLockStatus();
            deleteData.door_status = cachedData.getDoorStatus();

            deleteData.location = new BLEData.BLELoc();
            deleteData.location.data = new BLEData.DeviceData();
            deleteData.location.data.mac = cachedData.getMac();
            deleteData.location.data.rssi = cachedData.getRssi();

            //to avoid ConcurrentModificationException
            if (data.contains(deleteData))
                deleteList.add(cachedData);
        }

        for (CachedBleData delData : deleteList)
            cachedBleDao.delete(delData);
    }

    public synchronized List<AlertMsg> queryCachedAlertMsg() {
        List<AlertMsg> cachedList = new ArrayList<AlertMsg>();
        Query<CachedAlertMsg> query = cachedAlertQuery.forCurrentThread();
        int offset = 0;

        query.setOffset(offset);
        List<CachedAlertMsg> queryList = query.list();
        while (queryList != null && !queryList.isEmpty()) {
            for (CachedAlertMsg data : queryList) {
                AlertMsg newData = new AlertMsg();
                newData.device_id = data.getDeviceID();
                newData.time = data.getTime();

                newData.alert = new AlertMsg.AlertData();
                newData.alert.type = data.getType();
                newData.alert.level = data.getLevel();
                newData.alert.info = data.getInfo();

                cachedList.add(newData);
            }

            offset += QUERY_LIMIT;
            query.setOffset(offset);
            queryList = query.list();
        }

        return cachedList;
    }

    public synchronized void saveCachedAlertMsg(AlertMsg data) {
        if (data == null)
            return;

        CachedAlertMsg newData = new CachedAlertMsg();

        newData.setDeviceID(data.device_id);
        newData.setTime(data.time);
        newData.setType(data.alert.type);
        newData.setLevel(data.alert.level);
        newData.setInfo(data.alert.info);

        cachedAlertDao.insert(newData);
    }

    public synchronized void deleteCachedAlertMsg(List<AlertMsg> data) {
        if (data == null || data.isEmpty())
            return;

        List<CachedAlertMsg> cachedList = new ArrayList<CachedAlertMsg>();
        Query<CachedAlertMsg> query = cachedAlertQuery.forCurrentThread();
        int offset = 0;

        query.setOffset(offset);
        List<CachedAlertMsg> queryList = query.list();
        while (queryList != null && !queryList.isEmpty()) {
            cachedList.addAll(queryList);

            offset += QUERY_LIMIT;
            query.setOffset(offset);
            queryList = query.list();
        }

        if (cachedList.isEmpty())
            return;

        List<CachedAlertMsg> deleteList = new ArrayList<CachedAlertMsg>();
        for (CachedAlertMsg cachedData : cachedList) {
            AlertMsg deleteData = new AlertMsg();
            deleteData.device_id = cachedData.getDeviceID();
            deleteData.time = cachedData.getTime();

            deleteData.alert = new AlertMsg.AlertData();
            deleteData.alert.type = cachedData.getType();
            deleteData.alert.level = cachedData.getLevel();
            deleteData.alert.info = cachedData.getInfo();

            //to avoid ConcurrentModificationException
            if (data.contains(deleteData))
                deleteList.add(cachedData);
        }

        for (CachedAlertMsg delData : deleteList)
            cachedAlertDao.delete(delData);
    }
}
