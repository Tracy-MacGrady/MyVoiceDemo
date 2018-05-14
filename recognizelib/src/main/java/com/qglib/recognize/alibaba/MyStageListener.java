package com.qglib.recognize.alibaba;

import android.util.Log;

import com.alibaba.idst.nls.realtime.NlsClient;
import com.alibaba.idst.nls.realtime.StageListener;

public class MyStageListener extends StageListener {
    @Override
    public void onStartRecognizing(NlsClient recognizer) {
        super.onStartRecognizing(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onStopRecognizing(NlsClient recognizer) {
        super.onStopRecognizing(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onStartRecording(NlsClient recognizer) {
        super.onStartRecording(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onStopRecording(NlsClient recognizer) {
        super.onStopRecording(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onVoiceVolume(int volume) {
        Log.e("onVoiceVolume", "=========volume=" + volume);
        super.onVoiceVolume(volume);
    }
}
