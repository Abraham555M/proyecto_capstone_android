package com.example.projectcapstone.ui.Inicio;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.Categoria;
import com.example.projectcapstone.ui.Clases.Publicacion;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Inicio.Adapter.CategoriaAdapter;
import com.example.projectcapstone.ui.Inicio.Adapter.PublicacionAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        publicacionAdapter = new PublicacionAdapter(getContext(), listaPublicacion);
        rvPublicaciones.setAdapter(publicacionAdapter);

        cargarCategorias();
        cargarPublicaciones();

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

    private void cargarPublicaciones() {
        String url = ServidorConfig.URL_SERVIDOR + "publicacion/publicacion_listar_inicio.php";

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

                        listaPublicacion.add(new Publicacion(idPublicacion, nomEmprendimiento, imgEmprendimiento, titPublicacion, conPublicacion, imgPublicacion, totalInteracciones));
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
}