package com.mysystem.recorder.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mysystem.recorder.IActionAIDL;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MyActionService extends Service implements MediaPlayer.OnPreparedListener {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        IBinder iBinder = new IActionAIDL.Stub() {
            @Override
            public void voiceAction(int action, String voicePath) throws RemoteException {
                Log.e("aaaaaaaaaaaaaa", "ddddddddddd" + action);
                if (action == 1) {
                    playMusic(voicePath);
                }
                if (action == 2) {
                    stopPlayMusic();
                }
            }


        };
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer = null;
        }
        return super.onUnbind(intent);
    }

    private MediaPlayer mediaPlayer;

    public void playMusic(String voicePath) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) return;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        try {
            if (mediaPlayer == null) mediaPlayer = new MediaPlayer();
            File filePath = new File(voicePath);
            if (!filePath.exists()) {
                return;
            }
            FileInputStream is = new FileInputStream(filePath);
            mediaPlayer.setDataSource(is.getFD());
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPlayMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            Log.e("ffffffffffffff", "gggggggggggg===" + mediaPlayer.getCurrentPosition());
            mediaPlayer.stop();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
    }
}
