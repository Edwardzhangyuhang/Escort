package com.foxconn.cnsbg.escort.subsys.communication;

import android.content.Context;
import android.widget.Toast;

import com.foxconn.cnsbg.escort.common.SysConst;
import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialCode;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialCtrl;

public class ComCmdRxTask extends Thread {
    private static final String TAG = ComCmdRxTask.class.getSimpleName();

    protected boolean requestShutdown = false;

    private Context mContext;
    private SerialCtrl mSerialCtrl;
    private ComMQ mComMQ;
    private boolean mMQReady = false;

    public ComCmdRxTask(Context context, SerialCtrl sc, ComMQ mq) {
        mContext = context;
        mSerialCtrl = sc;
        mComMQ = mq;
    }

    @Override
    public void run() {
        while (!requestShutdown) {
            boolean ready = mComMQ.isConnected();
            if (mMQReady != ready) {
                mMQReady = ready;
                SysUtil.showToast(mContext, "MQ Ready:" + ready, Toast.LENGTH_LONG);
            }
            String cmd = mComMQ.receive(SysConst.MQ_RECV_MAX_TIMEOUT);
            handleCmd(cmd);
        }
    }

    public void requestShutdown() {
        requestShutdown = true;
    }

    private boolean handleCmd(String cmd) {
        if (cmd == null)
            return false;

        SysUtil.showToast(mContext, "MQ Rx:" + cmd, Toast.LENGTH_SHORT);

        String cmdCode;

        if (cmd.equalsIgnoreCase("c"))
            cmdCode = SerialCode.CMD_SET_LOCK;
        else if (cmd.equalsIgnoreCase("o"))
            cmdCode = SerialCode.CMD_SET_UNLOCK;
        else
            cmdCode = SerialCode.CMD_NOOP;

        byte[] ack = new byte[64];
        mSerialCtrl.write(cmdCode);
        for (int i = 0; i < 100; i++) {
            int num = mSerialCtrl.read(ack, ack.length);
            System.out.println("i=" + i + ", read " + num + " bytes");
        }
        //mSerialCtrl.close();

        return true;
    }
}
