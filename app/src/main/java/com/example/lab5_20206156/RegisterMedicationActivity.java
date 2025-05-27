package com.example.lab5_20206156;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent; // Importar Intent
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // Importar ImageButton para el botón de retroceso
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class RegisterMedicationActivity extends AppCompatActivity {

    private EditText editTextMedicationName;
    private Spinner spinnerMedicationType;
    private EditText editTextDosage;
    private EditText editTextFrequency;
    private TextView textViewStartDate;
    private Button buttonSaveMedication;
    private ImageButton buttonBack; // <--- AGREGADO: Declaración del ImageButton

    private Calendar selectedCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_medication);

        // Inicializar vistas
        editTextMedicationName = findViewById(R.id.editTextMedicationName);
        spinnerMedicationType = findViewById(R.id.spinnerMedicationType);
        editTextDosage = findViewById(R.id.editTextDosage);
        editTextFrequency = findViewById(R.id.editTextFrequency);
        textViewStartDate = findViewById(R.id.textViewStartDate);
        buttonSaveMedication = findViewById(R.id.buttonSaveMedication);
        buttonBack = findViewById(R.id.buttonBack); // <--- AGREGADO: Inicialización del ImageButton

        selectedCalendar = Calendar.getInstance(); // Inicializar con la fecha y hora actual
        updateDateTimeDisplay();

        // Configurar listeners
        textViewStartDate.setOnClickListener(v -> showDateTimePicker());
        buttonSaveMedication.setOnClickListener(v -> saveMedication());

        // <--- AGREGADO: Configurar listener para el botón de retroceso
        buttonBack.setOnClickListener(v -> {
            // Crear un Intent para volver a MedicationListActivity
            Intent intent = new Intent(RegisterMedicationActivity.this, MedicationListActivity.class);
            startActivity(intent);
            // Finalizar la actividad actual para que no se quede en la pila de actividades
            finish();
        });
    }

    private void showDateTimePicker() {
        // Date Picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, month);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    // Time Picker después de seleccionar la fecha
                    showTimePicker();
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker() {
        // Time Picker
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedCalendar.set(Calendar.MINUTE, minute);
                    selectedCalendar.set(Calendar.SECOND, 0); // Resetear segundos
                    updateDateTimeDisplay();
                },
                selectedCalendar.get(Calendar.HOUR_OF_DAY),
                selectedCalendar.get(Calendar.MINUTE),
                false); // false para formato 12 horas, true para 24 horas
        timePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        textViewStartDate.setText(sdf.format(selectedCalendar.getTime()));
    }

    private void saveMedication() {
        String name = editTextMedicationName.getText().toString().trim();
        String type = spinnerMedicationType.getSelectedItem().toString();
        String dosage = editTextDosage.getText().toString().trim();
        String frequencyStr = editTextFrequency.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(dosage) || TextUtils.isEmpty(frequencyStr)) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        int frequencyHours;
        try {
            frequencyHours = Integer.parseInt(frequencyStr);
            if (frequencyHours <= 0) {
                Toast.makeText(this, "La frecuencia debe ser un número positivo.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Frecuencia inválida. Ingresa un número.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generar un ID único para el medicamento
        String id = UUID.randomUUID().toString();
        long startDateMillis = selectedCalendar.getTimeInMillis();

        Medication newMedication = new Medication(id, name, type, dosage, frequencyHours, startDateMillis);

        // Cargar medicamentos existentes
        Gson gson = new Gson();
        String json = SharedPreferencesHelper.getMedicationsJson(this);
        Type typeList = new TypeToken<ArrayList<Medication>>() {}.getType();
        List<Medication> medicationList = gson.fromJson(json, typeList);

        if (medicationList == null) {
            medicationList = new ArrayList<>();
        }

        // Añadir el nuevo medicamento
        medicationList.add(newMedication);

        // Guardar la lista actualizada
        String updatedJson = gson.toJson(medicationList);
        SharedPreferencesHelper.saveMedicationsJson(this, updatedJson);

        // Programar la primera notificación para este medicamento
        NotificationHelper.scheduleMedicationAlarm(this, newMedication);

        Toast.makeText(this, "Medicamento guardado y recordatorio programado.", Toast.LENGTH_LONG).show();
        finish(); // Volver a la lista de medicamentos
    }
}