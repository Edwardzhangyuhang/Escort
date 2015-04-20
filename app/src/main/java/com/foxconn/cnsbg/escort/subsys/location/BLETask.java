package com.foxconn.cnsbg.escort.subsys.location;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.foxconn.cnsbg.escort.common.SysConst;
import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.communication.ComDataTxTask;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;

import java.util.Arrays;

public class BLETask extends ComDataTxTask implements BluetoothAdapter.LeScanCallback {
    private static final String TAG = BLETask.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;

    private int[] test_rssi = new int[500];
    private String[] test_mac = new String[500];
    private int i = 0;
    private long current_timer;
    private long last_timer;

    private BLEData bleData = new BLEData();

    private static final String bleTopic = SysConst.MQ_TOPIC_BLE_DATA + CtrlCenter.getUDID();

    public BLETask(Context context, ComMQ mq) {
        mContext = context;
        mComMQ = mq;

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            SysUtil.showToast(context, "FEATURE_BLUETOOTH_LE is not supported!", Toast.LENGTH_SHORT);
            requestShutdown = true;
            return;
        }

        mBluetoothAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (mBluetoothAdapter == null) {
            SysUtil.showToast(context, "BLUETOOTH_SERVICE is not supported!", Toast.LENGTH_SHORT);
            requestShutdown = true;
            return;
        }

        if (!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();

        runInterval = SysConst.BLE_TASK_RUN_INTERVAL;

        activeTask();
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        String record = device.getName() + rssi + device.getAddress();

        if( rssi >= -80 )
        {
            last_timer = System.currentTimeMillis();
            if ((last_timer - current_timer ) > 2000 )
            {
                test_mac[i] = device.getAddress();
                test_rssi[i] = rssi;
                current_timer = System.currentTimeMillis();
                analyse(test_mac,test_rssi);
            }
            else
            {
                test_mac[i] = device.getAddress();
                test_rssi[i] = rssi;
                i++;
            }
        }
    }

    private void scanLeDevice(boolean enable) {
        if (mScanning != enable) {
            mScanning = enable;
            if (enable)
                mBluetoothAdapter.startLeScan(this);
            else
                mBluetoothAdapter.stopLeScan(this);
        }
    }

    public void analyse(String[] mac,int[] rssi) {

        float rssi_test;
        String mac_test;
        int num = 1;

        int[][] rssi_i = new int[50][50];
        String[] mac_i = new String[50];
        int j[] = new int[50];

        rssi_i[0][0] = rssi[0];
        mac_i[0] = mac[0];
        j[0] = 1;

        int z;
        while (num <= i) {

            z = 0;

            while (z < 49) {

                //System.out.println(mac_i[z]);
                if (mac_i[z] == null) {
                    mac_i[z] = mac[num];
                    rssi_i[z][0] = rssi[num];
                    j[z]++;
                    break;
                } else if (mac_i[z].equals(mac[num]) == true) {
                    //System.out.println(mac_i[z]);
                    //System.out.println(rssi_i[z]);
                    //System.out.println(rssi[z]);
                    rssi_i[z][j[z]] = rssi[num];
                    //System.out.println(rssi_i[z][j[z]]);
                    j[z]++;
                    //System.out.println(j[z]);
                    break;
                }
                z++;
            }

            num++;
            //String printf =num + "  "+ mac[num] +"  " + rssi[num];
            //System.out.println(printf);
        }

        z = 1;

        rssi_test = average(rssi_i[0], j[0]);

        mac_test = mac_i[0];
        while (z <= 49) {
            if (mac_i[z] != null) {

                if (rssi_test < average(rssi_i[z], j[z])) {
                    mac_test = mac_i[z];
                    rssi_test = average(rssi_i[z], j[z]);
                }
                z++;
            } else
                break;
        }
        i = 0;
        String data = "";
        data = "mac address:" + mac_test + " rssi:" + rssi_test + "dbm";
        sendData(data);
    }

    public float average(int array[],int num){
        int total = 0;
        int i = 0;

        float ave = 0;

        if(num < 2)
        {
            while( i < num)
            {
                total += array[i];
                i++;
            }

            ave = ((float)total)/num ;
            return  ave;
        }
        else
        {

            Arrays.sort(array);

            total = array[num-1] + array[num-2];

            ave = ((float)total)/2 ;

            return  ave;
        }
    }

    @Override
    protected String collectData() {
        return null;
    }

    @Override
    protected boolean sendData(String data) {
        return true;
    }

    @Override
    protected boolean sendCachedData() {
        return true;
    }

    @Override
    protected void saveCachedData(String date) {
    }

    @Override
    protected void checkTask() {
    }

    @Override
    public void activeTask() {
        scanLeDevice(true);
    }

    @Override
    public void deactiveTask() {
        scanLeDevice(false);
    }
}
