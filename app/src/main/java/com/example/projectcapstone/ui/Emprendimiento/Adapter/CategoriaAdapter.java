package com.example.projectcapstone.ui.Emprendimiento.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.Categoria;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewHolder>{
    private List<Categoria> lista;
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(Categoria categoria);
    }

    public CategoriaAdapter(List<Categoria> listaCategorias, OnItemClickListener listener) {
        this.lista = listaCategorias;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categoria, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Categoria categoria = lista .get(position);
        holder.bind(categoria, listener);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        ImageView imgCategoria;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreCategoria);
            imgCategoria = itemView.findViewById(R.id.imgCategoria);
        }

        public void bind(final Categoria categoria, final OnItemClickListener listener) {
            txtNombre.setText(categoria.getNomCategoria());
            // Cargar la imagen con Glide (si usas URL)
            Glide.with(itemView.getContext())
                    .load(categoria.getImgCategoria())
                    .into(imgCategoria);

            // evento de clic
            itemView.setOnClickListener(v -> listener.onItemClick(categoria));
        }
    }
}
