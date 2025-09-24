package com.example.projectcapstone.ui.Autenticacion.RecuperarPassword;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.google.android.material.button.MaterialButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ConfirmarPassword extends Fragment {

    // Views del código
    private EditText etCodigo;
    private TextView tvDigito1, tvDigito2, tvDigito3, tvDigito4;
    private TextView tvEmail;
    private List<TextView> digitViews;

    // Botones
    private MaterialButton btnValidarCodigo;
    private TextView btnCancelarCodigo;

    // Variables
    private String correo;
    private String URL_RECUPERAR = ServidorConfig.URL_SERVIDOR + "estudiante/estudiante_recuperar.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirmar_cambio_password, container, false);

        inicializarViews(rootView);
        configurarCorreo();
        configurarCodeInput();
        configurarBotones();

        return rootView;
    }

    private void inicializarViews(View rootView) {
        // Campo de código oculto
        etCodigo = rootView.findViewById(R.id.etCodigo);

        // Círculos de dígitos
        tvDigito1 = rootView.findViewById(R.id.tvDigito1);
        tvDigito2 = rootView.findViewById(R.id.tvDigito2);
        tvDigito3 = rootView.findViewById(R.id.tvDigito3);
        tvDigito4 = rootView.findViewById(R.id.tvDigito4);

        // Lista para manejo fácil
        digitViews = Arrays.asList(tvDigito1, tvDigito2, tvDigito3, tvDigito4);

        // Otros views
        tvEmail = rootView.findViewById(R.id.tvEmail);
        btnValidarCodigo = rootView.findViewById(R.id.btnValidarCodigo);
        btnCancelarCodigo = rootView.findViewById(R.id.btnCancelarCodigo);
    }

    private void configurarCorreo() {
        // 📌 Recuperar correo del fragmento anterior
        if (getArguments() != null) {
            correo = getArguments().getString("correo");
            if (correo != null) {
                // Mostrar email parcialmente censurado
                tvEmail.setText(censurarEmail(correo));
            }
        }
    }

    private void configurarCodeInput() {
        // TextWatcher para el campo oculto
        etCodigo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String codigo = s.toString();
                actualizarCirculos(codigo);
                // Ya no auto-validamos, solo cuando se presione el botón
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Click en círculos para enfocar el campo
        for (TextView circle : digitViews) {
            circle.setOnClickListener(v -> {
                etCodigo.requestFocus();
                mostrarTeclado();
            });
        }

        // Enfocar automáticamente al crear la vista
        etCodigo.post(() -> {
            etCodigo.requestFocus();
            mostrarTeclado();
        });
    }

    private void configurarBotones() {
        btnValidarCodigo.setOnClickListener(v -> validarCodigo());

        if (btnCancelarCodigo != null) {
            btnCancelarCodigo.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(requireView());
                navController.popBackStack();
            });
        }
    }

    private void actualizarCirculos(String codigo) {
        for (int i = 0; i < digitViews.size(); i++) {
            TextView circle = digitViews.get(i);

            if (i < codigo.length()) {
                // Círculo con dígito - texto negro sin cambiar fondo
                circle.setText(String.valueOf(codigo.charAt(i)));
                circle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            } else {
                // Círculo vacío
                circle.setText("");
                circle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            }
        }
    }

    private void validarCodigo() {
        String codigo = etCodigo.getText().toString().trim();

        if (codigo.isEmpty()) {
            mostrarAlerta("Error", "Debes ingresar el código recibido");
            return;
        }

        if (codigo.length() != 4) {
            mostrarAlerta("Error", "El código debe tener 4 dígitos");
            return;
        }

        // Deshabilitar botón mientras se valida
        btnValidarCodigo.setEnabled(false);
        btnValidarCodigo.setText("Validando...");

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

                        NavController navController = Navigation.findNavController(requireView());
                        navController.navigate(R.id.action_nav_confirmar_password_to_nav_cambiar_password, bundle);

                    } else {
                        mostrarAlerta("Error", json.getString("message"));
                        limpiarCodigo();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mostrarAlerta("Error", "Error al procesar respuesta");
                    limpiarCodigo();
                } finally {
                    restaurarBoton();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mostrarAlerta("Error", "No se pudo conectar con el servidor");
                limpiarCodigo();
                restaurarBoton();
            }
        });
    }

    private void limpiarCodigo() {
        etCodigo.setText("");
        actualizarCirculos("");
    }

    private void restaurarBoton() {
        btnValidarCodigo.setEnabled(true);
        btnValidarCodigo.setText("Validar");
    }

    private void mostrarTeclado() {
        if (getContext() != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etCodigo, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    private String censurarEmail(String email) {
        if (email == null || !email.contains("@")) return email;

        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) return email;

        String censored = username.substring(0, 2);
        for (int i = 2; i < username.length(); i++) {
            censored += "x";
        }

        return censored + "@" + domain;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        new AlertDialog.Builder(requireContext())
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }
}