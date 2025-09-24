package com.example.projectcapstone.ui.Autenticacion.InicioSesion;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import com.example.projectcapstone.R;

public class InicioSesion extends Fragment implements View.OnClickListener{
    TextInputEditText etCorreo, etPassword;
    MaterialButton btnSiguiente;
    TextView btnOlvidePassword, btnCancelar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inicio_sesion, container, false);

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
            mostrarAlerta("Campos incompletos", "Por favor, ingresa tu correo y contraseña.");
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

                    if (json.getString("status").equals("success")) {
                        JSONObject user = json.getJSONObject("usuario");

                        // Guardamos datos de sesión
                        SharedPreferences prefs = requireContext().getSharedPreferences("usuario", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("id_estudiante", user.getInt("id_estudiante"));
                        editor.putString("nombre", user.getString("nombre"));
                        editor.putString("apellidos", user.getString("apellidos"));
                        editor.putInt("tipo_usuario", user.getInt("tipo_usuario"));
                        editor.apply();

                        // Navegamos al home
                        NavController navController = Navigation.findNavController(requireView());
                        navController.navigate(R.id.action_nav_inicio_sesion_to_nav_inicio);

                    } else {
                        // Mensaje de error enviado desde el servidor
                        mostrarAlerta("Acceso denegado", "El correo no se encuentra registrado");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mostrarAlerta("Error inesperado", "Ocurrió un problema al procesar la respuesta del servidor. Intenta nuevamente.");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 0) {
                    mostrarAlerta("Sin conexión", "No se pudo conectar con el servidor. Revisa tu conexión a Internet.");
                } else {
                    mostrarAlerta("Error de servidor", "Hubo un problema al intentar iniciar sesión. Código: " + statusCode);
                }
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.alert_dialog_res_negativa, null);

        TextView tvTitulo = dialogView.findViewById(R.id.tvTituloError);
        TextView tvMensaje = dialogView.findViewById(R.id.tvMensajeError);
        Button btnAceptar = dialogView.findViewById(R.id.btnFuncionalidadError);

        tvTitulo.setText(titulo);
        tvMensaje.setText(mensaje);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Fondo transparente (para que se respete el CardView con esquinas redondeadas)
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        btnAceptar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void onClick(View v) {
        if(v == btnCancelar){
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_nav_inicio_sesion_to_nav_start_upn);
        }
    }
}