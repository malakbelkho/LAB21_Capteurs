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

public class CompassAuraFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private TextView compassText;

    private final float[] gravityValues = new float[3];
    private final float[] magneticValues = new float[3];

    private boolean hasGravity = false;
    private boolean hasMagnetic = false;

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

        compassText = new TextView(requireContext());
        compassText.setText("Initialisation de la boussole...");
        compassText.setTextSize(22);
        compassText.setTextColor(Color.rgb(39, 48, 67));
        compassText.setBackgroundResource(R.drawable.bg_aura_card);
        compassText.setPadding(32, 28, 32, 28);

        root.addView(compassText);

        sensorManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (accelerometer != null) {
            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_UI
            );
        }

        if (magnetometer != null) {
            sensorManager.registerListener(
                    this,
                    magnetometer,
                    SensorManager.SENSOR_DELAY_UI
            );
        }

        if (accelerometer == null || magnetometer == null) {
            compassText.setText("Boussole indisponible : accéléromètre ou magnétomètre manquant.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, gravityValues, 0, 3);
            hasGravity = true;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magneticValues, 0, 3);
            hasMagnetic = true;
        }

        if (hasGravity && hasMagnetic) {
            float[] rotationMatrix = new float[9];
            float[] orientation = new float[3];

            boolean success = SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    gravityValues,
                    magneticValues
            );

            if (success) {
                SensorManager.getOrientation(rotationMatrix, orientation);

                float azimuth = (float) Math.toDegrees(orientation[0]);

                if (azimuth < 0) {
                    azimuth += 360;
                }
                if (Math.abs(azimuth) < 0.1f) {
                    azimuth = 0f;
                }

                compassText.setText(
                        "Direction : " + String.format("%.1f", azimuth) + "°\n\n"
                                + "Orientation : " + getDirectionName(azimuth)
                );
            }
        }
    }

    private String getDirectionName(float degree) {
        if (degree >= 337.5 || degree < 22.5) {
            return "Nord";
        } else if (degree < 67.5) {
            return "Nord-Est";
        } else if (degree < 112.5) {
            return "Est";
        } else if (degree < 157.5) {
            return "Sud-Est";
        } else if (degree < 202.5) {
            return "Sud";
        } else if (degree < 247.5) {
            return "Sud-Ouest";
        } else if (degree < 292.5) {
            return "Ouest";
        } else {
            return "Nord-Ouest";
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}