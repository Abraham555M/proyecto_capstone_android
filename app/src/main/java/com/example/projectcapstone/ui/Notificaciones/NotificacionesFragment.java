package com.example.projectcapstone.ui.Notificaciones;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.Notificacion;
import com.example.projectcapstone.ui.Clases.Soporte;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Configuracion.SessionManager;
import com.example.projectcapstone.ui.Notificaciones.Adapter.ActividadAdapter;
import com.example.projectcapstone.ui.Notificaciones.Adapter.SoporteAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class NotificacionesFragment extends Fragment {
    private SessionManager session;

    // 🔹 Soporte
    private RecyclerView recyclerSolicitudes;
    private SoporteAdapter soporteAdapter;
    private ArrayList<Soporte> listaSoportes;
    private LinearLayout emptyStateSolicitudes;

    // 🔹 Actividades
    private RecyclerView recyclerActividad;
    private ActividadAdapter actividadAdapter;
    private ArrayList<Notificacion> listaNotificaciones;
    private LinearLayout emptyStateActividad;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notificaciones, container, false);

        session = new SessionManager(requireContext());

        // ----- RecyclerView de Soporte -----
        recyclerSolicitudes = rootView.findViewById(R.id.recyclerSolicitudes);
        recyclerSolicitudes.setLayoutManager(new LinearLayoutManager(requireContext()));
        listaSoportes = new ArrayList<>();
        soporteAdapter = new SoporteAdapter(requireContext(), listaSoportes);
        recyclerSolicitudes.setAdapter(soporteAdapter);
        emptyStateSolicitudes = rootView.findViewById(R.id.emptyStateSolicitudes);

        // ----- RecyclerView de Actividad -----
        recyclerActividad = rootView.findViewById(R.id.recyclerActividad);
        recyclerActividad.setLayoutManager(new LinearLayoutManager(requireContext()));
        listaNotificaciones = new ArrayList<>();
        actividadAdapter = new ActividadAdapter(requireContext(), listaNotificaciones);
        recyclerActividad.setAdapter(actividadAdapter);
        emptyStateActividad = rootView.findViewById(R.id.emptyStateActividad);

        // Cargar datos
        cargarNotificacionesSoporte();
        cargarNotificacionesActividad();

        return rootView;
    }

    // 🔹 NOTIFICACIONES DE SOPORTE
    public void cargarNotificacionesSoporte() {
        int idEstudiante = session.getIdEstudiante();
        String url = ServidorConfig.URL_SERVIDOR + "soporte/soporte_listar_notificaciones.php?idEstudiante=" + idEstudiante;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONArray jsonArray = new JSONArray(response);

                    listaSoportes.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        Soporte soporte = new Soporte(
                                obj.getInt("id_soporte"),
                                obj.getInt("id_estudiante"),
                                obj.getString("men_soporte"),
                                obj.getString("fec_soporte"),
                                obj.getString("est_soporte")
                        );

                        listaSoportes.add(soporte);
                    }

                    soporteAdapter.notifyDataSetChanged();

                    // 🔸 Mostrar/ocultar mensaje vacío
                    if (listaSoportes.isEmpty()) {
                        recyclerSolicitudes.setVisibility(View.GONE);
                        emptyStateSolicitudes.setVisibility(View.VISIBLE);
                    } else {
                        recyclerSolicitudes.setVisibility(View.VISIBLE);
                        emptyStateSolicitudes.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    recyclerSolicitudes.setVisibility(View.GONE);
                    emptyStateSolicitudes.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                recyclerSolicitudes.setVisibility(View.GONE);
                emptyStateSolicitudes.setVisibility(View.VISIBLE);
            }
        });
    }

    // 🔹 NOTIFICACIONES DE ACTIVIDAD
    public void cargarNotificacionesActividad() {
        int idEstudiante = session.getIdEstudiante();
        String url = ServidorConfig.URL_SERVIDOR + "soporte/sosporte_listar_actividad.php?idEmprendedor=" + idEstudiante;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONArray jsonArray = new JSONArray(response);

                    listaNotificaciones.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        Notificacion noti = new Notificacion(
                                obj.getInt("id_notificacion"),
                                obj.getString("titulo"),
                                obj.getString("mensaje"),
                                obj.getString("fecha"),
                                obj.getInt("leida"),
                                obj.getString("tipo"),
                                obj.getString("nombre_emisor"),
                                obj.getString("correo_emisor")
                        );

                        listaNotificaciones.add(noti);
                    }

                    actividadAdapter.notifyDataSetChanged();

                    // 🔸 Mostrar/ocultar mensaje vacío
                    if (listaNotificaciones.isEmpty()) {
                        recyclerActividad.setVisibility(View.GONE);
                        emptyStateActividad.setVisibility(View.VISIBLE);
                    } else {
                        recyclerActividad.setVisibility(View.VISIBLE);
                        emptyStateActividad.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    recyclerActividad.setVisibility(View.GONE);
                    emptyStateActividad.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                recyclerActividad.setVisibility(View.GONE);
                emptyStateActividad.setVisibility(View.VISIBLE);
            }
        });
    }
}
