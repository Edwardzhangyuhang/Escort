package com.foxconn.cnsbg.escort.subsys.usbserial;

import android.content.Context;
import android.widget.Toast;

import com.foxconn.cnsbg.escort.common.SysConst;
import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;

public class SerialMonitorTask extends Thread {
    private static final String TAG = SerialMonitorTask.class.getSimpleName();

    protected int runInterval = 1000;
    protected boolean requestShutdown = false;

    private Context mContext;
    private SerialCtrl mSerialCtrl;
    private ComMQ mComMQ;
    private boolean mMCUConfigured = false;
    private int mStatus = 2;

    private static final String alertTopic = SysConst.MQ_TOPIC_ALERT + CtrlCenter.getUDID();

    public SerialMonitorTask(Context context, SerialCtrl sc, ComMQ mq) {
        mContext = context;
        mSerialCtrl = sc;
        mComMQ = mq;
    }

    @Override
    public void run() {
        while (!requestShutdown) {
            int status = mSerialCtrl.open();

            if (mStatus != status) {
                mStatus = status;
                SysUtil.showToast(mContext, "MCU status:" + status, Toast.LENGTH_LONG);

                if (status == 2) {
                    mMCUConfigured = false;
                } else if (status == 1) {
                    if (!mMCUConfigured) {
                        mSerialCtrl.config(
                                SerialCtrl.BAUD_RATE_9600,
                                SerialCtrl.DATA_BITS_8,
                                SerialCtrl.STOP_BITS_1,
                                SerialCtrl.PARITY_NONE,
                                SerialCtrl.FLOW_CONTROL_NONE);

                        SysUtil.showToast(mContext, "MCU configured!", Toast.LENGTH_LONG);
                        mMCUConfigured = true;
                    }
                }
            }

            try {
                //if (status == 2)
                //    mComMQ.publish(alertTopic, "MCU detached!", runInterval);
                //else
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