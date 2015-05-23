package com.foxconn.cnsbg.escort.mainctrl;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.foxconn.cnsbg.escort.common.SysPref;
import com.foxconn.cnsbg.escort.subsys.cache.CacheDao;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;
import com.foxconn.cnsbg.escort.subsys.communication.ComRxTask;
import com.foxconn.cnsbg.escort.subsys.communication.ComTxTask;
import com.foxconn.cnsbg.escort.subsys.controller.DeviceRoundTask;
import com.foxconn.cnsbg.escort.subsys.location.AccelTask;
import com.foxconn.cnsbg.escort.subsys.location.BLETask;
import com.foxconn.cnsbg.escort.subsys.location.LocTask;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialCtrl;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialMonitorTask;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialReadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CtrlCenter {
    private static final String TAG = CtrlCenter.class.getSimpleName();

    private static String UDID;
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private static CacheDao mDao;
    private static SerialCtrl mSc;
    private static ComMQ mMQ;

    private static SerialMonitorTask mSerialMonitorTask;
    private static SerialReadTask mSerialReadTask;
    private static DeviceRoundTask mDeviceRoundTask;
    private static ComRxTask mCmdTask;
    private static ComTxTask mAccelTask;
    private static ComTxTask mGPSTask;
    private static ComTxTask mBLETask;

    private static long motionDetectionTime = new Date().getTime();
    private static boolean isTrackingLocation = false;
    private static boolean isDoorAlarm = false;
    private static boolean isActiveState = false;

    public static String getUDID() {
        return UDID;
    }

    public static Gson getGson() {
        return gson;
    }

    public static CacheDao getDao() {
        return mDao;
    }

    public static boolean isTrackingLocation() {
        return isTrackingLocation;
    }

    public static void setTrackingLocation(boolean track) {
        isTrackingLocation = track;
    }

    public static long getMotionDetectionTime() {
        return motionDetectionTime;
    }

    public static void setMotionDetectionTime(long time) {
        motionDetectionTime = time;
    }

    public static boolean isDoorAlarm() {
        return isDoorAlarm;
    }

    public static void setDoorAlarm(boolean alarm) {
        isDoorAlarm = alarm;
    }

    public static boolean isActiveState() {
        return isActiveState;
    }

    public static void setActiveState(boolean state) {
        isActiveState = state;
    }

    public CtrlCenter(Context context) {
        SysPref.init(context);

        UDID = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (TextUtils.isEmpty(UDID)) {
            Log.e(TAG, "Can't get UDID, exiting...");
            return;
        }

        if (!TextUtils.isEmpty(SysPref.APP_DEBUG_UDID))
            UDID = SysPref.APP_DEBUG_UDID;

        //setup database for cache
        mDao = new CacheDao(context, SysPref.APP_DB_NAME);
        mSc = new SerialCtrl(context);
        mMQ = new ComMQ(context);

        mSerialMonitorTask = new SerialMonitorTask(context, mSc, mMQ);
        mSerialReadTask = new SerialReadTask(context, mSc, mMQ);
        mDeviceRoundTask = new DeviceRoundTask(context, mMQ);
        mCmdTask = new ComRxTask(context, mSc, mMQ);
        mAccelTask = new AccelTask(context);
        mGPSTask = new LocTask(context, mMQ);
        mBLETask = new BLETask(context, mMQ);

        List<String> subscribes = new ArrayList<String>();
        subscribes.add(SysPref.MQ_TOPIC_COMMAND + UDID);

        // debug control
        if (!TextUtils.isEmpty(SysPref.APP_DEBUG_UDID) && !SysPref.APP_DEBUG_UDID.equals(UDID))
            subscribes.add(SysPref.MQ_TOPIC_COMMAND + SysPref.APP_DEBUG_UDID);

        if (mMQ.init(subscribes)) {
            setTrackingLocation(true);
            startTask();
        }
    }

    public void cleanup() {
        stopTask();
        mSc.close();
        mMQ.disconnect();
        mDao.closeDao();
    }

    private void startTask() {
        mSerialMonitorTask.start();
        mSerialReadTask.start();
        mDeviceRoundTask.start();
        mCmdTask.start();
        mAccelTask.start();
        mGPSTask.start();
        mBLETask.start();
    }

    private void stopTask() {
        mSerialMonitorTask.requestShutdown();
        mSerialReadTask.requestShutdown();
        mDeviceRoundTask.requestShutdown();
        mCmdTask.requestShutdown();
        mAccelTask.requestShutdown();
        mGPSTask.requestShutdown();
        mBLETask.requestShutdown();

        boolean monitorTaskAlive = mSerialMonitorTask.isAlive();
        boolean readTaskAlive = mSerialReadTask.isAlive();
        boolean roundTaskAlive = mDeviceRoundTask.isAlive();
        boolean cmdTaskAlive = mCmdTask.isAlive();
        boolean accelTaskAlive = mAccelTask.isAlive();
        boolean gpsTaskAlive = mGPSTask.isAlive();
        boolean bleTaskAlive = mBLETask.isAlive();

        while (monitorTaskAlive || readTaskAlive || roundTaskAlive
                || cmdTaskAlive || accelTaskAlive
                || gpsTaskAlive || bleTaskAlive) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            monitorTaskAlive = mSerialMonitorTask.isAlive();
            readTaskAlive = mSerialReadTask.isAlive();
            roundTaskAlive = mDeviceRoundTask.isAlive();
            cmdTaskAlive = mCmdTask.isAlive();
            accelTaskAlive = mAccelTask.isAlive();
            gpsTaskAlive = mGPSTask.isAlive();
            bleTaskAlive = mBLETask.isAlive();
        }
    }
}
