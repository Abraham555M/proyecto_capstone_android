package com.example.projectcapstone.ui.Autenticacion.CrearCuenta;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.projectcapstone.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrearCuenta extends Fragment implements View.OnClickListener {
    private TextInputEditText edtNombres, edtApellidoPaterno, edtApellidoMaterno, edtCorreo, edtContra;
    private EditText etNumeroCelular;
    private AutoCompleteTextView actvSexo, actvSede;
    private Button btnListo, btnCancelar;
    private FrameLayout loaderContainer;

    private Map<String, Integer> sexoMap = new HashMap<>();
    private Map<String, Integer> sedeMap = new HashMap<>();

    // Variables temporales para enviar al siguiente fragmento
    private String nombres, apePat, apeMat, correo, contrasena, celular, sexo, sede;
    private int idSexo, idSede;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_crear_cuenta, container, false);

        edtNombres = rootView.findViewById(R.id.edtNombres);
        edtApellidoPaterno = rootView.findViewById(R.id.edtApellidoPaterno);
        edtApellidoMaterno = rootView.findViewById(R.id.edtApellidoMaterno);
        edtCorreo = rootView.findViewById(R.id.edtCorreo);
        edtContra = rootView.findViewById(R.id.edtContrasena);
        etNumeroCelular = rootView.findViewById(R.id.etNumeroCelular);
        actvSexo = rootView.findViewById(R.id.actvSexo);
        actvSede = rootView.findViewById(R.id.actvSede);
        btnListo = rootView.findViewById(R.id.btnListo);
        btnCancelar = rootView.findViewById(R.id.btnCancelar);
        loaderContainer = rootView.findViewById(R.id.loaderContainer);

        cargarSexo();
        cargarSedes();

        btnListo.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);

        return rootView;
    }

    private boolean validarCampos() {
        nombres = edtNombres.getText().toString().trim();
        apePat = edtApellidoPaterno.getText().toString().trim();
        apeMat = edtApellidoMaterno.getText().toString().trim();
        correo = edtCorreo.getText().toString().trim();
        contrasena = edtContra.getText().toString().trim();
        celular = etNumeroCelular.getText().toString().trim();
        sexo = actvSexo.getText().toString().trim();
        sede = actvSede.getText().toString().trim();

        // Validaciones básicas
        if (nombres.isEmpty()) {
            edtNombres.setError("Ingrese sus nombres");
            return false;
        }
        if (apePat.isEmpty()) {
            edtApellidoPaterno.setError("Ingrese su apellido paterno");
            return false;
        }
        if (apeMat.isEmpty()) {
            edtApellidoMaterno.setError("Ingrese su apellido materno");
            return false;
        }

        // Validación de correo institucional
        if (correo.isEmpty()) {
            edtCorreo.setError("Ingrese su correo");
            return false;
        }
        if (!correo.endsWith("@upn.pe")) {
            edtCorreo.setError("Debe usar el correo institucional (@upn.pe)");
            return false;
        }

        // Validación de celular
        if (celular.isEmpty()) {
            etNumeroCelular.setError("Ingrese su número de celular");
            return false;
        }
        if (!celular.matches("^9\\d{8}$")) {
            etNumeroCelular.setError("Número de celular inválido (debe tener 9 dígitos y empezar con 9)");
            return false;
        }

        // Validación de contraseña
        if (contrasena.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese su contraseña", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (contrasena.length() < 6) {
            Toast.makeText(requireContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$";
        if (!contrasena.matches(passwordPattern)) {
            Toast.makeText(requireContext(), "La contraseña debe tener 1 mayúscula, 1 número y 1 carácter especial", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar sexo
        if (sexo.isEmpty()) {
            Toast.makeText(requireContext(), "Seleccione su sexo", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar sede
        if (sede.isEmpty()) {
            Toast.makeText(requireContext(), "Seleccione su sede", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar existencia de IDs en los mapas
        if (!sexoMap.containsKey(sexo) || !sedeMap.containsKey(sede)) {
            Toast.makeText(requireContext(), "Selecciona valores válidos", Toast.LENGTH_SHORT).show();
            return false;
        }

        idSexo = sexoMap.get(sexo);
        idSede = sedeMap.get(sede);

        return true;
    }

    private void procesarRegistro() {
        if (!validarCampos()) {
            return;
        }
        verificarCorreoAntesDeEnviarCodigo();
    }

    private void verificarCorreoAntesDeEnviarCodigo() {
        mostrarLoader();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("correo", correo);

        String url = ServidorConfig.URL_SERVIDOR + "estudiante/verificar_correo.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ocultarLoader();
                try {
                    String respuesta = new String(responseBody);
                    JSONObject json = new JSONObject(respuesta);

                    boolean success = json.getBoolean("success");
                    String mensaje = json.getString("mensaje");

                    if (success) {
                        // Correo disponible → Enviar código
                        enviarCodigoVerificacion();
                    } else {
                        // Correo ya existe → Mostrar alerta
                        mostrarAlertaCorreoExistente(mensaje);
                    }

                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Error procesando respuesta", Toast.LENGTH_SHORT).show();
                    Log.e("CREAR_CUENTA", "Error parseando JSON: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ocultarLoader();
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e("CREAR_CUENTA", "Fallo conexión: " + error.getMessage());
            }
        });
    }

    private void mostrarAlertaCorreoExistente(String mensaje) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View vistaDialogo = inflater.inflate(R.layout.alert_dialog_res_negativa, null);

        // Referencias a los elementos de la vista
        TextView tvTitulo = vistaDialogo.findViewById(R.id.tvTituloError);
        TextView tvMensaje = vistaDialogo.findViewById(R.id.tvMensajeError);
        MaterialButton btnOk = vistaDialogo.findViewById(R.id.btnFuncionalidadError);

        // Personalizar contenido dinámico
        tvTitulo.setText("Correo ya registrado");
        tvMensaje.setText("\nSi olvidaste tu contraseña, puedes recuperarla.");

        // Crear el AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(vistaDialogo)
                .setCancelable(false)
                .create();

        // Botón de acción
        btnOk.setOnClickListener(v -> dialog.dismiss());

        // Mostrar con fondo transparente para respetar bordes redondeados
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }

    private void enviarCodigoVerificacion() {
        mostrarLoader();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("correo", correo);
        params.put("nombres", nombres + " " + apePat + " " + apeMat);

        String url = ServidorConfig.URL_SERVIDOR + "estudiante/enviar_codigo.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ocultarLoader();
                String respuesta = new String(responseBody);

                try {
                    JSONObject json = new JSONObject(respuesta);
                    String status = json.getString("status");

                    if (status.equals("ok")) {
                        Toast.makeText(requireContext(), "Código enviado a tu correo", Toast.LENGTH_SHORT).show();

                        Bundle bundle = new Bundle();
                        bundle.putString("nombres", nombres);
                        bundle.putString("apePat", apePat);
                        bundle.putString("apeMat", apeMat);
                        bundle.putString("correo", correo);
                        bundle.putString("contrasena", contrasena);
                        bundle.putString("celular", celular);
                        bundle.putInt("id_sexo", idSexo);
                        bundle.putInt("id_sede", idSede);

                        NavController navController = Navigation.findNavController(requireView());
                        navController.navigate(R.id.action_nav_crear_cuenta_to_nav_validar_correo_crear, bundle);

                    } else {
                        String msg = json.optString("msg", "Error al enviar el código");
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Error inesperado en la respuesta", Toast.LENGTH_SHORT).show();
                    Log.e("CREAR_CUENTA", "Error parseando respuesta: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ocultarLoader();
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e("CREAR_CUENTA", "Fallo conexión: " + error.getMessage());
            }
        });
    }

    private void cargarSexo() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = ServidorConfig.URL_SERVIDOR + "estudiante/obtener_sexo.php";

        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<String> nombresSexo = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject sexo = response.getJSONObject(i);
                        int id = sexo.getInt("id_sexo");
                        String nombre = sexo.getString("nom_sexo");
                        nombresSexo.add(nombre);
                        sexoMap.put(nombre, id);
                    }
                    ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_dropdown_item_1line, nombresSexo);
                    actvSexo.setAdapter(adapterSexo);

                } catch (JSONException e) {
                    Toast.makeText(requireContext(), "Error al procesar datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(requireContext(), "Error al cargar sexo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarSedes() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = ServidorConfig.URL_SERVIDOR + "estudiante/obtener_sedes.php";

        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<String> nombresSede = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject sede = response.getJSONObject(i);
                        int id = sede.getInt("id_sede");
                        String nombre = sede.getString("nom_sede");
                        nombresSede.add(nombre);
                        sedeMap.put(nombre, id);
                    }
                    ArrayAdapter<String> adapterSede = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_dropdown_item_1line, nombresSede);
                    actvSede.setAdapter(adapterSede);

                } catch (JSONException e) {
                    Toast.makeText(requireContext(), "Error al procesar datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(requireContext(), "Error al cargar sedes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarLoader() {
        if (loaderContainer != null) loaderContainer.setVisibility(View.VISIBLE);
    }

    private void ocultarLoader() {
        if (loaderContainer != null) loaderContainer.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        if (v == btnListo) {
            procesarRegistro();
        }
        if(v == btnCancelar){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_crear_cuenta_to_nav_start_upn);
        }
    }
}
