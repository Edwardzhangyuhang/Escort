package com.foxconn.cnsbg.escort.subsys.location;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.foxconn.cnsbg.escort.common.SysPref;
import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;
import com.foxconn.cnsbg.escort.subsys.communication.ComMsgCode;
import com.foxconn.cnsbg.escort.subsys.communication.ComTxTask;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocTask extends ComTxTask<LocData> {
    private static final String TAG = LocTask.class.getSimpleName();

    private boolean locUpdating = false;
    private LocationManager locManager;
    private LocationUpdateHandler locHandler;
    private long curMinTime;
    private float curMinDistance;
    private float curAccuracy;
    private String curProvider;
    private long lastProviderCheckTime;
    private boolean isProviderChanged = false;

    private long lastLocTime = 0;

    private boolean locDataUpdated = false;
    private LocData locData = new LocData();

    private static final String gpsTopic = SysPref.MQ_TOPIC_GPS_DATA + CtrlCenter.getUDID();

    public LocTask(Context context, ComMQ mq) {
        mContext = context;
        mComMQ = mq;

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION)) {
            SysUtil.debug(context, "FEATURE_LOCATION is not supported!");
            requestShutdown = true;
            return;
        }

        runInterval = SysPref.LOC_TASK_RUN_INTERVAL;

        setAccuracyLevel(SysPref.LOC_MIN_ACCURACY, SysPref.LOC_UPDATE_MIN_TIME, SysPref.LOC_UPDATE_MIN_DISTANCE);

        //get a handle on the location manager
        locHandler = new LocationUpdateHandler();
        locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        curProvider = LocationManager.NETWORK_PROVIDER;//used to trigger the updating
        curProvider = findAvailableProvider(Criteria.ACCURACY_COARSE);
        lastProviderCheckTime = new Date().getTime();

        //don't setup LocationUpdates at this point
        //locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, curMinTime, curMinDistance, locHandler);
        //locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, curMinTime, curMinDistance, locHandler);

        //Send our last known location to the database
        //List<String> matchingProviders = locManager.getAllProviders();
        //for (String provider : matchingProviders) {
        //    Location location = locManager.getLastKnownLocation(provider);
        //    if (location != null)
        //        handleLastKnownLocation(location);
        //}
    }

    private void handleLastKnownLocation(Location loc) {
        if (loc == null)
            return;

        if (!loc.hasAccuracy())
            return;

        if(loc.getAccuracy() > curAccuracy)
            return;

        lastLocTime = loc.getTime();
        handleLocation(loc);
    }

    private void setLocUpdating(boolean enable) {
        if (locUpdating == enable)
            return;

        locUpdating = enable;
        if (enable) {
            if (curProvider == null) {
                locManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locHandler);
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locHandler);
                locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locHandler);
            } else {
                locManager.requestLocationUpdates(curProvider, curMinTime, curMinDistance, locHandler);
            }
        } else {
            locManager.removeUpdates(locHandler);
        }
    }

    private void setAccuracyLevel(float accuracy, long time, float distance) {
        curAccuracy = accuracy;
        curMinTime = time;
        curMinDistance = distance;
    }

    @Override
    protected LocData collectData() {
        if (locDataUpdated) {
            locDataUpdated = false;
            return locData;
        }

        return null;
    }

    public class LocationUpdateHandler implements LocationListener {
        public void onLocationChanged(Location loc) {
            if (!loc.hasAccuracy()) {
                curProvider = findAvailableProvider(Criteria.ACCURACY_COARSE);
                return;
            }

            //find a provide with better accuracy
            if(loc.getAccuracy() > curAccuracy) {
                curProvider = findAvailableProvider(Criteria.ACCURACY_FINE);
                return;
            }

            handleLocation(loc);

            lastLocTime = loc.getTime();
        }

        public void onProviderDisabled(String provider) {
            Log.i(TAG + ":onProviderDisabled", "Status changed. Provider: " + provider + " Status: Disabled");
            curProvider = findAvailableProvider(Criteria.ACCURACY_COARSE);
        }

        public void onProviderEnabled(String provider) {
            Log.i(TAG + ":onProviderEnabled", "Status changed. Provider: " + provider + " Status: Enabled");
            curProvider = findAvailableProvider(Criteria.ACCURACY_COARSE);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG + ":onStatusChanged", "Status changed. Provider: " + provider + " Status: " + String.valueOf(status));
            curProvider = findAvailableProvider(Criteria.ACCURACY_COARSE);
        }
    }

    private String findAvailableProvider(int accuracy) {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        if (accuracy == Criteria.ACCURACY_FINE)
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
        else
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        String previousProvider = curProvider;
        String provider = locManager.getBestProvider(criteria, true);

        if (previousProvider != null && provider != null) {
            if (!provider.equals(previousProvider))
                isProviderChanged = true;
        } else if (previousProvider != null || provider != null) {
            isProviderChanged = true;
        }

        return provider;
    }

    private void handleLocation(Location loc) {
        if (loc == null)
            return;

        if (loc.getTime() <= 0)
            return;

        locData.device_id = CtrlCenter.getUDID();
        locData.time = new Date(loc.getTime());

        locData.battery_level = SysUtil.getBatteryLevel(mContext);
        locData.signal_strength = SysUtil.getSignalStrength(mContext);
        locData.voltage_level = SerialStatus.getVoltageLevel();
        locData.lock_status = SerialStatus.getStatusStr(ComMsgCode.TargetType.MCU_LOCK_STATUS);
        locData.door_status = SerialStatus.getStatusStr(ComMsgCode.TargetType.MCU_DOOR_STATUS);

        locData.location = new LocData.GPSLoc();
        locData.location.data = new LocData.GPSData();
        locData.location.data.latitude = loc.getLatitude();
        locData.location.data.longitude = loc.getLongitude();

        locData.location.data.provider = loc.getProvider();
        locData.location.data.accuracy = loc.getAccuracy();
        locData.location.data.altitude = loc.getAltitude();
        locData.location.data.bearing = loc.getBearing();
        locData.location.data.speed = loc.getSpeed();
        locData.location.data.mock = loc.isFromMockProvider();

        locDataUpdated = true;
    }

    @Override
    protected void checkTask() {
        if (CtrlCenter.isTrackingLocation()) {
            checkProvider();
        } else {
            isProviderChanged = true;
            setLocUpdating(false);
        }
    }

    private void checkProvider() {
        //not use GPS as far as possible
        long currentTime = new Date().getTime();
        long motionDetectTime = CtrlCenter.getMotionDetectionTime();

        if (currentTime - motionDetectTime > SysPref.LOC_UPDATE_PAUSE_IDLE_TIME
                && !curProvider.equals(LocationManager.PASSIVE_PROVIDER)) {
            System.out.println("Pause location tracking...");
            curProvider = LocationManager.PASSIVE_PROVIDER;
            setLocUpdating(false);
        } else if (motionDetectTime > lastProviderCheckTime
                && currentTime - lastProviderCheckTime > SysPref.LOC_PROVIDER_CHECK_TIME) {
            if (curProvider != null
                    && curProvider.equals(LocationManager.PASSIVE_PROVIDER))
                System.out.println("Resume location tracking...");
            else
                System.out.println("Checking provider...");
            lastProviderCheckTime = currentTime;
            curProvider = findAvailableProvider(Criteria.ACCURACY_COARSE);
        }

        if (isProviderChanged) {
            isProviderChanged = false;
            //Update location provider with less power consumption if possible
            Handler handler = new Handler(mContext.getMainLooper());
            final Runnable providerUpdateThread = new Runnable() {
                public void run() {
                    setLocUpdating(false);
                    setLocUpdating(true);
                }
            };
            handler.post(providerUpdateThread);

            SysUtil.debug(mContext, "Updated Provider:" + curProvider);
        }
    }

    @Override
    protected boolean sendData(LocData data) {
        if (data == null)
            return false;

        String dataStr = gson.toJson(data, LocData.class);
        return mComMQ.publish(gpsTopic, dataStr, SysPref.MQ_SEND_MAX_TIMEOUT);
    }

    @Override
    protected boolean sendCachedData() {
        List<LocData> dataList = CtrlCenter.getDao().queryCachedLocData();
        if (dataList == null || dataList.isEmpty())
            return true;

        List<LocData> sentList = new ArrayList<LocData>();
        for (LocData data : dataList) {
            if (sendData(data))
                sentList.add(data);
            else
                break;
        }

        //could delete one by one, bulk deletion is just for convenience
        CtrlCenter.getDao().deleteCachedLocData(sentList);

        return (sentList.size() == dataList.size());
    }

    @Override
    protected void saveCachedData(LocData data) {
        if (data == null)
            return;

        CtrlCenter.getDao().saveCachedLocData(data);
    }
}
