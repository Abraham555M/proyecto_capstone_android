package com.example.projectcapstone.ui.Autenticacion.CrearCuenta;

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
import android.widget.Toast;

import com.example.projectcapstone.R;
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

public class CrearCuenta extends Fragment {

    private TextInputEditText edtNombres, edtApellidoPaterno, edtApellidoMaterno, edtCorreo, edtContra;
    private EditText etNumeroCelular;
    private AutoCompleteTextView actvSexo, actvSede;
    private Button btnListo, btnCancelar;
    private Map<String, Integer> sexoMap = new HashMap<>();
    private Map<String, Integer> sedeMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_crear_cuenta, container, false);

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

        cargarSexo();
        // Configurar opciones para Sexo
        /*String[] opcionesSexo = {"Masculino", "Femenino", "Otro"};
        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                opcionesSexo
        );
        actvSexo.setAdapter(adapterSexo);*/

        cargarSedes();

        // Acción botón "Listo"
        btnListo.setOnClickListener(v -> {
            if (validarCampos()) {
                crearCuenta();
            }
        });

        // Acción botón "Cancelar"
        btnCancelar.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_nav_crear_cuenta_to_nav_start_upn);
        });

        return rootView;
    }

    private void cargarSexo() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = ServidorConfig.URL_SERVIDOR + "estudiante/obtener_sexo.php"; // cambia por tu URL

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

                    ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            nombresSexo
                    );
                    actvSexo.setAdapter(adapterSexo);

                    actvSexo.setOnItemClickListener((parent, view, position, id) -> {
                        String seleccionado = parent.getItemAtPosition(position).toString();
                        int idSexo = sexoMap.get(seleccionado);
                        // Aquí puedes guardar idSede en una variable o enviarlo a tu formulario
                        Log.d("SEXO", "Seleccionaste: " + seleccionado + " con id: " + idSexo);
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Error al procesar datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(requireContext(), "Error al cargar sexo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para validar campos
    private boolean validarCampos() {
        if (edtNombres.getText().toString().trim().isEmpty()) {
            edtNombres.setError("Ingrese su nombre");
            return false;
        }

        if (edtApellidoPaterno.getText().toString().trim().isEmpty()) {
            edtApellidoPaterno.setError("Ingrese su apellido paterno");
            return false;
        }

        String correo = edtCorreo.getText().toString().trim();
        String regex = "^[A-Za-z0-9._%+-]+@upn\\.pe$";

        if (correo.isEmpty()) {
            edtCorreo.setError("Ingrese su correo");
            return false;
        } else if (!correo.matches(regex)) {
            edtCorreo.setError("Ingrese un correo válido con dominio @upn.pe");
            return false;
        }
        if (edtContra.getText().toString().trim().isEmpty()) {
            edtContra.setError("Ingrese su contraseña");
            return false;
        }
        if (actvSexo.getText().toString().trim().isEmpty()) {
            actvSexo.setError("Seleccione su sexo");
            return false;
        }
        if (actvSede.getText().toString().trim().isEmpty()) {
            actvSede.setError("Seleccione su sede");
            return false;
        }
        return true;
    }

    private void crearCuenta() {
        String nombres = edtNombres.getText().toString().trim();
        String apePat = edtApellidoPaterno.getText().toString().trim();
        String apeMat = edtApellidoMaterno.getText().toString().trim();
        String correo = edtCorreo.getText().toString().trim();
        String contraseña = edtContra.getText().toString().trim();
        String celular = etNumeroCelular.getText().toString().trim();
        String sexoTexto = actvSexo.getText().toString().trim();
        String sedeTexto = actvSede.getText().toString().trim();

        int sexo = sexoMap.getOrDefault(sexoTexto, -1);
        int sede = sedeMap.getOrDefault(sedeTexto, -1);

        if (!validarCampos()) {
            return;
        }

        // URL de tu backend (PHP o API)
        String url = ServidorConfig.URL_SERVIDOR + "estudiante/crear_cuenta.php";

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("nombres", nombres);
        params.put("apePat", apePat);
        params.put("apeMat", apeMat);
        params.put("correo", correo);
        params.put("contrasena", contraseña);
        params.put("celular", celular);
        params.put("sexo", sexo);
        params.put("sede", sede);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                Toast.makeText(requireContext(), "Respuesta: " + respuesta, Toast.LENGTH_LONG).show();

                // 👉 Si el backend responde con "ok", navega al validar correo
                if (respuesta.contains("ok")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("correo", correo);
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.action_nav_crear_cuenta_to_nav_validar_correo_crear, bundle);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(requireContext(), "No se pudo CrearCuenta", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargarSedes() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = ServidorConfig.URL_SERVIDOR + "estudiante/obtener_sedes.php"; // cambia por tu URL

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

                    ArrayAdapter<String> adapterSede = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            nombresSede
                    );
                    actvSede.setAdapter(adapterSede);

                    // Guardar id_sede al seleccionar
                    actvSede.setOnItemClickListener((parent, view, position, id) -> {
                        String seleccionado = parent.getItemAtPosition(position).toString();
                        int idSede = sedeMap.get(seleccionado);
                        // Aquí puedes guardar idSede en una variable o enviarlo a tu formulario
                        Log.d("SEDE", "Seleccionaste: " + seleccionado + " con id: " + idSede);
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Error al procesar datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(requireContext(), "Error al cargar sedes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}