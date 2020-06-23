package com.example.controller;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;

public class MqttService extends Service {
    private AWSIotMqttManager mpttManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
