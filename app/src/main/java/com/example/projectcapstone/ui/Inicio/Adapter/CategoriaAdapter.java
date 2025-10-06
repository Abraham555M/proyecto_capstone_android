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
    private Context context;
    private List<Categoria> listaCategorias;
    private OnItemClickListener listener;
    private int selectedPosition = -1;

    // Listener opcional para click en categoría
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

        // Cargar imagen desde URL con Glide
        Glide.with(context)
                .load(categoria.getImgCategoria())
                .placeholder(R.drawable.ic_error)
                .error(R.drawable.ic_error)
                .into(holder.imgCategoria);

        // ← AGREGAR: Resaltar categoría seleccionada
        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.purple_500));
            holder.tvNombre.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.tvNombre.setTextColor(context.getResources().getColor(R.color.gray_dark));
        }

        // ← MODIFICAR: Evento click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition == RecyclerView.NO_POSITION) return;

                int previousPosition = selectedPosition;

                if (selectedPosition == adapterPosition) {
                    selectedPosition = -1;
                    listener.onItemClick(null);
                } else {
                    selectedPosition = adapterPosition;
                    listener.onItemClick(categoria);
                }

                if (previousPosition != -1)
                    notifyItemChanged(previousPosition);
                notifyItemChanged(adapterPosition);
            }
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
