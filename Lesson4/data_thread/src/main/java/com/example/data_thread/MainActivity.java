package com.example.data_thread;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.data_thread.databinding.ActivityMainBinding;

import java.util.concurrent.TimeUnit;



public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Runnable runn1 = new Runnable() {
            @Override
            public void run() {
                binding.TextView.setText("Run1");
            }
        };

        final Runnable runn2 = new Runnable() {
            @Override
            public void run() {
                binding.TextView.setText("Run2");
            }
        };

        final Runnable runn3 = new Runnable() {
            @Override
            public void run() {
                binding.TextView.setText("Run3");
            }
        };

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    runOnUiThread(runn1);
                    TimeUnit.SECONDS.sleep(1);
                    binding.TextView.postDelayed(runn3, 2000);
                    binding.TextView.post(runn2);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}