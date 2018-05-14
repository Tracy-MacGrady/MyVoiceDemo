package com.qglib.recognize.callback;

public class RecognizeResult {
    public String text;

    public RecognizeResult(String text) {
        setText(text);
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
