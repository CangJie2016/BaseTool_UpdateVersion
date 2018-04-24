package com.zxw.basetool_update;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LogWriter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.zxw.basetool_updateapp.VersionUpdateHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static int REQUEST_PERMISSION = 110;//写入权限

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            showDownLoadDialog();
        }
    }

    private void showDownLoadDialog() {
        String url = "http://data.wuxibus.com/static/defalult/driver/XBusDriver1.0.4.apk";
        String baseUrl = "http://data.wuxibus.com/";
        final String versionName = "1.0.0";
        String path = "/WuXiDriver/";
        String file = "WuXiDriver" + versionName + ".apk";
        String title = "版本更新";
        String provider = "com.cangjie.basetool_update";
        new VersionUpdateHelper(this, title, "ss", false, provider, url, baseUrl, path, file, new VersionUpdateHelper.OnVersionUpdateListener() {
            @Override
            public void downloadFailure(String s) {
                Log.w(TAG, "downloadFailure: " + s );
            }

            @Override
            public void sdCardError(String s) {
                Log.w(TAG, "sdCardError: " + s );

            }

            @Override
            public void nextTime() {
                Log.w(TAG, "nextTime: " );
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showDownLoadDialog();
                } else {
                    Log.w("log", "提示没有权限，安装不了咯");
                }
            }
        }
    }
}
