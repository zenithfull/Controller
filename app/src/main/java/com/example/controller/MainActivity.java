package com.example.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.service.autofill.TextValueSanitizer;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView textView, textInfo;
    private WebView cameraView;
    private Button cameraButton;

    protected final static double RAD2DEG = 180/Math.PI;

    float[] rotationMatrix = new float[9];
    float[] gravity = new float[3];
    float[] geomagnetic = new float[3];
    float[] attitude = new float[3];



    private int startupX , startupY, startupZ;
    private int sensorX, sensorY, sensorZ;

    private String direction, leftAndRight;

    private static final int initializePos = 0;

    private static final float forwardActionVal = 20;
    private static final float backActionVal = -20;
    private static final float leftActionVal = -20;
    private static final float rightActionVal = 20;

    MqttService mqttService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        textInfo = findViewById(R.id.text_info);

        textView = findViewById(R.id.text_view);

        cameraView = findViewById(R.id.camera_view);
        cameraView.setWebChromeClient(new WebChromeClient());
        cameraView.setWebViewClient(new WebViewClient());
        cameraView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        cameraView.loadUrl("http://google.com");
        cameraView.getSettings().setJavaScriptEnabled(true);

        cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(cameraButtonClick);

//        mqttService = new MqttService();

        initSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);

//        mqttService.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

//        mqttService.disconnect();

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        switch(event.sensor.getType()){
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomagnetic = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                gravity = event.values.clone();
                break;
        }

        if(geomagnetic != null && gravity != null) {

            SensorManager.getRotationMatrix(
                    rotationMatrix, null,
                    gravity, geomagnetic);

            SensorManager.getOrientation(
                    rotationMatrix,
                    attitude);

            sensorX = (int)(attitude[0] * RAD2DEG);
            sensorY = (int)(attitude[1] * RAD2DEG);
            sensorZ = (int)(attitude[2] * RAD2DEG);

            if (startupX == initializePos) {
                startupX = sensorX;
                startupY = sensorY;
                startupZ = sensorZ;
            }

            if (sensorZ > startupZ + forwardActionVal) {
                direction = "forward";
            } else if (sensorZ < startupZ + backActionVal) {
                direction = "back";
            } else {
                direction = "stop";
            }

            if (sensorX < startupX + leftActionVal) {
                leftAndRight = "right";
            } else if (sensorX > startupX + rightActionVal) {
                leftAndRight = "left";
            } else {
                leftAndRight = "straight";
            }

            String strTmp = "端末角度\n"
                    + " startX: " + startupX + "\n"
                    + " startY: " + startupY + "\n"
                    + " startZ: " + startupZ + "\n"
                    + " X: " + sensorX + "\n"
                    + " Y: " + sensorY + "\n"
                    + " Z: " + sensorZ + "\n"
                    + " direction: " + direction + "\n"
                    + " left / right: " + leftAndRight + "\n";

            textView.setText(strTmp);

//            showInfo(event);

        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        initSensor();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    protected void initSensor(){
        startupX = initializePos;
        startupY = initializePos;
        startupZ = initializePos;
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

    private View.OnClickListener cameraButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (cameraView.getVisibility() == View.VISIBLE) {
                cameraView.setVisibility(View.GONE);
            } else {
                cameraView.setVisibility(View.VISIBLE);
            }
        }
    };
}