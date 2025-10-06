package com.example.projectcapstone.ui.Inicio.Adapter;

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
import com.example.projectcapstone.ui.Clases.Categoria;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {
    private final Context context;
    private final List<Categoria> listaCategorias;
    private OnItemClickListener listener;

    // Nueva variable: guarda el ID de la categoría seleccionada (más estable que position)
    private Integer idCategoriaSeleccionada = null;

    public interface OnItemClickListener {
        void onItemClick(Categoria categoria);
    }

    public CategoriaAdapter(Context context, List<Categoria> listaCategorias) {
        this.context = context;
        this.listaCategorias = listaCategorias;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // ✅ Nuevo método público para actualizar visualmente la selección desde el fragmento
    public void setCategoriaSeleccionada(Integer idCategoria) {
        this.idCategoriaSeleccionada = idCategoria;
        notifyDataSetChanged(); // Refresca la vista completa
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categoria_inicio, parent, false);
        return new CategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        Categoria categoria = listaCategorias.get(position);

        holder.tvNombre.setText(categoria.getNomCategoria());

        Glide.with(context)
                .load(categoria.getImgCategoria())
                .placeholder(R.drawable.ic_error)
                .error(R.drawable.ic_error)
                .into(holder.imgCategoria);

        // 💙 Cambiar solo color del texto (sin fondo)
        boolean isSelected = idCategoriaSeleccionada != null &&
                idCategoriaSeleccionada.equals(categoria.getIdCategoria());

        if (isSelected) {
            holder.itemView.setBackgroundResource(0);
            holder.tvNombre.setTextColor(context.getResources().getColor(R.color.blue_primary));
        } else {
            holder.itemView.setBackgroundResource(0);
            holder.tvNombre.setTextColor(context.getResources().getColor(R.color.gray_dark));
        }

        // 🎬 Animación de clic (efecto de presión)
        holder.itemView.setOnClickListener(v -> {
            v.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();

                        // Tu lógica normal de selección
                        if (listener != null) {
                            if (isSelected) {
                                idCategoriaSeleccionada = null; // deselecciona
                                listener.onItemClick(null);
                            } else {
                                idCategoriaSeleccionada = categoria.getIdCategoria(); // selecciona
                                listener.onItemClick(categoria);
                            }
                            notifyDataSetChanged();
                        }
                    })
                    .start();
        });
    }


    @Override
    public int getItemCount() {
        return listaCategorias.size();
    }

    static class CategoriaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategoria;
        TextView tvNombre;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategoria = itemView.findViewById(R.id.ivImagen);
            tvNombre = itemView.findViewById(R.id.tvCategoria);
        }
    }
}
