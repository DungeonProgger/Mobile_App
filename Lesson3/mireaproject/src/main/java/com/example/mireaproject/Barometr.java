package com.example.mireaproject;

import static android.content.Context.SENSOR_SERVICE;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mireaproject.databinding.FragmentBlankBinding;


public class Barometr extends Fragment implements SensorEventListener {

    private FragmentBlankBinding binding;
    private SensorManager sensorManager;
    private Sensor barometer;
    private TextView pressureTextView;
    private TextView altitudeTextView;

    private static final float STANDARD_PRESSURE = 1013.25f; // Стандартное атмосферное давление (гПа)

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBlankBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sensorManager = (SensorManager) requireContext().getSystemService(SENSOR_SERVICE);
        barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        if (barometer != null) {
            sensorManager.registerListener(this, barometer, SensorManager.SENSOR_DELAY_UI);
        }

        pressureTextView = binding.TextViewPressure;
        altitudeTextView = binding.TextViewAltitude;  // Для высоты

    }

    @Override
    public void onPause() {
        super.onPause();
        if (barometer != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (barometer != null) {
            sensorManager.registerListener(this, barometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            float pressure = event.values[0];
            pressureTextView.setText("Давление: " + pressure + " hPa");

            // Вычисление высоты на основе давления
            float altitude = calculateAltitude(pressure);
            altitudeTextView.setText("Высота над уровнем моря: " + altitude + " m");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Не обрабатываем изменения точности для данного случая
    }

    /**
     * Вычисление высоты над уровнем моря на основе давления
     * @param pressure текущее атмосферное давление в гПа
     * @return высота в метрах
     */
    private float calculateAltitude(float pressure) {
        return 44330 * (1 - (float) Math.pow(pressure / STANDARD_PRESSURE, 1 / 5.255));
    }
}