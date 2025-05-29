package com.example.lab5_20206156;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    // para esta funcion si usa IA porque considere necesario que se debe reprogramar las alarmas
    // de notificación cuando el app se ha reiniciado y las alarmas se eliminan
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Toast.makeText(context, "Reiniciando alarmas de medicamentos...", Toast.LENGTH_LONG).show();

            Gson gson = new Gson(); // medicamento
            String json = SharedPreferencesHelper.getMedicationsJson(context); // durante la restauración, usaremos los datos guardados
            Type type = new TypeToken<ArrayList<Medication>>() {}.getType(); // y configurades con SharedPreferencies
            List<Medication> medicationList = gson.fromJson(json, type); //y los vuelve a programarlo con otro arreglo

            if (medicationList != null) { // al no haber una lista vacia
                for (Medication medication : medicationList) { // volvemos a llamar cada fucnion
                    NotificationHelper.scheduleMedicationAlarm(context, medication); // que vuelve a configurar su alarma
                }
            }

            String motivationalMessage = context.getSharedPreferences("MyMedicationPrefs", Context.MODE_PRIVATE) // mensaje motivacional
                    .getString("motivationalMessage", "¡Hoy es un buen día para cuidar tu salud!"); // aqui recupera el mensaje motivacional
            int motivationalFrequency = context.getSharedPreferences("MyMedicationPrefs", Context.MODE_PRIVATE)
                    .getInt("motivationalFrequency", 24);
            NotificationHelper.scheduleMotivationalAlarm(context, motivationalMessage, motivationalFrequency);
        }
    }
}