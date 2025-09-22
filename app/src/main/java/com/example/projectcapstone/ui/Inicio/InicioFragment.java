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
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Inicio.Adapter.CategoriaAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InicioFragment extends Fragment {
    private CategoriaAdapter categoriaAdapter;
    private List<Categoria> listaCategoria = new ArrayList<>();
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

        cargarCategorias();



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
                        int id_categoria = obj.getInt("id_categoria");
                        String nom_categoria = obj.getString("nom_categoria");
                        String img_categoria = obj.getString("img_categoria");

                        listaCategoria.add(new Categoria(id_categoria, nom_categoria, img_categoria));
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
}