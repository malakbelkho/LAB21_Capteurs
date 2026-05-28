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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.malak.capteurs.R;
import com.malak.capteurs.views.AuraLineGraphView;

public class MotionAxesFragment extends Fragment implements SensorEventListener {

    private static final String KEY_SENSOR_TYPE = "sensorType";
    private static final String KEY_TITLE = "screenTitle";

    private SensorManager sensorManager;
    private Sensor motionSensor;

    private TextView valuesText;
    private AuraLineGraphView graphView;

    private int sensorType;
    private String screenTitle;

    public static MotionAxesFragment create(int sensorType, String title) {
        MotionAxesFragment fragment = new MotionAxesFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(KEY_SENSOR_TYPE, sensorType);
        bundle.putString(KEY_TITLE, title);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent,
            @Nullable Bundle savedInstanceState) {

        sensorType = requireArguments().getInt(KEY_SENSOR_TYPE);
        screenTitle = requireArguments().getString(KEY_TITLE);

        sensorManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        motionSensor = sensorManager.getDefaultSensor(sensorType);

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

        valuesText = new TextView(requireContext());
        valuesText.setText("Initialisation du capteur...");
        valuesText.setTextSize(17);
        valuesText.setTextColor(Color.rgb(93, 101, 118));
        valuesText.setBackgroundResource(R.drawable.bg_aura_card);
        valuesText.setPadding(28, 24, 28, 24);

        graphView = new AuraLineGraphView(requireContext());
        LinearLayout.LayoutParams graphParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        620
                );
        graphParams.setMargins(0, 26, 0, 0);
        graphView.setLayoutParams(graphParams);
        graphView.setBackgroundResource(R.drawable.bg_aura_card);
        graphView.setElevation(8f);

        root.addView(titleView);
        root.addView(valuesText);
        root.addView(graphView);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (motionSensor != null) {
            sensorManager.registerListener(
                    this,
                    motionSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        } else {
            valuesText.setText("Capteur indisponible sur ce dispositif.");
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

        float magnitude = (float) Math.sqrt(x * x + y * y + z * z);

        valuesText.setText(
                "Axe X : " + String.format("%.2f", x) + "\n"
                        + "Axe Y : " + String.format("%.2f", y) + "\n"
                        + "Axe Z : " + String.format("%.2f", z) + "\n\n"
                        + "Norme globale : " + String.format("%.2f", magnitude)
        );

        graphView.pushPoint(magnitude);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}