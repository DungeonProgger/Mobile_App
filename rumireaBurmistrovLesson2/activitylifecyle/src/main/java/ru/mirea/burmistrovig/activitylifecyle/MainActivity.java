package ru.mirea.burmistrovig.activitylifecyle;


import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ActivityLifeCycle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "метод OnCreate сработал!");
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "метод OnStart сработал!");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "Метод onResume Сработал!");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG, "Метод onStop Сработал!");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG, "Метод onRestart Сработал!");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "метод onDestroy Сработал!");
    }
}