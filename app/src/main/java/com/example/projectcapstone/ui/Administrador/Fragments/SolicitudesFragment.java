package com.example.projectcapstone.ui.Administrador.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Administrador.Adapter.Solicitud;
import com.example.projectcapstone.ui.Administrador.Adapter.SolicitudAdapter;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SolicitudesFragment extends Fragment {
    private RecyclerView rvSolicitudes;
    private List<Solicitud> lista = new ArrayList<>();
    private SolicitudAdapter adapter; // ✅ mantener referencia

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solicitudes_admin, container, false);
        rvSolicitudes = view.findViewById(R.id.recyclerSolicitudes);
        rvSolicitudes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SolicitudAdapter(lista, getContext(), this::onEstadoCambiado);
        rvSolicitudes.setAdapter(adapter);
        cargarSolicitudes();
        return view;
    }

    // ✅ Llamado cuando se cambia el estado (aceptar o rechazar)
    private void onEstadoCambiado() {
        cargarSolicitudes(); // vuelve a cargar datos desde el servidor
    }

    private void cargarSolicitudes() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ServidorConfig.URL_SERVIDOR + "administrador/listar_solicitudes.php", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                lista.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        Solicitud s = new Solicitud();
                        s.setId_soporte(obj.getInt("id_soporte"));
                        s.setNom_estudiante(obj.getString("nom_estudiante"));
                        s.setMen_soporte(obj.getString("men_soporte"));
                        s.setFec_soporte(obj.getString("fec_soporte"));
                        s.setEst_soporte(obj.getString("est_soporte"));
                        lista.add(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }
}
