package com.example.projectcapstone.ui.Colaboraciones;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Configuracion.SessionManager;
import com.example.projectcapstone.ui.Publicaciones.Adapter.Publicacion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ColaboracionesFragment extends Fragment {
    private RecyclerView recyclerColaboraciones;
    private ColaboracionAdapter adapter;
    private List<Colaboracion> listaColaboraciones;
    private LinearLayout emptyStateColaboraciones;
    private TextView tvColaboracionesActivas; // 🆕 NUEVA VARIABLE
    private SessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_colaboraciones, container, false);

        recyclerColaboraciones = view.findViewById(R.id.recyclerColaboraciones);
        emptyStateColaboraciones = view.findViewById(R.id.emptyStateColaboraciones);
        tvColaboracionesActivas = view.findViewById(R.id.tvColaboracionesActivas); // 🆕 INICIALIZAR
        recyclerColaboraciones.setLayoutManager(new LinearLayoutManager(getContext()));

        listaColaboraciones = new ArrayList<>();
        adapter = new ColaboracionAdapter(getContext(), listaColaboraciones);
        recyclerColaboraciones.setAdapter(adapter);
        session = new SessionManager(requireContext());

        cargarColaboracionesDesdeServidor();

        return view;
    }

    private void cargarColaboracionesDesdeServidor() {
        int idEstudiante = session.getIdEstudiante();
        String url = ServidorConfig.URL_SERVIDOR + "colaboracion/listar_colaboraciones.php?id_estudiante=" + idEstudiante;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("success")) {
                        JSONArray data = obj.getJSONArray("data");
                        listaColaboraciones.clear();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject c = data.getJSONObject(i);
                            JSONObject p = c.getJSONObject("publicacion");

                            Publicacion pub = new Publicacion(
                                    p.getInt("id"),
                                    p.getString("titulo"),
                                    p.getString("descripcion"),
                                    p.getString("imagenUrl")
                            );

                            Colaboracion colab = new Colaboracion(
                                    c.getInt("id_colaboracion"),
                                    c.getInt("id_estudiante"),
                                    c.getInt("id_emprendimiento"),
                                    c.getInt("id_publicacion"),
                                    c.getString("men_colaboracion"),
                                    c.getString("fch_colaboracion"),
                                    c.getInt("est_colaboracion"),
                                    pub
                            );

                            listaColaboraciones.add(colab);
                        }

                        adapter.notifyDataSetChanged();

                        // 🔥 CONTROLAR VISIBILIDAD DEL TÍTULO Y ESTADO VACÍO
                        if (listaColaboraciones.isEmpty()) {
                            tvColaboracionesActivas.setVisibility(View.GONE); // 🆕 OCULTAR TÍTULO
                            emptyStateColaboraciones.setVisibility(View.VISIBLE);
                            recyclerColaboraciones.setVisibility(View.GONE);
                        } else {
                            tvColaboracionesActivas.setVisibility(View.VISIBLE); // 🆕 MOSTRAR TÍTULO
                            emptyStateColaboraciones.setVisibility(View.GONE);
                            recyclerColaboraciones.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mostrarEstadoVacio();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarEstadoVacio();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mostrarEstadoVacio();
            }
        });
    }

    private void mostrarEstadoVacio() {
        tvColaboracionesActivas.setVisibility(View.GONE); // 🆕 OCULTAR TÍTULO
        recyclerColaboraciones.setVisibility(View.GONE);
        emptyStateColaboraciones.setVisibility(View.VISIBLE);
    }
}