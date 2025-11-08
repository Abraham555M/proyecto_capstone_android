package com.example.projectcapstone.ui.Perfil.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcapstone.databinding.ItemEmprendimientoPerfilBinding;
import com.example.projectcapstone.ui.Clases.EmprendimientoPerfil;
import com.example.projectcapstone.R;
import com.example.projectcapstone.ui.Clases.PublicacionPerfil;

import java.util.ArrayList;
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
        private final ItemEmprendimientoPerfilBinding binding;

        public EmprendimientoViewHolder(ItemEmprendimientoPerfilBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(EmprendimientoPerfil emprendimiento) {
            binding.tvEmprendimientos.setText(emprendimiento.getNombreCategoria());

            List<PublicacionPerfil> publicaciones = emprendimiento.getPublicaciones();

            if (publicaciones == null) publicaciones = new ArrayList<>();

            binding.recyclerPosts.setVisibility(View.VISIBLE);
            binding.layoutEmptyPosts.setVisibility(View.GONE);


            // Ocultar placeholders y mostrar publicaciones reales
            binding.recyclerPosts.setVisibility(View.VISIBLE);
            binding.layoutEmptyPosts.setVisibility(View.GONE);

            // ✅ Mostrar solo las primeras 3 publicaciones
            List<PublicacionPerfil> primerasTres = publicaciones.size() > 3
                    ? publicaciones.subList(0, 3)
                    : publicaciones;

            // Configurar el RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
            );
            binding.recyclerPosts.setLayoutManager(layoutManager);
            binding.recyclerPosts.setAdapter(new PublicacionesPerfilAdapter(primerasTres));
            binding.recyclerPosts.setHasFixedSize(true);
            binding.recyclerPosts.setNestedScrollingEnabled(false);

            // Mostrar etiqueta +N si hay más publicaciones
            int restantes = publicaciones.size() - 3;
            if (restantes > 0) {
                binding.tvMasPublicaciones.setText("+" + restantes);
                binding.tvMasPublicaciones.setVisibility(View.VISIBLE);
            } else {
                binding.tvMasPublicaciones.setVisibility(View.GONE);
            }
        }
    }
}
