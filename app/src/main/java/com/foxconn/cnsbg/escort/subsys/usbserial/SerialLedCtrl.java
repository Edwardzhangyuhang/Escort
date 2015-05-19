package com.foxconn.cnsbg.escort.subsys.usbserial;

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

    public static void setLed(SerialCtrl sc, LedPattern gp, LedPattern ap, LedPattern rp) {
        String greenPattern = getLedPatternStr(gp);
        String amberPattern = getLedPatternStr(ap);
        String redPattern = getLedPatternStr(rp);

        sc.write(CMD_CODE_SET_LED_PREFIX + greenPattern + amberPattern + redPattern + "\r\n");
    }

    public static void setTaskStartLed(SerialCtrl sc) {
        setLed(sc, LedPattern.STAY_OFF, LedPattern.BLINK_MEDIUM, LedPattern.STAY_OFF);
    }

    public static void setTaskEndLed(SerialCtrl sc) {
        setLed(sc, LedPattern.BLINK_LOW, LedPattern.STAY_OFF, LedPattern.STAY_OFF);
    }

    public static void setLockStartLed(SerialCtrl sc) {
        setLed(sc, LedPattern.STAY_OFF, LedPattern.BLINK_HIGH, LedPattern.STAY_OFF);
    }

    public static void setCmdSuccessLed(SerialCtrl sc) {
        setLed(sc, LedPattern.STAY_OFF, LedPattern.STAY_OFF, LedPattern.STAY_OFF);
    }

    public static void setCmdFailLed(SerialCtrl sc) {
        setLed(sc, LedPattern.STAY_OFF, LedPattern.STAY_OFF, LedPattern.BLINK_MEDIUM);
    }
}
