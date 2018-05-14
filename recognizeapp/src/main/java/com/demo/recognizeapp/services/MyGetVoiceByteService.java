package com.demo.recognizeapp.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.demo.recognizeapp.IGetBytesAIDL;
import com.demo.recognizeapp.MainActivity;
import com.demo.testapp.IActionAIDL;
import com.qglib.recognize.callback.RecognizeResult;
import com.qglib.recognize.interfaces.RecognizeVoiceInterface;
import com.qglib.recognize.xunfei.XunFeiRecognizeUtil;


public class MyGetVoiceByteService extends Service implements RecognizeVoiceInterface {
    private boolean isFirst = true;
    private IActionAIDL iActionAIDL;
    private String voicePath = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        IBinder iBinder = new IGetBytesAIDL.Stub() {
            @Override
            public void sendVoiceByte(byte[] data) throws RemoteException {
                Message msg = Message.obtain();
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        Intent intent2 = new Intent();
        ComponentName componentName = new ComponentName(MainActivity.PACKAGE_NAME, MainActivity.PACKAGE_NAME + ".services.MyActionService");
        intent2.setComponent(componentName);
        bindService(intent2, serviceConnection, BIND_AUTO_CREATE);
        return iBinder;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iActionAIDL = IActionAIDL.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iActionAIDL = null;
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] data = (byte[]) msg.obj;
            if (isFirst) {
                isFirst = false;
                XunFeiRecognizeUtil.getInstance().init(getApplicationContext(), MyGetVoiceByteService.this);
                XunFeiRecognizeUtil.getInstance().start();
            }
            XunFeiRecognizeUtil.getInstance().writeData(data);
        }
    };

    @Override
    public boolean onUnbind(Intent intent) {
        unbindService(serviceConnection);
        XunFeiRecognizeUtil.getInstance().stop();
        return super.onUnbind(intent);
    }

    @Override
    public void initFailed() {

    }

    @Override
    public void volumeChange(int volume) {
        try {
            Log.e("onVolumeChanged", "dddddddddddddd" + volume);
            if (volume > 5) {
                iActionAIDL.voiceAction(2, "");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResult(RecognizeResult result) {
        try {
            if (iActionAIDL == null) return;
            Log.e(this.getClass().getSimpleName(), "====" + result.getText().toString());
            int action = 0;//action值为1的时候播放音频  为2的时候停止播放
            if (result.getText().trim().startsWith("播放")) {
                action = 1;
                voicePath = Environment.getExternalStorageDirectory() + "/msc/dddd.3gpp";
                iActionAIDL.voiceAction(action, voicePath);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
