package com.example.homework3;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.Path;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.animation.DynamicAnimation;
import android.support.animation.FlingAnimation;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Random;
import static java.lang.Math.abs;
import static java.lang.Math.min;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {
    private String[] answer_array;
    private Sensor mSensor;
    private int sensorType;
    private long lastUpdate = -1;
    private ImageView getBallImgViewFront;
    private int screenWidth;
    private int screenHeight;
    private int imgEdgeSize;
    private boolean layoutReady;
    private ConstraintLayout mainContainer;
    private Path upPath;
    private Path downPath;
    private boolean animFlag = false;
    private TextView textView;
    private int x = 0;
    private int y = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_sensor );
        textView =  findViewById ( R.id.textView );
        answer_array = getResources ().getStringArray ( R.array.answers );
        getBallImgViewFront = findViewById ( R.id.ballImageViewFull);
        Intent receivedIntent = getIntent ();
        if(receivedIntent != null) {
            sensorType = receivedIntent.getIntExtra ( MainActivity.SENSOR_TYPE, -1 );
            if (sensorType != -1) {
                mSensor = MainActivity.SensorMenager.getDefaultSensor ( sensorType );
                if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                    getBallImgViewFront.setVisibility ( View.VISIBLE );
                }
            }
            layoutReady = false;
            mainContainer = findViewById ( R.id.sensor_container );
            mainContainer.getViewTreeObserver ().addOnGlobalLayoutListener ( new ViewTreeObserver.OnGlobalLayoutListener () {
                @Override
                public void onGlobalLayout() {
                    imgEdgeSize = getBallImgViewFront.getWidth ();
                    screenWidth = mainContainer.getWidth ();
                    screenHeight = mainContainer.getHeight ();
                    float rectY = (screenHeight - imgEdgeSize) / 2f;
                    float rectHeight = screenHeight - rectY - imgEdgeSize;
                    float rectWidth = min ( screenWidth - imgEdgeSize, rectHeight );
                    float rectX = (screenWidth - rectWidth - imgEdgeSize) / 2;
                    RectF animRect = new RectF ( rectX, rectY, rectX + rectWidth, rectY + rectHeight );
                    upPath = new Path ();
                    downPath = new Path ();
                    upPath.arcTo ( animRect, 90f, -180f, true );
                    downPath.arcTo ( animRect, 270f, -108f, true );
                    mainContainer.getViewTreeObserver ().removeOnGlobalLayoutListener ( this );
                    layoutReady = true;
                }
            } );
        }
    }




    private void handleAccelerationSensor(final float sensorValue1, float sensorValue2, float sensorvalue3)
    {
        x = (int ) getBallImgViewFront.getX ();
        y = (int ) getBallImgViewFront.getY ();
        if(!animFlag) {
            FlingAnimation flingX = new FlingAnimation ( getBallImgViewFront, DynamicAnimation.X );
            final FlingAnimation flingY = new FlingAnimation ( getBallImgViewFront, DynamicAnimation.Y );

            FlingAnimation flingX1 = new FlingAnimation ( textView, DynamicAnimation.X );
            FlingAnimation flingY1 = new FlingAnimation ( textView, DynamicAnimation.Y );
            if ((abs ( sensorValue1 ) > 1) && (abs ( sensorValue2 ) > 1)) {
                animFlag = true;
                flingY.setStartVelocity ( sensorValue2 * screenHeight / 2f ).setMinValue ( 5 ).setMaxValue ( screenHeight - imgEdgeSize - 5 ).setFriction ( 2f );
                flingX.setStartVelocity ( -1 * sensorValue1 * screenWidth / 2f ).setMinValue ( 5 ).setMaxValue ( screenWidth - imgEdgeSize - 5 ).setFriction ( 2f );
                flingY1.setStartVelocity ( sensorValue2 * screenHeight / 2f ).setMinValue ( 5 ).setMaxValue ( screenHeight - imgEdgeSize - 5 ).setFriction ( 2f );
                flingX1.setStartVelocity ( -1 * sensorValue1 * screenWidth / 2f ).setMinValue ( 5 ).setMaxValue ( screenWidth - imgEdgeSize - 5 ).setFriction ( 2f );
                flingX.start ();
                flingY.start ();
                flingX1.start ();
                flingY1.start ();
                animFlag = false;

            }
            TextView textView = findViewById ( R.id.textView );
            if (flingX.isRunning () && flingY.isRunning () && getBallImgViewFront.getX () != x && getBallImgViewFront.getY () != y) {
                textView.setTypeface ( Typeface.DEFAULT, Typeface.BOLD);
                textView.setTextSize ( 30 );
                textView.setText ( "8" );
            }
            else
            {
                if(textView.getText () == "8") {
                    textView.setTypeface ( Typeface.DEFAULT, Typeface.NORMAL );
                    textView.setTextSize ( 10 );
                    Random random = new Random ();
                    if (sensorValue1 < sensorvalue3 && sensorValue2 < sensorvalue3)
                        textView.setText ( answer_array[(random.nextInt ( 6 ))] );
                    if (sensorValue2 < sensorValue1 && sensorvalue3 < sensorValue1)
                        textView.setText ( answer_array[(random.nextInt ( 7 ) + 6)] );
                    if (sensorValue1 < sensorValue2 && sensorvalue3 < sensorValue2)
                        textView.setText ( answer_array[random.nextInt ( 6 ) + 14] );
                }
            }
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(lastUpdate == -1)
            lastUpdate = event.timestamp;
        else
            lastUpdate = event.timestamp;
        if(layoutReady){
            handleAccelerationSensor ( event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {

        super.onResume ();
        if(mSensor != null)
            MainActivity.SensorMenager.registerListener ( this, mSensor, 100000 );
    }

    @Override
    protected void onPause() {

        super.onPause ();

        if(mSensor != null)
            MainActivity.SensorMenager.unregisterListener ( this, mSensor );
    }
}
