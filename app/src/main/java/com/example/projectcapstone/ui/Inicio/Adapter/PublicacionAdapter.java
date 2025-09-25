package com.example.projectcapstone.ui.Inicio.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.Publicacion;

import java.util.List;

public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.ViewHolder> {
    private Context context;
    private List<Publicacion> listaPublicaciones;
    private OnLikeClickListener likeListener;
    private OnReportClickListener reportListener;
    private OnSolicitudClickListener solicitudListener;

    public interface OnLikeClickListener {
        void onLikeClicked(Publicacion publicacion, ImageView ivLike, TextView tvLikes);
    }

    public interface OnReportClickListener {
        void onReportClicked(Publicacion publicacion);
    }
    public interface OnSolicitudClickListener {
        void onSolicitudClicked(Publicacion publicacion);
    }

    public PublicacionAdapter(Context context, List<Publicacion> listaPublicaciones,
                              OnLikeClickListener likeListener,
                              OnReportClickListener reportListener,
                              OnSolicitudClickListener solicitudListener) {
        this.context = context;
        this.listaPublicaciones = listaPublicaciones;
        this.likeListener = likeListener;
        this.reportListener = reportListener;
        this.solicitudListener = solicitudListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_publicacion_inicio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Publicacion publicacion = listaPublicaciones.get(position);

        holder.tvEntrepreneurName.setText(publicacion.getNomEmprendimiento());

        // Imagen perfil emprendimiento
        Glide.with(context)
                .load(publicacion.getImgEmprendimiento())
                .placeholder(R.color.gray_light)
                .circleCrop()
                .into(holder.ivEntrepreneurAvatar);

        // Imagen principal producto
        Glide.with(context)
                .load(publicacion.getImgPublicacion())
                .placeholder(R.color.gray_light)
                .centerCrop()
                .into(holder.ivProductImage);

        // Total de likes
        holder.tvLikes.setText(publicacion.getTotalInteracciones() + " Me gusta");
        holder.tvProductTitle.setText(publicacion.getTitPublicacion());
        holder.tvPrice.setText("S/ " + "20.00");
        holder.tvProductDescription.setText(publicacion.getConPublicacion());

        // 🚨 Usar dioLike para pintar corazón
        if (publicacion.isLiked()) {
            holder.ivLike.setImageResource(R.drawable.ic_corazon_lleno);
        } else {
            holder.ivLike.setImageResource(R.drawable.ic_corazon);
        }

        // Listener del botón Me gusta
        holder.ivLike.setOnClickListener(v -> {
            if (likeListener != null) {
                likeListener.onLikeClicked(publicacion, holder.ivLike, holder.tvLikes);
            }
        });

        holder.ivComment.setOnClickListener(v -> {
            // Acción para comentar
        });

        holder.ivBookmark.setOnClickListener(v -> {
            // Acción para guardar en favoritos
        });

        // Listener del botón Mas opciones
        holder.ivMoreOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.inflate(R.menu.menu_publicacion);

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_reportar) {
                    if (reportListener != null) {
                        reportListener.onReportClicked(publicacion);
                    }
                    return true;
                } else if (item.getItemId() == R.id.action_solicitud) {
                    if (solicitudListener != null) {
                        solicitudListener.onSolicitudClicked(publicacion);
                    }
                    return true;
                }
                return false;
            });

            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return listaPublicaciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivEntrepreneurAvatar, ivProductImage, ivLike, ivComment, ivBookmark, ivMoreOptions;
        TextView tvEntrepreneurName, tvLikes, tvProductTitle, tvPrice, tvProductDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivEntrepreneurAvatar = itemView.findViewById(R.id.ivEntrepreneurAvatar);
            tvEntrepreneurName = itemView.findViewById(R.id.tvEntrepreneurName);
            ivMoreOptions = itemView.findViewById(R.id.ivMoreOptions);

            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivComment = itemView.findViewById(R.id.ivComment);
            ivBookmark = itemView.findViewById(R.id.ivBookmark);

            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvProductTitle = itemView.findViewById(R.id.tvProductTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescription);


        }
    }
}