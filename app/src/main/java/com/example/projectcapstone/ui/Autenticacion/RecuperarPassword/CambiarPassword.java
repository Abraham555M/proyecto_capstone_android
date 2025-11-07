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

public class CambiarPassword extends Fragment {
    TextInputEditText etNuevaPassword, etRepetirPassword;
    MaterialButton btnCambiarPassword;
    String correo;
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
            mostrarAlertaPersonalizada("Error", "Debes ingresar ambas contraseñas", false);
            return;
        }

        if (!pass1.equals(pass2)) {
            mostrarAlertaPersonalizada("Error", "Las contraseñas no coinciden", false);
            return;
        }

        // ✅ Validación de contraseña: 1 mayúscula, 1 número y 1 carácter especial
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$";
        if (!pass1.matches(passwordPattern)) {
            mostrarAlertaPersonalizada("Error",
                    "La contraseña debe contener al menos una letra mayúscula, un número y un carácter especial (@#$%^&+=!)",
                    false);
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
                        mostrarAlertaPersonalizada("Éxito", "Tu contraseña ha sido cambiada", true);

                        NavController navController = Navigation.findNavController(requireView());
                        navController.navigate(R.id.nav_inicio_sesion);

                    } else {
                        mostrarAlertaPersonalizada("Error", json.getString("message"), false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mostrarAlertaPersonalizada("Error", "Error al procesar respuesta", false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mostrarAlertaPersonalizada("Error", "No se pudo conectar con el servidor", false);
            }
        });
    }

    private void mostrarAlertaPersonalizada(String titulo, String mensaje, boolean esPositivo) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView;

        if (esPositivo) {
            dialogView = inflater.inflate(R.layout.alert_dialog_res_positiva, null);
        } else {
            dialogView = inflater.inflate(R.layout.alert_dialog_res_negativa, null);
        }

        TextView tvTitulo, tvMensaje;
        Button btnAceptar;

        if (esPositivo) {
            tvTitulo = dialogView.findViewById(R.id.tvTituloExito);
            tvMensaje = dialogView.findViewById(R.id.tvMensajeExito);
            btnAceptar = dialogView.findViewById(R.id.btnFuncionalidadExito);
        } else {
            tvTitulo = dialogView.findViewById(R.id.tvTituloError);
            tvMensaje = dialogView.findViewById(R.id.tvMensajeError);
            btnAceptar = dialogView.findViewById(R.id.btnFuncionalidadError);
        }

        tvTitulo.setText(titulo);
        tvMensaje.setText(mensaje);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        btnAceptar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}

