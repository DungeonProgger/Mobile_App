package com.example.rumireaburmistrovlesson6;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rumireaburmistrovlesson6.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private EditText editTextGroup, editTextList, editMovie;
    private SharedPreferences sharedPreferences;

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

        sharedPreferences = getSharedPreferences("mirea_settings", MODE_PRIVATE);

        editTextGroup = binding.editTextText;
        editTextList = binding.editTextText2;
        editMovie = binding.editTextText3;

        Button buttonSave = binding.button;

        editTextGroup.setText(sharedPreferences.getString("GROUP", ""));
        editTextList.setText(String.valueOf(sharedPreferences.getInt("NUMBER", 0)));
        editMovie.setText(sharedPreferences.getString("MOVIE", ""));

        buttonSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String group = editTextGroup.getText().toString();
                int number = Integer.parseInt(editTextList.getText().toString());
                String movie = editMovie.getText().toString();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("GROUP", group);
                editor.putInt("NUMBER", number);
                editor.putString("MOVIE", movie);
                editor.apply();

                Toast.makeText(MainActivity.this, "Данные сохранены!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}