package com.example.projectcapstone.ui.Inicio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class InicioFragment extends Fragment implements View.OnClickListener {
    private CategoriaAdapter categoriaAdapter;
    private PublicacionAdapter publicacionAdapter;
    private TipoReporteAdapter tipoReporteAdapter;
    private List<Categoria> listaCategoria = new ArrayList<>();
    private List<Publicacion> listaPublicacion = new ArrayList<>();
    private List<TipoReporte> listaTipoReporte = new ArrayList<>();
    private List<Comentario> listaComentarios = new ArrayList<>();
    private RecyclerView rvCategoria, rvPublicaciones;
    private SessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inicio, container, false);

        session = new SessionManager(requireContext());
        rvCategoria = rootView.findViewById(R.id.rvCategoria);
        rvPublicaciones = rootView.findViewById(R.id.rvPublicaciones);

        // Configuración horizontal
        rvCategoria.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoriaAdapter = new CategoriaAdapter(getContext(), listaCategoria);
        rvCategoria.setAdapter(categoriaAdapter);

        // Configuración vertical de publicaciones
        rvPublicaciones.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        publicacionAdapter = new PublicacionAdapter(
                requireContext(),
                listaPublicacion,
                (publicacion, ivLike, tvLikes) -> registrarLike(
                        publicacion.getIdPublicacion(),
                        session.getIdEstudiante(), // ID del estudiante logueado
                        publicacion,
                        ivLike,
                        tvLikes
                ),
                publicacion -> mostrarDialogoReportar(requireContext(), publicacion.getIdPublicacion()),
                publicacion -> mostrarDialogoSolicitud(requireContext(),
                        session.getIdEstudiante(),  // estudiante logueado
                        publicacion.getIdPublicacion(),
                        publicacion.getIdEmprendimiento()),
                publicacion -> mostrarDialogoComentarios(requireContext(), publicacion.getIdPublicacion()),
                (publicacion, ivBookmark) -> registrarFavorito(
                        publicacion.getIdPublicacion(),
                        session.getIdEstudiante(), // estudiante logueado
                        publicacion,
                        ivBookmark
                )
        );

        // Listener para el botón SEGUIR
        publicacionAdapter.setFollowListener((publicacion, btnFollow) -> {
            registrarSeguimiento(
                    session.getIdEstudiante(), // id estudiante logueado
                    publicacion.getIdEmprendimiento(),
                    btnFollow
            );
        });

        // 🚨 Listener para cuando se presiona el nombre del emprendedor
        publicacionAdapter.setEntrepreneurClickListener(publicacion -> {
            Bundle bundle = new Bundle();
            bundle.putString("idEmprendimiento", publicacion.getImgEmprendimiento());

            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_nav_inicio_to_nav_perfil_emprendedor, bundle);
        });

        rvPublicaciones.setAdapter(publicacionAdapter);

        cargarCategorias();
        cargarPublicaciones(session.getIdEstudiante());

        return rootView;
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

                        listaComentarios.add(new Comentario(idComentario, conComentario, fchComentario, nomEstudiante, dioLike, totalLikes));
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

    private void cargarCategorias() {
        String url = ServidorConfig.URL_SERVIDOR + "categoria/categoria_listar.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody, "UTF-8");
                    JSONArray jsonArray = new JSONArray(respuesta);

                    listaCategoria.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int idCategoria = obj.getInt("id_categoria");
                        String nomCategoria = obj.getString("nom_categoria");
                        String imgCategoria = obj.getString("img_categoria");

                        listaCategoria.add(new Categoria(idCategoria, nomCategoria, imgCategoria));
                    }
                    categoriaAdapter.notifyDataSetChanged();

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

    private void cargarPublicaciones(int idEstudiante) {
        String url = ServidorConfig.URL_SERVIDOR + "publicacion/publicacion_listar_inicio.php?idEstudiante=" + idEstudiante;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody, "UTF-8");
                    JSONArray jsonArray = new JSONArray(respuesta);

                    listaPublicacion.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int idPublicacion = obj.getInt("id_publicacion");
                        int idEmprendimiento = obj.getInt("id_emprendimiento");
                        String nomEmprendimiento = obj.getString("nom_emprendimiento");
                        String imgEmprendimiento = obj.getString("img_per_emprendimiento");
                        String titPublicacion = obj.getString("tit_publicacion");
                        String conPublicacion = obj.getString("con_publicacion");
                        String imgPublicacion = obj.getString("img_publicacion");
                        Integer totalInteracciones = obj.getInt("total_me_gusta");
                        int dioLike = obj.getInt("dio_like");
                        int siguiendo = obj.getInt("siguiendo");
                        int esFavorito = obj.getInt("es_favorito");

                        listaPublicacion.add(new Publicacion(idPublicacion, idEmprendimiento, nomEmprendimiento, imgEmprendimiento, titPublicacion, conPublicacion, imgPublicacion, totalInteracciones, dioLike, siguiendo, esFavorito));
                    }
                    publicacionAdapter.notifyDataSetChanged();

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

    public void registrarFavorito(Integer idPublicacion, int idEstudiante, Publicacion publicacion, ImageView ivFavorito) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("idEstudiante", idEstudiante);
        params.put("idPublicacion", idPublicacion);

        String url = ServidorConfig.URL_SERVIDOR + "favorito/favorito_registrar_publicacion.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody);
                    JSONObject json = new JSONObject(respuesta);

                    String status = json.getString("status");

                    if (status.equals("favorited")) {
                        publicacion.setFavorito(true);
                        ivFavorito.setImageResource(R.drawable.ic_favoritos_lleno);

                        ivFavorito.animate()
                                .scaleX(1.3f).scaleY(1.3f) // aumenta tamaño
                                .setDuration(150)
                                .withEndAction(() -> ivFavorito.animate()
                                        .scaleX(1f).scaleY(1f) // vuelve a su tamaño original
                                        .setDuration(150))
                                .start();

                    } else if (status.equals("unfavorited")) {
                        publicacion.setFavorito(false);
                        ivFavorito.setImageResource(R.drawable.ic_favoritos);
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

    public void registrarLikeComentario(int idComentario, int idEstudiante, Comentario comentario, ImageView imgLike, TextView textLikeCount) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("idEstudiante", idEstudiante);
        params.put("idComentario", idComentario);

        String url = ServidorConfig.URL_SERVIDOR + "comentario/comentario_registrar_like.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody);
                    JSONObject json = new JSONObject(respuesta);
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

                    // Actualizar contador
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

    public void cargarTiposReporte(Context context) {
        String url = ServidorConfig.URL_SERVIDOR + "reporte/reporte_listar_tipos.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody, "UTF-8");
                    JSONArray jsonArray = new JSONArray(respuesta);

                    listaTipoReporte.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int idTipoReporte = obj.getInt("id_tipo_reporte");
                        String nomTipoReporte = obj.getString("nom_tipo_reporte");

                        listaTipoReporte.add(new TipoReporte(idTipoReporte, nomTipoReporte));
                    }

                    tipoReporteAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Toast.makeText(context, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registrarSeguimiento(int idEstudiante, int idEmprendimiento, MaterialButton btnFollow) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("idEstudiante", idEstudiante);
        params.put("idEmprendimiento", idEmprendimiento);

        String url = ServidorConfig.URL_SERVIDOR + "seguimiento/seguimiento_registrar.php";

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody);
                    JSONObject json = new JSONObject(respuesta);

                    String status = json.getString("status");

                    if (status.equals("seguido")) {
                        // Cuando se sigue
                        btnFollow.setText("Siguiendo");
                        btnFollow.setBackgroundColor(getResources().getColor(R.color.teal_700));
                        btnFollow.setStrokeWidth(0); // quitar borde
                        btnFollow.setTextColor(Color.WHITE);

                        Toast.makeText(getContext(), "Ahora sigues este emprendimiento", Toast.LENGTH_SHORT).show();

                    } else if (status.equals("no_seguido")) {
                        // Cuando se deja de seguir → vuelve al estilo XML original
                        btnFollow.setText("Seguir");
                        btnFollow.setBackgroundColor(Color.TRANSPARENT); // fondo transparente
                        btnFollow.setStrokeWidth(1); // vuelve a borde gris
                        btnFollow.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.gray_light)));
                        btnFollow.setTextColor(getResources().getColor(R.color.gray_dark));

                        Toast.makeText(getContext(), "Has dejado de seguir", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getContext(), "Error: " + json.getString("message"), Toast.LENGTH_SHORT).show();
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
                        mostrarDialogoExito("Hemos recibido tu reporte, gracias por reportar esta publicación", "Eliminaremos esta publicación si encontramos que va en contra de nuestras reglas. Gracias por ayudarnos a mantener StartUPN a salvo y apoyar nuestra comunidad");
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
                        // Mensaje personalizado de éxito
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

        // Referencias a las vistas
        TextView tvTituloExito = dialogView.findViewById(R.id.tvTituloExito);
        TextView tvMensajeExito = dialogView.findViewById(R.id.tvMensajeExito);
        MaterialButton btnAceptar = dialogView.findViewById(R.id.btnFuncionalidadExito);

        // Setear dinámicamente
        tvTituloExito.setText(titulo);
        tvMensajeExito.setText(mensaje);

        // Acción del botón
        btnAceptar.setOnClickListener(v -> dialog.dismiss());
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
        ComentarioAdapter comentarioAdapter = new ComentarioAdapter(context, listaComentarios);

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
            // 👉 en vez de registrar directamente, mostramos confirmación
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

    @Override
    public void onClick(View v) {

    }
}