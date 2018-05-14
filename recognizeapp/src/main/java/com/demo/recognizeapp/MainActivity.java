package com.demo.recognizeapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static String PACKAGE_NAME = "";
    private SharedPreferences sharedPreferences;

    private EditText editText;
    private Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.packagename);
        okButton = findViewById(R.id.ok_view);
        okButton.setOnClickListener(this);
        sharedPreferences = getSharedPreferences("APP_SP", MODE_PRIVATE);
        PACKAGE_NAME = sharedPreferences.getString("name", "");
        editText.setText(PACKAGE_NAME);
        initPermission();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_view:
                if (TextUtils.isEmpty(editText.getText())) return;
                PACKAGE_NAME = editText.getText().toString();
                break;
        }
    }
}
