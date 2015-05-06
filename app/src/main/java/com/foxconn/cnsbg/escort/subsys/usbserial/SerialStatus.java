package com.foxconn.cnsbg.escort.subsys.usbserial;

public class SerialStatus {
    public static enum VoltageLevel {
        CRITICAL,
        LOW,
        NORMAL
    }

    private static String mLockStatus = "";
    private static String mDoorStatus = "";
    private static String mMagnetStatus = "";
    private static String mBBoxStatus = "";
    private static String mCBoxStatus = "";
    private static String mVoltageLevel = "";

    public static void initStatus() {
        mLockStatus = "";
        mDoorStatus = "";
        mMagnetStatus = "";
        mBBoxStatus = "";
        mCBoxStatus = "";
        mVoltageLevel = "";
    }

    public synchronized static void setLockStatus(String status) {
        mLockStatus = status;
    }

    public synchronized static String getLockStatus() {
        return mLockStatus;
    }

    public synchronized static String getDoorStatus() {
        return mDoorStatus;
    }

    public synchronized static void setDoorStatus(String status) {
        mDoorStatus = status;
    }

    public synchronized static String getMagnetStatus() {
        return mMagnetStatus;
    }

    public synchronized static void setMagnetStatus(String status) {
        mMagnetStatus = status;
    }

    public synchronized static void setBBoxStatus(String status) {
        mBBoxStatus = status;
    }

    public synchronized static String getBBoxStatus() {
        return mBBoxStatus;
    }

    public synchronized static void setCBoxStatus(String status) {
        mCBoxStatus = status;
    }

    public synchronized static String getCBoxStatus() {
        return mCBoxStatus;
    }

    public synchronized static void setVoltageLevel(String level) {
        mVoltageLevel = level;
    }

    public synchronized static String getVoltageLevel() {
        return mVoltageLevel;
    }
}
