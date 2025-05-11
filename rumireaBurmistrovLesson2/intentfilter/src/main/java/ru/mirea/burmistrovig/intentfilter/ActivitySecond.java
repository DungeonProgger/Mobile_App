package ru.mirea.burmistrovig.intentfilter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ActivitySecond extends AppCompatActivity {

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
        TextView textFIO = findViewById(R.id.textView1);
        TextView textUniver = findViewById(R.id.textView6);

        textFIO.setText("ФИО: "+ getIntent().getSerializableExtra("textFIO"));
        textUniver.setText("Университет: "+ getIntent().getSerializableExtra("textUniver"));

    }
    public void OnClickback(View view){
        Intent intent = new Intent(ActivitySecond.this, MainActivity.class);
        startActivity(intent);
    }
}