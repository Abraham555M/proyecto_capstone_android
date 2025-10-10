package com.example.projectcapstone.ui.Publicaciones.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.google.android.material.button.MaterialButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import cz.msebera.android.httpclient.Header;

public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.ViewHolder>{
    private List<Publicacion> lista;
    private Context context;
    private OnPublicacionActualizadaListener listener;

    public PublicacionAdapter(List<Publicacion> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    public PublicacionAdapter(List<Publicacion> lista, Context context, OnPublicacionActualizadaListener listener) {
        this.lista = lista;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_publicacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Publicacion pub = lista.get(position);
        holder.txtTitulo.setText(pub.getTitulo());
        holder.txtDescripcion.setText(pub.getDescripcion());

        // Cargar imagen con Glide (si hay URL)
        Glide.with(context).load(pub.getImagenUrl()).into(holder.imgPublicacion);

        holder.btnEditar.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("id_publicacion", pub.getId());
            Navigation.findNavController(v).navigate(R.id.action_nav_publicaciones_to_editarPublicacionFragment, bundle);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            mostrarDialogoEliminar(v, pub.getId(), pub.getTitulo());
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPublicacion;
        TextView txtTitulo, txtDescripcion;
        ImageButton btnEditar, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPublicacion = itemView.findViewById(R.id.imgPublicacion);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

    private void mostrarDialogoEliminar(View view, int idPublicacion, String titulo) {
        // Inflar el layout personalizado del diálogo de opciones
        View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_opciones, null);

        TextView tvTituloError = dialogView.findViewById(R.id.tvTituloError);
        MaterialButton btnNo = dialogView.findViewById(R.id.btnNo);
        MaterialButton btnSi = dialogView.findViewById(R.id.btnSi);

        // Personalizar texto
        tvTituloError.setText("¿Deseas eliminar la publicación \"" + titulo + "\"?");

        // Crear el diálogo
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Botón No → cierra el diálogo
        btnNo.setOnClickListener(v -> alertDialog.dismiss());

        // Botón Sí → elimina (estado 0)
        btnSi.setOnClickListener(v -> {
            eliminarPublicacion(idPublicacion, alertDialog);
        });

        alertDialog.show();
    }

    private void eliminarPublicacion(int idPublicacion, AlertDialog alertDialogOpciones) {
        String url = ServidorConfig.URL_SERVIDOR + "publicacion/eliminar_publicacion.php";

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id_publicacion", idPublicacion);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                alertDialogOpciones.dismiss(); // cerrar el primer diálogo

                try {
                    String response = new String(responseBody);
                    JSONObject json = new JSONObject(response);

                    if (json.getString("status").equals("success")) {
                        mostrarDialogoExito("¡Publicación eliminada!",
                                "La publicación fue eliminada correctamente.");
                    } else {
                        Toast.makeText(context, "Error al eliminar la publicación.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                alertDialogOpciones.dismiss();
                Toast.makeText(context, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoExito(String titulo, String mensaje) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_res_positiva, null);

        TextView tvTituloExito = dialogView.findViewById(R.id.tvTituloExito);
        TextView tvMensajeExito = dialogView.findViewById(R.id.tvMensajeExito);
        MaterialButton btnFuncionalidadExito = dialogView.findViewById(R.id.btnFuncionalidadExito);

        tvTituloExito.setText(titulo);
        tvMensajeExito.setText(mensaje);

        AlertDialog successDialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnFuncionalidadExito.setOnClickListener(v -> {
            successDialog.dismiss();
            // Podrías actualizar el RecyclerView aquí si quieres quitar la publicación eliminada
            if (listener != null) {
                listener.onPublicacionesActualizadas(); // notifica al fragment
            }
        });

        successDialog.show();
    }

    public interface OnPublicacionActualizadaListener {
        void onPublicacionesActualizadas();
    }
}
