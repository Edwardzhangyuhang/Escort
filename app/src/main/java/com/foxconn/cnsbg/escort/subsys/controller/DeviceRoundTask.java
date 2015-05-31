package com.foxconn.cnsbg.escort.subsys.controller;

import android.content.Context;

import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;
import com.foxconn.cnsbg.escort.subsys.communication.ComMsg;
import com.foxconn.cnsbg.escort.subsys.communication.ComMsgCode;
import com.foxconn.cnsbg.escort.subsys.communication.ComRxTask;

public class DeviceRoundTask extends Thread {
    private static final String TAG = ComRxTask.class.getSimpleName();

    private boolean requestShutdown = false;

    private Context mContext;
    private ComMQ mComMQ;

    public DeviceRoundTask(Context context, ComMQ mq) {
        mContext = context;
        mComMQ = mq;
    }

    @Override
    public void run() {
        while (!requestShutdown) {
            mComMQ.checkConnection();

            boolean statusChanged;
            ComMsgCode.RespAck resp;

            resp = ComMsgCode.getRespAck(ComMsgCode.ACK_STR_GET_TEMPERATURE_OK);
            int temp = SysUtil.getBatteryTemperature(mContext);
            if (resp != null && temp != 0)
                resp.setInfo(Integer.toString(temp / 10));
            statusChanged = DeviceStatus.setStatus(resp);
            if (statusChanged)
                ComMsg.sendAlertMsg(mComMQ, resp, 500);

            resp = ComMsgCode.getRespAck(ComMsgCode.ACK_STR_GET_BATTERY_OK);
            int battery = SysUtil.getBatteryLevel(mContext);
            if (resp != null)
                resp.setInfo(Integer.toString(battery));
            statusChanged = DeviceStatus.setStatus(resp);
            if (statusChanged)
                ComMsg.sendAlertMsg(mComMQ, resp, 500);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void requestShutdown() {
        requestShutdown = true;
    }
}
