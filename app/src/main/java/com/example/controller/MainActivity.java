package com.example.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.service.autofill.TextValueSanitizer;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView textView, textInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        textInfo = findViewById(R.id.text_info);

        textView = findViewById(R.id.text_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float sensorX , sensorY, sensorZ;

        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            sensorX = sensorEvent.values[0];
            sensorY = sensorEvent.values[1];
            sensorZ = sensorEvent.values[2];

            String strTmp = "加速度センサー\n"
                    + " X: " + sensorX + "\n"
                    + " Y: " + sensorY + "\n"
                    + " Z: " + sensorZ;

            textView.setText(strTmp);

            showInfo(sensorEvent);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void showInfo(SensorEvent sensorEvent) {
        // センサー名
        StringBuffer info = new StringBuffer("Name: ");
        info.append(sensorEvent.sensor.getName());
        info.append("\n");

        // ベンダー名
        info.append("Vender: ");
        info.append(sensorEvent.sensor.getVendor());
        info.append("\n");

        // 型番
        info.append("Type: ");
        info.append(sensorEvent.sensor.getType());

        textInfo.setText(info);
    }
}