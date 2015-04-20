package com.foxconn.cnsbg.escort.mainctrl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import com.foxconn.cnsbg.escort.common.CrashHandler;
import com.foxconn.cnsbg.escort.common.SysConst;
import com.foxconn.cnsbg.escort.subsys.usbserial.UARTLoopbackActivity;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String sdcard = Environment.getExternalStorageDirectory().getPath();
        CrashHandler.getInstance().init(sdcard + SysConst.APP_CRASH_LOG_FILE);

        Intent serviceIntent = new Intent(MainActivity.this, MainService.class);
        startService(serviceIntent);

        startActivity(new Intent(MainActivity.this, UARTLoopbackActivity.class));
        finish();
    }
}
