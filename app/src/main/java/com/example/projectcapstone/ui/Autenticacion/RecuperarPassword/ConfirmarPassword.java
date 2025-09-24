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

public class ConfirmarPassword extends Fragment {
    EditText etCodigo;
    Button btnValidarCodigo;
    String correo;
    String URL_RECUPERAR = ServidorConfig.URL_SERVIDOR + "estudiante/estudiante_recuperar.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirmar_cambio_password, container, false);

        etCodigo = rootView.findViewById(R.id.etCodigo);
        btnValidarCodigo = rootView.findViewById(R.id.btnValidarCodigo);

        // 📌 Recuperar correo del fragmento anterior
        if (getArguments() != null) {
            correo = getArguments().getString("correo");
        }

        btnValidarCodigo.setOnClickListener(v -> validarCodigo());
        return rootView;

    }

    private void validarCodigo() {
        String codigo = etCodigo.getText().toString().trim();

        if (codigo.isEmpty()) {
            mostrarAlerta("Error", "Debes ingresar el código recibido");
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("accion", "validar_codigo");
        params.put("ema_estudiante", correo);
        params.put("codigo", codigo);

        client.post(URL_RECUPERAR, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject json = new JSONObject(response);

                    if (json.getString("status").equals("success")) {
                        mostrarAlerta("Éxito", "Código validado correctamente");

                        // ✅ Pasar correo al siguiente paso (cambiar contraseña)
                        Bundle bundle = new Bundle();
                        bundle.putString("correo", correo);

                        /*NavController navController = Navigation.findNavController(requireView());
                        navController.navigate(R.id.action_nav_confirmar_password_to_nav_cambiar_password, bundle);*/

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