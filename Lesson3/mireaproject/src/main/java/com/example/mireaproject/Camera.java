package com.example.mireaproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.mireaproject.databinding.FragmentCameraBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Camera extends Fragment {

    private FragmentCameraBinding binding;
    private Uri imageUri;
    private boolean isWork = false;
    private final ArrayList<Uri> photoUris = new ArrayList<>();
    private int currentPhotoIndex = -1;  // Индекс текущего отображаемого фото

    private ActivityResultLauncher<String[]> permissionLauncher;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean cameraGranted = result.getOrDefault(Manifest.permission.CAMERA, false);
                    isWork = cameraGranted != null && cameraGranted;
                    if (!isWork) {
                        Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
                    }
                });

        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        photoUris.add(imageUri);
                        currentPhotoIndex = photoUris.size() - 1;
                        updatePhotoDisplay();
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkPermissions();

        // Кнопка сделать фото
        binding.buttonTakePhoto.setOnClickListener(v -> {
            if (isWork) {
                openCamera();
            } else {
                Toast.makeText(requireContext(), "Permissions not granted", Toast.LENGTH_SHORT).show();
                checkPermissions();
            }
        });

        // Кнопки переключения фото
        binding.buttonPrevPhoto.setOnClickListener(v -> showPreviousPhoto());
        binding.buttonNextPhoto.setOnClickListener(v -> showNextPhoto());

        updatePhotoDisplay();
    }

    private void checkPermissions() {
        boolean cameraPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        if (cameraPermission) {
            isWork = true;
        } else {
            permissionLauncher.launch(new String[]{Manifest.permission.CAMERA});
        }
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            String authorities = requireContext().getPackageName() + ".fileprovider";
            imageUri = FileProvider.getUriForFile(requireContext(), authorities, photoFile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraActivityResultLauncher.launch(cameraIntent);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error creating file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        File storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDirectory);
    }

    private void updatePhotoDisplay() {
        if (photoUris.isEmpty()) {
            binding.imageView.setImageResource(android.R.drawable.ic_menu_camera);  // дефолтное изображение
            binding.textViewPhotoCount.setText("Фото 0 из 0");
            binding.buttonPrevPhoto.setEnabled(false);
            binding.buttonNextPhoto.setEnabled(false);
        } else {
            binding.imageView.setImageURI(photoUris.get(currentPhotoIndex));
            binding.textViewPhotoCount.setText("Фото " + (currentPhotoIndex + 1) + " из " + photoUris.size());
            binding.buttonPrevPhoto.setEnabled(currentPhotoIndex > 0);
            binding.buttonNextPhoto.setEnabled(currentPhotoIndex < photoUris.size() - 1);
        }
    }

    private void showPreviousPhoto() {
        if (currentPhotoIndex > 0) {
            currentPhotoIndex--;
            updatePhotoDisplay();
        }
    }

    private void showNextPhoto() {
        if (currentPhotoIndex < photoUris.size() - 1) {
            currentPhotoIndex++;
            updatePhotoDisplay();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
