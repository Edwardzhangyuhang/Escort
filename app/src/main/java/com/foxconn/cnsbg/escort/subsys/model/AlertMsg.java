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

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        AlertMsg data = AlertMsg.class.cast(obj);
        if (!data.device_id.equals(device_id))
            return false;

        if (data.time.getTime() != time.getTime())
            return false;

        if (!data.alert.type.equals(alert.type))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (device_id.hashCode() + time.hashCode());
    }
}
