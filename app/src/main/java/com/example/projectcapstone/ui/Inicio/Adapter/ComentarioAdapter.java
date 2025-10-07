package com.example.projectcapstone.ui.Inicio.Adapter;

import android.content.Context;
import android.graphics.Color;
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
    private OnCommentLikeClickListener likeClickListener;
    private OnReportCommentListener reportListener;

    public interface OnCommentLikeClickListener {
        void onCommentLikeClicked(Comentario comentario, ImageView imgLike, TextView textLikeCount);
    }

    public interface OnReportCommentListener {
        void onReportCommentClick(Comentario comentario);
    }

    public void setOnCommentLikeClickListener(OnCommentLikeClickListener listener) {
        this.likeClickListener = listener;
    }

    public void setOnReportCommentListener(OnReportCommentListener listener) {
        this.reportListener = listener;
    }

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
        holder.textTime.setText(getTiempoRelativo(comentario.getFchComentario()));

        // Mostrar cantidad de likes si es mayor a 0
        if (comentario.getTotalLikes() > 0) {
            holder.textLikeCount.setVisibility(View.VISIBLE);
            holder.textLikeCount.setText(String.valueOf(comentario.getTotalLikes()));
        } else {
            holder.textLikeCount.setVisibility(View.GONE);
        }

        // Estado del like
        if (comentario.isLiked()) {
            holder.imgLike.setImageResource(R.drawable.ic_corazon_lleno);
            holder.imgLike.setColorFilter(Color.parseColor("#FBAE3C")); // naranja
        } else {
            holder.imgLike.setImageResource(R.drawable.ic_corazon);
            holder.imgLike.setColorFilter(Color.parseColor("#BDBDBD")); // gris
        }

        // -------------------------------
        // EVENTOS
        // -------------------------------
        // Click en Like
        holder.layoutLike.setOnClickListener(v -> {
            if (likeClickListener != null) {
                likeClickListener.onCommentLikeClicked(comentario, holder.imgLike, holder.textLikeCount);
            }
        });

        // Click en Reportar comentario
        holder.layoutReport.setOnClickListener(v -> {
            if (reportListener != null) {
                reportListener.onReportCommentClick(comentario);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }

    private String getTiempoRelativo(String fechaComentario) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date fecha = sdf.parse(fechaComentario);
            if (fecha != null) {
                long tiempoComentario = fecha.getTime();
                long ahora = System.currentTimeMillis();
                long diferencia = ahora - tiempoComentario;

                if (diferencia < DateUtils.DAY_IN_MILLIS) {
                    long horas = diferencia / DateUtils.HOUR_IN_MILLIS;
                    if (horas <= 0) return "Hace un momento";
                    else if (horas == 1) return "Hace 1 hora";
                    else return "Hace " + horas + " horas";
                } else {
                    long dias = diferencia / DateUtils.DAY_IN_MILLIS;
                    return dias == 1 ? "Hace 1 día" : "Hace " + dias + " días";
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    // ============================
    // VIEW HOLDER
    // ============================
    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        TextView textUserName, textComment, textTime, textLikeCount;
        ImageView imgAvatar, imgLike, imgReport;
        View layoutLike, layoutReport;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.text_comment_username);
            textComment = itemView.findViewById(R.id.text_comment_content);
            textTime = itemView.findViewById(R.id.text_comment_time);
            imgAvatar = itemView.findViewById(R.id.img_comment_avatar);
            imgLike = itemView.findViewById(R.id.img_like);
            textLikeCount = itemView.findViewById(R.id.text_like_count);
            layoutLike = itemView.findViewById(R.id.layout_like);

            // NUEVOS elementos de reporte
            layoutReport = itemView.findViewById(R.id.layout_report);
            imgReport = itemView.findViewById(R.id.img_report);
        }
    }
}
