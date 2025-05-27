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

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Toast.makeText(context, "Reiniciando alarmas de medicamentos...", Toast.LENGTH_LONG).show();

            // Reprogramar todas las alarmas de medicamentos
            Gson gson = new Gson();
            String json = SharedPreferencesHelper.getMedicationsJson(context);
            Type type = new TypeToken<ArrayList<Medication>>() {}.getType();
            List<Medication> medicationList = gson.fromJson(json, type);

            if (medicationList != null) {
                for (Medication medication : medicationList) {
                    NotificationHelper.scheduleMedicationAlarm(context, medication);
                }
            }

            // Reprogramar la notificación motivacional
            String motivationalMessage = context.getSharedPreferences("MyMedicationPrefs", Context.MODE_PRIVATE)
                    .getString("motivationalMessage", "¡Hoy es un buen día para cuidar tu salud!");
            int motivationalFrequency = context.getSharedPreferences("MyMedicationPrefs", Context.MODE_PRIVATE)
                    .getInt("motivationalFrequency", 24);

            NotificationHelper.scheduleMotivationalAlarm(context, motivationalMessage, motivationalFrequency);
        }
    }
}
