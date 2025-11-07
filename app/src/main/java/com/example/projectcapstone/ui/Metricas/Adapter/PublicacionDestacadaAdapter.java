package com.example.projectcapstone.ui.Metricas.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.Publicacion;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PublicacionDestacadaAdapter extends RecyclerView.Adapter<PublicacionDestacadaAdapter.ViewHolder> {

    private final ArrayList<Publicacion> lista;

    public PublicacionDestacadaAdapter(ArrayList<Publicacion> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_publicacion_destacada, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Publicacion pub = lista.get(position);

        holder.tvTitulo.setText(pub.getTitPublicacion());
        holder.tvEmprendedor.setText(pub.getNomEmprendimiento() != null && !pub.getNomEmprendimiento().isEmpty()
                ? pub.getNomEmprendimiento()
                : "Mi emprendimiento");
        holder.tvInteracciones.setText(String.valueOf(pub.getTotalInteracciones()));

        // Cargar imagen si existe
        if (pub.getImgPublicacion() != null && !pub.getImgPublicacion().isEmpty()) {
            Picasso.get()
                    .load(pub.getImgPublicacion())
                    .placeholder(R.drawable.ic_empty_business)
                    .error(R.drawable.ic_empty_business)
                    .into(holder.ivImagen);
        } else {
            holder.ivImagen.setImageResource(R.drawable.ic_empty_business);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagen;
        TextView tvTitulo, tvEmprendedor, tvInteracciones;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.ivItemImg);
            tvTitulo = itemView.findViewById(R.id.tvItemTitulo);
            tvEmprendedor = itemView.findViewById(R.id.tvItemEmprendedor);
            tvInteracciones = itemView.findViewById(R.id.tvItemInteracciones);
        }
    }
}
