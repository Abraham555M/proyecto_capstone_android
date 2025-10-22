package com.example.projectcapstone.ui.Publicaciones;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Configuracion.SessionManager;
import com.example.projectcapstone.ui.Publicaciones.Adapter.CategoriaPublicacion;
import com.example.projectcapstone.ui.Publicaciones.Adapter.CategoriaPublicacionAdapter;
import com.example.projectcapstone.ui.Publicaciones.Adapter.Publicacion;
import com.example.projectcapstone.ui.Publicaciones.Adapter.PublicacionAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PublicacionesFragment extends Fragment implements View.OnClickListener{

    private RecyclerView recyclerView;
    private RecyclerView recyclerEmprendimientos;
    public PublicacionAdapter adapter;
    private CategoriaPublicacionAdapter emprendimientoAdapter;
    private List<Publicacion> publicaciones;
    private List<CategoriaPublicacion> categorias;
    private Button btnAgregarPublicacion;
    private SessionManager session;
    private TextView tvSinPublicaciones, tvSinEmprendimientos;
    private int idEmprendimientoSeleccionado = -1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_publicaciones, container, false);

        tvSinPublicaciones = rootView.findViewById(R.id.txtSinPublicaciones);
        tvSinEmprendimientos = rootView.findViewById(R.id.txtSinEmprendimientos);
        idEmprendimientoSeleccionado = -1;
        recyclerEmprendimientos = rootView.findViewById(R.id.recyclerEmprendimientos);
        recyclerView = rootView.findViewById(R.id.recyclerPublicaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        btnAgregarPublicacion = rootView.findViewById(R.id.btnAgregarPublicacion);

        // --- Configurar Recycler de Emprendimientos (Horizontal) ---
        recyclerEmprendimientos.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        categorias = new ArrayList<>();
        emprendimientoAdapter = new CategoriaPublicacionAdapter(
                categorias,getContext(),
                categoria -> {
                    int idCategoria = categoria.getIdCategoria();
                    idEmprendimientoSeleccionado = categoria.getIdEmprendimiento();
                    // Toast.makeText(getContext(), "Filtrando por: " + categoria.getNombre(), Toast.LENGTH_SHORT).show();
                    cargarPublicacionesPorCategoria(idCategoria, idEmprendimientoSeleccionado);
        });
        recyclerEmprendimientos.setAdapter(emprendimientoAdapter);
        session = new SessionManager(requireContext());

        publicaciones = new ArrayList<>();
        adapter = new PublicacionAdapter(publicaciones, getContext(), new PublicacionAdapter.OnPublicacionActualizadaListener() {
            @Override
            public void onPublicacionesActualizadas() {
                cargarPublicaciones();
            }
        });
        recyclerView.setAdapter(adapter);
        btnAgregarPublicacion.setOnClickListener(this);

        cargarEmprendimientos();
        cargarPublicaciones();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == btnAgregarPublicacion) {
            if (idEmprendimientoSeleccionado == -1) {
                Toast.makeText(getContext(), "Por favor, selecciona una categoría antes de agregar una publicación.", Toast.LENGTH_SHORT).show();
            } else {
                // 🔹 Guardamos el id del emprendimiento seleccionado antes de navegar
                Bundle bundle = new Bundle();
                bundle.putInt("id_emprendimiento", idEmprendimientoSeleccionado);

                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_nav_publicaciones_to_nuevaPublicacionFragment, bundle);
            }
        }
    }

    private void cargarPublicaciones() {
        String URL = ServidorConfig.URL_SERVIDOR + "publicacion/listar_publicaciones.php";
        AsyncHttpClient client = new AsyncHttpClient();

        SharedPreferences prefs = requireActivity().getSharedPreferences("usuario", Context.MODE_PRIVATE);
        int idEstudiante = prefs.getInt("id_estudiante", -1);

        RequestParams params = new RequestParams();
        params.put("idEstudiante", idEstudiante);

        client.get(URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONArray array = new JSONArray(response);

                    publicaciones.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        publicaciones.add(new Publicacion(
                                obj.getInt("id"),
                                obj.getString("titulo"),
                                obj.getString("descripcion"),
                                obj.getString("imagen_url")
                        ));
                    }

                    adapter.notifyDataSetChanged();

                    // Mostrar u ocultar mensaje
                    if (publicaciones.isEmpty()) {
                        tvSinPublicaciones.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvSinPublicaciones.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                tvSinPublicaciones.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }
    private void cargarEmprendimientos() {
        int idEstudiante = session.getIdEstudiante();
        String URL = ServidorConfig.URL_SERVIDOR + "publicacion/listar_categorias_publicacion.php?id_estudiante=" + idEstudiante;
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONArray array = new JSONArray(response);

                    categorias.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String imagen = obj.getString("img_categoria");
                        if (!imagen.startsWith("http")) {
                            imagen = ServidorConfig.URL_FOTOS_SERVIDOR + imagen;
                        }

                        categorias.add(new CategoriaPublicacion(
                                obj.getInt("id_categoria"),
                                obj.getInt("id_emprendimiento"),
                                obj.getString("nom_categoria"),
                                imagen
                        ));
                    }
                    emprendimientoAdapter.notifyDataSetChanged();

                    // ✅ Controlar visibilidad y posición
                    TextView txtSinEmprendimientos = getView().findViewById(R.id.txtSinEmprendimientos);
                    RecyclerView recyclerEmprendimientos = getView().findViewById(R.id.recyclerEmprendimientos);
                    TextView tvMisPublicaciones = getView().findViewById(R.id.tvMisPublicaciones);

                    if (categorias.isEmpty()) {
                        recyclerEmprendimientos.setVisibility(View.GONE);
                        txtSinEmprendimientos.setVisibility(View.VISIBLE);
                    } else {
                        recyclerEmprendimientos.setVisibility(View.VISIBLE);
                        txtSinEmprendimientos.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error procesando categorías", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                tvSinEmprendimientos.setVisibility(View.VISIBLE);
                recyclerEmprendimientos.setVisibility(View.GONE);
            }
        });
    }
    private void cargarPublicacionesPorCategoria(int idCategoria, int idEmprendimiento) {
        String URL = ServidorConfig.URL_SERVIDOR + "publicacion/listar_publicaciones_categoria.php";
        AsyncHttpClient client = new AsyncHttpClient();

        SharedPreferences prefs = requireActivity().getSharedPreferences("usuario", Context.MODE_PRIVATE);
        int idEstudiante = prefs.getInt("id_estudiante", -1);

        RequestParams params = new RequestParams();
        params.put("id_estudiante", idEstudiante);
        params.put("id_categoria", idCategoria);
        params.put("id_emprendimiento", idEmprendimiento);

        client.get(URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONArray array = new JSONArray(response);

                    publicaciones.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String imagen = obj.getString("imagen_url");
                        if (!imagen.startsWith("http")) {
                            imagen = ServidorConfig.URL_FOTOS_SERVIDOR + imagen;
                        }

                        publicaciones.add(new Publicacion(
                                obj.getInt("id"),
                                obj.getString("titulo"),
                                obj.getString("descripcion"),
                                imagen
                        ));
                    }

                    adapter.notifyDataSetChanged();

                    // Mostrar u ocultar mensaje
                    if (publicaciones.isEmpty()) {
                        tvSinPublicaciones.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvSinPublicaciones.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    tvSinPublicaciones.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                tvSinPublicaciones.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }
}