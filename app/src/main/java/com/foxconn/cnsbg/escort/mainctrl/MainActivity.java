package com.foxconn.cnsbg.escort.mainctrl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.foxconn.cnsbg.escort.common.CrashHandler;
import com.foxconn.cnsbg.escort.common.SysConst;
import com.foxconn.cnsbg.escort.subsys.usbserial.UARTLoopbackActivity;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CrashHandler.getInstance().init(SysConst.APP_CRASH_LOG_PATH);

        Intent serviceIntent = new Intent(MainActivity.this, MainService.class);
        startService(serviceIntent);

        startActivity(new Intent(MainActivity.this, UARTLoopbackActivity.class));
        finish();
    }
}
