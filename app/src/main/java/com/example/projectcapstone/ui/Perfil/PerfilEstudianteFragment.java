package com.example.projectcapstone.ui.Perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Configuracion.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import cz.msebera.android.httpclient.Header;

public class PerfilEstudianteFragment extends Fragment {

    private TextInputEditText etNombresEd, etApellidoPaEd, etApellidoMaEd, etCorreoEd;
    private AutoCompleteTextView spSexoEd, spSedeEd;
    private Map<String, Integer> sexoMap = new HashMap<>();
    private Map<String, Integer> sedeMap = new HashMap<>();
    private EditText etTelefonoEd;
    private MaterialButton btnListo;
    private FrameLayout loaderContainer;
    private SessionManager session;
    private AsyncHttpClient client;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil_estudiante, container, false);

        etNombresEd = view.findViewById(R.id.etNombresEd);
        etApellidoPaEd = view.findViewById(R.id.etApellidoPaEd);
        etApellidoMaEd = view.findViewById(R.id.etApellidoMaEd);
        etCorreoEd = view.findViewById(R.id.etCorreoEd);
        spSexoEd = view.findViewById(R.id.spSexoEd);
        spSedeEd = view.findViewById(R.id.spSedeEd);
        etTelefonoEd = view.findViewById(R.id.etTelefonoEd);
        btnListo = view.findViewById(R.id.btnListo);
        loaderContainer = view.findViewById(R.id.loaderContainer);

        session = new SessionManager(requireContext());
        client = new AsyncHttpClient();

        cargarDatosIniciales();

        btnListo.setOnClickListener(v -> actualizarPerfil());

        return view;
    }

    private void cargarDatosIniciales() {
        mostrarLoader(true);

        int idEstudiante = session.getIdEstudiante();
        String url = ServidorConfig.URL_SERVIDOR + "estudiante/estudiante_listar_perfil.php?idEstudiante=" + idEstudiante;

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mostrarLoader(false);
                try {
                    String respuesta = new String(responseBody, StandardCharsets.UTF_8);
                    JSONObject json = new JSONObject(respuesta);

                    etNombresEd.setText(json.optString("nom_estudiante", ""));
                    etApellidoPaEd.setText(json.optString("ape_pat_estudiante", ""));
                    etApellidoMaEd.setText(json.optString("ape_mat_estudiante", ""));
                    etCorreoEd.setText(json.optString("ema_estudiante", ""));
                    etTelefonoEd.setText(json.optString("tel_estudiante", ""));
                    spSexoEd.setText(json.optString("sexo", ""));
                    spSedeEd.setText(json.optString("sede", ""));
                    etCorreoEd.setEnabled(false);
                    etCorreoEd.setFocusable(false);
                    etCorreoEd.setClickable(false);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar datos del perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mostrarLoader(false);
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });

        cargarSedes();
        cargarSexo();
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
                    spSexoEd.setAdapter(adapterSexo);

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
                    spSedeEd.setAdapter(adapterSede);

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

    private void actualizarPerfil() {
        String telefono = etTelefonoEd.getText().toString().trim();

        // ✅ Validación: solo números y exactamente 9 dígitos
        if (!telefono.matches("\\d{9}")) {
            mostrarDialogoError("Número de teléfono inválido", "El número debe tener exactamente 9 dígitos y solo números.");
            return;
        }

        mostrarLoader(true);

        int idEstudiante = session.getIdEstudiante();
        String url = ServidorConfig.URL_SERVIDOR + "estudiante/estudiante_actualizar_perfil.php";

        RequestParams params = new RequestParams();
        params.put("id_estudiante", idEstudiante);
        params.put("nom_estudiante", etNombresEd.getText().toString().trim());
        params.put("ape_pat_estudiante", etApellidoPaEd.getText().toString().trim());
        params.put("ape_mat_estudiante", etApellidoMaEd.getText().toString().trim());
        params.put("ema_estudiante", etCorreoEd.getText().toString().trim());
        params.put("tel_estudiante", etTelefonoEd.getText().toString().trim());
        params.put("sexo", spSexoEd.getText().toString().trim());
        params.put("sede", spSedeEd.getText().toString().trim());

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mostrarLoader(false);
                try {
                    String respuesta = new String(responseBody, StandardCharsets.UTF_8);
                    JSONObject json = new JSONObject(respuesta);

                    if (json.optBoolean("success", false)) {
                        Toast.makeText(getContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                        NavController navController = NavHostFragment.findNavController(PerfilEstudianteFragment.this);
                        navController.navigate(R.id.nav_perfil);
                    } else {
                        Toast.makeText(getContext(), "No se pudo actualizar el perfil", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mostrarLoader(false);
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoError(String titulo, String mensaje) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.alert_dialog_res_negativa, null);

        TextView tvTitulo = view.findViewById(R.id.tvTituloError);
        TextView tvMensaje = view.findViewById(R.id.tvMensajeError);
        MaterialButton btnOk = view.findViewById(R.id.btnFuncionalidadError);

        tvTitulo.setText(titulo);
        tvMensaje.setText(mensaje);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setCancelable(false)
                .create();

        btnOk.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void mostrarLoader(boolean mostrar) {
        loaderContainer.setVisibility(mostrar ? View.VISIBLE : View.GONE);
    }
}
