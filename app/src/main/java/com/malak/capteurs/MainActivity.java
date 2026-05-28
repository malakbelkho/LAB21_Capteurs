package com.malak.capteurs;

import android.hardware.Sensor;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.malak.capteurs.fragments.ActivityMoodFragment;
import com.malak.capteurs.fragments.CompassAuraFragment;
import com.malak.capteurs.fragments.LiveSignalFragment;
import com.malak.capteurs.fragments.MotionAxesFragment;
import com.malak.capteurs.fragments.SensorCatalogFragment;
import com.malak.capteurs.fragments.StepPulseFragment;

public class MainActivity extends AppCompatActivity {

    private void displayFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );
            return insets;
        });

        if (savedInstanceState == null) {
            displayFragment(new SensorCatalogFragment());
        }

        findViewById(R.id.btn_catalog).setOnClickListener(v ->
                displayFragment(new SensorCatalogFragment())
        );

        findViewById(R.id.btn_temp).setOnClickListener(v ->
                displayFragment(
                        LiveSignalFragment.create(
                                Sensor.TYPE_AMBIENT_TEMPERATURE,
                                "Température ambiante",
                                "FIRST_VALUE"
                        )
                )
        );

        findViewById(R.id.btn_humidity).setOnClickListener(v ->
                displayFragment(
                        LiveSignalFragment.create(
                                Sensor.TYPE_RELATIVE_HUMIDITY,
                                "Humidité relative",
                                "FIRST_VALUE"
                        )
                )
        );

        findViewById(R.id.btn_proximity).setOnClickListener(v ->
                displayFragment(
                        LiveSignalFragment.create(
                                Sensor.TYPE_PROXIMITY,
                                "Capteur de proximité",
                                "FIRST_VALUE"
                        )
                )
        );

        findViewById(R.id.btn_magnetic).setOnClickListener(v ->
                displayFragment(
                        LiveSignalFragment.create(
                                Sensor.TYPE_MAGNETIC_FIELD,
                                "Champ magnétique",
                                "VECTOR_MAGNITUDE"
                        )
                )
        );

        findViewById(R.id.btn_accelerometer).setOnClickListener(v ->
                displayFragment(
                        MotionAxesFragment.create(
                                Sensor.TYPE_ACCELEROMETER,
                                "Accéléromètre : axes X, Y, Z"
                        )
                )
        );

        findViewById(R.id.btn_gravity).setOnClickListener(v ->
                displayFragment(
                        MotionAxesFragment.create(
                                Sensor.TYPE_GRAVITY,
                                "Gravité : composante X, Y, Z"
                        )
                )
        );

        findViewById(R.id.btn_gyroscope).setOnClickListener(v ->
                displayFragment(
                        MotionAxesFragment.create(
                                Sensor.TYPE_GYROSCOPE,
                                "Gyroscope : rotation rad/s"
                        )
                )
        );

        findViewById(R.id.btn_steps).setOnClickListener(v ->
                displayFragment(new StepPulseFragment())
        );

        findViewById(R.id.btn_compass).setOnClickListener(v ->
                displayFragment(new CompassAuraFragment())
        );

        findViewById(R.id.btn_activity).setOnClickListener(v ->
                displayFragment(new ActivityMoodFragment())
        );
    }
}