package com.example.mireaproject;

import android.Manifest;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mireaproject.databinding.FragmentAudioBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Audio extends Fragment {

    private FragmentAudioBinding binding;

    private MediaRecorder recorder;
    private MediaPlayer player;

    private boolean isRecording = false;
    private boolean isPlaying = false;

    private String currentFilePath;

    private final ArrayList<String> recordingsPaths = new ArrayList<>();
    private final ArrayList<String> recordingsNames = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private ActivityResultLauncher<String> permissionLauncher;
    private boolean hasRecordAudioPermission = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    hasRecordAudioPermission = granted;
                    if (!granted) {
                        Toast.makeText(requireContext(), "Permission denied. Closing fragment.", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        checkPermission();
    }

    private void checkPermission() {
        hasRecordAudioPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                == android.content.pm.PackageManager.PERMISSION_GRANTED;

        if (!hasRecordAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAudioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupListView();
        setupButtons();
        loadExistingRecordings();
    }

    private void setupListView() {
        adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1,
                recordingsNames);
        binding.listViewRecordings.setAdapter(adapter);

        binding.listViewRecordings.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            if (isPlaying) {
                stopPlaying();
            }
            String filePath = recordingsPaths.get(position);
            startPlaying(filePath);
        });
    }

    private void setupButtons() {
        binding.recordButton.setText("Запись");
        binding.recordButton.setOnClickListener(v -> {
            if (!hasRecordAudioPermission) {
                Toast.makeText(requireContext(), "Нет разрешения на запись аудио", Toast.LENGTH_SHORT).show();
                checkPermission();
                return;
            }
            if (!isRecording) {
                startRecording();
            } else {
                stopRecording();
            }
        });
    }

    private void loadExistingRecordings() {
        recordingsPaths.clear();
        recordingsNames.clear();

        File dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (dir != null && dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    recordingsPaths.add(files[i].getAbsolutePath());
                    recordingsNames.add("Запись " + (i + 1));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void startRecording() {
        try {
            File dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            if (dir == null) {
                Toast.makeText(requireContext(), "Ошибка доступа к хранилищу", Toast.LENGTH_SHORT).show();
                return;
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            currentFilePath = dir.getAbsolutePath() + "/record_" + timeStamp + ".3gp";

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(currentFilePath);

            recorder.prepare();
            recorder.start();

            isRecording = true;
            binding.recordButton.setText("Стоп");
            binding.listViewRecordings.setEnabled(false);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Ошибка при старте записи", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        try {
            recorder.stop();
            recorder.release();
            recorder = null;

            isRecording = false;
            binding.recordButton.setText("Запись");
            binding.listViewRecordings.setEnabled(true);

            // Добавляем запись в список и обновляем ListView
            recordingsPaths.add(currentFilePath);
            recordingsNames.add("Запись " + recordingsPaths.size());
            adapter.notifyDataSetChanged();

        } catch (RuntimeException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Ошибка при остановке записи", Toast.LENGTH_SHORT).show();
        }
    }

    private void startPlaying(String filePath) {
        try {
            player = new MediaPlayer();
            player.setDataSource(filePath);
            player.prepare();
            player.start();
            isPlaying = true;

            binding.recordButton.setEnabled(false);

            player.setOnCompletionListener(mp -> {
                stopPlaying();
            });

            Toast.makeText(requireContext(), "Воспроизведение записи", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlaying() {
        if (player != null) {
            player.release();
            player = null;
            isPlaying = false;
            binding.recordButton.setEnabled(true);
            Toast.makeText(requireContext(), "Воспроизведение остановлено", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
        if (player != null) {
            player.release();
            player = null;
        }
        binding = null;
    }
}
