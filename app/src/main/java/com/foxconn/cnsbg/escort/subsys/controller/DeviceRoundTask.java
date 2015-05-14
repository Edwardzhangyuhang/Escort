package com.foxconn.cnsbg.escort.subsys.controller;

import android.content.Context;
import android.widget.Toast;

import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;
import com.foxconn.cnsbg.escort.subsys.communication.ComMsg;
import com.foxconn.cnsbg.escort.subsys.communication.ComMsgCode;
import com.foxconn.cnsbg.escort.subsys.communication.ComRxTask;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialStatus;

public class DeviceRoundTask extends Thread {
    private static final String TAG = ComRxTask.class.getSimpleName();

    private boolean requestShutdown = false;

    private Context mContext;
    private ComMQ mComMQ;

    private static boolean mMQReady = false;

    public DeviceRoundTask(Context context, ComMQ mq) {
        mContext = context;
        mComMQ = mq;
    }

    @Override
    public void run() {
        while (!requestShutdown) {
            boolean ready = mComMQ.isConnected();
            if (mMQReady != ready) {
                mMQReady = ready;
                SysUtil.showToast(mContext, "MQ Ready:" + ready, Toast.LENGTH_SHORT);

                if (ready) {
                    ComMsg.sendOnlineMsg(mComMQ, 500);

                    //trigger status reporting after connection is back
                    SerialStatus.initStatus();
                    DeviceStatus.initStatus();
                }
            }

            ComMsgCode.RespAck resp = ComMsgCode.getRespAck(ComMsgCode.ACK_STR_GET_TEMPERATURE_OK);
            int temp = SysUtil.getBatteryTemperature(mContext);
            if (resp != null && temp != 0)
                resp.setInfo(Float.toString(temp/10.0F));
            boolean statusChanged = DeviceStatus.setStatus(resp);
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
