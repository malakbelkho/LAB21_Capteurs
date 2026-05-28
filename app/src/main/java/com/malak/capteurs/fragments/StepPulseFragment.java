package com.malak.capteurs.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.malak.capteurs.R;

public class StepPulseFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepSensor;

    private TextView stepText;
    private float firstStepValue = -1f;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            startStepCounter();
                        } else {
                            stepText.setText("Permission refusée pour le compteur de pas.");
                        }
                    }
            );

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

        stepText = new TextView(requireContext());
        stepText.setText("Initialisation du compteur de pas...");
        stepText.setTextSize(20);
        stepText.setTextColor(Color.rgb(39, 48, 67));
        stepText.setBackgroundResource(R.drawable.bg_aura_card);
        stepText.setPadding(32, 28, 32, 28);

        root.addView(stepText);

        sensorManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepSensor == null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (stepSensor == null) {
            stepText.setText("Compteur de pas indisponible sur ce téléphone.");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION
        ) != PackageManager.PERMISSION_GRANTED) {

            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
        } else {
            startStepCounter();
        }
    }

    private void startStepCounter() {
        sensorManager.registerListener(
                this,
                stepSensor,
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            float totalSinceBoot = event.values[0];

            if (firstStepValue < 0) {
                firstStepValue = totalSinceBoot;
            }

            int sessionSteps = (int) (totalSinceBoot - firstStepValue);

            stepText.setText(
                    "Compteur de pas\n\n"
                            + "Mode : compteur cumulatif\n\n"
                            + "Pas depuis le dernier redémarrage : "
                            + (int) totalSinceBoot
                            + "\n\nPas pendant cette session : "
                            + sessionSteps
            );

        } else if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if (firstStepValue < 0) {
                firstStepValue = 0;
            }

            firstStepValue++;

            stepText.setText(
                    "Compteur de pas\n\n"
                            + "Mode : détection pas par pas\n\n"
                            + "Pas détectés pendant cette session : "
                            + (int) firstStepValue
            );
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}