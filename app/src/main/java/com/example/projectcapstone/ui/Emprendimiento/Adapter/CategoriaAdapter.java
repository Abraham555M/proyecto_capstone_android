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

    public CategoriaAdapter(List<Categoria> lista) {
        this.lista = lista;
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
        Categoria cat = lista.get(position);
        holder.txtNombre.setText(cat.getNomCategoria());

        // Cargar imagen desde URL (Glide o Picasso)
        Glide.with(holder.itemView.getContext())
                .load(cat.getImgCategoria())
                .into(holder.imgCategoria);
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
    }
}
