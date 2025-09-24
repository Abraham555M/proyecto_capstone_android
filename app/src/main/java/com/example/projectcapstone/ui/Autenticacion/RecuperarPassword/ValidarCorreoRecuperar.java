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
import android.widget.TextView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ValidarCorreoRecuperar extends Fragment {
    TextInputEditText etCorreoRecuperar;
    MaterialButton btnEnviarCodigo;
    TextView btnVolver;
    String URL_RECUPERAR = ServidorConfig.URL_SERVIDOR + "estudiante/estudiante_recuperar.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_validar_correo_recuperar, container, false);

        etCorreoRecuperar = rootView.findViewById(R.id.etCorreoRecuperar);
        btnEnviarCodigo = rootView.findViewById(R.id.btnEnviarCodigo);

        btnEnviarCodigo.setOnClickListener(v -> enviarCodigo());
        return rootView;
    }

    private void enviarCodigo() {
        String correo = etCorreoRecuperar.getText().toString().trim();

        if (correo.isEmpty()) {
            mostrarAlerta("Error", "Debes ingresar tu correo");
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("accion", "enviar_codigo");
        params.put("ema_estudiante", correo);

        client.post(URL_RECUPERAR, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject json = new JSONObject(response);

                    if (json.getString("status").equals("success")) {
                        mostrarAlerta("Éxito", "Se envió un código a tu correo");

                        // ✅ Pasar el correo al siguiente fragmento (Confirmar código)
                        Bundle bundle = new Bundle();
                        bundle.putString("correo", correo);

                        NavController navController = Navigation.findNavController(requireView());
                        navController.navigate(R.id.action_nav_validar_correo_recuperar_to_nav_confirmar_password, bundle);

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