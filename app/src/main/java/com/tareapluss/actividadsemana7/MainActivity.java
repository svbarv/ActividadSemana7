package com.tareapluss.actividadsemana7;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private ImageView imageView;
    private float[] rotationMatrix = new float[9];
    private float[] orientationValues = new float[3];
    private boolean layoutReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME);
        } else {
            Toast.makeText(this, "Sensor de rotación no disponible", Toast.LENGTH_SHORT).show();
        }

        imageView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            layoutReady = true;
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (layoutReady && event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            SensorManager.getOrientation(rotationMatrix, orientationValues);
            float roll = orientationValues[2];  // Rotación lateral (eje X)

            float rollDegrees = (float) Math.toDegrees(roll);
            float adjustedRoll = rollDegrees * 2; // Ajustar la sensibilidad

            runOnUiThread(() -> {
                imageView.setRotation(adjustedRoll);
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}
