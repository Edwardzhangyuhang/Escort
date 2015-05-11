package com.foxconn.cnsbg.escort.subsys.communication;

import com.foxconn.cnsbg.escort.subsys.usbserial.SerialCode;

public class ComMsgCode {
    //Commands to Self
    public static final String CMD_STR_SET_ACTIVATION = "sa";
    public static final String ACK_STR_SET_ACTIVATION_OK = "SA_OK";
    public static final String ACK_STR_SET_ACTIVATION_FAIL = "SA_NG";

    public static final String CMD_STR_SET_DEACTIVATION = "sd";
    public static final String ACK_STR_SET_DEACTIVATION_OK = "SD_OK";
    public static final String ACK_STR_SET_DEACTIVATION_FAIL = "SD_NG";

    //Commands to MCU
    public static final String CMD_STR_SET_UNLOCK = "kf";
    public static final String CMD_STR_SET_UNLOCK_ONCE = "of";
    public static final String CMD_STR_SET_LOCK = "on";
    public static final String CMD_STR_GET_LOCK = "gl";
    public static final String CMD_STR_GET_DOOR = "gd";
    public static final String CMD_STR_GET_MAGNET = "gm";
    public static final String CMD_STR_SET_CLEAR_ALARM = "ca";
    public static final String CMD_STR_GET_BATTERY_BOX = "gb";
    public static final String CMD_STR_GET_CONTROL_BOX = "gc";
    public static final String CMD_STR_GET_VOLTAGE = "gv";

    //Fake Ack
    public static final String ACK_STR_MCU_ATTACHED = "MCU_A";
    public static final String ACK_STR_MCU_DETACHED = "MCU_D";

    public static final String ACK_STR_GET_LOCK_NONE = "GL_N";
    public static final String ACK_STR_GET_DOOR_NONE = "GD_N";
    public static final String ACK_STR_GET_MAGNET_NONE = "GM_N";
    public static final String ACK_STR_GET_BATTERY_BOX_NONE = "GB_N";
    public static final String ACK_STR_GET_CONTROL_BOX_NONE = "GC_N";
    public static final String ACK_STR_GET_VOLTAGE_NONE = "GV_N";

    private static int mCmdId = 0;

    public static void setCmdId(int id) {
        mCmdId = id;
    }

    public static int getCmdId() {
        return mCmdId;
    }

    public enum CmdType {
        SET,
        GET
    }

    public enum CmdTarget {
        SELF,
        SERIAL
    }

    public enum TargetType {
        OTHERS,
        LOCK,
        DOOR,
        MAGNET,
        BBOX,
        CBOX,
        VOLTAGE
    }

    public enum AckSource {
        SET,
        GET,
        ALERT,
        HEARTBEAT
    }

    public static class CtrlCmd {
        private CmdType mCmdType;
        private CmdTarget mCmdTarget;
        private TargetType mTargetType;
        private String mCmdCode;
        private String mCmdStr;

        public CtrlCmd(CmdType cmdType, CmdTarget cmdTarget, TargetType targetType, String cmdCode, String cmdStr) {
            mCmdType = cmdType;
            mCmdTarget = cmdTarget;
            mTargetType = targetType;
            mCmdCode = cmdCode;
            mCmdStr = cmdStr;
        }

        public CmdType getCmdType() {
            return mCmdType;
        }

        public CmdTarget getCmdTarget() {
            return mCmdTarget;
        }

        public TargetType getTargetType() {
            return mTargetType;
        }

        public String getCmdCode() {
            return mCmdCode;
        }

        public String getCmdStr() {
            return mCmdStr;
        }
    }

