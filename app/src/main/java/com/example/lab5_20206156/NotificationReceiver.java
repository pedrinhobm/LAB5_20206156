package com.example.lab5_20206156; // Asegúrate de que el paquete coincida con tu proyecto

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log; // Importar Log para depuración
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            Log.d("NotificationReceiver", "Broadcast recibido con acción: " + action); // Log para depuración

            if ("ACTION_MEDICATION_REMINDER".equals(action)) {
                // Notificación de medicamento
                String medicationId = intent.getStringExtra("medication_id");
                String medicationName = intent.getStringExtra("medication_name");
                String medicationType = intent.getStringExtra("medication_type");
                String dosage = intent.getStringExtra("medication_dosage");
                int frequency = intent.getIntExtra("medication_frequency", 0);
                long startDateMillis = intent.getLongExtra("medication_start_date_millis", 0);

                NotificationHelper.showMedicationNotification(context, medicationName, medicationType, dosage);

                // Reprogramar la próxima alarma para este medicamento si es una alarma periódica
                // Esto es importante si no se usa setRepeating en AlarmManager
                if (frequency > 0) {
                    Medication medication = new Medication(medicationId, medicationName, medicationType, dosage, frequency, startDateMillis);
                    // Recalcular la próxima alarma
                    NotificationHelper.scheduleMedicationAlarm(context, medication);
                }

            } else if ("ACTION_MOTIVATIONAL_REMINDER".equals(action)) {
                // Notificación motivacional
                String motivationalMessage = intent.getStringExtra("motivational_message");
                NotificationHelper.showMotivationalNotification(context, motivationalMessage);

                // Reprogramar la próxima alarma motivacional si es necesario (si no se usa setRepeating)
                // Se asume que la frecuencia se guarda en SharedPreferences y se puede recuperar aquí
                int frequency = context.getSharedPreferences("MyMedicationPrefs", Context.MODE_PRIVATE)
                        .getInt("motivationalFrequency", 24);
                NotificationHelper.scheduleMotivationalAlarm(context, motivationalMessage, frequency);
            }
        }
    }
}