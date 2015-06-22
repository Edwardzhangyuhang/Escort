package com.foxconn.cnsbg.escort.common;

import android.content.Context;

import com.emildiego.devicediscovery.cpu;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;
import com.foxconn.cnsbg.escort.subsys.model.SysInfo;
import com.foxconn.cnsbg.escort.common.SysUtil;
import com.google.gson.Gson;

/**
 * Created by Edward zhang on 2015/6/17.
 */
public class Sysmonitor {

    private String mlog = "Sysmonitor";

    private SysInfo sysinfo ;

    private Context mContext;

    public Sysmonitor(Context context) {
        mContext = context;
        sysinfo = new SysInfo();
    }

    private cpu cpuinfo = new cpu(mContext,mlog);

    private static final long MB = 1024L;

    private float cpu_idle = 0.0F;

    private long mem_avail = 0l;

    public boolean query() {

        int pid = android.os.Process.myPid();

        sysinfo.total_mem = SysUtil.getTotalMemory(mContext) / MB + "MB";

        mem_avail = SysUtil.getAvailMemory(mContext) / MB;

        sysinfo.avail_mem = mem_avail + "MB";

        sysinfo.escort_mem = SysUtil.getPidMemorySize(pid, mContext) / MB + "MB";


        cpuinfo.query();
        //System.out.println(cpuinfo.summaryString());
        sysinfo.user_cpu_utilized = cpuinfo.getUserUsage() + "%";

        sysinfo.system_cpu_utilized = cpuinfo.getSystemUsage() + "%";

        cpu_idle = cpuinfo.getIdle();

        sysinfo.idle_cpu = cpu_idle + "%";

       return true;
    }

    public String getSysinfo(){

        Gson json = CtrlCenter.getGson();

        String sys = json.toJson(this.sysinfo,SysInfo.class);

        return sys;
    }

    public float getCpuIdle(){
        return this.cpu_idle;
    }

    public long getMemoryAvail(){
        return this.mem_avail;
    }
}
