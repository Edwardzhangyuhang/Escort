package com.foxconn.cnsbg.escort.subsys.communication;

import android.content.Context;
import android.util.Log;

import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.google.gson.Gson;

public abstract class ComTxTask<T> extends Thread {
    private static final String TAG = ComTxTask.class.getSimpleName();

    protected volatile int runInterval = 1000; //Default wait time of 1 sec
    protected volatile boolean requestShutdown = false;

    protected Gson gson = CtrlCenter.getGson();
    protected Context mContext;
    protected ComMQ mComMQ;

    @Override
    public void run() {
        //stop this loop by calling the .stop() method is not recommended
        while (!requestShutdown) {
            //collect data
            T data = collectData();

            //then send it to the server
            if (sendData(data)) {
                //send OK, then send cached data as well
                //sent data will be cleared as well
                sendCachedData();
            } else {
                //send fail, then save collected data to cache
                saveCachedData(data);
            }

            //check for location provider and sensor change periodically
            checkTask();

            //then relax
            try {
                Thread.sleep(runInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.w(TAG, "Note that this may not be a bad thing. It may have been stopped on purpose.");
            }
        }
    }

    public void requestShutdown() {
        requestShutdown = true;
    }

    protected abstract T collectData();
    protected abstract boolean sendData(T data);
    protected abstract boolean sendCachedData();
    protected abstract void saveCachedData(T data);
    protected abstract void checkTask();
}
