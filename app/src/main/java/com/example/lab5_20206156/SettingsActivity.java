package com.example.lab5_20206156; // Asegúrate de que el paquete coincida con tu proyecto

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText; // Cambiamos a EditText si no usas Material Design para los campos
import android.widget.ImageButton; // Importar ImageButton
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyMedicationPrefs";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_MOTIVATIONAL_MESSAGE = "motivationalMessage";
    private static final String KEY_MOTIVATIONAL_FREQUENCY = "motivationalFrequency";

    // Si usas TextInputLayout, asegúrate de vincular los TextInputEditText
    private EditText editTextUserName;
    private EditText editTextMotivationalMessage;
    private EditText editTextMotivationalFrequency;
    private Button buttonSaveSettings;
    private ImageButton buttonBack; // Declarar el ImageButton

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Inicializar vistas
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextMotivationalMessage = findViewById(R.id.editTextMotivationalMessage);
        editTextMotivationalFrequency = findViewById(R.id.editTextMotivationalFrequency);
        buttonSaveSettings = findViewById(R.id.buttonSaveSettings);
        buttonBack = findViewById(R.id.buttonBack); // Inicializar el botón de retroceso

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Cargar y mostrar configuraciones actuales
        loadSettings();

        // Configurar listener para guardar configuraciones
        buttonSaveSettings.setOnClickListener(v -> saveSettings());

        // Configurar listener para el botón de retroceso
        buttonBack.setOnClickListener(v -> {
            // Al hacer clic, simplemente finaliza esta actividad para volver a la anterior
            finish();
        });
    }

    private void loadSettings() {
        String userName = sharedPreferences.getString(KEY_USER_NAME, ""); // Vacío para que el hint se vea claro
        String motivationalMessage = sharedPreferences.getString(KEY_MOTIVATIONAL_MESSAGE, ""); // Vacío para que el hint se vea claro
        int motivationalFrequency = sharedPreferences.getInt(KEY_MOTIVATIONAL_FREQUENCY, 24); // Valor por defecto

        editTextUserName.setText(userName);
        editTextMotivationalMessage.setText(motivationalMessage);
        editTextMotivationalFrequency.setText(String.valueOf(motivationalFrequency));
    }

    private void saveSettings() {
        String userName = editTextUserName.getText().toString().trim();
        String motivationalMessage = editTextMotivationalMessage.getText().toString().trim();
        String frequencyStr = editTextMotivationalFrequency.getText().toString().trim();

        if (userName.isEmpty()) {
            editTextUserName.setError("El nombre de usuario no puede estar vacío.");
            return;
        }
        if (motivationalMessage.isEmpty()) {
            editTextMotivationalMessage.setError("El mensaje motivacional no puede estar vacío.");
            return;
        }
        if (frequencyStr.isEmpty()) {
            editTextMotivationalFrequency.setError("La frecuencia no puede estar vacía.");
            return;
        }

        int motivationalFrequency;
        try {
            motivationalFrequency = Integer.parseInt(frequencyStr);
            if (motivationalFrequency <= 0) {
                editTextMotivationalFrequency.setError("La frecuencia debe ser un número positivo.");
                return;
            }
        } catch (NumberFormatException e) {
            editTextMotivationalFrequency.setError("Frecuencia inválida. Ingresa un número.");
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_MOTIVATIONAL_MESSAGE, motivationalMessage);
        editor.putInt(KEY_MOTIVATIONAL_FREQUENCY, motivationalFrequency);
        editor.apply(); // O editor.commit();

        Toast.makeText(this, "Configuraciones guardadas", Toast.LENGTH_SHORT).show();

        // Reprogramar la notificación motivacional con la nueva frecuencia
        NotificationHelper.scheduleMotivationalAlarm(this, motivationalMessage, motivationalFrequency);

        // Opcional: Volver a la actividad anterior (MainActivity)
        finish();
    }
}