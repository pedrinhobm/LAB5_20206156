package com.example.lab5_20206156;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyMedicationPrefs";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_MOTIVATIONAL_MESSAGE = "motivationalMessage";
    private static final String KEY_MOTIVATIONAL_FREQUENCY = "motivationalFrequency";
    private EditText editTextUserName;
    private EditText editTextMotivationalMessage;
    private EditText editTextMotivationalFrequency;
    private Button buttonSaveSettings;
    private ImageButton buttonBack;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // en la configuración encontramos la edición del nombre del usuario,mensaje motivacional y la frecuencia por hora en notificar
        editTextUserName = findViewById(R.id.editTextUserName); // por eso es cuando abres por primera vez el app , solo dirá " hola, usuario"
        editTextMotivationalMessage = findViewById(R.id.editTextMotivationalMessage); // y ese nombre junto con el  mensaje a editar ,
        editTextMotivationalFrequency = findViewById(R.id.editTextMotivationalFrequency); // serán guardados en SharedPreferences
        buttonSaveSettings = findViewById(R.id.buttonSaveSettings);
        buttonBack = findViewById(R.id.buttonBack);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loadSettings();
        buttonSaveSettings.setOnClickListener(v -> saveSettings()); // asi como las que las configuraciones guardadas

        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent); // con la flecha <- retornamos a la vista principal
            finish();
        });
    }

    private void loadSettings() {  // por ello es que cuando salimos y volvemos el app , se debe cargar configuraciones realizadas
        String userName = sharedPreferences.getString(KEY_USER_NAME, "");
        String motivationalMessage = sharedPreferences.getString(KEY_MOTIVATIONAL_MESSAGE, "");
        int motivationalFrequency = sharedPreferences.getInt(KEY_MOTIVATIONAL_FREQUENCY, 24);
        editTextUserName.setText(userName);
        editTextMotivationalMessage.setText(motivationalMessage);
        editTextMotivationalFrequency.setText(String.valueOf(motivationalFrequency));
    }

    private void saveSettings() { // con el boton "GUARDAR CONFIGURACIONES" manda a la accion de guardarlos
        String userName = editTextUserName.getText().toString().trim(); // cada uno de los campos mencionados
        String motivationalMessage = editTextMotivationalMessage.getText().toString().trim();
        String frequencyStr = editTextMotivationalFrequency.getText().toString().trim();

        // ahora pongamos por caso que si no rellenas un campo, saldrán las siguientes advertencias
        editTextUserName.setError(null);
        editTextMotivationalMessage.setError(null);
        editTextMotivationalFrequency.setError(null);

        if (userName.isEmpty()) {
            Toast.makeText(this, "El nombre de usuario no puede estar vacío.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (motivationalMessage.isEmpty()) {
            Toast.makeText(this, "El mensaje motivacional no puede estar vacío.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (frequencyStr.isEmpty()) {
            Toast.makeText(this, "La frecuencia no puede estar vacía.", Toast.LENGTH_SHORT).show();
            return;
        }
        // lo mismo sucedera aqui en caso no hayas elegido un número del 1 hasta adelante
        int motivationalFrequency;
        try {
            motivationalFrequency = Integer.parseInt(frequencyStr);
            if (motivationalFrequency <= 0) {
                Toast.makeText(this, "La frecuencia debe ser un número positivo.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Frecuencia inválida. Ingresa un número.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_MOTIVATIONAL_MESSAGE, motivationalMessage);
        editor.putInt(KEY_MOTIVATIONAL_FREQUENCY, motivationalFrequency);
        editor.apply();
        Toast.makeText(this, "Configuraciones guardadas", Toast.LENGTH_SHORT).show(); // si guardamos , volvemos a la vista principal
        NotificationHelper.scheduleMotivationalAlarm(this, motivationalMessage, motivationalFrequency); // y con el touch te indicará guardado exitoso junto con la actualización
        finish();
    }
}