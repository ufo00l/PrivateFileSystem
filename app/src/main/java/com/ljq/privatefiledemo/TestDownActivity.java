package com.ljq.privatefiledemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTaskListener;
import com.arialyy.aria.core.task.DownloadTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class TestDownActivity extends AppCompatActivity implements DownloadTaskListener{

    private static final String DOWNLOAD_URL = "http://a.xzfile.com/apk4/apkpurev3.17.44_downcc.com.apk";

    ProgressBar progress;
    TextView speed,size;
    Button start,stop,cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
        checkPermission();
    }

    private void initView() {
        progress = findViewById(R.id.progressBar);
        speed = findViewById(R.id.speed);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        cancel = findViewById(R.id.cancel);
        size = findViewById(R.id.size);

        start.setOnClickListener(onClickListener);
        stop.setOnClickListener(onClickListener);
        cancel.setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClick(v);
        }
    };

    private void checkPermission(){
        try {
            String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            int permission = ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 100);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getUrlFileName(String url){
        if(url == null){
            return "";
        }
        if(!url.contains("/")){
            return "";
        }else{
            String[] fileds= url.split("/");
            String name = fileds[fileds.length-1];
            return name;
        }
    }

    public void buttonClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                Aria.download(this)
                        .register();
                Aria.download(this)
                        .load(DOWNLOAD_URL)
                        .setFilePath(getExternalFilesDir("download") + getUrlFileName(DOWNLOAD_URL))
                        .create();
                break;
            case R.id.stop:
                Aria.download(this).load(0).stop();
                break;
            case R.id.cancel:
                Aria.download(this).load(0).cancel();
                break;
        }
    }


        @Override public void onTaskStart(DownloadTask task) {
            Toast.makeText(TestDownActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
            size.setText(Float.valueOf(task.getFileSize()/1024/1024)+"mb");
        }

        @Override public void onTaskStop(DownloadTask task) {
            Toast.makeText(TestDownActivity.this, "停止下载", Toast.LENGTH_SHORT).show();
        }

        @Override public void onTaskCancel(DownloadTask task) {
            Toast.makeText(TestDownActivity.this, "取消下载", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskFail(DownloadTask task, Exception e) {
            Toast.makeText(TestDownActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
        }

        @Override public void onTaskComplete(DownloadTask task) {
            progress.setProgress(100);
            Toast.makeText(TestDownActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
        }

        @Override public void onTaskRunning(DownloadTask task) {

            long speedNum = task.getSpeed();
            int percentNum = task.getPercent();

            Log.d("ljq","speedNum " + speedNum + " percentNum " + percentNum);
            speed.setText(speedNum/1024/1024 + "kb/s");
            progress.setProgress(percentNum);

            //使用转换单位后的速度，需要在aria_config.xml配置文件中将单位转换开关打开
            //https://github.com/AriaLyy/Aria#配置文件设置参数
//            mSpeed.setText(task.getConvertSpeed());
//            mPb.setProgress(task.getPercent());
        }

        @Override
        public void onNoSupportBreakPoint(DownloadTask task) {

        }

        @Override
        public void onWait(DownloadTask task) {

        }

        @Override
        public void onPre(DownloadTask task) {

        }

        @Override
        public void onTaskPre(DownloadTask task) {

        }

        @Override
        public void onTaskResume(DownloadTask task) {

        }


}