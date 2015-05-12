package com.foxconn.cnsbg.escort.subsys.communication;

import android.content.Context;
import android.widget.Toast;

import com.foxconn.cnsbg.escort.common.SysPref;
import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialCode;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialCtrl;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialStatus;

public final class ComRxTask extends Thread {
    private static final String TAG = ComRxTask.class.getSimpleName();

    private boolean requestShutdown = false;

    private Context mContext;
    private SerialCtrl mSerialCtrl;
    private ComMQ mComMQ;
    private boolean mMQReady = false;

    public ComRxTask(Context context, SerialCtrl sc, ComMQ mq) {
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
                SysUtil.showToast(mContext, "MQ Ready:" + ready, Toast.LENGTH_SHORT);

                //trigger status reporting after connection is back
                if (ready)
                    SerialStatus.initStatus();
            }

            String msgStr = mComMQ.receive(SysPref.MQ_RECV_MAX_TIMEOUT);
            if (msgStr == null)
                continue;

            String cmd = ComMsg.parseCmdMsg(msgStr);
            handleCmd(cmd);
        }
    }

    public void requestShutdown() {
        requestShutdown = true;
    }

    private boolean handleCmd(String cmdStr) {
        if (cmdStr == null)
            return false;

        ComMsgCode.CtrlCmd cmd = ComMsgCode.getCtrlCmd(cmdStr);
        if (cmd == null)
            return false;

        //only "Clear Alarm" command can clear ALARM flag
        if (cmd.getCmdCode().equals(SerialCode.CMD_CODE_SET_CLEAR_ALARM))
            CtrlCenter.setDoorAlarm(false);

        if (cmd.getCmdType() == ComMsgCode.CmdType.GET)
            return handleGetCmd(cmd);
        else if (cmd.getCmdTarget() == ComMsgCode.CmdTarget.SERIAL)
            return handleSerialCmd(cmd);
        else if (cmd.getCmdTarget() == ComMsgCode.CmdTarget.SELF)
            return handleSelfCmd(cmd);

        return false;
    }

    private boolean handleGetCmd(ComMsgCode.CtrlCmd cmd) {
        ComMsgCode.RespAck resp = SerialStatus.getStatus(cmd.getTargetType());

        return ComMsg.sendRespMsg(mComMQ, resp, SysPref.MQ_SEND_MAX_TIMEOUT);
    }

    private boolean handleSerialCmd(ComMsgCode.CtrlCmd cmd) {
        mSerialCtrl.write(cmd.getCmdCode() + "\r\n");
        return true;
    }

    private boolean handleSelfCmd(ComMsgCode.CtrlCmd cmd) {
        ComMsgCode.RespAck resp;
        String ackCode;

        if (cmd.getCmdStr().equals(ComMsgCode.CMD_STR_SET_ACTIVATION)) {
            CtrlCenter.setTrackingLocation(true);
            ackCode = ComMsgCode.ACK_STR_SET_ACTIVATION_OK;
        } else if (cmd.getCmdStr().equals(ComMsgCode.CMD_STR_SET_DEACTIVATION)) {
            CtrlCenter.setTrackingLocation(false);
            ackCode = ComMsgCode.ACK_STR_SET_DEACTIVATION_OK;
        } else {
            return false;
        }

        resp = ComMsgCode.getRespAck(ackCode);
        return ComMsg.sendRespMsg(mComMQ, resp, SysPref.MQ_SEND_MAX_TIMEOUT);
    }
}
