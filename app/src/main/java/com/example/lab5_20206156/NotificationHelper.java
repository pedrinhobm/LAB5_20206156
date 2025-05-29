package com.example.lab5_20206156;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.Calendar;
public class NotificationHelper {
    public static final String CHANNEL_PILL_ID = "channel_pill";
    public static final String CHANNEL_SYRUP_ID = "channel_syrup";
    public static final String CHANNEL_AMPOULE_ID = "channel_ampoule";
    public static final String CHANNEL_CAPSULE_ID = "channel_capsule";
    public static final String CHANNEL_MOTIVATIONAL_ID = "channel_motivational";
    private static final int MOTIVATIONAL_NOTIFICATION_ID = 999;

    // aqui cree los canales de notificación por tipo de medicamento como “Pastilla”, “Jarabe”, “Ampolla”, “Cápsula”
    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // primero verificamos la version de android de acuerdo a sdk
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            // divido en 5 ( 4 para los tipos de pastilla) y 1 para el mensaje motivacional

            NotificationChannel pillChannel = new NotificationChannel(CHANNEL_PILL_ID, "Pastilla", NotificationManager.IMPORTANCE_HIGH);
            pillChannel.setDescription("Recordatorios para tomar pastillas."); // y asi te va anunciar a la hora que debes tomar cada tipo de medicamento
            pillChannel.enableVibration(true);
            notificationManager.createNotificationChannel(pillChannel);

            NotificationChannel syrupChannel = new NotificationChannel(CHANNEL_SYRUP_ID, "Jarabe", NotificationManager.IMPORTANCE_HIGH);
            syrupChannel.setDescription("Recordatorios para tomar jarabes.");
            syrupChannel.enableVibration(true);
            notificationManager.createNotificationChannel(syrupChannel);

            NotificationChannel ampouleChannel = new NotificationChannel(CHANNEL_AMPOULE_ID, "Ampolla", NotificationManager.IMPORTANCE_HIGH);
            ampouleChannel.setDescription("Recordatorios para usar ampollas.");
            ampouleChannel.enableVibration(true);
            notificationManager.createNotificationChannel(ampouleChannel);

            NotificationChannel capsuleChannel = new NotificationChannel(CHANNEL_CAPSULE_ID, "Cápsula", NotificationManager.IMPORTANCE_HIGH);
            capsuleChannel.setDescription("Recordatorios para tomar cápsulas.");
            capsuleChannel.enableVibration(true);
            notificationManager.createNotificationChannel(capsuleChannel);

