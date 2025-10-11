package com.example.projectcapstone.ui.Publicaciones.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
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
    private static final int MAX_LINES = 3; // El límite que definiste en el XML

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

        // Cargar imagen con Glide
        Glide.with(context).load(pub.getImagenUrl()).into(holder.imgPublicacion);

        // 1. Configurar el estado inicial (o reciclado) de la descripción
        if (holder.isExpanded) {
            // Estado expandido
            holder.txtDescripcion.setMaxLines(Integer.MAX_VALUE);
            holder.txtDescripcion.setEllipsize(null);
            holder.txtVerMas.setText("Ver menos");
        } else {
            // Estado colapsado (por defecto)
            holder.txtDescripcion.setMaxLines(MAX_LINES);
            holder.txtDescripcion.setEllipsize(android.text.TextUtils.TruncateAt.END);
            holder.txtVerMas.setText("Ver más");
        }

        holder.txtDescripcion.setText(pub.getDescripcion());

        // 2. Verificar visibilidad de "Ver más"
        // Usamos post para asegurar que la vista ha sido medida (layout phase)
        // Esto solo es necesario al dibujar/reciclar la vista.
        holder.txtDescripcion.post(new Runnable() {
            @Override
            public void run() {
                // El botón "Ver más" debe ser visible si:
                // A) Está expandido (para que pueda ver "Ver menos")
                // B) El texto es largo y está truncado (para que pueda expandirlo)
                if (holder.isExpanded || isTextTruncated(holder.txtDescripcion)) {
                    holder.txtVerMas.setVisibility(View.VISIBLE);
                } else {
                    holder.txtVerMas.setVisibility(View.GONE);
                }
            }
        });

        // 3. Click en "Ver más / Ver menos"
        holder.txtVerMas.setOnClickListener(v -> {
            // Invertir el estado
            holder.isExpanded = !holder.isExpanded;

            if (holder.isExpanded) {
                // Expandir inmediatamente
                holder.txtDescripcion.setMaxLines(Integer.MAX_VALUE);
                holder.txtDescripcion.setEllipsize(null);
                holder.txtVerMas.setText("Ver menos");
            } else {
                // Colapsar inmediatamente
                holder.txtDescripcion.setMaxLines(MAX_LINES);
                holder.txtDescripcion.setEllipsize(android.text.TextUtils.TruncateAt.END);
                holder.txtVerMas.setText("Ver más");
            }

            // 💡 IMPORTANTE: Llamar a requestLayout para que el RecyclerView recalcule
            // el espacio de este item y el cambio de tamaño sea visible de inmediato.
            holder.itemView.requestLayout();

            // ❌ NO LLAMAR notifyItemChanged(holder.getLayoutPosition());
            // Llamar a notifyItemChanged fuerza un ciclo de reciclaje, lo que creaba el doble click.
        });

        holder.btnEditar.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("id_publicacion", pub.getId());
            Navigation.findNavController(v).navigate(R.id.action_nav_publicaciones_to_editarPublicacionFragment, bundle);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            mostrarDialogoEliminar(v, pub.getId(), pub.getTitulo());
        });
    }

    // Función auxiliar para determinar si el texto se truncaría
    private boolean isTextTruncated(TextView textView) {
        Layout layout = textView.getLayout();
        if (layout == null) {
            return false;
        }

        // Si el número de líneas es mayor al máximo permitido
        if (layout.getLineCount() > MAX_LINES) {
            return true;
        }

        // Si la última línea visible contiene elipsis, significa que está truncado
        int lastLine = layout.getLineCount() - 1;
        if (lastLine >= 0) {
            return layout.getEllipsisCount(lastLine) > 0;
        }

        return false;
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPublicacion;
        TextView txtTitulo, txtDescripcion, txtVerMas;
        ImageButton btnEditar, btnEliminar;

        // Mantenemos el estado de expansión aquí
        boolean isExpanded = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPublicacion = itemView.findViewById(R.id.imgPublicacion);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            txtVerMas = itemView.findViewById(R.id.txtVerMas);
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
