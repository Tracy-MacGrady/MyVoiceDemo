package com.qglib.recognize.alibaba;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.idst.nls.realtime.NlsClient;
import com.alibaba.idst.nls.realtime.NlsListener;
import com.alibaba.idst.nls.realtime.internal.protocol.NlsResponse;
import com.qglib.recognize.callback.RecognizeResult;
import com.qglib.recognize.interfaces.RecognizeVoiceInterface;

public class MyNlsListener extends NlsListener {
    private final String TAG = "MyNlsListener";
    private Context context;
    private RecognizeVoiceInterface listener;

    public MyNlsListener(Context context, RecognizeVoiceInterface listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onRecognizingResult(int status, NlsResponse result) {
        switch (status) {
            case NlsClient.ErrorCode.SUCCESS:
                if (result != null) {
                    if (result.getResult() != null) {
                        Log.i(TAG, "[demo] callback onRecognizResult :" + result.getResult().getText());
                        if (listener != null)
                            listener.onResult(new RecognizeResult(result.getResult().getText()));
                    }
                } else {
                    Log.i(TAG, "[demo] callback onRecognizResult finish!");
                }
                break;
            case NlsClient.ErrorCode.RECOGNIZE_ERROR:
                Toast.makeText(context, "recognizer error", Toast.LENGTH_LONG).show();
                break;
            case NlsClient.ErrorCode.RECORDING_ERROR:
                Toast.makeText(context, "recording error", Toast.LENGTH_LONG).show();
                break;
            case NlsClient.ErrorCode.NOTHING:
                Toast.makeText(context, "nothing", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onServiceStatChanged(boolean isStreamAvailable, boolean isRpcAvailable) {
        super.onServiceStatChanged(isStreamAvailable, isRpcAvailable);
    }
}
