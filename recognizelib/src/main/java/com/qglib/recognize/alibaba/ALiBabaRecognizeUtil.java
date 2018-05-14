package com.qglib.recognize.alibaba;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.alibaba.idst.nls.realtime.NlsClient;
import com.alibaba.idst.nls.realtime.internal.protocol.NlsRequest;
import com.qglib.recognize.interfaces.RecognizeVoiceInterface;

public class ALiBabaRecognizeUtil {
    private static ALiBabaRecognizeUtil instance;

    private NlsRequest mNlsRequest;
    private NlsClient mNlsClient;

    private int unit_length = 640;
    private byte[] voiceData = new byte[]{};
    private int startCopyLength = 0;

    private ALiBabaRecognizeUtil() {
    }

    public static ALiBabaRecognizeUtil getInstance() {
        if (instance == null) {
            synchronized (ALiBabaRecognizeUtil.class) {
                if (instance == null) instance = new ALiBabaRecognizeUtil();
            }
        }
        return instance;
    }

    public void init(Application appContext, Context context, RecognizeVoiceInterface listener) {
        if (mNlsClient != null) {
            Toast.makeText(context, "SDK已经初始化", Toast.LENGTH_LONG).show();
            return;
        }
        String appkey = "nls-service-shurufa16khz"; //请设置文档中Appkey   nls-service-realtime-8k

        mNlsRequest = new NlsRequest();
        mNlsRequest.setAppkey(appkey);    //appkey请从 "快速开始" 帮助页面的appkey列表中获取
        mNlsRequest.setResponseMode("streaming");//流式为streaming,非流式为normal

        /*设置热词相关属性*/
//        mNlsRequest.setVocabularyId("vocabid");
        /*设置热词相关属性*/

        NlsClient.openLog(true);
        NlsClient.configure(appContext); //全局配置
        mNlsClient = NlsClient.newInstance(context, new MyNlsListener(context, listener), new MyStageListener(), mNlsRequest);                          //实例化NlsClient
        mNlsClient.useDefaultAudioRecord(false);
        mNlsClient.setMaxRecordTime(60000);  //设置最长语音
        mNlsClient.setMaxStallTime(1000);    //设置最短语音
        mNlsClient.setMinRecordTime(500);    //设置最大录音中断时间
        mNlsClient.setRecordAutoStop(false);  //设置VAD
        mNlsClient.setMinVoiceValueInterval(200); //设置音量回调时长
    }

    public void startRecognize(String id, String secret) {
        if (mNlsClient.isStarted()) return;
        mNlsRequest.authorize(id, secret); //请替换为用户申请到的数加认证key和密钥
        mNlsClient.start();
    }

    public void stopRecognize() {
        voiceData = new byte[]{};
        startCopyLength = 0;
        if (!mNlsClient.isStarted()) return;
        mNlsClient.stop();
        mNlsClient = null;
        mNlsRequest = null;
    }

    public void setUnit_length(int length) {
        this.unit_length = length;
    }


    public synchronized void writeData(byte[] data) {
        voiceData = addBytes(voiceData, data);
        while (voiceData.length >= startCopyLength && mNlsClient != null && mNlsClient.isStarted()) {
            int copyLength = voiceData.length - startCopyLength;
            copyLength = copyLength < unit_length ? copyLength : unit_length;
            byte[] sendVoice = new byte[unit_length];
            System.arraycopy(voiceData, startCopyLength, sendVoice, 0, copyLength);
            mNlsClient.sendUserVoice(sendVoice);
            startCopyLength += copyLength;
        }
    }

    private byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }
}