    public static CtrlCmd getCtrlCmd(String cmdStr) {
        CmdType cmdType;
        CmdTarget cmdTarget;
        TargetType targetType;
        String cmdCode;

        if (cmdStr.startsWith("$")) { //backdoor for debug
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.SELF;
            targetType = TargetType.OTHERS;

            if (cmdStr.startsWith("$g"))
                cmdType = CmdType.GET;

            cmdCode = cmdStr.substring(1);
        } else if (cmdStr.equals(CMD_STR_SET_ACTIVATION)) {
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.SELF;
            targetType = TargetType.OTHERS;
            cmdCode = CMD_STR_SET_ACTIVATION;
        } else if (cmdStr.equals(CMD_STR_SET_DEACTIVATION)) {
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.SELF;
            targetType = TargetType.OTHERS;
            cmdCode = CMD_STR_SET_DEACTIVATION;
        } else if (cmdStr.startsWith("#")) { //backdoor for debug
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.SERIAL;
            targetType = TargetType.OTHERS;

            if (cmdStr.startsWith("#g"))
                cmdType = CmdType.GET;

            cmdCode = cmdStr.substring(1);
        } else if (cmdStr.equals(CMD_STR_SET_UNLOCK)) {
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.SERIAL;
            targetType = TargetType.LOCK;
            cmdCode = SerialCode.CMD_CODE_SET_UNLOCK;
        } else if (cmdStr.equals(CMD_STR_SET_UNLOCK_ONCE)) {
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.SERIAL;
            targetType = TargetType.LOCK;
            cmdCode = SerialCode.CMD_CODE_SET_UNLOCK_ONCE;
        } else if (cmdStr.equals(CMD_STR_SET_LOCK)) {
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.SERIAL;
            targetType = TargetType.LOCK;
            cmdCode = SerialCode.CMD_CODE_SET_LOCK;
        } else if (cmdStr.equals(CMD_STR_GET_LOCK)) {
            cmdType = CmdType.GET;
            cmdTarget = CmdTarget.SERIAL;
            targetType = TargetType.LOCK;
            cmdCode = SerialCode.CMD_CODE_GET_LOCK;
        } else if (cmdStr.equals(CMD_STR_GET_DOOR)) {
            cmdType = CmdType.GET;
            cmdTarget = CmdTarget.SERIAL;
            targetType = TargetType.DOOR;
            cmdCode = SerialCode.CMD_CODE_GET_DOOR;
        } else if (cmdStr.equals(CMD_STR_GET_MAGNET)) {
            cmdType = CmdType.GET;
            cmdTarget = CmdTarget.SERIAL;
            targetType = TargetType.MAGNET;
            cmdCode = SerialCode.CMD_CODE_GET_MAGNET;
        } else if (cmdStr.equals(CMD_STR_SET_CLEAR_ALARM)) {
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.SERIAL;
            targetType = TargetType.OTHERS;
            cmdCode = SerialCode.CMD_CODE_SET_CLEAR_ALARM;
        } else if (cmdStr.equals(CMD_STR_GET_BATTERY_BOX)) {
            cmdType = CmdType.GET;
            cmdTarget = CmdTarget.SERIAL;
            targetType = TargetType.BBOX;
            cmdCode = SerialCode.CMD_CODE_GET_BATTERY_BOX;
        } else if (cmdStr.equals(CMD_STR_GET_CONTROL_BOX)) {
            cmdType = CmdType.GET;
            cmdTarget = CmdTarget.SERIAL;
            targetType = TargetType.CBOX;
            cmdCode = SerialCode.CMD_CODE_GET_CONTROL_BOX;
        } else if (cmdStr.equals(CMD_STR_GET_VOLTAGE)) {
            cmdType = CmdType.GET;
            cmdTarget = CmdTarget.SERIAL;
            targetType = TargetType.VOLTAGE;
            cmdCode = SerialCode.CMD_CODE_GET_VOLTAGE;
        } else {
            return null;
        }

        return new CtrlCmd(cmdType, cmdTarget, targetType, cmdCode, cmdStr);
    }

    public static class RespAck {
        public static final String ACK_RESULT_OK = "success";
        public static final String ACK_RESULT_FAIL = "fail";

        private AckSource mAckSource;
        private TargetType mTargetType;
        private String mCmdStr;
        private String mAckCode;
        private String mResult;
        private String mInfo;

        public RespAck(AckSource source, TargetType type, String cmd, String code, String result, String info) {
            mAckSource = source;
            mTargetType = type;
            mCmdStr = cmd;
            mAckCode = code;
            mResult = result;
            mInfo = info;
        }

        public AckSource getAckSource() {
            return mAckSource;
        }

        public TargetType getTargetType() {
            return mTargetType;
        }

        public String getCmdStr() {
            return mCmdStr;
        }

        public String getAckCode() {
            return mAckCode;
        }

        public String getResult() {
            return mResult;
        }

        public String getInfo() {
            return mInfo;
        }
    }

