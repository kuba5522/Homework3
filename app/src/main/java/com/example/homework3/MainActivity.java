package com.example.homework3;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import java.util.List;

public class MainActivity extends AppCompatActivity {
    static public SensorManager SensorMenager;
    static List <Sensor> SensorList;
    static final public String SENSOR_TYPE = "sensorType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        SensorMenager = (SensorManager) getSystemService ( Context.SENSOR_SERVICE );
        SensorList = SensorMenager.getSensorList ( Sensor.TYPE_ALL );
        setContentView ( R.layout.activity_main );
    }


    public void Start(View view) {
        Intent sensIntent = new Intent ( MainActivity.this, SensorActivity.class );
        sensIntent.putExtra ( SENSOR_TYPE, Sensor.TYPE_ACCELEROMETER );
        startActivity ( sensIntent );

    }
}
