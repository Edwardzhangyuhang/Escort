package com.foxconn.cnsbg.escort.subsys.usbserial;

import android.content.Context;
import android.widget.Toast;

import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;
import com.foxconn.cnsbg.escort.subsys.communication.ComMsg;
import com.foxconn.cnsbg.escort.subsys.communication.ComMsgCode;

public class SerialMonitorTask extends Thread {
    private static final String TAG = SerialMonitorTask.class.getSimpleName();

    private int runInterval = 1000;
    protected boolean requestShutdown = false;

    private Context mContext;
    private SerialCtrl mSerialCtrl;
    private ComMQ mComMQ;
    private boolean mMCUConfigured = false;
    private int mStatus = 2;

    public SerialMonitorTask(Context context, SerialCtrl sc, ComMQ mq) {
        mContext = context;
        mSerialCtrl = sc;
        mComMQ = mq;
    }

    @Override
    public void run() {
        while (!requestShutdown) {
            int status = mSerialCtrl.open();
            ComMsgCode.RespAck resp = null;

            if (mStatus != status) {
                mStatus = status;

                if (status == 2) {
                    SysUtil.showToast(mContext, "MCU detached!", Toast.LENGTH_SHORT);
                    mMCUConfigured = false;

                    SerialStatus.initStatus();

                    resp = ComMsgCode.getRespAck(ComMsgCode.ACK_STR_MCU_DETACHED);
                } else if (status == 1) {
                    if (!mMCUConfigured) {
                        mSerialCtrl.config(
                                SerialCtrl.BAUD_RATE_9600,
                                SerialCtrl.DATA_BITS_8,
                                SerialCtrl.STOP_BITS_1,
                                SerialCtrl.PARITY_NONE,
                                SerialCtrl.FLOW_CONTROL_NONE);

                        SysUtil.showToast(mContext, "MCU configured!", Toast.LENGTH_SHORT);
                        mMCUConfigured = true;

                        if (CtrlCenter.isActiveState())
                            SerialLedCtrl.setActiveLed(mContext, mSerialCtrl);
                        else
                            SerialLedCtrl.setIdleLed(mContext, mSerialCtrl);

                        resp = ComMsgCode.getRespAck(ComMsgCode.ACK_STR_MCU_ATTACHED);
                    }
                }
            }

            if (status == 1) {
                //trigger serial read task to set status
                SerialStatus.checkStatus(mSerialCtrl);
            }

            try {
                if (resp != null)
                    ComMsg.sendAlertMsg(mComMQ, resp, runInterval);
                else
                    Thread.sleep(runInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void requestShutdown() {
        requestShutdown = true;
    }
}
