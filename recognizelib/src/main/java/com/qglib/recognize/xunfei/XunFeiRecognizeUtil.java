package com.qglib.recognize.xunfei;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.qglib.recognize.callback.RecognizeResult;
import com.qglib.recognize.interfaces.RecognizeVoiceInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class XunFeiRecognizeUtil {
    private static final String TAG = "XunFeiRecognizeUtil";
    private SpeechRecognizer speechRecognizer;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private static XunFeiRecognizeUtil instance;
    private RecognizeVoiceInterface listener;


    private XunFeiRecognizeUtil() {
    }

    public static XunFeiRecognizeUtil getInstance() {
        if (instance == null) {
            synchronized (XunFeiRecognizeUtil.class) {
                if (instance == null) instance = new XunFeiRecognizeUtil();
            }
        }
        return instance;
    }

    public void createUtility(Application context) {
        SpeechUtility.createUtility(context, "appid=5acd9d80");
    }

    public void init(Context context, RecognizeVoiceInterface listener) {
        this.listener = listener;
        speechRecognizer = SpeechRecognizer.createRecognizer(context, mInitListener);
        setParam();
    }

    public void start() {
        speechRecognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        speechRecognizer.startListening(myRecognizerListener);
    }

    public void writeData(byte[] data) {
        if (speechRecognizer == null) return;
        if (!speechRecognizer.isListening()) speechRecognizer.startListening(myRecognizerListener);
        speechRecognizer.writeAudio(data, 0, data.length);
    }

    public void stop() {
        if (speechRecognizer != null) speechRecognizer.stopListening();
        speechRecognizer = null;
    }

    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                if (listener != null) listener.initFailed();
                Log.e(TAG, "初始化失败，错误码：" + code);
            }
        }
    };

    public void setParam() {
        // 清空参数
        speechRecognizer.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语言
        speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        speechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");
        //此处用于设置dialog中不显示错误码信息
        //mIat.setParameter("view_tips_plain","false");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        speechRecognizer.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        speechRecognizer.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        speechRecognizer.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        speechRecognizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        speechRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    private RecognizerListener myRecognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            if (listener != null) listener.volumeChange(i);
        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            printResult(recognizerResult, b);
        }

        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    private void printResult(RecognizerResult results, boolean isLast) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);
        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        Log.d(TAG, "输出结果：" + resultBuffer.toString());
        if (isLast) {
            if (listener != null) listener.onResult(new RecognizeResult(resultBuffer.toString()));
        }
    }
}
