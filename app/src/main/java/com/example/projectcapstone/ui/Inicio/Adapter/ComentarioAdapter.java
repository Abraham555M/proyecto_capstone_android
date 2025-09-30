package com.example.projectcapstone.ui.Inicio.Adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.Comentario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder> {

    private List<Comentario> listaComentarios;
    private Context context;

    public ComentarioAdapter(Context context, List<Comentario> listaComentarios) {
        this.context = context;
        this.listaComentarios = listaComentarios;
    }


    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comentario, parent, false);
        return new ComentarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        Comentario comentario = listaComentarios.get(position);

        holder.textUserName.setText(comentario.getNomEstudiante());
        holder.textComment.setText(comentario.getConComentario());

        // Convertir fecha a tiempo relativo
        String fechaComentario = comentario.getFchComentario(); // Ej: "2025-09-28 16:20:00"
        holder.textTime.setText(getTiempoRelativo(fechaComentario));    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }

    private String getTiempoRelativo(String fechaComentario) {
        // Ajusta al formato real que devuelves desde BD
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date fecha = sdf.parse(fechaComentario);
            if (fecha != null) {
                long tiempoComentario = fecha.getTime();
                long ahora = System.currentTimeMillis();

                long diferencia = ahora - tiempoComentario;

                // Si es menos de 24h => horas
                if (diferencia < DateUtils.DAY_IN_MILLIS) {
                    long horas = diferencia / DateUtils.HOUR_IN_MILLIS;
                    if (horas <= 0) {
                        return "Hace un momento";
                    } else if (horas == 1) {
                        return "Hace 1 hora";
                    } else {
                        return "Hace " + horas + " horas";
                    }
                } else {
                    // Más de 24h => días
                    long dias = diferencia / DateUtils.DAY_IN_MILLIS;
                    if (dias == 1) {
                        return "Hace 1 día";
                    } else {
                        return "Hace " + dias + " días";
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        TextView textUserName, textComment, textTime;
        ImageView imgAvatar;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.text_comment_username);
            textComment = itemView.findViewById(R.id.text_comment_content);
            textTime = itemView.findViewById(R.id.text_comment_time);
            imgAvatar = itemView.findViewById(R.id.img_comment_avatar);
        }
    }
}