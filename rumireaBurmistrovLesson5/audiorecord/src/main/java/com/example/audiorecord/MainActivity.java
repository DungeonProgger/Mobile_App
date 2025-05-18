package com.example.audiorecord;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import	androidx.annotation.NonNull;
import	androidx.core.app.ActivityCompat;

import	android.Manifest;
import	android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;

import com.example.audiorecord.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final int REQUEST_CODE_PERMISSION = 200;
    //private final String TAG = MainActivity.class.getSimpleName();

    private boolean isWork;
    private String fileName = null;
    private Button recordButton = null;
    private Button playButton = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    boolean isStartRecord = true;
    boolean isStartPlay = true;


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

        recordButton =binding.RecordButton;
        playButton = binding.PlayButton;
        playButton.setEnabled(false);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStartRecord){
                    recordButton.setText("Стоп");
                    playButton.setEnabled(false);
                    startRecord();
                } else{
                    recordButton.setText("Запись");
                    playButton.setEnabled(true);
                    stopRecord();
                }
                isStartRecord = !isStartRecord;

            }
        });

        playButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isStartPlay){
                    playButton.setText("Стоп");
                    recordButton.setEnabled(false);
                    startPlay();
                } else {
                    playButton.setText("Воспроизведение");
                    recordButton.setEnabled(true);
                    stopPlay();
                }
                isStartPlay = !isStartPlay;
            }
        });




        int audioRecordPermissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if(audioRecordPermissionStatus == PackageManager.PERMISSION_GRANTED){
            isWork = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case REQUEST_CODE_PERMISSION:
                isWork = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if(!isWork){
            finish();
        }
    }

    private void startRecord(){
        fileName = getExternalFilesDir(null).getAbsolutePath() + "/audiorecord.3gp";
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(fileName);
        try{
            recorder.prepare();
            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void stopRecord(){
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private void startPlay() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (Exception e) {
        }
    }

    private void stopPlay() {
        player.release();
        player = null;
    }

}