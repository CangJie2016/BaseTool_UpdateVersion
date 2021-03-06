package com.zxw.basetool_updateapp;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

// 负责下载apk
public class DownLoadApkHelper {
    public DownloadFile apiStore;
    private Call<ResponseBody> call;
    private String url;
    private OnDownloadApkListener onDownloadApkListener;

    public DownLoadApkHelper(String downUrl, String baseUrl, String downloadLocalPath, String downloadLocalFileName, OnDownloadApkListener listener){
        url = downUrl;
        onDownloadApkListener = listener;
        setHttp(baseUrl);
        setCallBack(downloadLocalPath, downloadLocalFileName);
    }

    private void setHttp(String baseUrl){
        Retrofit.Builder retrofitBuilder =
                // 获取一个实例
                new Retrofit.Builder()
                        // 使用RxJava作为回调适配器
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        // 添加Gson转换器
                        .addConverterFactory(GsonConverterFactory.create())
                        // 设置baseUrl{以"/"结尾}
                        .baseUrl(baseUrl);
        // Retrofit文件下载进度显示的解决方法
        OkHttpClient.Builder builder = ProgressHelper.addProgress(null).connectTimeout(60, TimeUnit.SECONDS);

        ProgressHelper.setProgressHandler(new DownloadProgressHandler() {

            @Override
            protected void onProgress(long bytesRead, long contentLength, boolean done) {
                onDownloadApkListener.process(bytesRead, contentLength);
            }
        });

        apiStore = retrofitBuilder
                .client(builder.build())
                .build().create(DownloadFile.class);
    }

    private void setCallBack(final String downloadLocalPath, final String downloadLocalFileName){
        call = apiStore.getFile(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    InputStream is = response.body().byteStream();
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//判断SD卡是否挂载
                        File foder = new File(Environment.getExternalStorageDirectory(), downloadLocalPath);
                        Log.w("log", foder.getAbsolutePath());
                        File file = new File(foder, downloadLocalFileName);
                        Log.w("log", file.getAbsolutePath());
                        if (!foder.exists()) {
                            boolean mkdirs = foder.mkdirs();
                            Log.w("log", "mkdirs" + mkdirs);
                        }
                        FileOutputStream fos = new FileOutputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(is);
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = bis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                            fos.flush();
                        }
                        fos.close();
                        bis.close();
                        is.close();

                        onDownloadApkListener.downloadSuccess();
                    }else {
                        onDownloadApkListener.sdCardError("请检查你的SD卡");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onDownloadApkListener.failure("更新失败");
            }
        });
    }


    public interface OnDownloadApkListener{
        void downloadSuccess();
        void failure(String info);
        void process(long currentLength, long maxLength);
        void sdCardError(String info);
    }

    public interface DownloadFile {
        /**
         * 下载文件
         */
        @GET
        Call<ResponseBody> getFile(@Url String url);
    }

}
