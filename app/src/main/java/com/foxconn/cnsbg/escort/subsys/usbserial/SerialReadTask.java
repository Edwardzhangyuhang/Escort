package com.foxconn.cnsbg.escort.subsys.usbserial;

import android.content.Context;
import android.widget.Toast;

import com.foxconn.cnsbg.escort.common.SysConst;
import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;

public class SerialReadTask extends Thread {
    private static final String TAG = SerialReadTask.class.getSimpleName();
    private static final int SERIAL_READ_BUF_SIZE = 1024;

    protected int runInterval = 500;
    protected boolean requestShutdown = false;

    private Context mContext;
    private SerialCtrl mSerialCtrl;
    private ComMQ mComMQ;
    private byte[] mAckBuffer;

    private static final String alertTopic = SysConst.MQ_TOPIC_ALERT + CtrlCenter.getUDID();
    private static final String respTopic = SysConst.MQ_TOPIC_RESPONSE + CtrlCenter.getUDID();

    public SerialReadTask(Context context, SerialCtrl sc, ComMQ mq) {
        mContext = context;
        mSerialCtrl = sc;
        mComMQ = mq;
        mAckBuffer = new byte[SERIAL_READ_BUF_SIZE];
    }

    @Override
    public void run() {
        while (!requestShutdown) {
            int num = mSerialCtrl.read(mAckBuffer, mAckBuffer.length);
            if (num > 0)
                parseAck(mAckBuffer, num);

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

    private void parseAck(byte[] ackBytes, int num) {
        String ackStr = new String(ackBytes, 0, num);
        String acks[] = ackStr.split("\r\n");

        for (String ack : acks) {
            SysUtil.showToast(mContext, ack, Toast.LENGTH_SHORT);

            String ackCode = ack.substring(0, 1);
            SerialCode.AckResp resp = SerialCode.getAckResp(ackCode);

            if (resp == null)
                continue;

            if (resp.getType() == SerialCode.AckType.ACK_TYPE_ALERT)
                mComMQ.publish(alertTopic, resp.getResp(), runInterval);
            else
                mComMQ.publish(respTopic, resp.getResp(), runInterval);

            SysUtil.showToast(mContext, resp.getResp(), Toast.LENGTH_SHORT);
        }
    }
}
