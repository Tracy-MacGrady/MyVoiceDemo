package com.qglib.recognize.interfaces;

import com.qglib.recognize.callback.RecognizeResult;

public interface RecognizeVoiceInterface {
    void initFailed();

    void volumeChange(int volume);

    void onResult(RecognizeResult result);
}
