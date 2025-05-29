package com.example.lab5_20206156;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) { // las notificaciones a recibir se disitribuyen en 2
        if (intent != null) {
            String action = intent.getAction();
            if ("ACTION_MEDICATION_REMINDER".equals(action)) { // recordatorio del medicamento
                String medicationId = intent.getStringExtra("medication_id"); // se obtienen los datos del campo
                String medicationName = intent.getStringExtra("medication_name");
                String medicationType = intent.getStringExtra("medication_type");
                String dosage = intent.getStringExtra("medication_dosage");
                int frequency = intent.getIntExtra("medication_frequency", 0);
                long startDateMillis = intent.getLongExtra("medication_start_date_millis", 0);
                NotificationHelper.showMedicationNotification(context, medicationName, medicationType, dosage);

                if (frequency > 0) {
                    Medication medication = new Medication(medicationId, medicationName, medicationType, dosage, frequency, startDateMillis);
                    NotificationHelper.scheduleMedicationAlarm(context, medication); // dado la frecuencia periodica o constante, se vuelve a calcular la proxima notificacion
                }

            } else if ("ACTION_MOTIVATIONAL_REMINDER".equals(action)) { // mensaje motivacional
                String motivationalMessage = intent.getStringExtra("motivational_message"); // se obtienen los datos del campo
                NotificationHelper.showMotivationalNotification(context, motivationalMessage);
                int frequency = context.getSharedPreferences("MyMedicationPrefs", Context.MODE_PRIVATE)
                        .getInt("motivationalFrequency", 24);
                NotificationHelper.scheduleMotivationalAlarm(context, motivationalMessage, frequency); // dado la frecuencia periodica o constante, se vuelve a calcular la proxima notificacion
            }
        }
    }
}