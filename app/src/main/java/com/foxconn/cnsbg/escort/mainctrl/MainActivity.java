package com.foxconn.cnsbg.escort.mainctrl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.foxconn.cnsbg.escort.common.CrashHandler;
import com.foxconn.cnsbg.escort.common.SysPref;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialCtrl;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private SerialCtrl sc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String sdcard = Environment.getExternalStorageDirectory().getPath();
        CrashHandler.getInstance().init(sdcard + "/" + SysPref.APP_CRASH_LOG_FILE);

        String id = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (!TextUtils.isEmpty(id)) {
            startService(new Intent(this, MainService.class));
            startActivity(new Intent(this, SettingsActivity.class));
        }

        finish();
    }
}
