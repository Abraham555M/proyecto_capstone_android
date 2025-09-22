package com.example.projectcapstone.ui.Autenticacion.RecuperarPassword;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;


public class CambiarPassword extends Fragment {
    EditText etNuevaPassword, etRepetirPassword;
    Button btnCambiarPassword;
    String correo; // recibido desde ConfirmarCambioPassword

    String URL_RECUPERAR = ServidorConfig.URL_SERVIDOR + "estudiante/estudiante_recuperar.php";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cambiar_password, container, false);
        etNuevaPassword = rootView.findViewById(R.id.etNuevaPass);
        etRepetirPassword = rootView.findViewById(R.id.etConfirmarPass);
        btnCambiarPassword = rootView.findViewById(R.id.btnCambiarPass);

        // 📌 Recuperar correo del bundle
        if (getArguments() != null) {
            correo = getArguments().getString("correo");
        }

        btnCambiarPassword.setOnClickListener(v -> cambiarPassword());
        return rootView;
    }
    private void cambiarPassword() {
        String pass1 = etNuevaPassword.getText().toString().trim();
        String pass2 = etRepetirPassword.getText().toString().trim();

        if (pass1.isEmpty() || pass2.isEmpty()) {
            mostrarAlerta("Error", "Debes ingresar ambas contraseñas");
            return;
        }

        if (!pass1.equals(pass2)) {
            mostrarAlerta("Error", "Las contraseñas no coinciden");
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("accion", "cambiar_password");
        params.put("ema_estudiante", correo);
        params.put("nueva_password", pass1);

        client.post(URL_RECUPERAR, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject json = new JSONObject(response);

                    if (json.getString("status").equals("success")) {
                        mostrarAlerta("Éxito", "Tu contraseña ha sido cambiada");

                        // ✅ Regresar al inicio de sesión
                        NavController navController = Navigation.findNavController(requireView());
                        navController.navigate(R.id.nav_inicio_sesion);

                    } else {
                        mostrarAlerta("Error", json.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mostrarAlerta("Error", "Error al procesar respuesta");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mostrarAlerta("Error", "No se pudo conectar con el servidor");
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        new AlertDialog.Builder(requireContext())
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }
}
