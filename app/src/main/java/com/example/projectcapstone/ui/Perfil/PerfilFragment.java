package com.example.projectcapstone.ui.Perfil;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.EmprendimientoPerfil;
import com.example.projectcapstone.ui.Clases.PublicacionPerfil;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Perfil.Adapter.EmprendimientoPerfilAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PerfilFragment extends Fragment {
    private TextView tvCantidadPublicaciones, tvCantidadSeguidores, tvCantidadSeguidos;
    private RecyclerView rvEmprendimientosPerfil;
    private EmprendimientoPerfilAdapter emprendimientoAdapter;
    private List<EmprendimientoPerfil> listaEmprendimientos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Inicializar vistas
        tvCantidadPublicaciones = rootView.findViewById(R.id.tvCantidadPublicaciones);
        tvCantidadSeguidores = rootView.findViewById(R.id.tvCantidadSeguidores);
        tvCantidadSeguidos = rootView.findViewById(R.id.tvCantidadSeguidos);
        rvEmprendimientosPerfil = rootView.findViewById(R.id.rvEmprendimientosPerfil);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        cargarCantidadPublicaciones();
        cargarEmprendimientosConPublicaciones();
    }

    private void setupRecyclerView() {
        listaEmprendimientos = new ArrayList<>();
        emprendimientoAdapter = new EmprendimientoPerfilAdapter(listaEmprendimientos);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvEmprendimientosPerfil.setLayoutManager(layoutManager);
        rvEmprendimientosPerfil.setAdapter(emprendimientoAdapter);
    }

    private void cargarCantidadPublicaciones() {
        int idEstudiante = 1;
        String url = ServidorConfig.URL_SERVIDOR + "publicacion/publicacion_cantidad_publicaciones.php?idEstudiante=" + idEstudiante;

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

    private void cargarEmprendimientosConPublicaciones() {
        int idEstudiante = 1;
        String url = ServidorConfig.URL_SERVIDOR + "emprendimiento/emprendimiento_listar_perfil.php?idEstudiante=" + idEstudiante;

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
                        Toast.makeText(getContext(), "No tienes emprendimientos aún", Toast.LENGTH_SHORT).show();
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
}