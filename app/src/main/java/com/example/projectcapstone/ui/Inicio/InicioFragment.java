package com.example.projectcapstone.ui.Inicio;

import android.os.Bundle;

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
import com.example.projectcapstone.ui.Clases.Categoria;
import com.example.projectcapstone.ui.Clases.Publicacion;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Inicio.Adapter.CategoriaAdapter;
import com.example.projectcapstone.ui.Inicio.Adapter.PublicacionAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class InicioFragment extends Fragment {
    private CategoriaAdapter categoriaAdapter;
    private PublicacionAdapter publicacionAdapter;
    private List<Categoria> listaCategoria = new ArrayList<>();
    private List<Publicacion> listaPublicacion = new ArrayList<>();
    private RecyclerView rvCategoria, rvPublicaciones;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inicio, container, false);

        rvCategoria = rootView.findViewById(R.id.rvCategoria);
        rvPublicaciones = rootView.findViewById(R.id.rvPublicaciones);

        // Configuración horizontal
        rvCategoria.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoriaAdapter = new CategoriaAdapter(getContext(), listaCategoria);
        rvCategoria.setAdapter(categoriaAdapter);

        // Configuración vertical de publicaciones
        rvPublicaciones.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        publicacionAdapter = new PublicacionAdapter(getContext(), listaPublicacion,
                (publicacion, ivLike, tvLikes) -> {
                    registrarLike(publicacion.getIdPublicacion(), 1, publicacion, ivLike, tvLikes);
                }
        );
        rvPublicaciones.setAdapter(publicacionAdapter);


        cargarCategorias();
        cargarPublicaciones(1);

        return rootView;
    }

    private void cargarCategorias() {
        String url = ServidorConfig.URL_SERVIDOR + "categoria/categoria_listar.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody, "UTF-8");
                    JSONArray jsonArray = new JSONArray(respuesta);

                    listaCategoria.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int idCategoria = obj.getInt("id_categoria");
                        String nomCategoria = obj.getString("nom_categoria");
                        String imgCategoria = obj.getString("img_categoria");

                        listaCategoria.add(new Categoria(idCategoria, nomCategoria, imgCategoria));
                    }
                    categoriaAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarPublicaciones(int idEstudiante) {
        String url = ServidorConfig.URL_SERVIDOR + "publicacion/publicacion_listar_inicio.php?idEstudiante=" + idEstudiante;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody, "UTF-8");
                    JSONArray jsonArray = new JSONArray(respuesta);

                    listaPublicacion.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int idPublicacion = obj.getInt("id_publicacion");
                        String nomEmprendimiento = obj.getString("nom_emprendimiento");
                        String imgEmprendimiento = obj.getString("img_per_emprendimiento");
                        String titPublicacion = obj.getString("tit_publicacion");
                        String conPublicacion = obj.getString("con_publicacion");
                        String imgPublicacion = obj.getString("img_publicacion");
                        Integer totalInteracciones = obj.getInt("total_me_gusta");
                        int dioLike = obj.getInt("dio_like");

                        listaPublicacion.add(new Publicacion(idPublicacion, nomEmprendimiento, imgEmprendimiento, titPublicacion, conPublicacion, imgPublicacion, totalInteracciones, dioLike));
                    }
                    publicacionAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registrarLike(Integer idPublicacion, int idEstudiante, Publicacion publicacion, ImageView ivLike, TextView tvLikes) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("idEstudiante", idEstudiante);
        params.put("idPublicacion", idPublicacion);

        String url = ServidorConfig.URL_SERVIDOR + "interaccion/interaccion_registrar_like.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody);
                    JSONObject json = new JSONObject(respuesta);

                    String status = json.getString("status");

                    if (status.equals("liked")) {
                        publicacion.setLiked(true);
                        publicacion.setTotalInteracciones(publicacion.getTotalInteracciones() + 1);
                        ivLike.setImageResource(R.drawable.ic_corazon_lleno);

                        ivLike.animate()
                                .scaleX(1.3f).scaleY(1.3f) // aumenta tamaño
                                .setDuration(150)
                                .withEndAction(() -> ivLike.animate()
                                        .scaleX(1f).scaleY(1f) // vuelve a su tamaño original
                                        .setDuration(150))
                                .start();

                    } else if (status.equals("unliked")) {
                        publicacion.setLiked(false);
                        publicacion.setTotalInteracciones(publicacion.getTotalInteracciones() - 1);
                        ivLike.setImageResource(R.drawable.ic_corazon);
                    }

                    // actualizar solo el contador
                    tvLikes.setText(publicacion.getTotalInteracciones() + " Me gusta");


                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error de parsing", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }





}