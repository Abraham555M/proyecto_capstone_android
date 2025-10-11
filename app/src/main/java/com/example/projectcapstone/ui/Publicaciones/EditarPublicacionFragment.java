package com.example.projectcapstone.ui.Publicaciones;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.support.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class EditarPublicacionFragment extends Fragment {

    private Spinner spTipoPublicacion;
    private EditText etNombrePublicacion, etDescripcion;
    private Button btnActualizar;
    private LinearLayout layoutProductoE, layoutEventoE, layoutPromocionE;
    private ArrayList<TipoPublicacion> listaTipos = new ArrayList<>();
    private ArrayAdapter<TipoPublicacion> adapter;
    private ImageView imgUpload;
    private Uri imageUri;
    private ActivityResultLauncher<String> galleryLauncher;
    private int idPublicacion;
    private String tipoSeleccionadoNombre, urlImagenActual;
    private String imagenUrl;
    private EditText etTitulo;
    private ImageView ivImagen;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_publicacion, container, false);

        if (getArguments() != null) {
            idPublicacion = getArguments().getInt("id_publicacion");
        }

        spTipoPublicacion = view.findViewById(R.id.spTipoPublicacionE);
        layoutProductoE = view.findViewById(R.id.layoutProductoE);
        layoutEventoE = view.findViewById(R.id.layoutEventoE);
        layoutPromocionE = view.findViewById(R.id.layoutPromocionE);

        etNombrePublicacion = view.findViewById(R.id.etNombrePublicacionE);
        etDescripcion = view.findViewById(R.id.etDescripcionE);
        btnActualizar = view.findViewById(R.id.btnCrearE);
        imgUpload = view.findViewById(R.id.imgUploadE);

        spTipoPublicacion.setEnabled(false);

        adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, listaTipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoPublicacion.setAdapter(adapter);

        cargarTiposPublicacion(() -> {
            cargarDatosPublicacion();
        });

        spTipoPublicacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View itemView, int position, long id) {
                String opcion = parent.getItemAtPosition(position).toString();
                mostrarLayoutPorTipo(opcion);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        imgUpload.setOnClickListener(v -> openGallery());

        btnActualizar.setOnClickListener(v -> actualizarPublicacion());


        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        Glide.with(this).load(uri)
                                .placeholder(R.drawable.ic_buscar)
                                .error(R.drawable.ic_error)
                                .into(imgUpload);
                    }
                });
    }

    private void cargarTiposPublicacion(Runnable onComplete) {
        String url = ServidorConfig.URL_SERVIDOR + "publicacion/listar_tipo_publicacion.php";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                    listaTipos.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        listaTipos.add(new TipoPublicacion(
                                obj.getString("id_tipo_publicacion"),
                                obj.getString("nom_tipo_publicacion")
                        ));
                    }
                    adapter.notifyDataSetChanged();
                    onComplete.run();
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Error procesando tipos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int s, Header[] h, byte[] b, Throwable e) {
                Toast.makeText(requireContext(), "Error al cargar tipos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void seleccionarTipoYMostrarLayout() {
        for (int i = 0; i < listaTipos.size(); i++) {
            if (listaTipos.get(i).getNom_tipo_publicacion().equalsIgnoreCase(tipoSeleccionadoNombre)) {
                spTipoPublicacion.setSelection(i);
                mostrarLayoutPorTipo(tipoSeleccionadoNombre);
                break;
            }
        }
    }

    private void mostrarLayoutPorTipo(String opcion) {
        layoutProductoE.setVisibility(View.GONE);
        layoutEventoE.setVisibility(View.GONE);
        layoutPromocionE.setVisibility(View.GONE);

        switch (opcion) {
            case "Producto":
                layoutProductoE.setVisibility(View.VISIBLE);
                break;
            case "Evento":
                layoutEventoE.setVisibility(View.VISIBLE);
                configurarCalendarioEvento();
                break;
            case "Promoción":
                layoutPromocionE.setVisibility(View.VISIBLE);
                configurarCalendarioPromocion();
                break;
        }
    }

    private void openGallery() {
        galleryLauncher.launch("image/*");
    }

    private void configurarCalendarioPromocion() {
        EditText etInicio = layoutPromocionE.findViewById(R.id.etFechaInicioE);
        EditText etFin = layoutPromocionE.findViewById(R.id.etFechaFinE);
        prepararCampoFecha(etInicio);
        prepararCampoFecha(etFin);
    }

    private void configurarCalendarioEvento() {
        EditText etFechaEvento = layoutEventoE.findViewById(R.id.etFechaEventoE);
        prepararCampoFecha(etFechaEvento);
    }

    private void prepararCampoFecha(EditText et) {
        et.setInputType(0);
        et.setFocusable(false);
        et.setOnClickListener(v -> mostrarCalendario(et));
    }

    private void mostrarCalendario(EditText et) {
        final Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(requireContext(),
                (view, year, month, day) ->
                        et.setText(year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day)),
                y, m, d);
        dp.show();
    }

    private void actualizarPublicacion() {
        String urlPHP = ServidorConfig.URL_SERVIDOR + "publicacion/editar_publicacion.php";

        String titulo = etNombrePublicacion.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (titulo.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese un título", Toast.LENGTH_SHORT).show();
            return;
        }

        TipoPublicacion tipoSeleccionado = (TipoPublicacion) spTipoPublicacion.getSelectedItem();
        String idTipo = tipoSeleccionado.getId_tipo_publicacion();
        String nombreTipo = tipoSeleccionado.getNom_tipo_publicacion();

        if (imageUri != null) {
            // Subir nueva imagen a Firebase
            String nombreImagen = "publicaciones/" + System.currentTimeMillis() + ".jpg";
            com.google.firebase.storage.StorageReference storageRef =
                    com.google.firebase.storage.FirebaseStorage.getInstance().getReference().child(nombreImagen);

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot ->
                            storageRef.getDownloadUrl().addOnSuccessListener(uri ->
                                    enviarDatosActualizar(urlPHP, idTipo, titulo, descripcion, nombreTipo, uri.toString())
                            ))
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Error subiendo imagen", Toast.LENGTH_SHORT).show());
        } else {
            enviarDatosActualizar(urlPHP, idTipo, titulo, descripcion, nombreTipo, urlImagenActual);
        }
    }

    private void enviarDatosActualizar(String urlPHP, String idTipo, String titulo,
                                       String descripcion, String nombreTipo, @Nullable String urlImagen) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("id_publicacion", idPublicacion);
        params.put("id_tipo_publicacion", idTipo);
        params.put("tit_publicacion", titulo);
        params.put("con_publicacion", descripcion);
        params.put("img_publicacion", urlImagen);

        switch (nombreTipo) {
            case "Producto":
                EditText etPrecio = layoutProductoE.findViewById(R.id.etPrecioPublicacionE);
                EditText etStock = layoutProductoE.findViewById(R.id.etStockPublicacionE);
                params.put("prc_producto", etPrecio.getText().toString().trim());
                params.put("stk_producto", etStock.getText().toString().trim());
                break;

            case "Promoción":
                EditText etDesc = layoutPromocionE.findViewById(R.id.etDescuentoE);
                EditText etIni = layoutPromocionE.findViewById(R.id.etFechaInicioE);
                EditText etFin = layoutPromocionE.findViewById(R.id.etFechaFinE);
                params.put("dsc_promocion", etDesc.getText().toString().trim());
                params.put("fch_ini_promocion", etIni.getText().toString().trim());
                params.put("fch_fin_promocion", etFin.getText().toString().trim());
                break;

            case "Evento":
                EditText etFecha = layoutEventoE.findViewById(R.id.etFechaEventoE);
                EditText etLugar = layoutEventoE.findViewById(R.id.etLugarEventoE);
                params.put("fch_evento", etFecha.getText().toString().trim());
                params.put("lgr_evento", etLugar.getText().toString().trim());
                break;
        }

        client.post(urlPHP, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int s, Header[] h, byte[] b) {
                try {
                    JSONObject obj = new JSONObject(new String(b));
                    if (obj.optBoolean("success", false)) {
                        Toast.makeText(requireContext(), "Publicación actualizada", Toast.LENGTH_SHORT).show();
                        NavController nav = Navigation.findNavController(requireView());
                        nav.popBackStack();
                    } else {
                        Toast.makeText(requireContext(), "Error: " + obj.optString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Error procesando respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int s, Header[] h, byte[] b, Throwable e) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDatosPublicacion() {
        String url = ServidorConfig.URL_SERVIDOR + "publicacion/obtener_publicacion.php?id_publicacion=" + idPublicacion;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if (json.getString("status").equals("success")) {
                        JSONObject data = json.getJSONObject("data");
                        JSONObject publicacion = data.getJSONObject("publicacion");

                        // 🟢 Datos principales
                        String titulo = publicacion.getString("tit_publicacion");
                        String descripcion = publicacion.getString("con_publicacion");
                        urlImagenActual = publicacion.getString("img_publicacion");
                        tipoSeleccionadoNombre = publicacion.getString("nom_tipo_publicacion");

                        etNombrePublicacion.setText(titulo);
                        etDescripcion.setText(descripcion);

                        Glide.with(requireContext())
                                .load(urlImagenActual)
                                .placeholder(R.drawable.ic_buscar)
                                .error(R.drawable.ic_error)
                                .into(imgUpload);

                        // 🟢 Datos de detalle (si existen)
                        JSONObject detalles = data.optJSONObject("detalles");
                        if (detalles != null) {
                            switch (tipoSeleccionadoNombre) {
                                case "Promoción":
                                    layoutPromocionE.setVisibility(View.VISIBLE);
                                    EditText etDescuento = layoutPromocionE.findViewById(R.id.etDescuentoE);
                                    EditText etFechaInicio = layoutPromocionE.findViewById(R.id.etFechaInicioE);
                                    EditText etFechaFin = layoutPromocionE.findViewById(R.id.etFechaFinE);

                                    etDescuento.setText(detalles.optString("dsc_promocion", ""));
                                    etFechaInicio.setText(detalles.optString("fch_ini_promocion", ""));
                                    etFechaFin.setText(detalles.optString("fch_fin_promocion", ""));
                                    break;

                                case "Producto":
                                    layoutProductoE.setVisibility(View.VISIBLE);
                                    EditText etPrecio = layoutProductoE.findViewById(R.id.etPrecioPublicacionE);
                                    EditText etStock = layoutProductoE.findViewById(R.id.etStockPublicacionE);

                                    etPrecio.setText(detalles.optString("prc_producto", ""));
                                    etStock.setText(detalles.optString("stk_producto", ""));
                                    break;

                                case "Evento":
                                    layoutEventoE.setVisibility(View.VISIBLE);
                                    EditText etFechaEvento = layoutEventoE.findViewById(R.id.etFechaEventoE);
                                    EditText etLugarEvento = layoutEventoE.findViewById(R.id.etLugarEventoE);

                                    etFechaEvento.setText(detalles.optString("fch_evento", ""));
                                    etLugarEvento.setText(detalles.optString("lgr_evento", ""));
                                    break;
                            }
                        }

                        // 🟢 Seleccionar tipo en el spinner
                        seleccionarTipoYMostrarLayout();

                    } else {
                        Toast.makeText(requireContext(), "No se encontró la publicación", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Error procesando datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(requireContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}