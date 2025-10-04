package com.example.projectcapstone.ui.Perfil.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectcapstone.R;
import com.example.projectcapstone.databinding.ItemPublicacionesPerfilBinding;
import com.example.projectcapstone.ui.Clases.Publicacion;
import com.example.projectcapstone.ui.Clases.PublicacionPerfil;

import java.util.List;

public class PublicacionesPerfilAdapter extends RecyclerView.Adapter<PublicacionesPerfilAdapter.PublicacionViewHolder> {

    private List<PublicacionPerfil> publicaciones;
    private Context context;

    public PublicacionesPerfilAdapter(List<PublicacionPerfil> publicaciones) {
        this.publicaciones = publicaciones;
    }

    @NonNull
    @Override
    public PublicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemPublicacionesPerfilBinding binding = ItemPublicacionesPerfilBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new PublicacionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionViewHolder holder, int position) {
        holder.bind(publicaciones.get(position));
    }

    @Override
    public int getItemCount() {
        return publicaciones != null ? publicaciones.size() : 0;
    }

    class PublicacionViewHolder extends RecyclerView.ViewHolder {
        private ItemPublicacionesPerfilBinding binding;

        public PublicacionViewHolder(ItemPublicacionesPerfilBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PublicacionPerfil publicacion) {
            // Cargar imagen con Glide
            Glide.with(binding.imgPost.getContext())
                    .load(publicacion.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_error)
                    .into(binding.imgPost);

            // Click listener si necesitas
            binding.getRoot().setOnClickListener(v -> {
                // Manejar click en publicación
                // Toast.makeText(context, "Click en: " + publicacion.getTitulo(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}
