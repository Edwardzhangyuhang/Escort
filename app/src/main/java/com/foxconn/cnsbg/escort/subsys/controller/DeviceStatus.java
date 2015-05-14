package com.foxconn.cnsbg.escort.subsys.controller;

import android.text.TextUtils;

import com.foxconn.cnsbg.escort.subsys.communication.ComMsgCode;

public class DeviceStatus {
    private static String mTemperature = "";

    public synchronized static void initStatus() {
        mTemperature = "";
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
            default:
                resp = null;
                break;
        }

        return resp;
    }
}
