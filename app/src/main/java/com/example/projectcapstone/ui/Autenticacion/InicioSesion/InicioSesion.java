package com.example.projectcapstone.ui.Autenticacion.InicioSesion;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.widget.Button;
import android.widget.TextView;

import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Configuracion.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import com.example.projectcapstone.R;


public class InicioSesion extends Fragment implements View.OnClickListener {
    TextInputEditText etCorreo, etPassword;
    MaterialButton btnSiguiente;
    TextView btnOlvidePassword, btnCancelar;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_inicio_sesion, container, false);

        etCorreo = rootView.findViewById(R.id.etCorreo);
        etPassword = rootView.findViewById(R.id.etPassword);
        btnSiguiente = rootView.findViewById(R.id.btnSiguiente);
        btnCancelar = rootView.findViewById(R.id.btnCancelar);
        btnOlvidePassword = rootView.findViewById(R.id.btnOlvidePassword);

        btnCancelar.setOnClickListener(this);

        btnSiguiente.setOnClickListener(v -> iniciarSesion());

        btnOlvidePassword.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_nav_inicio_sesion_to_nav_validar_correo_recuperar);
        });

        return rootView;
    }

    private void iniciarSesion() {
        String url = ServidorConfig.URL_SERVIDOR + "estudiante/estudiante_login.php";

        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validaciones previas
        if (correo.isEmpty() || password.isEmpty()) {
            mostrarAlertaPersonalizada("Campos incompletos",
                    "Por favor, ingresa tu correo y contraseña.", false);
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("ema_estudiante", correo);
        params.put("pas_estudiante", password);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject json = new JSONObject(response);

                    Log.d("LOGIN_RESPONSE", response); // ✅ Para ver la respuesta completa del servidor

                    if (json.getString("status").equals("success")) {
                        // ✅ Obtenemos el objeto usuario del JSON
                        JSONObject user = json.getJSONObject("usuario");

                        // ✅ Guardamos la sesión
                        SessionManager sessionManager = new SessionManager(requireContext());
                        sessionManager.guardarSesion(user);

                        Log.d("SESION", "Sesión guardada con éxito para: " + user.getString("nombre"));

                        // ✅ Navegamos al Home
                        NavController navController = Navigation.findNavController(requireView());
                        NavOptions navOptions = new NavOptions.Builder()
                                .setPopUpTo(R.id.nav_start_upn, true)
                                .build();
                        navController.navigate(R.id.nav_inicio, null, navOptions);

                        mostrarAlertaPersonalizada("Bienvenido", "Inicio de sesión exitoso", true);

                    } else {
                        mostrarAlertaPersonalizada("Acceso denegado",
                                "Correo o contraseña incorrectos. Inténtalo nuevamente.", false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    mostrarAlertaPersonalizada("Error inesperado",
                            "Ocurrió un problema al procesar la respuesta del servidor. Intenta nuevamente.", false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 0) {
                    mostrarAlertaPersonalizada("Sin conexión",
                            "No se pudo conectar con el servidor. Revisa tu conexión a Internet.", false);
                } else {
                    mostrarAlertaPersonalizada("Error de servidor",
                            "Hubo un problema al intentar iniciar sesión. Código: " + statusCode, false);
                }
            }
        });
    }

    private void mostrarAlertaPersonalizada(String titulo, String mensaje, boolean esPositivo) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView;

        TextView tvTitulo;
        TextView tvMensaje;
        Button btnAceptar;

        if (esPositivo) {
            dialogView = inflater.inflate(R.layout.alert_dialog_res_positiva, null);
            tvTitulo = dialogView.findViewById(R.id.tvTituloExito);
            tvMensaje = dialogView.findViewById(R.id.tvMensajeExito);
            btnAceptar = dialogView.findViewById(R.id.btnFuncionalidadExito);
        } else {
            dialogView = inflater.inflate(R.layout.alert_dialog_res_negativa, null);
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


    @Override
    public void onClick(View v) {
        if (v == btnCancelar) {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_nav_inicio_sesion_to_nav_start_upn);
        }
    }
}
