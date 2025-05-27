package com.example.lab5_20206156; // Asegúrate de que el paquete coincida con tu proyecto


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log; // Importar Log para depuración
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.Date; // Importar Date para logs

public class NotificationHelper {

    // Canales de notificación por tipo de medicamento
    public static final String CHANNEL_PILL_ID = "channel_pill";
    public static final String CHANNEL_SYRUP_ID = "channel_syrup";
    public static final String CHANNEL_AMPOULE_ID = "channel_ampoule";
    public static final String CHANNEL_CAPSULE_ID = "channel_capsule";
    public static final String CHANNEL_MOTIVATIONAL_ID = "channel_motivational";

    private static final int MOTIVATIONAL_NOTIFICATION_ID = 999; // ID para notificación motivacional

    // Crear canales de notificación (debe llamarse al inicio de la app, por ejemplo, en MainActivity)
    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

            // Canal para Pastillas
            NotificationChannel pillChannel = new NotificationChannel(
                    CHANNEL_PILL_ID,
                    "Pastilla",
                    NotificationManager.IMPORTANCE_HIGH
            );
            pillChannel.setDescription("Recordatorios para tomar pastillas.");
            pillChannel.enableVibration(true);
            notificationManager.createNotificationChannel(pillChannel);
            Log.d("NotificationChannels", "Canal 'Pastilla' creado.");


            // Canal para Jarabes
            NotificationChannel syrupChannel = new NotificationChannel(
                    CHANNEL_SYRUP_ID,
                    "Jarabe",
                    NotificationManager.IMPORTANCE_HIGH
            );
            syrupChannel.setDescription("Recordatorios para tomar jarabes.");
            syrupChannel.enableVibration(true);
            notificationManager.createNotificationChannel(syrupChannel);
            Log.d("NotificationChannels", "Canal 'Jarabe' creado.");


            // Canal para Ampollas
            NotificationChannel ampouleChannel = new NotificationChannel(
                    CHANNEL_AMPOULE_ID,
                    "Ampolla",
                    NotificationManager.IMPORTANCE_HIGH
            );
            ampouleChannel.setDescription("Recordatorios para usar ampollas.");
            ampouleChannel.enableVibration(true);
            notificationManager.createNotificationChannel(ampouleChannel);
            Log.d("NotificationChannels", "Canal 'Ampolla' creado.");


            // Canal para Cápsulas
            NotificationChannel capsuleChannel = new NotificationChannel(
                    CHANNEL_CAPSULE_ID,
                    "Cápsula",
                    NotificationManager.IMPORTANCE_HIGH
            );
            capsuleChannel.setDescription("Recordatorios para tomar cápsulas.");
            capsuleChannel.enableVibration(true);
            notificationManager.createNotificationChannel(capsuleChannel);
            Log.d("NotificationChannels", "Canal 'Cápsula' creado.");


