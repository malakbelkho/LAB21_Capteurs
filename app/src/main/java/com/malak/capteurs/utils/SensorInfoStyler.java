package com.malak.capteurs.utils;


import android.hardware.Sensor;

public class SensorInfoStyler {

    public static String buildDetails(Sensor sensor) {
        return "Nom : " + sensor.getName() + "\n"
                + "Fabricant : " + sensor.getVendor() + "\n"
                + "Version : " + sensor.getVersion() + "\n"
                + "Type Android : " + sensor.getStringType() + "\n"
                + "Type numérique : " + sensor.getType() + "\n"
                + "Résolution : " + sensor.getResolution() + "\n"
                + "Énergie : " + sensor.getPower() + " mA\n"
                + "Portée maximale : " + sensor.getMaximumRange() + "\n"
                + "Délai minimal : " + sensor.getMinDelay() + " µs";
    }
}