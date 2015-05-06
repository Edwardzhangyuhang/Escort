package com.foxconn.cnsbg.escort.subsys.usbserial;

public interface SerialCode {
    public static final String CMD_SET_UNLOCK = "kf";
    public static final String ACK_SET_UNLOCK_OK = "A";
    public static final String ACK_SET_UNLOCK_ERROR = "C";

    public static final String CMD_SET_UNLOCK_ONCE = "of";
    public static final String ACK_SET_UNLOCK_ONCE_OK = "B";
    public static final String ACK_SET_UNLOCK_ONCE_ERROR = "D";

    public static final String CMD_SET_LOCK = "on";
    public static final String ACK_SET_LOCK_OK = "E";
    public static final String ACK_SET_LOCK_ERROR = "F";
    public static final String ACK_SET_LOCK_ERROR_DOOR_OPEN = "G";
    public static final String ACK_SET_LOCK_ERROR_MAGNET_LEAVE = "L";

    public static final String CMD_GET_LOCK = "gl";
    public static final String ACK_LOCK_UNLOCKED = "I";
    public static final String ACK_LOCK_LOCKED = "J";
    public static final String ACK_LOCK_NG = "N";

    public static final String CMD_GET_DOOR = "gd";
    public static final String ACK_DOOR_OPEN = "Q";
    public static final String ACK_DOOR_CLOSED = "K";

    public static final String CMD_GET_MAGNET = "gm";
    public static final String ACK_MAGNET_EXIST = "P";
    public static final String ACK_MAGNET_LEAVE = "O";

    public static final String CMD_CLEAR_ALARM = "ca";
    public static final String ACK_CLEAR_ALARM_OK = "M";
    public static final String ACK_DOOR_ALARM = "H";

    public static final String MCU_HEARTBEAT_REQ = "R";
    public static final String MCU_HEARTBEAT_ACK = "ha";

    public static final String CMD_GET_BATTERY_BOX = "gb";
    public static final String ACK_BATTERY_BOX_CLOSED = "S";
    public static final String ACK_BATTERY_BOX_OPEN = "T";

    public static final String CMD_GET_CONTROL_BOX = "gc";
    public static final String ACK_CONTROL_BOX_CLOSED = "U";
    public static final String ACK_CONTROL_BOX_OPEN = "V";

    public static final String CMD_GET_VOLTAGE = "gv";
    public static final String ACK_VOLTAGE_CRITICAL = "W";
    public static final String ACK_VOLTAGE_LOW = "X";
    public static final String ACK_VOLTAGE_NORMAL = "Y";
}
