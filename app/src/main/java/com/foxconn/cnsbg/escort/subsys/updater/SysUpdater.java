package com.foxconn.cnsbg.escort.subsys.updater;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.foxconn.cnsbg.escort.common.SysPref;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.model.VerInfo;
import com.google.gson.Gson;

import java.io.File;

import cn.trinea.android.common.entity.HttpRequest;
import cn.trinea.android.common.util.DownloadManagerPro;
import cn.trinea.android.common.util.FileUtils;
import cn.trinea.android.common.util.HttpUtils;
import cn.trinea.android.common.util.PackageUtils;
import cn.trinea.android.common.util.ShellUtils;

public class SysUpdater {
    private static Gson gson = CtrlCenter.getGson();
    private static final String VERSION_URL = "http://" + SysPref.HTTP_SERVER_HOST + "/tracker/Escort/Appinfo";
    private static final String DOWNLOAD_FILE = "escort.apk";

    public enum UpdateState {
        RUNNING,
        SUCCESS,
        FAIL
    }

    private static boolean isUpdating = false;

    public static UpdateState checkUpdate(Context context) {
        if (!PackageUtils.isSystemApplication(context) && !ShellUtils.checkRootPermission())
            return UpdateState.FAIL;

        if (isUpdating)
            return UpdateState.RUNNING;

        HttpRequest request = new HttpRequest(VERSION_URL);
        request.setConnectTimeout(1000);
        request.setReadTimeout(1000);
        String newVerStr = HttpUtils.httpGetString(request);

        try {
            VerInfo newVerInfo = gson.fromJson(newVerStr, VerInfo.class);
            int newVerCode = Integer.parseInt(newVerInfo.version);
            int curVerCode = PackageUtils.getAppVersionCode(context);
            if (curVerCode < newVerCode) {
                String url = "http://" + SysPref.HTTP_SERVER_HOST + newVerInfo.url;
                new UpdateThread(context, url).start();
                return UpdateState.RUNNING;
            }

            return UpdateState.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return UpdateState.FAIL;
    }

    private static class UpdateThread extends Thread {
        private Context mContext;
        private DownloadManagerPro mDownloadManagerPro;
        private long mDownloadId;

        public UpdateThread(Context context, String url) {
            mContext = context;

            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            mDownloadManagerPro = new DownloadManagerPro(dm);

            File file =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String filename = file.getAbsolutePath() + "/" + DOWNLOAD_FILE;
            FileUtils.deleteFile(filename);

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, DOWNLOAD_FILE);
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
                        String filePath = mDownloadManagerPro.getFileName(mDownloadId);
                        PackageUtils.install(mContext, filePath);
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
