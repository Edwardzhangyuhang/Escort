package com.foxconn.cnsbg.escort.subsys.usbserial;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.foxconn.cnsbg.escort.common.SysUtil;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class UsbSerialInterface
{
    private static final String ACTION_USB_PERMISSION = "com.foxconn.cnsbg.escort.USB_PERMISSION";
    private UsbManager usbmanager;
    private UsbSerialDriver mDriver;
    private UsbSerialPort mPort;
    private PendingIntent mPermissionIntent;
    private boolean mPermissionRequestPending = false;
    private boolean accessory_attached = false;
    private Context global_context;

    public UsbSerialInterface(Context context){
        global_context = context;

        usbmanager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
    }

    public void SetConfig(int baud, byte dataBits, byte stopBits,
                          byte parity, byte flowControl)
    {
        if (mPort == null)
            return;

        try {
            mPort.setParameters(baud, dataBits, stopBits, parity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte SendData(int numBytes, byte[] buffer)
    {
        byte status = 0x00; /*success by default*/

        if(numBytes < 1) {
            status = 0x01;
            return status;
        }

        if (mPort == null) {
            status = 0x01;
            return status;
        }

        try {
            if (mPort.write(buffer, 200) < 1)
                status = 0x01;
        } catch (IOException e) {
            status = 0x01;
        }

        return status;
    }

    public byte ReadData(int numBytes,byte[] buffer, int [] actualNumBytes)
    {
        byte status = 0x00;

        if(numBytes < 1){
            actualNumBytes[0] = 0;
            status = 0x01;
            return status;
        }

        if (mPort == null) {
            status = 0x01;
            return status;
        }

        try {
            actualNumBytes[0] = mPort.read(buffer, 200);
        } catch (IOException e) {
            actualNumBytes[0] = 0;
            status = 0x01;
        }

        return status;
    }

    /*resume accessory*/
    public int ResumeAccessory()
    {
        if (mPort != null) {
            return 1;
        }

        HashMap<String, UsbDevice> devices = usbmanager.getDeviceList();
        if (devices == null || devices.isEmpty()) {
            accessory_attached = false;
            return 2;
        }

        // Find all available drivers from attached devices.
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbmanager);
        if (availableDrivers == null || availableDrivers.isEmpty()) {
            accessory_attached = false;
            return 2;
        }

        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        if (driver == null) {
            accessory_attached = false;
            return 2;
        }

        mDriver = driver;
        UsbDevice device = driver.getDevice();
        if (device != null) {
            SysUtil.debug(global_context, device.toString());

            //"Manufacturer, Model & Version are matched!"
            accessory_attached = true;

            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
            global_context.registerReceiver(mUsbReceiver, filter);

            if (usbmanager.hasPermission(device)) {
                OpenAccessory(device);
            }
            else
            {
                synchronized (mUsbReceiver) {
                    if (!mPermissionRequestPending) {
                        SysUtil.debug(global_context, "Request USB Permission");
                        usbmanager.requestPermission(device,
                                mPermissionIntent);
                        mPermissionRequestPending = true;
                    }
                }
            }
        } else {}

        return 0;
    }

    /*destroy accessory*/
    public void DestroyAccessory(boolean bConfiged){
        CloseAccessory();

        if (accessory_attached)
            global_context.unregisterReceiver(mUsbReceiver);
    }

    /*********************helper routines*************************************************/

    private void OpenAccessory(UsbDevice device)
    {
        if (mDriver == null)
            return;

        UsbDeviceConnection connection = usbmanager.openDevice(device);
        if (connection == null)
            return;

        List<UsbSerialPort> ports = mDriver.getPorts();
        if (ports == null || ports.isEmpty())
            return;

        UsbSerialPort port = ports.get(0);
        if (port == null)
            return;

        try {
            port.open(connection);
            mPort = port;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void CloseAccessory()
    {
        if (mPort != null) {
            try {
                mPort.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mPort = null;
        }

        if (mDriver != null)
            mDriver = null;
    }

    /***********USB broadcast receiver*******************************************/
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action))
            {
                synchronized (this)
                {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
                    {
                        SysUtil.debug(global_context, "Allow USB Permission");
                        OpenAccessory(device);
                    }
                    else
                    {
                        SysUtil.debug(global_context, "Deny USB Permission");

                    }
                    mPermissionRequestPending = false;
                }
            }
            else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action))
            {
                SysUtil.debug(global_context, "USB Accessory detached");
                DestroyAccessory(true);
            }else
            {
                SysUtil.debug(global_context, "Rx Action:" + action);
            }
        }
    };
}