package com.foxconn.cnsbg.escort.subsys.communication;

import com.foxconn.cnsbg.escort.subsys.usbserial.SerialCode;

public class CmdCode {
    private static int mCmdId = 0;

    public static void setCmdId(int id) {
        mCmdId = id;
    }

    public static int getCmdId() {
        return mCmdId;
    }

    public static enum CmdTarget {
        SELF,
        SERIAL
    }

    public static enum AckSource {
        SET,
        GET,
        ALERT,
        HEARTBEAT
    }

    public static enum AckType {
        LOCK,
        DOOR,
        MAGNET,
        OTHERS
    }

    public static class CtrlCmd {
        private CmdTarget mTarget;
        private String mCode;

        public CtrlCmd(CmdTarget target, String code) {
            mTarget = target;
            mCode = code;
        }

        public CmdTarget getTarget() {
            return mTarget;
        }

        public String getCode() {
            return mCode;
        }
    }

    public static CtrlCmd getCtrlCmd(String cmdStr) {
        CtrlCmd cmd;

        if (cmdStr.equals("kf")) {
            cmd = new CtrlCmd(CmdTarget.SERIAL, SerialCode.CMD_SET_UNLOCK);
        } else if (cmdStr.equals("of")) {
            cmd = new CtrlCmd(CmdTarget.SERIAL, SerialCode.CMD_SET_UNLOCK_ONCE);
        } else if (cmdStr.equals("on")) {
            cmd = new CtrlCmd(CmdTarget.SERIAL, SerialCode.CMD_SET_LOCK);
        } else if (cmdStr.equals("gl")) {
            cmd = new CtrlCmd(CmdTarget.SERIAL, SerialCode.CMD_GET_LOCK);
        } else if (cmdStr.equals("gd")) {
            cmd = new CtrlCmd(CmdTarget.SERIAL, SerialCode.CMD_GET_DOOR);
        } else if (cmdStr.equals("gm")) {
            cmd = new CtrlCmd(CmdTarget.SERIAL, SerialCode.CMD_GET_MAGNET);
        } else if (cmdStr.equals("ca")) {
            cmd = new CtrlCmd(CmdTarget.SERIAL, SerialCode.CMD_CLEAR_ALARM);
        } else if (cmdStr.startsWith("#")) { //backdoor for debug
            cmd = new CtrlCmd(CmdTarget.SERIAL, cmdStr.substring(1));
        } else {
            cmd = null;
        }

        return cmd;
    }

    public static class RespAck {
        private AckSource mAckSource;
        private AckType mAckType;
        private String mCmd;
        private String mResult;
        private String mInfo;

        public RespAck(AckSource source, AckType type, String cmd, String result, String info) {
            mAckSource = source;
            mAckType = type;
            mCmd = cmd;
            mResult = result;
            mInfo = info;
        }

        public AckSource getAckSource() {
            return mAckSource;
        }

        public AckType getAckType() {
            return mAckType;
        }

        public String getCmd() {
            return mCmd;
        }

        public String getResult() {
            return mResult;
        }

        public String getInfo() {
            return mInfo;
        }
    }

    public static RespAck getRespAck(String ackCode) {
        RespAck ack;

        if (ackCode.equals(SerialCode.ACK_SET_UNLOCK_OK)) {
            ack = new RespAck(AckSource.SET, AckType.LOCK, SerialCode.CMD_SET_UNLOCK, "success", "unlock ok");
        } else if (ackCode.equals(SerialCode.ACK_SET_UNLOCK_ERROR)) {
            ack = new RespAck(AckSource.SET, AckType.LOCK, SerialCode.CMD_SET_UNLOCK, "fail", "unlock error");
        } else if (ackCode.equals(SerialCode.ACK_SET_UNLOCK_ONCE_OK)) {
            ack = new RespAck(AckSource.SET, AckType.LOCK, SerialCode.CMD_SET_UNLOCK_ONCE, "success", "unlock once ok");
        } else if (ackCode.equals(SerialCode.ACK_SET_UNLOCK_ONCE_ERROR)) {
            ack = new RespAck(AckSource.SET, AckType.LOCK, SerialCode.CMD_SET_UNLOCK_ONCE, "fail", "unlock once error");
        } else if (ackCode.equals(SerialCode.ACK_SET_LOCK_OK)) {
            ack = new RespAck(AckSource.SET, AckType.LOCK, SerialCode.CMD_SET_LOCK, "success", "lock ok");
        } else if (ackCode.equals(SerialCode.ACK_SET_LOCK_ERROR)) {
            ack = new RespAck(AckSource.SET, AckType.LOCK, SerialCode.CMD_SET_LOCK, "fail", "lock error");
        } else if (ackCode.equals(SerialCode.ACK_SET_LOCK_ERROR_DOOR_OPEN)) {
            ack = new RespAck(AckSource.SET, AckType.DOOR, SerialCode.CMD_SET_LOCK, "fail", "door is open");
        } else if (ackCode.equals(SerialCode.ACK_SET_LOCK_ERROR_MAGNET_LEAVE)) {
            ack = new RespAck(AckSource.SET, AckType.MAGNET, SerialCode.CMD_SET_LOCK, "fail", "magnet is not detected");
        } else if (ackCode.equals(SerialCode.ACK_LOCK_LOCKED)) {
            ack = new RespAck(AckSource.GET, AckType.LOCK, SerialCode.CMD_GET_LOCK, "success", "locked");
        } else if (ackCode.equals(SerialCode.ACK_LOCK_UNLOCKED)) {
            ack = new RespAck(AckSource.GET, AckType.LOCK, SerialCode.CMD_GET_LOCK, "success", "unlocked");
        } else if (ackCode.equals(SerialCode.ACK_LOCK_NG)) {
            ack = new RespAck(AckSource.ALERT, AckType.LOCK, SerialCode.CMD_GET_LOCK, "success", "status abnormal");
        } else if (ackCode.equals(SerialCode.ACK_DOOR_OPEN)) {
            ack = new RespAck(AckSource.GET, AckType.DOOR, SerialCode.CMD_GET_DOOR, "success", "open");
        } else if (ackCode.equals(SerialCode.ACK_DOOR_CLOSED)) {
            ack = new RespAck(AckSource.GET, AckType.DOOR, SerialCode.CMD_GET_DOOR, "success", "closed");
        } else if (ackCode.equals(SerialCode.ACK_MAGNET_EXIST)) {
            ack = new RespAck(AckSource.GET, AckType.MAGNET, SerialCode.CMD_GET_MAGNET, "success", "magnet is detected");
        } else if (ackCode.equals(SerialCode.ACK_MAGNET_LEAVE)) {
            ack = new RespAck(AckSource.GET, AckType.MAGNET, SerialCode.CMD_GET_MAGNET, "success", "magnet is not detected");
        } else if (ackCode.equals(SerialCode.ACK_CLEAR_ALARM_OK)) {
            ack = new RespAck(AckSource.SET, AckType.OTHERS, SerialCode.CMD_CLEAR_ALARM, "success", "clear alarm ok");
        } else if (ackCode.equals(SerialCode.ACK_DOOR_ALARM)) {
            ack = new RespAck(AckSource.ALERT, AckType.DOOR, "", "success", "door is hacked");
        } else if (ackCode.equals(SerialCode.MCU_HEARTBEAT_REQ)) {
            ack = new RespAck(AckSource.HEARTBEAT, AckType.OTHERS, "", "success", "heartbeat");
        } else {
            ack = null;
        }

        return ack;
    }
}
