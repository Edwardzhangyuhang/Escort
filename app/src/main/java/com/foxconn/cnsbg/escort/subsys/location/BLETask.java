package com.foxconn.cnsbg.escort.subsys.location;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.foxconn.cnsbg.escort.common.SysPref;
import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;
import com.foxconn.cnsbg.escort.subsys.communication.ComMsgCode;
import com.foxconn.cnsbg.escort.subsys.communication.ComTxTask;
import com.foxconn.cnsbg.escort.subsys.model.BLEData;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class BLETask extends ComTxTask<BLEData> implements BluetoothAdapter.LeScanCallback {
    private static final String TAG = BLETask.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;

    //private List<BLEData.DeviceData> dataList = new ArrayList<BLEData.DeviceData>();
    private long lastUpdateTime = 0L;

    private boolean bleDataUpdated = false;
    private BLEData bleData = new BLEData();

    private static final String bleTopic = SysPref.MQ_TOPIC_BLE_DATA + CtrlCenter.getUDID();

    public BLETask(Context context, ComMQ mq) {
        mContext = context;
        mComMQ = mq;

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            SysUtil.debug(context, "FEATURE_BLUETOOTH_LE is not supported!");
            requestShutdown = true;
            return;
        }

        mBluetoothAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (mBluetoothAdapter == null) {
            SysUtil.debug(context, "BLUETOOTH_SERVICE is not supported!");
            requestShutdown = true;
            return;
        }

        if (!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();

        runInterval = SysPref.BLE_TASK_RUN_INTERVAL;
        setBleScanning(true);
        bleData.location = new BLEData.BLELoc();
        bleData.location.data = new ArrayList<BLEData.DeviceData>() ;
    }

    private void setBleScanning(boolean enable) {

        if (mScanning == enable)
            return;

        mScanning = enable;
        if (enable)
            mBluetoothAdapter.startLeScan(this);
        else
            mBluetoothAdapter.stopLeScan(this);
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        long time = System.currentTimeMillis();
        if (lastUpdateTime == 0)
            lastUpdateTime = time;

        if (!device.getName().contains(SysPref.BLE_DEVICE_NAME_FILTER))
            return;

        //if (rssi < SysPref.BLE_RSSI_THRESHOLD)
            //return;

        BLEData.DeviceData data = new BLEData.DeviceData();
        data.mac = device.getAddress();
        data.rssi = rssi;
        //System.out.println("mac:" + data.mac + "  rssi:" + rssi);
        bleData.location.data.add(data);



        //if (time - lastUpdateTime > SysPref.BLE_UPDATE_MIN_TIME) {
            lastUpdateTime = time;

            //BLEData.DeviceData result = analyse(dataList);


            bleData.device_id = CtrlCenter.getUDID();
            bleData.time = new Date();
            /*
            bleData.battery_level = SysUtil.getBatteryLevel(mContext);
            bleData.signal_strength = SysUtil.getSignalStrength(mContext);
            bleData.voltage_level = SerialStatus.getVoltageLevel();
            bleData.lock_status = SerialStatus.getStatusStr(ComMsgCode.TargetType.MCU_LOCK_STATUS);
            bleData.door_status = SerialStatus.getStatusStr(ComMsgCode.TargetType.MCU_DOOR_STATUS);
            */
            /*bleData.location = new BLEData.BLELoc();
            bleData.location.data = new BLEData.DeviceData();
            bleData.location.data.mac = result.mac;
            bleData.location.data.rssi = result.rssi;
            */
            //bleDataUpdated = true;
        //}
    }

    public BLEData.DeviceData analyse(List<BLEData.DeviceData> list) {
        Collections.sort(list, new Comparator<BLEData.DeviceData>() {
            @Override
            public int compare(BLEData.DeviceData data1, BLEData.DeviceData data2) {
                return data2.rssi - data1.rssi;
            }
        });

        return list.get(0);
    }

    @Override
    protected BLEData collectData() {
        if (bleDataUpdated) {
            bleDataUpdated = false;
            return bleData;
        }

        return null;
    }

    @Override
    protected boolean sendData(BLEData data) {
        if (data == null)
            return false;

        if (!mComMQ.isConnected())
            return false;
        setBleScanning(false);
        String dataStr = gson.toJson(data, BLEData.class);
        bleData.location.data.clear();
        setBleScanning(true);
        return mComMQ.publish(bleTopic, dataStr, SysPref.MQ_SEND_MAX_TIMEOUT);
    }

    @Override
    protected boolean sendCachedData() {

        List<BLEData> dataList = CtrlCenter.getDao().queryCachedBleData();
        if (dataList == null || dataList.isEmpty())
            return true;

        List<BLEData> sentList = new ArrayList<BLEData>();
        for (BLEData data : dataList) {
            if (sendData(data))
                sentList.add(data);
            else
                break;
        }

        //could delete one by one, bulk deletion is just for convenience
        CtrlCenter.getDao().deleteCachedBleData(sentList);

        return (sentList.size() == dataList.size());

    }

    @Override
    protected void saveCachedData(BLEData data) {

        if (data == null)
            return;

        CtrlCenter.getDao().saveCachedBleData(data);
    }

    @Override
    protected void checkTask() {

        //setBleScanning(false);
        /*
        if (CtrlCenter.isTrackingLocation()) {
            long currentTime = new Date().getTime();
            long motionDetectTime = CtrlCenter.getMotionDetectionTime();

            if (currentTime - motionDetectTime < SysPref.LOC_UPDATE_PAUSE_IDLE_TIME || CtrlCenter.isActiveState())
                setBleScanning(true);
        }*/
        //setBleScanning(true);
        bleDataUpdated = true;
    }
}
