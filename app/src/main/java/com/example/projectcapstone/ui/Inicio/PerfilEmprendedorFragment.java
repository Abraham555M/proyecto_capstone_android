package com.example.projectcapstone.ui.Inicio;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.ColaboracionPerfil;
import com.example.projectcapstone.ui.Clases.EmprendimientoPerfil;
import com.example.projectcapstone.ui.Clases.PublicacionPerfil;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Perfil.Adapter.ColaboradoresPerfilAdapter;
import com.example.projectcapstone.ui.Perfil.Adapter.EmprendimientoPerfilAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PerfilEmprendedorFragment extends Fragment implements View.OnClickListener {
    private TextView tvCantidadPublicaciones, tvCantidadSeguidores, tvNombre, tvSede, tvTelefono;
    private int idEstudianteEmprendedor = -1; // ID del estudiante emprendedor
    private EmprendimientoPerfilAdapter emprendimientoAdapter;
    private List<EmprendimientoPerfil> listaEmprendimientos;
    private RecyclerView rvEmprendimientosPerfil;
    private AlertDialog dialogColaboraciones;
    private ImageView ivColaboraciones;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_perfil_emprendedor, container, false);

        tvCantidadPublicaciones = rootView.findViewById(R.id.tvCantidadPublicaciones);
        tvCantidadSeguidores = rootView.findViewById(R.id.tvCantidadSeguidores);
        rvEmprendimientosPerfil = rootView.findViewById(R.id.rvEmprendimientosPerfil);

        tvNombre = rootView.findViewById(R.id.tvNombre);
        tvSede = rootView.findViewById(R.id.tvSede);
        tvTelefono = rootView.findViewById(R.id.tvTelefono);
        ivColaboraciones = rootView.findViewById(R.id.ivColaboraciones);

        Bundle args = getArguments();
        if (args != null) {
            int idEmprendimiento = args.getInt("idEmprendimiento", -1);

            if (idEmprendimiento != -1) {
                obtenerDatosEmprendedor(idEmprendimiento);
            }
        }
        ivColaboraciones.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        listaEmprendimientos = new ArrayList<>();
        emprendimientoAdapter = new EmprendimientoPerfilAdapter(listaEmprendimientos);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvEmprendimientosPerfil.setLayoutManager(layoutManager);
        rvEmprendimientosPerfil.setAdapter(emprendimientoAdapter);
    }

    private void obtenerDatosEmprendedor(int IdEstudianteEmprendedor) {
        String url = ServidorConfig.URL_SERVIDOR + "emprendimiento/emprendimiento_obtener_estudiante.php?idEmprendimiento=" + IdEstudianteEmprendedor;

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody, "UTF-8");
                    JSONObject json = new JSONObject(respuesta);

                    String nombreCompleto = json.getString("nombre_completo");
                    String sede = json.getString("nom_sede");
                    String telefono = json.getString("tel_estudiante");

                    idEstudianteEmprendedor = json.getInt("id_estudiante");

                    tvNombre.setText(nombreCompleto);
                    tvSede.setText(sede);
                    tvTelefono.setText(telefono);

                    cargarCantidadPublicaciones(idEstudianteEmprendedor);
                    cargarEmprendimientosConPublicaciones(idEstudianteEmprendedor);
                    cargarCantidadSeguidores(idEstudianteEmprendedor);

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarCantidadSeguidores(int idEstudiante) {
        String url = ServidorConfig.URL_SERVIDOR + "seguimiento/seguimiento_listar_perfil.php?idEmprendedor=" + idEstudiante;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody, "UTF-8");
                    JSONObject jsonObject = new JSONObject(respuesta);

                    int total = jsonObject.getInt("total_seguidores");
                    tvCantidadSeguidores.setText(String.valueOf(total));

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarColaboracionesDesdeBackend(int idEstudiante) {
        String url = ServidorConfig.URL_SERVIDOR + "colaboracion/colaboracion_listar_perfil.php?idEmprendedor=" + idEstudiante;

        // Log para verificar la URL
        android.util.Log.d("ColaboracionesDebug", "URL: " + url);

        // Mostrar diálogo vacío mientras carga
        mostrarDialogoColaboraciones(new ArrayList<>(), true);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody, "UTF-8");

                    // Log para debug
                    android.util.Log.d("ColaboracionesDebug", "Respuesta: " + respuesta);

                    List<ColaboracionPerfil> listaColaboraciones = new ArrayList<>();

                    // Verificar si la respuesta es un array válido
                    if (respuesta != null && !respuesta.trim().isEmpty() && respuesta.trim().startsWith("[")) {
                        JSONArray jsonArray = new JSONArray(respuesta);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            // Crear objeto ColaboracionPerfil con los campos correctos del JSON
                            ColaboracionPerfil colaboracion = new ColaboracionPerfil();
                            colaboracion.setIdColaboracion(obj.getInt("id_colaboracion"));
                            colaboracion.setNomEstudiante(obj.getString("nom_estudiante"));
                            colaboracion.setApePatEstudiante(obj.getString("ape_pat_estudiante"));
                            colaboracion.setApeMatEstudiante(obj.getString("ape_mat_estudiante"));
                            colaboracion.setEmaEstudiante(obj.getString("ema_estudiante"));
                            colaboracion.setFchColaboracion(obj.getString("fch_colaboracion"));
                            colaboracion.setEstColaboracion(obj.getInt("est_colaboracion"));
                            colaboracion.setEstadoTexto(obj.getString("estado_texto"));

                            listaColaboraciones.add(colaboracion);
                        }
                    }

                    // Mostrar los datos en el diálogo (aunque esté vacío)
                    mostrarDialogoColaboraciones(listaColaboraciones, false);

                } catch (Exception e) {
                    e.printStackTrace();
                    android.util.Log.e("ColaboracionesError", "Error: " + e.getMessage());
                    // Mostrar diálogo vacío en caso de error
                    mostrarDialogoColaboraciones(new ArrayList<>(), false);
                    Toast.makeText(getContext(), "Error al procesar colaboraciones", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                android.util.Log.e("ColaboracionesError", "Status: " + statusCode + ", Error: " + error.getMessage());
                // Cerrar el diálogo de carga y mostrar toast
                if (dialogColaboraciones != null && dialogColaboraciones.isShowing()) {
                    dialogColaboraciones.dismiss();
                }
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoColaboraciones(List<ColaboracionPerfil> listaColaboraciones, boolean cargando) {
        Context context = requireContext();

        // Si ya existe un diálogo abierto, lo cerramos para evitar duplicados
        if (dialogColaboraciones != null && dialogColaboraciones.isShowing()) {
            dialogColaboraciones.dismiss();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_colaboradores_perfil, null);
        builder.setView(view);

        RecyclerView rvColaboradores = view.findViewById(R.id.rvColaboradores);
        LinearLayout layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        LinearLayout layoutLoading = view.findViewById(R.id.layoutLoading);
        TextView tvTotalColaboradores = view.findViewById(R.id.tvTotalColaboradores);
        ImageButton btnCerrar = view.findViewById(R.id.btnCerrar);

        rvColaboradores.setLayoutManager(new LinearLayoutManager(context));

        // Configurar el adapter
        ColaboradoresPerfilAdapter adapter = new ColaboradoresPerfilAdapter(context, listaColaboraciones);
        rvColaboradores.setAdapter(adapter);

        // Mostrar estado de carga o contenido
        if (cargando) {
            layoutLoading.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
            rvColaboradores.setVisibility(View.GONE);
        } else {
            layoutLoading.setVisibility(View.GONE);

            if (listaColaboraciones.isEmpty()) {
                layoutEmptyState.setVisibility(View.VISIBLE);
                rvColaboradores.setVisibility(View.GONE);
                tvTotalColaboradores.setText("(0)");
            } else {
                layoutEmptyState.setVisibility(View.GONE);
                rvColaboradores.setVisibility(View.VISIBLE);
                tvTotalColaboradores.setText("(" + listaColaboraciones.size() + ")");
            }
        }

        btnCerrar.setOnClickListener(v -> dialogColaboraciones.dismiss());

        dialogColaboraciones = builder.create();
        dialogColaboraciones.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogColaboraciones.show();
    }

    private void cargarEmprendimientosConPublicaciones(int IdEstudianteEmprendedor) {
        String url = ServidorConfig.URL_SERVIDOR + "emprendimiento/emprendimiento_listar_perfil.php?idEstudiante=" + IdEstudianteEmprendedor;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody, "UTF-8");
                    JSONArray jsonArray = new JSONArray(respuesta);

                    listaEmprendimientos.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonEmprendimiento = jsonArray.getJSONObject(i);

                        // Datos del emprendimiento
                        String idEmprendimiento = jsonEmprendimiento.getString("id_emprendimiento");
                        String nombreEmprendimiento = jsonEmprendimiento.getString("nom_emprendimiento");
                        String imagenEmprendimiento = jsonEmprendimiento.optString("img_per_emprendimiento", "");

                        // Lista de publicaciones del emprendimiento
                        JSONArray jsonPublicaciones = jsonEmprendimiento.getJSONArray("publicaciones");
                        List<PublicacionPerfil> publicaciones = new ArrayList<>();

                        for (int j = 0; j < jsonPublicaciones.length(); j++) {
                            JSONObject jsonPublicacion = jsonPublicaciones.getJSONObject(j);

                            String idPublicacion = jsonPublicacion.getString("id_publicacion");
                            String titulo = jsonPublicacion.getString("tit_publicacion");
                            String contenido = jsonPublicacion.getString("con_publicacion");
                            String imagenUrl = jsonPublicacion.getString("img_publicacion");
                            String fecha = jsonPublicacion.getString("fch_publicacion");

                            // Construir URL completa de la imagen
                            PublicacionPerfil publicacion = new PublicacionPerfil(
                                    idPublicacion,
                                    imagenUrl,
                                    titulo
                            );
                            publicaciones.add(publicacion);
                        }

                        // Crear objeto Emprendimiento
                        EmprendimientoPerfil emprendimiento = new EmprendimientoPerfil(
                                idEmprendimiento,
                                nombreEmprendimiento,
                                publicaciones
                        );
                        listaEmprendimientos.add(emprendimiento);
                    }

                    // Notificar al adapter que los datos han cambiado
                    emprendimientoAdapter.notifyDataSetChanged();

                    // Mensaje si no hay emprendimientos
                    if (listaEmprendimientos.isEmpty()) {
                        rvEmprendimientosPerfil.setVisibility(View.GONE);
                    } else {
                        rvEmprendimientosPerfil.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar emprendimientos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al cargar emprendimientos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarCantidadPublicaciones(int idEstudianteEmprendedor) {
        String url = ServidorConfig.URL_SERVIDOR + "publicacion/publicacion_cantidad_publicaciones.php?idEstudiante=" + idEstudianteEmprendedor;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody, "UTF-8");
                    JSONObject jsonObject = new JSONObject(respuesta);

                    int total = jsonObject.getInt("total_publicaciones");
                    tvCantidadPublicaciones.setText(String.valueOf(total));

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == ivColaboraciones) {
            cargarColaboracionesDesdeBackend(idEstudianteEmprendedor);
        }
    }
}