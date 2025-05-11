package ru.mirea.burmistrovig.multiactivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "ActivityLifeCycle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String resieveddata = (String) getIntent().getSerializableExtra("key");

        TextView textSecond = findViewById(R.id.textSecond);

        textSecond.setText(resieveddata);

        Log.d(TAG, "onCreate In Second Activity");
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume In Second Activity");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG, "onStop In Second Activity");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG, "onRestart In Second Activity");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy In Second Activity");
    }


}