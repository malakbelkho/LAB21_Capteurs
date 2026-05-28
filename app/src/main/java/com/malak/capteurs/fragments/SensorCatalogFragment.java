package com.malak.capteurs.fragments;


import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.malak.capteurs.R;
import com.malak.capteurs.utils.SensorInfoStyler;

import java.util.List;

public class SensorCatalogFragment extends Fragment {

    private LinearLayout sensorContainer;
    private SensorManager auraSensorManager;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent,
            @Nullable Bundle savedInstanceState) {

        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.setBackgroundResource(R.drawable.bg_aura_screen);

        sensorContainer = new LinearLayout(requireContext());
        sensorContainer.setOrientation(LinearLayout.VERTICAL);
        sensorContainer.setPadding(28, 28, 28, 40);

        scrollView.addView(sensorContainer);

        auraSensorManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        showAvailableSensors();

        return scrollView;
    }

    private void showAvailableSensors() {
        List<Sensor> detectedSensors = auraSensorManager.getSensorList(Sensor.TYPE_ALL);

        TextView header = new TextView(requireContext());
        header.setText("Catalogue des capteurs embarqués");
        header.setTextSize(24);
        header.setTextColor(Color.rgb(39, 48, 67));
        header.setPadding(4, 0, 4, 24);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        sensorContainer.addView(header);

        for (Sensor sensor : detectedSensors) {
            if (sensor.getName().toLowerCase().contains("uncalibrated")) {
                continue;
            }
            LinearLayout card = new LinearLayout(requireContext());
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.bg_aura_card);
            card.setPadding(28, 24, 28, 24);

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

            cardParams.setMargins(0, 0, 0, 22);
            card.setLayoutParams(cardParams);
            card.setElevation(7f);

            TextView badge = new TextView(requireContext());
            badge.setText(getSensorBadge(sensor));
            badge.setTextColor(Color.WHITE);
            badge.setTextSize(12);
            badge.setBackgroundResource(R.drawable.bg_sensor_badge);
            badge.setPadding(18, 8, 18, 8);

            TextView name = new TextView(requireContext());
            name.setText(sensor.getName());
            name.setTextSize(18);
            name.setTextColor(Color.rgb(39, 48, 67));
            name.setTypeface(null, android.graphics.Typeface.BOLD);
            name.setPadding(0, 16, 0, 10);

            TextView details = new TextView(requireContext());
            details.setText(SensorInfoStyler.buildDetails(sensor));
            details.setTextSize(14);
            details.setTextColor(Color.rgb(93, 101, 118));
            details.setLineSpacing(4, 1.05f);

            card.addView(badge);
            card.addView(name);
            card.addView(details);

            sensorContainer.addView(card);
        }
    }
    private String getSensorBadge(Sensor sensor) {
        switch (sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                return "Mouvement";

            case Sensor.TYPE_GYROSCOPE:
                return "Rotation";

            case Sensor.TYPE_MAGNETIC_FIELD:
                return "Champ magnétique";

            case Sensor.TYPE_LIGHT:
                return "Lumière";

            case Sensor.TYPE_PRESSURE:
                return "Pression";

            case Sensor.TYPE_PROXIMITY:
                return "Proximité";

            case Sensor.TYPE_GRAVITY:
                return "Gravité";

            case Sensor.TYPE_LINEAR_ACCELERATION:
                return "Accélération linéaire";

            case Sensor.TYPE_ROTATION_VECTOR:
                return "Vecteur rotation";

            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return "Humidité";

            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return "Température";

            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                return "Rotation jeu";

            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                return "Rotation géomagnétique";

            case Sensor.TYPE_ORIENTATION:
                return "Orientation";

            default:
                return "Type " + sensor.getType();
        }
    }
}