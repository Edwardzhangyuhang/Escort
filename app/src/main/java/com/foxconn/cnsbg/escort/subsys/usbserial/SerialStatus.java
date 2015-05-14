package com.foxconn.cnsbg.escort.subsys.usbserial;

import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.communication.ComMsgCode;

public final class SerialStatus {
    private static String mLockStatusCode = ComMsgCode.ACK_STR_GET_LOCK_NONE;
    private static String mDoorStatusCode = ComMsgCode.ACK_STR_GET_DOOR_NONE;
    private static String mMagnetStatusCode = ComMsgCode.ACK_STR_GET_MAGNET_NONE;
    private static String mBBoxStatusCode = ComMsgCode.ACK_STR_GET_BATTERY_BOX_NONE;
    private static String mCBoxStatusCode = ComMsgCode.ACK_STR_GET_CONTROL_BOX_NONE;
    private static String mVoltageStatusCode = ComMsgCode.ACK_STR_GET_VOLTAGE_NONE;

    public synchronized static void initStatus() {
        mLockStatusCode = ComMsgCode.ACK_STR_GET_LOCK_NONE;
        mDoorStatusCode = ComMsgCode.ACK_STR_GET_DOOR_NONE;
        mMagnetStatusCode = ComMsgCode.ACK_STR_GET_MAGNET_NONE;
        mBBoxStatusCode = ComMsgCode.ACK_STR_GET_BATTERY_BOX_NONE;
        mCBoxStatusCode = ComMsgCode.ACK_STR_GET_CONTROL_BOX_NONE;
        mVoltageStatusCode = ComMsgCode.ACK_STR_GET_VOLTAGE_NONE;
    }

    public synchronized static void checkStatus(SerialCtrl sc) {
        sc.write(SerialCode.CMD_CODE_GET_ALL_STATUS + "\r\n");
    }

    public synchronized static boolean setStatus(ComMsgCode.RespAck resp) {
        ComMsgCode.TargetType type = resp.getTargetType();
        String ackCode = resp.getAckCode();

        switch (type) {
            case MCU_LOCK_STATUS:
                //lock status is not reliable in this state
                if (CtrlCenter.isDoorAlarm())
                    ackCode = ComMsgCode.ACK_STR_GET_LOCK_NONE;

                if (mLockStatusCode.equals(ackCode))
                    return false;

                mLockStatusCode = ackCode;
                return true;
            case MCU_DOOR_STATUS:
                if (mDoorStatusCode.equals(ackCode))
                    return false;

                mDoorStatusCode = ackCode;
                return true;
            case MCU_MAGNET_STATUS:
                if (mMagnetStatusCode.equals(ackCode))
                    return false;

                mMagnetStatusCode = ackCode;
                return true;
            case MCU_BBOX_STATUS:
                if (mBBoxStatusCode.equals(ackCode))
                    return false;

                mBBoxStatusCode = ackCode;
                return true;
            case MCU_CBOX_STATUS:
                if (mCBoxStatusCode.equals(ackCode))
                    return false;

                mCBoxStatusCode = ackCode;
                return true;
            case MCU_BATTERY_LEVEL:
                if (mVoltageStatusCode.equals(ackCode))
                    return false;

                mVoltageStatusCode = ackCode;
                return true;
            default:
                return false;
        }
    }

    public synchronized static ComMsgCode.RespAck getStatus(ComMsgCode.TargetType type) {
        ComMsgCode.RespAck resp;

        switch (type) {
            case MCU_LOCK_STATUS:
                resp = ComMsgCode.getRespAck(mLockStatusCode);
                break;
            case MCU_DOOR_STATUS:
                resp = ComMsgCode.getRespAck(mDoorStatusCode);
                break;
            case MCU_MAGNET_STATUS:
                resp = ComMsgCode.getRespAck(mMagnetStatusCode);
                break;
            case MCU_BBOX_STATUS:
                resp = ComMsgCode.getRespAck(mBBoxStatusCode);
                break;
            case MCU_CBOX_STATUS:
                resp = ComMsgCode.getRespAck(mCBoxStatusCode);
                break;
            case MCU_BATTERY_LEVEL:
                resp = ComMsgCode.getRespAck(mVoltageStatusCode);
                break;
            default:
                resp = null;
                break;
        }

        return resp;
    }

    public synchronized static String getStatusStr(ComMsgCode.TargetType type) {
        ComMsgCode.RespAck resp = getStatus(type);
        if (resp == null)
            return "";

        return resp.getInfo();
    }

    public synchronized static int getVoltageLevel() {
        String status = getStatusStr(ComMsgCode.TargetType.MCU_BATTERY_LEVEL);
        if (status.equals("critical"))
            return 15;
        else if (status.equals("low"))
            return 30;
        else if (status.equals("normal"))
            return 90;
        else
            return 0;
    }
}
