package com.foxconn.cnsbg.escort.subsys.updater;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.foxconn.cnsbg.escort.common.SysPref;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.util.Date;

import cn.trinea.android.common.entity.HttpRequest;
import cn.trinea.android.common.util.DownloadManagerPro;
import cn.trinea.android.common.util.HttpUtils;
import cn.trinea.android.common.util.PackageUtils;
import cn.trinea.android.common.util.ShellUtils;

public class SysUpdater {
    private static Gson gson = CtrlCenter.getGson();

    public enum UpdateState {
        RUNNING,
        SUCCESS,
        FAIL
    }

    public static class VerInfo {
        public Date time;
        public String version;
        public String url;
    }

    private static boolean isUpdating = false;

    public static UpdateState checkUpdate(Context context) {
        if (!PackageUtils.isSystemApplication(context) && !ShellUtils.checkRootPermission())
            return UpdateState.FAIL;

        if (isUpdating)
            return UpdateState.RUNNING;

        String verUri = "http://" + SysPref.HTTP_SERVER_HOST + "/tracker/Escort/Appinfo";
        HttpRequest request = new HttpRequest(verUri);
        request.setConnectTimeout(1000);
        request.setReadTimeout(1000);
        String newVerStr = HttpUtils.httpGetString(request);

        try {
            VerInfo newVerInfo = gson.fromJson(newVerStr, VerInfo.class);
            int newVerCode = Integer.parseInt(newVerInfo.version);
            int curVerCode = PackageUtils.getAppVersionCode(context);
            if (curVerCode < newVerCode) {
                new UpdateThread(context, newVerInfo.url).start();
                return UpdateState.RUNNING;
            }

            return UpdateState.SUCCESS;
        } catch (JsonParseException e) {
            Log.w("checkUpdate:", "JsonParseException");
        } catch (NullPointerException e) {
            Log.w("checkUpdate:", "NullPointerException");
        }

        return UpdateState.FAIL;
    }

    private static class UpdateThread extends Thread {
        private Context mContext;
        private DownloadManagerPro mDownloadManagerPro;
        private String mDownloadPath;
        private long mDownloadId;

        public UpdateThread(Context context, String url) {
            mContext = context;

            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            mDownloadManagerPro = new DownloadManagerPro(dm);

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "escort.apk");
            mDownloadPath = Environment.DIRECTORY_DOWNLOADS + "/" + "escort.apk";

            mDownloadId = dm.enqueue(request);
        }

        @Override
        public void run() {
            isUpdating = true;

            while (isUpdating) {
                switch (mDownloadManagerPro.getStatusById(mDownloadId)) {
                    case DownloadManager.STATUS_PENDING:
                        break;
                    case DownloadManager.STATUS_RUNNING:
                        break;
                    case DownloadManager.STATUS_PAUSED:
                        mDownloadManagerPro.resumeDownload(mDownloadId);
                        break;
                    case DownloadManager.STATUS_FAILED:
                        isUpdating = false;
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        PackageUtils.install(mContext, mDownloadPath);
                        isUpdating = false;
                        break;
                    default:
                        break;
                }

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
