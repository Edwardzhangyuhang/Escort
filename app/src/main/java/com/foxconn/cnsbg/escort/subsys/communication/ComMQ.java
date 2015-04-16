package com.foxconn.cnsbg.escort.subsys.communication;

import android.content.Context;
import android.util.Log;

import com.foxconn.cnsbg.escort.subsys.common.SysConst;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

public class ComMQ {
    private final String TAG = ComMQ.class.getSimpleName();

    private static Context mContext;
    private static BlockingConnection mConn;

    public ComMQ(Context context) {
        mContext = context;
    }

    public boolean init() {
        try {
            MQTT mqtt = new MQTT();
            mqtt.setHost(SysConst.MQ_SERVER_HOST, SysConst.MQ_SERVER_PORT);
            mqtt.setKeepAlive(SysConst.MQ_KEEP_ALIVE);
            //mqtt.setReconnectDelayMax(10000);

            mConn = mqtt.blockingConnection();
        } catch (Exception e) {
            Log.e(TAG, "init fail!");
            return false;
        }

        return true;
    }

    public boolean publish(String topic, String payload) {
        try {
            if (!mConn.isConnected())
                mConn.connect();

            if (mConn.isConnected())
                mConn.publish(topic, payload.getBytes(), QoS.AT_LEAST_ONCE, false);
        } catch (Exception e) {
            Log.w(TAG, "publish fail!");
            return false;
        }

        return true;
    }

    public boolean subscribe(String topic) {
        Topic[] topics = {new Topic(topic, QoS.AT_LEAST_ONCE)};
        try {
            if (!mConn.isConnected())
                mConn.connect();

            if (mConn.isConnected())
                mConn.subscribe(topics);
        } catch (Exception e) {
            Log.w(TAG, "subscribe fail!");
            return false;
        }

        return true;
    }

    public boolean receive(ComCallback<byte[]> cb) {
        Message msg = null;

        try {
            if (!mConn.isConnected())
                mConn.connect();

            if (mConn.isConnected())
                msg = mConn.receive();

            if (msg != null) {
                cb.onSuccess(msg.getPayload());
                msg.ack();
            }
        } catch (Exception e) {
            Log.w(TAG, "receive fail!");
            cb.onFailure(e);
            return false;
        }

        return true;
    }

    public boolean disconnect() {
        try {
            if (mConn.isConnected())
                mConn.disconnect();
        } catch (Exception e) {
            Log.w(TAG, "disconnect fail!");
            return false;
        }

        return true;
    }
}
