package com.foxconn.cnsbg.escort.subsys.usbserial;

import android.content.Context;

import com.foxconn.cnsbg.escort.common.SysConst;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;

public class SerialReadTask extends Thread {
    private static final String TAG = SerialReadTask.class.getSimpleName();

    protected int runInterval = 1000;
    protected boolean requestShutdown = false;

    private Context mContext;
    private SerialCtrl mSerialCtrl;
    private ComMQ mComMQ;

    private static final String alertTopic = SysConst.MQ_TOPIC_ALERT + CtrlCenter.getUDID();
    private static final String respTopic = SysConst.MQ_TOPIC_RESPONSE + CtrlCenter.getUDID();

    public SerialReadTask(Context context, SerialCtrl sc, ComMQ mq) {
        mContext = context;
        mSerialCtrl = sc;
        mComMQ = mq;
    }

    @Override
    public void run() {
        while (!requestShutdown) {
            try {
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
