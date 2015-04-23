package com.foxconn.cnsbg.escort.subsys.location;

import java.util.Date;

public final class BLEData {
    public String device_id;
    public Date time;
    public int battery_level;
    public int signal_strength;
    public String lock_status;
    public String door_status;
    public BLELoc location;

    public static class BLELoc {
        public String type;
        public DeviceData data;
    }

    public static class DeviceData {
        public String mac;
        public int rssi;
    }
}
