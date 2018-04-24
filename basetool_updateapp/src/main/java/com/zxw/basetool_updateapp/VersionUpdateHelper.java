package com.zxw.basetool_updateapp;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by 李振强 on 2017/8/28.
 */

public class VersionUpdateHelper {
    OnVersionUpdateListener mListener;
    private MaterialDialog tipsDialog;
    private MaterialDialog downloadDialog;


    /**
     *
     final String versionName = "1.0.0";
     String path =  "/WuXiDriver/";
     String file = "WuXiDriver" + versionName + ".apk";
     * @param listener callback
     */

    public VersionUpdateHelper(final Activity mActivity, String title, String message,boolean isForce, final String provider,
                               final String downUrl, final String baseUrl, final String downloadLocalPath, final String downloadLocalFileName, final OnVersionUpdateListener listener){
        mListener = listener;
        if (isForce){
            download(mActivity, downUrl, baseUrl, downloadLocalPath, downloadLocalFileName, provider);
        }else{
            tipsDialog = new MaterialDialog.Builder(mActivity)
                    .title(title)
                    .content(message)
                    .positiveText("立刻下载")
                    .negativeText("下次再说")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                            download(mActivity, downUrl, baseUrl, downloadLocalPath, downloadLocalFileName, provider);
                            if (dialog.isShowing())
                                dialog.dismiss();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            listener.nextTime();
                            if (dialog.isShowing())
                                dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    private void download(final Activity mActivity, String downUrl, String baseUrl, final String downloadLocalPath, final String downloadLocalFileName, final String provider) {
        downloadDialog = new MaterialDialog.Builder(mActivity)
                .title("正在下载")
                .progress(false, 100, false)
                .cancelable(false)
                .show();
        new DownLoadApkHelper(downUrl, baseUrl, downloadLocalPath, downloadLocalFileName, new DownLoadApkHelper.OnDownloadApkListener() {
            @Override
            public void downloadSuccess() {
                downloadDialog.setContent("下载完成，正在安装");
                InstallHelper.installApk(mActivity, provider, downloadLocalPath, downloadLocalFileName, downloadDialog);
            }

            @Override
            public void failure(String s) {
                mListener.downloadFailure(s);
            }

            @Override
            public void process(long currentLength, long sumLength) {
                if (downloadDialog != null && downloadDialog.isShowing()) {
                    float percent = (float) currentLength / (float) sumLength;
                    int progress = Math.round(percent * 100);
                    downloadDialog.setProgress(progress);
                }
            }

            @Override
            public void sdCardError(String s) {
                mListener.sdCardError(s);
            }
        });
    }

    //    public void dismiss(){
//        if (tipsDialog !)
//        tipsDialog.dismiss();
//    }
    public interface OnVersionUpdateListener {
        void downloadFailure(String s);
        void sdCardError(String s);
        void nextTime();
    }
}
