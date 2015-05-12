package com.foxconn.cnsbg.escort.subsys.communication;

import android.util.Log;

import com.foxconn.cnsbg.escort.common.SysPref;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.util.Date;

public class ComMsg {
    public static class AlertMsg {
        public String device_id;
        public Date time;
        public AlertData alert;

        public static class AlertData {
            public int type;
            public String level;
            public String info;
        }
    }

    public static class CtrlMsg {
        public String device_id;
        public Date time;
        public String cmd;
        public int cmd_id;
    }

    public static class RespMsg {
        public String device_id;
        public Date time;
        public String cmd;
        public int cmd_id;
        public String result;
        public String reason;
    }

    public static ComMsg.AlertMsg generateAlertMsg(ComMsgCode.RespAck resp) {
        ComMsg.AlertMsg msg = new ComMsg.AlertMsg();

        msg.device_id = CtrlCenter.getUDID();
        msg.time = new Date();

        msg.alert = new ComMsg.AlertMsg.AlertData();
        msg.alert.type = resp.getTargetType().ordinal();

        if (resp.getAckSource() == ComMsgCode.AckSource.ALERT)
            msg.alert.level = "urgent";
        else
            msg.alert.level = "normal";

        msg.alert.info = resp.getInfo();

        return msg;
    }

    public static ComMsg.RespMsg generateRespMsg(ComMsgCode.RespAck resp) {
        ComMsg.RespMsg msg = new ComMsg.RespMsg();

        msg.device_id = CtrlCenter.getUDID();
        msg.time = new Date();
        msg.cmd = resp.getCmdStr();
        msg.cmd_id = ComMsgCode.getCmdId();
        msg.result = resp.getResult();
        msg.reason = resp.getInfo();

        return msg;
    }

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private static final String alertTopic = SysPref.MQ_TOPIC_ALERT + CtrlCenter.getUDID();
    private static final String respTopic = SysPref.MQ_TOPIC_RESPONSE + CtrlCenter.getUDID();

    public static String parseCmdMsg(String msgStr) {
        try {
            CtrlMsg msg = gson.fromJson(msgStr, ComMsg.CtrlMsg.class);
            if (!msg.device_id.equals(CtrlCenter.getUDID()))
                return null;

            ComMsgCode.setCmdId(msg.cmd_id);
            return msg.cmd;
        } catch (JsonParseException e) {
            Log.w("parseCmdMsg:", "JsonParseException");
        } catch (NullPointerException e) {
            Log.w("parseCmdMsg:", "NullPointerException");
        }

        ComMsgCode.setCmdId(0);
        return msgStr;
    }

    public static boolean sendAlertMsg(ComMQ mq, ComMsgCode.RespAck resp, long timeout) {
        AlertMsg msg = generateAlertMsg(resp);

        String json = gson.toJson(msg, AlertMsg.class);
        return mq.publish(alertTopic, json, timeout);
    }

    public static boolean sendRespMsg(ComMQ mq, ComMsgCode.RespAck resp, long timeout) {
        RespMsg msg = generateRespMsg(resp);

        String json = gson.toJson(msg, RespMsg.class);
        return mq.publish(respTopic, json, timeout);
    }
}
