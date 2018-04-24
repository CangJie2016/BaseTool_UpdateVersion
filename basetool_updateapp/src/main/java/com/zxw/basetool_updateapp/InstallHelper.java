package com.zxw.basetool_updateapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;

public class InstallHelper {
    /**
     *
     * @param provider "zxzs.ppgj.provider"
     * @param downloadLocalPath  xxx/
     * @param downloadLocalFileName  xxx.apk
     * @param downloadDialog
     */
    public static void installApk(Activity activity, String provider, String downloadLocalPath, String downloadLocalFileName, MaterialDialog downloadDialog) {
        File foder = new File(Environment.getExternalStorageDirectory(), downloadLocalPath);
        File file = new File(foder, downloadLocalFileName);

        Intent intent = new Intent(Intent.ACTION_VIEW);

//判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(activity, provider, file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        if (downloadDialog != null && downloadDialog.isShowing())
            downloadDialog.dismiss();
        activity.startActivity(intent);
        activity.finish();
    }
}