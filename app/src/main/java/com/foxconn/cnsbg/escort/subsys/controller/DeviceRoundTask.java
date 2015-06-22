package com.foxconn.cnsbg.escort.subsys.controller;

import android.content.Context;

import com.foxconn.cnsbg.escort.common.SysPref;
import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.common.Sysmonitor;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;
import com.foxconn.cnsbg.escort.subsys.communication.ComMsg;
import com.foxconn.cnsbg.escort.subsys.communication.ComMsgCode;
import com.foxconn.cnsbg.escort.subsys.communication.ComRxTask;
import com.foxconn.cnsbg.escort.subsys.model.SysInfo;
import com.google.gson.Gson;

public class DeviceRoundTask extends Thread {
    private static final String TAG = ComRxTask.class.getSimpleName();

    private boolean requestShutdown = false;


    private Context mContext;
    private ComMQ mComMQ;


    public DeviceRoundTask(Context context, ComMQ mq) {
        mContext = context;
        mComMQ = mq;
    }



    @Override
    public void run() {
        while (!requestShutdown) {
            mComMQ.checkConnection();

            boolean statusChanged;
            ComMsgCode.RespAck resp;


            Sysmonitor monitor = new Sysmonitor(mContext);

            monitor.query();

            //System.out.println(monitor.getSysinfo());
            resp = ComMsgCode.getRespAck(ComMsgCode.ACK_STR_GET_SYSTEM_INFO_OK);

            if (resp !=null && monitor.getSysinfo() != null)
                resp.setInfo(monitor.getSysinfo());
            DeviceStatus.setStatus(resp);

            if (monitor.getCpuIdle() < SysPref.DEV_CPU_USEAGE_WARNING || monitor.getMemoryAvail() < SysPref.DEV_MEMORY_USEAGE_WARNING) {
                ComMsg.sendAlertMsg(mComMQ, resp, 500);
            }

            resp = ComMsgCode.getRespAck(ComMsgCode.ACK_STR_GET_TEMPERATURE_OK);
            int temp = SysUtil.getBatteryTemperature(mContext);
            if (resp != null && temp != 0)
                resp.setInfo(Integer.toString(temp / 10));
            statusChanged = DeviceStatus.setStatus(resp);
            if (statusChanged)
                ComMsg.sendAlertMsg(mComMQ, resp, 500);


            resp = ComMsgCode.getRespAck(ComMsgCode.ACK_STR_GET_BATTERY_OK);
            int battery = SysUtil.getBatteryLevel(mContext);
            if (resp != null)
                resp.setInfo(Integer.toString(battery));
            statusChanged = DeviceStatus.setStatus(resp);
            if (statusChanged)
                ComMsg.sendAlertMsg(mComMQ, resp, 500);

            try {
                Thread.sleep(500);
                //System.out.println("CPU Usage: " + monitor.cpuTimes().getCpuUsage(cputime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void requestShutdown() {
        requestShutdown = true;
    }
}
