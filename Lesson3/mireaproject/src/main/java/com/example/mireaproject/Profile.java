package com.example.mireaproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;


import com.example.mireaproject.databinding.FragmentProfileBinding;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Profile extends Fragment {

    private FragmentProfileBinding binding;
    private SharedPreferences secureSharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        // Обработка паддингов (если нужно)
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {
            String masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            secureSharedPreferences = EncryptedSharedPreferences.create(
                    "secure_prefs",
                    masterKey,
                    requireContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        // Читаем сохранённые данные и устанавливаем в поля
        String savedName = secureSharedPreferences.getString("WRITE_NAME", "");
        String savedValue = secureSharedPreferences.getString("WRITE_VALUE", "");
        binding.editTextText.setText(savedName);
        binding.editTextText2.setText(savedValue);

        binding.button.setOnClickListener(v -> {
            String name = binding.editTextText.getText().toString();
            String value = binding.editTextText2.getText().toString();

            secureSharedPreferences.edit()
                    .putString("WRITE_NAME", name)
                    .putString("WRITE_VALUE", value)
                    .apply();

            Toast.makeText(requireContext(), "Данные сохранены!", Toast.LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}