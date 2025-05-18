package com.example.internalfilestorage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText editTextDate;
    private static final String FILENAME = "historical_date.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextDate = findViewById(R.id.editTextText);
        Button buttonSave = findViewById(R.id.button);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = editTextDate.getText().toString();
                String data = date;

                // Запись в файл
                try (FileOutputStream fos = openFileOutput(FILENAME, MODE_PRIVATE)) {
                    fos.write(data.getBytes());
                    Toast.makeText(MainActivity.this, "Файл сохранён!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "Ошибка записи!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
}