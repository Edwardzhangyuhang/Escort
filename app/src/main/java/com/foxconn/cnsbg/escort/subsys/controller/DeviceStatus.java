package com.foxconn.cnsbg.escort.subsys.controller;

import android.text.TextUtils;

import com.foxconn.cnsbg.escort.subsys.communication.ComMsgCode;

public class DeviceStatus {
    private static String mTemperature = "";
    private static String mBattery = "";
    private static String mSystemInfo = "";

    public synchronized static void initStatus() {
        mTemperature = "";
        mBattery = "";
        mSystemInfo= "";
    }

    public synchronized static boolean setStatus(ComMsgCode.RespAck resp) {
        ComMsgCode.TargetType type = resp.getTargetType();

        switch (type) {
            case DEV_TEMPERATURE:
                String temperature = resp.getInfo();
                if (temperature.equals(mTemperature))
                    return false;

                mTemperature = temperature;
                return true;
            case DEV_BATTERY:
                String battery = resp.getInfo();
                if (battery.equals(mBattery))
                    return false;

                mBattery = battery;
                return true;
            case DEV_SYSTEM_INFO:
                String systeminfo = resp.getInfo();
                if (systeminfo.equals(mSystemInfo))
                    return false;

                mSystemInfo = systeminfo;
                return true;
            default:
                return false;
        }
    }

    public synchronized static ComMsgCode.RespAck getStatus(ComMsgCode.TargetType type) {
        ComMsgCode.RespAck resp;

        switch (type) {
            case DEV_TEMPERATURE:
                if (TextUtils.isEmpty(mTemperature))
                    resp = ComMsgCode.getRespAck(ComMsgCode.ACK_STR_GET_TEMPERATURE_FAIL);
                else
                    resp = ComMsgCode.getRespAck(ComMsgCode.ACK_STR_GET_TEMPERATURE_OK);

                if (resp != null)
                    resp.setInfo(mTemperature);
                break;
            case DEV_BATTERY:
                if (TextUtils.isEmpty(mBattery))
                    resp = ComMsgCode.getRespAck(ComMsgCode.ACK_STR_GET_BATTERY_FAIL);
                else
                    resp = ComMsgCode.getRespAck(ComMsgCode.ACK_STR_GET_BATTERY_OK);

                if (resp != null)
                    resp.setInfo(mBattery);
                break;
            case DEV_SYSTEM_INFO:
                if (TextUtils.isEmpty(mSystemInfo))
                    resp = ComMsgCode.getRespAck(ComMsgCode.ACK_STR_GET_SYSTEM_INFO_FAIL);
                else
                    resp = ComMsgCode.getRespAck(ComMsgCode.ACK_STR_GET_SYSTEM_INFO_OK);

                if (resp != null)
                    resp.setInfo(mSystemInfo);
                break;

            default:
                resp = null;
                break;
        }

        return resp;
    }
}
