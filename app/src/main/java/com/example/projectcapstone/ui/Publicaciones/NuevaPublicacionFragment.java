package com.example.projectcapstone.ui.Publicaciones;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.annotation.Nullable;

import cz.msebera.android.httpclient.Header;

public class NuevaPublicacionFragment extends Fragment {

    private Spinner spTipoPublicacion;
    private LinearLayout layoutProducto, layoutEvento, layoutPromocion;

    private ArrayList<TipoPublicacion> listaTipos = new ArrayList<>();
    private ArrayAdapter<TipoPublicacion> adapter;

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

        return view;
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
}