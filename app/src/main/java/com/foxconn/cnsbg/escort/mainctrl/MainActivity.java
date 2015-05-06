package com.foxconn.cnsbg.escort.mainctrl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

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

        startService(new Intent(this, MainService.class));

        startActivity(new Intent(this, SettingsActivity.class));
        finish();
    }
}
