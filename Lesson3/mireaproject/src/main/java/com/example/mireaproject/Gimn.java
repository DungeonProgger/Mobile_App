package com.example.mireaproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import com.example.mireaproject.databinding.FragmentGimnBinding;
import com.example.mireaproject.PlayerService;

public class Gimn extends Fragment {
    private FragmentGimnBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGimnBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.imageButton.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(requireContext(), PlayerService.class);
            ContextCompat.startForegroundService(requireContext(), serviceIntent);
        });

        binding.imageButton2.setOnClickListener(v -> {
            requireContext().stopService(new Intent(requireContext(), PlayerService.class));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
