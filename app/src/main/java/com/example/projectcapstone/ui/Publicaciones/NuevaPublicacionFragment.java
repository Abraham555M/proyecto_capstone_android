package com.example.projectcapstone.ui.Publicaciones;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
import com.example.projectcapstone.ui.Publicaciones.Adapter.TipoPublicacion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

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
    private int idEmprendimientoSeleccionado = -1;

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

        Bundle args = getArguments();
        if (args != null) {
            idEmprendimientoSeleccionado = args.getInt("id_emprendimiento", -1);
        }

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
                        configurarCalendarioEvento();
                        break;
                    case "Promoción":
                        layoutPromocion.setVisibility(View.VISIBLE);
                        configurarCalendarioPromocion();
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
            agregarPublicacionFirebase();
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
    private void agregarPublicacion() {
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
        String nombreTipo = tipoSeleccionado.getNom_tipo_publicacion();

        String idEmprendimiento = String.valueOf(idEmprendimientoSeleccionado);

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
                params.put("img_publicacion", inputStream, "imagen.jpg");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error al preparar imagen", Toast.LENGTH_SHORT).show();
            }
        }

        switch (nombreTipo) {
            case "Producto":
                EditText etPrecio = layoutProducto.findViewById(R.id.etPrecioPublicacion);
                EditText etStock = layoutProducto.findViewById(R.id.etStockPublicacion);

                String precio = etPrecio.getText().toString().trim();
                String stock = etStock.getText().toString().trim();

                if (precio.isEmpty() || stock.isEmpty()) {
                    Toast.makeText(requireContext(), "Complete precio y stock", Toast.LENGTH_SHORT).show();
                    return;
                }

                params.put("prc_producto", precio);
                params.put("stk_producto", stock);
                break;

            case "Promoción":
                EditText etDescuento = layoutPromocion.findViewById(R.id.etDescuento);
                EditText etFechaIni = layoutPromocion.findViewById(R.id.etFechaInicio);
                EditText etFechaFin = layoutPromocion.findViewById(R.id.etFechaFin);

                String descuento = etDescuento.getText().toString().trim();
                String fechaIni = etFechaIni.getText().toString().trim();
                String fechaFin = etFechaFin.getText().toString().trim();

                if (descuento.isEmpty() || fechaIni.isEmpty() || fechaFin.isEmpty()) {
                    Toast.makeText(requireContext(), "Complete todos los datos de la promoción", Toast.LENGTH_SHORT).show();
                    return;
                }

                params.put("dsc_promocion", descuento);
                params.put("fch_ini_promocion", fechaIni);
                params.put("fch_fin_promocion", fechaFin);
                break;

            case "Evento":
                EditText etFechaEvento = layoutEvento.findViewById(R.id.etFechaEvento);
                EditText etLugar = layoutEvento.findViewById(R.id.etLugarEvento);

                String fechaEvento = etFechaEvento.getText().toString().trim();
                String lugarEvento = etLugar.getText().toString().trim();

                if (fechaEvento.isEmpty() || lugarEvento.isEmpty()) {
                    Toast.makeText(requireContext(), "Complete fecha y lugar del evento", Toast.LENGTH_SHORT).show();
                    return;
                }

                params.put("fch_evento", fechaEvento);
                params.put("lgr_evento", lugarEvento);
                break;
        }

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("success")) {
                        Toast.makeText(requireContext(), "Publicación agregada correctamente", Toast.LENGTH_SHORT).show();

                        etNombrePublicacion.setText("");
                        etDescripcion.setText("");
                        imgUpload.setImageResource(R.drawable.ic_buscar);
                        imageUri = null;

                        NavController navController = Navigation.findNavController(requireView());
                        navController.popBackStack();
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

    private void configurarCalendarioPromocion(){
        EditText etFechaInicio = layoutPromocion.findViewById(R.id.etFechaInicio);
        EditText etFechaFin = layoutPromocion.findViewById(R.id.etFechaFin);

        prepararCampoFecha(etFechaInicio);
        prepararCampoFecha(etFechaFin);

        etFechaInicio.setOnClickListener(v -> mostrarCalendario(etFechaInicio));
        etFechaFin.setOnClickListener(v -> mostrarCalendario(etFechaFin));
    }

    private void configurarCalendarioEvento() {
        EditText etFechaEvento = layoutEvento.findViewById(R.id.etFechaEvento);

        prepararCampoFecha(etFechaEvento);

        etFechaEvento.setOnClickListener(v -> mostrarCalendario(etFechaEvento));
    }

    private void prepararCampoFecha(EditText editText) {
        // Evita que aparezca el teclado
        editText.setInputType(0);
        editText.setFocusable(false);
        editText.setClickable(true);

        // Abre el calendario al primer toque
        editText.setOnClickListener(v -> mostrarCalendario(editText));
    }

    private void mostrarCalendario(EditText editText) {

        final Calendar calendar = Calendar.getInstance();
        int año = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String fecha = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth);
                    editText.setText(fecha);
                },
                año, mes, dia
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void limpiarCampos() {
        etNombrePublicacion.setText("");
        etDescripcion.setText("");
        imgUpload.setImageResource(R.drawable.ic_buscar);
        imageUri = null;
    }

    private void agregarPublicacionFirebase() {
        String urlPHP = ServidorConfig.URL_SERVIDOR + "publicacion/agregar_publicacion.php";

        // Validación básica
        String titulo = etNombrePublicacion.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (titulo.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese un título", Toast.LENGTH_SHORT).show();
            return;
        }

        TipoPublicacion tipoSeleccionado = (TipoPublicacion) spTipoPublicacion.getSelectedItem();
        String idTipo = tipoSeleccionado.getId_tipo_publicacion();
        String nombreTipo = tipoSeleccionado.getNom_tipo_publicacion();

        String idEmprendimiento = String.valueOf(idEmprendimientoSeleccionado);

        if (imageUri != null) {
            // 1️⃣ Subir a Firebase Storage
            String nombreImagen = "publicaciones/" + System.currentTimeMillis() + ".jpg";
            com.google.firebase.storage.StorageReference storageRef =
                    com.google.firebase.storage.FirebaseStorage.getInstance().getReference().child(nombreImagen);

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot ->
                            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String urlImagenFirebase = uri.toString();
                                enviarPublicacionPHP(urlPHP, idEmprendimiento, idTipo, titulo, descripcion,
                                        nombreTipo, urlImagenFirebase);
                            })
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Error subiendo imagen: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        } else {
            // Sin imagen
            enviarPublicacionPHP(urlPHP, idEmprendimiento, idTipo, titulo, descripcion, nombreTipo, null);
        }
    }

    private void enviarPublicacionPHP(String urlPHP, String idEmprendimiento, String idTipo,
                                      String titulo, String descripcion, String nombreTipo,
                                      @Nullable String urlImagen) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("id_emprendimiento", idEmprendimiento);
        params.put("id_tipo_publicacion", idTipo);
        params.put("tit_publicacion", titulo);
        params.put("con_publicacion", descripcion);
        params.put("est_publicacion", "1");

        if (urlImagen != null) {
            params.put("img_publicacion", urlImagen); // enviar URL de Firebase
        }

        switch (nombreTipo) {
            case "Producto":
                EditText etPrecio = layoutProducto.findViewById(R.id.etPrecioPublicacion);
                EditText etStock = layoutProducto.findViewById(R.id.etStockPublicacion);
                params.put("prc_producto", etPrecio.getText().toString().trim());
                params.put("stk_producto", etStock.getText().toString().trim());
                break;

            case "Promoción":
                EditText etDescuento = layoutPromocion.findViewById(R.id.etDescuento);
                EditText etFechaIni = layoutPromocion.findViewById(R.id.etFechaInicio);
                EditText etFechaFin = layoutPromocion.findViewById(R.id.etFechaFin);
                params.put("dsc_promocion", etDescuento.getText().toString().trim());
                params.put("fch_ini_promocion", etFechaIni.getText().toString().trim());
                params.put("fch_fin_promocion", etFechaFin.getText().toString().trim());
                break;

            case "Evento":
                EditText etFechaEvento = layoutEvento.findViewById(R.id.etFechaEvento);
                EditText etLugar = layoutEvento.findViewById(R.id.etLugarEvento);
                params.put("fch_evento", etFechaEvento.getText().toString().trim());
                params.put("lgr_evento", etLugar.getText().toString().trim());
                break;
        }

        client.post(urlPHP, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject obj = new JSONObject(response);

                    if (obj.optBoolean("success", false)) {
                        Toast.makeText(requireContext(), "Publicación agregada correctamente", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        NavController navController = Navigation.findNavController(requireView());
                        navController.popBackStack();
                    } else {
                        Toast.makeText(requireContext(), "Error: " + obj.optString("message"), Toast.LENGTH_SHORT).show();
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
}