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

    public static final String CMD_STR_GET_TEMPERATURE = "gt";
    public static final String ACK_STR_GET_TEMPERATURE_OK = "GT_OK";
    public static final String ACK_STR_GET_TEMPERATURE_FAIL = "GT_NG";

    public static final String CMD_STR_SET_TASK_START = "ss";
    public static final String ACK_STR_SET_TASK_START_OK = "SS_OK";
    public static final String ACK_STR_SET_TASK_START_FAIL = "SS_NG";

    public static final String CMD_STR_SET_TASK_END = "se";
    public static final String ACK_STR_SET_TASK_END_OK = "SE_OK";
    public static final String ACK_STR_SET_TASK_END_FAIL = "SE_NG";

    public static final String CMD_STR_SET_UPDATE = "su";
    public static final String ACK_STR_SET_UPDATE_OK = "SU_OK";
    public static final String ACK_STR_SET_UPDATE_UPDATING = "SU_RUN";
    public static final String ACK_STR_SET_UPDATE_FAIL = "SU_NG";

    public static final String CMD_STR_GET_BATTERY = "ge";
    public static final String ACK_STR_GET_BATTERY_OK = "GE_OK";
    public static final String ACK_STR_GET_BATTERY_FAIL = "GE_NG";

    public static final String CMD_STR_GET_SYSTEM_INFO = "gi";
    public static final String ACK_STR_GET_SYSTEM_INFO_OK = "GI_OK";
    public static final String ACK_STR_GET_SYSTEM_INFO_FAIL = "GI_NG";

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
    public static final String CMD_STR_SET_HEARTBEAT_ACK_TIME = "hb_";

    //Fake Ack
    public static final String ACK_STR_MCU_ATTACHED = "MCU_A";
    public static final String ACK_STR_MCU_DETACHED = "MCU_D";

    public static final String ACK_STR_MCU_HEARTBEAT_TIMEOUT = "MCU_HTO";

    public static final String ACK_STR_GET_LOCK_NONE = "GL_N";
    public static final String ACK_STR_GET_DOOR_NONE = "GD_N";
    public static final String ACK_STR_GET_MAGNET_NONE = "GM_N";
    public static final String ACK_STR_GET_BATTERY_BOX_NONE = "GB_N";
    public static final String ACK_STR_GET_CONTROL_BOX_NONE = "GC_N";
    public static final String ACK_STR_GET_VOLTAGE_NONE = "GV_N";

    public static final String ACK_STR_SERVICE_BOOT_UP = "SV_U";



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
        DEVICE,
        MCU
    }

    public enum TargetType {
        OTHERS,
        MCU_LOCK_STATUS,
        MCU_DOOR_STATUS,
        MCU_MAGNET_STATUS,
        MCU_BBOX_STATUS,
        MCU_CBOX_STATUS,
        MCU_BATTERY_LEVEL,
        MCU_HEARTBEAT_ACK_TIME,
        DEV_TEMPERATURE,
        DEV_BATTERY,
        DEV_SYSTEM_INFO
    }

    public enum AckSource {
        SET_CMD,
        GET_CMD,
        ALERT,
        HEARTBEAT
    }

    public enum AckLevel {
        NORMAL,
        URGENT
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
            cmdTarget = CmdTarget.DEVICE;
            targetType = TargetType.OTHERS;

            if (cmdStr.startsWith("$g"))
                cmdType = CmdType.GET;

            cmdCode = cmdStr.substring(1);
        } else if (cmdStr.equals(CMD_STR_SET_ACTIVATION)) {
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.DEVICE;
            targetType = TargetType.OTHERS;
            cmdCode = cmdStr;
        } else if (cmdStr.equals(CMD_STR_SET_DEACTIVATION)) {
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.DEVICE;
            targetType = TargetType.OTHERS;
            cmdCode = cmdStr;
        } else if (cmdStr.equals(CMD_STR_SET_TASK_START)) {
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.DEVICE;
            targetType = TargetType.OTHERS;
            cmdCode = cmdStr;
        } else if (cmdStr.equals(CMD_STR_SET_TASK_END)) {
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.DEVICE;
            targetType = TargetType.OTHERS;
            cmdCode = cmdStr;
        } else if (cmdStr.equals(CMD_STR_GET_TEMPERATURE)) {
            cmdType = CmdType.GET;
            cmdTarget = CmdTarget.DEVICE;
            targetType = TargetType.DEV_TEMPERATURE;
            cmdCode = cmdStr;
        } else if (cmdStr.equals(CMD_STR_SET_UPDATE)) {
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.DEVICE;
            targetType = TargetType.OTHERS;
            cmdCode = cmdStr;
        } else if (cmdStr.equals(CMD_STR_GET_BATTERY)) {
            cmdType = CmdType.GET;
            cmdTarget = CmdTarget.DEVICE;
            targetType = TargetType.DEV_BATTERY;
            cmdCode = cmdStr;
        } else if (cmdStr.equals(CMD_STR_GET_SYSTEM_INFO)){
            cmdType = CmdType.GET;
            cmdTarget = CmdTarget.DEVICE;
            targetType = TargetType.DEV_SYSTEM_INFO;
            cmdCode = cmdStr;
        } else if (cmdStr.startsWith("#")) { //backdoor for debug
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.MCU;
            targetType = TargetType.OTHERS;

            if (cmdStr.startsWith("#g"))
                cmdType = CmdType.GET;

            cmdCode = cmdStr.substring(1);
        } else if (cmdStr.equals(CMD_STR_SET_UNLOCK)) {
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.MCU;
            targetType = TargetType.MCU_LOCK_STATUS;
            cmdCode = SerialCode.CMD_CODE_SET_UNLOCK;
        } else if (cmdStr.equals(CMD_STR_SET_UNLOCK_ONCE)) {
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.MCU;
            targetType = TargetType.MCU_LOCK_STATUS;
            cmdCode = SerialCode.CMD_CODE_SET_UNLOCK_ONCE;
        } else if (cmdStr.equals(CMD_STR_SET_LOCK)) {
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.MCU;
            targetType = TargetType.MCU_LOCK_STATUS;
            cmdCode = SerialCode.CMD_CODE_SET_LOCK;
        } else if (cmdStr.equals(CMD_STR_GET_LOCK)) {
            cmdType = CmdType.GET;
            cmdTarget = CmdTarget.MCU;
            targetType = TargetType.MCU_LOCK_STATUS;
            cmdCode = SerialCode.CMD_CODE_GET_LOCK;
        } else if (cmdStr.equals(CMD_STR_GET_DOOR)) {
            cmdType = CmdType.GET;
            cmdTarget = CmdTarget.MCU;
            targetType = TargetType.MCU_DOOR_STATUS;
            cmdCode = SerialCode.CMD_CODE_GET_DOOR;
        } else if (cmdStr.equals(CMD_STR_GET_MAGNET)) {
            cmdType = CmdType.GET;
            cmdTarget = CmdTarget.MCU;
            targetType = TargetType.MCU_MAGNET_STATUS;
            cmdCode = SerialCode.CMD_CODE_GET_MAGNET;
        } else if (cmdStr.equals(CMD_STR_SET_CLEAR_ALARM)) {
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.MCU;
            targetType = TargetType.OTHERS;
            cmdCode = SerialCode.CMD_CODE_SET_CLEAR_ALARM;
        } else if (cmdStr.equals(CMD_STR_GET_BATTERY_BOX)) {
            cmdType = CmdType.GET;
            cmdTarget = CmdTarget.MCU;
            targetType = TargetType.MCU_BBOX_STATUS;
            cmdCode = SerialCode.CMD_CODE_GET_BATTERY_BOX;
        } else if (cmdStr.equals(CMD_STR_GET_CONTROL_BOX)) {
            cmdType = CmdType.GET;
            cmdTarget = CmdTarget.MCU;
            targetType = TargetType.MCU_CBOX_STATUS;
            cmdCode = SerialCode.CMD_CODE_GET_CONTROL_BOX;
        } else if (cmdStr.equals(CMD_STR_GET_VOLTAGE)) {
            cmdType = CmdType.GET;
            cmdTarget = CmdTarget.MCU;
            targetType = TargetType.MCU_BATTERY_LEVEL;
            cmdCode = SerialCode.CMD_CODE_GET_VOLTAGE;
        } else if (cmdStr.startsWith(CMD_STR_SET_HEARTBEAT_ACK_TIME)){
            cmdType = CmdType.SET;
            cmdTarget = CmdTarget.MCU;
            targetType = TargetType.MCU_HEARTBEAT_ACK_TIME;
            cmdCode = cmdStr;
        } else {
            return null;
        }

        return new CtrlCmd(cmdType, cmdTarget, targetType, cmdCode, cmdStr);
    }

    public static class RespAck {
        public static final String ACK_RESULT_OK = "success";
        public static final String ACK_RESULT_FAIL = "fail";

        public static final String TYPE_STR_OTHERS = "others";
        public static final String TYPE_STR_MCU_LOCK_STATUS = "lock_status";
        public static final String TYPE_STR_MCU_DOOR_STATUS = "door_status";
        public static final String TYPE_STR_MCU_MAGNET_STATUS = "magnet_status";
        public static final String TYPE_STR_MCU_BBOX_STATUS = "battery_box_status";
        public static final String TYPE_STR_MCU_CBOX_STATUS = "control_box_status";
        public static final String TYPE_STR_MCU_BATTERY_LEVEL = "main_battery_level";
        public static final String TYPE_STR_DEV_TEMPERATURE = "device_temperature";
        public static final String TYPE_STR_DEV_BATTERY = "device_battery";
        public static final String TYPE_STR_DEV_SYSTEMINFO = "system_info";

        private AckSource mAckSource;
        private AckLevel mAckLevel;
        private TargetType mTargetType;
        private String mTargetTypeStr;
        private String mCmdStr;
        private String mAckCode;
        private String mResult;
        private String mInfo;

        public RespAck(AckSource source, AckLevel level, TargetType type,
                       String typeStr, String cmd, String code,
                       String result, String info) {
            mAckSource = source;
            mAckLevel = level;
            mTargetType = type;
            mTargetTypeStr = typeStr;
            mCmdStr = cmd;
            mAckCode = code;
            mResult = result;
            mInfo = info;
        }

        public AckSource getAckSource() {
            return mAckSource;
        }

        public AckLevel getAckLevel() {
            return mAckLevel;
        }

        public TargetType getTargetType() {
            return mTargetType;
        }

        public String getTargetTypeStr() {
            return mTargetTypeStr;
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

        public void setInfo(String info) {
            mInfo = info;
        }
    }

    public static RespAck getRespAck(String ackCode) {
        AckSource ackSource;
        AckLevel ackLevel;
        TargetType targetType;
        String targetTypeStr;
        String cmdStr;
        String result;
        String info;

        if (ackCode.equals(ACK_STR_SET_ACTIVATION_OK)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.OTHERS;
            targetTypeStr = RespAck.TYPE_STR_OTHERS;
            cmdStr = CMD_STR_SET_ACTIVATION;
            result = RespAck.ACK_RESULT_OK;
            info = "activate ok";
        } else if (ackCode.equals(ACK_STR_SET_ACTIVATION_FAIL)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.OTHERS;
            targetTypeStr = RespAck.TYPE_STR_OTHERS;
            cmdStr = CMD_STR_SET_ACTIVATION;
            result = RespAck.ACK_RESULT_FAIL;
            info = "activate error";
        } else if (ackCode.equals(ACK_STR_SET_DEACTIVATION_OK)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.OTHERS;
            targetTypeStr = RespAck.TYPE_STR_OTHERS;
            cmdStr = CMD_STR_SET_DEACTIVATION;
            result = RespAck.ACK_RESULT_OK;
            info = "deactivate ok";
        } else if (ackCode.equals(ACK_STR_SET_DEACTIVATION_FAIL)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.OTHERS;
            targetTypeStr = RespAck.TYPE_STR_OTHERS;
            cmdStr = CMD_STR_SET_DEACTIVATION;
            result = RespAck.ACK_RESULT_FAIL;
            info = "deactivate error";
        } else if (ackCode.equals(ACK_STR_GET_TEMPERATURE_OK)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.DEV_TEMPERATURE;
            targetTypeStr = RespAck.TYPE_STR_DEV_TEMPERATURE;
            cmdStr = CMD_STR_GET_TEMPERATURE;
            result = RespAck.ACK_RESULT_OK;
            info = "";
        } else if (ackCode.equals(ACK_STR_GET_TEMPERATURE_FAIL)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.DEV_TEMPERATURE;
            targetTypeStr = RespAck.TYPE_STR_DEV_TEMPERATURE;
            cmdStr = CMD_STR_GET_TEMPERATURE;
            result = RespAck.ACK_RESULT_FAIL;
            info = "";
        } else if (ackCode.equals(ACK_STR_SET_TASK_START_OK)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.OTHERS;
            targetTypeStr = RespAck.TYPE_STR_OTHERS;
            cmdStr = CMD_STR_SET_TASK_START;
            result = RespAck.ACK_RESULT_OK;
            info = "task start ok";
        } else if (ackCode.equals(ACK_STR_SET_TASK_START_FAIL)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.OTHERS;
            targetTypeStr = RespAck.TYPE_STR_OTHERS;
            cmdStr = CMD_STR_SET_TASK_START;
            result = RespAck.ACK_RESULT_FAIL;
            info = "task start error";
        } else if (ackCode.equals(ACK_STR_SET_TASK_END_OK)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.OTHERS;
            targetTypeStr = RespAck.TYPE_STR_OTHERS;
            cmdStr = CMD_STR_SET_TASK_END;
            result = RespAck.ACK_RESULT_OK;
            info = "task end ok";
        } else if (ackCode.equals(ACK_STR_SET_TASK_END_FAIL)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.OTHERS;
            targetTypeStr = RespAck.TYPE_STR_OTHERS;
            cmdStr = CMD_STR_SET_TASK_END;
            result = RespAck.ACK_RESULT_FAIL;
            info = "task end error";
        } else if (ackCode.equals(ACK_STR_SET_UPDATE_OK)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.OTHERS;
            targetTypeStr = RespAck.TYPE_STR_OTHERS;
            cmdStr = CMD_STR_SET_UPDATE;
            result = RespAck.ACK_RESULT_OK;
            info = "updated";
        } else if (ackCode.equals(ACK_STR_SET_UPDATE_UPDATING)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.OTHERS;
            targetTypeStr = RespAck.TYPE_STR_OTHERS;
            cmdStr = CMD_STR_SET_UPDATE;
            result = RespAck.ACK_RESULT_OK;
            info = "updating";
        } else if (ackCode.equals(ACK_STR_SET_UPDATE_FAIL)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.OTHERS;
            targetTypeStr = RespAck.TYPE_STR_OTHERS;
            cmdStr = CMD_STR_SET_UPDATE;
            result = RespAck.ACK_RESULT_FAIL;
            info = "update fail";
        } else if (ackCode.equals(ACK_STR_GET_BATTERY_OK)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.DEV_BATTERY;
            targetTypeStr = RespAck.TYPE_STR_DEV_BATTERY;
            cmdStr = CMD_STR_GET_BATTERY;
            result = RespAck.ACK_RESULT_OK;
            info = "";
        } else if (ackCode.equals(ACK_STR_GET_BATTERY_FAIL)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.DEV_BATTERY;
            targetTypeStr = RespAck.TYPE_STR_DEV_BATTERY;
            cmdStr = CMD_STR_GET_BATTERY;
            result = RespAck.ACK_RESULT_FAIL;
            info = "";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_UNLOCK_OK)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_LOCK_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_LOCK_STATUS;
            cmdStr = CMD_STR_SET_UNLOCK;
            result = RespAck.ACK_RESULT_OK;
            info = "unlock ok";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_UNLOCK_ERROR)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_LOCK_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_LOCK_STATUS;
            cmdStr = CMD_STR_SET_UNLOCK;
            result = RespAck.ACK_RESULT_FAIL;
            info = "unlock error";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_UNLOCK_ONCE_OK)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_LOCK_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_LOCK_STATUS;
            cmdStr = CMD_STR_SET_UNLOCK_ONCE;
            result = RespAck.ACK_RESULT_OK;
            info = "unlock once ok";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_UNLOCK_ONCE_ERROR)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_LOCK_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_LOCK_STATUS;
            cmdStr = CMD_STR_SET_UNLOCK_ONCE;
            result = RespAck.ACK_RESULT_FAIL;
            info = "unlock once error";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_LOCK_OK)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_LOCK_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_LOCK_STATUS;
            cmdStr = CMD_STR_SET_LOCK;
            result = RespAck.ACK_RESULT_OK;
            info = "lock ok";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_LOCK_ERROR)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_LOCK_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_LOCK_STATUS;
            cmdStr = CMD_STR_SET_LOCK;
            result = RespAck.ACK_RESULT_FAIL;
            info = "lock error";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_LOCK_ERROR_DOOR_OPEN)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_DOOR_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_DOOR_STATUS;
            cmdStr = CMD_STR_SET_LOCK;
            result = RespAck.ACK_RESULT_FAIL;
            info = "door is open";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_LOCK_ERROR_MAGNET_LEAVE)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_MAGNET_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_MAGNET_STATUS;
            cmdStr = CMD_STR_SET_LOCK;
            result = RespAck.ACK_RESULT_FAIL;
            info = "magnet is not detected";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_LOCK_LOCKED)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_LOCK_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_LOCK_STATUS;
            cmdStr = CMD_STR_GET_LOCK;
            result = RespAck.ACK_RESULT_OK;
            info = "locked";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_LOCK_UNLOCKED)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_LOCK_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_LOCK_STATUS;
            cmdStr = CMD_STR_GET_LOCK;
            result = RespAck.ACK_RESULT_OK;
            info = "unlocked";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_LOCK_NG)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.URGENT;
            targetType = TargetType.MCU_LOCK_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_LOCK_STATUS;
            cmdStr = CMD_STR_GET_LOCK;
            result = RespAck.ACK_RESULT_OK;
            info = "abnormal";
        } else if (ackCode.equals(ACK_STR_GET_LOCK_NONE)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_LOCK_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_LOCK_STATUS;
            cmdStr = CMD_STR_GET_LOCK;
            result = RespAck.ACK_RESULT_FAIL;
            info = "";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_DOOR_OPEN)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_DOOR_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_DOOR_STATUS;
            cmdStr = CMD_STR_GET_DOOR;
            result = RespAck.ACK_RESULT_OK;
            info = "open";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_DOOR_CLOSED)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_DOOR_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_DOOR_STATUS;
            cmdStr = CMD_STR_GET_DOOR;
            result = RespAck.ACK_RESULT_OK;
            info = "closed";
        } else if (ackCode.equals(ACK_STR_GET_DOOR_NONE)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_DOOR_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_DOOR_STATUS;
            cmdStr = CMD_STR_GET_DOOR;
            result = RespAck.ACK_RESULT_FAIL;
            info = "";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_MAGNET_EXIST)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_MAGNET_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_MAGNET_STATUS;
            cmdStr = CMD_STR_GET_MAGNET;
            result = RespAck.ACK_RESULT_OK;
            info = "detected";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_MAGNET_LEAVE)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_MAGNET_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_MAGNET_STATUS;
            cmdStr = CMD_STR_GET_MAGNET;
            result = RespAck.ACK_RESULT_OK;
            info = "undetected";
        } else if (ackCode.equals(ACK_STR_GET_MAGNET_NONE)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_MAGNET_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_MAGNET_STATUS;
            cmdStr = CMD_STR_GET_MAGNET;
            result = RespAck.ACK_RESULT_FAIL;
            info = "";
        } else if (ackCode.equals(SerialCode.ACK_CODE_SET_CLEAR_ALARM_OK)) {
            ackSource = AckSource.SET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.OTHERS;
            targetTypeStr = RespAck.TYPE_STR_OTHERS;
            cmdStr = CMD_STR_SET_CLEAR_ALARM;
            result = RespAck.ACK_RESULT_OK;
            info = "clear alarm ok";
        } else if (ackCode.equals(SerialCode.MCU_CODE_DOOR_ALARM)) {
            ackSource = AckSource.ALERT;
            ackLevel = AckLevel.URGENT;
            targetType = TargetType.MCU_DOOR_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_DOOR_STATUS;
            cmdStr = "";
            result = RespAck.ACK_RESULT_OK;
            info = "door is hacked";
        } else if (ackCode.equals(SerialCode.MCU_CODE_HEARTBEAT_REQ)) {
            ackSource = AckSource.HEARTBEAT;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.OTHERS;
            targetTypeStr = RespAck.TYPE_STR_OTHERS;
            cmdStr = "";
            result = RespAck.ACK_RESULT_OK;
            info = "heartbeat request";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_BATTERY_BOX_OPEN)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_BBOX_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_BBOX_STATUS;
            cmdStr = CMD_STR_GET_BATTERY_BOX;
            result = RespAck.ACK_RESULT_OK;
            info = "open";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_BATTERY_BOX_CLOSED)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_BBOX_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_BBOX_STATUS;
            cmdStr = CMD_STR_GET_BATTERY_BOX;
            result = RespAck.ACK_RESULT_OK;
            info = "closed";
        } else if (ackCode.equals(ACK_STR_GET_BATTERY_BOX_NONE)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_BBOX_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_BBOX_STATUS;
            cmdStr = CMD_STR_GET_BATTERY_BOX;
            result = RespAck.ACK_RESULT_FAIL;
            info = "";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_CONTROL_BOX_OPEN)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.URGENT;
            targetType = TargetType.MCU_CBOX_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_CBOX_STATUS;
            cmdStr = CMD_STR_GET_CONTROL_BOX;
            result = RespAck.ACK_RESULT_OK;
            info = "open";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_CONTROL_BOX_CLOSED)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_CBOX_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_CBOX_STATUS;
            cmdStr = CMD_STR_GET_CONTROL_BOX;
            result = RespAck.ACK_RESULT_OK;
            info = "closed";
        } else if (ackCode.equals(ACK_STR_GET_CONTROL_BOX_NONE)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_CBOX_STATUS;
            targetTypeStr = RespAck.TYPE_STR_MCU_CBOX_STATUS;
            cmdStr = CMD_STR_GET_CONTROL_BOX;
            result = RespAck.ACK_RESULT_FAIL;
            info = "";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_VOLTAGE_CRITICAL)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.URGENT;
            targetType = TargetType.MCU_BATTERY_LEVEL;
            targetTypeStr = RespAck.TYPE_STR_MCU_BATTERY_LEVEL;
            cmdStr = CMD_STR_GET_VOLTAGE;
            result = RespAck.ACK_RESULT_OK;
            info = "critical";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_VOLTAGE_LOW)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_BATTERY_LEVEL;
            targetTypeStr = RespAck.TYPE_STR_MCU_BATTERY_LEVEL;
            cmdStr = CMD_STR_GET_VOLTAGE;
            result = RespAck.ACK_RESULT_OK;
            info = "low";
        } else if (ackCode.equals(SerialCode.ACK_CODE_GET_VOLTAGE_NORMAL)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_BATTERY_LEVEL;
            targetTypeStr = RespAck.TYPE_STR_MCU_BATTERY_LEVEL;
            cmdStr = CMD_STR_GET_VOLTAGE;
            result = RespAck.ACK_RESULT_OK;
            info = "normal";
        } else if (ackCode.equals(ACK_STR_GET_VOLTAGE_NONE)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.MCU_BATTERY_LEVEL;
            targetTypeStr = RespAck.TYPE_STR_MCU_BATTERY_LEVEL;
            cmdStr = CMD_STR_GET_VOLTAGE;
            result = RespAck.ACK_RESULT_FAIL;
            info = "";
        } else if (ackCode.equals(ACK_STR_MCU_ATTACHED)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.OTHERS;
            targetTypeStr = RespAck.TYPE_STR_OTHERS;
            cmdStr = "";
            result = RespAck.ACK_RESULT_OK;
            info = "MCU attached";
        } else if (ackCode.equals(ACK_STR_MCU_DETACHED)) {
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.URGENT;
            targetType = TargetType.OTHERS;
            targetTypeStr = RespAck.TYPE_STR_OTHERS;
            cmdStr = "";
            result = RespAck.ACK_RESULT_OK;
            info = "MCU detached";
        } else if (ackCode.equals(ACK_STR_MCU_HEARTBEAT_TIMEOUT)) {
            ackSource = AckSource.HEARTBEAT;
            ackLevel = AckLevel.URGENT;
            targetType = TargetType.OTHERS;
            targetTypeStr = RespAck.TYPE_STR_OTHERS;
            cmdStr = "";
            result = RespAck.ACK_RESULT_OK;
            info = "MCU heartbeat timeout";
        } else if (ackCode.equals(ACK_STR_SERVICE_BOOT_UP)) {
            ackSource = AckSource.ALERT;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.OTHERS;
            targetTypeStr = RespAck.TYPE_STR_OTHERS;
            cmdStr = "";
            result = RespAck.ACK_RESULT_OK;
            info = "Service boot up";
        } else if (ackCode.equals(ACK_STR_GET_SYSTEM_INFO_FAIL)){
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.DEV_SYSTEM_INFO;
            targetTypeStr = RespAck.TYPE_STR_DEV_SYSTEMINFO;
            cmdStr = CMD_STR_GET_SYSTEM_INFO;
            result = RespAck.ACK_RESULT_FAIL;
            info = "";
        } else if (ackCode.equals(ACK_STR_GET_SYSTEM_INFO_OK)){
            ackSource = AckSource.GET_CMD;
            ackLevel = AckLevel.NORMAL;
            targetType = TargetType.DEV_SYSTEM_INFO;
            targetTypeStr = RespAck.TYPE_STR_DEV_SYSTEMINFO;
            cmdStr = CMD_STR_GET_SYSTEM_INFO;
            result = RespAck.ACK_RESULT_OK;
            info = "";
        } else {
            return null;
        }

        return new RespAck(ackSource, ackLevel, targetType, targetTypeStr, cmdStr, ackCode, result, info);
    }
}
