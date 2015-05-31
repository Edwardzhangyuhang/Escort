package com.foxconn.cnsbg.escort.subsys.communication;

import android.content.Context;

import com.foxconn.cnsbg.escort.common.SysPref;
import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.controller.DeviceStatus;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialStatus;

import org.fusesource.hawtdispatch.Dispatch;
import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ComMQ {
    private static final String TAG = ComMQ.class.getSimpleName();

    private Context mContext;
    private FutureConnection mConn;
    Future<Message> mReceive = null;

    private boolean mReady = false;

    public ComMQ(Context context) {
        mContext = context;
    }

    public boolean init(List<String> subscribes) {
        MQTT mqtt = new MQTT();

        try {
            mqtt.setHost(SysPref.MQ_SERVER_HOST, SysPref.MQ_SERVER_PORT);
        } catch (Exception e) {
            return false;
        }

        //setClientId(CtrlCenter.getUDID());
        //setCleanSession(false);
        mqtt.setKeepAlive(SysPref.MQ_KEEP_ALIVE);
        mqtt.setConnectAttemptsMax(SysPref.MQ_CONNECT_ATTEMPTS);
        mqtt.setReconnectAttemptsMax(SysPref.MQ_RECONNECT_ATTEMPTS);
        mqtt.setReconnectDelay(SysPref.MQ_RECONNECT_DELAY);
        mqtt.setReconnectDelayMax(SysPref.MQ_RECONNECT_MAX_DELAY);
        mqtt.setDispatchQueue(Dispatch.createQueue());

        mqtt.setWillTopic(SysPref.MQ_TOPIC_CONNECTION + CtrlCenter.getUDID());
        mqtt.setWillMessage("offline");

        mConn = mqtt.futureConnection();
        mConn.connect();

        if (subscribes != null) {
            for (String topic : subscribes)
                subscribe(topic, QoS.EXACTLY_ONCE);
        }

        mReceive = mConn.receive();

        return true;
    }

    public void checkConnection() {
        boolean ready = mConn.isConnected();
        if (mReady != ready) {
            mReady = ready;
            SysUtil.debug(mContext, "MQ Ready:" + ready);

            if (ready) {
                ComMsg.sendOnlineMsg(this, 500);

                //trigger status reporting after connection is back
                SerialStatus.initStatus();
                DeviceStatus.initStatus();
            }
        }
    }

    public boolean publish(String topic, String payload, long milliseconds) {
        checkConnection();

        try {
            mConn.publish(topic, payload.getBytes(), QoS.AT_MOST_ONCE, false)
                    .await(milliseconds, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            return false;
        }

        SysUtil.debug(mContext, "MQ publish:" + payload);
        return true;
    }

    public boolean subscribe(String topic, QoS qos) {
        checkConnection();

        Topic[] topics = {new Topic(topic, qos)};
        mConn.subscribe(topics);

        return true;
    }

    public String receive(long milliseconds) {
        String result;

        checkConnection();

        try {
            Message msg = mReceive.await(milliseconds, TimeUnit.MILLISECONDS);
            result = new String(msg.getPayload());
            msg.ack();
            mReceive = mConn.receive();
        } catch (Exception e) {
            return null;
        }

        SysUtil.debug(mContext, "MQ receive:" + result);
        return result;
    }

    public boolean disconnect() {
        mConn.disconnect();

        return true;
    }
}
