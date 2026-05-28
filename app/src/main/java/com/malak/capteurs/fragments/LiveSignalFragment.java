package com.malak.capteurs.fragments;


import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Build;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.malak.capteurs.R;
import com.malak.capteurs.views.AuraLineGraphView;

public class LiveSignalFragment extends Fragment implements SensorEventListener {

    private static final String KEY_SENSOR_CODE = "sensorCode";
    private static final String KEY_SCREEN_TITLE = "screenTitle";
    private static final String KEY_READ_MODE = "readMode";

    private SensorManager signalManager;
    private Sensor activeSensor;

    private TextView liveValueText;
    private AuraLineGraphView auraGraph;

    private int sensorCode;
    private String screenTitle;
    private String readMode;

    private final Handler demoSignalHandler = new Handler(Looper.getMainLooper());
    private float demoTick = 0f;

    public static LiveSignalFragment create(int sensorCode, String title, String mode) {
        LiveSignalFragment fragment = new LiveSignalFragment();

        Bundle data = new Bundle();
        data.putInt(KEY_SENSOR_CODE, sensorCode);
        data.putString(KEY_SCREEN_TITLE, title);
        data.putString(KEY_READ_MODE, mode);

        fragment.setArguments(data);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent,
            @Nullable Bundle savedInstanceState) {

        sensorCode = requireArguments().getInt(KEY_SENSOR_CODE);
        screenTitle = requireArguments().getString(KEY_SCREEN_TITLE);
        readMode = requireArguments().getString(KEY_READ_MODE);

        signalManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        activeSensor = signalManager.getDefaultSensor(sensorCode);

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 28, 28, 40);
        root.setBackgroundResource(R.drawable.bg_aura_screen);

        TextView titleView = new TextView(requireContext());
        titleView.setText(screenTitle);
        titleView.setTextSize(24);
        titleView.setTextColor(Color.rgb(39, 48, 67));
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setPadding(0, 0, 0, 20);

        liveValueText = new TextView(requireContext());
        liveValueText.setText("Initialisation du capteur...");
        liveValueText.setTextSize(18);
        liveValueText.setTextColor(Color.rgb(93, 101, 118));
        liveValueText.setBackgroundResource(R.drawable.bg_aura_card);
        liveValueText.setPadding(28, 24, 28, 24);

        auraGraph = new AuraLineGraphView(requireContext());
        LinearLayout.LayoutParams graphParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        620);

        graphParams.setMargins(0, 26, 0, 0);
        auraGraph.setLayoutParams(graphParams);
        auraGraph.setBackgroundResource(R.drawable.bg_aura_card);
        auraGraph.setElevation(8f);

        root.addView(titleView);
        root.addView(liveValueText);
        root.addView(auraGraph);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isRunningOnEmulator()
                && (sensorCode == Sensor.TYPE_AMBIENT_TEMPERATURE
                || sensorCode == Sensor.TYPE_RELATIVE_HUMIDITY
                || sensorCode == Sensor.TYPE_PROXIMITY)) {

            liveValueText.setText("Mode simulation activé sur émulateur.");
            startDemoSignal();
            return;
        }

        if (activeSensor != null) {
            signalManager.registerListener(
                    this,
                    activeSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        } else {
            liveValueText.setText("Capteur absent sur ce dispositif. Mode simulation activé.");
            startDemoSignal();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        signalManager.unregisterListener(this);
        demoSignalHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float value = readSensorValue(event.values);

        if (isInvalidValue(value)) {
            signalManager.unregisterListener(this);
            liveValueText.setText("Lecture non fiable sur émulateur.\nMode simulation activé.");
            startDemoSignal();
            return;
        }

        refreshScreen(value, false);
    }
    private boolean isInvalidValue(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            return true;
        }

        if (sensorCode == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            return value < -50f || value > 80f;
        }

        if (sensorCode == Sensor.TYPE_RELATIVE_HUMIDITY) {
            return value < 0f || value > 100f;
        }

        if (sensorCode == Sensor.TYPE_PROXIMITY) {
            return value < 0f || value > 1000f;
        }

        if (sensorCode == Sensor.TYPE_MAGNETIC_FIELD) {
            return value < 0f || value > 3000f;
        }

        return false;
    }

    private float readSensorValue(float[] values) {
        if ("VECTOR_MAGNITUDE".equals(readMode)) {
            return (float) Math.sqrt(
                    values[0] * values[0]
                            + values[1] * values[1]
                            + values[2] * values[2]);
        }

        return values[0];
    }

    private void refreshScreen(float value, boolean simulated) {
        String source = simulated ? "Simulation" : "Lecture réelle";

        liveValueText.setText(
                source + "\n\n"
                        + "Valeur actuelle : " + String.format("%.2f", value));

        auraGraph.pushPoint(value);
    }

    private void startDemoSignal() {
        demoSignalHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                demoTick++;

                float value;

                if (sensorCode == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                    value = 23f + (float) Math.sin(demoTick / 5f) * 2.5f;
                } else if (sensorCode == Sensor.TYPE_RELATIVE_HUMIDITY) {
                    value = 50f + (float) Math.sin(demoTick / 6f) * 18f;
                } else if (sensorCode == Sensor.TYPE_PROXIMITY) {
                    value = demoTick % 8 < 4 ? 0f : 5f;
                } else if (sensorCode == Sensor.TYPE_MAGNETIC_FIELD) {
                    value = 44f + (float) Math.sin(demoTick / 3f) * 13f;
                } else {
                    value = (float) Math.sin(demoTick);
                }

                refreshScreen(value, true);
                demoSignalHandler.postDelayed(this, 900);
            }
        }, 900);
    }

    private boolean isRunningOnEmulator() {
        return Build.FINGERPRINT.contains("generic")
                || Build.FINGERPRINT.contains("emulator")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}