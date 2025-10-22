package com.example.projectcapstone.ui.Metricas;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.Publicacion;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Configuracion.SessionManager;
import com.example.projectcapstone.ui.Metricas.Adapter.PublicacionDestacadaAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import android.app.DatePickerDialog;
import android.widget.Button;
import android.widget.EditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


import cz.msebera.android.httpclient.Header;

public class MetricasFragment extends Fragment {

    private TextView tvPromocionesActivas, tvTotalFavoritos, tvInteraccionesTotales, tvPromedioComentarios;
    private RecyclerView rvDestacadas;
    private LineChart chartCrecimiento;
    private PublicacionDestacadaAdapter adapter;
    private ArrayList<Publicacion> listaDestacadas = new ArrayList<>();
    private SessionManager session;
    private EditText etFechaInicio, etFechaFin;
    private Button btnFiltrar;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_metricas, container, false);

        session = new SessionManager(getContext());

        // 🔹 Vincular vistas
        tvPromocionesActivas = rootView.findViewById(R.id.tvPromocionesActivas);
        tvTotalFavoritos = rootView.findViewById(R.id.tvTotalFavoritos);
        tvInteraccionesTotales = rootView.findViewById(R.id.tvInteraccionesTotales);
        tvPromedioComentarios = rootView.findViewById(R.id.tvPromedioComentarios);
        rvDestacadas = rootView.findViewById(R.id.rvPublicacionesDestacadas);
        chartCrecimiento = rootView.findViewById(R.id.chartCrecimiento);
        etFechaInicio = rootView.findViewById(R.id.etFechaInicio);
        etFechaFin = rootView.findViewById(R.id.etFechaFin);
        btnFiltrar = rootView.findViewById(R.id.btnFiltrarMetricas);

        Calendar calendar = Calendar.getInstance();

        // Selector fecha inicio
        etFechaInicio.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                    (view, y, m, d) -> etFechaInicio.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", y, (m + 1), d)),
                    year, month, day);
            datePicker.show();
        });

        // Selector fecha fin
        etFechaFin.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                    (view, y, m, d) -> etFechaFin.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", y, (m + 1), d)),
                    year, month, day);
            datePicker.show();
        });

        // Botón Filtrar
        btnFiltrar.setOnClickListener(v -> {
            String inicio = etFechaInicio.getText().toString();
            String fin = etFechaFin.getText().toString();

            if (inicio.isEmpty() || fin.isEmpty()) {
                Toast.makeText(getContext(), "Seleccione ambas fechas", Toast.LENGTH_SHORT).show();
                return;
            }

            cargarMetricasFiltradas(inicio, fin);
        });


        // 🔹 Configurar RecyclerView
        rvDestacadas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PublicacionDestacadaAdapter(listaDestacadas);
        rvDestacadas.setAdapter(adapter);
        // 🔹 Cargar métricas
        cargarMetricas();

        return rootView;
    }

    private void cargarMetricas() {
        int idEstudiante = session.getIdEstudiante();
        String url = ServidorConfig.URL_SERVIDOR + "metricas/metricas_emprendedor.php?idEstudiante=" + idEstudiante;

        Log.d("METRICAS_URL", "➡️ URL usada: " + url);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody, "UTF-8");
                    Log.d("METRICAS_RESPUESTA", "📦 " + respuesta);

                    JSONObject json = new JSONObject(respuesta);
                    if (json.getString("status").equals("success")) {
                        JSONObject data = json.getJSONObject("data");

                        // ✅ Asignar datos a las métricas principales
                        tvPromocionesActivas.setText(String.valueOf(data.getInt("promociones_activas")));
                        tvTotalFavoritos.setText(String.valueOf(data.getInt("total_favoritos")));
                        tvInteraccionesTotales.setText(String.valueOf(data.getInt("interacciones_totales")));
                        tvPromedioComentarios.setText(String.valueOf(data.getDouble("promedio_comentarios")));

                        // ✅ Cargar publicaciones destacadas
                        JSONArray publicacionesArray = data.getJSONArray("publicaciones_destacadas");
                        listaDestacadas.clear();

                        for (int i = 0; i < publicacionesArray.length(); i++) {
                            JSONObject p = publicacionesArray.getJSONObject(i);
                            listaDestacadas.add(new Publicacion(
                                    p.getInt("id_publicacion"),
                                    0,
                                    p.optString("nom_emprendimiento", ""),
                                    "",
                                    p.getString("tit_publicacion"),
                                    "",
                                    p.optString("img_publicacion", ""),
                                    p.getInt("total_interacciones")
                            ));
                        }
                        adapter.notifyDataSetChanged();

                        // ✅ Gráfico de crecimiento mensual
                        JSONArray crecimientoArray = data.getJSONArray("crecimiento_mensual");
                        configurarGrafico(crecimientoArray);

                    } else {
                        Toast.makeText(getContext(), "⚠️ No se pudieron obtener las métricas", Toast.LENGTH_SHORT).show();
                        Log.e("METRICAS_ERROR", json.optString("message"));
                    }

                } catch (Exception e) {
                    Log.e("METRICAS_EXCEPTION", "❌ " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Error procesando datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
                Log.e("METRICAS_FAIL", "❌ status: " + statusCode + " - " + error.getMessage());
            }
        });
    }

    private void configurarGrafico(JSONArray crecimientoArray) {
        try {
            List<Entry> entries = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            for (int i = 0; i < crecimientoArray.length(); i++) {
                JSONObject obj = crecimientoArray.getJSONObject(i);
                labels.add(obj.getString("mes"));
                entries.add(new Entry(i, (float) obj.getInt("total")));
            }

            LineDataSet dataSet = new LineDataSet(entries, "Seguidores por mes");
            dataSet.setColor(Color.parseColor("#2196F3"));
            dataSet.setValueTextSize(10f);
            dataSet.setCircleRadius(4f);
            dataSet.setLineWidth(2f);

            LineData lineData = new LineData(dataSet);
            chartCrecimiento.setData(lineData);

            Description description = new Description();
            description.setText("");
            chartCrecimiento.setDescription(description);
            chartCrecimiento.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void cargarMetricasFiltradas(String fechaInicio, String fechaFin) {
        int idEstudiante = session.getIdEstudiante();
        String url = ServidorConfig.URL_SERVIDOR +
                "metricas/metricas_emprendedor.php?idEstudiante=" + idEstudiante +
                "&fecha_inicio=" + fechaInicio +
                "&fecha_fin=" + fechaFin;

        Log.d("METRICAS_URL_FILTRO", "➡️ URL usada: " + url);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody, "UTF-8");
                    Log.d("METRICAS_FILTRO_RESPUESTA", respuesta);

                    JSONObject json = new JSONObject(respuesta);
                    if (json.getString("status").equals("success")) {
                        JSONObject data = json.getJSONObject("data");

                        // Reutiliza tu mismo proceso para actualizar
                        tvPromocionesActivas.setText(String.valueOf(data.getInt("promociones_activas")));
                        tvTotalFavoritos.setText(String.valueOf(data.getInt("total_favoritos")));
                        tvInteraccionesTotales.setText(String.valueOf(data.getInt("interacciones_totales")));
                        tvPromedioComentarios.setText(String.valueOf(data.getDouble("promedio_comentarios")));

                        JSONArray publicacionesArray = data.getJSONArray("publicaciones_destacadas");
                        listaDestacadas.clear();

                        for (int i = 0; i < publicacionesArray.length(); i++) {
                            JSONObject p = publicacionesArray.getJSONObject(i);
                            listaDestacadas.add(new Publicacion(
                                    p.getInt("id_publicacion"),
                                    0,
                                    p.optString("nom_emprendimiento", ""),
                                    "",
                                    p.getString("tit_publicacion"),
                                    "",
                                    p.optString("img_publicacion", ""),
                                    p.getInt("total_interacciones")
                            ));
                        }
                        adapter.notifyDataSetChanged();

                        JSONArray crecimientoArray = data.getJSONArray("crecimiento_mensual");
                        configurarGrafico(crecimientoArray);
                    } else {
                        Toast.makeText(getContext(), "Sin datos para ese rango", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("METRICAS_FILTRO_EXC", e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
