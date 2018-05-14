package com.demo.testapp;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.demo.recognizeapp.IGetBytesAIDL;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private IGetBytesAIDL bytesAIDL;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        findViewById(R.id.start_record_view).setOnClickListener(this);
        findViewById(R.id.stop_record_view).setOnClickListener(this);
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
        };
        ArrayList<String> toApplyList = new ArrayList<String>();
        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.e(this.getClass().getSimpleName(), "permissions==" + permissions);
        Log.e(this.getClass().getSimpleName(), "grantResults==" + grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (intent != null)
            unbindService(serviceConnection);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bytesAIDL = IGetBytesAIDL.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bytesAIDL = null;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_record_view:
                if (intent == null) {
                    intent = new Intent();
                    ComponentName componentName = new ComponentName("com.demo.recognizeapp", "com.demo.recognizeapp.services.MyGetVoiceByteService");
                    intent.setComponent(componentName);
                    bindService(intent, serviceConnection, BIND_AUTO_CREATE);
                }
                if (AliBabaAudioRecordTest.getInstance().isStart()) return;
                AliBabaAudioRecordTest.getInstance().startRecord(Environment.getExternalStorageDirectory() + "/msc/gggg.pcm", handler);
                break;
            case R.id.stop_record_view:
                if (intent != null) {
                    intent = null;
                    unbindService(serviceConnection);
                }
                AliBabaAudioRecordTest.getInstance().stopRecord();
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            synchronized (msg) {
                try {
                    byte[] data = (byte[]) msg.obj;
                    if (bytesAIDL != null) bytesAIDL.sendVoiceByte(data);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }
    };

}
