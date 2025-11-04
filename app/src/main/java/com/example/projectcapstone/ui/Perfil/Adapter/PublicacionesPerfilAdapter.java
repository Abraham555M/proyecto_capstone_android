package com.example.projectcapstone.ui.Perfil.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectcapstone.R;
import com.example.projectcapstone.databinding.ItemPublicacionesPerfilBinding;
import com.example.projectcapstone.ui.Clases.PublicacionPerfil;

import java.util.ArrayList;
import java.util.List;

public class PublicacionesPerfilAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_PUBLICACION = 1;
    private static final int TYPE_PLACEHOLDER = 2;

    private List<PublicacionPerfil> publicaciones;
    private Context context;

    public PublicacionesPerfilAdapter(List<PublicacionPerfil> publicaciones) {
        // Evita nulls
        this.publicaciones = (publicaciones != null) ? publicaciones : new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        // Los primeros N elementos son publicaciones reales,
        // el resto (hasta 3) son placeholders
        if (position < publicaciones.size()) {
            return TYPE_PUBLICACION;
        } else {
            return TYPE_PLACEHOLDER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == TYPE_PUBLICACION) {
            ItemPublicacionesPerfilBinding binding = ItemPublicacionesPerfilBinding.inflate(inflater, parent, false);
            return new PublicacionViewHolder(binding);
        } else {
            // Inflate del nuevo XML que creaste: item_placeholder_publicacion.xml
            return new PlaceholderViewHolder(
                    inflater.inflate(R.layout.item_placeholder_publicacion, parent, false)
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PublicacionViewHolder && position < publicaciones.size()) {
            ((PublicacionViewHolder) holder).bind(publicaciones.get(position));
        }
        // Los placeholders no requieren bind
    }

    @Override
    public int getItemCount() {
        // Siempre mostrar 3: publicaciones reales + placeholders faltantes
        return Math.max(3, publicaciones.size());
    }

    // --- ViewHolder para publicaciones reales ---
    class PublicacionViewHolder extends RecyclerView.ViewHolder {
        private final ItemPublicacionesPerfilBinding binding;

        public PublicacionViewHolder(ItemPublicacionesPerfilBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PublicacionPerfil publicacion) {
            Glide.with(binding.imgPost.getContext())
                    .load(publicacion.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_error)
                    .into(binding.imgPost);

            // Si quieres manejar clics en publicaciones:
            // binding.getRoot().setOnClickListener(v -> {
            //     Toast.makeText(context, "Click en: " + publicacion.getTitulo(), Toast.LENGTH_SHORT).show();
            // });
        }
    }

    // --- ViewHolder para placeholders ---
    static class PlaceholderViewHolder extends RecyclerView.ViewHolder {
        public PlaceholderViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
        }
    }
}
