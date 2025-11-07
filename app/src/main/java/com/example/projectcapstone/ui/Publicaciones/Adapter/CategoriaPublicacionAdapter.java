package com.example.projectcapstone.ui.Publicaciones.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;

import java.util.List;

public class CategoriaPublicacionAdapter extends RecyclerView.Adapter<CategoriaPublicacionAdapter.ViewHolder> {
    private final List<CategoriaPublicacion> categorias;
    private final Context context;
    private final OnCategoriaClickListener listener;

    // 👉 Guarda qué posición está seleccionada
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnCategoriaClickListener {
        void onCategoriaClick(CategoriaPublicacion categoria);
    }

    public CategoriaPublicacionAdapter(List<CategoriaPublicacion> categorias, Context context, OnCategoriaClickListener listener) {
        this.categorias = categorias;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoriaPublicacionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categoria_publicacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaPublicacionAdapter.ViewHolder holder, int position) {
        CategoriaPublicacion categoria = categorias.get(position);
        holder.txtNombre.setText(categoria.getNombre());

        // Cargar imagen
        String imgUrl = categoria.getImagen();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            if (!imgUrl.startsWith("http")) {
                imgUrl = ServidorConfig.URL_FOTOS_SERVIDOR + imgUrl;
            }
            Glide.with(context)
                    .load(imgUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(holder.imgCategoria);
        }

        // 🎨 Efecto de selección
        if (selectedPosition == position) {
            holder.txtNombre.setTextColor(Color.parseColor("#FBAE3C"));
            holder.cardImage.setCardBackgroundColor(Color.parseColor("#FFF3E0")); // fondo leve
            holder.cardImage.setCardElevation(8f);
        } else {
            holder.txtNombre.setTextColor(Color.parseColor("#212121"));
            holder.cardImage.setCardBackgroundColor(Color.WHITE);
            holder.cardImage.setCardElevation(4f);
        }

        // 🎬 Click listener
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onCategoriaClick(categoria);
            }

            // Pequeña animación de toque
            v.animate()
                    .scaleX(0.94f)
                    .scaleY(0.94f)
                    .setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                    .start();
        });
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategoria;
        TextView txtNombre;
        CardView cardImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategoria = itemView.findViewById(R.id.imgEmprendimiento);
            txtNombre = itemView.findViewById(R.id.tvNombreEmprendimiento);
            cardImage = itemView.findViewById(R.id.cardImage); // lo agregaremos abajo
        }
    }
}
