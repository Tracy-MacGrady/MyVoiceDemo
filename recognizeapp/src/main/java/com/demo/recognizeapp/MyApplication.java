package com.demo.recognizeapp;

import android.app.Application;

import com.qglib.recognize.xunfei.XunFeiRecognizeUtil;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        XunFeiRecognizeUtil.getInstance().createUtility(this);
        super.onCreate();
    }
}
