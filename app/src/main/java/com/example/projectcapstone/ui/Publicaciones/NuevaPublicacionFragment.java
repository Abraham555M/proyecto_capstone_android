package com.example.projectcapstone.ui.Publicaciones;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import javax.annotation.Nullable;

import cz.msebera.android.httpclient.Header;

public class NuevaPublicacionFragment extends Fragment {

    private Spinner spTipoPublicacion;
    private EditText etNombrePublicacion, etDescripcion;
    private Button btnCrear;
    private LinearLayout layoutProducto, layoutEvento, layoutPromocion;
    private ArrayList<TipoPublicacion> listaTipos = new ArrayList<>();
    private ArrayAdapter<TipoPublicacion> adapter;
    private ImageView imgUpload;
    private Uri imageUri; // para guardar la URI de la foto seleccionada
    private ActivityResultLauncher<String> galleryLauncher; // Lanzador para galería

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nueva_publicacion, container, false);

        // Referencias a las vistas
        spTipoPublicacion = view.findViewById(R.id.spTipoPublicacion);
        layoutProducto = view.findViewById(R.id.layoutProducto);
        layoutEvento = view.findViewById(R.id.layoutEvento);
        layoutPromocion = view.findViewById(R.id.layoutPromocion);

        etNombrePublicacion = view.findViewById(R.id.etNombrePublicacion);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        btnCrear = view.findViewById(R.id.btnCrear);
        imgUpload = view.findViewById(R.id.imgUpload);

        // Adaptador vacío inicialmente
        adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, listaTipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoPublicacion.setAdapter(adapter);

        // Llamada a la API con AsyncHttpClient
        cargarTiposPublicacion();

        // Listener del Spinner
        spTipoPublicacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View itemView, int position, long id) {
                String opcion = parent.getItemAtPosition(position).toString();

                // Ocultar todos primero
                layoutProducto.setVisibility(View.GONE);
                layoutEvento.setVisibility(View.GONE);
                layoutPromocion.setVisibility(View.GONE);

                // Mostrar según selección
                switch (opcion) {
                    case "Producto":
                        layoutProducto.setVisibility(View.VISIBLE);
                        break;
                    case "Evento":
                        layoutEvento.setVisibility(View.VISIBLE);
                        break;
                    case "Promoción":
                        layoutPromocion.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nada por defecto
            }
        });

        // Evento de galería (si haces click en la imagen actual)
        imgUpload.setOnClickListener(v -> openGallery());

        btnCrear.setOnClickListener(v -> {
            agregarPublicacion();
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializamos el launcher de galería
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        Glide.with(this)
                                .load(uri)
                                .placeholder(R.drawable.ic_buscar) // mientras carga
                                .error(R.drawable.ic_error)        // si falla
                                .into(imgUpload);
                    }
                });
    }
    private void cargarTiposPublicacion() {
        String url = ServidorConfig.URL_SERVIDOR + "publicacion/listar_tipo_publicacion.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONArray jsonArray = new JSONArray(response);
                    listaTipos.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        listaTipos.add(new TipoPublicacion(
                                obj.getString("id_tipo_publicacion"),
                                obj.getString("nom_tipo_publicacion")
                        ));
                    }

                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(),
                            "Error al procesar JSON", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(requireContext(),
                        "Error de conexión: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void agregarPublicacion(){
        String url = ServidorConfig.URL_SERVIDOR + "publicacion/agregar_publicacion.php";

        // Validación básica
        String titulo = etNombrePublicacion.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (titulo.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese un título", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener tipo seleccionado
        TipoPublicacion tipoSeleccionado = (TipoPublicacion) spTipoPublicacion.getSelectedItem();
        String idTipo = tipoSeleccionado.getId_tipo_publicacion();

        String idEmprendimiento = "4"; // Como lo haria esto de aqui ?? - dudoso

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id_emprendimiento", idEmprendimiento);
        params.put("id_tipo_publicacion", idTipo);
        params.put("tit_publicacion", titulo);
        params.put("con_publicacion", descripcion);
        params.put("est_publicacion", "1");

        // 👉 enviar imagen si se seleccionó
        if (imageUri != null) {
            try {
                InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
                params.put("img_publicacion", inputStream, "imagen.jpg");  // clave "img_publicacion"
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error al preparar imagen", Toast.LENGTH_SHORT).show();
            }
        }

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("success")) {
                        Toast.makeText(requireContext(), "Publicación agregada", Toast.LENGTH_SHORT).show();
                        // opcional: limpiar campos
                        etNombrePublicacion.setText("");
                        etDescripcion.setText("");
                    } else {
                        Toast.makeText(requireContext(), "Error: " + obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Error procesando respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(requireContext(),
                        "Error de conexión: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openGallery() {
        galleryLauncher.launch("image/*");
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = { android.provider.MediaStore.Images.Media.DATA };
        android.database.Cursor cursor = requireActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }
}