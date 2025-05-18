package com.example.mireaproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.example.mireaproject.databinding.FragmentChangerBinding;

import java.io.InputStream;
import java.io.OutputStream;

public class Changer extends Fragment {

    private FragmentChangerBinding binding;

    private Uri pickedFolderUri = null;
    private Uri currentFileUri = null;

    private ActivityResultLauncher<Uri> pickFolderLauncher;
    private ActivityResultLauncher<String[]> pickFileLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChangerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickFolderLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocumentTree(),
                uri -> {
                    if (uri != null) {
                        pickedFolderUri = uri;
                        requireContext().getContentResolver().takePersistableUriPermission(uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        Toast.makeText(requireContext(), "Папка выбрана", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        pickFileLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        currentFileUri = uri;
                        requireContext().getContentResolver().takePersistableUriPermission(uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                        DocumentFile docFile = DocumentFile.fromSingleUri(requireContext(), uri);
                        if (docFile != null && docFile.getName() != null) {
                            binding.editTextFileName.setText(docFile.getName());
                        }
                    }
                }
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.Select.setOnClickListener(v -> {
            if (pickedFolderUri == null) {

                pickFolderLauncher.launch(null);
            } else {

                pickFileLauncher.launch(new String[]{"*/*"});
            }
        });

        binding.buttonRename.setOnClickListener(v -> {
            if (pickedFolderUri == null) {
                Toast.makeText(requireContext(), "Сначала выберите папку", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentFileUri == null) {
                Toast.makeText(requireContext(), "Сначала выберите файл", Toast.LENGTH_SHORT).show();
                return;
            }
            String newName = binding.editTextFileName.getText().toString().trim();
            if (TextUtils.isEmpty(newName)) {
                Toast.makeText(requireContext(), "Введите новое имя файла", Toast.LENGTH_SHORT).show();
                return;
            }
            renameFileInFolder(pickedFolderUri, currentFileUri, newName);
        });
    }

    private void renameFileInFolder(Uri folderUri, Uri fileUri, String newName) {
        try {
            DocumentFile pickedDir = DocumentFile.fromTreeUri(requireContext(), folderUri);
            DocumentFile oldFile = DocumentFile.fromSingleUri(requireContext(), fileUri);

            if (pickedDir == null || oldFile == null) {
                Toast.makeText(requireContext(), "Ошибка доступа к файлам", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pickedDir.canWrite()) {
                Toast.makeText(requireContext(), "Нет доступа на запись в выбранную папку", Toast.LENGTH_SHORT).show();
                return;
            }


            String extension = "";
            if (newName.contains(".")) {
                extension = newName.substring(newName.lastIndexOf('.') + 1);
            }
            String mimeType = getMimeTypeFromExtension(extension);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            DocumentFile newFile = pickedDir.createFile(mimeType, newName);
            if (newFile == null) {
                Toast.makeText(requireContext(), "Не удалось создать новый файл", Toast.LENGTH_SHORT).show();
                return;
            }

            try (InputStream in = requireContext().getContentResolver().openInputStream(fileUri);
                 OutputStream out = requireContext().getContentResolver().openOutputStream(newFile.getUri())) {
                if (in == null || out == null) {
                    Toast.makeText(requireContext(), "Ошибка открытия потоков", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] buffer = new byte[8192];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }

            boolean deleted = oldFile.delete();
            if (!deleted) {
                Toast.makeText(requireContext(), "Не удалось удалить старый файл", Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(requireContext(), "Файл переименован (копированием)", Toast.LENGTH_SHORT).show();
            binding.editTextFileName.setText(newName);
            currentFileUri = newFile.getUri();

        } catch (Exception e) {
            Log.e("FileConverter", "Ошибка при переименовании", e);
            Toast.makeText(requireContext(), "Ошибка при переименовании файла", Toast.LENGTH_SHORT).show();
        }
    }

    private String getMimeTypeFromExtension(String ext) {
        if (ext == null || ext.isEmpty()) return null;
        return android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase());
    }
}
