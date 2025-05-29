package com.example.lab5_20206156;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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

public class MedicationListActivity extends AppCompatActivity {
    private RecyclerView recyclerViewMedications;
    private MedicationAdapter medicationAdapter;
    private List<Medication> medicationList;
    private TextView textViewNoMedications;
    private ImageButton buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_list);
        recyclerViewMedications = findViewById(R.id.recyclerViewMedications);// Para esta vista se va mostrar cada item en un RecycletView
        textViewNoMedications = findViewById(R.id.textViewNoMedications);
        FloatingActionButton fabAddMedication = findViewById(R.id.fabAddMedication); // este es el boton (+) para añadir un medicamento
        buttonBack = findViewById(R.id.buttonBack); // este es el botón de retroceso en el encabezado verde
        recyclerViewMedications.setLayoutManager(new LinearLayoutManager(this));
        medicationList = new ArrayList<>(); // creamos una lista de medicamentos
        medicationAdapter = new MedicationAdapter(medicationList); // para ello llamamos a la funcion MedicationAdapter
        recyclerViewMedications.setAdapter(medicationAdapter);

        fabAddMedication.setOnClickListener(v -> {
            Intent intent = new Intent(MedicationListActivity.this, RegisterMedicationActivity.class);
            startActivity(intent); // este es el boton (+) para dirigir a la vista de registrar medicamento
        });

        medicationAdapter.setOnItemClickListener(position -> {
            showDeleteConfirmationDialog(position); // con la configuracion del listener, eliminamos el medicamento en el adaptador del RecyclerView
        });

        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(MedicationListActivity.this, MainActivity.class);
            startActivity(intent); // con la flecha <- retornamos a la vista principal
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume(); // siguiendo la funcion anterior a la vista principal se cargan medicamentos cada vez que la actividad se reanuda
        loadMedications(); // es decir que persisten y mantienen guardados
    }
    private void loadMedications() { // para esta funcion de cargar la lista de medicamentos registrados, tambien uso SharedPreferences
        String json = SharedPreferencesHelper.getMedicationsJson(this);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Medication>>() {}.getType();
        List<Medication> loadedMedications = gson.fromJson(json, type);

        if (loadedMedications == null) { // por eso se guarda de nuevo en una lista lo que has registrado
            loadedMedications = new ArrayList<>(); // y a través de la biblioteca gson
        }

        // las acciones de la lista de medicamentos están en limpiar lo que hay en la vista, añadir los medicamentos cargados
        medicationList.clear();  // y sobretodo , llamar al adaptador la actualización de la lista
        medicationList.addAll(loadedMedications);
        medicationAdapter.updateMedications(medicationList);

        // esta selectiva permitira identificar cuando la lista está vacia
        if (medicationList.isEmpty()) { // si está vacia, colocamos el mensaje que no hay medicamentos registrados
            textViewNoMedications.setVisibility(View.VISIBLE); // a través de un textView
            recyclerViewMedications.setVisibility(View.GONE);
        } else { // si sucede lo contrario y hay medicamentos
            textViewNoMedications.setVisibility(View.GONE); // se mostrara a cada uno de la lista
            recyclerViewMedications.setVisibility(View.VISIBLE);
        }
    }
    private void showDeleteConfirmationDialog(final int position) {
        new AlertDialog.Builder(this) // en caso hagas click al boton del tacho , saldrá la alerta si eliminarás el medicamento
                .setTitle("Confirmar Eliminación") // para ello se usó el AlertDialog
                .setMessage("¿Estás seguro de que quieres eliminar este medicamento?")
                .setPositiveButton("Sí", (dialog, which) -> deleteMedication(position)) // nos redirigimos a la siguiente funcion
                .setNegativeButton("No", null) // no pasa nada
                .show();
    }

    private void deleteMedication(int position) {
        if (position >= 0 && position < medicationList.size()) {  // para la elimiacion de cada medicamentos agrego una selectiva
            Medication medicationToDelete = medicationList.get(position); // que indica si hay un numero mayor o igual a cero hasta el tamaño total hasta ese momento
            NotificationHelper.cancelMedicationAlarm(this, medicationToDelete.getId()); // aqui hay una alarma que indica cuando el medicamento ha sido eliminado

            medicationList.remove(position); // para ello primero lo eiminamos de la lista en memoria
            saveMedications(); // guardamos lo que hay
            medicationAdapter.notifyItemRemoved(position); // y colocamos el mensaje a mostrar
            Toast.makeText(this, "Medicamento eliminado", Toast.LENGTH_SHORT).show();

            if (medicationList.isEmpty()) { // y cuando la lista está vacia , se actualiza la vista con el mensaje de que no hay medicamentos registrados
                textViewNoMedications.setVisibility(View.VISIBLE); // como en la funcion de loadMedications()
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
