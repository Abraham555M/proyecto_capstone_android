package com.example.projectcapstone.ui.Colaboraciones;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ColaboracionAdapter extends RecyclerView.Adapter<ColaboracionAdapter.ViewHolder> {
    private Context context;
    private List<Colaboracion> listaColaboraciones;

    public ColaboracionAdapter(Context context, List<Colaboracion> listaColaboraciones) {
        this.context = context;
        this.listaColaboraciones = listaColaboraciones;
    }

    @NonNull
    @Override
    public ColaboracionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_colaboracion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColaboracionAdapter.ViewHolder holder, int position) {
        Colaboracion colaboracion = listaColaboraciones.get(position);

        holder.tvMensajeColaboracion.setText(colaboracion.getMen_colaboracion());

        // Imagen de publicación
        if (colaboracion.getPublicacion() != null && colaboracion.getPublicacion().getImagenUrl() != null) {
            Glide.with(context)
                    .load(colaboracion.getPublicacion().getImagenUrl())
                    .placeholder(R.drawable.ic_inicio)
                    .error(R.drawable.ic_placeholder)
                    .into(holder.imgUsuario);
        } else {
            holder.imgUsuario.setImageResource(R.drawable.ic_inicio);
        }

        // Mostrar vista según el estado
        switch (colaboracion.getEst_colaboracion()) {
            case 0: // Pendiente
                holder.layoutBotones.setVisibility(View.VISIBLE);
                holder.tvConfirmacion.setVisibility(View.GONE);
                holder.btnOpciones.setVisibility(View.GONE);
                break;

            case 1: // Aceptada
                holder.layoutBotones.setVisibility(View.GONE);
                holder.tvConfirmacion.setVisibility(View.VISIBLE);
                holder.tvConfirmacion.setText("¡Ahora son colaboradores, felicitaciones!");
                holder.btnOpciones.setVisibility(View.VISIBLE);
                break;

            case 2: // Rechazada
            case 4: // Eliminada
                listaColaboraciones.remove(position);
                notifyItemRemoved(position);
                return;
        }

        // Botón ACEPTAR
        holder.btnAceptar.setOnClickListener(v -> {
            colaboracion.setEst_colaboracion(1);
            notifyItemChanged(position);
            actualizarEstadoEnServidor(colaboracion.getId_colaboracion(), 1);
            Toast.makeText(context, "¡Ahora son colaboradores!", Toast.LENGTH_SHORT).show();
        });

        // Botón RECHAZAR
        holder.btnRechazar.setOnClickListener(v -> {
            colaboracion.setEst_colaboracion(2);
            listaColaboraciones.remove(position);
            notifyItemRemoved(position);
            actualizarEstadoEnServidor(colaboracion.getId_colaboracion(), 2);
            Toast.makeText(context, "Solicitud rechazada", Toast.LENGTH_SHORT).show();
        });

        // Tres puntos (Opciones)
        holder.btnOpciones.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.btnOpciones);
            MenuInflater inflater = popup.getMenuInflater();

            if (colaboracion.getEst_colaboracion() == 1) {
                popup.getMenu().add("Eliminar");
            } else {
                popup.getMenu().add("Opciones no disponibles");
            }

            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Eliminar")) {
                    colaboracion.setEst_colaboracion(4);
                    listaColaboraciones.remove(position);
                    notifyItemRemoved(position);
                    actualizarEstadoEnServidor(colaboracion.getId_colaboracion(), 4);
                    Toast.makeText(context, "Colaboración eliminada", Toast.LENGTH_SHORT).show();
                }
                return true;
            });

            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return listaColaboraciones.size();
    }

    /**
     * Envía la actualización del estado al servidor (PHP/MySQL)
     */
    private void actualizarEstadoEnServidor(int idColaboracion, int nuevoEstado) {
        String url = ServidorConfig.URL_SERVIDOR +
                "colaboracion/actualizar_estado.php?id_colaboracion=" + idColaboracion +
                "&estado=" + nuevoEstado;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Puedes validar la respuesta del servidor si devuelve JSON
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, "Error al actualizar en servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUsuario, btnOpciones;
        TextView tvMensajeColaboracion, tvConfirmacion;
        Button btnAceptar, btnRechazar;
        LinearLayout layoutBotones;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUsuario = itemView.findViewById(R.id.imgUsuario);
            tvMensajeColaboracion = itemView.findViewById(R.id.tvMensajeColaboracion);
            layoutBotones = itemView.findViewById(R.id.layoutBotones);
            btnAceptar = itemView.findViewById(R.id.btnAceptar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
            tvConfirmacion = itemView.findViewById(R.id.tvConfirmacion);
            btnOpciones = itemView.findViewById(R.id.btnOpciones);
        }
    }
}
