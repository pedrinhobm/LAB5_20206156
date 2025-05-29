package com.example.lab5_20206156;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private ImageButton buttonBack;
    private Calendar selectedCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_medication);
        editTextMedicationName = findViewById(R.id.editTextMedicationName);
        spinnerMedicationType = findViewById(R.id.spinnerMedicationType);
        editTextDosage = findViewById(R.id.editTextDosage);
        editTextFrequency = findViewById(R.id.editTextFrequency);
        textViewStartDate = findViewById(R.id.textViewStartDate);
        buttonSaveMedication = findViewById(R.id.buttonSaveMedication);
        buttonBack = findViewById(R.id.buttonBack);
        selectedCalendar = Calendar.getInstance();
        updateDateTimeDisplay();
        textViewStartDate.setOnClickListener(v -> showDateTimePicker());
        buttonSaveMedication.setOnClickListener(v -> saveMedication());

        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterMedicationActivity.this, MedicationListActivity.class);
            startActivity(intent); // con la flecha <- retornamos a la lista de medicamentos
            finish();
        });
    }

    private void showDateTimePicker() { // En esta funcion de DatePickerDialog use IA para establecer la fecha inicial para ser notificado
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> { // así selecciono día,mes y año
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, month);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    showTimePicker();
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show(); // y lo muestro dentro del campo
    }

    private void showTimePicker() { // En esta funcion de TimePickerDialog use IA para establecer la fecha inicial para ser notificado
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> { // asi  selecciono hora, minutos
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay); // pero reseteamos los segundos, no considero que hará falta
                    selectedCalendar.set(Calendar.MINUTE, minute);
                    selectedCalendar.set(Calendar.SECOND, 0);
                    updateDateTimeDisplay();
                },
                selectedCalendar.get(Calendar.HOUR_OF_DAY),
                selectedCalendar.get(Calendar.MINUTE),
                false);
        timePickerDialog.show(); // y lo muestro dentro del campo
    }

    private void updateDateTimeDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        textViewStartDate.setText(sdf.format(selectedCalendar.getTime()));
    }

    private void saveMedication() { // aqui realizara la misma función de rellenear los campos como en la vista de configuración
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
            if (frequencyHours <= 0){
                Toast.makeText(this, "La frecuencia debe ser un número positivo.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Frecuencia inválida. Ingresa un número.", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = UUID.randomUUID().toString();
        long startDateMillis = selectedCalendar.getTimeInMillis(); // cargamos el tiempo en ms
        Medication newMedication = new Medication(id, name, type, dosage, frequencyHours, startDateMillis); // y todos los datos lo guardamos a la clase de Medication para mostrarlo en la vista anterior

        Gson gson = new Gson();
        String json = SharedPreferencesHelper.getMedicationsJson(this);
        Type typeList = new TypeToken<ArrayList<Medication>>() {}.getType();
        List<Medication> medicationList = gson.fromJson(json, typeList);

        if (medicationList == null) {
            medicationList = new ArrayList<>();
        }

        medicationList.add(newMedication);
        String updatedJson = gson.toJson(medicationList); // se guarda en formato Json la lista
        SharedPreferencesHelper.saveMedicationsJson(this, updatedJson);
        NotificationHelper.scheduleMedicationAlarm(this, newMedication); // aqui usamos la clase  NotificationHelper para esperar su anuncio del medicamento con su hora
        Toast.makeText(this, "Medicamento guardado y recordatorio programado.", Toast.LENGTH_LONG).show();
        finish(); // Con un Toast anuncian el guardado correcto del medicamento y tiempo de notificación al volver a la lista de medicamentos
    }
}