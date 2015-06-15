package com.foxconn.cnsbg.escort.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.foxconn.cnsbg.escort.R;

public class SysPref {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Constants: can NOT be changed
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String APP_DB_NAME = "escort-db";
    public static final String APP_CRASH_LOG_FILE = "escort_crash_log.txt";

    public static final String APP_SERVICE_DESTROY = "com.foxconn.cnsbg.escort.service.destroy";

    //Debug
    public static String APP_DEBUG_UDID = "";
    public static boolean APP_DEBUG_TOAST = true;
    public static boolean APP_DEBUG_LOG = false;
    public static final String APP_DEBUG_LOG_FILE = "escort_debug_log.txt";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //HTTP Server
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static String HTTP_SERVER_HOST = "61.129.93.20"; //"10.116.57.136"
    public static int HTTP_SERVER_PORT = 80;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //MQ Server
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static String MQ_SERVER_HOST = "61.129.93.20";
    public static int MQ_SERVER_PORT = 1883;
    public static short MQ_KEEP_ALIVE = 20; //seconds
    public static long MQ_CONNECT_ATTEMPTS = -1L;
    public static long MQ_RECONNECT_ATTEMPTS = -1L;
    public static long MQ_RECONNECT_DELAY = 5 * 1000L; //milliseconds
    public static long MQ_RECONNECT_MAX_DELAY = 15 * 1000L; //milliseconds
    public static long MQ_SEND_MAX_TIMEOUT = 5 * 1000L; //milliseconds
    public static long MQ_RECV_MAX_TIMEOUT = 5 * 1000L; //milliseconds
    public static String MQ_TOPIC_GPS_DATA = "data/dev/";
    public static String MQ_TOPIC_BLE_DATA = "data/dev/";
    public static String MQ_TOPIC_COMMAND = "control/dev/";
    public static String MQ_TOPIC_RESPONSE = "response/dev/";
    public static String MQ_TOPIC_ALERT = "alerts/dev/";
    public static String MQ_TOPIC_CONNECTION = "connection/dev/";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Location Task Parameter
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static int LOC_TASK_RUN_INTERVAL = 1000;
    //change these to fine tune the power consumption
    //minimal accuracy
    public static float LOC_MIN_ACCURACY = 31.0F;
    //minimal update distance
    public static float LOC_UPDATE_MIN_DISTANCE = 5.0F;
    //minimal update interval
    public static long LOC_UPDATE_MIN_TIME = 5 * 1000L;
    //interval of try to switch provider
    public static long LOC_PROVIDER_CHECK_TIME = 90 * 1000L;
    //stop location tracking if no motion is detected
    public static long LOC_UPDATE_PAUSE_IDLE_TIME = 300 * 1000L;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //BLE Task Parameter
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static int BLE_TASK_RUN_INTERVAL = 1000;
    public static String BLE_DEVICE_NAME_FILTER = "InFocus";
    public static int BLE_RSSI_THRESHOLD = -90;
    public static long BLE_UPDATE_MIN_TIME = 5 * 1000L;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //MCU Parameter
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static long MCU_HEART_BEAT_TIMEOUT = 4 * 1000L;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Parameter Initialization
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static void init(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String value;

        value = String.valueOf(APP_DEBUG_UDID);
        value = pref.getString(context.getString(R.string.key_debug_id), value);
        APP_DEBUG_UDID = value;

        value = String.valueOf(APP_DEBUG_TOAST);
        value = pref.getString(context.getString(R.string.key_debug_toast), value);
        APP_DEBUG_TOAST = Boolean.parseBoolean(value);

        value = String.valueOf(APP_DEBUG_LOG);
        value = pref.getString(context.getString(R.string.key_debug_log), value);
        APP_DEBUG_LOG = Boolean.parseBoolean(value);

        value = String.valueOf(HTTP_SERVER_HOST);
        value = pref.getString(context.getString(R.string.key_http_host), value);
        HTTP_SERVER_HOST = value;

        value = String.valueOf(HTTP_SERVER_PORT);
        value = pref.getString(context.getString(R.string.key_http_port), value);
        HTTP_SERVER_PORT = Integer.parseInt(value);

        value = String.valueOf(MQ_SERVER_HOST);
        value = pref.getString(context.getString(R.string.key_mq_host), value);
        MQ_SERVER_HOST = value;

        value = String.valueOf(MQ_SERVER_PORT);
        value = pref.getString(context.getString(R.string.key_mq_port), value);
        MQ_SERVER_PORT = Integer.valueOf(value);

        value = String.valueOf(MQ_KEEP_ALIVE);
        value = pref.getString(context.getString(R.string.key_mq_keep_alive), value);
        MQ_KEEP_ALIVE = Short.valueOf(value);

        value = String.valueOf(MQ_CONNECT_ATTEMPTS);
        value = pref.getString(context.getString(R.string.key_mq_connect_attempts), value);
        MQ_CONNECT_ATTEMPTS = Long.valueOf(value);

        value = String.valueOf(MQ_RECONNECT_ATTEMPTS);
        value = pref.getString(context.getString(R.string.key_mq_reconnect_attempts), value);
        MQ_RECONNECT_ATTEMPTS = Long.valueOf(value);

        value = String.valueOf(MQ_RECONNECT_DELAY);
        value = pref.getString(context.getString(R.string.key_mq_reconnect_delay), value);
        MQ_RECONNECT_DELAY = Long.valueOf(value);

        value = String.valueOf(MQ_RECONNECT_MAX_DELAY);
        value = pref.getString(context.getString(R.string.key_mq_reconnect_max_delay), value);
        MQ_RECONNECT_MAX_DELAY = Long.valueOf(value);

        value = String.valueOf(MQ_SEND_MAX_TIMEOUT);
        value = pref.getString(context.getString(R.string.key_mq_send_max_timeout), value);
        MQ_SEND_MAX_TIMEOUT = Long.valueOf(value);

        value = String.valueOf(MQ_RECV_MAX_TIMEOUT);
        value = pref.getString(context.getString(R.string.key_mq_recv_max_timeout), value);
        MQ_RECV_MAX_TIMEOUT = Long.valueOf(value);

        value = String.valueOf(MQ_TOPIC_GPS_DATA);
        value = pref.getString(context.getString(R.string.key_mq_topic_gps_data), value);
        MQ_TOPIC_GPS_DATA = value;

        value = String.valueOf(MQ_TOPIC_BLE_DATA);
        value = pref.getString(context.getString(R.string.key_mq_topic_ble_data), value);
        MQ_TOPIC_BLE_DATA = value;

        value = String.valueOf(MQ_TOPIC_COMMAND);
        value = pref.getString(context.getString(R.string.key_mq_topic_cmd), value);
        MQ_TOPIC_COMMAND = value;

        value = String.valueOf(MQ_TOPIC_RESPONSE);
        value = pref.getString(context.getString(R.string.key_mq_topic_response), value);
        MQ_TOPIC_RESPONSE = value;

        value = String.valueOf(MQ_TOPIC_ALERT);
        value = pref.getString(context.getString(R.string.key_mq_topic_alert), value);
        MQ_TOPIC_ALERT = value;

        value = String.valueOf(MQ_TOPIC_CONNECTION);
        value = pref.getString(context.getString(R.string.key_mq_topic_connection), value);
        MQ_TOPIC_CONNECTION = value;

        value = String.valueOf(LOC_TASK_RUN_INTERVAL);
        value = pref.getString(context.getString(R.string.key_loc_task_run_interval), value);
        LOC_TASK_RUN_INTERVAL = Integer.valueOf(value);

        value = String.valueOf(LOC_MIN_ACCURACY);
        value = pref.getString(context.getString(R.string.key_loc_min_accuracy), value);
        LOC_MIN_ACCURACY = Float.valueOf(value);

        value = String.valueOf(LOC_UPDATE_MIN_DISTANCE);
        value = pref.getString(context.getString(R.string.key_loc_update_min_distance), value);
        LOC_UPDATE_MIN_DISTANCE = Float.valueOf(value);

        value = String.valueOf(LOC_UPDATE_MIN_TIME);
        value = pref.getString(context.getString(R.string.key_loc_update_min_time), value);
        LOC_UPDATE_MIN_TIME = Long.valueOf(value);

        value = String.valueOf(LOC_PROVIDER_CHECK_TIME);
        value = pref.getString(context.getString(R.string.key_loc_provider_check_time), value);
        LOC_PROVIDER_CHECK_TIME = Long.valueOf(value);

        value = String.valueOf(LOC_UPDATE_PAUSE_IDLE_TIME);
        value = pref.getString(context.getString(R.string.key_loc_update_pause_idle_time), value);
        LOC_UPDATE_PAUSE_IDLE_TIME = Long.valueOf(value);

        value = String.valueOf(BLE_TASK_RUN_INTERVAL);
        value = pref.getString(context.getString(R.string.key_ble_task_run_interval), value);
        BLE_TASK_RUN_INTERVAL = Integer.valueOf(value);

        value = String.valueOf(BLE_DEVICE_NAME_FILTER);
        value = pref.getString(context.getString(R.string.key_ble_device_name_filter), value);
        BLE_DEVICE_NAME_FILTER = value;

        value = String.valueOf(BLE_RSSI_THRESHOLD);
        value = pref.getString(context.getString(R.string.key_ble_rssi_threshold), value);
        BLE_RSSI_THRESHOLD = Integer.valueOf(value);

        value = String.valueOf(BLE_UPDATE_MIN_TIME);
        value = pref.getString(context.getString(R.string.key_ble_update_min_time), value);
        BLE_UPDATE_MIN_TIME = Long.valueOf(value);

        value = String.valueOf(MCU_HEART_BEAT_TIMEOUT);
        value = pref.getString(context.getString(R.string.key_mcu_heartbeat_timeout), value);
        MCU_HEART_BEAT_TIMEOUT = Long.valueOf(value);
    }
}
