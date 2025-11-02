package com.example.projectcapstone.ui.Favoritos;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.Categoria;
import com.example.projectcapstone.ui.Clases.Comentario;
import com.example.projectcapstone.ui.Clases.Publicacion;
import com.example.projectcapstone.ui.Clases.TipoReporte;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.example.projectcapstone.ui.Configuracion.SessionManager;
import com.example.projectcapstone.ui.Inicio.Adapter.CategoriaAdapter;
import com.example.projectcapstone.ui.Inicio.Adapter.ComentarioAdapter;
import com.example.projectcapstone.ui.Inicio.Adapter.PublicacionAdapter;
import com.example.projectcapstone.ui.Inicio.Adapter.TipoReporteAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class FavoritosFragment extends Fragment {
    private RecyclerView rvCategoriaFavoritos, rvPublicacionesFavoritos;
    private EditText etSearchFavoritos;
    private LinearLayout layoutEmptyFavoritos;
    private TextView tvEmptyFavoritosTitle;
    private TextView tvEmptyFavoritosSubtitle;    private CategoriaAdapter categoriaAdapter;
    private PublicacionAdapter publicacionAdapter;
    private List<Categoria> listaCategorias = new ArrayList<>();
    private List<Publicacion> listaFavoritos = new ArrayList<>();
    private SessionManager session;
    private boolean tieneFavoritosEnTotal = false;
    private TipoReporteAdapter tipoReporteAdapter;
    private List<TipoReporte> listaTipoReporte = new ArrayList<>();
    private List<Comentario> listaComentarios = new ArrayList<>();
    private boolean isFiltering = false;
    private LinearLayout layoutSearchFavoritos, layoutCategoriasSection, layoutPublicacionesSection;
    private int categoriaSeleccionada = -1;
    private View dividerFavoritos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favoritos, container, false);

        rvCategoriaFavoritos = view.findViewById(R.id.rvCategoriaFavoritos);
        rvPublicacionesFavoritos = view.findViewById(R.id.rvPublicacionesFavoritos);
        etSearchFavoritos = view.findViewById(R.id.etSearchFavoritos);
        layoutEmptyFavoritos = view.findViewById(R.id.layoutEmptyFavoritos);
        layoutSearchFavoritos = view.findViewById(R.id.layoutSearchFavoritos);
        tvEmptyFavoritosTitle = view.findViewById(R.id.tvEmptyFavoritosTitle);
        tvEmptyFavoritosSubtitle = view.findViewById(R.id.tvEmptyFavoritosSubtitle);
        layoutSearchFavoritos = view.findViewById(R.id.layoutSearchFavoritos);
        layoutCategoriasSection = view.findViewById(R.id.layoutCategoriasSection);
        dividerFavoritos = view.findViewById(R.id.dividerFavoritos);
        layoutPublicacionesSection = view.findViewById(R.id.layoutPublicacionesSection);
        tvEmptyFavoritosTitle = view.findViewById(R.id.tvEmptyFavoritosTitle);
        tvEmptyFavoritosSubtitle = view.findViewById(R.id.tvEmptyFavoritosSubtitle);


        session = new SessionManager(requireContext());

        int idEstudiante = session.getIdEstudiante();

        // Configurar categorías (horizontal)
        rvCategoriaFavoritos.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        categoriaAdapter = new CategoriaAdapter(getContext(), listaCategorias);
        rvCategoriaFavoritos.setAdapter(categoriaAdapter);

        categoriaAdapter.setOnItemClickListener(categoria -> {
            if (categoria == null) {
                categoriaSeleccionada = -1;
                isFiltering = false;
                cargarFavoritos(idEstudiante);
                etSearchFavoritos.setText("");
            } else {
                categoriaSeleccionada = categoria.getIdCategoria();
                isFiltering = true;
                filtrarFavoritosPorCategoria(idEstudiante, categoria.getIdCategoria());
                etSearchFavoritos.setText("");
            }
        });

        // Configurar publicaciones (vertical)
        rvPublicacionesFavoritos.setLayoutManager(new LinearLayoutManager(getContext()));
        publicacionAdapter = new PublicacionAdapter(
                requireContext(),
                listaFavoritos,
                (publicacion, ivLike, tvLikes) -> registrarLike(
                        publicacion.getIdPublicacion(),
                        session.getIdEstudiante(),
                        publicacion,
                        ivLike,
                        tvLikes
                ),
                publicacion -> mostrarDialogoReportar(requireContext(), publicacion.getIdPublicacion()),
                publicacion -> mostrarDialogoSolicitud(requireContext(),
                        session.getIdEstudiante(),
                        publicacion.getIdPublicacion(),
                        publicacion.getIdEmprendimiento()),
                publicacion -> mostrarDialogoComentarios(requireContext(), publicacion.getIdPublicacion()),
                (publicacion, ivFavorito) -> toggleFavorito(publicacion, ivFavorito)
        );
        rvPublicacionesFavoritos.setAdapter(publicacionAdapter);

        // Cargar datos iniciales
        cargarCategorias();
        cargarFavoritos(idEstudiante);

        // Buscar entre favoritos
        etSearchFavoritos.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                buscarFavoritos(idEstudiante, s.toString().trim());
            }
        });

        return view;
    }

    private void cargarCategorias() {
        String url = ServidorConfig.URL_SERVIDOR + "categoria/categoria_listar.php";
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    listaCategorias.clear();
                    JSONArray array = new JSONArray(new String(responseBody, "UTF-8"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        listaCategorias.add(new Categoria(
                                obj.getInt("id_categoria"),
                                obj.getString("nom_categoria"),
                                obj.getString("img_categoria")
                        ));
                    }
                    categoriaAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al cargar categorías", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarFavoritos(int idEstudiante) {
        isFiltering = false;
        categoriaSeleccionada = -1;
        String url = ServidorConfig.URL_SERVIDOR + "favorito/favorito_listar_publicacion.php?idEstudiante=" + idEstudiante;
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    listaFavoritos.clear();
                    JSONArray array = new JSONArray(new String(responseBody, "UTF-8"));

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = array.getJSONObject(i);

                        // --- Objetos principales ---
                        JSONObject pubObj = item.getJSONObject("publicacion");
                        JSONObject empObj = item.getJSONObject("emprendimiento");

                        // --- Datos generales ---
                        int idPublicacion = pubObj.getInt("id");
                        int idEmprendimiento = empObj.getInt("id");
                        String nomEmprendimiento = empObj.getString("nombre");
                        String imgEmprendimiento = empObj.optString("imagen_perfil", null);
                        String titPublicacion = pubObj.getString("titulo");
                        String conPublicacion = pubObj.getString("contenido");
                        String imgPublicacion = pubObj.optString("imagen", null);
                        int totalLikes = pubObj.getInt("likes");
                        int dioLike = pubObj.getBoolean("dio_like") ? 1 : 0;
                        int siguiendo = empObj.getBoolean("siguiendo") ? 1 : 0;
                        int esFavorito = pubObj.getBoolean("es_favorito") ? 1 : 0;
                        int tipoPublicacion = pubObj.getInt("tipo_publicacion");
                        int esActualizado = pubObj.optInt("es_actualizado", 0);

                        // --- Variables de tipos específicos ---
                        Publicacion.Producto producto = null;
                        Publicacion.Promocion promocion = null;
                        Publicacion.Evento evento = null;

                        // --- Si existe un objeto según el tipo, lo parseamos ---
                        if (tipoPublicacion == 1 && item.has("producto")) {
                            JSONObject prodObj = item.getJSONObject("producto");
                            producto = new Publicacion.Producto(
                                    prodObj.optDouble("precio", 0.0),
                                    prodObj.optInt("stock", 0)
                            );
                        } else if (tipoPublicacion == 2 && item.has("promocion")) {
                            JSONObject promObj = item.getJSONObject("promocion");
                            promocion = new Publicacion.Promocion(
                                    promObj.optString("descripcion", ""),
                                    promObj.optString("fecha_inicio", ""),
                                    promObj.optString("fecha_fin", "")
                            );
                        } else if (tipoPublicacion == 3 && item.has("evento")) {
                            JSONObject evObj = item.getJSONObject("evento");
                            evento = new Publicacion.Evento(
                                    evObj.optString("fecha", ""),
                                    evObj.optString("lugar", "")
                            );
                        }

                        // --- Crear objeto Publicacion ---
                        Publicacion publicacion = new Publicacion(
                                idPublicacion,
                                idEmprendimiento,
                                nomEmprendimiento,
                                imgEmprendimiento,
                                titPublicacion,
                                conPublicacion,
                                imgPublicacion,
                                totalLikes,
                                dioLike,
                                siguiendo,
                                esFavorito,
                                tipoPublicacion,
                                producto,
                                evento,
                                promocion,
                                esActualizado
                        );

                        listaFavoritos.add(publicacion);
                    }

                    // --- Actualizar vista ---
                    if (publicacionAdapter != null) {
                        publicacionAdapter.notifyDataSetChanged();
                        tieneFavoritosEnTotal = !listaFavoritos.isEmpty();
                        actualizarEstadoFavoritos();
                    }

                    // --- Mostrar/ocultar layout vacío ---
                    layoutEmptyFavoritos.setVisibility(listaFavoritos.isEmpty() ? View.VISIBLE : View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar favoritos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarFavoritos(int idEstudiante, String texto) {
        if (texto.isEmpty()) {
            // Si no hay texto, restaurar estado previo
            if (categoriaSeleccionada != -1) {
                filtrarFavoritosPorCategoria(idEstudiante, categoriaSeleccionada);
            } else {
                isFiltering = false;
                cargarFavoritos(idEstudiante);
            }
            return;
        }

        String url = ServidorConfig.URL_SERVIDOR +
                "favorito/favorito_buscar_publicacion.php?texto=" + texto + "&idEstudiante=" + idEstudiante;

        // Si hay categoría seleccionada, agregarla
        if (categoriaSeleccionada != -1) {
            url += "&idCategoria=" + categoriaSeleccionada;
        }

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    listaFavoritos.clear();
                    JSONArray array = new JSONArray(new String(responseBody, "UTF-8"));

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = array.getJSONObject(i);

                        // --- Objetos principales ---
                        JSONObject pubObj = item.getJSONObject("publicacion");
                        JSONObject empObj = item.getJSONObject("emprendimiento");

                        // --- Datos generales ---
                        int idPublicacion = pubObj.getInt("id");
                        int idEmprendimiento = empObj.getInt("id");
                        String nomEmprendimiento = empObj.getString("nombre");
                        String imgEmprendimiento = empObj.optString("imagen_perfil", null);
                        String titPublicacion = pubObj.getString("titulo");
                        String conPublicacion = pubObj.getString("contenido");
                        String imgPublicacion = pubObj.optString("imagen", null);
                        int totalLikes = pubObj.getInt("likes");
                        int dioLike = pubObj.getBoolean("dio_like") ? 1 : 0;
                        int siguiendo = empObj.getBoolean("siguiendo") ? 1 : 0;
                        int esFavorito = pubObj.getBoolean("es_favorito") ? 1 : 0;
                        int tipoPublicacion = pubObj.getInt("tipo_publicacion");
                        int esActualizado = pubObj.optInt("es_actualizado", 0);

                        // --- Variables de tipos específicos ---
                        Publicacion.Producto producto = null;
                        Publicacion.Promocion promocion = null;
                        Publicacion.Evento evento = null;

                        if (tipoPublicacion == 1 && item.has("producto")) {
                            JSONObject prodObj = item.getJSONObject("producto");
                            producto = new Publicacion.Producto(
                                    prodObj.optDouble("precio", 0.0),
                                    prodObj.optInt("stock", 0)
                            );
                        } else if (tipoPublicacion == 2 && item.has("promocion")) {
                            JSONObject promObj = item.getJSONObject("promocion");
                            promocion = new Publicacion.Promocion(
                                    promObj.optString("descripcion", ""),
                                    promObj.optString("fecha_inicio", ""),
                                    promObj.optString("fecha_fin", "")
                            );
                        } else if (tipoPublicacion == 3 && item.has("evento")) {
                            JSONObject evObj = item.getJSONObject("evento");
                            evento = new Publicacion.Evento(
                                    evObj.optString("fecha", ""),
                                    evObj.optString("lugar", "")
                            );
                        }

                        // --- Crear objeto Publicacion ---
                        Publicacion publicacion = new Publicacion(
                                idPublicacion,
                                idEmprendimiento,
                                nomEmprendimiento,
                                imgEmprendimiento,
                                titPublicacion,
                                conPublicacion,
                                imgPublicacion,
                                totalLikes,
                                dioLike,
                                siguiendo,
                                esFavorito,
                                tipoPublicacion,
                                producto,
                                evento,
                                promocion,
                                esActualizado
                        );

                        listaFavoritos.add(publicacion);
                    }

                    if (publicacionAdapter != null) {
                        publicacionAdapter.notifyDataSetChanged();
                        actualizarEstadoFavoritos();
                    }

                    layoutEmptyFavoritos.setVisibility(listaFavoritos.isEmpty() ? View.VISIBLE : View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar la búsqueda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarFavoritosPorCategoria(int idEstudiante, int idCategoria) {
        isFiltering = true;
        categoriaSeleccionada = idCategoria;

        String url = ServidorConfig.URL_SERVIDOR +
                "favorito/favorito_filtrar_categoria.php?idEstudiante=" + idEstudiante +
                "&idCategoria=" + idCategoria;

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    listaFavoritos.clear();
                    JSONArray array = new JSONArray(new String(responseBody, "UTF-8"));

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = array.getJSONObject(i);

                        JSONObject pubObj = item.getJSONObject("publicacion");
                        JSONObject empObj = item.getJSONObject("emprendimiento");

                        int idPublicacion = pubObj.getInt("id");
                        int idEmprendimiento = empObj.getInt("id");
                        String nomEmprendimiento = empObj.getString("nombre");
                        String imgEmprendimiento = empObj.optString("imagen_perfil", null);
                        String titPublicacion = pubObj.getString("titulo");
                        String conPublicacion = pubObj.getString("contenido");
                        String imgPublicacion = pubObj.optString("imagen", null);
                        int totalLikes = pubObj.getInt("likes");
                        int dioLike = pubObj.getBoolean("dio_like") ? 1 : 0;
                        int siguiendo = empObj.getBoolean("siguiendo") ? 1 : 0;
                        int esFavorito = pubObj.getBoolean("es_favorito") ? 1 : 0;
                        int tipoPublicacion = pubObj.getInt("tipo_publicacion");
                        int esActualizado = pubObj.optInt("es_actualizado", 0);

                        Publicacion.Producto producto = null;
                        Publicacion.Promocion promocion = null;
                        Publicacion.Evento evento = null;

                        if (tipoPublicacion == 1 && item.has("producto")) {
                            JSONObject prodObj = item.getJSONObject("producto");
                            producto = new Publicacion.Producto(
                                    prodObj.optDouble("precio", 0.0),
                                    prodObj.optInt("stock", 0)
                            );
                        } else if (tipoPublicacion == 2 && item.has("promocion")) {
                            JSONObject promObj = item.getJSONObject("promocion");
                            promocion = new Publicacion.Promocion(
                                    promObj.optString("descripcion", ""),
                                    promObj.optString("fecha_inicio", ""),
                                    promObj.optString("fecha_fin", "")
                            );
                        } else if (tipoPublicacion == 3 && item.has("evento")) {
                            JSONObject evObj = item.getJSONObject("evento");
                            evento = new Publicacion.Evento(
                                    evObj.optString("fecha", ""),
                                    evObj.optString("lugar", "")
                            );
                        }

                        Publicacion publicacion = new Publicacion(
                                idPublicacion,
                                idEmprendimiento,
                                nomEmprendimiento,
                                imgEmprendimiento,
                                titPublicacion,
                                conPublicacion,
                                imgPublicacion,
                                totalLikes,
                                dioLike,
                                siguiendo,
                                esFavorito,
                                tipoPublicacion,
                                producto,
                                evento,
                                promocion,
                                esActualizado
                        );

                        listaFavoritos.add(publicacion);
                    }

                    if (publicacionAdapter != null) {
                        publicacionAdapter.notifyDataSetChanged();
                        actualizarEstadoFavoritos();
                    }

                    layoutEmptyFavoritos.setVisibility(listaFavoritos.isEmpty() ? View.VISIBLE : View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al filtrar favoritos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleFavorito(Publicacion publicacion, ImageView ivFavorito) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("idPublicacion", publicacion.getIdPublicacion());
        params.put("idEstudiante", session.getIdEstudiante());

        String url = ServidorConfig.URL_SERVIDOR + "favorito/favorito_registrar_publicacion.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String resp = new String(responseBody, "UTF-8");
                    JSONObject json = new JSONObject(resp);
                    String status = json.optString("status", "");

                    if ("unfavorited".equals(status)) {
                        // animación visual del icono
                        ivFavorito.animate()
                                .scaleX(0.8f).scaleY(0.8f)
                                .setDuration(100)
                                .withEndAction(() -> {
                                    ivFavorito.setImageResource(R.drawable.ic_favoritos); // icono vacío
                                    ivFavorito.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                                }).start();

                        // quitar del listado BUSCANDO POR ID
                        int idx = -1;
                        for (int i = 0; i < listaFavoritos.size(); i++) {
                            if (listaFavoritos.get(i).getIdPublicacion() == publicacion.getIdPublicacion()) {
                                idx = i;
                                break;
                            }
                        }
                        if (idx != -1) {
                            listaFavoritos.remove(idx);
                            publicacionAdapter.notifyItemRemoved(idx);
                            publicacionAdapter.notifyItemRangeChanged(idx, listaFavoritos.size() - idx);
                        } else {
                            // fallback seguro: buscar y eliminar por id con iterator
                            for (int i = 0; i < listaFavoritos.size(); i++) {
                                if (listaFavoritos.get(i).getIdPublicacion() == publicacion.getIdPublicacion()) {
                                    listaFavoritos.remove(i);
                                    publicacionAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }

                        if (listaFavoritos.isEmpty()) layoutEmptyFavoritos.setVisibility(View.VISIBLE);
                        cargarFavoritos(session.getIdEstudiante());
                        Toast.makeText(getContext(), "Eliminado de favoritos", Toast.LENGTH_SHORT).show();

                    } else if ("favorited".equals(status)) {
                        ivFavorito.setImageResource(R.drawable.ic_favoritos_lleno);
                        Toast.makeText(getContext(), "Agregado a favoritos", Toast.LENGTH_SHORT).show();
                    } else {
                        String msg = json.optString("message", "Respuesta inesperada");
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoReportar(Context context, int idPublicacion) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_reporte, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Botón cerrar
        ImageView ivClose = dialogView.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(v -> dialog.dismiss());

        // RecyclerView dentro del diálogo
        RecyclerView rvReportOptions = dialogView.findViewById(R.id.rvReportOptions);
        rvReportOptions.setLayoutManager(new LinearLayoutManager(context));

        // Adapter con callback de selección
        tipoReporteAdapter = new TipoReporteAdapter(listaTipoReporte, tipo -> {
            // En vez de registrar directamente, mostramos confirmación
            mostrarDialogoConfirmar(context, idPublicacion, tipo.getIdTipoReporte());
            dialog.dismiss(); // cerramos el dialogo de opciones
        });

        rvReportOptions.setAdapter(tipoReporteAdapter);

        // Cargar opciones desde el backend
        cargarTiposReporte(context);
    }

    private void mostrarDialogoConfirmar(Context context, int idPublicacion, int idTipoReporte) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_opciones, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Referencias a vistas
        TextView tvTitulo = dialogView.findViewById(R.id.tvTituloError);
        MaterialButton btnNo = dialogView.findViewById(R.id.btnNo);
        MaterialButton btnSi = dialogView.findViewById(R.id.btnSi);

        // Personalizar título
        tvTitulo.setText("¿Seguro que deseas reportar esta publicación?");

        // Botón No → cerrar
        btnNo.setOnClickListener(v -> dialog.dismiss());

        // Botón Sí → confirmar acción
        btnSi.setOnClickListener(v -> {
            registrarReporte(idPublicacion, idTipoReporte);
            dialog.dismiss();
        });
    }

    private void mostrarDialogoSolicitud(Context context, int idEstudiante, int idPublicacion, int idEmprendimiento) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_solicitud_colaboracion, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Referencias a las vistas
        TextInputEditText etMensaje = dialogView.findViewById(R.id.etMensajeSolicitud);
        MaterialButton btnEnviar = dialogView.findViewById(R.id.btnEnviar);
        ImageButton btnCerrar = dialogView.findViewById(R.id.btnCerrar);

        // Acción botón cerrar
        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        // Acción enviar
        btnEnviar.setOnClickListener(v -> {
            String mensaje = etMensaje.getText().toString().trim();
            if (mensaje.isEmpty()) {
                Toast.makeText(context, "Por favor ingresa un mensaje", Toast.LENGTH_SHORT).show();
                return;
            }

            registrarColaboracion(context, idEstudiante, idPublicacion, idEmprendimiento, mensaje, dialog);
        });
    }

    private void mostrarDialogoComentarios(Context context, int idPublicacion) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_comentarios, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Referencias a vistas
        RecyclerView recyclerComments = dialogView.findViewById(R.id.recycler_comments);
        LinearLayout layoutEmpty = dialogView.findViewById(R.id.layout_empty_state);
        ImageView btnClose = dialogView.findViewById(R.id.btn_close);
        ImageView btnEnviarComentario = dialogView.findViewById(R.id.btnEnviarComentario);
        EditText etComentario = dialogView.findViewById(R.id.etComentario);

        // Lista y adapter
        listaComentarios.clear();
        ComentarioAdapter comentarioAdapter = new ComentarioAdapter(context, listaComentarios, session.getIdEstudiante());
        recyclerComments.setAdapter(comentarioAdapter);
        comentarioAdapter.setOnReportCommentListener(comentario -> {
            mostrarDialogoReportarComentario(requireContext(), comentario.getIdComentario());
        });
        // Asignar el listener al adapter
        comentarioAdapter.setOnCommentLikeClickListener((comentario, imgLike, textLikeCount) -> {
            registrarLikeComentario(
                    comentario.getIdComentario(),
                    session.getIdEstudiante(), // estudiante logueado
                    comentario,
                    imgLike,
                    textLikeCount
            );
        });

        comentarioAdapter.setOnDeleteCommentListener(comentario -> {
            eliminarComentario(comentario.getIdComentario(), idPublicacion,
                    comentarioAdapter, listaComentarios, layoutEmpty, recyclerComments);
        });

        // Layout manager y adapter
        recyclerComments.setLayoutManager(new LinearLayoutManager(context));
        recyclerComments.setAdapter(comentarioAdapter);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Cargar comentarios desde backend
        cargarComentariosPublicacion(idPublicacion, comentarioAdapter, listaComentarios, layoutEmpty, recyclerComments, session.getIdEstudiante());

        btnEnviarComentario.setOnClickListener(v -> {
            String textoComentario = etComentario.getText().toString().trim();
            if (!textoComentario.isEmpty()) {
                registrarComentario(idPublicacion, session.getIdEstudiante(), textoComentario);
                etComentario.setText("");
                cargarComentariosPublicacion(idPublicacion, comentarioAdapter, listaComentarios, layoutEmpty, recyclerComments, session.getIdEstudiante());
            } else {
                Toast.makeText(context, "Escribe un comentario primero", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    public void registrarComentario(Integer idPublicacion, int idEstudiante, String conComentario) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("idEstudiante", idEstudiante);
        params.put("idPublicacion", idPublicacion);
        params.put("conComentario", conComentario);

        String url = ServidorConfig.URL_SERVIDOR + "comentario/comentario_registrar.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody);
                    JSONObject json = new JSONObject(respuesta);

                    if (json.getString("status").equals("success")) {
                        Toast.makeText(getContext(), "Comentario agregado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error: " + json.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error de parsing", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarComentariosPublicacion(int idPublicacion, ComentarioAdapter comentarioAdapter, List<Comentario> listaComentarios, LinearLayout layoutEmpty, RecyclerView recyclerComments, int idEstudiante) {
        String url = ServidorConfig.URL_SERVIDOR + "publicacion/publicacion_listar_comentarios.php?idPublicacion=" + idPublicacion + "&idEstudiante=" + idEstudiante;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody, "UTF-8");
                    JSONArray jsonArray = new JSONArray(respuesta);

                    listaComentarios.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int idComentario = obj.getInt("id_comentario");
                        String conComentario = obj.getString("con_comentario");
                        String fchComentario = obj.getString("fch_comentario");
                        String nomEstudiante = obj.getString("estudiante");
                        int totalLikes = obj.getInt("total_likes");
                        boolean dioLike = obj.getInt("dio_like") == 1;
                        int idEstudianteComentario = obj.getInt("id_estudiante");

                        listaComentarios.add(new Comentario(idComentario, conComentario, fchComentario, nomEstudiante,dioLike, totalLikes, idEstudianteComentario));
                    }

                    if (listaComentarios.isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        recyclerComments.setVisibility(View.GONE);
                    } else {
                        layoutEmpty.setVisibility(View.GONE);
                        recyclerComments.setVisibility(View.VISIBLE);
                    }

                    comentarioAdapter.notifyDataSetChanged();

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

    private void registrarReporte(int idPublicacion, int idTipoReporte) {
        int idEstudiante = session.getIdEstudiante();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("idEstudiante", idEstudiante);
        params.put("idPublicacion", idPublicacion);
        params.put("idTipoReporte", idTipoReporte);

        String url = ServidorConfig.URL_SERVIDOR + "reporte/reporte_registrar_reporte.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("status");

                    if ("reported".equals(status)) {
                        mostrarDialogoExito(
                                "Hemos recibido tu reporte, gracias por reportar esta publicación",
                                "Eliminaremos esta publicación si encontramos que va en contra de nuestras reglas. Gracias por ayudarnos a mantener StartUPN a salvo y apoyar nuestra comunidad"
                        );
                    } else {
                        String msg = json.has("message") ? json.getString("message") : "Error desconocido";
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error procesando respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registrarColaboracion(Context context, int idEstudiante, int idPublicacion, int idEmprendimiento, String mensaje, AlertDialog dialog) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("idEstudiante", idEstudiante);
        params.put("idPublicacion", idPublicacion);
        params.put("idEmprendimiento", idEmprendimiento);
        params.put("menColaboracion", mensaje);

        String url = ServidorConfig.URL_SERVIDOR + "colaboracion/colaboracion_registrar.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    String status = json.optString("status");

                    if ("success".equals(status)) {
                        mostrarDialogoExito(
                                "Solicitud enviada",
                                "Tu solicitud ha sido enviada correctamente. El emprendedor recibirá tu mensaje y podrá contactarse contigo."
                        );
                    } else {
                        String msg = json.has("message") ? json.getString("message") : "Error desconocido";
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error procesando respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoExito(String titulo, String mensaje) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_res_positiva, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvTituloExito = dialogView.findViewById(R.id.tvTituloExito);
        TextView tvMensajeExito = dialogView.findViewById(R.id.tvMensajeExito);
        MaterialButton btnAceptar = dialogView.findViewById(R.id.btnFuncionalidadExito);

        tvTituloExito.setText(titulo);
        tvMensajeExito.setText(mensaje);

        btnAceptar.setOnClickListener(v -> dialog.dismiss());
    }

    private void registrarLikeComentario(int idComentario, int idEstudiante, Comentario comentario, ImageView imgLike, TextView textLikeCount) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("idEstudiante", idEstudiante);
        params.put("idComentario", idComentario);

        String url = ServidorConfig.URL_SERVIDOR + "comentario/comentario_registrar_like.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    String status = json.getString("status");

                    if (status.equals("liked")) {
                        comentario.setLiked(true);
                        comentario.setTotalLikes(comentario.getTotalLikes() + 1);
                        imgLike.setImageResource(R.drawable.ic_corazon_lleno);
                    } else if (status.equals("unliked")) {
                        comentario.setLiked(false);
                        comentario.setTotalLikes(comentario.getTotalLikes() - 1);
                        imgLike.setImageResource(R.drawable.ic_corazon);
                    }

                    if (comentario.getTotalLikes() > 0) {
                        textLikeCount.setVisibility(View.VISIBLE);
                        textLikeCount.setText(String.valueOf(comentario.getTotalLikes()));
                    } else {
                        textLikeCount.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error de parsing", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarTiposReporte(Context context) {
        String url = ServidorConfig.URL_SERVIDOR + "reporte/reporte_listar_tipos.php";
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    listaTipoReporte.clear();
                    JSONArray array = new JSONArray(new String(responseBody, "UTF-8"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        listaTipoReporte.add(new TipoReporte(
                                obj.getInt("id_tipo_reporte"),
                                obj.getString("nom_tipo_reporte")
                        ));
                    }
                    tipoReporteAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(context, "Error al procesar tipos de reporte", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registrarLike(Integer idPublicacion, int idEstudiante, Publicacion publicacion, ImageView ivLike, TextView tvLikes) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("idEstudiante", idEstudiante);
        params.put("idPublicacion", idPublicacion);

        String url = ServidorConfig.URL_SERVIDOR + "interaccion/interaccion_registrar_like.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody);
                    JSONObject json = new JSONObject(respuesta);

                    String status = json.getString("status");

                    if (status.equals("liked")) {
                        publicacion.setLiked(true);
                        publicacion.setTotalInteracciones(publicacion.getTotalInteracciones() + 1);
                        ivLike.setImageResource(R.drawable.ic_corazon_lleno);

                        ivLike.animate()
                                .scaleX(1.3f).scaleY(1.3f) // aumenta tamaño
                                .setDuration(150)
                                .withEndAction(() -> ivLike.animate()
                                        .scaleX(1f).scaleY(1f) // vuelve a su tamaño original
                                        .setDuration(150))
                                .start();

                    } else if (status.equals("unliked")) {
                        publicacion.setLiked(false);
                        publicacion.setTotalInteracciones(publicacion.getTotalInteracciones() - 1);
                        ivLike.setImageResource(R.drawable.ic_corazon);
                    }

                    // actualizar solo el contador
                    tvLikes.setText(publicacion.getTotalInteracciones() + " Me gusta");


                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error de parsing", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoReportarComentario(Context context, int idComentario) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_reporte_comentario, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView ivClose = dialogView.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(v -> dialog.dismiss());

        RecyclerView rvReportOptions = dialogView.findViewById(R.id.rvReportOptions);
        rvReportOptions.setLayoutManager(new LinearLayoutManager(context));

        tipoReporteAdapter = new TipoReporteAdapter(listaTipoReporte, tipo -> {
            mostrarDialogoConfirmarReporteComentario(context, idComentario, tipo.getIdTipoReporte());
            dialog.dismiss();
        });
        rvReportOptions.setAdapter(tipoReporteAdapter);

        cargarTiposReporte(context);
    }

    private void mostrarDialogoConfirmarReporteComentario(Context context, int idComentario, int idTipoReporte) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_opciones, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvTitulo = dialogView.findViewById(R.id.tvTituloError);
        MaterialButton btnNo = dialogView.findViewById(R.id.btnNo);
        MaterialButton btnSi = dialogView.findViewById(R.id.btnSi);

        tvTitulo.setText("¿Deseas reportar este comentario?");

        btnNo.setOnClickListener(v -> dialog.dismiss());
        btnSi.setOnClickListener(v -> {
            registrarReporteComentario(idComentario, idTipoReporte);
            dialog.dismiss();
        });
    }

    private void registrarReporteComentario(int idComentario, int idTipoReporte) {
        int idEstudiante = session.getIdEstudiante();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("idEstudiante", idEstudiante);
        params.put("idComentario", idComentario);
        params.put("idTipoReporte", idTipoReporte);

        String url = ServidorConfig.URL_SERVIDOR + "reporte/reporte_registrar_comentario.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject json = new JSONObject(new String(responseBody, "UTF-8"));
                    String status = json.getString("status");

                    if ("reported".equals(status)) {
                        mostrarDialogoExito(
                                "Reporte enviado",
                                "Gracias por ayudarnos a mantener la comunidad segura. Revisaremos este comentario."
                        );
                    } else {
                        String msg = json.optString("message", "Error al registrar el reporte");
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarComentario(int idComentario, int idPublicacion, ComentarioAdapter adapter,
                                    List<Comentario> lista, LinearLayout layoutEmpty, RecyclerView recycler) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("idComentario", idComentario);
        params.put("idEstudiante", session.getIdEstudiante()); // ← Enviar ID del usuario actual

        String url = ServidorConfig.URL_SERVIDOR + "comentario/comentario_eliminar.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject json = new JSONObject(new String(responseBody, "UTF-8"));
                    String status = json.getString("status");

                    if ("success".equals(status)) {
                        Toast.makeText(getContext(), "Comentario eliminado", Toast.LENGTH_SHORT).show();
                        // Recargar comentarios
                        cargarComentariosPublicacion(idPublicacion, adapter, lista, layoutEmpty, recycler, session.getIdEstudiante());
                    } else {
                        String msg = json.optString("message", "Error al eliminar");
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarEstadoFavoritos() {
        if (tieneFavoritosEnTotal) {
            // Mostrar controles cuando hay favoritos
            rvCategoriaFavoritos.setVisibility(View.VISIBLE);
            layoutSearchFavoritos.setVisibility(View.VISIBLE);
            layoutCategoriasSection.setVisibility(View.VISIBLE);
            dividerFavoritos.setVisibility(View.VISIBLE);
            layoutPublicacionesSection.setVisibility(View.VISIBLE);

            if (listaFavoritos.isEmpty()) {
                String textoBusqueda = etSearchFavoritos.getText().toString().trim();
                if (!textoBusqueda.isEmpty()) {
                    tvEmptyFavoritosTitle.setText("Sin resultados");
                    tvEmptyFavoritosSubtitle.setText("No se encontraron favoritos con '" + textoBusqueda + "'");
                } else if (isFiltering) {
                    tvEmptyFavoritosTitle.setText("Sin favoritos aquí");
                    tvEmptyFavoritosSubtitle.setText("No hay favoritos en esta categoría");
                }
                layoutEmptyFavoritos.setVisibility(View.VISIBLE);
                rvPublicacionesFavoritos.setVisibility(View.GONE);
            } else {
                layoutEmptyFavoritos.setVisibility(View.GONE);
                rvPublicacionesFavoritos.setVisibility(View.VISIBLE);
            }
        } else {
            // Ocultar TODO y mostrar solo el mensaje vacío inicial
            tvEmptyFavoritosTitle.setText("Aún no tienes favoritos");
            tvEmptyFavoritosSubtitle.setText("Guarda las publicaciones que te gusten para verlas más tarde");
            layoutEmptyFavoritos.setVisibility(View.VISIBLE);
            rvPublicacionesFavoritos.setVisibility(View.GONE);
            rvCategoriaFavoritos.setVisibility(View.GONE);
            layoutSearchFavoritos.setVisibility(View.GONE);
            layoutCategoriasSection.setVisibility(View.GONE);
            dividerFavoritos.setVisibility(View.GONE);
            layoutPublicacionesSection.setVisibility(View.GONE);
        }
    }
}
