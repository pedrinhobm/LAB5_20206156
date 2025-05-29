package com.example.lab5_20206156;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


// este es la interfaz del usuario
public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyMedicationPrefs";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_MOTIVATIONAL_MESSAGE = "motivationalMessage";
    private static final String IMAGE_FILE_NAME = "profile_image.png";
    private TextView textViewGreeting;
    private TextView textViewMotivationalMessage;
    private ImageView imageViewProfile;
    private SharedPreferences sharedPreferences;


    // Aqui si use IA con launcher para seleccionar una imagen de mi galería
    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    try { // aquí la función cumple con un URI que identifica la imagen escogisa
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        imageViewProfile.setImageBitmap(bitmap); // tambien lo use para un dato de ImageView
                        saveImageToInternalStorage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace(); // en caso que no funciono , cargará un error indicandolo ahí mismo
                        Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    // De igual manera aquí porque consideré que fue necesario un auncher que solicita permisos
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    pickImageLauncher.launch("image/*");
                } else {
                    Toast.makeText(this, "Permiso de almacenamiento denegado.", Toast.LENGTH_SHORT).show();
                } // ojo que se puede seleccionar la imagen
            } // gracias al <uses-permission android:name="android.permission.READ_MEDIA_IMAGES"/>
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NotificationHelper.createNotificationChannels(this);
        textViewGreeting = findViewById(R.id.textViewGreeting);
        textViewMotivationalMessage = findViewById(R.id.textViewMotivationalMessage);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        Button buttonViewMedications = findViewById(R.id.buttonViewMedications);
        Button buttonSettings = findViewById(R.id.buttonSettings);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loadUserData();  // esta función sirve para mostrar datos guardados
        loadImageFromInternalStorage();

        imageViewProfile.setOnClickListener(v -> {
            checkAndRequestPermissions(); // aqui use ia ya que al subir la imagen desde mi galeria era en base a la configuración del listener
        });

        buttonViewMedications.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MedicationListActivity.class);
            startActivity(intent); // este es el boton que redirige a la lista de medicamentos , para cambiar de vista uso Intent
        });

        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent); // este es el boton de configuración de datos , para cambiar de vista uso Intent
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData(); // se va llamar a esa función para que actualice los datos colocados en la configuración
    } // tanto del mensaje motivacional o el nombre del usuario

    private void loadUserData() {  // aqui si usa SharedPreferences para guardar y actualizar los datos luego de haber realizado los cambios en la vista de configuración
        String userName = sharedPreferences.getString(KEY_USER_NAME, "Usuario"); // por ello se uso shared, al abrir la imagen por primera vez se vera el nombre de USUARIO
        String motivationalMessage = sharedPreferences.getString(KEY_MOTIVATIONAL_MESSAGE, "¡Hoy es un buen día para cuidar tu salud!"); // así como el mensaje motivacional que esta en el enunciado del LAB5
        textViewGreeting.setText("¡Hola, " + userName + "!");
        textViewMotivationalMessage.setText(motivationalMessage);
    }
    private void checkAndRequestPermissions() { // continuando con lo anterior permiso
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // busca solicitarlo al usuario de acuerdo a la version de android
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                pickImageLauncher.launch("image/*");
            } else { // en caso de éxtio , se abre el selector de imagenes , ya sea de galeria o google photos
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImageLauncher.launch("image/*"); // aqui es para versiones pasadas
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void saveImageToInternalStorage(Bitmap bitmap) {
        File directory = getDir("images", Context.MODE_PRIVATE); // Directorio privado para imágenes
        File file = new File(directory, IMAGE_FILE_NAME); // permite que la imagen sea accesible solo en el app desarrollado
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file); // aqui si use ia  para comprimir la imagen
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // al formato PNG
            Toast.makeText(this, "Imagen guardada exitosamente", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace(); // en caso de un error
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

    private void loadImageFromInternalStorage() { // aqui carga la imagen previa que guardaste desde el almacen interno
        File directory = getDir("images", Context.MODE_PRIVATE); // y eso lo muestra con ImageView
        File file = new File(directory, IMAGE_FILE_NAME);
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file); // el escenario es si encuentra el archivo
                Bitmap bitmap = BitmapFactory.decodeStream(fis); // dentro del directorio privado del app
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