package com.example.favoritebook;

import static com.example.favoritebook.MainActivity.BOOK_NAME_KEY;
import static com.example.favoritebook.MainActivity.QUOTES_KEY;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ShareActivity extends AppCompatActivity {

    EditText editBook;
    EditText editQuote;
    Intent def_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_share);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;

        });


        editBook = findViewById(R.id.editTextBook);
        editQuote = findViewById(R.id.editTextQuote);

        def_text = getIntent();

        editBook.setText(def_text.getStringExtra(BOOK_NAME_KEY));
        editQuote.setText(def_text.getStringExtra(QUOTES_KEY));
    }

    public void onClickBack(View view){
        Intent data = new Intent();
        data.putExtra(MainActivity.USER_MESSAGE, "Название Вашей любимой книги: " + editBook.getText().toString() + ". Цитата: " + editQuote.getText().toString());
        setResult(Activity.RESULT_OK, data);
        finish();
    }



}