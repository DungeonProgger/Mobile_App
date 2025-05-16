package com.example.thread;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.thread.databinding.ActivityMainBinding;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private Handler handler;
    private int counter;

    private int allLessons;

    private int allLessondays;
    private double result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setContentView(binding.getRoot());

        Thread mainthread = Thread.currentThread();
        binding.textView.setText("Имя текущего потока: " + mainthread.getName());

        mainthread.setName("Мой номер группы: БСБО-07-22, мой номер по списку: 4, мой любимый фильм: Мстители");
        binding.textView.append("\n Новое имя потока: " + mainthread.getName());
        Log.d("Поток","Stack: " + Arrays.toString(mainthread.getStackTrace()));

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        int numberthread = counter++;
                        Log.d("Поток",String.format("Запущен поток № %d студентом группы № %s номер по списку № %d", numberthread, "БСБО-07-22", 4));
                        long endTime = System.currentTimeMillis() + 2 * 1000;
                        while(System.currentTimeMillis() < endTime){
                            synchronized (this){
                                try{
                                    allLessons = Integer.valueOf(String.valueOf(binding.editTextPairs.getText()));
                                    allLessondays = Integer.valueOf(String.valueOf(binding.editTextDays.getText()));
                                    wait(endTime - System.currentTimeMillis());

                                    Log.d(MainActivity.class.getSimpleName(), "Endtime: "+ endTime);
                                    result = (double) allLessons /allLessondays;
                                    Log.d("Поток", ""+result);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.textView2.setText("среднее кол-во пар за месяц: "+ result);
                                        }
                                    });

                                }catch (Exception e){
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }

                }).start();

            }
        });

    }
}