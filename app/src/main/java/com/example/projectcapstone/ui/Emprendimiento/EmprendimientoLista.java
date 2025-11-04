package com.example.projectcapstone.ui.Emprendimiento;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.Emprendimiento;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Configuracion.SessionManager;
import com.example.projectcapstone.ui.Emprendimiento.Adapter.EmprendimientoAdapter;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

public class EmprendimientoLista extends Fragment {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout layoutEmpty;
    EmprendimientoAdapter adapter;
    List<Emprendimiento> lista = new ArrayList<>();

    // Variables para el diálogo de edición
    private AlertDialog dialogEditar;
    private ImageView imgEditarPortada;
    private EditText etEditarNombre, etEditarDescripcion;
    private Uri nuevaImagenUri;
    private Emprendimiento emprendimientoActual;
    private ProgressDialog progressDialog;

    // Lanzador para seleccionar imagen
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    // Para el SharePreference
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_emprendimiento_lista, container, false);

        recyclerView = root.findViewById(R.id.recyclerViewEmprendimientos);
        progressBar = root.findViewById(R.id.progressBar);
        layoutEmpty = root.findViewById(R.id.layoutEmptyState);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Configurar el lanzador de selección de imagen
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        nuevaImagenUri = result.getData().getData();
                        if (imgEditarPortada != null && nuevaImagenUri != null) {
                            Picasso.get()
                                    .load(nuevaImagenUri)
                                    .into(imgEditarPortada);
                        }
                    }
                }
        );

        adapter = new EmprendimientoAdapter(getContext(), lista, new EmprendimientoAdapter.OnItemClickListener() {
            @Override
            public void onEditarClick(Emprendimiento empr) {
                mostrarDialogEditar(empr);
            }

            @Override
            public void onEliminarClick(Emprendimiento empr) {
                confirmarEliminar(empr);
            }
        });

        session = new SessionManager(requireContext());
        recyclerView.setAdapter(adapter);
        cargarEmprendimientos();

        return root;
    }

    private void mostrarDialogEditar(Emprendimiento empr) {
        emprendimientoActual = empr;
        nuevaImagenUri = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_editar_emprendimiento, null);

        imgEditarPortada = dialogView.findViewById(R.id.imgEditarPortada);
        etEditarNombre = dialogView.findViewById(R.id.etEditarNombre);
        etEditarDescripcion = dialogView.findViewById(R.id.etEditarDescripcion);

        // ✅ CAMBIO: Ahora es CardView en lugar de ImageButton
        CardView containerImagen = dialogView.findViewById(R.id.containerImagen);
        CardView btnSeleccionarImagen = dialogView.findViewById(R.id.btnSeleccionarImagen);

        // ✅ CAMBIO: Ahora buscamos el botón de cerrar y los botones como TextView
        ImageView btnCerrar = dialogView.findViewById(R.id.btn_close);
        TextView btnCancelar = dialogView.findViewById(R.id.btnCancelar);
        TextView btnGuardarCambios = dialogView.findViewById(R.id.btnGuardarCambios);

        // Cargar datos actuales
        etEditarNombre.setText(empr.getNom_emprendimiento());
        etEditarDescripcion.setText(empr.getDes_emprendimiento());

        if (empr.getImg_por_emprendimiento() != null && !empr.getImg_por_emprendimiento().isEmpty()) {
            Picasso.get()
                    .load(empr.getImg_por_emprendimiento())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(imgEditarPortada);
        }

        // ✅ CAMBIO: Listener para abrir galería (ahora con CardView)
        View.OnClickListener abrirGaleria = v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        };

        // Ambos elementos pueden abrir la galería
        containerImagen.setOnClickListener(abrirGaleria);
        btnSeleccionarImagen.setOnClickListener(abrirGaleria);

        builder.setView(dialogView);
        dialogEditar = builder.create();

        // ✅ CAMBIO: Fondo transparente para el dialog
        if (dialogEditar.getWindow() != null) {
            dialogEditar.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // ✅ CAMBIO: Botón cerrar (X)
        btnCerrar.setOnClickListener(v -> dialogEditar.dismiss());

        // Guardar cambios
        btnGuardarCambios.setOnClickListener(v -> {
            String nuevoNombre = etEditarNombre.getText().toString().trim();
            String nuevaDesc = etEditarDescripcion.getText().toString().trim();

            if (nuevoNombre.isEmpty()) {
                mostrarAlertaPersonalizada("Campo requerido", "El nombre del emprendimiento es obligatorio", false);
                return;
            }

            if (nuevaDesc.isEmpty()) {
                mostrarAlertaPersonalizada("Campo requerido", "La descripción es obligatoria", false);
                return;
            }

            // Si hay nueva imagen, subirla a Firebase primero
            if (nuevaImagenUri != null) {
                subirImagenYActualizar(nuevoNombre, nuevaDesc);
            } else {
                // Actualizar solo texto
                actualizarEmprendimiento(nuevoNombre, nuevaDesc, empr.getImg_por_emprendimiento());
            }
        });

        // Cancelar
        btnCancelar.setOnClickListener(v -> dialogEditar.dismiss());

        dialogEditar.show();
    }

    private void subirImagenYActualizar(String nombre, String descripcion) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Subiendo imagen...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Referencia a Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Crear nombre único para la imagen
        String nombreArchivo = "emprendimientos/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageRef.child(nombreArchivo);

        // Subir imagen
        imageRef.putFile(nuevaImagenUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Obtener URL de descarga
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String urlImagen = uri.toString();

                        // Eliminar imagen antigua de Firebase si existe
                        if (emprendimientoActual.getImg_por_emprendimiento() != null &&
                                !emprendimientoActual.getImg_por_emprendimiento().isEmpty() &&
                                emprendimientoActual.getImg_por_emprendimiento().contains("firebase")) {

                            StorageReference oldImageRef = storage.getReferenceFromUrl(emprendimientoActual.getImg_por_emprendimiento());
                            oldImageRef.delete();
                        }

                        progressDialog.setMessage("Actualizando datos...");
                        actualizarEmprendimiento(nombre, descripcion, urlImagen);
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Error al obtener URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage("Subiendo: " + (int) progress + "%");
                });
    }

    private void actualizarEmprendimiento(String nombre, String descripcion, String urlImagen) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Actualizando emprendimiento...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        String url = ServidorConfig.URL_SERVIDOR + "emprendimiento/emprendimiento_actualizar.php";

        RequestParams params = new RequestParams();
        params.put("id_emprendimiento", emprendimientoActual.getId_emprendimiento());
        params.put("nom_emprendimiento", nombre);
        params.put("des_emprendimiento", descripcion);
        params.put("img_por_emprendimiento", urlImagen);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.dismiss();
                try {
                    String response = new String(responseBody, "UTF-8").trim();
                    JSONObject json = new JSONObject(response);

                    String status = json.optString("status", "error");
                    String message = json.optString("message", "Operación completada");

                    if (status.equalsIgnoreCase("success")) {
                        dialogEditar.dismiss();
                        mostrarDialogExito("Actualización exitosa", message, true);
                        cargarEmprendimientos(); // Recargar lista
                    } else {
                        mostrarDialogExito("Error", message, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error de conexión: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarEliminar(Emprendimiento empr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog_opciones, null);

        TextView tvTitulo = dialogView.findViewById(R.id.tvTituloError);
        Button btnNo = dialogView.findViewById(R.id.btnNo);
        Button btnSi = dialogView.findViewById(R.id.btnSi);

        tvTitulo.setText("¿Eliminar '" + empr.getNom_emprendimiento() + "'?");

        AlertDialog dialog = builder.setView(dialogView)
                .setCancelable(true)
                .create();

        // Fondo transparente
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // Botón No - Cancelar
        btnNo.setOnClickListener(v -> dialog.dismiss());

        // Botón Sí - Eliminar
        btnSi.setOnClickListener(v -> {
            dialog.dismiss();
            eliminarEmprendimiento(empr);
        });

        dialog.show();
    }

    private void mostrarDialogExito(String titulo, String mensaje, boolean esExito) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.alert_dialog_res_positiva, null);

        TextView tvTitulo = dialogView.findViewById(R.id.tvTituloExito);
        TextView tvMensaje = dialogView.findViewById(R.id.tvMensajeExito);
        com.google.android.material.button.MaterialButton btnAceptar = dialogView.findViewById(R.id.btnFuncionalidadExito);

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

    private void eliminarEmprendimiento(Emprendimiento empr) {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Eliminando...");
        pd.show();

        String url = ServidorConfig.URL_SERVIDOR + "emprendimiento/emprendimiento_eliminar.php";

        RequestParams params = new RequestParams();
        params.put("id_emprendimiento", empr.getId_emprendimiento());

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pd.dismiss();
                try {
                    String response = new String(responseBody, "UTF-8").trim();
                    JSONObject json = new JSONObject(response);

                    if (json.optString("status").equalsIgnoreCase("success")) {
                        Toast.makeText(getContext(), "Eliminado correctamente", Toast.LENGTH_SHORT).show();

                        // Eliminar imagen de Firebase si existe
                        if (empr.getImg_por_emprendimiento() != null &&
                                empr.getImg_por_emprendimiento().contains("firebase")) {
                            FirebaseStorage.getInstance()
                                    .getReferenceFromUrl(empr.getImg_por_emprendimiento())
                                    .delete();
                        }

                        cargarEmprendimientos();
                    } else {
                        Toast.makeText(getContext(), json.optString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pd.dismiss();
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarAlertaPersonalizada(String titulo, String mensaje, boolean esPositivo) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView;

        TextView tvTitulo;
        TextView tvMensaje;
        Button btnAceptar;

        if (esPositivo) {
            dialogView = inflater.inflate(R.layout.alert_dialog_res_positiva, null);
            tvTitulo = dialogView.findViewById(R.id.tvTituloExito);
            tvMensaje = dialogView.findViewById(R.id.tvMensajeExito);
            btnAceptar = dialogView.findViewById(R.id.btnFuncionalidadExito);
        } else {
            dialogView = inflater.inflate(R.layout.alert_dialog_res_negativa, null);
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

    private void cargarEmprendimientos() {
        progressBar.setVisibility(View.VISIBLE);
        int idEstudiante = session.getIdEstudiante();

        if (idEstudiante == -1) {
            progressBar.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "No se encontró el ID del estudiante", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ServidorConfig.URL_SERVIDOR + "emprendimiento/emprendimiento_listar.php?id_estudiante=" + idEstudiante;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressBar.setVisibility(View.GONE);
                try {
                    String response = new String(responseBody, "UTF-8");
                    response = response.replace("\uFEFF", "").trim();

                    JSONArray jsonArray;
                    if (response.startsWith("{")) {
                        JSONObject json = new JSONObject(response);
                        String status = json.optString("status", "success");

                        if (!status.equalsIgnoreCase("success")) {
                            layoutEmpty.setVisibility(View.VISIBLE);
                            return;
                        }
                        jsonArray = json.optJSONArray("emprendimientos");
                        if (jsonArray == null) jsonArray = new JSONArray();
                    } else if (response.startsWith("[")) {
                        jsonArray = new JSONArray(response);
                    } else {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        return;
                    }

                    lista.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Emprendimiento empr = new Emprendimiento(
                                obj.optString("id_emprendimiento", ""),
                                obj.optString("id_estudiante", ""),
                                obj.optString("id_categoria", ""),
                                obj.optString("nom_emprendimiento", ""),
                                obj.optString("des_emprendimiento", ""),
                                obj.optString("img_por_emprendimiento", ""),
                                obj.optString("img_per_emprendimiento", ""),
                                obj.optInt("est_emprendimiento", 0)
                        );
                        lista.add(empr);
                    }

                    adapter.notifyDataSetChanged();
                    layoutEmpty.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBar.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            }
        });
    }
}