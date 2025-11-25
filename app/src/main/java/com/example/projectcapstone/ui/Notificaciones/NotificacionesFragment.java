package com.example.projectcapstone.ui.Notificaciones;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.Notificacion;
import com.example.projectcapstone.ui.Clases.Soporte;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Configuracion.SessionManager;
import com.example.projectcapstone.ui.Notificaciones.Adapter.ActividadAdapter;
import com.example.projectcapstone.ui.Notificaciones.Adapter.SoporteAdapter;
import com.google.android.material.button.MaterialButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class NotificacionesFragment extends Fragment {
    private SessionManager session;

    // 🔹 SwipeRefresh
    private SwipeRefreshLayout swipeRefreshLayout;
    private ScrollView scrollView; // 🔹 Referencia al ScrollView
    private boolean soporteLoaded = false;
    private boolean actividadLoaded = false;

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

        // 🔹 SwipeRefresh
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::cargarTodo);

        // 🔹 ScrollView
        scrollView = rootView.findViewById(R.id.scrollView);

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

        actividadAdapter.setOnNotificacionClickListener(noti -> {
            Log.e("CLICK", "ID referencia = " + noti.getId_ref_notificacion());

            if (noti.getId_ref_notificacion() <= 0) {
                Toast.makeText(requireContext(), "No hay publicación asociada", Toast.LENGTH_SHORT).show();
                return;
            }

            // 👉 1. Marcar como leída en el servidor
            marcarNotificacionLeida(noti.getId_notificacion());

            // 👉 2. Marcar como leída en memoria
            noti.setLeida(1);

            // 👉 3. Actualizar UI SOLO del item que cambió
            int index = listaNotificaciones.indexOf(noti);
            if (index != -1) actividadAdapter.notifyItemChanged(index);

            // 👉 4. Abrir la publicación
            abrirDialogPublicacion(noti.getId_ref_notificacion());
        });

        // Controlar SwipeRefresh según posición del scroll
        configurarSwipeRefreshConScroll();

        // Cargar datos al inicio
        cargarTodo();

        return rootView;
    }

    // 🔹 MÉTODO NUEVO: Controla cuándo el SwipeRefresh debe activarse
    private void configurarSwipeRefreshConScroll() {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            // Solo permite SwipeRefresh si el ScrollView está en el tope
            int scrollY = scrollView.getScrollY();
            swipeRefreshLayout.setEnabled(scrollY == 0);
        });

        // También controlar cuando el RecyclerView de actividad hace scroll
        recyclerActividad.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Si el RecyclerView puede scrollear hacia arriba, deshabilitar SwipeRefresh
                boolean canScrollUp = recyclerView.canScrollVertically(-1);

                // Solo habilitar SwipeRefresh si:
                // 1. El ScrollView está en el tope (scrollY == 0)
                // 2. El RecyclerView NO puede scrollear más hacia arriba
                int scrollY = scrollView.getScrollY();
                swipeRefreshLayout.setEnabled(scrollY == 0 && !canScrollUp);
            }
        });
    }

    private void cargarTodo() {
        swipeRefreshLayout.setRefreshing(true);
        soporteLoaded = false;
        actividadLoaded = false;

        cargarNotificacionesSoporte();
        cargarNotificacionesActividad();
    }

    private void verificarSiTerminaron() {
        if (soporteLoaded && actividadLoaded) {
            swipeRefreshLayout.setRefreshing(false);
        }
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
                        recyclerSolicitudes.setVisibility(View.GONE);
                        emptyStateSolicitudes.setVisibility(View.VISIBLE);
                    } else {
                        recyclerSolicitudes.setVisibility(View.VISIBLE);
                        emptyStateSolicitudes.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    recyclerSolicitudes.setVisibility(View.GONE);
                    emptyStateSolicitudes.setVisibility(View.VISIBLE);
                } finally {
                    soporteLoaded = true;
                    verificarSiTerminaron();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                recyclerSolicitudes.setVisibility(View.GONE);
                emptyStateSolicitudes.setVisibility(View.VISIBLE);
                soporteLoaded = true;
                verificarSiTerminaron();
            }
        });
    }

    public void abrirDialogPublicacion(int idPublicacion) {

        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_publicacion_inicio);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // ======== Referencias del layout ========
        ImageView ivProductImage = dialog.findViewById(R.id.ivProductImage);
        TextView tvEntrepreneurName = dialog.findViewById(R.id.tvEntrepreneurName);
        TextView tvLikes = dialog.findViewById(R.id.tvLikes);
        TextView tvProductTitle = dialog.findViewById(R.id.tvProductTitle);
        TextView tvProductDescription = dialog.findViewById(R.id.tvProductDescription);
        TextView tvVerMas = dialog.findViewById(R.id.tvVerMas);
        TextView tvActualizado = dialog.findViewById(R.id.tvActualizado);

        ImageView ivLike = dialog.findViewById(R.id.ivLike);
        ImageView ivBookmark = dialog.findViewById(R.id.ivBookmark);
        MaterialButton btnFollow = dialog.findViewById(R.id.btnFollow);

        View sectionProducto = dialog.findViewById(R.id.sectionProducto);
        TextView tvPrecioProducto = dialog.findViewById(R.id.tvPrecioProducto);
        TextView tvStockProducto = dialog.findViewById(R.id.tvStockProducto);

        View sectionEvento = dialog.findViewById(R.id.sectionEvento);
        TextView tvFechaEvento = dialog.findViewById(R.id.tvFechaEvento);
        TextView tvLugarEvento = dialog.findViewById(R.id.tvLugarEvento);

        View sectionPromocion = dialog.findViewById(R.id.sectionPromocion);
        TextView tvDescripcionPromocion = dialog.findViewById(R.id.tvDescripcionPromocion);
        TextView tvFechasPromocion = dialog.findViewById(R.id.tvFechasPromocion);

        // ======== Llamado al backend ========
        int idEstudiante = session.getIdEstudiante();

        String url = ServidorConfig.URL_SERVIDOR +
                "publicacion/publicacion_listar_por_id.php?idPublicacion=" + idPublicacion +
                "&idEstudiante=" + idEstudiante;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String res = new String(responseBody);
                    JSONObject root = new JSONObject(res);

                    // Extraer objetos principales
                    JSONObject pub = root.getJSONObject("publicacion");
                    JSONObject emp = root.getJSONObject("emprendimiento");

                    // ======== Datos Emprendimiento ========
                    tvEntrepreneurName.setText(emp.getString("nombre"));

                    boolean siguiendo = emp.getBoolean("siguiendo");
                    if (siguiendo) {
                        btnFollow.setText("Siguiendo");
                        btnFollow.setTextColor(Color.WHITE);
                        btnFollow.setBackgroundColor(Color.parseColor("#FBAE3C"));
                    } else {
                        btnFollow.setText("Seguir");
                        btnFollow.setTextColor(Color.parseColor("#FBAE3C"));
                        btnFollow.setBackgroundColor(Color.TRANSPARENT);
                    }

                    // ======== Datos Publicación ========
                    tvProductTitle.setText(pub.getString("titulo"));
                    tvProductDescription.setText(pub.getString("contenido"));
                    tvLikes.setText(pub.getInt("likes") + " Me gusta");

                    // Mostrar botón "Ver más"
                    if (pub.getString("contenido").length() > 120) {
                        tvVerMas.setVisibility(View.VISIBLE);
                    }

                    // Imagen
                    Glide.with(requireContext())
                            .load(pub.getString("imagen"))
                            .into(ivProductImage);

                    // Actualizado
                    tvActualizado.setVisibility(pub.getInt("es_actualizado") == 1 ? View.VISIBLE : View.GONE);

                    // Like
                    boolean dioLike = pub.getBoolean("dio_like");
                    ivLike.setImageResource(dioLike ? R.drawable.ic_corazon_lleno : R.drawable.ic_corazon);

                    // Favorito
                    boolean favorito = pub.getBoolean("es_favorito");
                    ivBookmark.setImageResource(favorito ? R.drawable.ic_favoritos_lleno : R.drawable.ic_favoritos);

                    // ======== Mostrar secciones según tipo ========
                    int tipo = pub.getInt("tipo_publicacion");

                    sectionProducto.setVisibility(View.GONE);
                    sectionEvento.setVisibility(View.GONE);
                    sectionPromocion.setVisibility(View.GONE);

                    // PRODUCTO
                    if (tipo == 1 && root.has("producto")) {
                        JSONObject prod = root.getJSONObject("producto");
                        sectionProducto.setVisibility(View.VISIBLE);

                        tvPrecioProducto.setText("Precio: S/ " + prod.getDouble("precio"));
                        tvStockProducto.setText("Stock: " + prod.getInt("stock"));
                    }

                    // PROMOCIÓN
                    if (tipo == 2 && root.has("promocion")) {
                        JSONObject prom = root.getJSONObject("promocion");
                        sectionPromocion.setVisibility(View.VISIBLE);

                        tvDescripcionPromocion.setText(prom.getString("descripcion"));

                        tvFechasPromocion.setText(
                                "Válido del " + prom.getString("fecha_inicio") +
                                        " al " + prom.getString("fecha_fin")
                        );
                    }

                    // EVENTO
                    if (tipo == 3 && root.has("evento")) {
                        JSONObject ev = root.getJSONObject("evento");
                        sectionEvento.setVisibility(View.VISIBLE);

                        tvFechaEvento.setText("Fecha: " + ev.getString("fecha"));
                        tvLugarEvento.setText("Lugar: " + ev.getString("lugar"));
                    }

                } catch (Exception e) {
                    Log.e("PUBLICACION_ERROR", "Error parseando JSON", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("PUBLICACION_FAIL", "Error: " + statusCode + " - " + error.getMessage());
            }
        });

        dialog.show();
    }

    private void marcarNotificacionLeida(int idNotificacion) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("idNotificacion", idNotificacion);

        client.post(ServidorConfig.URL_SERVIDOR + "notificacion/marcar_notificacion_leida.php",
            params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.e("LEIDO", "Marcado como leído: " + idNotificacion);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e("LEIDO_ERROR", "Error al marcar leído: " + error.getMessage());
                }
            });
    }

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
                    Log.e("DEBUG_JSON", response);

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
                                obj.getString("correo_emisor"),
                                obj.getInt("id_ref_notificacion")
                        );
                        listaNotificaciones.add(noti);
                    }

                    actividadAdapter.notifyDataSetChanged();

                    if (listaNotificaciones.isEmpty()) {
                        recyclerActividad.setVisibility(View.GONE);
                        emptyStateActividad.setVisibility(View.VISIBLE);
                    } else {
                        recyclerActividad.setVisibility(View.VISIBLE);
                        emptyStateActividad.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    recyclerActividad.setVisibility(View.GONE);
                    emptyStateActividad.setVisibility(View.VISIBLE);
                } finally {
                    actividadLoaded = true;
                    verificarSiTerminaron();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                recyclerActividad.setVisibility(View.GONE);
                emptyStateActividad.setVisibility(View.VISIBLE);
                actividadLoaded = true;
                verificarSiTerminaron();
            }
        });
    }
}