package com.example.lab5_20206156;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyMedicationPrefs";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_MOTIVATIONAL_MESSAGE = "motivationalMessage";
    private static final String IMAGE_FILE_NAME = "profile_image.png";

    private TextView textViewGreeting;
    private TextView textViewMotivationalMessage;
    private ImageView imageViewProfile;
    private SharedPreferences sharedPreferences;

    // Launcher para seleccionar imagen de la galería
    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        imageViewProfile.setImageBitmap(bitmap);
                        saveImageToInternalStorage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    // Launcher para solicitar permisos
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    pickImageLauncher.launch("image/*");
                } else {
                    Toast.makeText(this, "Permiso de almacenamiento denegado. No se puede cargar la imagen.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- IMPORTANTE: CREAR LOS CANALES DE NOTIFICACIÓN AQUÍ ---
        // Esto asegura que los canales estén registrados con el sistema Android
        // antes de que cualquier notificación intente usarlos.
        NotificationHelper.createNotificationChannels(this);
        // ------------------------------------------------------------

        textViewGreeting = findViewById(R.id.textViewGreeting);
        textViewMotivationalMessage = findViewById(R.id.textViewMotivationalMessage);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        Button buttonViewMedications = findViewById(R.id.buttonViewMedications);
        Button buttonSettings = findViewById(R.id.buttonSettings);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Cargar y mostrar datos guardados
        loadUserData();
        loadImageFromInternalStorage();

        // Configurar listener para la imagen (subir desde galería)
        imageViewProfile.setOnClickListener(v -> {
            checkAndRequestPermissions();
        });

        // Configurar listeners para los botones
        buttonViewMedications.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MedicationListActivity.class);
            startActivity(intent);
        });

        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos en onResume para actualizar después de volver de SettingsActivity
        loadUserData();
    }

    private void loadUserData() {
        String userName = sharedPreferences.getString(KEY_USER_NAME, "Usuario");
        String motivationalMessage = sharedPreferences.getString(KEY_MOTIVATIONAL_MESSAGE, "¡Hoy es un buen día para cuidar tu salud!");

        textViewGreeting.setText("¡Hola, " + userName + "!");
        textViewMotivationalMessage.setText(motivationalMessage);
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Para Android 13 (API 33) y superior, se necesita READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                pickImageLauncher.launch("image/*");
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            // Para versiones anteriores, se necesita READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImageLauncher.launch("image/*");
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void saveImageToInternalStorage(Bitmap bitmap) {
        File directory = getDir("images", Context.MODE_PRIVATE); // Directorio privado para imágenes
        File file = new File(directory, IMAGE_FILE_NAME);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // Guardar como PNG
            Toast.makeText(this, "Imagen guardada exitosamente", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadImageFromInternalStorage() {
        File directory = getDir("images", Context.MODE_PRIVATE);
        File file = new File(directory, IMAGE_FILE_NAME);
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                imageViewProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}