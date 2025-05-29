package com.example.lab5_20206156;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private static final String PREFS_FILE_NAME = "MyMedicationAppPrefs";
    private static final String KEY_MEDICATIONS_JSON = "medicationsJson";

    public static void saveMedicationsJson(Context context, String json) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit(); // en esta función, usamos sharedpreferences para la persistencia de medicamentos con KEY_MEDICATION_JSON
        editor.putString(KEY_MEDICATIONS_JSON, json); // para guardar la configuración de la cadena json de los medicamentos
        editor.apply();
    }
    public static String getMedicationsJson(Context context) {  // para esta función, obtiene la cadena  json usando sharedpreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_MEDICATIONS_JSON, null); // en caso no haya nada, lo cuenta como nulo
    }
}
