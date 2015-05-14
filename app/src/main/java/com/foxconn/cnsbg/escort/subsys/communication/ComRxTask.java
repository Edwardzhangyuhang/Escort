package com.foxconn.cnsbg.escort.subsys.communication;

import android.content.Context;

import com.foxconn.cnsbg.escort.common.SysPref;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.controller.DeviceStatus;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialCode;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialCtrl;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialStatus;

public final class ComRxTask extends Thread {
    private static final String TAG = ComRxTask.class.getSimpleName();

    private boolean requestShutdown = false;

    private Context mContext;
    private SerialCtrl mSerialCtrl;
    private ComMQ mComMQ;

    public ComRxTask(Context context, SerialCtrl sc, ComMQ mq) {
        mContext = context;
        mSerialCtrl = sc;
        mComMQ = mq;
    }

    @Override
    public void run() {
        while (!requestShutdown) {
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

        switch (cmd.getCmdTarget()) {
            case MCU:
                return handleSerialCmd(cmd);
            case DEVICE:
                return handleDeviceCmd(cmd);
            default:
                return false;
        }
    }

    private boolean handleSerialCmd(ComMsgCode.CtrlCmd cmd) {
        switch (cmd.getCmdType()) {
            case GET:
                ComMsgCode.RespAck resp = SerialStatus.getStatus(cmd.getTargetType());
                return ComMsg.sendRespMsg(mComMQ, resp, SysPref.MQ_SEND_MAX_TIMEOUT);
            case SET:
                mSerialCtrl.write(cmd.getCmdCode() + "\r\n");
                return true;
            default:
                return false;
        }
    }

    private boolean handleDeviceCmd(ComMsgCode.CtrlCmd cmd) {
        ComMsgCode.RespAck resp;
        String ackCode;

        switch (cmd.getCmdType()) {
            case GET:
                resp = DeviceStatus.getStatus(cmd.getTargetType());
                return ComMsg.sendRespMsg(mComMQ, resp, SysPref.MQ_SEND_MAX_TIMEOUT);
            case SET:
                if (cmd.getCmdStr().equals(ComMsgCode.CMD_STR_SET_ACTIVATION)) {
                    CtrlCenter.setTrackingLocation(true);
                    ackCode = ComMsgCode.ACK_STR_SET_ACTIVATION_OK;
                } else if (cmd.getCmdStr().equals(ComMsgCode.CMD_STR_SET_DEACTIVATION)) {
                    CtrlCenter.setTrackingLocation(false);
                    ackCode = ComMsgCode.ACK_STR_SET_DEACTIVATION_OK;
                } else if (cmd.getCmdStr().equals(ComMsgCode.CMD_STR_SET_TASK_START)) {
                    CtrlCenter.setActiveState(true);
                    ackCode = ComMsgCode.ACK_STR_SET_TASK_START_OK;
                } else if (cmd.getCmdStr().equals(ComMsgCode.CMD_STR_SET_TASK_END)) {
                    CtrlCenter.setActiveState(false);
                    ackCode = ComMsgCode.ACK_STR_SET_TASK_END_OK;
                } else {
                    return false;
                }

                resp = ComMsgCode.getRespAck(ackCode);
                return ComMsg.sendRespMsg(mComMQ, resp, SysPref.MQ_SEND_MAX_TIMEOUT);
            default:
                return false;
        }
    }
}
