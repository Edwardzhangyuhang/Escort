package com.foxconn.cnsbg.escort.subsys.usbserial;

import android.content.Context;

import com.foxconn.cnsbg.escort.common.SysUtil;

public class SerialLedCtrl {
    public static final String CMD_CODE_SET_LED_PREFIX = "sl_";

    public enum LedPattern {
        STAY_OFF,
        STAY_ON,
        BLINK_LOW,
        BLINK_MEDIUM,
        BLINK_HIGH
    }

    private static String getLedPatternStr(LedPattern pattern) {
        switch (pattern) {
            case STAY_OFF:
                return "f";
            case STAY_ON:
                return "n";
            case BLINK_LOW:
                return "l";
            case BLINK_MEDIUM:
                return "m";
            case BLINK_HIGH:
                return "h";
            default:
                return "f";
        }
    }

    public static void setLed(Context context, SerialCtrl sc, LedPattern gp, LedPattern ap, LedPattern rp) {
        String greenPattern = getLedPatternStr(gp);
        String amberPattern = getLedPatternStr(ap);
        String redPattern = getLedPatternStr(rp);

        String ledCmd = CMD_CODE_SET_LED_PREFIX + greenPattern + amberPattern + redPattern;
        sc.write(ledCmd + "\r\n");
        SysUtil.debug(context, "setLed:" + ledCmd);
    }

    public static void setActiveLed(Context context, SerialCtrl sc) {
        setLed(context, sc, LedPattern.STAY_OFF, LedPattern.BLINK_MEDIUM, LedPattern.STAY_OFF);
    }

    public static void setIdleLed(Context context, SerialCtrl sc) {
        setLed(context, sc, LedPattern.BLINK_LOW, LedPattern.STAY_OFF, LedPattern.STAY_OFF);
    }

    public static void setCmdFailLed(Context context, SerialCtrl sc) {
        setLed(context, sc, LedPattern.STAY_OFF, LedPattern.STAY_OFF, LedPattern.BLINK_MEDIUM);
    }
}
