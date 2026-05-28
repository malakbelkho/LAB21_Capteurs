package com.malak.capteurs.fragments;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.malak.capteurs.R;

import java.util.LinkedList;
import java.util.Queue;

public class ActivityMoodFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private TextView activityText;

    private final float[] gravity = new float[3];
    private final Queue<Float> movementWindow = new LinkedList<>();

    private static final int WINDOW_SIZE = 30;
    private static final float ALPHA = 0.8f;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent,
            @Nullable Bundle savedInstanceState) {

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 28, 28, 40);
        root.setBackgroundResource(R.drawable.bg_aura_screen);

        activityText = new TextView(requireContext());
        activityText.setText("Calibration du mouvement...");
        activityText.setTextSize(20);
        activityText.setTextColor(Color.rgb(39, 48, 67));
        activityText.setBackgroundResource(R.drawable.bg_aura_card);
        activityText.setPadding(32, 28, 32, 28);

        root.addView(activityText);

        sensorManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (accelerometer != null) {
            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_GAME
            );
        } else {
            activityText.setText("Accéléromètre indisponible.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * x;
        gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * y;
        gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * z;

        float linearX = x - gravity[0];
        float linearY = y - gravity[1];
        float linearZ = z - gravity[2];

        float movement = (float) Math.sqrt(
                linearX * linearX
                        + linearY * linearY
                        + linearZ * linearZ
        );

        addMovementValue(movement);

        String detectedActivity = classifyActivity(x, y, z);

        activityText.setText(
                "Reconnaissance d’activité\n\n"
                        + "X : " + String.format("%.2f", x) + "\n"
                        + "Y : " + String.format("%.2f", y) + "\n"
                        + "Z : " + String.format("%.2f", z) + "\n\n"
                        + "Intensité mouvement : " + String.format("%.2f", movement) + "\n\n"
                        + "Activité détectée : " + detectedActivity
        );
    }

    private void addMovementValue(float movement) {
        if (movementWindow.size() >= WINDOW_SIZE) {
            movementWindow.poll();
        }

        movementWindow.add(movement);
    }

    private String classifyActivity(float x, float y, float z) {

        if (movementWindow.size() < WINDOW_SIZE) {
            return "Calibration...";
        }

        float mean = 0f;
        float max = 0f;

        for (float value : movementWindow) {
            mean += value;
            max = Math.max(max, value);
        }

        mean = mean / movementWindow.size();

        float variance = 0f;

        for (float value : movementWindow) {
            variance += (value - mean) * (value - mean);
        }

        variance = variance / movementWindow.size();

        float standardDeviation = (float) Math.sqrt(variance);

        if (max > 10f) {
            return "Saut ou mouvement brusque";
        }

        if (standardDeviation > 1.2f) {
            return "Marche";
        }

        if (Math.abs(z) > 8f) {
            return "Stable / téléphone à plat";
        }

        if (Math.abs(y) > 7f || Math.abs(x) > 7f) {
            return "Position stable / téléphone vertical";
        }

        return "Position stable";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
