package com.foxconn.cnsbg.escort.mainctrl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.foxconn.cnsbg.escort.R;
import com.foxconn.cnsbg.escort.common.SysPref;
import com.foxconn.cnsbg.escort.common.SysUtil;

public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    private void setupSimplePreferencesScreen() {
        addPreferencesFromResource(R.xml.pref_sys);
        addPreferencesFromResource(R.xml.pref_http);
        addPreferencesFromResource(R.xml.pref_mq);
        addPreferencesFromResource(R.xml.pref_loc);
        addPreferencesFromResource(R.xml.pref_ble);

        String id = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        setPreference(R.string.key_device_id, id);

        setPreference(R.string.key_http_host, String.valueOf(SysPref.HTTP_SERVER_HOST));
        setPreference(R.string.key_http_port, String.valueOf(SysPref.HTTP_SERVER_PORT));

        setPreference(R.string.key_mq_host, String.valueOf(SysPref.MQ_SERVER_HOST));
        setPreference(R.string.key_mq_port, String.valueOf(SysPref.MQ_SERVER_PORT));
        setPreference(R.string.key_mq_keep_alive, String.valueOf(SysPref.MQ_KEEP_ALIVE));
        setPreference(R.string.key_mq_connect_attempts, String.valueOf(SysPref.MQ_CONNECT_ATTEMPTS));
        setPreference(R.string.key_mq_reconnect_attempts, String.valueOf(SysPref.MQ_RECONNECT_ATTEMPTS));
        setPreference(R.string.key_mq_reconnect_delay, String.valueOf(SysPref.MQ_RECONNECT_DELAY));
        setPreference(R.string.key_mq_reconnect_max_delay, String.valueOf(SysPref.MQ_RECONNECT_MAX_DELAY));
        setPreference(R.string.key_mq_send_max_timeout, String.valueOf(SysPref.MQ_SEND_MAX_TIMEOUT));
        setPreference(R.string.key_mq_recv_max_timeout, String.valueOf(SysPref.MQ_RECV_MAX_TIMEOUT));

        setPreference(R.string.key_mq_topic_gps_data, String.valueOf(SysPref.MQ_TOPIC_GPS_DATA));
        setPreference(R.string.key_mq_topic_ble_data, String.valueOf(SysPref.MQ_TOPIC_BLE_DATA));
        setPreference(R.string.key_mq_topic_cmd, String.valueOf(SysPref.MQ_TOPIC_COMMAND));
        setPreference(R.string.key_mq_topic_response, String.valueOf(SysPref.MQ_TOPIC_RESPONSE));
        setPreference(R.string.key_mq_topic_alert, String.valueOf(SysPref.MQ_TOPIC_ALERT));

        setPreference(R.string.key_loc_task_run_interval, String.valueOf(SysPref.LOC_TASK_RUN_INTERVAL));
        setPreference(R.string.key_loc_min_accuracy, String.valueOf(SysPref.LOC_MIN_ACCURACY));
        setPreference(R.string.key_loc_update_min_distance, String.valueOf(SysPref.LOC_UPDATE_MIN_DISTANCE));
        setPreference(R.string.key_loc_update_min_time, String.valueOf(SysPref.LOC_UPDATE_MIN_TIME));
        setPreference(R.string.key_loc_provider_check_time, String.valueOf(SysPref.LOC_PROVIDER_CHECK_TIME));
        setPreference(R.string.key_loc_update_pause_idle_time, String.valueOf(SysPref.LOC_UPDATE_PAUSE_IDLE_TIME));

        setPreference(R.string.key_ble_task_run_interval, String.valueOf(SysPref.BLE_TASK_RUN_INTERVAL));
        setPreference(R.string.key_ble_device_name_filter, String.valueOf(SysPref.BLE_DEVICE_NAME_FILTER));
        setPreference(R.string.key_ble_rssi_threshold, String.valueOf(SysPref.BLE_RSSI_THRESHOLD));
        setPreference(R.string.key_ble_update_min_time, String.valueOf(SysPref.BLE_UPDATE_MIN_TIME));
    }

    private void setPreference(int keyId, String defaults) {
        EditTextPreference pref = (EditTextPreference) findPreference(getString(keyId));
        if (pref == null)
            return;

        String value = defaults;

        if (pref.isEnabled()) {
            value = pref.getText();

            if (TextUtils.isEmpty(value)) {
                pref.setText(defaults);
                value = defaults;
            }
        }

        pref.setSummary(value);
        pref.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        EditTextPreference pref = (EditTextPreference) preference;
        String value = String.valueOf(o);

        pref.setSummary(value);

        restart_service();
        return true;
    }

    private void restart_service() {
        Intent serviceIntent = new Intent(this, MainService.class);

        if (SysUtil.isServiceRunning(this, MainService.class))
            stopService(serviceIntent);

        startService(serviceIntent);
    }
}
