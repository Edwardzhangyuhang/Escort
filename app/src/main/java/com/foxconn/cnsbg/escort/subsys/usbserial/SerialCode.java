package com.foxconn.cnsbg.escort.subsys.usbserial;

public class SerialCode {
    public enum CmdType {
        CMD_TYPE_SELF,
        CMD_TYPE_SERIAL
    }

    public enum AckType {
        ACK_TYPE_NORMAL,
        ACK_TYPE_ALERT
    }

    public static final String CMD_SET_UNLOCK = "kf";
    public static final String ACK_SET_UNLOCK_OK = "A";
    public static final String ACK_SET_UNLOCK_ERROR = "C";

    public static final String CMD_SET_UNLOCK_ONCE = "of";
    public static final String ACK_SET_UNLOCK_ONCE_OK = "B";
    public static final String ACK_SET_UNLOCK_ONCE_ERROR = "D";

    public static final String CMD_SET_LOCK = "on";
    public static final String ACK_SET_LOCK_OK = "E";
    public static final String ACK_SET_LOCK_ERROR = "F";

    public static final String CMD_GET_LOCK_STATUS = "gl";
    public static final String ACK_LOCK_LOCKED = "J";
    public static final String ACK_LOCK_UNLOCKED = "I";

    public static final String CMD_GET_DOOR_STATUS = "gd";
    public static final String ACK_DOOR_OPEN = "G";
    public static final String ACK_DOOR_CLOSED = "K";
    public static final String ACK_DOOR_HACK = "H";

    public static class CmdCode {
        private CmdType mType;
        private String mCode;

        public CmdCode(CmdType type, String code) {
            mType = type;
            mCode = code;
        }

        public CmdType getType() {
            return mType;
        }

        public String getCode() {
            return mCode;
        }
    }

    public static CmdCode getCmdCode(String cmdStr) {
        CmdCode cmdCode;

        if (cmdStr.equals("kf")) {
            cmdCode = new CmdCode(CmdType.CMD_TYPE_SERIAL, CMD_SET_UNLOCK);
        } else if (cmdStr.equals("of")) {
            cmdCode = new CmdCode(CmdType.CMD_TYPE_SERIAL, CMD_SET_UNLOCK_ONCE);
        } else if (cmdStr.equals("on")) {
            cmdCode = new CmdCode(CmdType.CMD_TYPE_SERIAL, CMD_SET_LOCK);
        } else if (cmdStr.equals("gl")) {
            cmdCode = new CmdCode(CmdType.CMD_TYPE_SERIAL, CMD_GET_LOCK_STATUS);
        } else if (cmdStr.equals("gd")) {
            cmdCode = new CmdCode(CmdType.CMD_TYPE_SERIAL, CMD_GET_DOOR_STATUS);
        } else {
            //debug
            if (cmdStr.equals("c"))
                cmdCode = new CmdCode(CmdType.CMD_TYPE_SERIAL, CMD_SET_LOCK);
            else if (cmdStr.equals("o"))
                cmdCode = new CmdCode(CmdType.CMD_TYPE_SERIAL, CMD_SET_UNLOCK);
            else
                cmdCode = null;
        }

        return cmdCode;
    }

    public static class AckResp {
        private AckType mType;
        private String mResp;

        public AckResp(AckType type, String resp) {
            mType = type;
            mResp = resp;
        }

        public AckType getType() {
            return mType;
        }

        public String getResp() {
            return mResp;
        }
    }

    public static AckResp getAckResp(String ackCode) {
        AckResp resp;

        if (ackCode.equals(ACK_SET_UNLOCK_OK)) {
            resp = new AckResp(AckType.ACK_TYPE_NORMAL, "unlock ok");
        } else if (ackCode.equals(ACK_SET_UNLOCK_ERROR)) {
            resp = new AckResp(AckType.ACK_TYPE_NORMAL, "unlock error");
        } else if (ackCode.equals(ACK_SET_UNLOCK_ONCE_OK)) {
            resp = new AckResp(AckType.ACK_TYPE_NORMAL, "unlock once ok");
        } else if (ackCode.equals(ACK_SET_UNLOCK_ONCE_ERROR)) {
            resp = new AckResp(AckType.ACK_TYPE_NORMAL, "unlock once error");
        } else if (ackCode.equals(ACK_SET_LOCK_OK)) {
            resp = new AckResp(AckType.ACK_TYPE_NORMAL, "lock ok");
        } else if (ackCode.equals(ACK_SET_LOCK_ERROR)) {
            resp = new AckResp(AckType.ACK_TYPE_NORMAL, "lock error");
        } else if (ackCode.equals(ACK_LOCK_LOCKED)) {
            resp = new AckResp(AckType.ACK_TYPE_NORMAL, "lock locked");
        } else if (ackCode.equals(ACK_LOCK_UNLOCKED)) {
            resp = new AckResp(AckType.ACK_TYPE_NORMAL, "lock unlocked");
        } else if (ackCode.equals(ACK_DOOR_OPEN)) {
            resp = new AckResp(AckType.ACK_TYPE_NORMAL, "door open");
        } else if (ackCode.equals(ACK_DOOR_CLOSED)) {
            resp = new AckResp(AckType.ACK_TYPE_NORMAL, "door closed");
        } else if (ackCode.equals(ACK_DOOR_HACK)) {
            resp = new AckResp(AckType.ACK_TYPE_NORMAL, "door had been hacked");
        } else {
            resp = null;
        }

        return resp;
    }
}
