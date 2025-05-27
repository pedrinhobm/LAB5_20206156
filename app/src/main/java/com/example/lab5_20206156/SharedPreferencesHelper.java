package com.example.lab5_20206156;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private static final String PREFS_FILE_NAME = "MyMedicationAppPrefs";
    private static final String KEY_MEDICATIONS_JSON = "medicationsJson";

    // Guarda la cadena JSON de medicamentos en SharedPreferences
    public static void saveMedicationsJson(Context context, String json) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MEDICATIONS_JSON, json);
        editor.apply(); // apply() para guardar asíncronamente
    }

    // Obtiene la cadena JSON de medicamentos de SharedPreferences
    public static String getMedicationsJson(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_MEDICATIONS_JSON, null); // Devuelve null si no hay datos
    }

    // Métodos para guardar y obtener el nombre de usuario y mensaje motivacional
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_MOTIVATIONAL_MESSAGE = "motivationalMessage";
    private static final String KEY_MOTIVATIONAL_FREQUENCY = "motivationalFrequency";

    public static void saveUserName(Context context, String userName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_NAME, userName);
        editor.apply();
    }

    public static String getUserName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_NAME, "Usuario");
    }

    public static void saveMotivationalMessage(Context context, String message) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MOTIVATIONAL_MESSAGE, message);
        editor.apply();
    }

    public static String getMotivationalMessage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_MOTIVATIONAL_MESSAGE, "¡Hoy es un buen día para cuidar tu salud!");
    }

    public static void saveMotivationalFrequency(Context context, int frequencyHours) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_MOTIVATIONAL_FREQUENCY, frequencyHours);
        editor.apply();
    }

    public static int getMotivationalFrequency(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_MOTIVATIONAL_FREQUENCY, 24);
    }
}
