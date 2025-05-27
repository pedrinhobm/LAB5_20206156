package com.example.lab5_20206156;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton; // Importar ImageButton
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // Asegúrate de que UUID esté importado si se usa en otras partes

public class MedicationListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMedications;
    private MedicationAdapter medicationAdapter;
    private List<Medication> medicationList;
    private TextView textViewNoMedications;
    private ImageButton buttonBack; // Declarar ImageButton para el botón de retroceso

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_list);

        // Inicializar vistas
        recyclerViewMedications = findViewById(R.id.recyclerViewMedications);
        textViewNoMedications = findViewById(R.id.textViewNoMedications);
        FloatingActionButton fabAddMedication = findViewById(R.id.fabAddMedication);
        buttonBack = findViewById(R.id.buttonBack); // Inicializar el botón de retroceso del encabezado

        recyclerViewMedications.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar la lista de medicamentos (se cargará en onResume)
        medicationList = new ArrayList<>();
        medicationAdapter = new MedicationAdapter(medicationList);
        recyclerViewMedications.setAdapter(medicationAdapter);

        // Configurar listener para el botón flotante de añadir medicamento
        fabAddMedication.setOnClickListener(v -> {
            Intent intent = new Intent(MedicationListActivity.this, RegisterMedicationActivity.class);
            startActivity(intent);
        });

        // Configurar listener para la eliminación de medicamentos en el adaptador del RecyclerView
        medicationAdapter.setOnItemClickListener(position -> {
            showDeleteConfirmationDialog(position);
        });

        // Configurar listener para el botón de retroceso en el encabezado
        // Al hacer clic, se iniciará MainActivity y luego se cerrará esta actividad.
        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(MedicationListActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Cierra MedicationListActivity para evitar una instancia duplicada en la pila
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cargar medicamentos cada vez que la actividad se reanuda (ej. al volver de RegisterMedicationActivity)
        loadMedications();
    }

    private void loadMedications() {
        String json = SharedPreferencesHelper.getMedicationsJson(this);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Medication>>() {}.getType();
        List<Medication> loadedMedications = gson.fromJson(json, type);

        if (loadedMedications == null) {
            loadedMedications = new ArrayList<>();
        }

        medicationList.clear(); // Limpiar la lista actual
        medicationList.addAll(loadedMedications); // Añadir los medicamentos cargados
        medicationAdapter.updateMedications(medicationList); // Notificar al adaptador sobre los cambios

        // Mostrar u ocultar el mensaje "No hay medicamentos registrados"
        if (medicationList.isEmpty()) {
            textViewNoMedications.setVisibility(View.VISIBLE);
            recyclerViewMedications.setVisibility(View.GONE);
        } else {
            textViewNoMedications.setVisibility(View.GONE);
            recyclerViewMedications.setVisibility(View.VISIBLE);
        }
    }


    private void showDeleteConfirmationDialog(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Estás seguro de que quieres eliminar este medicamento?")
                .setPositiveButton("Sí", (dialog, which) -> deleteMedication(position))
                .setNegativeButton("No", null)
                .show();
    }


    private void deleteMedication(int position) {
        if (position >= 0 && position < medicationList.size()) {
            Medication medicationToDelete = medicationList.get(position);

            // Cancelar la alarma asociada a este medicamento antes de eliminarlo
            NotificationHelper.cancelMedicationAlarm(this, medicationToDelete.getId());

            medicationList.remove(position); // Eliminar de la lista en memoria
            saveMedications(); // Guardar la lista actualizada en SharedPreferences
            medicationAdapter.notifyItemRemoved(position); // Notificar al adaptador para actualizar la UI
            Toast.makeText(this, "Medicamento eliminado", Toast.LENGTH_SHORT).show();

            // Actualizar visibilidad del mensaje "No hay medicamentos" si la lista queda vacía
            if (medicationList.isEmpty()) {
                textViewNoMedications.setVisibility(View.VISIBLE);
                recyclerViewMedications.setVisibility(View.GONE);
            }
        }
    }


    private void saveMedications() {
        Gson gson = new Gson();
        String json = gson.toJson(medicationList);
        SharedPreferencesHelper.saveMedicationsJson(this, json);
    }
}
