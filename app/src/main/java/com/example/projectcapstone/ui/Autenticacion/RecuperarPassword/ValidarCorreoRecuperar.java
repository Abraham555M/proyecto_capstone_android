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
import android.widget.ProgressBar;
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

public class ValidarCorreoRecuperar extends Fragment implements View.OnClickListener{
    TextInputEditText etCorreoRecuperar;
    MaterialButton btnEnviarCodigo;
    TextView btnVolver;
    private View loader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_validar_correo_recuperar, container, false);

        etCorreoRecuperar = rootView.findViewById(R.id.etCorreoRecuperar);
        btnEnviarCodigo = rootView.findViewById(R.id.btnEnviarCodigo);
        btnVolver = rootView.findViewById(R.id.btnVolver);
        loader = rootView.findViewById(R.id.includeLoader);

        btnVolver.setOnClickListener(this);
        btnEnviarCodigo.setOnClickListener(v -> enviarCodigo());
        return rootView;
    }

    private void showLoader() {
        loader.setVisibility(View.VISIBLE);
        btnEnviarCodigo.setEnabled(false);
    }

    private void hideLoader() {
        loader.setVisibility(View.GONE);
        btnEnviarCodigo.setEnabled(true);
    }

    private void enviarCodigo() {
        String url = ServidorConfig.URL_SERVIDOR + "estudiante/estudiante_recuperar.php";
        String correo = etCorreoRecuperar.getText().toString().trim();

        if (correo.isEmpty()) {
            mostrarAlertaPersonalizada("Campo requerido", "Por favor, ingresa tu correo electrónico.", false);
            return;
        }

        showLoader();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("accion", "enviar_codigo");
        params.put("ema_estudiante", correo);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                hideLoader();

                try {
                    String response = new String(responseBody);
                    JSONObject json = new JSONObject(response);

                    if (json.getString("status").equals("success")) {
                        mostrarAlertaPersonalizada("Código enviado",
                                "Hemos enviado un código de verificación a tu correo electrónico.", true);

                        // ✅ Pasar el correo al siguiente fragmento (Confirmar código)
                        Bundle bundle = new Bundle();
                        bundle.putString("correo", correo);

                        NavController navController = Navigation.findNavController(requireView());
                        navController.navigate(
                                R.id.action_nav_validar_correo_recuperar_to_nav_confirmar_password,
                                bundle
                        );

                    } else {
                        mostrarAlertaPersonalizada("No encontrado",
                                "El correo ingresado no está registrado en el sistema.", false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mostrarAlertaPersonalizada("Error inesperado",
                            "Ocurrió un problema al procesar la respuesta. Intenta nuevamente.", false);

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                hideLoader();

                if (statusCode == 0) {
                    mostrarAlertaPersonalizada("Sin conexión",
                            "No se pudo conectar con el servidor. Revisa tu conexión a Internet.", false);
                } else {
                    mostrarAlertaPersonalizada("Error de servidor",
                            "Hubo un problema al procesar tu solicitud. Código de error: " + statusCode, false);
                }
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

    @Override
    public void onClick(View v) {
        if(v == btnVolver){
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_nav_validar_correo_recuperar_to_nav_inicio_sesion);
        }
    }
}
