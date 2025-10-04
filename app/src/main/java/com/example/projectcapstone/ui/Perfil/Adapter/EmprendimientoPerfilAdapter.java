package com.example.projectcapstone.ui.Perfil.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.R;
import com.example.projectcapstone.databinding.ItemEmprendimientoPerfilBinding;
import com.example.projectcapstone.ui.Clases.EmprendimientoPerfil;

import java.util.List;

public class EmprendimientoPerfilAdapter extends RecyclerView.Adapter<EmprendimientoPerfilAdapter.EmprendimientoViewHolder> {

    private List<EmprendimientoPerfil> emprendimientos;
    private Context context;

    public EmprendimientoPerfilAdapter(List<EmprendimientoPerfil> emprendimientos) {
        this.emprendimientos = emprendimientos;
    }

    @NonNull
    @Override
    public EmprendimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemEmprendimientoPerfilBinding binding = ItemEmprendimientoPerfilBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new EmprendimientoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EmprendimientoViewHolder holder, int position) {
        holder.bind(emprendimientos.get(position));
    }

    @Override
    public int getItemCount() {
        return emprendimientos != null ? emprendimientos.size() : 0;
    }

    class EmprendimientoViewHolder extends RecyclerView.ViewHolder {
        private ItemEmprendimientoPerfilBinding binding;

        public EmprendimientoViewHolder(ItemEmprendimientoPerfilBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(EmprendimientoPerfil emprendimiento) {
            // Título de la categoría
            binding.tvEmprendimientos.setText(emprendimiento.getNombreCategoria());

            // Configurar RecyclerView hijo (horizontal)
            LinearLayoutManager layoutManager = new LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
            );

            binding.recyclerPosts.setLayoutManager(layoutManager);

            // Crear y asignar adapter hijo
            PublicacionesPerfilAdapter publicacionesAdapter =
                    new PublicacionesPerfilAdapter(emprendimiento.getPublicaciones());
            binding.recyclerPosts.setAdapter(publicacionesAdapter);

            // Opcional: mejorar performance
            binding.recyclerPosts.setHasFixedSize(true);
            binding.recyclerPosts.setNestedScrollingEnabled(false);
        }
    }
}