package com.example.projectcapstone.ui.Publicaciones.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectcapstone.R;

import java.util.List;

public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.ViewHolder>{
    private List<Publicacion> lista;
    private Context context;

    public PublicacionAdapter(List<Publicacion> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_publicacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Publicacion pub = lista.get(position);
        holder.txtTitulo.setText(pub.getTitulo());
        holder.txtDescripcion.setText(pub.getDescripcion());

        // Cargar imagen con Glide (si hay URL)
        Glide.with(context).load(pub.getImagenUrl()).into(holder.imgPublicacion);

        holder.btnEditar.setOnClickListener(v -> {
            Toast.makeText(context, "Editar: " + pub.getTitulo(), Toast.LENGTH_SHORT).show();
        });

        holder.btnEliminar.setOnClickListener(v -> {
            Toast.makeText(context, "Eliminar: " + pub.getTitulo(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPublicacion;
        TextView txtTitulo, txtDescripcion;
        ImageButton btnEditar, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPublicacion = itemView.findViewById(R.id.imgPublicacion);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
