package com.foxconn.cnsbg.escort.subsys.communication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.foxconn.cnsbg.escort.common.SysPref;
import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialCtrl;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public final class ComRxTask extends Thread {
    private static final String TAG = ComRxTask.class.getSimpleName();

    private boolean requestShutdown = false;

    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
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
            }
            String msg = mComMQ.receive(SysPref.MQ_RECV_MAX_TIMEOUT);
            String cmd = handleMessage(msg);
            handleCmd(cmd);
        }
    }

    public void requestShutdown() {
        requestShutdown = true;
    }

    private String handleMessage(String msgStr) {
        if (msgStr == null)
            return null;

        try {
            ComMsg.CtrlMsg msg = gson.fromJson(msgStr, ComMsg.CtrlMsg.class);
            if (!msg.device_id.equals(CtrlCenter.getUDID()))
                return null;

            ComMsgCode.setCmdId(msg.cmd_id);
            return msg.cmd;
        } catch (JsonParseException e) {
            Log.w(TAG + ":handleMessage", "JsonParseException");
        } catch (NullPointerException e) {
            Log.w(TAG + ":handleMessage", "NullPointerException");
        }

        ComMsgCode.setCmdId(0);
        return msgStr;
    }

    private boolean handleCmd(String cmdStr) {
        if (cmdStr == null)
            return false;

        ComMsgCode.CtrlCmd cmd = ComMsgCode.getCtrlCmd(cmdStr);
        if (cmd == null)
            return false;

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

        if (cmd.getCmdStr().equals(ComMsgCode.CMD_STR_SET_ACTIVATION)) {
            CtrlCenter.setTrackingLocation(true);
            resp = ComMsgCode.getRespAck(ComMsgCode.ACK_STR_SET_ACTIVATION_OK);
        } else if (cmd.getCmdStr().equals(ComMsgCode.CMD_STR_SET_DEACTIVATION)) {
            CtrlCenter.setTrackingLocation(false);
            resp = ComMsgCode.getRespAck(ComMsgCode.ACK_STR_SET_DEACTIVATION_OK);
        } else {
            return false;
        }

        return ComMsg.sendRespMsg(mComMQ, resp, SysPref.MQ_SEND_MAX_TIMEOUT);
    }
}
