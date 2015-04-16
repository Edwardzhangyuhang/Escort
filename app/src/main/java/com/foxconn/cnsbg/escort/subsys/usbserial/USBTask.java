package com.foxconn.cnsbg.escort.subsys.usbserial;

import android.content.Context;

import com.foxconn.cnsbg.escort.subsys.common.SysConst;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;

public class USBTask extends Thread {
    private final String TAG = USBTask.class.getSimpleName();

    protected int runInterval = 1000; //Default wait time of 1 sec
    protected boolean requestShutdown = false;

    private Context mContext;
    private SerialCtrl mSerialCtrl;
    private ComMQ mComMQ;
    private boolean mConfigured = false;

    public USBTask(Context context, SerialCtrl sc, ComMQ mq) {
        mContext = context;
        mSerialCtrl = sc;
        mComMQ = mq;
    }

    @Override
    public void run() {
        while (!requestShutdown) {
            int status = mSerialCtrl.open();

            if (status == 2) {
                mComMQ.publish(SysConst.MQ_TOPIC_LOCK_DATA, "MCU detached!");
                mConfigured = false;
            } else if (status == 1) {
                if (!mConfigured) {
                    mConfigured = true;
                    mSerialCtrl.config(SerialCtrl.BAUD_RATE_9600, SerialCtrl.DATA_BITS_8, SerialCtrl.STOP_BITS_1,
                            SerialCtrl.PARITY_NONE, SerialCtrl.FLOW_CONTROL_NONE);

                    //////////////////////////debug///////////////////////////
                    byte[] ack = new byte[64];
                    mSerialCtrl.write(SerialCode.CMD_SET_LOCK);
                    for (int i = 0; i < 100; i++) {
                        int num = mSerialCtrl.read(ack, ack.length);
                        System.out.println("i=" + i + ", read " + num + " bytes");
                    }
                    //mSerialCtrl.close();
                }
            }

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
