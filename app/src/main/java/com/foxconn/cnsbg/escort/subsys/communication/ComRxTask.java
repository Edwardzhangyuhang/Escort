package com.foxconn.cnsbg.escort.subsys.communication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.foxconn.cnsbg.escort.common.SysPref;
import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialCtrl;
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
            CmdCtrlMsg msg = gson.fromJson(msgStr, CmdCtrlMsg.class);
            if (!msg.device_id.equals(CtrlCenter.getUDID()))
                return null;

            CmdCode.setCmdId(msg.cmd_id);
            return msg.cmd;
        } catch (JsonParseException e) {
            Log.w(TAG + ":handleMessage", "JsonParseException");
        } catch (NullPointerException e) {
            Log.w(TAG + ":handleMessage", "NullPointerException");
        }

        CmdCode.setCmdId(0);
        return msgStr;
    }

    private boolean handleCmd(String cmdStr) {
        if (cmdStr == null)
            return false;

        CmdCode.CtrlCmd cmd = CmdCode.getCtrlCmd(cmdStr);
        if (cmd == null)
            return false;

        switch (cmd.getTarget()) {
            case SERIAL:
                mSerialCtrl.write(cmd.getCode() + "\r\n");
                break;
            default:
                break;
        }

        return true;
    }
}
