package com.example.projectcapstone.ui.Publicaciones.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Configuracion.ServidorConfig;

import java.util.List;

public class CategoriaPublicacionAdapter extends RecyclerView.Adapter<CategoriaPublicacionAdapter.ViewHolder> {
    private List<CategoriaPublicacion> categorias;
    private Context context;
    private OnCategoriaClickListener listener;

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

        // Cargar imagen con Glide
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

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoriaClick(categoria);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategoria;
        TextView txtNombre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategoria = itemView.findViewById(R.id.imgEmprendimiento);
            txtNombre = itemView.findViewById(R.id.tvNombreEmprendimiento);
        }
    }
}
