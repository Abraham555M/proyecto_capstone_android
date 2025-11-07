package com.example.projectcapstone.ui.Configuracion;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class SessionManager {
    private static final String PREF_NAME = "usuario";
    private static final String KEY_ID = "id_estudiante";
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_APELLIDOS = "apellidos";
    private static final String KEY_TIPO_USUARIO = "tipo_usuario";
    private static final String KEY_SESION_ACTIVA = "isSesionActiva";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ===============================
    // GUARDAR DATOS DE SESIÓN
    // ===============================
    public void guardarSesion(JSONObject user) {
        try {
            editor.putInt(KEY_ID, user.getInt("id_estudiante"));
            editor.putString(KEY_NOMBRE, user.getString("nombre"));
            editor.putString(KEY_APELLIDOS, user.getString("apellidos"));
            editor.putInt(KEY_TIPO_USUARIO, user.getInt("tipo_usuario"));
            editor.putBoolean(KEY_SESION_ACTIVA, true);
            editor.apply();

            Log.d("SESSION_MANAGER", "✅ Sesión guardada correctamente: " + user.getString("nombre"));
        } catch (JSONException e) {
            Log.e("SESSION_MANAGER", "❌ Error al guardar sesión: " + e.getMessage());
        }
    }

    // ===============================
    // OBTENER DATOS
    // ===============================
    public int getIdEstudiante() {
        return prefs.getInt(KEY_ID, -1);
    }

    public String getNombre() {
        return prefs.getString(KEY_NOMBRE, null);
    }

    public String getApellidos() {
        return prefs.getString(KEY_APELLIDOS, null);
    }

    public int getTipoUsuario() {
        return prefs.getInt(KEY_TIPO_USUARIO, -1);
    }

    // ===============================
    // VERIFICAR SESIÓN ACTIVA
    // ===============================
    public boolean isSesionActiva() {
        return prefs.getBoolean(KEY_SESION_ACTIVA, false);
    }

    // ===============================
    // CERRAR SESIÓN
    // ===============================
    public void cerrarSesion() {
        editor.clear();
        editor.apply();
        Log.d("SESSION_MANAGER", "🚪 Sesión cerrada correctamente");
    }
}