            NotificationChannel motivationalChannel = new NotificationChannel(CHANNEL_MOTIVATIONAL_ID, "Mensaje Motivacional", NotificationManager.IMPORTANCE_DEFAULT);
            motivationalChannel.setDescription("Mensajes motivacionales diarios.");
            notificationManager.createNotificationChannel(motivationalChannel);

        } else { // para el caso que no cumpla ni proceda una versión, agrego un log para anunciarlo
            Log.d("NotificationChannels", "No se crean canales de notificación.");
        }
    }

    public static void scheduleMedicationAlarm(Context context, Medication medication) { // para la alarma de medicamento
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class); // porque para cada notificacion
        intent.setAction("ACTION_MEDICATION_REMINDER");  // se va a recopilar los datos de cada medicamento
        intent.putExtra("medication_id", medication.getId());
        intent.putExtra("medication_name", medication.getName());
        intent.putExtra("medication_type", medication.getType());
        intent.putExtra("medication_dosage", medication.getDosage());
        intent.putExtra("medication_frequency", medication.getFrequencyHours()); // asi como la frecuencia que debes ingerir por horas
        intent.putExtra("medication_start_date_millis", medication.getStartDateMillis()); // y del tiempo que se calculará mas adelante

        // en este caso use ia para el obtener el hash del ID del medicamento
        int requestCode = medication.getId().hashCode(); // como request code en un PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance(); // lo mismo se usa en el caso de las notificaciones que es por instancia para calcular el tiempo
        calendar.setTimeInMillis(medication.getStartDateMillis()); // esos datos que son para la primera hora lo colocamos en ms
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) { // en caso haya pasado la 1ra hora pactada, se considera la siguiente
            long diff = System.currentTimeMillis() - calendar.getTimeInMillis();
            long hoursPassed = diff / (1000 * 60 * 60);
            long nextOccurrenceHours = (hoursPassed / medication.getFrequencyHours() + 1) * medication.getFrequencyHours();
            calendar.setTimeInMillis(medication.getStartDateMillis() + nextOccurrenceHours * (1000 * 60 * 60));
        }
        long intervalMillis = (long) medication.getFrequencyHours() * 60 * 60 * 1000; // para convertir una alarma repetida, obtenemos la frecuencia de hora convertido en ms ( 60 min , 60 seg y 1000 ms)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // nuevamente verificamos l version de android, si es el adecuado para el enunciado ...
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent); // uso setExactAndAllowWhileIdle para una mayor precisión ( aqui si use IA)
        } else { // si es lo contrario , solo tiende a repetirlo
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intervalMillis, pendingIntent);
        } // y el Toast cuando ya has programado la alarma para el medicamento registrado
        Toast.makeText(context, "Alarma programada para " + medication.getName(), Toast.LENGTH_SHORT).show();
    }
    public static void cancelMedicationAlarm(Context context, String medicationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction("ACTION_MEDICATION_REMINDER");
        int requestCode = medicationId.hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel(); // también cancelamos el PendingIntent
            Toast.makeText(context, "Alarma cancelada", Toast.LENGTH_SHORT).show(); // este es el toast de la alarma cancelada
        } else {
            Log.d("AlarmDebug", "No se encontró PendingIntent para cancelar la alarma" );
        }
    }


    @SuppressLint("MissingPermission")
    public static void showMedicationNotification(Context context, String medicationName, String medicationType, String dosage) {
        String channelId; // en esta funcion se mostrara notificación de medicamento
        int smallIconResId; // es por ello que se va distribuir por el canal y icono de cada medicamento
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
                channelId = CHANNEL_PILL_ID;
                smallIconResId = R.drawable.ic_pill_notification;
                break;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(smallIconResId)
                .setContentTitle("Recordatorio de medicamento: " + medicationName)
                .setContentText("Hora de tomar " + dosage + " de " + medicationType + ".")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true); // La notificación se cierra al hacer clic
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(medicationName.hashCode(), builder.build());
    }

    public static void scheduleMotivationalAlarm(Context context, String message, int frequencyHours) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction("ACTION_MOTIVATIONAL_REMINDER");
        intent.putExtra("motivational_message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, MOTIVATIONAL_NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent); // aqui usa ia para cancelar mensaje existente al reprogramar dado que lo cambias y actualizas en la configuración

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1); // calculamos la primera hora con un tiempo estimado en milisegundos
        long intervalMillis = (long) frequencyHours * 60 * 60 * 1000; // convertimos la frecuencia de horas en ms

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // lo mismo que la anterior funcion, validamos que sea la versión adecuada
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else { // replicamos el codigo anterior con los tiempos
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intervalMillis, pendingIntent);
        }
        Toast.makeText(context, "Notificación motivacional programada cada " + frequencyHours + " horas", Toast.LENGTH_SHORT).show();
    }
    @SuppressLint("MissingPermission")
    public static void showMotivationalNotification(Context context, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_MOTIVATIONAL_ID)
                .setSmallIcon(R.drawable.ic_motivational_notification)
                .setContentTitle("¡Un mensaje dedicado para vos!")
                .setContentText(message) // se muestra el mensaje en la notificacion
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(MOTIVATIONAL_NOTIFICATION_ID, builder.build());
    }
}

