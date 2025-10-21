package com.example.projectcapstone.ui.Notificaciones;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    // 🔹 Actividades
    private RecyclerView recyclerActividad;
    private ActividadAdapter actividadAdapter;
    private ArrayList<Notificacion> listaNotificaciones;

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

        // ----- RecyclerView de Actividad -----
        recyclerActividad = rootView.findViewById(R.id.recyclerActividad);
        recyclerActividad.setLayoutManager(new LinearLayoutManager(requireContext()));
        listaNotificaciones = new ArrayList<>();
        actividadAdapter = new ActividadAdapter(requireContext(), listaNotificaciones);
        recyclerActividad.setAdapter(actividadAdapter);

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

                    if (listaSoportes.isEmpty()) {
                        Toast.makeText(getContext(), "No tienes solicitudes de soporte registradas.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar datos del servidor.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al conectar con el servidor.", Toast.LENGTH_SHORT).show();
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

                        // Se crea el objeto usando tu clase Notificacion
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

                    if (listaNotificaciones.isEmpty()) {
                        Toast.makeText(getContext(), "No tienes notificaciones de actividad.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar las notificaciones.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al conectar con el servidor.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
