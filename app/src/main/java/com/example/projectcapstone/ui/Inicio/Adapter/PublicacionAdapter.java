package com.example.projectcapstone.ui.Inicio.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.Publicacion;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.ViewHolder> {
    private Context context;
    private List<Publicacion> listaPublicaciones;
    private OnLikeClickListener likeListener;
    private OnReportClickListener reportListener;
    private OnSolicitudClickListener solicitudListener;
    private OnCommentClickListener commentListener;
    private OnFollowClickListener followListener;
    private OnFavoriteClickListener favoriteListener;
    private OnEntrepreneurClickListener entrepreneurClickListener;

    public interface OnLikeClickListener {
        void onLikeClicked(Publicacion publicacion, ImageView ivLike, TextView tvLikes);
    }

    public interface OnReportClickListener {
        void onReportClicked(Publicacion publicacion);
    }

    public interface OnSolicitudClickListener {
        void onSolicitudClicked(Publicacion publicacion);
    }

    public interface OnCommentClickListener {
        void onCommentClicked(Publicacion publicacion);
    }

    public interface OnFollowClickListener {
        void onFollowClicked(Publicacion publicacion, MaterialButton btnFollow);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClicked(Publicacion publicacion, ImageView ivBookmark);
    }

    public interface OnEntrepreneurClickListener {
        void onEntrepreneurClicked(Publicacion publicacion);
    }

    public PublicacionAdapter(Context context, List<Publicacion> listaPublicaciones,
                              OnLikeClickListener likeListener,
                              OnReportClickListener reportListener,
                              OnSolicitudClickListener solicitudListener,
                              OnCommentClickListener commentListener,
                              OnFavoriteClickListener favoriteClickListener) {
        this.context = context;
        this.listaPublicaciones = listaPublicaciones;
        this.likeListener = likeListener;
        this.reportListener = reportListener;
        this.solicitudListener = solicitudListener;
        this.commentListener = commentListener;
        this.favoriteListener = favoriteClickListener;
    }

    public void setFollowListener(OnFollowClickListener followListener) {
        this.followListener = followListener;
    }

    public void setFavoriteListener(OnFavoriteClickListener favoriteListener) {
        this.favoriteListener = favoriteListener;
    }

    public void setEntrepreneurClickListener(OnEntrepreneurClickListener listener) {
        this.entrepreneurClickListener = listener;
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

        // 🔹 Datos generales
        holder.tvEntrepreneurName.setText(publicacion.getNomEmprendimiento());
        Glide.with(context)
                .load(publicacion.getImgEmprendimiento())
                .placeholder(R.color.gray_light)
                .circleCrop()
                .into(holder.ivEntrepreneurAvatar);

        Glide.with(context)
                .load(publicacion.getImgPublicacion())
                .placeholder(R.color.gray_light)
                .centerCrop()
                .into(holder.ivProductImage);

        holder.tvLikes.setText(publicacion.getTotalInteracciones() + " Me gusta");
        holder.tvProductTitle.setText(publicacion.getTitPublicacion());
        holder.tvProductDescription.setText(publicacion.getConPublicacion());

        // Corazón
        holder.ivLike.setImageResource(publicacion.isLiked() ? R.drawable.ic_corazon_lleno : R.drawable.ic_corazon);
        // Favorito
        holder.ivBookmark.setImageResource(publicacion.isFavorito() ? R.drawable.ic_favoritos_lleno : R.drawable.ic_favoritos);
        // Seguir
        if (publicacion.isSiguiendo()) {
            holder.btnFollow.setText("Siguiendo");
            holder.btnFollow.setBackgroundColor(context.getResources().getColor(R.color.teal_700));
            holder.btnFollow.setStrokeWidth(0);
            holder.btnFollow.setTextColor(Color.WHITE);
        } else {
            holder.btnFollow.setText("Seguir");
            holder.btnFollow.setBackgroundColor(Color.TRANSPARENT);
            holder.btnFollow.setStrokeWidth(1);
            holder.btnFollow.setStrokeColor(ColorStateList.valueOf(context.getResources().getColor(R.color.gray_light)));
            holder.btnFollow.setTextColor(context.getResources().getColor(R.color.gray_dark));
        }

        // 🔹 Mostrar sección según tipoPublicacion
        holder.sectionProducto.setVisibility(View.GONE);
        holder.sectionEvento.setVisibility(View.GONE);
        holder.sectionPromocion.setVisibility(View.GONE);

        switch (publicacion.getTipoPublicacion()) {
            case 1: // Producto
                if (publicacion.getProducto() != null) {
                    holder.sectionProducto.setVisibility(View.VISIBLE);
                    holder.tvPrecioProducto.setText("Precio: S/ " + publicacion.getProducto().getPrecio());
                    holder.tvStockProducto.setText("Stock: " + publicacion.getProducto().getStock());
                }
                break;
            case 2: // Promoción
                if (publicacion.getPromocion() != null) {
                    holder.sectionPromocion.setVisibility(View.VISIBLE);
                    holder.tvDescripcionPromocion.setText(publicacion.getPromocion().getDescripcion());
                    holder.tvFechasPromocion.setText(
                            "Válido del " + publicacion.getPromocion().getFechaInicio() +
                                    " al " + publicacion.getPromocion().getFechaFin()
                    );
                }
                break;
            case 3: // Evento
                if (publicacion.getEvento() != null) {
                    holder.sectionEvento.setVisibility(View.VISIBLE);
                    holder.tvFechaEvento.setText("Fecha: " + publicacion.getEvento().getFecha());
                    holder.tvLugarEvento.setText("Lugar: " + publicacion.getEvento().getLugar());
                }
                break;
        }

        // ===== Listeners =====
        holder.ivLike.setOnClickListener(v -> { if (likeListener != null) likeListener.onLikeClicked(publicacion, holder.ivLike, holder.tvLikes); });
        holder.ivComment.setOnClickListener(v -> { if (commentListener != null) commentListener.onCommentClicked(publicacion); });
        holder.btnFollow.setOnClickListener(v -> {
            if (followListener != null) followListener.onFollowClicked(publicacion, holder.btnFollow);
            int nuevoEstado = publicacion.isSiguiendo() ? 0 : 1;
            for (Publicacion pub : listaPublicaciones) {
                if (pub.getIdEmprendimiento().equals(publicacion.getIdEmprendimiento())) {
                    pub.setDioSeguimiento(nuevoEstado);
                }
            }
            notifyDataSetChanged();
        });
        holder.ivBookmark.setOnClickListener(v -> { if (favoriteListener != null) favoriteListener.onFavoriteClicked(publicacion, holder.ivBookmark); });
        holder.tvEntrepreneurName.setOnClickListener(v -> { if (entrepreneurClickListener != null) entrepreneurClickListener.onEntrepreneurClicked(publicacion); });

        // Ver más / Ver menos
        holder.tvProductDescription.setMaxLines(2);
        holder.tvProductDescription.setEllipsize(TextUtils.TruncateAt.END);
        holder.tvVerMas.setText("Ver más");
        holder.tvVerMas.setVisibility(View.GONE);
        holder.tvProductDescription.post(() -> {
            int lineCount = holder.tvProductDescription.getLineCount();
            android.text.Layout layout = holder.tvProductDescription.getLayout();
            if (layout != null && (lineCount > 2 || layout.getEllipsisCount(lineCount - 1) > 0)) {
                holder.tvVerMas.setVisibility(View.VISIBLE);
            }
        });
        holder.tvVerMas.setOnClickListener(v -> {
            boolean expandido = holder.tvVerMas.getText().toString().equals("Ver menos");
            if (expandido) {
                holder.tvProductDescription.setMaxLines(2);
                holder.tvProductDescription.setEllipsize(TextUtils.TruncateAt.END);
                holder.tvVerMas.setText("Ver más");
            } else {
                holder.tvProductDescription.setMaxLines(Integer.MAX_VALUE);
                holder.tvProductDescription.setEllipsize(null);
                holder.tvVerMas.setText("Ver menos");
            }
        });

        // Más opciones
        holder.ivMoreOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.inflate(R.menu.menu_publicacion);
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_reportar && reportListener != null) {
                    reportListener.onReportClicked(publicacion);
                    return true;
                } else if (item.getItemId() == R.id.action_solicitud && solicitudListener != null) {
                    solicitudListener.onSolicitudClicked(publicacion);
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
        TextView tvEntrepreneurName, tvLikes, tvProductTitle, tvPrice, tvProductDescription, tvVerMas;
        MaterialButton btnFollow;

        LinearLayout sectionProducto, sectionEvento, sectionPromocion;
        TextView tvPrecioProducto, tvStockProducto;
        TextView tvFechaEvento, tvLugarEvento;
        TextView tvDescripcionPromocion, tvFechasPromocion;

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
            tvVerMas = itemView.findViewById(R.id.tvVerMas);
            btnFollow = itemView.findViewById(R.id.btnFollow);

            sectionProducto = itemView.findViewById(R.id.sectionProducto);
            tvPrecioProducto = itemView.findViewById(R.id.tvPrecioProducto);
            tvStockProducto = itemView.findViewById(R.id.tvStockProducto);

            sectionEvento = itemView.findViewById(R.id.sectionEvento);
            tvFechaEvento = itemView.findViewById(R.id.tvFechaEvento);
            tvLugarEvento = itemView.findViewById(R.id.tvLugarEvento);

            sectionPromocion = itemView.findViewById(R.id.sectionPromocion);
            tvDescripcionPromocion = itemView.findViewById(R.id.tvDescripcionPromocion);
            tvFechasPromocion = itemView.findViewById(R.id.tvFechasPromocion);
        }
    }

}