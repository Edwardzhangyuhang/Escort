package com.foxconn.cnsbg.escort.subsys.location;

import java.util.Date;

public final class LocData {
    public String device_id;
    public Date time;
    public int battery_level;
    public int signal_strength;
    public String lock_status;
    public String door_status;
    public GPSLoc location;

    public static class GPSLoc {
        public String type;
        public GPSData data;
    }

    public static class GPSData {
        public double latitude;
        public double longitude;
    }
}
