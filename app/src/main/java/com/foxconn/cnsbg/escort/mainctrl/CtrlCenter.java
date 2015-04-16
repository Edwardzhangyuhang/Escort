package com.foxconn.cnsbg.escort.mainctrl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.foxconn.cnsbg.escort.subsys.cache.CacheDao;
import com.foxconn.cnsbg.escort.subsys.common.SysConst;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;
import com.foxconn.cnsbg.escort.subsys.communication.ComTask;
import com.foxconn.cnsbg.escort.subsys.location.BLETask;
import com.foxconn.cnsbg.escort.subsys.location.GPSTask;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialCtrl;
import com.foxconn.cnsbg.escort.subsys.usbserial.USBTask;

import java.util.Date;

public class CtrlCenter {
    private final String TAG = CtrlCenter.class.getSimpleName();

    private static Context mContext;
    private static SharedPreferences mPrefs;
    private static CacheDao mDao;
    private static String UDID;

    private static ComTask mGPSTask;
    private static ComTask mBLETask;
    private static USBTask mUSBTask;

    private static boolean isTrackingLocation = false;
    private static boolean isAppVisible = false;
    private static long motionDetectionTime = new Date().getTime();

    public static Context getContext() {
        return mContext;
    }

    public static SharedPreferences getPrefs() {
        return mPrefs;
    }

    public static CacheDao getDao() {
        return mDao;
    }

    public static String getUDID() {
        return UDID;
    }

    public static boolean isTrackingLocation() {
        return isTrackingLocation;
    }

    public static void setTrackingLocation(boolean track) {
        isTrackingLocation = track;
        return;
    }

    public static boolean isAppVisible() {
        return isAppVisible;
    }

    public static void setAppVisible(boolean visible) {
        isAppVisible = visible;
    }

    public static long getMotionDetectionTime() {
        return motionDetectionTime;
    }

    public static void setMotionDetectionTime(long time) {
        motionDetectionTime = time;
    }

    public CtrlCenter(Context context) {
        try {
            PackageInfo pinfo  = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            System.out.println("Version:" + pinfo.versionName + "-r" + pinfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Can't get version!");
        }

        mContext = context;
        //mPrefs is used for saving settings
        mPrefs =  context.getSharedPreferences(SysConst.APP_PREF_NAME, Context.MODE_PRIVATE);
        //setup database for cache
        mDao = new CacheDao(context, SysConst.APP_DB_NAME);
        //set UDID
        UDID = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (UDID == null) {
            Log.w(TAG, "Can't get UDID!");
        }

        isTrackingLocation = true;

        ComMQ mq = new ComMQ(context);

        mGPSTask = new GPSTask(context, mq);
        mBLETask = new BLETask(context, mq);

        SerialCtrl sc = new SerialCtrl(context);
        mUSBTask = new USBTask(context, sc, mq);

        if (mq.init())
            startTask();
    }

    public void cleanup() {
        stopTask();
        mDao.closeDao();
    }

    private void startTask() {
        mGPSTask.start();
        mBLETask.start();

        mUSBTask.start();
    }

    private void stopTask() {
        mGPSTask.requestShutdown();
        mBLETask.requestShutdown();

        mUSBTask.requestShutdown();

        while (mGPSTask.isAlive()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (mBLETask.isAlive()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (mUSBTask.isAlive()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
