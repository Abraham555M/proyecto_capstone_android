package com.example.projectcapstone.ui.Autenticacion.CrearCuenta;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.projectcapstone.MainActivity;
import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Configuracion.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class ValidarCorreoCrear extends Fragment implements View.OnClickListener {
    private TextView tvDigit1, tvDigit2, tvDigit3, tvDigit4, tvCancelar, tvEmail;
    private EditText etCodigo;
    private MaterialButton btnValidar;

    // Variables que vienen del fragment anterior
    private String nombres, apePat, apeMat, correo, contrasena, celular;
    private int idSexo, idSede;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_validar_correo_crear, container, false);

        // Referencias UI
        tvDigit1 = rootView.findViewById(R.id.tvDigito1);
        tvDigit2 = rootView.findViewById(R.id.tvDigito2);
        tvDigit3 = rootView.findViewById(R.id.tvDigito3);
        tvDigit4 = rootView.findViewById(R.id.tvDigito4);
        etCodigo = rootView.findViewById(R.id.etCodigo);
        btnValidar = rootView.findViewById(R.id.btnValidarCodigo);
        tvCancelar = rootView.findViewById(R.id.btnCancelarCodigo);
        tvEmail = rootView.findViewById(R.id.tvEmail);

        btnValidar.setOnClickListener(this);
        tvCancelar.setOnClickListener(this);

        // Recuperar datos del Bundle
        if (getArguments() != null) {
            nombres = getArguments().getString("nombres");
            apePat = getArguments().getString("apePat");
            apeMat = getArguments().getString("apeMat");
            correo = getArguments().getString("correo");
            contrasena = getArguments().getString("contrasena");
            celular = getArguments().getString("celular");
            idSexo = getArguments().getInt("id_sexo");
            idSede = getArguments().getInt("id_sede");
            tvEmail.setText(correo);
        }

        sessionManager = new SessionManager(requireContext());

        // AutoFocus al EditText oculto
        etCodigo.requestFocus();

        // Listener para mostrar los dígitos ingresados
        etCodigo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                actualizarDigitos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return rootView;
    }

    private void actualizarDigitos(String codigo) {
        // Reiniciar texto
        tvDigit1.setText("");
        tvDigit2.setText("");
        tvDigit3.setText("");
        tvDigit4.setText("");

        // Reflejar caracteres digitados
        if (codigo.length() > 0) tvDigit1.setText(String.valueOf(codigo.charAt(0)));
        if (codigo.length() > 1) tvDigit2.setText(String.valueOf(codigo.charAt(1)));
        if (codigo.length() > 2) tvDigit3.setText(String.valueOf(codigo.charAt(2)));
        if (codigo.length() > 3) tvDigit4.setText(String.valueOf(codigo.charAt(3)));
    }

    private void validarCodigoYRegistrar(String codigo) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("codigo", codigo);
        params.put("correo", correo);
        params.put("nombres", nombres);
        params.put("apePat", apePat);
        params.put("apeMat", apeMat);
        params.put("contrasena", contrasena);
        params.put("celular", celular);
        params.put("id_sexo", idSexo);
        params.put("id_sede", idSede);
        params.put("fecha_registro", getFechaActual());

        String url = ServidorConfig.URL_SERVIDOR + "estudiante/validar_codigo_registrar.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONObject json = new JSONObject(respuesta);
                    String status = json.getString("status");

                    switch (status) {
                        case "ok":
                            // Guardar sesión con los datos devueltos
                            if (json.has("user")) {
                                JSONObject user = json.getJSONObject("user");
                                sessionManager.guardarSesion(user);
                            }

                            Toast.makeText(requireContext(), "Cuenta creada correctamente ✅", Toast.LENGTH_SHORT).show();

                            // Reiniciar la MainActivity para que detecte la sesión activa
                            Intent intent = new Intent(requireContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            requireActivity().finish();

                            break;

                        case "codigo_invalido":
                            mostrarAlertaError("Código inválido", "El código que ingresaste es incorrecto.");
                            break;

                        case "codigo_expirado":
                            mostrarAlertaError("Código expirado", "El código ha caducado. Solicita uno nuevo.");
                            break;

                        default:
                            String msg = json.optString("msg", "Error al registrar. Inténtalo de nuevo.");
                            mostrarAlertaError("Error", msg);
                            break;
                    }

                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Error inesperado en la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.alert_dialog_res_negativa, null);

        TextView tvTitulo = dialogView.findViewById(R.id.tvTituloError);
        TextView tvMensaje = dialogView.findViewById(R.id.tvMensajeError);
        MaterialButton btnOk = dialogView.findViewById(R.id.btnFuncionalidadError);

        tvTitulo.setText(titulo);
        tvMensaje.setText(mensaje);

        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        btnOk.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    private String getFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public void onClick(View v) {
        if (v == btnValidar) {
            String codigo = etCodigo.getText().toString().trim();

            if (codigo.length() != 4) {
                Toast.makeText(requireContext(), "Ingresa el código de 4 dígitos", Toast.LENGTH_SHORT).show();
                return;
            }

            validarCodigoYRegistrar(codigo);
        } else if (v == tvCancelar) {
            // Navegar atrás al fragmento anterior
            NavController navController = Navigation.findNavController(requireView());
            navController.popBackStack();
        }
    }
}
