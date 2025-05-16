package com.example.cryptoloader;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.example.cryptoloader.databinding.ActivityMainBinding;

import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private final int LoaderID = 1234;
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
    }

    public void onClick(View view){
        Bundle bundle = new Bundle();
        SecretKey key = generateKey();
        bundle.putByteArray(MyLoader.ARG_WORD, encryptMsg(binding.editTextText.getText().toString(), key));
        bundle.putByteArray("Key", key.getEncoded());
        LoaderManager.getInstance(this).restartLoader(LoaderID, bundle, this);

    }


    @Override
    public void onLoaderReset(@NonNull Loader<String> loader){
        Log.d("Loader", "OnLoaderReset");
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle bundle){
        if(id== LoaderID){
            Toast.makeText(this, "onCreateLoader: " + id, Toast.LENGTH_SHORT).show();
            return new MyLoader(this, bundle);
        }
        throw new InvalidParameterException("Invalid loader id");

    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        if(loader.getId()==LoaderID){
            Log.d("Loader", "On loaderFinished: " + s);
            Toast.makeText(this, "Расшифрованное сообщение: " + s, Toast.LENGTH_SHORT).show();
        }

    }

    public static SecretKey generateKey() {
        try{
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed("Metal Gear Solid".getBytes());
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256, random);
            return keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public static byte[] encryptMsg(String message, SecretKey key) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(message.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }
}