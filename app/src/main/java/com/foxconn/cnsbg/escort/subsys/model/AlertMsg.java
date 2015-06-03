package com.foxconn.cnsbg.escort.subsys.model;

import java.util.Date;

public class AlertMsg {
    public String device_id;
    public Date time;
    public AlertData alert;

    public static class AlertData {
        public String type;
        public String level;
        public String info;
    }
}
