package com.example.projectcapstone.ui.Administrador.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class DashboardFragment extends Fragment {

    private LineChart chartLine;
    private BarChart chartBar;
    private PieChart chartPie;
    private Button btnFiltrar;
    private String fechaInicio = "";
    private String fechaFin = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_admin, container, false);

        chartLine = view.findViewById(R.id.chartLine);
        chartBar = view.findViewById(R.id.chartBar);
        chartPie = view.findViewById(R.id.chartPie);
        btnFiltrar = view.findViewById(R.id.btnFiltrar);

        obtenerDatosDashboard("", "");

        // Filtrado por fecha
        btnFiltrar.setOnClickListener(v -> mostrarSelectorRangoFechas());

        return view;
    }

    // --- Selecciona dos fechas: inicio y fin ---
    private void mostrarSelectorRangoFechas() {
        Calendar calendario = Calendar.getInstance();

        // Primer DatePicker: Fecha de inicio
        DatePickerDialog inicioPicker = new DatePickerDialog(requireContext(),
                (view, year, month, day) -> {
                    fechaInicio = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);

                    // Segundo DatePicker: Fecha fin
                    DatePickerDialog finPicker = new DatePickerDialog(requireContext(),
                            (view2, year2, month2, day2) -> {
                                fechaFin = String.format(Locale.getDefault(), "%04d-%02d-%02d", year2, month2 + 1, day2);
                                obtenerDatosDashboard(fechaInicio, fechaFin);
                            },
                            calendario.get(Calendar.YEAR),
                            calendario.get(Calendar.MONTH),
                            calendario.get(Calendar.DAY_OF_MONTH)
                    );
                    finPicker.setTitle("Selecciona fecha fin");
                    finPicker.show();
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
        );

        inicioPicker.setTitle("Selecciona fecha inicio");
        inicioPicker.show();
    }

    private void obtenerDatosDashboard(String inicio, String fin) {
        String url = ServidorConfig.URL_SERVIDOR + "administrador/estadisticas_dashboard.php";;

        AsyncHttpClient client = new AsyncHttpClient();
        String query = "?fecha_inicio=" + inicio + "&fecha_fin=" + fin;
        String fullUrl = url + query;

        client.get(fullUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                Toast.makeText(getContext(), "Cargando datos...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String responseStr = new String(responseBody);
                    JSONObject json = new JSONObject(responseStr);

                    int publicaciones = json.getInt("publicaciones_activas");
                    int colabAceptadas = json.getInt("colaboraciones_aceptadas");
                    int colabPendientes = json.getInt("colaboraciones_pendientes");
                    int reportes = json.getInt("reportes_pendientes");

                    mostrarGraficos(publicaciones, colabAceptadas, colabPendientes, reportes);

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error al procesar datos", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarGraficos(int publicaciones, int colabAceptadas, int colabPendientes, int reportes) {
        mostrarGraficoLineas(publicaciones, colabAceptadas, colabPendientes, reportes);
        mostrarGraficoBarras(publicaciones, colabAceptadas, colabPendientes, reportes);
        mostrarGraficoTorta(publicaciones, colabAceptadas, colabPendientes, reportes);
    }

    private void mostrarGraficoLineas(int publicaciones, int colabAceptadas, int colabPendientes, int reportes) {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, publicaciones));
        entries.add(new Entry(2, colabAceptadas));
        entries.add(new Entry(3, colabPendientes));
        entries.add(new Entry(4, reportes));

        LineDataSet dataSet = new LineDataSet(entries, "Actividad Reciente");
        dataSet.setColor(ColorTemplate.rgb("#1976D2"));
        dataSet.setCircleColor(ColorTemplate.rgb("#1976D2"));
        dataSet.setValueTextSize(10f);

        chartLine.setData(new LineData(dataSet));
        chartLine.getDescription().setText("Tendencia de Actividad");
        chartLine.animateX(1500);
    }

    private void mostrarGraficoBarras(int publicaciones, int colabAceptadas, int colabPendientes, int reportes) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1, publicaciones));
        entries.add(new BarEntry(2, colabAceptadas));
        entries.add(new BarEntry(3, colabPendientes));
        entries.add(new BarEntry(4, reportes));

        BarDataSet dataSet = new BarDataSet(entries, "Estadísticas Generales");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(10f);

        chartBar.setData(new BarData(dataSet));
        chartBar.getDescription().setText("Totales actuales");
        chartBar.animateY(1500);
    }

    private void mostrarGraficoTorta(int publicaciones, int colabAceptadas, int colabPendientes, int reportes) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(publicaciones, "Publicaciones activas"));
        entries.add(new PieEntry(colabAceptadas, "Colab. aceptadas"));
        entries.add(new PieEntry(colabPendientes, "Colab. pendientes"));
        entries.add(new PieEntry(reportes, "Reportes pendientes"));


        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(6f);

        chartPie.setData(new PieData(dataSet));
        Description desc = new Description();
        desc.setText("Distribución de elementos activos");
        chartPie.setDescription(desc);
        chartPie.animateY(1200);
    }
}
