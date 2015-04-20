package com.foxconn.cnsbg.escort.common;

public interface SysConst {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //HTTP Server
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String HTTP_SERVER_HOST = "61.129.93.20";
    //public static final String HTTP_SERVER_INTERNAL_HOST = "10.116.57.136";
    public static final int HTTP_SERVER_PORT = 80;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //MQ Server
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String MQ_SERVER_HOST = "61.129.93.20";
    public static final int MQ_SERVER_PORT = 1883;
    public static final short MQ_KEEP_ALIVE = 10; /* seconds */
    public static final long MQ_CONNECT_ATTEMPTS = -1;
    public static final long MQ_RECONNECT_ATTEMPTS = -1;
    public static final long MQ_RECONNECT_DELAY = 5000; /* milliseconds */
    public static final long MQ_RECONNECT_MAX_DELAY = 15000; /* milliseconds */
    public static final long MQ_SEND_MAX_TIMEOUT = 10000; /* milliseconds */
    public static final long MQ_RECV_MAX_TIMEOUT = 10000; /* milliseconds */

    public static final String MQ_TOPIC_GPS_DATA = "data/dev/";
    public static final String MQ_TOPIC_BLE_DATA = "data/dev/";
    public static final String MQ_TOPIC_COMMAND = "control/dev/";
    public static final String MQ_TOPIC_RESPONSE = "response/dev/";
    public static final String MQ_TOPIC_ALERT = "alerts/dev/";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Filename
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String APP_DB_NAME = "escort-db";
    public static final String APP_PREF_NAME = "escort-perf";
    public static final String APP_CRASH_LOG_FILE = "escort_crash_log.txt";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Location Task Parameter
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int LOC_TASK_RUN_INTERVAL = 5000;
    public static final int LOC_ACCURACY_LEVEL = 50;
    //change these to fine tune the power consumption
    public static final float LOC_MIN_ACCURACY = 101.0f;
    public static final float LOC_UPDATE_MIN_DISTANCE = 10.0f;//Only care about updates greater than 10 meters apart
    public static final long LOC_UPDATE_MIN_TIME = 60000L;//Only update every 1min or so.
    public static final long LOC_PROVIDER_CHECK_TIME = 300000L;//try to switch network every 5min
    public static final long LOC_UPDATE_PAUSE_IDLE_TIME = 600000L;//stop location tracking if no motion is detected in 10min

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //BLE Task Parameter
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int BLE_TASK_RUN_INTERVAL = 5000;
}