            // Canal para Notificación Motivacional
            NotificationChannel motivationalChannel = new NotificationChannel(
                    CHANNEL_MOTIVATIONAL_ID,
                    "Mensaje Motivacional",
                    NotificationManager.IMPORTANCE_DEFAULT // Puede ser menos intrusivo
            );
            motivationalChannel.setDescription("Mensajes motivacionales diarios.");
            notificationManager.createNotificationChannel(motivationalChannel);
            Log.d("NotificationChannels", "Canal 'Mensaje Motivacional' creado.");

        } else {
            Log.d("NotificationChannels", "Versión de Android < O. No se crean canales de notificación.");
        }
    }

    // Programar alarma para un medicamento
    public static void scheduleMedicationAlarm(Context context, Medication medication) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction("ACTION_MEDICATION_REMINDER");
        intent.putExtra("medication_id", medication.getId());
        intent.putExtra("medication_name", medication.getName());
        intent.putExtra("medication_type", medication.getType());
        intent.putExtra("medication_dosage", medication.getDosage());
        intent.putExtra("medication_frequency", medication.getFrequencyHours());
        intent.putExtra("medication_start_date_millis", medication.getStartDateMillis());

        // Usar el hash del ID del medicamento como request code para un PendingIntent único
        int requestCode = medication.getId().hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Calcular la primera hora de la notificación
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(medication.getStartDateMillis());

        // Si la hora de inicio ya pasó, programar para la próxima ocurrencia
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            long diff = System.currentTimeMillis() - calendar.getTimeInMillis();
            long hoursPassed = diff / (1000 * 60 * 60);
            long nextOccurrenceHours = (hoursPassed / medication.getFrequencyHours() + 1) * medication.getFrequencyHours();
            calendar.setTimeInMillis(medication.getStartDateMillis() + nextOccurrenceHours * (1000 * 60 * 60));
            Log.d("AlarmDebug", "Hora de inicio ajustada para " + medication.getName() + ". Nueva hora: " + new Date(calendar.getTimeInMillis()));
        }

        // Programar la alarma repetitiva
        long intervalMillis = (long) medication.getFrequencyHours() * 60 * 60 * 1000; // Convertir horas a milisegundos

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Para API 23 (Marshmallow) y superior, usar setExactAndAllowWhileIdle o setAndAllowWhileIdle
            // setExactAndAllowWhileIdle es más preciso y funciona en Doze mode
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            // Para versiones anteriores, usar setRepeating
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intervalMillis, pendingIntent);
        }
        Log.d("AlarmDebug", "Alarma programada para: " + medication.getName() + " a las " + new Date(calendar.getTimeInMillis()) + " con frecuencia " + medication.getFrequencyHours() + " horas.");
        Toast.makeText(context, "Alarma programada para " + medication.getName(), Toast.LENGTH_SHORT).show();
    }

    // Cancelar alarma de un medicamento
    public static void cancelMedicationAlarm(Context context, String medicationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction("ACTION_MEDICATION_REMINDER"); // Debe coincidir con la acción usada al programar
        int requestCode = medicationId.hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel(); // También cancelar el PendingIntent
            Log.d("AlarmDebug", "Alarma cancelada para: " + medicationId);
            Toast.makeText(context, "Alarma cancelada para " + medicationId, Toast.LENGTH_SHORT).show();
        } else {
            Log.d("AlarmDebug", "No se encontró PendingIntent para cancelar la alarma: " + medicationId);
        }
    }

    // Mostrar notificación de medicamento
    @SuppressLint("MissingPermission") // Suprime la advertencia de lint para POST_NOTIFICATIONS
    public static void showMedicationNotification(Context context, String medicationName, String medicationType, String dosage) {
        String channelId;
        int smallIconResId;

        // Determinar el canal y el icono según el tipo de medicamento
        switch (medicationType) {
            case "Pastilla":
                channelId = CHANNEL_PILL_ID;
                smallIconResId = R.drawable.ic_pill_notification;
                break;
            case "Jarabe":
                channelId = CHANNEL_SYRUP_ID;
                smallIconResId = R.drawable.ic_syrup_notification;
                break;
            case "Ampolla":
                channelId = CHANNEL_AMPOULE_ID;
                smallIconResId = R.drawable.ic_ampoule_notification;
                break;
            case "Cápsula":
                channelId = CHANNEL_CAPSULE_ID;
                smallIconResId = R.drawable.ic_capsule_notification;
                break;
            default:
                channelId = CHANNEL_PILL_ID; // Canal por defecto
                smallIconResId = R.drawable.ic_pill_notification; // Icono por defecto
                break;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(smallIconResId)
                .setContentTitle("Recordatorio de Medicamento: " + medicationName)
                .setContentText("Es hora de tomar " + dosage + " de " + medicationType + ".")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true); // La notificación se cierra al hacer clic

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // Usar un ID de notificación único para cada medicamento (o un ID base + hash)
        notificationManager.notify(medicationName.hashCode(), builder.build());
        Log.d("NotificationDebug", "Notificación de medicamento mostrada: " + medicationName);
    }

    // Programar alarma para notificación motivacional
    public static void scheduleMotivationalAlarm(Context context, String message, int frequencyHours) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction("ACTION_MOTIVATIONAL_REMINDER");
        intent.putExtra("motivational_message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, MOTIVATIONAL_NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Cancelar cualquier alarma motivacional existente para reprogramar
        alarmManager.cancel(pendingIntent);

        // Calcular la primera hora (por ejemplo, 1 hora después de guardar o la próxima hora completa)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1); // Empezar en la próxima hora

        long intervalMillis = (long) frequencyHours * 60 * 60 * 1000; // Convertir horas a milisegundos

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intervalMillis, pendingIntent);
        }
        Log.d("AlarmDebug", "Alarma motivacional programada con mensaje: '" + message + "' cada " + frequencyHours + " horas.");
        Toast.makeText(context, "Notificación motivacional programada cada " + frequencyHours + " horas.", Toast.LENGTH_SHORT).show();
    }

    // Mostrar notificación motivacional
    @SuppressLint("MissingPermission") // Suprime la advertencia de lint para POST_NOTIFICATIONS
    public static void showMotivationalNotification(Context context, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_MOTIVATIONAL_ID)
                .setSmallIcon(R.drawable.ic_motivational_notification)
                .setContentTitle("¡Un Mensaje para ti!")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(MOTIVATIONAL_NOTIFICATION_ID, builder.build());
        Log.d("NotificationDebug", "Notificación motivacional mostrada: " + message);
    }
}

