package com.example.projectcapstone.ui.Notificaciones.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.Notificacion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ViewHolder>  {
    private final Context context;
    private final List<Notificacion> listaNotificaciones;

    public ActividadAdapter(Context context, List<Notificacion> listaNotificaciones) {
        this.context = context;
        this.listaNotificaciones = listaNotificaciones;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_actividad, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notificacion noti = listaNotificaciones.get(position);

        // Mostrar el mensaje principal
        holder.tvMensajeNotificacion.setText(noti.getNombre_emisor() + " " + noti.getMensaje());

        // Mostrar el tiempo formateado (ej: "Hace 5 min")
        holder.tvTiempoNotificacion.setText(calcularTiempoTranscurrido(noti.getFecha()));

        // Mostrar el punto solo si la notificación no está leída
        holder.indicadorNoLeida.setVisibility(noti.getLeida() == 0 ? View.VISIBLE : View.GONE);

        // Cambiar icono e imagen según el tipo de notificación
        switch (noti.getTipo().toLowerCase()) {
            case "like":
                holder.ivIconoTipo.setImageResource(R.drawable.ic_corazon);
                holder.cvIconoTipo.setCardBackgroundColor(context.getColor(R.color.blue_light));
                holder.ivIconoTipo.setColorFilter(context.getColor(R.color.blue_primary));
                break;
            case "comentario":
                holder.ivIconoTipo.setImageResource(R.drawable.ic_comentario);
                holder.cvIconoTipo.setCardBackgroundColor(context.getColor(R.color.pink_light));
                holder.ivIconoTipo.setColorFilter(context.getColor(R.color.purple_200));
                break;
            case "mensaje":
                holder.ivIconoTipo.setImageResource(R.drawable.ic_mensaje);
                holder.cvIconoTipo.setCardBackgroundColor(context.getColor(R.color.mint_green));
                holder.ivIconoTipo.setColorFilter(context.getColor(R.color.circle_green));
                break;
            default:
                holder.ivIconoTipo.setImageResource(R.drawable.ic_notificaciones);
                holder.cvIconoTipo.setCardBackgroundColor(context.getColor(R.color.gray_medium));
                holder.ivIconoTipo.setColorFilter(context.getColor(R.color.gray_dark));
                break;
        }

        // Puedes implementar aquí un onClickListener si quieres abrir una actividad detallada
        holder.itemView.setOnClickListener(v -> {
            // Ejemplo:
            // Intent intent = new Intent(context, DetalleNotificacionActivity.class);
            // intent.putExtra("id_notificacion", noti.getId_notificacion());
            // context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaNotificaciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMensajeNotificacion, tvTiempoNotificacion;
        ImageView ivFotoUsuario, ivIconoTipo;
        View indicadorNoLeida;
        CardView cvIconoTipo;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMensajeNotificacion = itemView.findViewById(R.id.tvMensajeNotificacion);
            tvTiempoNotificacion = itemView.findViewById(R.id.tvTiempoNotificacion);
            ivFotoUsuario = itemView.findViewById(R.id.ivFotoUsuario);
            ivIconoTipo = itemView.findViewById(R.id.ivIconoTipo);
            indicadorNoLeida = itemView.findViewById(R.id.indicadorNoLeida);
            cvIconoTipo = itemView.findViewById(R.id.cvIconoTipo);
        }
    }

    // ---------------------------
    // MÉTODO AUXILIAR: convierte la fecha a formato "Hace X tiempo"
    // ---------------------------
    private String calcularTiempoTranscurrido(String fechaStr) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        formato.setTimeZone(TimeZone.getTimeZone("America/Lima"));
        Log.d("DEBUG_FECHA", "Fecha servidor: " + fechaStr);
        Log.d("DEBUG_HORA_LOCAL", "Hora dispositivo local: " + new Date().toString());

        try {
            Date fecha = formato.parse(fechaStr);
            long diffMillis = new Date().getTime() - fecha.getTime();

            long minutos = TimeUnit.MILLISECONDS.toMinutes(diffMillis);
            long horas = TimeUnit.MILLISECONDS.toHours(diffMillis);
            long dias = TimeUnit.MILLISECONDS.toDays(diffMillis);

            if (minutos < 1) return "Hace un momento";
            else if (minutos < 60) return "Hace " + minutos + " min";
            else if (horas < 24) return "Hace " + horas + " h";
            else if (dias < 7) return "Hace " + dias + " días";
            else {
                // Si tiene más de una semana, mostrar la fecha
                return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
