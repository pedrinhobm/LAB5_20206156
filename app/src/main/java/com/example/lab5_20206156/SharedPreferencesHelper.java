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

}
