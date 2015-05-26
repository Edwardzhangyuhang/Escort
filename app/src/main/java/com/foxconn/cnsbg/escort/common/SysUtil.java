package com.foxconn.cnsbg.escort.common;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Handler;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class SysUtil {
    public static boolean isServerReachable(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo;
        //netInfo could be null if network is switching, so wait up to 5 seconds
        for (int i = 0; i < 5; i++) {
            netInfo = connMgr.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                break;
            }

            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 5; i++) {
            if (checkHttpConnection(10 * 1000))
                return true;

            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean checkHttpConnection(int timeout) {
        try {
            URI uri = new URI("http", null, SysPref.HTTP_SERVER_HOST, SysPref.HTTP_SERVER_PORT, null, null, null);
            URL url = uri.toURL();
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setRequestProperty("User-Agent", "Escort");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(timeout);
            urlc.connect();
            //treat it as OK if no exception is thrown
            //if (urlc.getResponseCode() == HttpURLConnection.HTTP_OK)
            urlc.disconnect();
            return true;
        } catch (MalformedURLException e) {
            Log.w("checkHttpConnection", "MalformedURLException");
        } catch (IOException e) {
            Log.w("checkHttpConnection", "IOException");
        } catch (URISyntaxException e) {
            Log.w("checkHttpConnection", "URISyntaxException:" + e.getReason());
        }

        return false;
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void debug(final Context context, final String text) {
        if (SysPref.APP_DEBUG_TOAST) {
            Handler handler = new Handler(context.getMainLooper());
            final Runnable runnable = new Runnable() {
                public void run() {
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                }
            };
            handler.post(runnable);
        }

        if (SysPref.APP_DEBUG_LOG) {
            String sdcard = Environment.getExternalStorageDirectory().getPath();
            String debugLogPath = sdcard + "/" + SysPref.APP_DEBUG_LOG_FILE;

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(debugLogPath, true);
                PrintStream printStream = new PrintStream(fileOutputStream);
                CharSequence date = DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date());
                printStream.println(date + ":" + text);
                printStream.flush();
                printStream.close();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent == null)
            return 0;

        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if (level == -1 || scale == -1)
            return 0;

        return (int)((float)level / (float)scale * 100.0f);
    }

    public static int getBatteryTemperature(Context context) {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent == null)
            return 0;

        int temperature = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        if (temperature == -1)
            return 0;

        return temperature;
    }

    public static int getSignalStrength(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> allCellInfo = tm.getAllCellInfo();
        if (allCellInfo == null || allCellInfo.isEmpty())
            return 0;

        int dbm = 0;
        for (CellInfo info : allCellInfo) {
            if (info instanceof CellInfoLte) {
                CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                dbm = lte.getDbm();
                break;
            } else if (info instanceof CellInfoWcdma) {
                CellSignalStrengthWcdma wcdma = ((CellInfoWcdma) info).getCellSignalStrength();
                dbm = wcdma.getDbm();
                break;
            } else if (info instanceof CellInfoCdma) {
                CellSignalStrengthCdma cdma = ((CellInfoCdma) info).getCellSignalStrength();
                dbm = cdma.getDbm();
                break;
            } else if (info instanceof CellInfoGsm) {
                CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                dbm = gsm.getDbm();
                break;
            }
        }

        return dbm;
    }
}
