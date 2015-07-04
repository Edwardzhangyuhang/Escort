package com.foxconn.cnsbg.escort.subsys.usbserial;

import android.content.Context;

import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;
import com.foxconn.cnsbg.escort.subsys.communication.ComMsg;
import com.foxconn.cnsbg.escort.subsys.communication.ComMsgCode;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialStatus;

public final class SerialReadTask extends Thread {
    private static final String TAG = SerialReadTask.class.getSimpleName();
    private static final int SERIAL_READ_BUF_SIZE = 1024;

    protected int runInterval = 500;
    protected boolean requestShutdown = false;

    private Context mContext;
    private SerialCtrl mSerialCtrl;
    private ComMQ mComMQ;
    private byte[] mAckBuffer;

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
            if (ack.length() == 0)
                continue;

            //FIXME! Should remove for formal code
            if (ack.length() < 2 || ack.charAt(1) != ':')
                continue;

            if (ack.equals("Z:hb set ok"))
            {
                SysUtil.debug(mContext,"Heartbeat set OK");
                continue;
            }

            String ackCode = ack.substring(0, 1);
            ComMsgCode.RespAck resp = ComMsgCode.getRespAck(ackCode);

            if (resp == null)
                continue;

            if (resp.getAckSource() != null)
                SerialStatus.setRecieve_heartbeat(mContext);

            //set ALARM flag for lock status processing
            if (resp.getAckCode().equals(SerialCode.MCU_CODE_DOOR_ALARM))
                CtrlCenter.setDoorAlarm(true);

            if (resp.getAckSource() == ComMsgCode.AckSource.SET_CMD) {
                if (resp.getResult().equals(ComMsgCode.RespAck.ACK_RESULT_FAIL))
                    SerialLedCtrl.setCmdFailLed(mContext, mSerialCtrl);
                else if (CtrlCenter.isActiveState())
                    SerialLedCtrl.setActiveLed(mContext, mSerialCtrl);
                else
                    SerialLedCtrl.setIdleLed(mContext, mSerialCtrl);
            }

            switch (resp.getAckSource()) {
                case ALERT:
                    ComMsg.sendAlertMsg(mComMQ, resp, runInterval);
                    break;
                case SET_CMD:
                    ComMsg.sendRespMsg(mComMQ, resp, runInterval);
                    break;
                case GET_CMD:
                    boolean statusChanged = SerialStatus.setStatus(resp);
                    if (!statusChanged)
                        break;

                    ComMsg.sendAlertMsg(mComMQ, resp, runInterval);
                    break;
                case HEARTBEAT:
                    mSerialCtrl.write(SerialCode.MCU_CODE_HEARTBEAT_ACK + "\r\n");
                    break;
                default:
                    break;
            }
        }
    }
}