    public static RespAck getRespAck(String ackCode) {
        AckSource ackSource;
        TargetType targetType;
        String cmdStr;
        String result;
        String info;

        if (ackCode.equals(ACK_STR_SET_ACTIVATION_OK)) {
            ackSource = AckSource.SET;
            targetType = TargetType.OTHERS;
            cmdStr = CMD_STR_SET_ACTIVATION;
            result = RespAck.ACK_RESULT_OK;
            info = "activate ok";
        } else if (ackCode.equals(ACK_STR_SET_ACTIVATION_FAIL)) {
            ackSource = AckSource.SET;
            targetType = TargetType.OTHERS;
            cmdStr = CMD_STR_SET_ACTIVATION;
            result = RespAck.ACK_RESULT_FAIL;
            info = "activate error";
        } else if (ackCode.equals(ACK_STR_SET_DEACTIVATION_OK)) {
            ackSource = AckSource.SET;
            targetType = TargetType.OTHERS;
            cmdStr = CMD_STR_SET_DEACTIVATION;
            result = RespAck.ACK_RESULT_OK;
            info = "deactivate ok";
        } else if (ackCode.equals(ACK_STR_SET_DEACTIVATION_FAIL)) {
            ackSource = AckSource.SET;
            targetType = TargetType.OTHERS;
            cmdStr = CMD_STR_SET_DEACTIVATION;
            result = RespAck.ACK_RESULT_FAIL;
            info = "deactivate error";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_UNLOCK_OK)) {
            ackSource = AckSource.SET;
            targetType = TargetType.LOCK;
            cmdStr = CMD_STR_SET_UNLOCK;
            result = RespAck.ACK_RESULT_OK;
            info = "unlock ok";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_UNLOCK_ERROR)) {
            ackSource = AckSource.SET;
            targetType = TargetType.LOCK;
            cmdStr = CMD_STR_SET_UNLOCK;
            result = RespAck.ACK_RESULT_FAIL;
            info = "unlock error";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_UNLOCK_ONCE_OK)) {
            ackSource = AckSource.SET;
            targetType = TargetType.LOCK;
            cmdStr = CMD_STR_SET_UNLOCK_ONCE;
            result = RespAck.ACK_RESULT_OK;
            info = "unlock once ok";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_UNLOCK_ONCE_ERROR)) {
            ackSource = AckSource.SET;
            targetType = TargetType.LOCK;
            cmdStr = CMD_STR_SET_UNLOCK_ONCE;
            result = RespAck.ACK_RESULT_FAIL;
            info = "unlock once error";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_LOCK_OK)) {
            ackSource = AckSource.SET;
            targetType = TargetType.LOCK;
            cmdStr = CMD_STR_SET_LOCK;
            result = RespAck.ACK_RESULT_OK;
            info = "lock ok";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_LOCK_ERROR)) {
            ackSource = AckSource.SET;
            targetType = TargetType.LOCK;
            cmdStr = CMD_STR_SET_LOCK;
            result = RespAck.ACK_RESULT_FAIL;
            info = "lock error";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_LOCK_ERROR_DOOR_OPEN)) {
            ackSource = AckSource.SET;
            targetType = TargetType.DOOR;
            cmdStr = CMD_STR_SET_LOCK;
            result = RespAck.ACK_RESULT_FAIL;
            info = "door is open";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_LOCK_ERROR_MAGNET_LEAVE)) {
            ackSource = AckSource.SET;
            targetType = TargetType.MAGNET;
            cmdStr = CMD_STR_SET_LOCK;
            result = RespAck.ACK_RESULT_FAIL;
            info = "magnet is not detected";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_LOCK_LOCKED)) {
            ackSource = AckSource.GET;
            targetType = TargetType.LOCK;
            cmdStr = CMD_STR_GET_LOCK;
            result = RespAck.ACK_RESULT_OK;
            info = "locked";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_LOCK_UNLOCKED)) {
            ackSource = AckSource.GET;
            targetType = TargetType.LOCK;
            cmdStr = CMD_STR_GET_LOCK;
            result = RespAck.ACK_RESULT_OK;
            info = "unlocked";
        } else if (ackCode.equals(ACK_STR_GET_LOCK_NONE)) {
            ackSource = AckSource.GET;
            targetType = TargetType.LOCK;
            cmdStr = CMD_STR_GET_LOCK;
            result = RespAck.ACK_RESULT_FAIL;
            info = "no lock";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_LOCK_NG)) {
            ackSource = AckSource.GET;
            targetType = TargetType.LOCK;
            cmdStr = CMD_STR_GET_LOCK;
            result = RespAck.ACK_RESULT_OK;
            info = "abnormal";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_DOOR_OPEN)) {
            ackSource = AckSource.GET;
            targetType = TargetType.DOOR;
            cmdStr = CMD_STR_GET_DOOR;
            result = RespAck.ACK_RESULT_OK;
            info = "open";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_DOOR_CLOSED)) {
            ackSource = AckSource.GET;
            targetType = TargetType.DOOR;
            cmdStr = CMD_STR_GET_DOOR;
            result = RespAck.ACK_RESULT_OK;
            info = "closed";
        } else if (ackCode.equals(ACK_STR_GET_DOOR_NONE)) {
            ackSource = AckSource.GET;
            targetType = TargetType.DOOR;
            cmdStr = CMD_STR_GET_DOOR;
            result = RespAck.ACK_RESULT_FAIL;
            info = "";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_MAGNET_EXIST)) {
            ackSource = AckSource.GET;
            targetType = TargetType.MAGNET;
            cmdStr = CMD_STR_GET_MAGNET;
            result = RespAck.ACK_RESULT_OK;
            info = "detected";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_MAGNET_LEAVE)) {
            ackSource = AckSource.GET;
            targetType = TargetType.MAGNET;
            cmdStr = CMD_STR_GET_MAGNET;
            result = RespAck.ACK_RESULT_OK;
            info = "undetected";
        } else if (ackCode.equals(ACK_STR_GET_MAGNET_NONE)) {
            ackSource = AckSource.GET;
            targetType = TargetType.MAGNET;
            cmdStr = CMD_STR_GET_MAGNET;
            result = RespAck.ACK_RESULT_FAIL;
            info = "";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_CLEAR_ALARM_OK)) {
            ackSource = AckSource.SET;
            targetType = TargetType.OTHERS;
            cmdStr = CMD_STR_SET_CLEAR_ALARM;
            result = RespAck.ACK_RESULT_OK;
            info = "clear alarm ok";
        } else if (ackCode.equals(SerialCode.MCU_CODE_DOOR_ALARM)) {
            ackSource = AckSource.ALERT;
            targetType = TargetType.DOOR;
            cmdStr = "";
            result = RespAck.ACK_RESULT_OK;
            info = "door is hacked";
        } else if (ackCode.equals(SerialCode.MCU_CODE_HEARTBEAT_REQ)) {
            ackSource = AckSource.HEARTBEAT;
            targetType = TargetType.OTHERS;
            cmdStr = "";
            result = RespAck.ACK_RESULT_OK;
            info = "heartbeat request";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_BATTERY_BOX_OPEN)) {
            ackSource = AckSource.GET;
            targetType = TargetType.BBOX;
            cmdStr = CMD_STR_GET_BATTERY_BOX;
            result = RespAck.ACK_RESULT_OK;
            info = "open";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_BATTERY_BOX_CLOSED)) {
            ackSource = AckSource.GET;
            targetType = TargetType.BBOX;
            cmdStr = CMD_STR_GET_BATTERY_BOX;
            result = RespAck.ACK_RESULT_OK;
            info = "closed";
        } else if (ackCode.equals(ACK_STR_GET_BATTERY_BOX_NONE)) {
            ackSource = AckSource.GET;
            targetType = TargetType.BBOX;
            cmdStr = CMD_STR_GET_BATTERY_BOX;
            result = RespAck.ACK_RESULT_FAIL;
            info = "";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_CONTROL_BOX_OPEN)) {
            ackSource = AckSource.GET;
            targetType = TargetType.CBOX;
            cmdStr = CMD_STR_GET_CONTROL_BOX;
            result = RespAck.ACK_RESULT_OK;
            info = "open";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_CONTROL_BOX_CLOSED)) {
            ackSource = AckSource.GET;
            targetType = TargetType.CBOX;
            cmdStr = CMD_STR_GET_CONTROL_BOX;
            result = RespAck.ACK_RESULT_OK;
            info = "closed";
        } else if (ackCode.equals(ACK_STR_GET_CONTROL_BOX_NONE)) {
            ackSource = AckSource.GET;
            targetType = TargetType.CBOX;
            cmdStr = CMD_STR_GET_CONTROL_BOX;
            result = RespAck.ACK_RESULT_FAIL;
            info = "";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_VOLTAGE_CRITICAL)) {
            ackSource = AckSource.GET;
            targetType = TargetType.VOLTAGE;
            cmdStr = CMD_STR_GET_VOLTAGE;
            result = RespAck.ACK_RESULT_OK;
            info = "critical";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_VOLTAGE_LOW)) {
            ackSource = AckSource.GET;
            targetType = TargetType.VOLTAGE;
            cmdStr = CMD_STR_GET_VOLTAGE;
            result = RespAck.ACK_RESULT_OK;
            info = "low";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_VOLTAGE_NORMAL)) {
            ackSource = AckSource.GET;
            targetType = TargetType.VOLTAGE;
            cmdStr = CMD_STR_GET_VOLTAGE;
            result = RespAck.ACK_RESULT_OK;
            info = "normal";
        } else if (ackCode.equals(ACK_STR_GET_VOLTAGE_NONE)) {
            ackSource = AckSource.GET;
            targetType = TargetType.VOLTAGE;
            cmdStr = CMD_STR_GET_VOLTAGE;
            result = RespAck.ACK_RESULT_FAIL;
            info = "";
        } else if (ackCode.equals(ACK_STR_MCU_ATTACHED)) {
            ackSource = AckSource.ALERT;
            targetType = TargetType.OTHERS;
            cmdStr = "";
            result = RespAck.ACK_RESULT_OK;
            info = "MCU attached";
        } else if (ackCode.equals(ACK_STR_MCU_DETACHED)) {
            ackSource = AckSource.ALERT;
            targetType = TargetType.OTHERS;
            cmdStr = "";
            result = RespAck.ACK_RESULT_OK;
            info = "MCU detached";
        } else {
            return null;
        }

        return new RespAck(ackSource, targetType, cmdStr, ackCode, result, info);
    }
}
