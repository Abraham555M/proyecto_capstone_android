package com.example.projectcapstone.ui.Inicio.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.format.DateUtils;
import android.util.Log;
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
    private int idEstudianteActual;

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

    public interface OnDeleteCommentListener {
        void onDeleteCommentClick(Comentario comentario);
    }

    private OnDeleteCommentListener deleteListener;

    public void setOnDeleteCommentListener(OnDeleteCommentListener listener) {
        this.deleteListener = listener;
    }

    public ComentarioAdapter(Context context, List<Comentario> listaComentarios, int idEstudianteActual) {
        this.context = context;
        this.listaComentarios = listaComentarios;
        this.idEstudianteActual = idEstudianteActual;
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
            holder.imgLike.setColorFilter(Color.parseColor("#FBAE3C"));
        } else {
            holder.imgLike.setImageResource(R.drawable.ic_corazon);
            holder.imgLike.setColorFilter(Color.parseColor("#BDBDBD"));
        }

        // 🔹 Resetear estado inicial
        holder.isExpanded = false;
        holder.textVerMas.setText("Ver más");
        holder.textVerMas.setVisibility(View.GONE);

        // Configurar el TextView
        holder.textComment.setMaxLines(3);
        holder.textComment.setEllipsize(android.text.TextUtils.TruncateAt.END);
        holder.textComment.setText(comentario.getConComentario());

        // 🔹 Verificar si necesita "Ver más" después del layout
        holder.textComment.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Layout layout = holder.textComment.getLayout();
                if (layout != null) {
                    int lines = layout.getLineCount();
                    if (lines > 0) {
                        // Verificar si el texto está truncado (tiene más contenido que lo visible)
                        int ellipsisCount = layout.getEllipsisCount(lines - 1);
                        if (ellipsisCount > 0 || lines > 3) {
                            holder.textVerMas.setVisibility(View.VISIBLE);
                        } else {
                            holder.textVerMas.setVisibility(View.GONE);
                        }
                        // Remover el listener para no ejecutarlo múltiples veces
                        holder.textComment.removeOnLayoutChangeListener(this);
                    }
                }
            }
        });

        // 🔹 Click en "Ver más / Ver menos"
        holder.textVerMas.setOnClickListener(v -> {
            if (holder.isExpanded) {
                // Colapsar
                holder.textComment.setMaxLines(3);
                holder.textComment.setEllipsize(android.text.TextUtils.TruncateAt.END);
                holder.textVerMas.setText("Ver más");
                holder.isExpanded = false;
            } else {
                // Expandir
                holder.textComment.setMaxLines(Integer.MAX_VALUE);
                holder.textComment.setEllipsize(null);
                holder.textVerMas.setText("Ver menos");
                holder.isExpanded = true;
            }
        });

        holder.layoutLike.setOnClickListener(v -> {
            if (likeClickListener != null) {
                likeClickListener.onCommentLikeClicked(comentario, holder.imgLike, holder.textLikeCount);
            }
        });

        // Click en todo el comentario para mostrar opciones
        holder.imgOptions.setOnClickListener(v -> {
            // Crear el menú anclado al ícono de opciones
            androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(context, holder.imgOptions);
            popupMenu.getMenuInflater().inflate(R.menu.menu_comentario, popupMenu.getMenu());

            // Si el comentario es del usuario actual → mostrar "Eliminar", ocultar "Reportar"
            if (comentario.getIdEstudiante() == idEstudianteActual) {
                popupMenu.getMenu().findItem(R.id.action_reportar).setVisible(false);
                popupMenu.getMenu().findItem(R.id.action_solicitud).setVisible(true);
            } else {
                popupMenu.getMenu().findItem(R.id.action_reportar).setVisible(true);
                popupMenu.getMenu().findItem(R.id.action_solicitud).setVisible(false);
            }

            // Acción al hacer clic en una opción
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_reportar && reportListener != null) {
                    reportListener.onReportCommentClick(comentario);
                    return true;
                } else if (id == R.id.action_solicitud && deleteListener != null) {
                    mostrarDialogoConfirmarEliminar(comentario);
                    return true;
                }
                return false;
            });

            // Forzar íconos visibles en el menú (si los agregas luego)
            try {
                java.lang.reflect.Field mFieldPopup = popupMenu.getClass().getDeclaredField("mPopup");
                mFieldPopup.setAccessible(true);
                Object mPopup = mFieldPopup.get(popupMenu);
                mPopup.getClass().getDeclaredMethod("setForceShowIcon", boolean.class).invoke(mPopup, true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            popupMenu.show();
        });

    }

    private void mostrarDialogoConfirmarEliminar(Comentario comentario) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.alert_dialog_opciones, null);

        TextView tvTitulo = view.findViewById(R.id.tvTituloError);
        com.google.android.material.button.MaterialButton btnNo = view.findViewById(R.id.btnNo);
        com.google.android.material.button.MaterialButton btnSi = view.findViewById(R.id.btnSi);

        tvTitulo.setText("¿Estás seguro de eliminar este comentario?");
        btnNo.setText("No");
        btnSi.setText("Sí");

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnNo.setOnClickListener(v -> dialog.dismiss());

        btnSi.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteCommentClick(comentario);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }

    private String getTiempoRelativo(String fechaComentario) {
        try {
            // 1️⃣ Indicar formato original
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            // 2️⃣ Forzar interpretación en zona horaria del servidor (Perú = UTC-5)
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("America/Lima"));

            Date fecha = sdf.parse(fechaComentario);
            if (fecha == null) return "";

            // 3️⃣ Calcular diferencia con hora local del dispositivo
            long diferencia = System.currentTimeMillis() - fecha.getTime();

            if (diferencia < 60_000) {
                return "Hace un momento";
            } else if (diferencia < 3_600_000) {
                long minutos = diferencia / 60_000;
                return "Hace " + minutos + (minutos == 1 ? " minuto" : " minutos");
            } else if (diferencia < 86_400_000) {
                long horas = diferencia / 3_600_000;
                return "Hace " + horas + (horas == 1 ? " hora" : " horas");
            } else {
                long dias = diferencia / 86_400_000;
                return "Hace " + dias + (dias == 1 ? " día" : " días");
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        TextView textUserName, textComment, textTime, textLikeCount, textVerMas;
        ImageView imgAvatar, imgLike;
        View layoutLike;
        boolean isExpanded = false;
        ImageView imgOptions;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.text_comment_username);
            textComment = itemView.findViewById(R.id.text_comment_content);
            textTime = itemView.findViewById(R.id.text_comment_time);
            imgAvatar = itemView.findViewById(R.id.img_comment_avatar);
            imgLike = itemView.findViewById(R.id.img_like);
            textLikeCount = itemView.findViewById(R.id.text_like_count);
            layoutLike = itemView.findViewById(R.id.layout_like);
            textVerMas = itemView.findViewById(R.id.text_ver_mas);
            imgOptions = itemView.findViewById(R.id.img_options);
        }
    }
}