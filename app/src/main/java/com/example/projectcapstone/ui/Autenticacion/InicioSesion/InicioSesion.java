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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import com.example.projectcapstone.R;

public class InicioSesion extends Fragment {
    EditText etCorreo, etPassword;
    Button btnSiguiente, btnCancelar, btnOlvidePassword;
    String URL_LOGIN = "http://10.0.2.2/proyecto_capstone_php/controlador/estudiante/estudiante_login.php";
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
        btnSiguiente.setOnClickListener(v -> iniciarSesion());
        btnCancelar.setOnClickListener(v -> requireActivity().finish());
        btnOlvidePassword.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_nav_inicio_sesion_to_nav_validar_correo_recuperar);
        });
        return rootView;
    }

    private void iniciarSesion() {
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (correo.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Debes ingresar todos los campos");
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("ema_estudiante", correo);
        params.put("pas_estudiante", password);

        client.post(URL_LOGIN, params, new AsyncHttpResponseHandler() {
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

                        // Navegamos al home (ajusta el id al de tu nav_graph.xml)
                        NavController navController = Navigation.findNavController(requireView());
                        navController.navigate(R.id.action_nav_inicio_sesion_to_nav_inicio);


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