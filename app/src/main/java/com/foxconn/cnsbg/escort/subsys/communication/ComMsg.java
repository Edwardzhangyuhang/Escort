package com.foxconn.cnsbg.escort.subsys.communication;

import com.foxconn.cnsbg.escort.common.SysPref;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.model.AlertMsg;
import com.foxconn.cnsbg.escort.subsys.model.CtrlMsg;
import com.foxconn.cnsbg.escort.subsys.model.RespMsg;
import com.google.gson.Gson;

import java.util.Date;

public class ComMsg {

    public static AlertMsg generateAlertMsg(ComMsgCode.RespAck resp) {
        AlertMsg msg = new AlertMsg();

        msg.device_id = CtrlCenter.getUDID();
        msg.time = new Date();

        msg.alert = new AlertMsg.AlertData();
        msg.alert.type = resp.getTargetTypeStr();

        switch (resp.getAckLevel()) {
            case URGENT:
                msg.alert.level = "urgent";
                break;
            case NORMAL:
            default:
                msg.alert.level = "normal";
                break;
        }

        msg.alert.info = resp.getInfo();

        return msg;
    }

    public static RespMsg generateRespMsg(ComMsgCode.RespAck resp) {
        RespMsg msg = new RespMsg();

        msg.device_id = CtrlCenter.getUDID();
        msg.time = new Date();
        msg.cmd = resp.getCmdStr();
        msg.cmd_id = ComMsgCode.getCmdId();
        msg.result = resp.getResult();
        msg.reason = resp.getInfo();

        ComMsgCode.setCmdId(0);
        return msg;
    }

    private static Gson gson = CtrlCenter.getGson();
    private static final String alertTopic = SysPref.MQ_TOPIC_ALERT + CtrlCenter.getUDID();
    private static final String respTopic = SysPref.MQ_TOPIC_RESPONSE + CtrlCenter.getUDID();

    public static String parseCmdMsg(String msgStr) {
        try {
            CtrlMsg msg = gson.fromJson(msgStr, CtrlMsg.class);
            if (!msg.device_id.equals(CtrlCenter.getUDID()))
                return null;

            ComMsgCode.setCmdId(msg.cmd_id);
            return msg.cmd;
        } catch (Exception e) {
            e.printStackTrace();
        }

        ComMsgCode.setCmdId(0);
        return msgStr;
    }

    public static boolean sendAlertMsg(ComMQ mq, ComMsgCode.RespAck resp, long timeout) {
        if (resp == null)
            return false;

        AlertMsg msg = generateAlertMsg(resp);

        String json = gson.toJson(msg, AlertMsg.class);
        return mq.publish(alertTopic, json, timeout);
    }

    public static boolean sendRespMsg(ComMQ mq, ComMsgCode.RespAck resp, long timeout) {
        if (resp == null)
            return false;

        RespMsg msg = generateRespMsg(resp);

        String json = gson.toJson(msg, RespMsg.class);
        return mq.publish(respTopic, json, timeout);
    }
}
