package com.example.projectcapstone.ui.Administrador.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Administrador.Adapter.ReporteAdapter;
import com.example.projectcapstone.ui.Administrador.Adapter.ReporteModel;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.example.projectcapstone.R;

import cz.msebera.android.httpclient.Header;

public class ReportesFragment extends Fragment {

    private RecyclerView recyclerReportes;
    private ReporteAdapter adapter;
    private List<ReporteModel> listaReportes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reportes_admin, container, false);

        recyclerReportes = root.findViewById(R.id.recyclerReportes);
        recyclerReportes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReporteAdapter(listaReportes, (view, reporte) -> mostrarOpcionesReporte(view, reporte));
        recyclerReportes.setAdapter(adapter);

        cargarReportes();

        return root;
    }

    private void cargarReportes() {
        String url = ServidorConfig.URL_SERVIDOR + "reporte/listar_reportes.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody, StandardCharsets.UTF_8);
                    JSONArray jsonArray = new JSONArray(respuesta);
                    listaReportes.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        ReporteModel reporte = new ReporteModel(
                                obj.getInt("id_reporte"),
                                obj.getString("usuario_reporta"),
                                obj.getString("motivo"),
                                obj.getString("titulo"),
                                obj.getString("contenido"),
                                obj.getString("fecha"),
                                obj.getString("estado")
                        );
                        listaReportes.add(reporte);
                    }

                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error procesando reportes", Toast.LENGTH_SHORT).show();
                    Log.e("REPORTES", e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al cargar reportes", Toast.LENGTH_SHORT).show();
                Log.e("HTTP_ERROR", error.getMessage());
            }
        });
    }

    private void mostrarOpcionesReporte(View anchorView, ReporteModel reporte) {
        androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(requireContext(), anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.menu_reporte, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.action_eliminar) {
                actualizarEstado(reporte.getIdReporte(), "eliminado");
                return true;
            } else {
                return false;
            }
        });

        popupMenu.show();
    }

    private void actualizarEstado(int idReporte, String nuevoEstado) {
        String url = ServidorConfig.URL_SERVIDOR + "reporte/actualizar_estado_reporte.php?idReporte=" + idReporte + "&estado=" + nuevoEstado;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getContext(), "Reporte actualizado a " + nuevoEstado, Toast.LENGTH_SHORT).show();
                cargarReportes(); // Refrescar lista
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al actualizar estado", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
